package org.wit.controllers

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.javalin.http.Context
import org.wit.domain.ActivityDTO
import org.wit.domain.FoodDTO
import org.wit.domain.MeasurementDTO
import org.wit.repository.MeasurementDAO
import org.wit.repository.UserDAO
import org.wit.util.jsonToObject

object MeasurementTrackerAPI {
    private val userDao = UserDAO()
    private val measurementDAO = MeasurementDAO()

    //--------------------------------------------------------------
    // Measurement Specifics
    //-------------------------------------------------------------

    fun getAllMeasurements(ctx: Context) {
        val measurements = measurementDAO.getAll()
        if(measurements.size!=0){
            ctx.status(200)
        }
        else{
            ctx.status(404)
        }
        ctx.json(measurements)
    }

    fun getMeasurementsByUserId(ctx: Context) {
        if (userDao.findById(ctx.pathParam("user-id").toInt()) != null) {
            val measurements = measurementDAO.findByUserId(ctx.pathParam("user-id").toInt())
            if (measurements.size > 0) {
                ctx.json(measurements)
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

    fun getMeasurementsByMeasurementId(ctx: Context) {
        val measurement = measurementDAO.findByMeasurementId((ctx.pathParam("measurement-id").toInt()))
        if (measurement != null){
            ctx.json(measurement)
            ctx.status(200)
        }
        else{
            ctx.status(404)
        }
    }

    fun addMeasurement(ctx: Context) {
        /*val mapper = jacksonObjectMapper()
            .registerModule(JodaModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        val measurement = mapper.readValue<MeasurementDTO>(ctx.body())
        measurementDAO.save(measurement)
        ctx.json(measurement)*/
        val measurementDTO : MeasurementDTO = jsonToObject(ctx.body())
        val userId = userDao.findById(measurementDTO.userId)
        if (userId != null) {
            val measurementId = measurementDAO.save(measurementDTO)
            if (measurementId != null) {
                measurementDTO.id = measurementId
                ctx.json(measurementDTO)
                ctx.status(201)
            }
        }
        else{
            ctx.status(404)
        }
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
        val measurement : MeasurementDTO = jsonToObject(ctx.body())
        if (measurementDAO.updateByMeasurementId(
                measurementId = ctx.pathParam("measurement-id").toInt(),
                measurementDTO=measurement) != 0)
            ctx.status(204)
        else
            ctx.status(404)
    }
}