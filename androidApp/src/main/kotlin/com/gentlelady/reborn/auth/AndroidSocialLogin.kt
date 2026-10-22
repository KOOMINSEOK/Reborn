package com.gentlelady.reborn.auth

import android.app.Activity
import android.content.Intent
import android.net.Uri
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

    override fun openBrowser(url: String) {
        current().startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }

    override suspend fun naverAccessToken(): String? = suspendCancellableCoroutine { cont ->
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
