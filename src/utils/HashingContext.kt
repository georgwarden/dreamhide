package net.rocketparty.utils

import kotlinx.io.core.toByteArray
import java.nio.charset.Charset
import java.security.MessageDigest

class HashingContext internal constructor(method: String) {

    private val pool = Pool<MessageDigest>(
        { MessageDigest.getInstance(method) },
        { it.reset() }
    )

    fun String.hashed(): String {
        val digest = pool.obtain()
        val result = digest.digest(this.toByteArray(Charset.forName("UTF-8")))
            .toString(Charset.forName("UTF-8"))
        pool.free(digest)
        return result
    }

}

fun Md5Context() = HashingContext("MD5")