package org.wit.config
import mu.KotlinLogging
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.name
import java.net.URI
class DbConfig{

    private val logger = KotlinLogging.logger {}

    //NOTE: you need the ?sslmode=require otherwise you get an error complaining about the ssl certificate

    fun getDbConnection() :Database{
        logger.info{"Starting DB Connection..."}
        val dbConfig = Database.connect("jdbc:postgresql://ec2-54-172-169-87.compute-1.amazonaws.com:5432/d75u9glj5k4v13?sslmode=require",
            driver = "org.postgresql.Driver",
            user = "ssbrrxzccwlwpd",
            password ="39966b431ff8422ea4aff334461b9972bc36039cb241f169029ab14f792ed5f6")
        logger.info{"DbConfig name = " + dbConfig.name}
        logger.info{"DbConfig url = " + dbConfig.url}
        return dbConfig
    }

    /*   fun getDbConnection() :Database{
           logger.info{"Starting DB Connection..."}
           val databaseURL = URI(System.getenv("DATABASE_URL"))
           val dbConfig = Database.connect(
               "jdbc:postgresql://" + databaseURL.host + ":" + databaseURL.port + databaseURL.path + "?sslmode=require",
               driver = "org.postgresql.Driver",
               user = databaseURL.userInfo.split(":").toTypedArray()[0],
               password = databaseURL.userInfo.split(":").toTypedArray()[1])
           logger.info{"DbConfig name = " + dbConfig.name}
           logger.info{"DbConfig url = " + dbConfig.url}
           return dbConfig
       }*/

}