package com.june.strongboxkey.util

import com.june.strongboxkey.constant.Constants.CURVE_TYPE
import com.june.strongboxkey.constant.Constants.KEY_AGREEMENT_ALGORITHM
import com.june.strongboxkey.constant.Constants.KEY_GEN_ALGORITHM
import com.june.strongboxkey.constant.Constants.MESSAGE_DIGEST_ALGORITHM
import com.june.strongboxkey.model.KeyPairModel
import java.security.*
import java.security.spec.ECGenParameterSpec
import javax.crypto.KeyAgreement

class KeyProvider {
    fun createKeyPair(): KeyPairModel {
        val keyPairGenerator = KeyPairGenerator.getInstance(KEY_GEN_ALGORITHM) //EC
        keyPairGenerator.initialize(ECGenParameterSpec(CURVE_TYPE)) //secp256r1
        val keyPair = keyPairGenerator.generateKeyPair()
        return KeyPairModel(keyPair.private, keyPair.public)
    }

    fun createSharedSecretHash(
        myPrivateKey: PrivateKey,
        counterpartPublicKey: PublicKey
    ): ByteArray {
        val keyAgreement = KeyAgreement.getInstance(KEY_AGREEMENT_ALGORITHM) //ECDH
        keyAgreement.init(myPrivateKey)
        keyAgreement.doPhase(counterpartPublicKey, true)
        val sharedSecret: ByteArray = keyAgreement.generateSecret()
        return hashSHA256(sharedSecret)
    }

    private fun hashSHA256(key: ByteArray): ByteArray {
        val hash: ByteArray
        try {
            val messageDigest = MessageDigest.getInstance(MESSAGE_DIGEST_ALGORITHM) //SHA-256
            messageDigest.update(key)
            hash = messageDigest.digest(getRandomNumbers())
        }
        catch (e: CloneNotSupportedException) {
            throw DigestException("$e")
        }
        return hash
    }

    private fun getRandomNumbers(): ByteArray {
        val randomByteArray = ByteArray(32)
        SecureRandom().nextBytes(randomByteArray)
        return randomByteArray
    }
}