package ru.bingosoft.teploObhod.ui.qrlist

import ru.bingosoft.teploObhod.db.QRList.QRList

interface QRListContractView {
    fun showCheckups(qrs: List<QRList>)
}