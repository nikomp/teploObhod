package ru.bingosoft.teploObhod.util

import androidx.room.TypeConverter
import com.google.gson.JsonObject
import com.google.gson.JsonParser

class JsonConverter {
    @TypeConverter
    fun fromString(value: String?): JsonObject? {

        return if (value == null) null else JsonParser().parse(value).asJsonObject
    }

    @TypeConverter
    fun jsonToString(jsonObject: JsonObject?): String? {
        return jsonObject?.toString()
    }
}