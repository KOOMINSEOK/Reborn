package com.gentlelady.reborn.auth

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.AtomicFile
import android.util.Base64
import android.util.Log
import com.gentlelady.reborn.core.auth.SessionStorage
import java.io.File
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * 로그인 세션(access/refresh 토큰)을 Android Keystore 의 AES 키로 암호화(AES-GCM)해 파일로 둔다.
 * - 키는 Keystore 밖으로 나오지 않는다 → 파일만 빼가서는 못 읽는다.
 * - noBackupFilesDir 에 두어 기기 백업·다른 기기 복원에 실리지 않는다.
 * - AtomicFile 로 쓰기·삭제 → 쓰다가 앱이 죽어도 반쯤 쓴 파일·이전 계정 토큰이 남지 않는다.
 * - 못 읽거나(재설치·키 무효화·손상) 못 쓰면 파일과 키를 지우고 null → 다시 로그인. 앱을 죽이지 않는다.
 *
 * ponytail: 파일·Keystore 작업은 호출한 스레드(메인일 수 있음)에서 동기로 — 수백 바이트라 수 ms.
 * 첫 키 생성만 조금 걸린다. 느려지면 SessionStorage 를 suspend 로 바꿔 IO 로 옮긴다.
 */
class KeystoreSessionStorage(context: Context) : SessionStorage {

    private val file = AtomicFile(File(context.noBackupFilesDir, FILE_NAME))

    override fun read(): String? {
        if (!file.baseFile.exists()) return null
        return try {
            val bytes = Base64.decode(file.readFully(), Base64.NO_WRAP)
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.DECRYPT_MODE, key(), GCMParameterSpec(TAG_BITS, bytes, 0, IV_BYTES))
            cipher.doFinal(bytes, IV_BYTES, bytes.size - IV_BYTES).decodeToString()
        } catch (e: Exception) {
            // 키가 무효화됐거나(잠금 해제 방식 변경·재설치) 파일이 깨짐 → 둘 다 버리고 새로 시작
            Log.w(TAG, "stored session unreadable, discarding", e)
            clear()
            deleteKey()
            null
        }
    }

    override fun write(value: String) {
        val sealed = runCatching { encrypt(value) }
            .recoverCatching {
                // 키가 망가졌으면 한 번만 새 키로 다시
                Log.w(TAG, "encrypt failed, regenerating key", it)
                deleteKey()
                encrypt(value)
            }
            .getOrElse {
                // 저장 못 하면 이번 실행 동안만 로그인 유지. 예전 계정 토큰이 남지 않게 파일은 지운다.
                Log.e(TAG, "cannot persist session", it)
                clear()
                return
            }
        val out = runCatching { file.startWrite() }.getOrElse {
            Log.e(TAG, "cannot open session file", it)
            clear()
            return
        }
        try {
            out.write(Base64.encode(sealed, Base64.NO_WRAP))
            file.finishWrite(out)
        } catch (e: Exception) {
            file.failWrite(out)
            Log.e(TAG, "cannot write session file", e)
            clear()
        }
    }

    override fun clear() {
        file.delete()
        // 그래도 남았으면(삭제 실패) 내용을 덮어써 다시는 복원되지 않게
        if (file.baseFile.exists()) {
            Log.e(TAG, "session file delete failed, overwriting")
            runCatching { file.baseFile.writeBytes(ByteArray(0)) }
        }
    }

    private fun encrypt(value: String): ByteArray {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, key()) // IV 는 Keystore 가 매번 새로 만든다
        return cipher.iv + cipher.doFinal(value.encodeToByteArray())
    }

    private fun keyStore(): KeyStore = KeyStore.getInstance(KEYSTORE).apply { load(null) }

    private fun deleteKey() {
        runCatching { keyStore().deleteEntry(KEY_ALIAS) }
    }

    private fun key(): SecretKey {
        (keyStore().getKey(KEY_ALIAS, null) as? SecretKey)?.let { return it }
        return KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, KEYSTORE).apply {
            init(
                KeyGenParameterSpec.Builder(KEY_ALIAS, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setKeySize(KEY_BITS)
                    .build()
            )
        }.generateKey()
    }

    private companion object {
        const val TAG = "SessionStorage"
        const val KEYSTORE = "AndroidKeyStore"
        const val KEY_ALIAS = "reborn_session_key"
        const val FILE_NAME = "session.bin"
        const val TRANSFORMATION = "AES/GCM/NoPadding"
        const val IV_BYTES = 12
        const val TAG_BITS = 128
        const val KEY_BITS = 256
    }
}
