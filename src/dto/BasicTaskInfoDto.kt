package net.rocketparty.dto

import com.google.gson.annotations.SerializedName

data class BasicTaskInfoDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("reward")
    val reward: Int,
    @SerializedName("category")
    val category: CategoryDto,
    @SerializedName("solved")
    val solved: Boolean
)