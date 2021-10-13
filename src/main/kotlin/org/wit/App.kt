package org.wit

import org.wit.config.JavalinConfig
import org.wit.config.DbConfig
fun main() {
    DbConfig().getDbConnection()
    JavalinConfig().startJavalinService()

}