package net.rocketparty.dto.response

import com.google.gson.annotations.SerializedName

data class AttemptResponse(
    @SerializedName("solved")
    val solved: Boolean
)