package ru.bingosoft.teploObhod.ui.mainactivity

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.navigation.NavigationView
import dagger.android.AndroidInjection
import ru.bingosoft.teploObhod.BuildConfig
import ru.bingosoft.teploObhod.R
import ru.bingosoft.teploObhod.api.ApiService
import ru.bingosoft.teploObhod.db.Checkup.Checkup
import ru.bingosoft.teploObhod.db.RouteList.RouteList
import ru.bingosoft.teploObhod.models.Models
import ru.bingosoft.teploObhod.ui.checkup.CheckupFragment
import ru.bingosoft.teploObhod.ui.login.LoginActivity
import ru.bingosoft.teploObhod.ui.qrlist.QRListFragment
import ru.bingosoft.teploObhod.util.Const
import ru.bingosoft.teploObhod.util.Const.MessageCode.REFUSED_PERMISSION
import ru.bingosoft.teploObhod.util.Const.MessageCode.REPEATEDLY_REFUSED
import ru.bingosoft.teploObhod.util.Const.RequestCodes.PHOTO
import ru.bingosoft.teploObhod.util.Const.RequestCodes.QR_SCAN
import ru.bingosoft.teploObhod.util.SharedPrefSaver
import ru.bingosoft.teploObhod.util.Toaster
import timber.log.Timber
import javax.inject.Inject


