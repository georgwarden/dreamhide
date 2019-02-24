package net.rocketparty.repository

interface JwtRepository {

    fun getIssuer(): String
    fun getAudience(): String
    fun getRealm(): String
    fun getSecret(): String

    fun getAdminAudience(): String


}