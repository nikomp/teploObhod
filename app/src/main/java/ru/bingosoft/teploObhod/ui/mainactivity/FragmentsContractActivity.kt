package ru.bingosoft.teploObhod.ui.mainactivity

import ru.bingosoft.teploObhod.db.Checkup.Checkup
import ru.bingosoft.teploObhod.db.RouteList.RouteList

interface FragmentsContractActivity {
    fun setCheckup(checkup: Checkup)
    fun setQRListRoute(order: RouteList)
    //fun setCoordinates(point: Point, controlId: Int)
}