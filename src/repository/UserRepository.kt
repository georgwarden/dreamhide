package net.rocketparty.repository

import net.rocketparty.entity.User

interface UserRepository {

    fun findByName(name: String): User?

}