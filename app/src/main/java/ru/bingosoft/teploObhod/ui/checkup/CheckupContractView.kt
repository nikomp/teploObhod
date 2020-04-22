package ru.bingosoft.teploObhod.ui.checkup

import ru.bingosoft.teploObhod.db.Checkup.Checkup

interface CheckupContractView {
    fun dataIsLoaded(checkup: Checkup)
    fun showCheckupMessage(resID: Int)
}