package net.rocketparty.entity

data class TaskDelta(
    val id: Int,
    val title: String?,
    val description: String?,
    val categoryId: Int?,
    val reward: Int?,
    val attachments: List<String>?
)