package org.wit.helpers

import kong.unirest.HttpResponse
import kong.unirest.JsonNode
import kong.unirest.Unirest
import org.jetbrains.exposed.sql.SchemaUtils
import org.joda.time.DateTime
import org.wit.config.DbConfig
import org.wit.db.Activities
import org.wit.db.Foods
import org.wit.db.Measurements
import org.wit.db.Users
import org.wit.domain.ActivityDTO
import org.wit.domain.FoodDTO
import org.wit.domain.MeasurementDTO
import org.wit.domain.UserDTO
import org.wit.repository.ActivityDAO
import org.wit.repository.FoodDAO
import org.wit.repository.MeasurementDAO
import org.wit.repository.UserDAO


val app = ServerContainer.instance
val origin = "http://localhost:" + app.port()

val nonExistingEmail = "112233445566778testUser@xxxxx.xx"
val validName = "Test User 1"
val validEmail = "testuser1@test.com"
val updatedName = "Updated Name"
val updatedEmail = "Updated Email"

//val validStarted  =  DateTime.parse("2020-06-10T05:59:27.258+01:00")
val updatedDescription = "Updated Description"
val updatedDuration = 30.0
val updatedCalories = 945
val updatedStarted = DateTime.parse("2020-06-11T05:59:27.258Z")


val updatedMealName = "Update MealName"
val updatedFoodName = "Updated FoodName"
val updatedFoodTime = DateTime.parse("2020-06-11T05:59:27.258Z")

val updatedWeight = 52
val updatedHeight = 160
val updatedAddedOn = DateTime.parse("2020-06-11T05:59:27.258Z")

val users = arrayListOf<UserDTO>(
    UserDTO(name = "Alice1 Wonderland", email = "alice@wonderland.com", id = 1),
    UserDTO(name = "Bob Cat", email = "bob@cat.ie", id = 2),
    UserDTO(name = "Mary Contrary", email = "mary@contrary.com", id = 3),
    UserDTO(name = "Carol Singer", email = "carol@singer.com", id = 4)
)

val activities = arrayListOf<ActivityDTO>(
    ActivityDTO(id = 1, description = "Running", duration = 22.0, calories = 230, started = DateTime.now(), userId = 1),
    ActivityDTO(id = 2, description = "Hopping", duration = 10.5, calories = 80, started = DateTime.now(), userId = 1),
    ActivityDTO(id = 3, description = "Walking", duration = 12.0, calories = 120, started = DateTime.now(), userId = 2)
)

val foods = arrayListOf<FoodDTO>(
    FoodDTO(id = 1, mealname = "Breakfast", foodname = "Milk and Cornflakes", calories = 230, foodtime = DateTime.now(), userId = 1),
    FoodDTO(id = 2, mealname = "Lunch", foodname = "Rice and chicken", calories = 80, foodtime = DateTime.now(), userId = 1),
    FoodDTO(id = 3, mealname = "Breakfast", foodname = "Milk and Egg", calories = 120, foodtime = DateTime.now(), userId = 2)
)

val measurements = arrayListOf<MeasurementDTO>(
    MeasurementDTO(id = 1, weight = 52, height = 150, addedon = DateTime.now(), userId = 1),
    MeasurementDTO(id = 2, weight = 62, height = 160, addedon = DateTime.now(), userId = 1),
    MeasurementDTO(id = 3, weight = 42, height = 140, addedon = DateTime.now(), userId = 2)
)

fun populateUserTable(): UserDAO {
    SchemaUtils.create(Users)
    val userDAO = UserDAO()
    userDAO.save(users.get(0))
    userDAO.save(users.get(1))
    userDAO.save(users.get(2))
    return userDAO
}

fun populateActivityTable(): ActivityDAO {
    SchemaUtils.create(Activities)
    val activityDAO = ActivityDAO()
    activityDAO.save(activities.get(0))
    activityDAO.save(activities.get(1))
    activityDAO.save(activities.get(2))
    return activityDAO
}

fun populateFoodTable(): FoodDAO {
    SchemaUtils.create(Foods)
    val foodDAO = FoodDAO()
    foodDAO.save(foods.get(0))
    foodDAO.save(foods.get(1))
    foodDAO.save(foods.get(2))
    return foodDAO
}

fun populateMeasurementTable(): MeasurementDAO {
    SchemaUtils.create(Measurements)
    val measurementDAO = MeasurementDAO()
    measurementDAO.save(measurements.get(0))
    measurementDAO.save(measurements.get(1))
    measurementDAO.save(measurements.get(2))
    return measurementDAO
}

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

//--------------------------------------------------------------------------------------
// ACTIVITY - METHODS
//--------------------------------------------------------------------------------------

//helper function to retrieve all activities
fun retrieveAllActivities(): HttpResponse<JsonNode> {
    return Unirest.get(origin + "/api/activities").asJson()
}

//helper function to retrieve activities by user id
fun retrieveActivitiesByUserId(id: Int): HttpResponse<JsonNode> {
    return Unirest.get(origin + "/api/users/${id}/activities").asJson()
}

//helper function to retrieve activity by activity id
fun retrieveActivityByActivityId(id: Int): HttpResponse<JsonNode> {
    return Unirest.get(origin + "/api/activities/${id}").asJson()
}

//helper function to delete an activity by activity id
fun deleteActivityByActivityId(id: Int): HttpResponse<String> {
    return Unirest.delete(origin + "/api/activities/$id").asString()
}

//helper function to delete an activity by activity id
fun deleteActivitiesByUserId(id: Int): HttpResponse<String> {
    return Unirest.delete(origin + "/api/users/$id/activities").asString()
}

//helper function to add a test user to the database
fun updateActivity(id: Int, description: String, duration: Double, calories: Int,
                           started: DateTime, userId: Int): HttpResponse<JsonNode> {
    return Unirest.patch(origin + "/api/activities/$id")
        .body("""
                {
                  "description":"$description",
                  "duration":$duration,
                  "calories":$calories,
                  "started":"$started",
                  "userId":$userId
                }
            """.trimIndent()).asJson()
}

//helper function to add an activity
fun addActivity(description: String, duration: Double, calories: Int,
                        started: DateTime, userId: Int): HttpResponse<JsonNode> {
    return Unirest.post(origin + "/api/activities")
        .body("""
                {
                   "description":"$description",
                   "duration":$duration,
                   "calories":$calories,
                   "started":"$started",
                   "userId":$userId
                }
            """.trimIndent())
        .asJson()
}

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