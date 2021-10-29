package org.wit.domain

import org.joda.time.DateTime

data class MeasurementDTO (var id: Int,
                           var weight:Int,
                           var height: Int,
                           var addedon: DateTime,
                           var userId: Int)