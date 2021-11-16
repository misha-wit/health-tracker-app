package org.wit.util

import kong.unirest.HttpResponse
import kong.unirest.JsonNode
import kong.unirest.Unirest
import org.wit.helpers.origin


//--------------------------------------------------------------------------------------
// HELPER METHODS - could move them into a test utility class when submitting assignment
//--------------------------------------------------------------------------------------

//--------------------------------------------------------------------------------------
// USER - METHODS
//--------------------------------------------------------------------------------------

//helper function to add a test user to the database
fun addUser (name: String, email: String): HttpResponse<JsonNode> {
    return Unirest.post(origin + "/api/users")
        .body("{\"name\":\"$name\", \"email\":\"$email\"}")
        .asJson()
}

//helper function to delete a test user from the database
fun deleteUser (id: Int): HttpResponse<String> {
    return Unirest.delete(origin + "/api/users/$id").asString()
}

//helper function to retrieve a test user from the database by email
fun retrieveUserByEmail(email : String) : HttpResponse<String> {
    return Unirest.get(origin + "/api/users/email/${email}").asString()
}

//helper function to retrieve a test user from the database by id
fun retrieveUserById(id: Int) : HttpResponse<String> {
    return Unirest.get(origin + "/api/users/${id}").asString()
}

//helper function to add a test user to the database
fun updateUser (id: Int, name: String, email: String): HttpResponse<JsonNode> {
    return Unirest.patch(origin + "/api/users/$id")
        .body("{\"name\":\"$name\", \"email\":\"$email\"}")
        .asJson()
}