package ru.bingosoft.teploObhod.db.User

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import ru.bingosoft.teploObhod.util.DateConverter
import java.util.*

@Entity(tableName = "TrackingUserLocation")
@TypeConverters(DateConverter::class)
data class TrackingUserLocation(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var lat: Double?,
    var lon: Double?,
    var provider: String = "",
    var status: String = "",
    var dateLocation: Date? = null
)