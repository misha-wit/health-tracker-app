package org.wit.repository

import junit.framework.Assert.assertEquals
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.wit.db.Measurements
import org.wit.domain.MeasurementDTO
import org.wit.helpers.measurements
import org.wit.helpers.populateMeasurementTable
import org.wit.helpers.populateUserTable

//retrieving some test data from Fixtures
val measurement1 = measurements[0]
val measurement2 = measurements.get(1)
val measurement3 = measurements.get(2)


class MeasurementDAOTest {

    companion object {

        //Make a connection to a local, in memory H2 database.
        @BeforeAll
        @JvmStatic
        internal fun setupInMemoryDatabaseConnection() {
            Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver", user = "root", password = "")
        }
    }

    @Nested
    inner class CreateMeasurements {

        @Test
        fun `multiple measurements added to table can be retrieved successfully`() {
            transaction {
                //Arrange - create and populate tables with three users and three measurements
                val userDAO = populateUserTable()
                val measurementDAO = populateMeasurementTable()
                //Act & Assert
                assertEquals(3, measurementDAO.getAll().size)
                assertEquals(measurement1, measurementDAO.findByMeasurementId(measurement1.id))
                assertEquals(measurement2, measurementDAO.findByMeasurementId(measurement2.id))
                assertEquals(measurement3, measurementDAO.findByMeasurementId(measurement3.id))
            }
        }
    }

    @Nested
    inner class ReadMeasurements {

        @Test
        fun `getting all activites from a populated table returns all rows`() {
            transaction {
                //Arrange - create and populate tables with three users and three Measurements
                val userDAO = populateUserTable()
                val measurementDAO = populateMeasurementTable()
                //Act & Assert
                assertEquals(3, measurementDAO.getAll().size)
            }
        }

        @Test
        fun `get measurement by user id that has no Measurements, results in no record returned`() {
            transaction {
                //Arrange - create and populate tables with three users and three Measurements
                val userDAO = populateUserTable()
                val measurementDAO = populateMeasurementTable()
                //Act & Assert
                assertEquals(0, measurementDAO.findByUserId(3).size)
            }
        }

        @Test
        fun `get measurement by user id that exists, results in a correct activitie(s) returned`() {
            transaction {
                //Arrange - create and populate tables with three users and three Measurements
                val userDAO = populateUserTable()
                val measurementDAO = populateMeasurementTable()
                //Act & Assert
                assertEquals(measurement1, measurementDAO.findByUserId(1).get(0))
                assertEquals(measurement2, measurementDAO.findByUserId(1).get(1))
                assertEquals(measurement3, measurementDAO.findByUserId(2).get(0))
            }
        }

        @Test
        fun `get all Measurements over empty table returns none`() {
            transaction {

                //Arrange - create and setup measurementDAO object
                SchemaUtils.create(Measurements)
                val measurementDAO = MeasurementDAO()

                //Act & Assert
                assertEquals(0, measurementDAO.getAll().size)
            }
        }

        @Test
        fun `get measurement by measurement id that has no records, results in no record returned`() {
            transaction {
                //Arrange - create and populate tables with three users and three measurements
                val userDAO = populateUserTable()
                val measurementDAO = populateMeasurementTable()
                //Act & Assert
                assertEquals(null, measurementDAO.findByMeasurementId(4))
            }
        }

        @Test
        fun `get measurement by measurement id that exists, results in a correct measurement returned`() {
            transaction {
                //Arrange - create and populate tables with three users and three Measurements
                val userDAO = populateUserTable()
                val measurementDAO = populateMeasurementTable()
                //Act & Assert
                assertEquals(measurement1, measurementDAO.findByMeasurementId(1))
                assertEquals(measurement2, measurementDAO.findByMeasurementId(2))
                assertEquals(measurement3, measurementDAO.findByMeasurementId(3))
            }
        }
    }

    @Nested
    inner class UpdateMeasurements {

        @Test
        fun `updating existing measurement in table results in successful update`() {
            transaction {

                //Arrange - create and populate tables with three users and three measurements
                val userDAO = populateUserTable()
                val measurementDAO = populateMeasurementTable()

                //Act & Assert
                val measurement3updated = MeasurementDTO(id = 3, weight = 32, height = 130, addedon = DateTime.now(), userId = 2)
                measurementDAO.updateByMeasurementId(measurement3updated.id, measurement3updated)
                assertEquals(measurement3updated, measurementDAO.findByMeasurementId(3))
            }
        }

        @Test
        fun `updating non-existant measurement in table results in no updates`() {
            transaction {

                //Arrange - create and populate tables with three users and three Measurements
                val userDAO = populateUserTable()
                val measurementDAO = populateMeasurementTable()

                //Act & Assert
                val measurement4updated = MeasurementDTO(id = 3, weight = 32, height = 130, addedon = DateTime.now(), userId = 2)
                measurementDAO.updateByMeasurementId(4, measurement4updated)
                assertEquals(null, measurementDAO.findByMeasurementId(4))
                assertEquals(3, measurementDAO.getAll().size)
            }
        }
    }

    @Nested
    inner class DeleteMeasurements {

        @Test
        fun `deleting a non-existant measurement (by id) in table results in no deletion`() {
            transaction {

                //Arrange - create and populate tables with three users and three Measurements
                val userDAO = populateUserTable()
                val measurementDAO = populateMeasurementTable()

                //Act & Assert
                assertEquals(3, measurementDAO.getAll().size)
                measurementDAO.deleteByMeasurementId(4)
                assertEquals(3, measurementDAO.getAll().size)
            }
        }

        @Test
        fun `deleting an existing measurement (by id) in table results in record being deleted`() {
            transaction {

                //Arrange - create and populate tables with three users and three Measurements
                val userDAO = populateUserTable()
                val measurementDAO = populateMeasurementTable()

                //Act & Assert
                assertEquals(3, measurementDAO.getAll().size)
                measurementDAO.deleteByMeasurementId(measurement3.id)
                assertEquals(2, measurementDAO.getAll().size)
            }
        }


        @Test
        fun `deleting Measurements when none exist for user id results in no deletion`() {
            transaction {

                //Arrange - create and populate tables with three users and three measurements
                val userDAO = populateUserTable()
                val measurementDAO = populateMeasurementTable()
                assertEquals(3, measurementDAO.getAll().size)

                //Act
                measurementDAO.deleteByUserId(3)

                //Assert
                assertEquals(3, measurementDAO.getAll().size)
            }
        }

        @Test
        fun `deleting measurements when 1 or more exist for user id results in deletion`() {
            transaction {

                //Arrange - create and populate tables with three users and three measurements
                val userDAO = populateUserTable()
                val measurementDAO = populateMeasurementTable()

                //Act & Assert
                assertEquals(3, measurementDAO.getAll().size)
                measurementDAO.deleteByUserId(1)
                assertEquals(1, measurementDAO.getAll().size)
            }
        }
    }


}