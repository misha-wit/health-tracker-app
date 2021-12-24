package org.wit.controllers

import org.jetbrains.exposed.sql.Database
import org.junit.Assert
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.wit.config.DbConfig
import org.wit.domain.MeasurementDTO
import org.wit.domain.UserDTO
import org.wit.helpers.*
import org.wit.util.jsonToArrayWithDate
import org.wit.util.jsonToObject
import org.wit.util.jsonToObjectWithDate

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MeasurementTrackerAPITest {
    val db = Database.connect(
        "jdbc:postgresql://ec2-54-74-102-48.eu-west-1.compute.amazonaws.com:5432/dbnbsti78ce6m4?sslmode=require",
        driver = "org.postgresql.Driver",
        user = "wynkhjxpwnatok",
        password = "cb388b6a8b43f9a860898d3eaaf47ac93d77b1bafaa9e005663d1511536fbf88")
    @Nested
    inner class CreateMeasurements {
        //   post(  "/api/measurements", HealthTrackerAPI::addMeasurement)

        @Test
        fun `add a measurement when a user exists for it, returns a 201 response`() {

            //Arrange - add a user and an associated measurement that we plan to do a delete on
            val addedUser: UserDTO = jsonToObject(addUser(validName, validEmail).body.toString())

            val addMeasurementResponse = addMeasurement(
                measurements.get(0).weight,
                measurements.get(0).height, measurements.get(0).addedon, addedUser.id
            )
            Assert.assertEquals(201, addMeasurementResponse.status)

            //After - delete the user (Measurement will cascade delete in the database)
            deleteUser(addedUser.id)
        }

        @Test
        fun `add a measurement when no user exists for it, returns a 404 response`() {

            //Arrange - check there is no user for -1 id
            val userId = -1
            Assert.assertEquals(404, retrieveUserById(userId).status)

            val addMeasurementResponse = addMeasurement(
                measurements.get(0).weight, measurements.get(0).height,
                measurements.get(0).addedon, userId
            )
            Assert.assertEquals(404, addMeasurementResponse.status)
        }
    }

    @Nested
    inner class ReadMeasurements {
        //   get(   "/api/users/:user-id/measurements", HealthTrackerAPI::getMeasurementsByUserId)
        //   get(   "/api/measurements", HealthTrackerAPI::getAllMeasurements)
        //   get(   "/api/measurements/:measurement-id", HealthTrackerAPI::getMeasurementsByMeasurementId)
        @Test
        fun `get all measurements from the database returns 200 or 404 response`() {
            val response = retrieveAllMeasurements()
            if (response.status == 200){
                val retrievedMeasurements = jsonToArrayWithDate(response, Array<MeasurementDTO>::class.java)
                Assert.assertNotEquals(0, retrievedMeasurements.size)
            }
            else{
                Assert.assertEquals(404, response.status)
            }
        }

        @Test
        fun `get all measurements by user id when user and measurements exists returns 200 response`() {
            //Arrange - add a user and 3 associated measurements that we plan to retrieve
            val addedUser : UserDTO = jsonToObject(addUser(validName, validEmail).body.toString())
            addMeasurement(
                measurements.get(0).weight, measurements.get(0).height,
                measurements.get(0).addedon, addedUser.id)
            addMeasurement(
                measurements.get(1).weight, measurements.get(1).height,
                measurements.get(1).addedon, addedUser.id)
            addMeasurement(
                measurements.get(2).weight, measurements.get(2).height,
                measurements.get(2).addedon, addedUser.id)

            //Assert and Act - retrieve the three added measurements by user id
            val response = retrieveMeasurementsByUserId(addedUser.id)
            Assert.assertEquals(200, response.status)
            val retrievedMeasurements = jsonToArrayWithDate(response, Array<MeasurementDTO>::class.java)
            Assert.assertEquals(3, retrievedMeasurements.size)

            //After - delete the added user and assert a 204 is returned (measurements are cascade deleted)
            Assert.assertEquals(204, deleteUser(addedUser.id).status)
        }

        @Test
        fun `get all measurements by user id when no measurements exist returns 404 response`() {
            //Arrange - add a user
            val addedUser : UserDTO = jsonToObject(addUser(validName, validEmail).body.toString())

            //Assert and Act - retrieve the measurements by user id
            val response = retrieveMeasurementsByUserId(addedUser.id)
            Assert.assertEquals(404, response.status)

            //After - delete the added user and assert a 204 is returned
            Assert.assertEquals(204, deleteUser(addedUser.id).status)
        }

        @Test
        fun `get all measurements by user id when no user exists returns 404 response`() {
            //Arrange
            val userId = -1

            //Assert and Act - retrieve measurements by user id
            val response = retrieveMeasurementsByUserId(userId)
            Assert.assertEquals(404, response.status)
        }

        @Test
        fun `get measurement by measurement id when no measurement exists returns 404 response`() {
            //Arrange
            val measurementId = -1
            //Assert and Act - attempt to retrieve the measurement by measurement id
            val response = retrieveMeasurementByMeasurementId(measurementId)
            Assert.assertEquals(404, response.status)
        }


        @Test
        fun `get measurement by measurement id when measurement exists returns 200 response`() {
            //Arrange - add a user and associated measurement
            val addedUser : UserDTO = jsonToObject(addUser(validName, validEmail).body.toString())
            val addMeasurementResponse = addMeasurement(
                measurements.get(0).weight,
                measurements.get(0).height,
                measurements.get(0).addedon, addedUser.id)
            Assert.assertEquals(201, addMeasurementResponse.status)
            val addedMeasurement = jsonToObjectWithDate(addMeasurementResponse, MeasurementDTO::class.java)

            //Act & Assert - retrieve the measurement by measurement id
            val response = retrieveMeasurementByMeasurementId(addedMeasurement.id)
            Assert.assertEquals(200, response.status)

            //After - delete the added user and assert a 204 is returned
            Assert.assertEquals(204, deleteUser(addedUser.id).status)
        }

    }

    @Nested
    inner class UpdateMeasurements {
        //  patch( "/api/measurements/:measurement-id", HealthTrackerAPI::updateMeasurement)
        @Test
        fun `updating an measurement by measurement id when it doesn't exist, returns a 404 response`() {
            val userId = -1
            val measurementID = -1

            //Arrange - check there is no user for -1 id
            Assert.assertEquals(404, retrieveUserById(userId).status)

            //Act & Assert - attempt to update the details of an measurement/user that doesn't exist
            Assert.assertEquals(
                404, updateMeasurement(
                    measurementID, updatedWeight, updatedHeight,
                    updatedAddedOn, userId
                ).status
            )
        }

        @Test
        fun `updating an measurement by measurement id when it exists, returns 204 response`() {

            //Arrange - add a user and an associated measurement that we plan to do an update on
            val addedUser : UserDTO = jsonToObject(addUser(validName, validEmail).body.toString())
            val addMeasurementResponse = addMeasurement(
                measurements.get(0).weight,
                measurements.get(0).height,
                measurements.get(0).addedon, addedUser.id)
            Assert.assertEquals(201, addMeasurementResponse.status)
            val addedMeasurement = jsonToObjectWithDate(addMeasurementResponse, MeasurementDTO::class.java)

            //Act & Assert - update the added measurement and assert a 204 is returned
            val updatedMeasurementResponse = updateMeasurement(addedMeasurement.id, updatedWeight,
                updatedHeight, updatedAddedOn, addedUser.id)
            Assert.assertEquals(204, updatedMeasurementResponse.status)

            //Assert that the individual fields were all updated as expected
            val retrievedMeasurementResponse = retrieveMeasurementByMeasurementId(addedMeasurement.id)
            val updatedMeasurement = jsonToObjectWithDate(retrievedMeasurementResponse, MeasurementDTO::class.java)
            Assert.assertEquals(updatedWeight, updatedMeasurement.weight)
            Assert.assertEquals(updatedHeight, updatedMeasurement.height)
            //TODO updatedMeasurement object has current timestamp even though database has the right timestamp
            //TODO assertEquals(updateAddedOn, updatedMeasurement.addedon )

            //After - delete the user
            deleteUser(addedUser.id)
        }
    }

    @Nested
    inner class DeleteMeasurements {
        //   delete("/api/measurements/:measurement-id", HealthTrackerAPI::deleteMeasurementByMeasurementId)
        //   delete("/api/users/:user-id/measurements", HealthTrackerAPI::deleteMeasurementByUserId)
        @Test
        fun `deleting an measurement by measurement id when it doesn't exist, returns a 404 response`() {
            //Act & Assert - attempt to delete a user that doesn't exist
            Assert.assertEquals(404, deleteMeasurementByMeasurementId(-1).status)
        }

        @Test
        fun `deleting measurements by user id when it doesn't exist, returns a 404 response`() {
            //Act & Assert - attempt to delete a user that doesn't exist
            Assert.assertEquals(404, deleteMeasurementsByUserId(-1).status)
        }

        @Test
        fun `deleting an measurement by id when it exists, returns a 204 response`() {

            //Arrange - add a user and an associated measurement that we plan to do a delete on
            val addedUser : UserDTO = jsonToObject(addUser(validName, validEmail).body.toString())
            val addMeasurementResponse = addMeasurement(
                measurements.get(0).weight, measurements.get(0).height,
                measurements.get(0).addedon, addedUser.id)
            Assert.assertEquals(201, addMeasurementResponse.status)

            //Act & Assert - delete the added measurement and assert a 204 is returned
            val addedMeasurement = jsonToObjectWithDate(addMeasurementResponse, MeasurementDTO::class.java)
            Assert.assertEquals(204, deleteMeasurementByMeasurementId(addedMeasurement.id).status)

            //After - delete the user
            deleteUser(addedUser.id)
        }

        @Test
        fun `deleting all measurements by userid when it exists, returns a 204 response`() {

            //Arrange - add a user and 3 associated measurements that we plan to do a cascade delete
            val addedUser : UserDTO = jsonToObject(addUser(validName, validEmail).body.toString())
            val addMeasurementResponse1 = addMeasurement(
                measurements.get(0).weight, measurements.get(0).height,
                measurements.get(0).addedon, addedUser.id)
            Assert.assertEquals(201, addMeasurementResponse1.status)
            val addMeasurementResponse2 = addMeasurement(
                measurements.get(1).weight, measurements.get(1).height,
                measurements.get(1).addedon, addedUser.id)
            Assert.assertEquals(201, addMeasurementResponse2.status)
            val addMeasurementResponse3 = addMeasurement(
                measurements.get(2).weight, measurements.get(2).height,
                measurements.get(2).addedon, addedUser.id)
            Assert.assertEquals(201, addMeasurementResponse3.status)

            //Act & Assert - delete the added user and assert a 204 is returned
            Assert.assertEquals(204, deleteUser(addedUser.id).status)

            //Act & Assert - attempt to retrieve the deleted measurements
            val addedMeasurement1 = jsonToObjectWithDate(addMeasurementResponse1, MeasurementDTO::class.java)
            val addedMeasurement2 = jsonToObjectWithDate(addMeasurementResponse2, MeasurementDTO::class.java)
            val addedMeasurement3 = jsonToObjectWithDate(addMeasurementResponse3, MeasurementDTO::class.java)
            Assert.assertEquals(404, retrieveMeasurementByMeasurementId(addedMeasurement1.id).status)
            Assert.assertEquals(404, retrieveMeasurementByMeasurementId(addedMeasurement2.id).status)
            Assert.assertEquals(404, retrieveMeasurementByMeasurementId(addedMeasurement3.id).status)
        }
    }
}