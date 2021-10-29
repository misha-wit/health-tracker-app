package org.wit.repository

import junit.framework.Assert.assertEquals
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.wit.db.Foods
import org.wit.domain.FoodDTO
import org.wit.helpers.foods
import org.wit.helpers.populateFoodTable
import org.wit.helpers.populateUserTable

//retrieving some test data from Fixtures
val food1 = foods[0]
val food2 = foods.get(1)
val food3 = foods.get(2)


class FoodDAOTest {

    companion object {

        //Make a connection to a local, in memory H2 database.
        @BeforeAll
        @JvmStatic
        internal fun setupInMemoryDatabaseConnection() {
            Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver", user = "root", password = "")
        }
    }

    @Nested
    inner class CreateFoods {

        @Test
        fun `multiple foods added to table can be retrieved successfully`() {
            transaction {
                //Arrange - create and populate tables with three users and three Foods
                val userDAO = populateUserTable()
                val foodDAO = populateFoodTable()
                //Act & Assert
                assertEquals(3, foodDAO.getAll().size)
                assertEquals(food1, foodDAO.findByFoodId(food1.id))
                assertEquals(food2, foodDAO.findByFoodId(food2.id))
                assertEquals(food3, foodDAO.findByFoodId(food3.id))
            }
        }
    }

    @Nested
    inner class ReadFoods {

        @Test
        fun `getting all activites from a populated table returns all rows`() {
            transaction {
                //Arrange - create and populate tables with three users and three Foods
                val userDAO = populateUserTable()
                val foodDAO = populateFoodTable()
                //Act & Assert
                assertEquals(3, foodDAO.getAll().size)
            }
        }

        @Test
        fun `get food by user id that has no Foods, results in no record returned`() {
            transaction {
                //Arrange - create and populate tables with three users and three Foods
                val userDAO = populateUserTable()
                val foodDAO = populateFoodTable()
                //Act & Assert
                assertEquals(0, foodDAO.findByUserId(3).size)
            }
        }

        @Test
        fun `get food by user id that exists, results in a correct activitie(s) returned`() {
            transaction {
                //Arrange - create and populate tables with three users and three Foods
                val userDAO = populateUserTable()
                val foodDAO = populateFoodTable()
                //Act & Assert
                assertEquals(food1, foodDAO.findByUserId(1).get(0))
                assertEquals(food2, foodDAO.findByUserId(1).get(1))
                assertEquals(food3, foodDAO.findByUserId(2).get(0))
            }
        }

        @Test
        fun `get all Foods over empty table returns none`() {
            transaction {

                //Arrange - create and setup foodDAO object
                SchemaUtils.create(Foods)
                val foodDAO = FoodDAO()

                //Act & Assert
                assertEquals(0, foodDAO.getAll().size)
            }
        }

        @Test
        fun `get food by food id that has no records, results in no record returned`() {
            transaction {
                //Arrange - create and populate tables with three users and three Foods
                val userDAO = populateUserTable()
                val foodDAO = populateFoodTable()
                //Act & Assert
                assertEquals(null, foodDAO.findByFoodId(4))
            }
        }

        @Test
        fun `get food by food id that exists, results in a correct food returned`() {
            transaction {
                //Arrange - create and populate tables with three users and three Foods
                val userDAO = populateUserTable()
                val foodDAO = populateFoodTable()
                //Act & Assert
                assertEquals(food1, foodDAO.findByFoodId(1))
                assertEquals(food2, foodDAO.findByFoodId(2))
                assertEquals(food3, foodDAO.findByFoodId(3))
            }
        }
    }

    @Nested
    inner class UpdateFoods {

        @Test
        fun `updating existing food in table results in successful update`() {
            transaction {

                //Arrange - create and populate tables with three users and three Foods
                val userDAO = populateUserTable()
                val foodDAO = populateFoodTable()

                //Act & Assert
                val food3updated = FoodDTO(id = 3, mealname = "BreakFast", foodname = "Idly and curry",
                    calories = 220, foodtime = DateTime.now(), userId = 2)
                foodDAO.updateByFoodId(food3updated.id, food3updated)
                assertEquals(food3updated, foodDAO.findByFoodId(3))
            }
        }

        @Test
        fun `updating non-existant food in table results in no updates`() {
            transaction {

                //Arrange - create and populate tables with three users and three Foods
                val userDAO = populateUserTable()
                val foodDAO = populateFoodTable()

                //Act & Assert
                val food4updated = FoodDTO(id = 4, mealname = "Lunch", foodname = "Porotta and Chicken",
                    calories = 420, foodtime = DateTime.now(), userId = 2)
                foodDAO.updateByFoodId(4, food4updated)
                assertEquals(null, foodDAO.findByFoodId(4))
                assertEquals(3, foodDAO.getAll().size)
            }
        }
    }

    @Nested
    inner class DeleteFoods {

        @Test
        fun `deleting a non-existant food (by id) in table results in no deletion`() {
            transaction {

                //Arrange - create and populate tables with three users and three Foods
                val userDAO = populateUserTable()
                val foodDAO = populateFoodTable()

                //Act & Assert
                assertEquals(3, foodDAO.getAll().size)
                foodDAO.deleteByFoodId(4)
                assertEquals(3, foodDAO.getAll().size)
            }
        }

        @Test
        fun `deleting an existing food (by id) in table results in record being deleted`() {
            transaction {

                //Arrange - create and populate tables with three users and three Foods
                val userDAO = populateUserTable()
                val foodDAO = populateFoodTable()

                //Act & Assert
                assertEquals(3, foodDAO.getAll().size)
                foodDAO.deleteByFoodId(food3.id)
                assertEquals(2, foodDAO.getAll().size)
            }
        }


        @Test
        fun `deleting Foods when none exist for user id results in no deletion`() {
            transaction {

                //Arrange - create and populate tables with three users and three Foods
                val userDAO = populateUserTable()
                val foodDAO = populateFoodTable()
                assertEquals(3, foodDAO.getAll().size)

                //Act
                foodDAO.deleteByUserId(3)

                //Assert
                assertEquals(3, foodDAO.getAll().size)
            }
        }

        @Test
        fun `deleting Foods when 1 or more exist for user id results in deletion`() {
            transaction {

                //Arrange - create and populate tables with three users and three Foods
                val userDAO = populateUserTable()
                val foodDAO = populateFoodTable()

                //Act & Assert
                assertEquals(3, foodDAO.getAll().size)
                foodDAO.deleteByUserId(1)
                assertEquals(1, foodDAO.getAll().size)
            }
        }
    }


}