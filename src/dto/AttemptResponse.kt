package net.rocketparty.dto

import com.google.gson.annotations.SerializedName

data class AttemptResponse(
    @SerializedName("solved")
    val solved: Boolean
)