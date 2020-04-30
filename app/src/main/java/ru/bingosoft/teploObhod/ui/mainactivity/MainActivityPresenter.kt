package ru.bingosoft.teploObhod.ui.mainactivity

import android.os.Bundle
import androidx.fragment.app.FragmentManager
import com.google.gson.Gson
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import ru.bingosoft.teploObhod.R
import ru.bingosoft.teploObhod.api.ApiService
import ru.bingosoft.teploObhod.db.AppDatabase
import ru.bingosoft.teploObhod.db.Checkup.Checkup
import ru.bingosoft.teploObhod.ui.checkup.CheckupFragment
import ru.bingosoft.teploObhod.util.OtherUtil
import ru.bingosoft.teploObhod.util.PhotoHelper
import ru.bingosoft.teploObhod.util.ThrowHelper
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class MainActivityPresenter @Inject constructor(
    val db: AppDatabase,
    private val photoHelper: PhotoHelper
) {
    var view: MainActivityContractView? = null

    @Inject
    lateinit var apiService: ApiService

    private lateinit var disposable: Disposable
    private lateinit var checkupsWasSync: List<Checkup>
    lateinit var zipFile: File

    fun attachView(view: MainActivityContractView) {
        this.view = view
    }

    fun sendUserRoute() {
        Timber.d("sendUserRoute")
        disposable = db.trackingUserDao()
            .getAll()
            .subscribeOn(Schedulers.io())
            .map { trackingUserLocation ->
                val actionBody =
                    "trackingUserLocation".toRequestBody("multipart/form-data".toMediaType())
                Timber.d("trackingUserLocation_${Gson().toJson(trackingUserLocation)}")
                val jsonBody = Gson().toJson(trackingUserLocation)
                    .toRequestBody("application/json; charset=utf-8".toMediaType())

                //Timber.d("sendUserRoute_jsonBody.toString()=${jsonBody}")
                return@map actionBody to jsonBody
            }
            .flatMap { actionAndJsonBodies ->
                Timber.d(actionAndJsonBodies.toString())

                apiService.sendTrackingUserLocation(
                    actionAndJsonBodies.first,
                    actionAndJsonBodies.second
                ).toFlowable()
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { response ->
                    Timber.d(response.toString())
                    view?.showMainActivityMsg(R.string.msgRouteUserSendOk)

                }, { throwable ->
                    throwable.printStackTrace()

                }
            )
    }

    private fun isCheckupWithResult(msg: String) {
        Single.fromCallable {
            db.checkupDao().existCheckupWithResult()
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { result ->
                Timber.d("result_existCheckupWithResult=$result")
                if (result > 0) {
                    view?.showMainActivityMsg("$msg Есть чеклисты с неподтвержденными шагами")
                } else {
                    view?.showMainActivityMsg(msg)
                }
            }
    }

    fun sendData() {
        Timber.d("sendData")
        disposable =
            db.checkupDao()
                .getResultAll()
                .subscribeOn(Schedulers.io())
                .takeWhile { listCheckup ->
                    if (listCheckup.isEmpty()) {
                        throw ThrowHelper("Ошибка! Нет данных для передачи на сервер")
                    } else {
                        listCheckup.isNotEmpty()
                    }
                }
                .map { checkups ->
                    val actionBody =
                        "reverseSync".toRequestBody("multipart/form-data".toMediaType())

                    Timber.d("Данные=${Gson().toJson(checkups)}")

                    val jsonBody = Gson().toJson(checkups)
                        .toRequestBody("application/json; charset=utf-8".toMediaType())

                    checkupsWasSync = checkups // Сохраняю данные, которые должны быть переданы

                    return@map actionBody to jsonBody
                }
                .flatMap { actionAndJsonBodies ->
                    //Timber.d(actionAndJsonBodies.toString())
                    // Архив с файлами
                    var fileBody: MultipartBody.Part? = null

                    val syncDirs = OtherUtil().getDirForSync(checkupsWasSync)

                    val zipF = photoHelper.prepareZip(syncDirs)
                    if (zipF != null) {
                        zipFile = zipF
                        Timber.d("Есть ZIP отправляем ${zipF.name}")

                        val requestBody = zipF.asRequestBody("multipart/form-data".toMediaType())
                        fileBody = MultipartBody.Part.createFormData(
                            "zip", zipF.name,
                            requestBody
                        )

                    } else {
                        Timber.d("zipFile == null")
                    }
                    apiService.doReverseSync(
                        actionAndJsonBodies.first,
                        actionAndJsonBodies.second,
                        fileBody
                    ).toFlowable()
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response ->
                        Timber.d(response.toString())
                        zipFile.delete() // удалим переданный архив

                        view?.dataSyncOK() // Пометим чеклисты как переданные
                        view?.showMainActivityMsg(R.string.msgDataSendOk)

                    }, { throwable ->
                        throwable.printStackTrace()
                        if (throwable is ThrowHelper) {
                            //view?.showMainActivityMsg("${throwable.message}")
                            //view?.showMainActivityMsg(R.string.isCheckupWithResult)
                            isCheckupWithResult("${throwable.message}")
                        } else {
                            view?.showMainActivityMsg(R.string.msgDataSendError)
                        }
                    }
                )
    }


    fun sendMessageToAdmin(codeMsg: Int) {
        Timber.d("sendMessageToAdmin codeMsg=$codeMsg")
        disposable =
            apiService.sendMessageToAdmin(action = "sendMessageToAdmin", codeMessage = codeMsg)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    Timber.d(response.toString())
                }, {
                    Timber.d("ошибка!!!")
                    Timber.d(it.printStackTrace().toString())
                })
    }

    fun updData() {
        Timber.d("updData")
        if (this::disposable.isInitialized) {
            disposable.dispose()
        }
        Single.fromCallable {
            Timber.d("Single")
            checkupsWasSync.forEach {
                it.sync = true
                db.checkupDao().update(it)

                // Обновим состояние заявки
                /*val idOrder=it.idOrder
                if (idOrder!=null) {
                    val order=db.ordersDao().getById(idOrder)
                    order.state=STATE_DONE // выполнено

                    db.ordersDao().update(order)
                }*/

            }
        }
            .subscribeOn(Schedulers.io())
            .subscribe { response ->
                view?.updDataOK()
            }
    }

    fun openCheckup(fragmentManager: FragmentManager, idQr: Long) {
        Timber.d("openCheckup")
        // Получим информацию о чеклисте, по orderId
        Single.fromCallable {
            val idCheckup = db.checkupDao().getCheckupIdByOrder(idQr)
            Timber.d("idCheckup=$idCheckup")
            //Загружаем чеклист
            val bundle = Bundle()
            bundle.putBoolean("loadCheckupById", true)
            bundle.putLong("checkupId", idCheckup)

            val fragmentCheckup = CheckupFragment()
            fragmentCheckup.arguments = bundle

            fragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, fragmentCheckup, "checkup_fragment_tag")
                .addToBackStack(null)
                .commit()

            fragmentManager.executePendingTransactions()
        }
            .subscribeOn(Schedulers.io())
            .subscribe()

    }


    fun onDestroy() {
        this.view = null
        if (this::disposable.isInitialized) {
            disposable.dispose()
        }
    }

}