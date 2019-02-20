package net.rocketparty.dto.model

import com.google.gson.annotations.SerializedName

data class TaskDeltaDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("category")
    val categoryId: Int?,
    @SerializedName("reward")
    val reward: Int?,
    @SerializedName("attachments")
    val attachments: List<String>?
)