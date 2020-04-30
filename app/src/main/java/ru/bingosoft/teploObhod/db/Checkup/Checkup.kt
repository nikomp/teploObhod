package ru.bingosoft.teploObhod.db.Checkup

import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import com.google.gson.JsonObject
import ru.bingosoft.teploObhod.db.QRList.QRList
import ru.bingosoft.teploObhod.util.JsonConverter

@Entity(
    tableName = "Checkup",
    foreignKeys = arrayOf(
        ForeignKey(
            entity = QRList::class,
            parentColumns = ["id"],
            childColumns = ["idQr"],
            onDelete = CASCADE
        )
    ),
    indices = [Index("idQr")]
)
@TypeConverters(JsonConverter::class)
data class Checkup(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var guidQ: String,
    var guidQr: String,
    var guidM: String,
    var text: JsonObject? = null,
    var idQr: Long? = null,
    var textResult: JsonObject? = null,
    var sync: Boolean = false
)
