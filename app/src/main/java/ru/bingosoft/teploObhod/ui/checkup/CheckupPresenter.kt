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
        Timber.d("loadCheckups")
        disposable = db.checkupDao().getById(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                Timber.d("Обследования получили из БД")
                Timber.d(it.toString())

                view?.dataIsLoaded(it)
            }

        Timber.d("ОК")

    }

    fun saveCheckup(uiCreator: UICreator) {
        Timber.d("Сохраняем данные чеклиста")
        /*val filterControls=uiCreator.controlList.list.filter { !it.checked }
        if (filterControls.isNotEmpty()) {
            view?.showCheckupMessage(R.string.notConfirmStep)
        } else {
            val resCheckup= Gson().toJsonTree(uiCreator.controlList, Models.ControlList::class.java)
            uiCreator.checkup.textResult=resCheckup as JsonObject

            disposable=Single.fromCallable{
                db.checkupDao().insert(uiCreator.checkup)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe{_->
                view?.showCheckupMessage(R.string.msgSaveCheckup)
            }
        }*/

        val resCheckup = Gson().toJsonTree(uiCreator.controlList, Models.ControlList::class.java)
        uiCreator.checkup.textResult = resCheckup as JsonObject

        disposable = Single.fromCallable {
            db.checkupDao().insert(uiCreator.checkup)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ _ ->
                view?.showCheckupMessage(R.string.msgSaveCheckup)
            }, { trowable ->
                trowable.printStackTrace()
            })
    }

    fun onDestroy() {
        this.view = null
        if (this::disposable.isInitialized) {
            disposable.dispose()
        }

    }
}