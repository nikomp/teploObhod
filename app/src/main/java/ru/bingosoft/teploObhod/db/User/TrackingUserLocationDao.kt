package ru.bingosoft.teploObhod.db.User

import androidx.room.*
import io.reactivex.Flowable

@Dao
interface TrackingUserLocationDao {
    @Query("SELECT * FROM TrackingUserLocation")
    fun getAll(): Flowable<List<TrackingUserLocation>>

    @Query("SELECT count(*) FROM TrackingUserLocation")
    fun getSize(): Int

    @Query("DELETE FROM TrackingUserLocation")
    fun clear()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(userLocation: TrackingUserLocation)

    @Update
    fun update(userLocation: TrackingUserLocation)

    @Delete
    fun delete(userLocation: TrackingUserLocation)
}