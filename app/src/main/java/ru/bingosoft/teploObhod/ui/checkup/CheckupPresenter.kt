package ru.bingosoft.teploObhod.ui.checkup

import com.google.gson.Gson
import com.google.gson.JsonObject
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import ru.bingosoft.teploObhod.R
import ru.bingosoft.teploObhod.db.AppDatabase
import ru.bingosoft.teploObhod.models.Models
import ru.bingosoft.teploObhod.util.UICreator
import timber.log.Timber
import javax.inject.Inject

class CheckupPresenter @Inject constructor(val db: AppDatabase) {
    var view: CheckupContractView? = null

    private lateinit var disposable: Disposable

    fun attachView(view: CheckupContractView) {
        this.view = view
    }

    fun loadCheckup(id: Long) {
        Timber.d("loadCheckup")
        disposable = db.checkupDao().getById(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    Timber.d("Обследования получили из БД")
                    Timber.d(it.toString())

                    view?.dataIsLoaded(it)
                }, { error ->
                    error.printStackTrace()
                }

            )
    }

    fun saveCheckup(uiCreator: UICreator) {
        Timber.d("Сохраняем данные чеклиста")
        val resCheckup = Gson().toJsonTree(uiCreator.controlList, Models.ControlList::class.java)
        uiCreator.checkup.textResult = resCheckup as JsonObject

        disposable = Single.fromCallable {
            db.checkupDao().insert(uiCreator.checkup)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ _ ->
                view?.showCheckupMessage(R.string.msgSaveCheckup)
                updateAnsweredCount(uiCreator)
                updateErrorCount(uiCreator)
            }, { error ->
                error.printStackTrace()
            })
    }

    private fun updateAnsweredCount(uiCreator: UICreator) {
        // Отфильтруем только вопросы у которых answered=true
        val filterControls = uiCreator.controlList.list.filter { it.answered }

        disposable = Single.fromCallable {
            db.qrListDao().updateAnsweredCount(uiCreator.checkup.idQr, filterControls.size)
        }
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    private fun updateErrorCount(uiCreator: UICreator) {
        // Отфильтруем только вопросы у которых error=true
        val filterControls = uiCreator.controlList.list.filter { it.error }

        disposable = Single.fromCallable {
            db.qrListDao().updateErrorCount(uiCreator.checkup.idQr, filterControls.size)
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