package net.rocketparty.dto.model

import com.google.gson.annotations.SerializedName

data class FullTaskInfoDto(
    @SerializedName("task_info")
    val essential: BasicTaskInfoDto,
    @SerializedName("task_description")
    val description: TaskDescriptionDto,
    @SerializedName("solved")
    val solved: Boolean
)