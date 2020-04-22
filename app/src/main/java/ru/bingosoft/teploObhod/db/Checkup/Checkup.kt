package ru.bingosoft.teploObhod.db.Checkup

import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import com.google.gson.JsonObject
import ru.bingosoft.teploObhod.db.RouteList.RouteList
import ru.bingosoft.teploObhod.util.JsonConverter

@Entity(
    tableName = "Checkup",
    foreignKeys = arrayOf(
        ForeignKey(
            entity = RouteList::class,
            parentColumns = ["id"],
            childColumns = ["idOrder"],
            onDelete = CASCADE
        )
    ),
    indices = [Index("idOrder")]
)
@TypeConverters(JsonConverter::class)
data class Checkup(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var guid: String,
    var kindObject: String,
    var nameObject: String,
    var text: JsonObject? = null,
    var idOrder: Long? = null,
    var textResult: JsonObject? = null,
    var sync: Boolean = false
)
