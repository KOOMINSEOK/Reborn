package com.gentlelady.reborn

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.gentlelady.reborn.auth.KeystoreSessionStorage
import com.gentlelady.reborn.core.auth.SessionStorage
import com.gentlelady.reborn.di.initKoin
import com.gentlelady.reborn.home.ui.HomeRoute
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        initKoin {
            androidContext(this@MainActivity)
            // 로그인 세션을 기기에 암호화 저장 → 앱을 다시 켜도 로그인 유지
            modules(module { single<SessionStorage> { KeystoreSessionStorage(applicationContext) } })
        }

        setContent {
            HomeRoute()
        }
    }
}
