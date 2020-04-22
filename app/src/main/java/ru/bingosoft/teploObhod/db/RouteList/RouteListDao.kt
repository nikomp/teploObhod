package ru.bingosoft.teploObhod.db.RouteList

import androidx.room.*
import io.reactivex.Flowable


@Dao
interface RouteListDao {
    @Query("SELECT * FROM routelist order by dateRoute")
    fun getAll(): Flowable<List<RouteList>>

    @Query("SELECT * FROM routelist WHERE id = :id")
    fun getById(id: Long): RouteList

    @Query("SELECT * FROM routelist WHERE route = :route")
    fun getByRoute(route: Long): Flowable<RouteList>

    @Query("SELECT count(*) FROM routelist")
    fun getSize(): Int

    @Query("DELETE FROM routelist")
    fun clearOrders()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(routeList: RouteList)

    @Update
    fun update(routeList: RouteList)

    @Delete
    fun delete(routeList: RouteList)
}