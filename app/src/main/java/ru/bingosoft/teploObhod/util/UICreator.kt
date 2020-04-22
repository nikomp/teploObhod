package ru.bingosoft.teploObhod.util

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.mapbox.mapboxsdk.geometry.LatLng
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner
import ru.bingosoft.teploObhod.R
import ru.bingosoft.teploObhod.db.Checkup.Checkup
import ru.bingosoft.teploObhod.models.Models
import ru.bingosoft.teploObhod.ui.checkup.CheckupFragment
import ru.bingosoft.teploObhod.ui.mainactivity.MainActivity
import timber.log.Timber
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Класс, который создает интерфейс Фрагмента Обследования
 */
//private val rootView: View, val checkup: Checkup, private val photoHelper: PhotoHelper, private val checkupPresenter: CheckupPresenter
class UICreator(val parentFragment: CheckupFragment, val checkup: Checkup) {
    lateinit var controlList: Models.ControlList

    val photoHelper = parentFragment.photoHelper

    fun create() {
        // Возможно чеклист был ранее сохранен, тогда берем сохраненный и восстанавливаем его
        controlList = if (checkup.textResult != null) {
            Gson().fromJson(checkup.textResult, Models.ControlList::class.java)
        } else {
            Gson().fromJson(checkup.text, Models.ControlList::class.java)
        }

        val rootView = parentFragment.root
        val checkupPresenter = parentFragment.checkupPresenter

        controlList.list.forEach controls@{ it ->
            when (it.type) {
                // Выпадающий список
                "combobox" -> {
                    Timber.d("генерим combobox")

                    val templateStep = LayoutInflater.from(rootView.context).inflate(
                        R.layout.template_material_spinner, rootView.parent as ViewGroup?, false
                    ) as LinearLayout

                    attachListenerToFab(templateStep, it)

                    templateStep.id = it.id
                    templateStep.findViewById<TextView>(R.id.question).text = it.question

                    val materialSpinner =
                        templateStep.findViewById<MaterialBetterSpinner>(R.id.android_material_design_spinner)

                    doAssociateParent(templateStep, rootView.findViewById(R.id.mainControl))

                    val spinnerArrayAdapter: ArrayAdapter<String> = ArrayAdapter(
                        rootView.context,
                        R.layout.template_multiline_spinner_item,
                        it.value
                    )

                    // Заполним spinner
                    materialSpinner.setAdapter(spinnerArrayAdapter)

                    // Если шаг чеклиста был ранее сохранен восстановим значение
                    Timber.d("it.checked=${it.checked}")
                    if (it.checked) {
                        changeChecked(templateStep, it) // Установим цвет шага
                    }
                    if (it.resvalue.isNotEmpty()) {
                        materialSpinner.setText(it.resvalue)
                    }
                    // Вешаем обработчик на spinner последним, иначе сбрасывается цвет шага
                    materialSpinner.addTextChangedListener(
                        TextWatcherHelper(
                            it,
                            this,
                            templateStep
                        )
                    )

                    return@controls

                }
                // Строковое поле ввода однострочное
                "textinput" -> {
                    // Строковое поле однострочное
                    val templateStep = LayoutInflater.from(rootView.context).inflate(
                        R.layout.template_textinput, rootView.parent as ViewGroup?, false
                    ) as LinearLayout

                    attachListenerToFab(templateStep, it)

                    templateStep.id = it.id
                    templateStep.findViewById<TextView>(R.id.question).text = it.question

                    val textInputLayout = templateStep.findViewById<TextInputLayout>(R.id.til)
                    textInputLayout.hint = it.hint


                    val textInputEditText = templateStep.findViewById<TextInputEditText>(R.id.tiet)


                    doAssociateParent(templateStep, rootView.findViewById(R.id.mainControl))

                    // Если шаг чеклиста был ранее сохранен восстановим значение
                    Timber.d("it.checked=${it.checked}")
                    if (it.checked) {
                        changeChecked(templateStep, it) // Установим цвет шага
                        Timber.d("it.resvalue=${it.resvalue}")

                    }
                    if (it.resvalue.isNotEmpty()) {
                        textInputEditText.setText(it.resvalue)
                    }
                    // Вешаем обработчик на textInputEditText последним, иначе сбрасывается цвет шага
                    textInputEditText.addTextChangedListener(
                        TextWatcherHelper(
                            it,
                            this,
                            templateStep
                        )
                    )

                    return@controls
                }
                "photo" -> {
                    // контрол с кнопкой для фото
                    val templateStep = LayoutInflater.from(rootView.context).inflate(
                        R.layout.template_photo, rootView.parent as ViewGroup?, false
                    ) as LinearLayout

                    attachListenerToFab(templateStep, it)

                    templateStep.id = it.id
                    templateStep.findViewById<TextView>(R.id.question).text = it.question

                    doAssociateParent(templateStep, rootView.findViewById(R.id.mainControl))

                    // Если шаг чеклиста был ранее сохранен восстановим значение
                    if (it.checked) {
                        changeChecked(templateStep, it) // Установим цвет шага
                    }

                    Timber.d("Фото11 ${it.resvalue}")
                    if (it.resvalue.isNotEmpty()) {
                        Timber.d("Фото ${it.resvalue}")
                        templateStep.findViewById<TextView>(R.id.photoResult).text =
                            parentFragment.getString(
                                R.string.photoResult,
                                "DCIM\\PhotoForApp\\${it.resvalue}"
                            )
                    }

                    // Обработчик для кнопки "Добавить фото"
                    val btnPhoto = templateStep.findViewById<Button>(R.id.btnPhoto)
                    val stepCheckup = it
                    btnPhoto.setOnClickListener {
                        Timber.d("Добавляем фото")
                        (parentFragment.requireActivity() as MainActivity).photoStep =
                            stepCheckup // Сохраним id контрола для которого делаем фото
                        (parentFragment.requireActivity() as MainActivity).photoDir =
                            "${checkup.guid}/${stepCheckup.guid}" // Сохраним id контрола для которого делаем фото
                        photoHelper.createPhoto(checkup.guid, stepCheckup)
                    }

                    return@controls
                }
                "map_coordinates" -> {
                    // контрол с кнопкой, открываем карту и забираем с нее координаты клика
                    val templateStep = LayoutInflater.from(rootView.context).inflate(
                        R.layout.template_map_coordinates, rootView.parent as ViewGroup?, false
                    ) as LinearLayout

                    attachListenerToFab(templateStep, it)

                    Timber.d("it.id=${it.id}")
                    templateStep.id = it.id
                    Timber.d("templateStep.id=${templateStep.id}")
                    templateStep.findViewById<TextView>(R.id.question).text = it.question

                    doAssociateParent(templateStep, rootView.findViewById(R.id.mainControl))

                    // Если шаг чеклиста был ранее сохранен восстановим значение
                    if (it.checked) {
                        changeChecked(templateStep, it) // Установим цвет шага
                    }
                    if (it.resvalue.isNotEmpty()) {
                        Timber.d("map_coordinates=${it.resvalue}")
                        val point = Gson().fromJson(it.resvalue, Models.MapPoint::class.java)
                        templateStep.findViewById<TextView>(R.id.mapCoordinatesResult).text =
                            parentFragment.getString(
                                R.string.coordinates,
                                BigDecimal(point.lat!!).setScale(5, RoundingMode.HALF_EVEN),
                                BigDecimal(point.lon!!).setScale(5, RoundingMode.HALF_EVEN)
                            )

                    } else {
                        // Возьмем координаты от Activity
                        val controlId =
                            (parentFragment.requireActivity() as MainActivity).controlMapId
                        if (it.id == controlId) {
                            val point = (parentFragment.requireActivity() as MainActivity).mapPoint
                            templateStep.findViewById<TextView>(R.id.mapCoordinatesResult).text =
                                parentFragment.getString(
                                    R.string.coordinates,
                                    BigDecimal(point.latitude).setScale(5, RoundingMode.HALF_EVEN),
                                    BigDecimal(point.longitude).setScale(5, RoundingMode.HALF_EVEN)
                                )

                            val mapPoint = Models.MapPoint(point.latitude, point.longitude)

                            it.resvalue = Gson().toJson(mapPoint)
                            Timber.d("mapPoint=${it.resvalue}")
                        }
                    }

                    // Обработчик для кнопки "Добавить координаты"
                    val btnMap = templateStep.findViewById<Button>(R.id.btnMap)
                    //val stepCheckup=it
                    btnMap.setOnClickListener {
                        Timber.d("Добавляем координаты")

                        // Сохраним текущее состояние чеклиста
                        checkupPresenter.saveCheckup(this)

                    }

                    return@controls
                }
                else -> {
                    Timber.d("Неизвестный элемент интерфейса")
                    return@controls
                }
            }
        }

    }

