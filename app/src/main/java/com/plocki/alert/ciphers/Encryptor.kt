package com.plocki.alert.ciphers

import android.security.keystore.KeyProperties
import android.security.keystore.KeyGenParameterSpec
import com.plocki.alert.models.EncryptionResult
import java.io.IOException
import java.security.*
import javax.crypto.*


class Encryptor {
    private val transformation = "AES/GCM/NoPadding"
    private val androidKeyStore = "AndroidKeyStore"

    private var encryption: ByteArray? = null
    private var iv: ByteArray? = null

    @Throws(
        UnrecoverableEntryException::class,
        NoSuchAlgorithmException::class,
        KeyStoreException::class,
        NoSuchProviderException::class,
        NoSuchPaddingException::class,
        InvalidKeyException::class,
        IOException::class,
        InvalidAlgorithmParameterException::class,
        SignatureException::class,
        BadPaddingException::class,
        IllegalBlockSizeException::class
    )
    fun encryptText(alias: String, textToEncrypt: String): EncryptionResult {

        val cipher = Cipher.getInstance(transformation)
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(alias))

        iv = cipher.iv
        encryption = cipher.doFinal(textToEncrypt.toByteArray(charset("UTF-8")))

        return EncryptionResult(encryption!!, iv!!)
    }

    @Throws(
        NoSuchAlgorithmException::class,
        NoSuchProviderException::class,
        InvalidAlgorithmParameterException::class
    )
    private fun getSecretKey(alias: String): SecretKey {

        val keyGenerator = KeyGenerator
            .getInstance(KeyProperties.KEY_ALGORITHM_AES, androidKeyStore)

        keyGenerator.init(
            KeyGenParameterSpec.Builder(
                alias,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .build()
        )

        return keyGenerator.generateKey()
    }

}