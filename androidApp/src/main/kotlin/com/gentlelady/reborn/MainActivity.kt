package com.gentlelady.reborn

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.gentlelady.reborn.androidApp.BuildConfig
import com.gentlelady.reborn.auth.AndroidSocialLogin
import com.gentlelady.reborn.auth.KeystoreSessionStorage
import com.gentlelady.reborn.core.auth.OAUTH_REDIRECT
import com.gentlelady.reborn.core.auth.OAuthRedirects
import com.gentlelady.reborn.core.auth.SessionStorage
import com.gentlelady.reborn.core.auth.SocialLoginLauncher
import com.gentlelady.reborn.di.initKoin
import com.gentlelady.reborn.home.ui.HomeRoute
import com.navercorp.nid.NaverIdLoginSDK
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/** 소셜 로그인 창은 지금 화면이 필요하다 — 화면이 다시 만들어져도 같은 것을 쓴다 */
private val socialLogin = AndroidSocialLogin()

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        initKoin {
            androidContext(this@MainActivity)
            modules(
                module {
                    // 로그인 세션을 기기에 암호화 저장 → 앱을 다시 켜도 로그인 유지
                    single<SessionStorage> { KeystoreSessionStorage(applicationContext) }
                    single<SocialLoginLauncher> { socialLogin }
                },
            )
        }
        socialLogin.attach(this)
        NaverIdLoginSDK.initialize(applicationContext, BuildConfig.NAVER_CLIENT_ID, BuildConfig.NAVER_CLIENT_SECRET, "Reborn")
        deliverRedirect(intent)

        setContent {
            HomeRoute()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        deliverRedirect(intent)
    }

    /** 브라우저 로그인에서 돌아왔으면 그 주소를 가입 화면에 넘긴다 */
    private fun deliverRedirect(intent: Intent?) {
        val uri = intent?.data?.toString() ?: return
        if (uri.startsWith(OAUTH_REDIRECT)) OAuthRedirects.deliver(uri)
    }
}
