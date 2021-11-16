package org.wit.util

import kong.unirest.HttpResponse
import kong.unirest.JsonNode
import kong.unirest.Unirest
import org.joda.time.DateTime
import org.wit.helpers.origin

//--------------------------------------------------------------------------------------
// FOOD - METHODS
//--------------------------------------------------------------------------------------

//helper function to retrieve all foods
fun retrieveAllFoods(): HttpResponse<JsonNode> {
    return Unirest.get(origin + "/api/foods").asJson()
}

//helper function to retrieve foods by user id
fun retrieveFoodsByUserId(id: Int): HttpResponse<JsonNode> {
    return Unirest.get(origin + "/api/users/${id}/foods").asJson()
}

//helper function to retrieve Food by Food id
fun retrieveFoodByFoodId(id: Int): HttpResponse<JsonNode> {
    return Unirest.get(origin + "/api/foods/${id}").asJson()
}

//helper function to delete a Food by Food id
fun deleteFoodByFoodId(id: Int): HttpResponse<String> {
    return Unirest.delete(origin + "/api/foods/$id").asString()
}

//helper function to delete a Food by Food id
fun deleteFoodsByUserId(id: Int): HttpResponse<String> {
    return Unirest.delete(origin + "/api/users/$id/foods").asString()
}

//helper function to add a test user to the database
fun updateFood(id: Int, mealname: String, foodname: String, calories: Int,
               foodtime: DateTime, userId: Int): HttpResponse<JsonNode> {
    return Unirest.patch(origin + "/api/foods/$id")
        .body("""
                {
                   "mealname":"$mealname",
                   "foodname":"$foodname",
                   "calories":$calories,
                   "foodtime":"$foodtime",
                   "userId":$userId
                }
            """.trimIndent()).asJson()
}

//helper function to add a Food
fun addFood(mealname: String, foodname: String, calories: Int,
            foodtime: DateTime, userId: Int): HttpResponse<JsonNode> {
    return Unirest.post(origin + "/api/foods")
        .body("""
                {
                   "mealname":"$mealname",
                   "foodname":"$foodname",
                   "calories":$calories,
                   "foodtime":"$foodtime",
                   "userId":$userId
                }
            """.trimIndent())
        .asJson()
}