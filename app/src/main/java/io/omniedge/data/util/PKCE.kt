package io.omniedge.data.util

import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom

data class PKCEInfo(
    val verifier: String,
    val challenge: String,
    val state: String
)

object PKCE {
    fun generate(): PKCEInfo {
        val state = randomString(32)
        val verifier = randomString(64)
        val challenge = generateChallenge(verifier)
        return PKCEInfo(verifier, challenge, state)
    }

    private fun randomString(length: Int): String {
        val charPool = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-._~"
        val random = SecureRandom()
        return (1..length)
            .map { random.nextInt(charPool.length) }
            .map(charPool::get)
            .joinToString("")
    }

    private fun generateChallenge(verifier: String): String {
        val bytes = verifier.toByteArray(Charsets.US_ASCII)
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return Base64.encodeToString(digest, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
    }
}
