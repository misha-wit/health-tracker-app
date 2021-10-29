package org.wit.db

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

// SRP - Responsibility is to manage one measurement item.
//       Database wise, this is the table object.

object Measurements : Table("measurements") {
    val id = integer("id").autoIncrement().primaryKey()
    val weight = integer("weight")
    val height = integer("height")
    val addedon = datetime("addedon")
    val userId = integer("user_id").references(Users.id, onDelete = ReferenceOption.CASCADE)
}
