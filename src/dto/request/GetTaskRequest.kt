package net.rocketparty.dto.request

import com.google.gson.annotations.SerializedName

data class GetTaskRequest(
    @SerializedName("id")
    val id: Int
)