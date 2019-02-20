package net.rocketparty.dto.response

import com.google.gson.annotations.SerializedName
import net.rocketparty.dto.model.CategoryDto

data class CategoriesResponse(
    @SerializedName("categories")
    val categories: List<CategoryDto>
)