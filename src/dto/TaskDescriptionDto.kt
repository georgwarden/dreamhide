package net.rocketparty.dto

import com.google.gson.annotations.SerializedName

data class TaskDescriptionDto(
    @SerializedName("description")
    val text: String,
    @SerializedName("attachments")
    val attachments: List<String>
)