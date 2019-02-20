package net.rocketparty.dto.response

import com.google.gson.annotations.SerializedName
import net.rocketparty.dto.model.TeamDto

data class GetTeamsResponse(
    @SerializedName("teams")
    val teams: List<TeamDto>
)