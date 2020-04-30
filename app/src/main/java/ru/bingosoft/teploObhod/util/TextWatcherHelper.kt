package ru.bingosoft.teploObhod.util

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import ru.bingosoft.teploObhod.models.Models
import timber.log.Timber

class TextWatcherHelper(
    private val control: Models.TemplateControl,
    private val uiCreator: UICreator,
    val v: View
) : TextWatcher {
    override fun afterTextChanged(s: Editable?) {
        control.answered = true
        control.resvalue = s.toString()

        Timber.d(s.toString())
        val posValue = control.value.indexOf(s.toString())
        if (control.typevalue.isNotEmpty()) {
            val type = control.typevalue[posValue]
            control.error = type == "2"
            uiCreator.changeChecked(v, control)
        }
        if (control.type == "numeric") {
            if (control.maxRange != null && control.minRange != null) {
                val min = control.minRange
                val max = control.maxRange
                val res = s.toString().toDouble()
                if (!(res >= min!! && res <= max!!)) {
                    control.error = true
                    uiCreator.changeChecked(v, control)
                }
            }
        }


    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        //TODO реализую при необходимости
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        //TODO реализую при необходимости
    }
}