package ru.bingosoft.teploObhod.ui.login

import com.google.gson.Gson
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import ru.bingosoft.teploObhod.R
import ru.bingosoft.teploObhod.api.ApiService
import ru.bingosoft.teploObhod.db.AppDatabase
import ru.bingosoft.teploObhod.models.Models
import ru.bingosoft.teploObhod.util.SharedPrefSaver
import ru.bingosoft.teploObhod.util.ThrowHelper
import timber.log.Timber
import java.util.*
import javax.inject.Inject


class LoginPresenter @Inject constructor(
    private val apiService: ApiService,
    private val db: AppDatabase,
    private val sharedPrefSaver: SharedPrefSaver

) {
    var view: LoginContractView? = null
    private var stLogin: String = ""
    private var stPassword: String = ""

    private lateinit var disposable: Disposable

    fun attachView(view: LoginContractView) {
        this.view = view
    }

    fun authorization(stLogin: String?, stPassword: String?) {
        Timber.d("authorization1 $stLogin _ $stPassword")
        if (stLogin != null && stPassword != null) {

            Timber.d("jsonBody=${Gson().toJson(Models.LP(login = stLogin, password = stPassword))}")


            val jsonBody = Gson().toJson(Models.LP())
                .toRequestBody("application/json".toMediaType())

            disposable = apiService.getAuthentication(jsonBody)
                .subscribeOn(Schedulers.io())
                .flatMap { uuid ->
                    Timber.d("uuid=$uuid")
                    Timber.d("jsonBody2=${Gson().toJson(uuid)}")
                    val jsonBody2 = Gson().toJson(uuid)
                        .toRequestBody("application/json".toMediaType())
                    apiService.getAuthorization(jsonBody2)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { token ->
                        Timber.d(token.toString())
                        this.stLogin = stLogin
                        this.stPassword = stPassword
                        view?.saveLoginPasswordToSharedPreference(stLogin, stPassword)
                        view?.saveToken(token.token)

                        val v = view
                        if (v != null) {
                            Timber.d("startService_LoginPresenter")
                            v.alertRepeatSync()
                        }

                    }, { throwable ->
                        throwable.printStackTrace()
                        view?.showFailureTextView()
                    }
                )

        }

    }

    private fun getInfoCurrentUser() {
        Timber.d("getInfoCurrentUser")
        disposable = apiService.getInfoAboutCurrentUser(action = "getUserInfo")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Timber.d("Получили информацию о пользователе")
                Timber.d(it.fullname)
                // сохраним данные в SharedPreference
                view?.saveInfoUserToSharedPreference(it)

            }, {
                it.printStackTrace()
            })

    }

    private fun saveTokenGCM() {
        Timber.d("saveTokenGCM")

        disposable =
            apiService.saveGCMToken(action = "saveGCMToken", token = sharedPrefSaver.getTokenGCM())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Timber.d(it.msg)

                }, {
                    it.printStackTrace()
                })
    }

    fun onDestroy() {
        this.view = null
        if (this::disposable.isInitialized) {
            disposable.dispose()
        }

        sharedPrefSaver.clearAuthData() // Очистим информацию об авторизации
    }

    fun syncDB() {
        Timber.d("syncDB")
        disposable = syncOrder()
            .andThen(syncCheckupGuide())
            .andThen(apiService.getCheckups(action = "getCheckups"))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ checkups ->
                Timber.d("Получили обследования")
                Timber.d("checkups.success=${checkups.success}")

                if (!checkups.success) {
                    throw ThrowHelper("Нет обследований")
                } else {
                    Timber.d("Обследования есть")
                    val data: Models.CheckupList = checkups
                    Single.fromCallable {
                        db.checkupDao().clearCheckup() // Перед вставкой очистим таблицу
                        data.checkups.forEach {
                            Timber.d(it.toString())
                            db.checkupDao().insert(it)
                        }
                    }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe()/*{ _ ->
                            Timber.d("Сохранили обследования в БД")

                        }*/

                    view?.saveDateSyncToSharedPreference(Calendar.getInstance().time)
                }

            }, { throwable ->
                Timber.d("throwable syncDB")
                throwable.printStackTrace()
                if (throwable is ThrowHelper) {
                    view?.showMessageLogin("${throwable.message}")
                }
            })
    }

    private fun syncOrder(): Completable = apiService.getListOrder(action = "getAllOrders")
        .subscribeOn(Schedulers.io())
        .map { response ->
            if (!response.success) {
                throw ThrowHelper("Нет заявок")
            } else {
                val data: Models.OrderList = response
                Single.fromCallable {
                    db.routeListDao().clearOrders() // Перед вставкой очистим таблицу
                    Timber.d("data.orders=${data.orders}")
                    data.orders.forEach {
                        db.routeListDao().insert(it)
                    }

                }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { _ ->
                        view?.showMessageLogin(R.string.order_refresh)

                    }
            }


        }
        .ignoreElement()


    private fun syncCheckupGuide(): Completable =
        apiService.getCheckupGuide(action = "getCheckupGuide")
            .subscribeOn(Schedulers.io())
            //.observeOn(AndroidSchedulers.mainThread())
            .map { response ->
                Timber.d("Получили справочник чеклистов")
                Timber.d(response.toString())

                val data: Models.CheckupGuideList = response
                Single.fromCallable {
                    db.checkupGuideDao().clearCheckupGuide() // Перед вставкой очистим таблицу
                    data.guides.forEach {
                        db.checkupGuideDao().insert(it)
                    }

                }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { _ ->
                        Timber.d("Сохранили справочник чеклистов в БД")
                    }

            }
            .ignoreElement()

}