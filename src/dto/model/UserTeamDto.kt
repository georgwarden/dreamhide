package net.rocketparty.dto.model

import com.google.gson.annotations.SerializedName

data class UserTeamDto(
    @SerializedName("user")
    val user: UserDto,
    @SerializedName("team")
    val team: TeamDto
)