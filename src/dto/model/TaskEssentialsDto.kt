package net.rocketparty.dto.model

import com.google.gson.annotations.SerializedName

data class TaskEssentialsDto(
    @SerializedName("task_info")
    val info: BasicTaskInfoDto,
    @SerializedName("solved")
    val solved: Boolean
)