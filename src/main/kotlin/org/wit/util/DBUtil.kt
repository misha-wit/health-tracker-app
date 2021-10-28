package org.wit.util

import org.jetbrains.exposed.sql.ResultRow
import org.wit.db.Activities
import org.wit.db.Foods
import org.wit.db.Users
import org.wit.domain.ActivityDTO
import org.wit.domain.FoodDTO
import org.wit.domain.UserDTO

fun mapToUserDTO(it: ResultRow) = UserDTO(
    id = it[Users.id],
    name = it[Users.name],
    email = it[Users.email]
)

fun mapToActivityDTO(it: ResultRow) = ActivityDTO(
    id = it[Activities.id],
    description = it[Activities.description],
    duration = it[Activities.duration],
    started = it[Activities.started],
    calories = it[Activities.calories],
    userId = it[Activities.userId]
)

fun mapToFoodDTO(it: ResultRow) = FoodDTO(
    id = it[Foods.id],
    mealname = it[Foods.mealname],
    foodname = it[Foods.foodname],
    foodtime = it[Foods.foodtime],
    calories = it[Foods.calories],
    userId = it[Foods.userId]
)
