package ru.bingosoft.teploObhod.db.QRList

import androidx.room.*
import io.reactivex.Flowable

@Dao
interface QRListDao {
    @Query("SELECT * FROM qrlist")
    fun getAll(): Flowable<List<QRList>>

    @Query("SELECT * FROM qrlist WHERE id = :id")
    fun getById(id: Long): Flowable<QRList>

    @Query("SELECT * FROM qrlist WHERE routeTemplate = :route")
    fun getByRoute(route: Long): Flowable<List<QRList>>

    @Query("DELETE FROM qrlist")
    fun clearCheckupGuide()

    @Query("UPDATE qrlist SET answeredCount=:count WHERE id = :idQr")
    fun updateAnsweredCount(idQr: Long?, count: Int)

    @Query("UPDATE qrlist SET errorCount=:count WHERE id = :idQr")
    fun updateErrorCount(idQr: Long?, count: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(guide: QRList)

    @Update
    fun update(guide: QRList)

    @Delete
    fun delete(guide: QRList)
}