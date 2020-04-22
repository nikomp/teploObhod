package ru.bingosoft.teploObhod.db.CheckupGuide

import androidx.room.*
import io.reactivex.Flowable

@Dao
interface CheckupGuideDao {
    @Query("SELECT * FROM CheckupGuide")
    fun getAll(): Flowable<List<CheckupGuide>>

    @Query("SELECT * FROM CheckupGuide WHERE id = :id")
    fun getById(id: Long): Flowable<CheckupGuide>

    @Query("DELETE FROM CheckupGuide")
    fun clearCheckupGuide()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(guide: CheckupGuide)

    @Update
    fun update(guide: CheckupGuide)

    @Delete
    fun delete(guide: CheckupGuide)
}