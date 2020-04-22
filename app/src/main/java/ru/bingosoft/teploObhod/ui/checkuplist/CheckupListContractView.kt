package ru.bingosoft.teploObhod.ui.checkuplist

import ru.bingosoft.teploObhod.db.Checkup.Checkup

interface CheckupListContractView {
    fun showCheckups(checkups: List<Checkup>)
}