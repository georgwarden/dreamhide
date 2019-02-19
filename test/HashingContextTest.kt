package net.rocketparty

import net.rocketparty.utils.Md5Context
import kotlin.test.Test

class HashingContextTest {

    @Test
    fun testMD5() {
        val context = Md5Context()
        val result = context.run {
            "12345678".hashed()
        }
        assert(result == "25d55ad283aa400af464c76d713c07ad")
    }

}