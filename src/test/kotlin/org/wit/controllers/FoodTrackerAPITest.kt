package org.wit.controllers

import org.jetbrains.exposed.sql.Database
import org.junit.Assert
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.wit.domain.FoodDTO
import org.wit.domain.UserDTO
import org.wit.helpers.*
import org.wit.util.jsonToArrayWithDate
import org.wit.util.jsonToObject
import org.wit.util.jsonToObjectWithDate

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FoodTrackerAPITest {
    val db = Database.connect(
        "jdbc:postgresql://ec2-54-172-169-87.compute-1.amazonaws.com:5432/d75u9glj5k4v13?sslmode=require",
        driver = "org.postgresql.Driver",
        user = "ssbrrxzccwlwpd",
        password = "39966b431ff8422ea4aff334461b9972bc36039cb241f169029ab14f792ed5f6")
    @Nested
    inner class CreateFoods {
        //   post(  "/api/foods", HealthTrackerAPI::addFood)

        @Test
        fun `add a food when a user exists for it, returns a 201 response`() {

            //Arrange - add a user and an associated food that we plan to do a delete on
            val addedUser: UserDTO = jsonToObject(addUser(validName, validEmail).body.toString())

            val addFoodResponse = addFood(
                foods.get(0).mealname,
                foods.get(0).foodname, foods.get(0).calories, foods.get(0).foodtime, addedUser.id
            )
            Assert.assertEquals(201, addFoodResponse.status)

            //After - delete the user (Food will cascade delete in the database)
            deleteUser(addedUser.id)
        }

        @Test
        fun `add a food when no user exists for it, returns a 404 response`() {

            //Arrange - check there is no user for -1 id
            val userId = -1
            Assert.assertEquals(404, retrieveUserById(userId).status)

            val addFoodResponse = addFood(
                foods.get(0).mealname, foods.get(0).foodname,
                foods.get(0).calories, foods.get(0).foodtime, userId
            )
            Assert.assertEquals(404, addFoodResponse.status)
        }
    }

    @Nested
    inner class ReadFoods {
        //   get(   "/api/users/:user-id/foods", HealthTrackerAPI::getFoodsByUserId)
        //   get(   "/api/foods", HealthTrackerAPI::getAllFoods)
        //   get(   "/api/foods/:food-id", HealthTrackerAPI::getFoodsByFoodId)
        @Test
        fun `get all foods from the database returns 200 or 404 response`() {
            val response = retrieveAllFoods()
            if (response.status == 200){
                val retrievedFoods = jsonToArrayWithDate(response, Array<FoodDTO>::class.java)
                Assert.assertNotEquals(0, retrievedFoods.size)
            }
            else{
                Assert.assertEquals(404, response.status)
            }
        }

        @Test
        fun `get all foods by user id when user and foods exists returns 200 response`() {
            //Arrange - add a user and 3 associated foods that we plan to retrieve
            val addedUser : UserDTO = jsonToObject(addUser(validName, validEmail).body.toString())
            addFood(
                foods.get(0).mealname, foods.get(0).foodname,
                foods.get(0).calories, foods.get(0).foodtime, addedUser.id)
            addFood(
                foods.get(1).mealname, foods.get(1).foodname,
                foods.get(1).calories, foods.get(1).foodtime, addedUser.id)
            addFood(
                foods.get(2).mealname, foods.get(2).foodname,
                foods.get(2).calories, foods.get(2).foodtime, addedUser.id)

            //Assert and Act - retrieve the three added foods by user id
            val response = retrieveFoodsByUserId(addedUser.id)
            Assert.assertEquals(200, response.status)
            val retrievedFoods = jsonToArrayWithDate(response, Array<FoodDTO>::class.java)
            Assert.assertEquals(3, retrievedFoods.size)

            //After - delete the added user and assert a 204 is returned (foods are cascade deleted)
            Assert.assertEquals(204, deleteUser(addedUser.id).status)
        }

        @Test
        fun `get all foods by user id when no foods exist returns 404 response`() {
            //Arrange - add a user
            val addedUser : UserDTO = jsonToObject(addUser(validName, validEmail).body.toString())

            //Assert and Act - retrieve the foods by user id
            val response = retrieveFoodsByUserId(addedUser.id)
            Assert.assertEquals(404, response.status)

            //After - delete the added user and assert a 204 is returned
            Assert.assertEquals(204, deleteUser(addedUser.id).status)
        }

        @Test
        fun `get all foods by user id when no user exists returns 404 response`() {
            //Arrange
            val userId = -1

            //Assert and Act - retrieve foods by user id
            val response = retrieveFoodsByUserId(userId)
            Assert.assertEquals(404, response.status)
        }

        @Test
        fun `get food by food id when no food exists returns 404 response`() {
            //Arrange
            val foodId = -1
            //Assert and Act - attempt to retrieve the food by food id
            val response = retrieveFoodByFoodId(foodId)
            Assert.assertEquals(404, response.status)
        }


        @Test
        fun `get food by food id when food exists returns 200 response`() {
            //Arrange - add a user and associated food
            val addedUser : UserDTO = jsonToObject(addUser(validName, validEmail).body.toString())
            val addFoodResponse = addFood(
                foods.get(0).mealname,
                foods.get(0).foodname, foods.get(0).calories,
                foods.get(0).foodtime, addedUser.id)
            Assert.assertEquals(201, addFoodResponse.status)
            val addedFood = jsonToObjectWithDate(addFoodResponse, FoodDTO::class.java)

            //Act & Assert - retrieve the food by food id
            val response = retrieveFoodByFoodId(addedFood.id)
            Assert.assertEquals(200, response.status)

            //After - delete the added user and assert a 204 is returned
            Assert.assertEquals(204, deleteUser(addedUser.id).status)
        }

    }

    @Nested
    inner class UpdateFoods {
        //  patch( "/api/foods/:food-id", HealthTrackerAPI::updateFood)
        @Test
        fun `updating an food by food id when it doesn't exist, returns a 404 response`() {
            val userId = -1
            val foodID = -1

            //Arrange - check there is no user for -1 id
            Assert.assertEquals(404, retrieveUserById(userId).status)

            //Act & Assert - attempt to update the details of an food/user that doesn't exist
            Assert.assertEquals(
                404, updateFood(
                    foodID, updatedMealName, updatedFoodName,
                    updatedCalories, updatedFoodTime, userId
                ).status
            )
        }

        @Test
        fun `updating an food by food id when it exists, returns 204 response`() {

            //Arrange - add a user and an associated food that we plan to do an update on
            val addedUser : UserDTO = jsonToObject(addUser(validName, validEmail).body.toString())
            val addFoodResponse = addFood(
                foods.get(0).mealname,
                foods.get(0).foodname, foods.get(0).calories,
                foods.get(0).foodtime, addedUser.id)
            Assert.assertEquals(201, addFoodResponse.status)
            val addedFood = jsonToObjectWithDate(addFoodResponse, FoodDTO::class.java)

            //Act & Assert - update the added food and assert a 204 is returned
            val updatedFoodResponse = updateFood(addedFood.id, updatedMealName,
                updatedFoodName, updatedCalories, updatedFoodTime, addedUser.id)
            Assert.assertEquals(204, updatedFoodResponse.status)

            //Assert that the individual fields were all updated as expected
            val retrievedFoodResponse = retrieveFoodByFoodId(addedFood.id)
            val updatedFood = jsonToObjectWithDate(retrievedFoodResponse, FoodDTO::class.java)
            Assert.assertEquals(updatedMealName, updatedFood.mealname)
            Assert.assertEquals(updatedFoodName, updatedFood.foodname)
            Assert.assertEquals(updatedCalories, updatedFood.calories)
            //TODO updatedFood object has current timestamp even though database has the right timestamp
            //TODO assertEquals(updateFoodTime, updatedFood.foodtime )

            //After - delete the user
            deleteUser(addedUser.id)
        }
    }

    @Nested
    inner class DeleteFoods {
        //   delete("/api/foods/:food-id", HealthTrackerAPI::deleteFoodByFoodId)
        //   delete("/api/users/:user-id/foods", HealthTrackerAPI::deleteFoodByUserId)
        @Test
        fun `deleting an food by food id when it doesn't exist, returns a 404 response`() {
            //Act & Assert - attempt to delete a user that doesn't exist
            Assert.assertEquals(404, deleteFoodByFoodId(-1).status)
        }

        @Test
        fun `deleting foods by user id when it doesn't exist, returns a 404 response`() {
            //Act & Assert - attempt to delete a user that doesn't exist
            Assert.assertEquals(404, deleteFoodsByUserId(-1).status)
        }

        @Test
        fun `deleting an food by id when it exists, returns a 204 response`() {

            //Arrange - add a user and an associated food that we plan to do a delete on
            val addedUser : UserDTO = jsonToObject(addUser(validName, validEmail).body.toString())
            val addFoodResponse = addFood(
                foods.get(0).mealname,
                foods.get(0).foodname, foods.get(0).calories, foods.get(0).foodtime, addedUser.id)
            Assert.assertEquals(201, addFoodResponse.status)

            //Act & Assert - delete the added food and assert a 204 is returned
            val addedFood = jsonToObjectWithDate(addFoodResponse, FoodDTO::class.java)
            Assert.assertEquals(204, deleteFoodByFoodId(addedFood.id).status)

            //After - delete the user
            deleteUser(addedUser.id)
        }

        @Test
        fun `deleting all foods by userid when it exists, returns a 204 response`() {

            //Arrange - add a user and 3 associated foods that we plan to do a cascade delete
            val addedUser : UserDTO = jsonToObject(addUser(validName, validEmail).body.toString())
            val addFoodResponse1 = addFood(
                foods.get(0).mealname, foods.get(0).foodname,
                foods.get(0).calories, foods.get(0).foodtime, addedUser.id)
            Assert.assertEquals(201, addFoodResponse1.status)
            val addFoodResponse2 = addFood(
                foods.get(1).mealname, foods.get(1).foodname,
                foods.get(1).calories, foods.get(1).foodtime, addedUser.id)
            Assert.assertEquals(201, addFoodResponse2.status)
            val addFoodResponse3 = addFood(
                foods.get(2).mealname, foods.get(2).foodname,
                foods.get(2).calories, foods.get(2).foodtime, addedUser.id)
            Assert.assertEquals(201, addFoodResponse3.status)

            //Act & Assert - delete the added user and assert a 204 is returned
            Assert.assertEquals(204, deleteUser(addedUser.id).status)

            //Act & Assert - attempt to retrieve the deleted foods
            val addedFood1 = jsonToObjectWithDate(addFoodResponse1, FoodDTO::class.java)
            val addedFood2 = jsonToObjectWithDate(addFoodResponse2, FoodDTO::class.java)
            val addedFood3 = jsonToObjectWithDate(addFoodResponse3, FoodDTO::class.java)
            Assert.assertEquals(404, retrieveFoodByFoodId(addedFood1.id).status)
            Assert.assertEquals(404, retrieveFoodByFoodId(addedFood2.id).status)
            Assert.assertEquals(404, retrieveFoodByFoodId(addedFood3.id).status)
        }
    }

}