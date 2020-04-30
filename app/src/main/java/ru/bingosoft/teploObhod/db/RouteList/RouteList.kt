package ru.bingosoft.teploObhod.db.RouteList

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.SerializedName
import ru.bingosoft.teploObhod.util.DateConverter
import java.util.*

@Entity(tableName = "RouteList")
@TypeConverters(DateConverter::class)
data class RouteList(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    var id: Long = 0,
    @SerializedName("guid")
    var guid: String,
    @SerializedName("route")
    var route: Long = 0,
    @SerializedName("routeTemplate")
    var routeTemplate: Long = 0,
    @SerializedName("nameRouteTemplate")
    var nameRouteTemplate: String,
    @SerializedName("user")
    var user: Long = 0,
    @SerializedName("datetime")
    var dateRoute: Date? = null
)

