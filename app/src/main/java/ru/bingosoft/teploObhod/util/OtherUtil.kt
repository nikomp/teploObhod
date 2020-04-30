package ru.bingosoft.teploObhod.util

import com.google.gson.Gson
import ru.bingosoft.teploObhod.db.Checkup.Checkup
import ru.bingosoft.teploObhod.models.Models
import timber.log.Timber
import java.io.File

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

    fun getFilesFromDir(dir: String): List<String> {
        Timber.d("getFilesFromDir")
        val list = mutableListOf<String>()
        val directory = File(dir)
        val files = directory.listFiles()
        files.forEach {
            list.add("$dir/${it.name}")
        }
        return list
    }
}