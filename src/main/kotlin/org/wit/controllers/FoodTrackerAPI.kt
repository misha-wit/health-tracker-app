package org.wit.controllers

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.javalin.http.Context
import org.wit.domain.ActivityDTO
import org.wit.domain.FoodDTO
import org.wit.repository.FoodDAO
import org.wit.repository.UserDAO
import org.wit.util.jsonToObject

object FoodTrackerAPI {
    private val userDao = UserDAO()
    private val foodDAO = FoodDAO()
    //--------------------------------------------------------------
    // FoodDAO specifics
    //-------------------------------------------------------------

    fun getAllFoods(ctx: Context) {
        val foods = foodDAO.getAll()
        if(foods.size!=0){
            ctx.status(200)
        }
        else{
            ctx.status(404)
        }
        ctx.json(foods)
    }



    fun getFoodsByUserId(ctx: Context) {
        if (userDao.findById(ctx.pathParam("user-id").toInt()) != null) {
            val foods = foodDAO.findByUserId(ctx.pathParam("user-id").toInt())
            if (foods.size > 0) {
                ctx.json(foods)
                ctx.status(200)
            }
            else{
                ctx.status(404)
            }
        }
        else{
            ctx.status(404)
        }
    }

    fun getFoodsByFoodId(ctx: Context) {
        val food = foodDAO.findByFoodId((ctx.pathParam("food-id").toInt()))
        if (food != null){
            ctx.json(food)
            ctx.status(200)
        }
        else{
            ctx.status(404)
        }
    }

    fun addFood(ctx: Context) {
        val foodDTO : FoodDTO = jsonToObject(ctx.body())
        val userId = userDao.findById(foodDTO.userId)
        if (userId != null) {
            val foodId = foodDAO.save(foodDTO)
            if (foodId != null) {
                foodDTO.id = foodId
                ctx.json(foodDTO)
                ctx.status(201)
            }
        }
        else{
            ctx.status(404)
        }
    }

    fun deleteFoodByFoodId(ctx: Context){
        if (foodDAO.deleteByFoodId(ctx.pathParam("food-id").toInt()) != 0)
            ctx.status(204)
        else
            ctx.status(404)

    }

    fun deleteFoodByUserId(ctx: Context){
        if (foodDAO.deleteByUserId(ctx.pathParam("user-id").toInt()) != 0)
            ctx.status(204)
        else
            ctx.status(404)
    }

    fun updateFood(ctx: Context){
        val food : FoodDTO = jsonToObject(ctx.body())
        if (foodDAO.updateByFoodId(
                foodId = ctx.pathParam("food-id").toInt(),
                foodDTO=food) != 0)
            ctx.status(204)
        else
            ctx.status(404)
    }

}