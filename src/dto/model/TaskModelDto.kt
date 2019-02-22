package net.rocketparty.dto.model

import com.google.gson.annotations.SerializedName

data class TaskModelDto(
    @SerializedName("task_info")
    val info: BasicTaskInfoDto,
    @SerializedName("task_description")
    val description: TaskDescriptionDto
)