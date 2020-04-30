package ru.bingosoft.teploObhod.util

import android.graphics.Color
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner
import ru.bingosoft.teploObhod.R
import ru.bingosoft.teploObhod.db.Checkup.Checkup
import ru.bingosoft.teploObhod.models.Models
import ru.bingosoft.teploObhod.ui.checkup.CheckupFragment
import ru.bingosoft.teploObhod.ui.mainactivity.MainActivity
import timber.log.Timber
import java.io.File


/**
 * Класс, который создает интерфейс Фрагмента Обследования
 */
//private val rootView: View, val checkup: Checkup, private val photoHelper: PhotoHelper, private val checkupPresenter: CheckupPresenter
class UICreator(private val parentFragment: CheckupFragment, val checkup: Checkup) {
    lateinit var controlList: Models.ControlList

    private val photoHelper = parentFragment.photoHelper

    fun create(enabled: Boolean = true) {
        // Возможно чеклист был ранее сохранен, тогда берем сохраненный и восстанавливаем его
        controlList = if (checkup.textResult != null) {
            Gson().fromJson(checkup.textResult, Models.ControlList::class.java)
        } else {
            Gson().fromJson(checkup.text, Models.ControlList::class.java)
        }

        val rootView = parentFragment.root

        controlList.list.forEach controls@{
            when (it.type) {
                // Выпадающий список
                "combobox" -> {
                    Timber.d("генерим combobox")

                    val templateStep = LayoutInflater.from(rootView.context).inflate(
                        R.layout.template_material_spinner, rootView.parent as ViewGroup?, false
                    ) as LinearLayout

                    templateStep.id = it.id
                    templateStep.findViewById<TextView>(R.id.question).text = it.question


                    val materialSpinner =
                        templateStep.findViewById<MaterialBetterSpinner>(R.id.android_material_design_spinner)

                    materialSpinner.isEnabled = enabled
                    if (enabled) {
                        materialSpinner.dropDownHeight = WindowManager.LayoutParams.WRAP_CONTENT
                    } else {
                        materialSpinner.dropDownHeight = 0
                    }

                    doAssociateParent(templateStep, rootView.findViewById(R.id.mainControl))

                    val spinnerArrayAdapter: ArrayAdapter<String> = ArrayAdapter(
                        rootView.context,
                        R.layout.template_multiline_spinner_item,
                        it.value
                    )

                    // Заполним spinner
                    materialSpinner.setAdapter(spinnerArrayAdapter)

                    // Если шаг чеклиста был ранее сохранен восстановим значение
                    Timber.d("it.checked=${it.answered}")
                    if (it.error) {
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
                    val templateStep = LayoutInflater.from(rootView.context).inflate(
                        R.layout.template_textinput, rootView.parent as ViewGroup?, false
                    ) as LinearLayout


                    templateStep.id = it.id
                    templateStep.findViewById<TextView>(R.id.question).text = it.question

                    val textInputLayout = templateStep.findViewById<TextInputLayout>(R.id.til)
                    textInputLayout.hint = it.hint
                    textInputLayout.isEnabled = enabled

                    val textInputEditText = templateStep.findViewById<TextInputEditText>(R.id.tiet)

                    doAssociateParent(templateStep, rootView.findViewById(R.id.mainControl))

                    // Если шаг чеклиста был ранее сохранен восстановим значение
                    Timber.d("it.checked=${it.answered}")
                    if (it.error) {
                        changeChecked(templateStep, it) // Установим цвет шага
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
                // Числовое поле
                "numeric" -> {
                    val templateStep = LayoutInflater.from(rootView.context).inflate(
                        R.layout.template_numeric, rootView.parent as ViewGroup?, false
                    ) as LinearLayout


                    templateStep.id = it.id
                    templateStep.findViewById<TextView>(R.id.question).text = it.question

                    val textInputLayout = templateStep.findViewById<TextInputLayout>(R.id.til)
                    textInputLayout.hint = it.hint
                    textInputLayout.isEnabled = enabled

                    val textInputEditText = templateStep.findViewById<TextInputEditText>(R.id.tiet)

                    doAssociateParent(templateStep, rootView.findViewById(R.id.mainControl))

                    // Если шаг чеклиста был ранее сохранен восстановим значение
                    Timber.d("it.checked=${it.answered}")
                    if (it.error) {
                        changeChecked(templateStep, it) // Установим цвет шага
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
                    // Если шаг чеклиста был ранее сохранен восстановим значение
                    /*if (it.checked) {
                        changeChecked(templateStep, it) // Установим цвет шага
                    }*/

                    Timber.d("Генерим фото")

                    val templateStep = LayoutInflater.from(rootView.context).inflate(
                        R.layout.template_photo2, rootView.parent as ViewGroup?, false
                    ) as LinearLayout

                    // Обработчик для кнопки "Добавить фото"
                    val btnPhoto = templateStep.findViewById<MaterialButton>(R.id.btnPhoto)
                    btnPhoto.isEnabled = enabled
                    val stepCheckup = it
                    btnPhoto.setOnClickListener {
                        Timber.d("Добавляем фото")
                        (parentFragment.requireActivity() as MainActivity).photoStep =
                            stepCheckup // Сохраним id контрола для которого делаем фото
                        (parentFragment.requireActivity() as MainActivity).photoDir =
                            "${checkup.guidQr}/${stepCheckup.guid}" // Сохраним id контрола для которого делаем фото

                        photoHelper.createPhoto(checkup.guidQr, stepCheckup)
                    }

                    val btnClearAll =
                        templateStep.findViewById<MaterialButton>(R.id.btnPhotoDeleteAll)
                    btnClearAll.isEnabled = enabled
                    btnClearAll.setOnClickListener {
                        Timber.d("Удалим все фото")
                    }

                    templateStep.id = it.id
                    templateStep.findViewById<TextView>(R.id.question).text = it.question

                    doAssociateParent(templateStep, rootView.findViewById(R.id.mainControl))

                    Timber.d("Фото11 ${it.resvalue}")

                    val images: List<String>
                    if (it.resvalue.isNotEmpty()) {
                        Timber.d("Фото ${it.resvalue}")
                        // Обновим список с фото
                        val stDir = "PhotoForApp/${checkup.guidQr}/${stepCheckup.guid}"
                        val storageDir =
                            File(
                                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
                                stDir
                            )

                        images = OtherUtil().getFilesFromDir("$storageDir")

                    } else {
                        images = listOf()
                    }

                    val leftBtn = templateStep.findViewById(R.id.left_nav) as ImageButton
                    val rightBtn = templateStep.findViewById(R.id.right_nav) as ImageButton

                    // Обновим вьювер с фотками
                    parentFragment.refreshPhotoViewer(templateStep, images, rootView.context)

                    val pager = templateStep.findViewById(R.id.pager) as ViewPager
                    val myList = templateStep.findViewById(R.id.recyclerviewFrag) as RecyclerView

                    leftBtn.setOnClickListener {
                        var tab = pager.currentItem
                        if (tab > 0) {
                            tab--
                            pager.currentItem = tab
                        } else if (tab == 0) {
                            pager.currentItem = tab
                        }
                    }

                    rightBtn.setOnClickListener {
                        var tab = pager.currentItem
                        tab++
                        pager.currentItem = tab
                    }

                    pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                        override fun onPageScrollStateChanged(state: Int) {

                        }

                        override fun onPageScrolled(
                            position: Int,
                            positionOffset: Float,
                            positionOffsetPixels: Int
                        ) {

                        }

                        override fun onPageSelected(position: Int) {
                            myList.scrollToPosition(position)
                        }

                    })

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
     * Сменим цвет шага
     */
    fun changeChecked(v: View, control: Models.TemplateControl) {
        Timber.d("changeChecked")
        when (control.type) {
            "combobox" -> {
                Timber.d("combobox")
                val controlView =
                    v.findViewById<MaterialBetterSpinner>(R.id.android_material_design_spinner)
                if (control.error) {
                    controlView.error = v.context.getString(R.string.bad_answere)
                    controlView.setTextColor(
                        ContextCompat.getColor(
                            parentFragment.context!!,
                            R.color.errorAnswere
                        )
                    )
                } else {
                    controlView.setTextColor(Color.BLACK)
                }

            }
            "numeric", "textinput" -> {
                val controlView = v.findViewById<TextInputEditText>(R.id.tiet)
                if (control.error) {
                    controlView.error = v.context.getString(R.string.bad_answere)
                    controlView.setTextColor(
                        ContextCompat.getColor(
                            parentFragment.context!!,
                            R.color.errorAnswere
                        )
                    )
                } else {
                    controlView.setTextColor(Color.BLACK)
                }
            }
            else -> {
                Timber.d("Неизвестный элемент интерфейса")
            }

        }
    }

}