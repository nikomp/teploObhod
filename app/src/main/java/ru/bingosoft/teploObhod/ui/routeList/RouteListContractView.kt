package ru.bingosoft.teploObhod.ui.routeList

import ru.bingosoft.teploObhod.db.RouteList.RouteList

interface RouteListContractView {
    fun showRoutes(routes: List<RouteList>)
    fun showMessageOrders(msg: String)
}