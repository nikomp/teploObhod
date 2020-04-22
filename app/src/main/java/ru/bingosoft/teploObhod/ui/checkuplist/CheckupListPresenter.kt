package ru.bingosoft.teploObhod.ui.checkuplist

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import ru.bingosoft.teploObhod.db.AppDatabase
import timber.log.Timber
import javax.inject.Inject

class CheckupListPresenter @Inject constructor(val db: AppDatabase) {
    var view: CheckupListContractView? = null

    private lateinit var disposable: Disposable

    fun attachView(view: CheckupListContractView) {
        this.view = view
    }

    fun loadCheckupList() {
        disposable = db.checkupDao().getAll()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                Timber.d("Все Обследования получили из БД")
                Timber.d("Число строк=${it.size}")
                Timber.d(it.toString())

                view?.showCheckups(it)
            }

    }

    fun loadCheckupListByOrder(idOrder: Long) {
        disposable = db.checkupDao().getCheckupsOrder(idOrder)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                Timber.d("loadCheckupListByOrder")
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