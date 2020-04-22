package ru.bingosoft.teploObhod.ui.checkup

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.mapbox.mapboxsdk.geometry.LatLng
import dagger.android.support.AndroidSupportInjection
import ru.bingosoft.teploObhod.R
import ru.bingosoft.teploObhod.db.Checkup.Checkup
import ru.bingosoft.teploObhod.util.Const
import ru.bingosoft.teploObhod.util.PhotoHelper
import ru.bingosoft.teploObhod.util.Toaster
import ru.bingosoft.teploObhod.util.UICreator
import timber.log.Timber
import javax.inject.Inject


class CheckupFragment : Fragment(), CheckupContractView, View.OnClickListener {

    @Inject
    lateinit var checkupPresenter: CheckupPresenter

    @Inject
    lateinit var toaster: Toaster

    @Inject
    lateinit var photoHelper: PhotoHelper

    lateinit var root: View
    private lateinit var uiCreator: UICreator

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        AndroidSupportInjection.inject(this)
        Timber.d("CheckupFragment.onCreateView")

        val view = inflater.inflate(R.layout.fragment_gallery, container, false)
        this.root = view

        val btnSave = view.findViewById(R.id.mbSaveCheckup) as MaterialButton
        btnSave.setOnClickListener(this)

        checkupPresenter.attachView(this)

        val tag = arguments?.getBoolean("loadCheckupById")
        if (tag != null && tag == true) {
            Timber.d("loadCheckupById")
            val checkupId = arguments?.getLong("checkupId")
            Timber.d("checkupId=$checkupId")
            if (checkupId != null) {
                checkupPresenter.loadCheckup(checkupId)
            }
        }

        // Устанавливаем заголовок фрагмента
        (this.requireActivity() as AppCompatActivity).supportActionBar?.setTitle(R.string.title_checkup_fragment)


        checkPhotoPermission() // Проверим разрешения для фото
        return view
    }

    /*override fun onStop() {
        Timber.d("CheckupFragment_onStop")
        super.onStop()
    }*/

    override fun dataIsLoaded(checkup: Checkup) {
        Timber.d("Checkup готов к работе")
        Timber.d(checkup.toString())

        photoHelper.parentFragment = this
        uiCreator = UICreator(this, checkup)
        uiCreator.create()

    }

    fun setResultMapPoint(point: LatLng, controlId: Int) {
        Timber.d("CheckupFragment setResultMapPoint $point, $controlId")
        /*val templateStep=root.findViewById<LinearLayout>(controlId)
        val mapResultTextView=templateStep.findViewById<TextView>(R.id.mapCoordinatesResult)
        mapResultTextView.text=point.toString()*/

    }


    override fun showCheckupMessage(resID: Int) {
        toaster.showToast(resID)
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.mbSaveCheckup -> {
                    checkupPresenter.saveCheckup(uiCreator)
                }
            }
        }
    }

    override fun onDestroy() {
        Timber.d("CheckupFragment_onDestroy")
        super.onDestroy()
        checkupPresenter.onDestroy()
    }


    fun checkPhotoPermission() {
        // Проверим разрешения
        if (ContextCompat.checkSelfPermission(
                this.requireContext(),
                (Manifest.permission.READ_EXTERNAL_STORAGE)
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this.requireContext(),
                (Manifest.permission.WRITE_EXTERNAL_STORAGE)
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this.requireContext(),
                (Manifest.permission.CAMERA)
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
                ),
                Const.RequestCodes.PERMISSION
            )

        }
    }

    /*override fun onPause() {
        Timber.d("CheckupFragment_onPause")
        super.onPause()
    }*/

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d(Const.LogTags.LOGTAG, "onRequestPermissionsResult")
        when (requestCode) {
            Const.RequestCodes.PERMISSION -> {
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                    && grantResults[2] == PackageManager.PERMISSION_GRANTED
                ) {
                    // Разрешения выданы, повторим попытку
                    Timber.d("enableLocationComponent")
                    //enableLocationComponent(mapboxMap?.getStyle()!!)
                    //getUserLocation(mapboxMap.style!!)
                } else {
                    // Разрешения не выданы оповестим юзера
                    toaster.showToast(R.string.not_permissions)
                }
            }
            else -> Timber.d("Неизвестный PERMISSION_REQUEST_CODE")
        }

    }

    fun setPhotoResult(controlId: Int?, photoDir: String) {
        Timber.d("setPhotoResult from fragment ${controlId}")
        if (controlId != null) {
            Timber.d("controlId!=null")
            val linearLayout = root.findViewById<LinearLayout>(controlId)
            linearLayout.findViewById<TextView>(R.id.photoResult).text =
                this.getString(R.string.photoResult, photoDir)
        }


    }

}