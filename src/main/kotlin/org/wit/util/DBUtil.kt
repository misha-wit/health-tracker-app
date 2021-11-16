package org.wit.util

import org.jetbrains.exposed.sql.Alias
import org.jetbrains.exposed.sql.ResultRow
import org.wit.db.Activities
import org.wit.db.Foods
import org.wit.db.Measurements
import org.wit.db.Users
import org.wit.domain.*

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

fun mapToMeasurementDTO(it: ResultRow) = MeasurementDTO(
    id = it[Measurements.id],
    weight = it[Measurements.weight],
    height = it[Measurements.height],
    addedon = it[Measurements.addedon],
    userId = it[Measurements.userId]
)

fun mapToAnalysisDTO(it: ResultRow, measurements : Alias<Measurements>, foods : Alias<Foods> ) = AnalysisDTO(
    calories = it[foods[Foods.calories]],
    weight = it[measurements[Measurements.weight]],
    userId = it[Users.id]
)