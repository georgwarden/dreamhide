package net.rocketparty.dto

import com.google.gson.annotations.SerializedName

data class AuthorizationResponse(
    @SerializedName("token")
    val token: String
)