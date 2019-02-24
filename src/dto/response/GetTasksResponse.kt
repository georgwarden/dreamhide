package net.rocketparty.dto.response

import com.google.gson.annotations.SerializedName
import net.rocketparty.dto.model.TaskEssentialsDto

data class GetTasksResponse(
    @SerializedName("tasks")
    val tasks: List<TaskEssentialsDto>
)