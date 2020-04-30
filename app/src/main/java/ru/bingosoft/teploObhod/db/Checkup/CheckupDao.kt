package ru.bingosoft.teploObhod.db.Checkup

import androidx.room.*
import io.reactivex.Flowable

@Dao
interface CheckupDao {
    @Query("SELECT * FROM checkup")
    fun getAll(): Flowable<List<Checkup>>

    @Query("SELECT * FROM checkup WHERE id = :id")
    fun getById(id: Long): Flowable<Checkup>

    /*@Query("SELECT * FROM checkup WHERE idOrder = :id")
    fun getCheckupsOrder(id: Long): Flowable<List<Checkup>>*/

    @Query("SELECT id FROM checkup WHERE idQr = :id")
    fun getCheckupIdByOrder(id: Long): Long

    @Query("SELECT * FROM checkup where textResult is not null and sync=0 and textResult not like '%\"checked\":false%'")
    fun getResultAll(): Flowable<List<Checkup>>

    @Query("SELECT count(*) FROM checkup where textResult is not null and sync=0")
    fun existCheckupWithResult(): Int

    @Query("DELETE FROM checkup")
    fun clearCheckup()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(checkup: Checkup)

    @Update
    fun update(checkup: Checkup)

    @Delete
    fun delete(checkup: Checkup)
}