    /**
     * Метод, в котором осуществляется привязка дочернего View к родительскому
     */
    private fun doAssociateParent(v: View, mainView: View) {
        if (mainView is LinearLayout) {
            mainView.addView(v)
        }
    }

    /**
     * Найдем кнопку Fab и добавим для нее обработчик
     */
    private fun attachListenerToFab(v: View, control: Models.TemplateControl) {
        Timber.d("attachListenerToFab")

        val fab: FloatingActionButton = v.findViewById(R.id.fabCheck)
        fab.setOnClickListener {
            Timber.d("Прошли шаг чеклиста")

            var isEmpty = false

            // Проверим введено ли значение
            when (control.type) {
                "combobox" -> {
                    val controlView =
                        v.findViewById<MaterialBetterSpinner>(R.id.android_material_design_spinner)
                    if (TextUtils.isEmpty(controlView.text.toString())) {
                        controlView.error = "Нужно выбрать значение из списка"
                        isEmpty = true
                    }
                }
                "textinput" -> {
                    val controlView = v.findViewById<TextInputEditText>(R.id.tiet)
                    if (TextUtils.isEmpty(controlView.text.toString())) {
                        controlView.error = "Нужно заполнить поле"
                        isEmpty = true
                    }
                }
                "photo" -> {
                    // Проверим были ли добавлены фото
                    Timber.d("Папка ${checkup.guid}/${control.guid}")
                    val controlViewError = v.findViewById<TextView>(R.id.errorPhoto)
                    if (photoHelper.checkDirAndEmpty("${checkup.guid}/${control.guid}")) {
                        Timber.d("Папка есть, она не пуста")
                        controlViewError.visibility = View.INVISIBLE
                        // Сохраним filemap в resValue
                        //control.resvalue="${checkup.guid}/${control.guid}"

                        Timber.d(control.resvalue)

                    } else {
                        Timber.d("Папка с фото отсутствует")
                        controlViewError.visibility = View.VISIBLE
                        isEmpty = true
                    }
                }
                "map_coordinates" -> {
                    val controlView = v.findViewById<TextView>(R.id.mapCoordinatesResult)
                    val controlViewError = v.findViewById<TextView>(R.id.errorMap)
                    if (!controlView.text.equals(parentFragment.resources.getString(R.string.not_coordinates))) {
                        Timber.d("координаты заданы")
                        controlViewError.visibility = View.INVISIBLE
                    } else {
                        Timber.d("координаты не заданы")
                        controlViewError.visibility = View.VISIBLE
                        isEmpty = true
                    }
                }
                else -> {
                    Timber.d("Неизвестный элемент интерфейса")
                }

            }

            if (!isEmpty) {
                control.checked = !control.checked
                changeChecked(v, control)
            }

        }
    }

    /**
     * Сменим цвет шага
     */
    fun changeChecked(v: View, control: Models.TemplateControl) {
        Timber.d("changeChecked")
        val cardView = v.findViewById<CardView>(R.id.cv)
        if (control.checked) {
            Timber.d("green")
            cardView?.setCardBackgroundColor(
                ContextCompat.getColor(
                    v.context,
                    R.color.colorCardSelect
                )
            )
        } else {
            cardView?.setCardBackgroundColor(
                ContextCompat.getColor(
                    v.context,
                    R.color.colorCardItem
                )
            )
        }
    }

    private fun parseLatLng(str: String): LatLng {
        Timber.d("parseLatLng $str")
        val lat = str.substring(str.indexOf("latitude=") + 9, str.indexOf(", longitude")).toDouble()
        val lon =
            str.substring(str.indexOf("longitude=") + 10, str.indexOf(", altitude")).toDouble()
        return LatLng(lat, lon)
    }

}