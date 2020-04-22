package ru.bingosoft.teploObhod.ui.mainactivity

import com.yandex.mapkit.geometry.Point
import ru.bingosoft.teploObhod.db.Checkup.Checkup
import ru.bingosoft.teploObhod.db.RouteList.RouteList

interface FragmentsContractActivity {
    fun setCheckup(checkup: Checkup)
    fun setChecupListOrder(order: RouteList)
    fun setCoordinates(point: Point, controlId: Int)
}