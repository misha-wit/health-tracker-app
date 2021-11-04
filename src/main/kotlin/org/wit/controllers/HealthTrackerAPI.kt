package org.wit.controllers

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.javalin.http.Context
import mu.KotlinLogging
import org.wit.domain.ActivityDTO
import org.wit.domain.FoodDTO
import org.wit.domain.MeasurementDTO
import org.wit.domain.UserDTO
import org.wit.repository.ActivityDAO
import org.wit.repository.FoodDAO
import org.wit.repository.MeasurementDAO
import org.wit.repository.UserDAO
import org.wit.util.jsonToObject

// SRP - Responsibility of this API is to manage IO between the DAOs and JSON context

object HealthTrackerAPI {
    private val logger = KotlinLogging.logger {}
    private val userDao = UserDAO()
    private val activityDAO = ActivityDAO()
    private val foodDAO = FoodDAO()
    private val measurementDAO = MeasurementDAO()

    //--------------------------------------------------------------
    // UserDAO specifics
    //-------------------------------------------------------------
    fun getAllUsers(ctx: Context) {
        val users = userDao.getAll()
        if (users.size != 0) {
            ctx.status(200)
        }
        else{
            ctx.status(404)
        }
        ctx.json(users)
    }

    fun getUserByUserId(ctx: Context) {
        val user = userDao.findById(ctx.pathParam("user-id").toInt())
        if (user != null) {
            ctx.json(user)
            ctx.status(200)
        }
        else{
            ctx.status(404)
        }
    }

    fun getUserByEmail(ctx: Context) {
        val user = userDao.findByEmail(ctx.pathParam("email"))
        if (user != null) {
            ctx.json(user)
            ctx.status(200)
        }
        else{
            ctx.status(404)
        }
    }

    fun addUser(ctx: Context) {
        val user : UserDTO = jsonToObject(ctx.body())
        val userId = userDao.save(user)
        if (userId != null) {
            user.id = userId
            ctx.json(user)
            ctx.status(201)
        }
    }

    fun deleteUser(ctx: Context){
        if (userDao.delete(ctx.pathParam("user-id").toInt()) != 0)
            ctx.status(204)
        else
            ctx.status(404)
    }

    fun updateUser(ctx: Context){
        val user : UserDTO = jsonToObject(ctx.body())
        if ((userDao.update(id = ctx.pathParam("user-id").toInt(), userDTO=user)) != 0)
            ctx.status(204)
        else
            ctx.status(404)
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
            if (activities.size > 0) {
                ctx.json(activities)
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

    fun getActivitiesByActivityId(ctx: Context) {
        val activity = activityDAO.findByActivityId((ctx.pathParam("activity-id").toInt()))
        if (activity != null){
            ctx.json(activity)
            ctx.status(200)
        }
        else{
            ctx.status(404)
        }
    }


    fun deleteActivityByActivityId(ctx: Context){
        if (activityDAO.deleteByActivityId(ctx.pathParam("activity-id").toInt()) != 0)
            ctx.status(204)
        else
            ctx.status(404)
    }

    fun deleteActivityByUserId(ctx: Context){
        if (activityDAO.deleteByUserId(ctx.pathParam("user-id").toInt()) != 0)
            ctx.status(204)
        else
            ctx.status(404)
    }

    fun updateActivity(ctx: Context){
        val activity : ActivityDTO = jsonToObject(ctx.body())
        if (activityDAO.updateByActivityId(
                activityId = ctx.pathParam("activity-id").toInt(),
                activityDTO=activity) != 0)
            ctx.status(204)
        else
            ctx.status(404)
    }

    fun addActivity(ctx: Context) {
        val activityDTO : ActivityDTO = jsonToObject(ctx.body())
        val userId = userDao.findById(activityDTO.userId)
        if (userId != null) {
            val activityId = activityDAO.save(activityDTO)
            if (activityId != null) {
                activityDTO.id = activityId
                ctx.json(activityDTO)
                ctx.status(201)
            }
        }
        else{
            ctx.status(404)
        }
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
        val mapper = jacksonObjectMapper()
            .registerModule(JodaModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        val food = mapper.readValue<FoodDTO>(ctx.body())
        if (foodDAO.updateByFoodId(
                foodId = ctx.pathParam("food-id").toInt(),
                foodDTO=food) != 0)
            ctx.status(204)
        else
            ctx.status(404)
    }

    //--------------------------------------------------------------
    // Measurement Specifics
    //-------------------------------------------------------------

    fun getAllMeasurements(ctx: Context) {
        ctx.json(measurementDAO.getAll())
    }

    fun getMeasurementsByUserId(ctx: Context) {
        if (userDao.findById(ctx.pathParam("user-id").toInt()) != null) {
            val measurements = measurementDAO.findByUserId(ctx.pathParam("user-id").toInt())
            if (measurements.size > 0)
                ctx.json(measurements)
        }
    }

    fun getMeasurementsByMeasurementId(ctx: Context) {
        val measurement = measurementDAO.findByMeasurementId((ctx.pathParam("measurement-id").toInt()))
        if (measurement != null){
            ctx.json(measurement)
        }
    }

    fun addMeasurement(ctx: Context) {
        val mapper = jacksonObjectMapper()
            .registerModule(JodaModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        val measurement = mapper.readValue<MeasurementDTO>(ctx.body())
        measurementDAO.save(measurement)
        ctx.json(measurement)
    }

    fun deleteMeasurementByMeasurementId(ctx: Context){
        if (measurementDAO.deleteByMeasurementId(ctx.pathParam("measurement-id").toInt()) != 0)
            ctx.status(204)
        else
            ctx.status(404)
    }

    fun deleteMeasurementByUserId(ctx: Context){
        if (measurementDAO.deleteByUserId(ctx.pathParam("user-id").toInt()) != 0)
            ctx.status(204)
        else
            ctx.status(404)
    }

    fun updateMeasurement(ctx: Context){
        val mapper = jacksonObjectMapper()
            .registerModule(JodaModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        val measurement = mapper.readValue<MeasurementDTO>(ctx.body())
        if (measurementDAO.updateByMeasurementId(
                measurementId = ctx.pathParam("measurement-id").toInt(),
                measurementDTO=measurement) != 0)
            ctx.status(204)
        else
            ctx.status(404)
    }

}
