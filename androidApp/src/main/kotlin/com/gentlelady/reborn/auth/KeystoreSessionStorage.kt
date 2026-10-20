package com.gentlelady.reborn.auth

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
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
 * - 못 읽으면(재설치로 키가 바뀜, 파일 손상) null → 다시 로그인.
 */
class KeystoreSessionStorage(context: Context) : SessionStorage {

    private val file = File(context.noBackupFilesDir, FILE_NAME)

    override fun read(): String? = runCatching {
        if (!file.exists()) return null
        val bytes = Base64.decode(file.readText(), Base64.NO_WRAP)
        val iv = bytes.copyOfRange(0, IV_BYTES)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, key(), GCMParameterSpec(TAG_BITS, iv))
        cipher.doFinal(bytes, IV_BYTES, bytes.size - IV_BYTES).decodeToString()
    }.getOrNull()

    override fun write(value: String) {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, key()) // IV 는 Keystore 가 매번 새로 만든다
        val sealed = cipher.iv + cipher.doFinal(value.encodeToByteArray())
        // 임시 파일에 쓰고 바꿔치기 → 쓰다가 앱이 죽어도 반쯤 쓴 파일이 남지 않게
        val tmp = File(file.parentFile, "$FILE_NAME.tmp")
        tmp.writeText(Base64.encodeToString(sealed, Base64.NO_WRAP))
        if (!tmp.renameTo(file)) {
            file.delete()
            tmp.renameTo(file)
        }
    }

    override fun clear() {
        file.delete()
    }

    private fun key(): SecretKey {
        val keyStore = KeyStore.getInstance(KEYSTORE).apply { load(null) }
        (keyStore.getKey(KEY_ALIAS, null) as? SecretKey)?.let { return it }
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
        const val KEYSTORE = "AndroidKeyStore"
        const val KEY_ALIAS = "reborn_session_key"
        const val FILE_NAME = "session.bin"
        const val TRANSFORMATION = "AES/GCM/NoPadding"
        const val IV_BYTES = 12
        const val TAG_BITS = 128
        const val KEY_BITS = 256
    }
}
