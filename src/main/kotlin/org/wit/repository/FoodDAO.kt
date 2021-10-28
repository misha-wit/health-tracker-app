package org.wit.repository

import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.wit.db.Foods
import org.wit.domain.FoodDTO
import org.wit.util.mapToFoodDTO

class FoodDAO {

    //Get all the food items in the database regardless of user id
    fun getAll(): ArrayList<FoodDTO> {
        val foodList: ArrayList<FoodDTO> = arrayListOf()
        transaction {
            Foods.selectAll().map {
                foodList.add(mapToFoodDTO(it)) }
        }
        return foodList
    }

    //Find a specific food item by food item id
    fun findByFoodId(id: Int): FoodDTO?{
        return transaction {
            Foods
                .select() { Foods.id eq id}
                .map{mapToFoodDTO(it)}
                .firstOrNull()
        }
    }

    //Find all Foods for a specific user id
    fun findByUserId(userId: Int): List<FoodDTO>{
        return transaction {
            Foods
                .select {Foods.userId eq userId}
                .map {mapToFoodDTO(it)}
        }
    }

    //Save a food item to the database
    fun save(foodDTO: FoodDTO){
        transaction {
            Foods.insert {
                it[mealname] = foodDTO.mealname
                it[foodname] = foodDTO.foodname
                it[calories] = foodDTO.calories
                it[foodtime] = foodDTO.foodtime
                it[userId] = foodDTO.userId
            }
        }
    }

}