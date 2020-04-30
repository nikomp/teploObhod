package ru.bingosoft.teploObhod.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.bingosoft.teploObhod.db.Checkup.Checkup
import ru.bingosoft.teploObhod.db.Checkup.CheckupDao
import ru.bingosoft.teploObhod.db.QRList.QRList
import ru.bingosoft.teploObhod.db.QRList.QRListDao
import ru.bingosoft.teploObhod.db.RouteList.RouteList
import ru.bingosoft.teploObhod.db.RouteList.RouteListDao
import ru.bingosoft.teploObhod.db.User.TrackingUserLocation
import ru.bingosoft.teploObhod.db.User.TrackingUserLocationDao


@Database(
    entities = arrayOf(
        RouteList::class,
        Checkup::class,
        QRList::class,
        TrackingUserLocation::class
    ), version = 1, exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun routeListDao(): RouteListDao
    abstract fun checkupDao(): CheckupDao
    abstract fun qrListDao(): QRListDao
    abstract fun trackingUserDao(): TrackingUserLocationDao
}