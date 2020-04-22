package ru.bingosoft.teploObhod.util

import com.yandex.mapkit.geometry.Point

class Const {
    object LogTags {
        const val LOGTAG = "myLogs"
        const val SPS = "sharedPrefSaver"
    }

    object RequestCodes {
        const val PHOTO = 1
        const val AUTH = 2
        const val PERMISSION = 123
        const val QR_SCAN = 11
    }

    object SharedPrefConst {
        const val APP_PREFERENCES = "AppSettings"
        const val LOGIN = "login"
        const val TOKEN = "token"
        const val PASSWORD = "password"
        const val SESSION = "session_id"
        const val DATESYNC = "last_sync_date"
        const val USER_FULLNAME = "fullname"
        const val USER_PHOTO_URL = "photo_url"
        const val FIREBASE_MESSAGE = "message_token"
        const val LOCATION_TRACKING = "location_tracking"
    }

    object LocationStatus {
        const val PROVIDER_DISABLED = "PROVIDER_DISABLED"
        const val PROVIDER_ENABLED = "PROVIDER_ENABLED"
        const val NOT_AVAILABLE = "NOT_AVAILABLE"
        const val AVAILABLE = "AVAILABLE"
    }

    object MessageCode {
        const val REFUSED_PERMISSION = 1 //пользователь отказался выдать разрешение на Геолокацию
        const val REPEATEDLY_REFUSED = 2 //пользователь повторно отказался включить GPS
        const val DISABLE_LOCATION = 3 //пользователь выключил GPS
    }

    object Location {
        val TARGET_POINT = Point(56.3287, 44.002) //Нижний Новгород

        const val ZOOM_LEVEL = 12.0f
        const val DESIRED_ACCURACY = 0.0
        const val MINIMAL_TIME = 0L
        const val MINIMAL_DISTANCE = 50.0
        const val USE_IN_BACKGROUND = false

    }

}