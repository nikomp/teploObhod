package ru.bingosoft.teploObhod.ui.checkuplist

import android.view.View

interface CheckupListRVClickListeners {
    fun recyclerViewListClicked(v: View?, position: Int)
}