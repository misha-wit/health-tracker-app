package org.wit.helpers

import org.jetbrains.exposed.sql.SchemaUtils
import org.joda.time.DateTime
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


val nonExistingEmail = "112233445566778testUser@xxxxx.xx"
val validName = "Test User 1"
val validEmail = "testuser1@test.com"
val updatedName = "Updated Name"
val updatedEmail = "Updated Email"

val validStarted  =  DateTime.parse("2020-06-10T05:59:27.258+01:00")
val updatedDescription = "Updated Description"
val updatedDuration = 30.0
val updatedCalories = 945
val updatedStarted = DateTime.parse("2020-06-11T05:59:27.258Z")

val users = arrayListOf<UserDTO>(
    UserDTO(name = "Alice Wonderland", email = "alice@wonderland.com", id = 1),
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