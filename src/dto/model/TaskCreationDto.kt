package net.rocketparty.dto.model

import com.google.gson.annotations.SerializedName

data class TaskCreationDto(
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("reward")
    val reward: Int,
    @SerializedName("category")
    val categoryId: Int,
    @SerializedName("attachments")
    val attachments: List<String>
)