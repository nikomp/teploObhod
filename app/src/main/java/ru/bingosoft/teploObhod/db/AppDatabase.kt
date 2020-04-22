package ru.bingosoft.teploObhod.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.bingosoft.teploObhod.db.Checkup.Checkup
import ru.bingosoft.teploObhod.db.Checkup.CheckupDao
import ru.bingosoft.teploObhod.db.CheckupGuide.CheckupGuide
import ru.bingosoft.teploObhod.db.CheckupGuide.CheckupGuideDao
import ru.bingosoft.teploObhod.db.RouteList.RouteList
import ru.bingosoft.teploObhod.db.RouteList.RouteListDao
import ru.bingosoft.teploObhod.db.User.TrackingUserLocation
import ru.bingosoft.teploObhod.db.User.TrackingUserLocationDao


@Database(
    entities = arrayOf(
        RouteList::class,
        Checkup::class,
        CheckupGuide::class,
        TrackingUserLocation::class
    ), version = 1, exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun routeListDao(): RouteListDao
    abstract fun checkupDao(): CheckupDao
    abstract fun checkupGuideDao(): CheckupGuideDao
    abstract fun trackingUserDao(): TrackingUserLocationDao
}