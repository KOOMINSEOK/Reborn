package com.gentlelady.reborn.auth

import android.app.Activity
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import com.gentlelady.reborn.core.auth.SocialLoginLauncher
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthErrorCode
import com.navercorp.nid.oauth.OAuthLoginCallback
import java.lang.ref.WeakReference
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine

/** 안드로이드 소셜 로그인 창. 지금 화면(Activity)은 MainActivity 가 [attach] 로 알려준다. */
class AndroidSocialLogin : SocialLoginLauncher {
    private var activity = WeakReference<Activity>(null)

    fun attach(activity: Activity) {
        this.activity = WeakReference(activity)
    }

    private fun current() = activity.get() ?: error("no activity")

    /** 앱 위에 겹쳐 뜨는 로그인 창 (Chrome 앱으로 넘어가지 않게) */
    override fun openBrowser(url: String) {
        CustomTabsIntent.Builder().setShowTitle(true).build().launchUrl(current(), Uri.parse(url))
    }

    override suspend fun naverAccessToken(otherAccount: Boolean): String? = suspendCancellableCoroutine { cont ->
        // SDK 가 기억한 토큰이 있으면 화면 없이 바로 그 계정이 된다 → 지우고 네이버를 거친다
        NaverIdLoginSDK.logout()
        // 다른 계정: 브라우저에 남은 네이버 로그인을 쓰지 않고 아이디·비밀번호부터
        NaverIdLoginSDK.isRequiredCustomTabsReAuth = otherAccount
        NaverIdLoginSDK.authenticate(current(), object : OAuthLoginCallback {
            override fun onSuccess() {
                cont.resume(NaverIdLoginSDK.getAccessToken())
            }

            override fun onFailure(httpStatus: Int, message: String) {
                if (NaverIdLoginSDK.getLastErrorCode() == NidOAuthErrorCode.CLIENT_USER_CANCEL) cont.resume(null)
                else cont.resumeWithException(IllegalStateException("naver login failed: $httpStatus"))
            }

            override fun onError(errorCode: Int, message: String) = onFailure(errorCode, message)
        })
    }
}
