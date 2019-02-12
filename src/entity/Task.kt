package net.rocketparty.entity

data class Task(
    val id: Int,
    val name: String,
    val description: String,
    val reward: Int,
    val category: Category,
    val flag: String,
    val attachments: List<Attachment>
)