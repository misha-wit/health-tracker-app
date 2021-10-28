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
            get("/api/users", HealthTrackerAPI::getAllUsers)
            get("/api/users/:user-id", HealthTrackerAPI::getUserByUserId)
            post("/api/users", HealthTrackerAPI::addUser)
            get("/api/users/email/:email", HealthTrackerAPI::getUserByEmail)
            delete("/api/users/:user-id", HealthTrackerAPI::deleteUser)
            patch("/api/users/:user-id", HealthTrackerAPI::updateUser)
            get("/api/users/:user-id/activities", HealthTrackerAPI::getActivitiesByUserId)
            get("/api/users/:user-id/foods", HealthTrackerAPI::getFoodsByUserId)
            get("/api/activities", HealthTrackerAPI::getAllActivities)
            get("/api/activities/activity-id", HealthTrackerAPI::getActivitiesByActivityId)
            post("/api/activities", HealthTrackerAPI::addActivity)
            get("/api/foods", HealthTrackerAPI::getAllFoods)
            get("/api/foods/:food-id", HealthTrackerAPI::getFoodsByFoodId)
            post("/api/foods", HealthTrackerAPI::addFood)


        }
    }
    private fun getHerokuAssignedPort(): Int {
        val herokuPort = System.getenv("PORT")
        return if (herokuPort != null) {
            Integer.parseInt(herokuPort)
        } else 7000
    }
}