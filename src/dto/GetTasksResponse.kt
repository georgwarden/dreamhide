package net.rocketparty.dto

import com.google.gson.annotations.SerializedName

data class GetTasksResponse(
    @SerializedName("tasks")
    val tasks: List<BasicTaskInfoDto>
)