package net.rocketparty.dto.request

import com.google.gson.annotations.SerializedName

data class AttemptRequest(
    @SerializedName("task_id")
    val taskId: Int,
    @SerializedName("flag")
    val flag: String
)