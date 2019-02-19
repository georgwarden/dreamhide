package net.rocketparty.dto.response

import com.google.gson.annotations.SerializedName

data class AuthorizationResponse(
    @SerializedName("token")
    val token: String
)