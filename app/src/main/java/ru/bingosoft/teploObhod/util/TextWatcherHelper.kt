package ru.bingosoft.teploObhod.util

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import ru.bingosoft.teploObhod.models.Models
import timber.log.Timber

class TextWatcherHelper(
    val control: Models.TemplateControl,
    val uiCreator: UICreator,
    val v: View
) : TextWatcher {
    override fun afterTextChanged(s: Editable?) {
        control.answered = true
        control.resvalue = s.toString()

        Timber.d(s.toString())
        val posValue = control.value.indexOf(s.toString())
        Timber.d("posValue=$posValue")
        if (control.typevalue.isNotEmpty()) {
            val valutype = control.typevalue[posValue]
            control.error = valutype == "2"
            uiCreator.changeChecked(v, control)
        }

    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        //TODO реализую при необходимости
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        //TODO реализую при необходимости
    }
}