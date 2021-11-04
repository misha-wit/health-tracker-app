package org.wit.repository

import mu.KotlinLogging
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.wit.db.Foods
import org.wit.db.Measurements
import org.wit.domain.MeasurementDTO
import org.wit.util.mapToMeasurementDTO

class MeasurementDAO {
    private val logger = KotlinLogging.logger {}
    //Get all the Measurement items in the database regardless of user id
    fun getAll(): ArrayList<MeasurementDTO> {
        val measurementsList: ArrayList<MeasurementDTO> = arrayListOf()
        transaction {
            Measurements.selectAll().map {
                measurementsList.add(mapToMeasurementDTO(it)) }
        }
        return measurementsList
    }

    //Find a specific Measurement by Measurement id
    fun findByMeasurementId(id: Int): MeasurementDTO?{
        return transaction {
            Measurements
                .select() { Measurements.id eq id}
                .map{ mapToMeasurementDTO(it) }
                .firstOrNull()
        }
    }

    //Find all Measurements for a specific user id
    fun findByUserId(userId: Int): List<MeasurementDTO>{
        return transaction {
            Measurements
                .select { Measurements.userId eq userId}
                .map { mapToMeasurementDTO(it) }
        }
    }

    //Save a Measurement item to the database
    fun save(measurementDTO: MeasurementDTO) : Int?{
        return transaction {
            Measurements.insert {
                it[weight] = measurementDTO.weight
                it[height] = measurementDTO.height
                it[addedon] = measurementDTO.addedon
                it[userId] = measurementDTO.userId
            }
        } get Measurements.id
    }

    //update a specific measurement item by measurement item id
    fun updateByMeasurementId(measurementId: Int, measurementDTO: MeasurementDTO): Int{
        return transaction {
            Measurements.update ({
                Measurements.id eq measurementId}) {
                it[weight] = measurementDTO.weight
                it[height] = measurementDTO.height
                it[addedon] = measurementDTO.addedon
                it[userId] = measurementDTO.userId
            }
        }
    }

    //delete a specific measurement item by measurement item id
    fun deleteByMeasurementId (measurementId: Int): Int{
        return transaction{
            Measurements.deleteWhere { Measurements.id eq measurementId }
        }
    }

    //delete a specific measurement item by userId
    fun deleteByUserId (userId: Int): Int{
        return transaction{
            Measurements.deleteWhere { Measurements.userId eq userId }
        }
    }

}