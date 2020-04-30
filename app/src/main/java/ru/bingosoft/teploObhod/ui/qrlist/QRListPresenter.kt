package ru.bingosoft.teploObhod.ui.qrlist

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import ru.bingosoft.teploObhod.db.AppDatabase
import timber.log.Timber
import javax.inject.Inject

class QRListPresenter @Inject constructor(val db: AppDatabase) {
    var view: QRListContractView? = null

    private lateinit var disposable: Disposable

    fun attachView(view: QRListContractView) {
        this.view = view
    }

    fun loadQRList() {
        disposable = db.qrListDao().getAll()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                Timber.d("Все Обследования получили из БД")
                Timber.d("Число строк=${it.size}")
                Timber.d(it.toString())

                //view?.showCheckups(it)
            }

    }

    fun loadQRListByRoute(idRoute: Long) {
        disposable = db.qrListDao().getByRoute(idRoute)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                Timber.d("loadQRListByRoute")
                Timber.d(it.toString())
                view?.showCheckups(it)
            }
    }

    fun onDestroy() {
        this.view = null
        if (this::disposable.isInitialized) {
            disposable.dispose()
        }
    }
}