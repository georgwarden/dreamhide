package net.rocketparty.dto

import com.google.gson.annotations.SerializedName

data class TaskDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("category")
    val category: CategoryDto,
    @SerializedName("reward")
    val reward: Int,
    @SerializedName("attachments")
    val attachments: List<String>,
    @SerializedName("solved")
    val solved: Boolean
)