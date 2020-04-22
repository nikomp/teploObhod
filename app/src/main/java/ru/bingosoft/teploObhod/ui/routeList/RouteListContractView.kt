package ru.bingosoft.teploObhod.ui.routeList

import ru.bingosoft.teploObhod.db.RouteList.RouteList

interface RouteListContractView {
    fun showOrders(orders: List<RouteList>)
    fun showMessageOrders(msg: String)
}