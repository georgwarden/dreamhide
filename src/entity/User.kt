package net.rocketparty.entity

data class User(
    val id: Int,
    val name: String,
    val passwordHash: String,
    val team: Team
)