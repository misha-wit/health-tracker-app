package org.wit.config

import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.*
import io.javalin.plugin.rendering.vue.VueComponent
import org.wit.controllers.AnalysisAPI
//import org.wit.controllers.AnalysisAPI
import org.wit.controllers.FoodTrackerAPI
import org.wit.controllers.HealthTrackerAPI
import org.wit.controllers.MeasurementTrackerAPI

class JavalinConfig {

    fun startJavalinService(): Javalin {

        val app = Javalin.create { config ->
            config.enableWebjars()
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
            get(   "/api/users/:user-id/foods", FoodTrackerAPI::getFoodsByUserId)
            get(   "/api/users/:user-id/measurements", MeasurementTrackerAPI::getMeasurementsByUserId)
            post(  "/api/users", HealthTrackerAPI::addUser)
            delete("/api/users/:user-id", HealthTrackerAPI::deleteUser)
            delete("/api/users/:user-id/activities", HealthTrackerAPI::deleteActivityByUserId)
            delete("/api/users/:user-id/foods", FoodTrackerAPI::deleteFoodByUserId)
            delete("/api/users/:user-id/measurements", MeasurementTrackerAPI::deleteMeasurementByUserId)
            patch( "/api/users/:user-id", HealthTrackerAPI::updateUser)

            //ACTIVITIES - API CRUD
            get(   "/api/activities", HealthTrackerAPI::getAllActivities)
            get(   "/api/activities/:activity-id", HealthTrackerAPI::getActivitiesByActivityId)
            post(  "/api/activities", HealthTrackerAPI::addActivity)
            delete("/api/activities/:activity-id", HealthTrackerAPI::deleteActivityByActivityId)
            patch( "/api/activities/:activity-id", HealthTrackerAPI::updateActivity)

            //FOODS - API CRUD
            get("/api/foods", FoodTrackerAPI::getAllFoods)
            get("/api/foods/:food-id", FoodTrackerAPI::getFoodsByFoodId)
            post("/api/foods", FoodTrackerAPI::addFood)
            delete("/api/foods/:food-id", FoodTrackerAPI::deleteFoodByFoodId)
            patch( "/api/foods/:food-id", FoodTrackerAPI::updateFood)

            //MEASUREMENTS HISTORY- API CRUD
            get("/api/measurements", MeasurementTrackerAPI::getAllMeasurements)
            get("/api/measurements/:measurement-id", MeasurementTrackerAPI::getMeasurementsByMeasurementId)
            post("/api/measurements", MeasurementTrackerAPI::addMeasurement)
            delete("/api/measurements/:measurement-id", MeasurementTrackerAPI::deleteMeasurementByMeasurementId)
            patch("/api/measurements/:measurement-id", MeasurementTrackerAPI::updateMeasurement)

            //ANALYSIS API
            get("/api/analysis/measurement/:user-id", AnalysisAPI::getAnalysisDetails)

            // The @routeComponent that we added in layout.html earlier will be replaced
            // by the String inside of VueComponent. This means a call to / will load
            // the layout and display our <home-page> component.
            get("/", VueComponent("<home-page></home-page>"))
            get("/users", VueComponent("<user-overview></user-overview>"))
            get("/users/:user-id", VueComponent("<user-profile></user-profile>"))
            get("/users/:user-id/activities", VueComponent("<user-activity-overview></user-activity-overview>"))

            get("/activities", VueComponent("<activity-overview></activity-overview>"))
            get("/activities/:activity-id", VueComponent("<activity-profile></activity-profile>"))
        }
    }



    private fun getHerokuAssignedPort(): Int {
        val herokuPort = System.getenv("PORT")
        return if (herokuPort != null) {
            Integer.parseInt(herokuPort)
        } else 7000
    }
}