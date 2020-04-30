package ru.bingosoft.teploObhod.db.QRList

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "QRList")
data class QRList(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var guid: String,
    var name: String,
    var location: String,
    var routeTemplate: Int = 0,
    var order: Int = 0,
    var questionCount: Int = 0,
    var answeredCount: Int = 0,
    var errorCount: Int = 0
)


