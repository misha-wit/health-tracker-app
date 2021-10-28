package org.wit.domain
import org.joda.time.DateTime
data class FoodDTO (var id: Int,
                        var mealname:String,
                        var foodname: String,
                        var calories: Int,
                        var foodtime: DateTime,
                        var userId: Int)