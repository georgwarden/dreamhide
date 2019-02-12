package net.rocketparty.repository

import net.rocketparty.entity.Category
import net.rocketparty.exposed.Categories
import net.rocketparty.exposed.toCategory
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class ExposedCategoriesRepository : CategoryRepository {

    override fun findAll(): List<Category> {
        return transaction {
            Categories.selectAll()
        }.map { row -> row.toCategory() }
    }

}