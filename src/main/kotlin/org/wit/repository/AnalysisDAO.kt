package org.wit.repository

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.wit.db.Activities
import org.wit.db.Foods
import org.wit.db.Measurements
import org.wit.db.Users
import org.wit.domain.AnalysisDTO
import org.wit.util.mapToActivityDTO
import org.wit.util.mapToAnalysisDTO

class AnalysisDAO {

    //Get the analysis of calorie intake and weight change
    fun getAll(id: Int): ArrayList<AnalysisDTO> {
        val measurements = Measurements.alias("measurements")
        val foods = Foods.alias("foods")
        //val users = Users.alias("users")
        val analysisList: ArrayList<AnalysisDTO> = arrayListOf()
        transaction {
            Users
                .join(measurements,JoinType.INNER,Users.id,measurements[Measurements.userId])
                .join(foods,JoinType.INNER,Users.id,foods[Foods.userId])
                .select(Users.id eq id).map {
                    analysisList.add(mapToAnalysisDTO(it,measurements,foods))
                }
        }
        return analysisList
    }





}