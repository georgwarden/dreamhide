package net.rocketparty.dto

import com.google.gson.annotations.SerializedName

data class TaskGetRequest(
    @SerializedName("task_id")
    val taskId: Int
)