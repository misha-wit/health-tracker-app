package org.wit.util

import kong.unirest.HttpResponse
import kong.unirest.JsonNode
import kong.unirest.Unirest
import org.joda.time.DateTime
import org.wit.helpers.origin

//--------------------------------------------------------------------------------------
// MEASUREMENT - METHODS
//--------------------------------------------------------------------------------------

//helper function to retrieve all measurements
fun retrieveAllMeasurements(): HttpResponse<JsonNode> {
    return Unirest.get(origin + "/api/measurements").asJson()
}

//helper function to retrieve measurements by user id
fun retrieveMeasurementsByUserId(id: Int): HttpResponse<JsonNode> {
    return Unirest.get(origin + "/api/users/${id}/measurements").asJson()
}

//helper function to retrieve measurement by measurement id
fun retrieveMeasurementByMeasurementId(id: Int): HttpResponse<JsonNode> {
    return Unirest.get(origin + "/api/measurements/${id}").asJson()
}

//helper function to delete a measurement by measurement id
fun deleteMeasurementByMeasurementId(id: Int): HttpResponse<String> {
    return Unirest.delete(origin + "/api/measurements/$id").asString()
}

//helper function to delete a measurement by measurement id
fun deleteMeasurementsByUserId(id: Int): HttpResponse<String> {
    return Unirest.delete(origin + "/api/users/$id/measurements").asString()
}

//helper function to add a test user to the database
fun updateMeasurement(id: Int, weight: Int, height: Int,
                      addedon: DateTime, userId: Int): HttpResponse<JsonNode> {
    return Unirest.patch(origin + "/api/measurements/$id")
        .body("""
                {
                   "weight":$weight,
                   "height":$height,                
                   "addedon":"$addedon",
                   "userId":$userId
                }
            """.trimIndent()).asJson()
}

//helper function to add a measurement
fun addMeasurement(weight: Int, height: Int,
                   addedon: DateTime, userId: Int): HttpResponse<JsonNode> {
    return Unirest.post(origin + "/api/measurements")
        .body("""
                {
                   "weight":$weight,
                   "height":$height,                   
                   "addedon":"$addedon",
                   "userId":$userId
                }
            """.trimIndent())
        .asJson()
}

