package org.wit.controllers

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.javalin.http.Context
import org.wit.domain.ActivityDTO
import org.wit.domain.FoodDTO
import org.wit.domain.UserDTO
import org.wit.repository.ActivityDAO
import org.wit.repository.FoodDAO
import org.wit.repository.UserDAO

// SRP - Responsibility of this API is to manage IO between the DAOs and JSON context

object HealthTrackerAPI {

    private val userDao = UserDAO()
    private val activityDAO = ActivityDAO()
    private val foodDAO = FoodDAO()

    //--------------------------------------------------------------
    // UserDAO specifics
    //-------------------------------------------------------------
    fun getAllUsers(ctx: Context) {
        ctx.json(userDao.getAll())
    }

    fun getUserByUserId(ctx: Context) {
        val user = userDao.findById(ctx.pathParam("user-id").toInt())
        if (user != null) {
            ctx.json(user)
        }
    }

    fun getUserByEmail(ctx: Context) {
        val user = userDao.findByEmail(ctx.pathParam("email"))
        if (user != null) {
            ctx.json(user)
        }
    }

    fun addUser(ctx: Context) {
        val mapper = jacksonObjectMapper()
        val user = mapper.readValue<UserDTO>(ctx.body())
        userDao.save(user)
        ctx.json(user)
    }

    fun deleteUser(ctx: Context){
        userDao.delete(ctx.pathParam("user-id").toInt())
    }

    fun updateUser(ctx: Context){
        val mapper = jacksonObjectMapper()
        val user = mapper.readValue<UserDTO>(ctx.body())
        userDao.update(
            id = ctx.pathParam("user-id").toInt(),
            userDTO=user)
    }

    //--------------------------------------------------------------
    // ActivityDAO specifics
    //-------------------------------------------------------------

    fun getAllActivities(ctx: Context) {
        ctx.json(activityDAO.getAll())
    }

    fun getActivitiesByUserId(ctx: Context) {
        if (userDao.findById(ctx.pathParam("user-id").toInt()) != null) {
            val activities = activityDAO.findByUserId(ctx.pathParam("user-id").toInt())
            if (activities.isNotEmpty())
                ctx.json(activities)
        }
    }

    fun getActivitiesByActivityId(ctx: Context) {
        val activity = activityDAO.findByActivityId((ctx.pathParam("activity-id").toInt()))
        if (activity != null){
            ctx.json(activity)
        }
    }

    fun addActivity(ctx: Context) {
        val mapper = jacksonObjectMapper()
            .registerModule(JodaModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        val activity = mapper.readValue<ActivityDTO>(ctx.body())
        activityDAO.save(activity)
        ctx.json(activity)
    }

    //--------------------------------------------------------------
    // FoodDAO specifics
    //-------------------------------------------------------------

    fun getAllFoods(ctx: Context) {
        ctx.json(foodDAO.getAll())
    }

    fun getFoodsByUserId(ctx: Context) {
        if (userDao.findById(ctx.pathParam("user-id").toInt()) != null) {
            val foods = foodDAO.findByUserId(ctx.pathParam("user-id").toInt())
            if (foods.size > 0)
                ctx.json(foods)
        }
    }

    fun getFoodsByFoodId(ctx: Context) {
        val food = foodDAO.findByFoodId((ctx.pathParam("food-id").toInt()))
        if (food != null){
            ctx.json(food)
        }
    }

    fun addFood(ctx: Context) {
        val mapper = jacksonObjectMapper()
            .registerModule(JodaModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        val food = mapper.readValue<FoodDTO>(ctx.body())
        foodDAO.save(food)
        ctx.json(food)
    }
}
