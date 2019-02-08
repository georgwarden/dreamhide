package net.rocketparty.repository

import net.rocketparty.entity.Team

interface TeamRepository {

    fun findById(id: Int): Team?
    fun getAll(): List<Team>

}