package ru.bingosoft.teploObhod.ui.checkup

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.button.MaterialButton
import dagger.android.support.AndroidSupportInjection
import ru.bingosoft.teploObhod.R
import ru.bingosoft.teploObhod.db.Checkup.Checkup
import ru.bingosoft.teploObhod.ui.mainactivity.MainActivity
import ru.bingosoft.teploObhod.util.*
import ru.bingosoft.teploObhod.util.photoSliderHelper.GalleryPagerAdapter
import ru.bingosoft.teploObhod.util.photoSliderHelper.HorizontalAdapter
import timber.log.Timber
import java.io.File
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
    private var blockCheckup: Boolean? = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        AndroidSupportInjection.inject(this)
        Timber.d("CheckupFragment.onCreateView")

        val view = inflater.inflate(R.layout.fragment_gallery, container, false)
        this.root = view

        blockCheckup = arguments?.getBoolean("block")
        val btnSave = view.findViewById(R.id.mbSaveCheckup) as MaterialButton
        btnSave.setOnClickListener(this)
        btnSave.isEnabled = blockCheckup!!

        val btnSend = view.findViewById(R.id.mbSendCheckup) as MaterialButton
        btnSend.setOnClickListener(this)
        btnSend.isEnabled = blockCheckup!!

        checkupPresenter.attachView(this)


        val tag = arguments?.getBoolean("loadCheckupById")
        if (tag != null && tag == true) {
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

    override fun dataIsLoaded(checkup: Checkup) {
        Timber.d("Checkup готов к работе")
        Timber.d(checkup.toString())

        photoHelper.parentFragment = this
        uiCreator = UICreator(this, checkup)
        uiCreator.create(blockCheckup!!)

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
                R.id.mbSendCheckup -> {
                    Timber.d("Отправляем данные на сервер")
                    (this.requireActivity() as MainActivity).mainPresenter.sendData()
                }
            }
        }
    }

    override fun onDestroy() {
        Timber.d("CheckupFragment_onDestroy")
        super.onDestroy()
        checkupPresenter.onDestroy()
    }


    private fun checkPhotoPermission() {
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

            // Обновим список с фото
            val stDir = "PhotoForApp/$photoDir"
            val storageDir =
                File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
                    stDir
                )

            Timber.d("$storageDir")

            val images = OtherUtil().getFilesFromDir("$storageDir")
            refreshPhotoViewer(linearLayout, images, root.context)

        }

    }

    fun refreshPhotoViewer(v: View, images: List<String>, ctx: Context) {
        val pager = v.findViewById(R.id.pager) as ViewPager
        val myList = v.findViewById(R.id.recyclerviewFrag) as RecyclerView
        val photoCount = v.findViewById(R.id.photoCount) as TextView
        photoCount.text = images.size.toString()

        val adapter =
            GalleryPagerAdapter(
                images,
                pager,
                ctx
            )
        pager.adapter = adapter

        pager.offscreenPageLimit = 4 // сколько фоток загружать в память

        adapter.notifyDataSetChanged()
        val horizontalAdapter = HorizontalAdapter(images, pager, ctx)
        val horizontalLayoutManagaer =
            LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false)
        myList.layoutManager = horizontalLayoutManagaer
        myList.adapter = horizontalAdapter
        horizontalAdapter.notifyDataSetChanged()
    }


}