class MainActivity : AppCompatActivity(), FragmentsContractActivity,
    NavigationView.OnNavigationItemSelectedListener, MainActivityContractView {

    @Inject
    lateinit var mainPresenter: MainActivityPresenter
    @Inject
    lateinit var toaster: Toaster
    @Inject
    lateinit var sharedPref: SharedPrefSaver
    @Inject
    lateinit var apiService: ApiService

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController

    var photoDir: String = ""
    var lastKnownFilenamePhoto = ""
    var photoStep: Models.TemplateControl? = null
    var images: List<String>? = listOf()
    private lateinit var locationManager: LocationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.d("MainActivity_onCreate")
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        mainPresenter.attachView(this)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)

        drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerStateChanged(newState: Int) {
                Timber.d("onDrawerStateChanged")

            }

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                Timber.d("onDrawerSlide")

            }

            override fun onDrawerClosed(drawerView: View) {
                Timber.d("onDrawerClosed")

            }

            override fun onDrawerOpened(drawerView: View) {
                Timber.d("onDrawerOpened")
                invalidateNavigationDrawer()
            }

        })


        val navView: NavigationView = findViewById(R.id.nav_view)
        navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.nav_gallery,
                R.id.nav_slideshow,
                R.id.nav_checkup
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setNavigationItemSelectedListener(this)
        //navView.setupWithNavController(navController) // Переключалка фрагментов по-умолчанию

    }

    private fun buildAlertMessageNoGps() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Датчик GPS выключен, включить?").setCancelable(false)
            .setPositiveButton(
                "Да"
            ) { _, _ -> startActivity(Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }
            .setNegativeButton(
                "Нет"
            ) { dialog, _ ->
                Timber.d("Сообщение администратору")
                mainPresenter.sendMessageToAdmin(REPEATEDLY_REFUSED)
                dialog?.cancel()
            }
        val alert = builder.create()

        alert.setOnShowListener { dialog ->
            val posButton = (dialog as AlertDialog).getButton(DialogInterface.BUTTON_POSITIVE)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.leftMargin = 20
            posButton.layoutParams = params
        }
        alert.show()
    }

    private fun requestPermission() {
        // Проверим разрешения
        if (ContextCompat.checkSelfPermission(
                this,
                (Manifest.permission.ACCESS_FINE_LOCATION)
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ),
                    Const.RequestCodes.PERMISSION
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Timber.d("onRequestPermissionsResult")
        when (requestCode) {
            Const.RequestCodes.PERMISSION -> {
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    // Разрешения выданы
                    Timber.d("startService_Permission")
                    //startService(Intent(this,UserLocationService::class.java))
                    //enableLocationComponent()
                } else {
                    // Разрешения не выданы оповестим юзера
                    toaster.showToast(R.string.not_permissions)
                    Timber.d("ОТКАЗАЛСЯ ОТ ГЕОЛОКАЦИИ")
                    mainPresenter.sendMessageToAdmin(REFUSED_PERMISSION)
                }
            }
            else -> Timber.d("Неизвестный PERMISSION_REQUEST_CODE")
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PHOTO -> {
                    Timber.d("REQUEST_CODE_PHOTO")
                    setPhotoResult()

                }
                QR_SCAN -> {
                    Timber.d(data?.getStringExtra("SCAN_RESULT"))
                    val scanResult = data?.getStringExtra("SCAN_RESULT")
                    val orderId = scanResult?.toLongOrNull()
                    Timber.d(orderId.toString())
                    if (orderId != null) {
                        mainPresenter.openCheckup(this.supportFragmentManager, orderId)
                    } else {
                        toaster.showToast(R.string.not_checkup)
                    }


                }
                else -> {
                    Timber.d("Неизвестный requestCode")
                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        /*stopService(Intent(this,UserLocationService::class.java))
        mainPresenter.onDestroy()
        //unregisterReceiver(userLocationReceiver)
        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(userLocationReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        userLocationReceiver.onDestroy()*/

    }


    override fun onBackPressed() {
        super.onBackPressed()
        Timber.d("onBackPressed")
        Timber.d("backStackEntryCount=${supportFragmentManager.backStackEntryCount}")

        if (supportFragmentManager.backStackEntryCount == 0) {
            supportActionBar?.setTitle(R.string.menu_orders)
        }

        val fragment = supportFragmentManager.findFragmentByTag("checkup_fragment_tag")
        if (fragment != null) {
            Timber.d("onBackPressed_checkup_fragment_tag")
            val checkupId = fragment.arguments?.getLong("checkupId")
            Timber.d("checkupId=$checkupId")
            if (checkupId != null) {
                (fragment as CheckupFragment).checkupPresenter.loadCheckup(checkupId)
            }
        }

        val fragment2 = supportFragmentManager.findFragmentByTag("checkup_list_fragment_tag")
        if (fragment2 != null) {
            Timber.d("onBackPressed_checkup_list_fragment_tag")
            val idOrder = fragment2.arguments?.getLong("idOrder")
            if (idOrder != null) {
                (fragment2 as QRListFragment).QRListPresenter.loadQRListByRoute(
                    idOrder
                ) // Грузим объекты только выбранной заявки
            } else {
                (fragment2 as QRListFragment).QRListPresenter.loadQRList() // Грузим все объекты
            }
        }
    }

    override fun setCheckup(checkup: Checkup) {
        Timber.d("setCheckup from Activity")
        val cf =
            this.supportFragmentManager.findFragmentByTag("checkup_fragment_tag") as? CheckupFragment
        cf?.dataIsLoaded(checkup)
    }

    override fun setQRListRoute(route: RouteList) {
        Timber.d("setChecupListOrder from Activity")
        val clf =
            this.supportFragmentManager.findFragmentByTag("checkup_list_fragment_tag") as? QRListFragment
        clf?.showQRListRoute(route)
    }

    /*override fun setCoordinates(point: Point, controlId: Int) {
        Timber.d("setCoordinates from Activity")
        mapPoint = point
        this.controlMapId = controlId
    }*/

    private fun setPhotoResult() {
        Timber.d("setPhotoResult from Activity")
        val cf =
            this.supportFragmentManager.findFragmentByTag("checkup_fragment_tag") as? CheckupFragment
        cf?.setPhotoResult(photoStep?.id, "$photoDir")
        photoStep?.resvalue = photoDir
        photoStep?.answered = true

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)

        Timber.d("onNavigationItemSelected")

        when (item.itemId) {
            R.id.nav_home -> {
                navController.navigate(R.id.nav_home)
                return true
            }
            R.id.nav_gallery -> {
                navController.navigate(R.id.nav_gallery)
                return true
            }
            R.id.nav_slideshow -> {
                navController.navigate(R.id.nav_slideshow)
                return true
            }
            R.id.nav_send_data -> {
                Timber.d("Отправляем данные на сервер")
                mainPresenter.sendData()
                //mainPresenter.isCheckupWithResult()
                return true
            }
            R.id.nav_auth -> {
                // Запустим активити с настройками
                val intent = Intent(this, LoginActivity::class.java)
                startActivityForResult(intent, Const.RequestCodes.AUTH)
                return true
            }
            R.id.nav_qr_scan -> {
                // Запустим сканер QR
                return try {
                    val intent = Intent("com.google.zxing.client.android.SCAN")
                    intent.putExtra("SCAN_MODE", "QR_CODE_MODE")
                    startActivityForResult(intent, QR_SCAN)
                    true
                } catch (e: Exception) {

                    val marketUri = Uri.parse("market://details?id=com.google.zxing.client.android")
                    val marketIntent = Intent(Intent.ACTION_VIEW, marketUri)
                    startActivity(marketIntent)
                    false

                }

            }
            else -> {
                return false
            }
        }
    }

    override fun showMainActivityMsg(resID: Int) {
        Timber.d("showMainActivityMsg")
        toaster.showToast(resID)
    }

    override fun showMainActivityMsg(msg: String) {
        Timber.d("showMainActivityMsg")
        toaster.showToast(msg)
    }

    override fun dataSyncOK() {
        Timber.d("dataSyncOK")
        mainPresenter.updData()
    }

    override fun updDataOK() {
        Timber.d("updDataOK")
        //Передаем маршрут пользователя
        mainPresenter.sendUserRoute()

    }

    fun invalidateNavigationDrawer() {
        Timber.d("invalidateNavigationDrawer")
        val user = sharedPref.getUser()

        val navView: NavigationView = findViewById(R.id.nav_view)

        val headerLayout = navView.getHeaderView(0)

        if (user.photoUrl != "") {
            val ivAvatar: ImageView =
                headerLayout.findViewById(R.id.imageAvatar)

            val glideUrl = GlideUrl(
                "${BuildConfig.urlServer}/${user.photoUrl}", LazyHeaders.Builder()
                    .build()
            )

            Glide
                .with(this)
                .load(glideUrl)
                .apply(RequestOptions.circleCropTransform())
                .into(ivAvatar)
        }

        if (user.fullname != "") {
            val tvName: TextView = headerLayout.findViewById(R.id.fullname)
            tvName.text = user.fullname
        }
    }

}
