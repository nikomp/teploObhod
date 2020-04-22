package ru.bingosoft.teploObhod.ui.mainactivity

interface MainActivityContractView {
    fun showMainActivityMsg(resID: Int)
    fun showMainActivityMsg(msg: String)
    fun dataSyncOK()
    fun updDataOK()
}