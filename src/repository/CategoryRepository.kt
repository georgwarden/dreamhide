package net.rocketparty.repository

import net.rocketparty.entity.Category

interface CategoryRepository {

    fun findAll(): List<Category>

}