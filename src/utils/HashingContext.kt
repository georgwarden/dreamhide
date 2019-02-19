package net.rocketparty.utils

import kotlinx.io.core.toByteArray
import java.nio.charset.Charset
import java.security.MessageDigest
import javax.xml.bind.DatatypeConverter

class HashingContext internal constructor(method: String) {

    private val pool = Pool<MessageDigest>(
        { MessageDigest.getInstance(method) },
        { it.reset() }
    )

    fun String.hashed(): String {
        val digest = pool.obtain()
        val result = digest.digest(this.toByteArray())
            .let(DatatypeConverter::printHexBinary)
            .toLowerCase()
        pool.free(digest)
        return result
    }

}

fun Md5Context() = HashingContext("MD5")