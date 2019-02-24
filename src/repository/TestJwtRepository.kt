package net.rocketparty.repository

class TestJwtRepository : JwtRepository {

    override fun getIssuer(): String = "localhost"

    override fun getAudience(): String = "players"

    override fun getRealm(): String = "dreamhide"

    override fun getSecret(): String = "old"

    override fun getAdminAudience(): String = "admins"

}