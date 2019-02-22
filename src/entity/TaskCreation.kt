package net.rocketparty.entity

data class TaskCreation(
    val title: String,
    val description: String,
    val reward: Int,
    val categoryId: Int,
    val flag: String,
    val attachments: List<String>
)