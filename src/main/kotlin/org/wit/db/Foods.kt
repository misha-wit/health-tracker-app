package org.wit.db

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

// SRP - Responsibility is to manage one food item.
//       Database wise, this is the table object.

object Foods : Table("foods") {
    val id = integer("id").autoIncrement().primaryKey()
    val mealname = varchar("mealname", 100)
    val foodname = varchar("foodname", 100)
    var calories = integer("calories")
    val foodtime = datetime("foodtime")
    val userId = integer("user_id").references(Users.id, onDelete = ReferenceOption.CASCADE)
}


