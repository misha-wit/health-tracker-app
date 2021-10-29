package org.wit.config

import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.*
import org.wit.controllers.HealthTrackerAPI

class JavalinConfig {

    fun startJavalinService(): Javalin {

        val app = Javalin.create().apply {
            exception(Exception::class.java) { e, ctx -> e.printStackTrace() }
            error(404) { ctx -> ctx.json("404 - Not Found") }
        }.start(getHerokuAssignedPort())

        registerRoutes(app)
        return app
    }

    private fun registerRoutes(app: Javalin) {
        app.routes {
            //USERS - API CRUD
            get(   "/api/users", HealthTrackerAPI::getAllUsers)
            get(   "/api/users/:user-id", HealthTrackerAPI::getUserByUserId)
            get(   "/api/users/email/:email", HealthTrackerAPI::getUserByEmail)
            get(   "/api/users/:user-id/activities", HealthTrackerAPI::getActivitiesByUserId)
            get(   "/api/users/:user-id/foods", HealthTrackerAPI::getFoodsByUserId)
            get(   "/api/users/:user-id/measurements", HealthTrackerAPI::getMeasurementsByUserId)
            post(  "/api/users", HealthTrackerAPI::addUser)
            delete("/api/users/:user-id", HealthTrackerAPI::deleteUser)
            delete("/api/users/:user-id/activities", HealthTrackerAPI::deleteActivityByUserId)
            delete("/api/users/:user-id/foods", HealthTrackerAPI::deleteFoodByUserId)
            delete("/api/users/:user-id/measurements", HealthTrackerAPI::deleteMeasurementByUserId)
            patch( "/api/users/:user-id", HealthTrackerAPI::updateUser)

            //ACTIVITIES - API CRUD
            get(   "/api/activities", HealthTrackerAPI::getAllActivities)
            get(   "/api/activities/:activity-id", HealthTrackerAPI::getActivitiesByActivityId)
            post(  "/api/activities", HealthTrackerAPI::addActivity)
            delete("/api/activities/:activity-id", HealthTrackerAPI::deleteActivityByActivityId)
            patch( "/api/activities/:activity-id", HealthTrackerAPI::updateActivity)

            //FOODS - API CRUD
            get("/api/foods", HealthTrackerAPI::getAllFoods)
            get("/api/foods/:food-id", HealthTrackerAPI::getFoodsByFoodId)
            post("/api/foods", HealthTrackerAPI::addFood)
            delete("/api/foods/:food-id", HealthTrackerAPI::deleteFoodByFoodId)
            patch( "/api/foods/:food-id", HealthTrackerAPI::updateFood)

            //MEASUREMENTS HISTORY- API CRUD
            get("/api/measurements", HealthTrackerAPI::getAllMeasurements)
            get("/api/measurements/:measurement-id", HealthTrackerAPI::getMeasurementsByMeasurementId)
            post("/api/measurements", HealthTrackerAPI::addMeasurement)
            delete("/api/measurements/:measurement-id", HealthTrackerAPI::deleteMeasurementByMeasurementId)
            patch("/api/measurements/:measurement-id", HealthTrackerAPI::updateMeasurement)


        }
    }
    private fun getHerokuAssignedPort(): Int {
        val herokuPort = System.getenv("PORT")
        return if (herokuPort != null) {
            Integer.parseInt(herokuPort)
        } else 7000
    }
}