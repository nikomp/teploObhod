package ru.bingosoft.teploObhod.util

import android.content.Context
import android.widget.Toast
import androidx.core.content.ContextCompat
import ru.bingosoft.teploObhod.R

class Toaster(val ctx: Context) {
    fun showToast(resId: Int, duration: Int = Toast.LENGTH_SHORT) {
        showToast(ctx.getString(resId), duration) // Используется перегрузка метода
    }

    fun showToast(msg: String, duration: Int = Toast.LENGTH_SHORT) {
        val toast = Toast.makeText(ctx, msg, duration)
        val view = toast.view
        view.setBackgroundColor(ContextCompat.getColor(ctx, R.color.colorAccent))
        toast.show()
    }
}