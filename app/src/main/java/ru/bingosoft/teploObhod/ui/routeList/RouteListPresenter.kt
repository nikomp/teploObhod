package ru.bingosoft.teploObhod.ui.routeList

import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import ru.bingosoft.teploObhod.db.AppDatabase
import ru.bingosoft.teploObhod.db.RouteList.RouteList
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class RouteListPresenter @Inject constructor(
    val db: AppDatabase
) {

    var view: RouteListContractView? = null

    lateinit var disposable: Disposable

    fun attachView(view: RouteListContractView) {
        this.view = view
    }

    fun loadOrders() {
        Timber.d("loadOrders")
        disposable = db.routeListDao().getAll()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Timber.d("Данные получили из БД")
                Timber.d(it.toString())
                view?.showRoutes(it)
            }, { error ->
                error.printStackTrace()
            })
    }


    fun onDestroy() {
        this.view = null
        if (this::disposable.isInitialized) {
            disposable.dispose()
        }
    }

    fun importData() {
        // Вставка данных в БД
        Timber.d("importData")
        Single.fromCallable {
            val route1 = RouteList(
                route = 3,
                guid = "d4ef2847-a981-47b6-8e20-28e3bd31d83e",
                routeTemplate = 2,
                nameRouteTemplate = "Маршрут 2",
                user = 2,
                dateRoute = SimpleDateFormat(
                    "yyyy-MM-dd H:m:s",
                    Locale("ru", "RU")
                ).parse("2020-04-21 12:00:000")
            )
            db.routeListDao().insert(route1)

            val route2 = RouteList(
                route = 2,
                guid = "a02c5db2-3f4b-437f-ae36-cc40decac2cf",
                routeTemplate = 2,
                nameRouteTemplate = "Маршрут 2",
                user = 2,
                dateRoute = SimpleDateFormat(
                    "yyyy-MM-dd H:m:s",
                    Locale("ru", "RU")
                ).parse("2020-04-21 10:00:000")
            )
            db.routeListDao().insert(route2)
        }
            .subscribeOn(Schedulers.io())
            .subscribe()
    }


}