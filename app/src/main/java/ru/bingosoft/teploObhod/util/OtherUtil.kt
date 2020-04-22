package ru.bingosoft.teploObhod.util

import android.location.Location
import androidx.exifinterface.media.ExifInterface
import com.google.gson.Gson
import ru.bingosoft.teploObhod.db.Checkup.Checkup
import ru.bingosoft.teploObhod.models.Models
import timber.log.Timber
import java.io.IOException
import kotlin.math.roundToInt

class OtherUtil {
    fun getDirForSync(checkups: List<Checkup>): List<String> {
        Timber.d("getDirForSync")
        val list = mutableListOf<String>()
        checkups.forEach { it ->
            val controlList = Gson().fromJson(it.textResult, Models.ControlList::class.java)
            val controlPhoto = controlList.list.filter { it.type == "photo" }
            controlPhoto.forEach {
                //val photoResult= Gson().fromJson(it.resvalue, Models.PhotoResult::class.java)
                list.add(it.resvalue)
            }
        }

        return list
    }

    fun saveExifLocation(filename: String, photoLocation: Location?) {
        try {
            Timber.d("Exif")
            val exif = ExifInterface(filename)
            //Timber.d(convertLat(photoLocation!!.latitude))

            val num1Lat = photoLocation!!.latitude.roundToInt()
            val num2Lat = ((photoLocation.latitude - num1Lat) * 60).roundToInt()
            val num3Lat = (photoLocation.latitude - (num1Lat + num2Lat / 60))

            val num1Lon = photoLocation.longitude.roundToInt()
            val num2Lon = ((photoLocation.longitude - num1Lon) * 60).roundToInt()
            val num3Lon = (photoLocation.longitude - (num1Lon + num2Lon / 60))

            exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, "$num1Lat/1,$num2Lat/1,$num3Lat/1000")
            exif.setAttribute(
                ExifInterface.TAG_GPS_LONGITUDE,
                "$num1Lon/1,$num2Lon/1,$num3Lon/1000"
            )

            if (photoLocation.latitude > 0) {
                exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, "N")
            } else {
                exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, "S")
            }

            if (photoLocation.longitude > 0) {
                exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, "E")
            } else {
                exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, "W")
            }

            exif.saveAttributes()
            Timber.d("Exif_saved")
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}