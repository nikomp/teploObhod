package ru.bingosoft.teploObhod.util

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import ru.bingosoft.teploObhod.models.Models

class TextWatcherHelper(
    val control: Models.TemplateControl,
    val uiCreator: UICreator,
    val v: View
) : TextWatcher {
    override fun afterTextChanged(s: Editable?) {
        control.checked = false
        control.resvalue = s.toString()
        uiCreator.changeChecked(v, control)
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        //TODO реализую при необходимости
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        //TODO реализую при необходимости
    }
}