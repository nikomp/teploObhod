package ru.bingosoft.teploObhod

import android.app.Application
import android.util.Log
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import io.reactivex.plugins.RxJavaPlugins
import ru.bingosoft.teploObhod.di.DaggerAppComponent
import ru.bingosoft.teploObhod.util.Const
import timber.log.Timber
import java.util.*
import javax.inject.Inject


class App : Application(), HasAndroidInjector {


    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        DaggerAppComponent.builder()
            .application(this)
            .build()
            .inject(this)


        //https://proandroiddev.com/rxjava2-undeliverableexception-f01d19d18048
        //https://stackoverflow.com/questions/52631581/rxjava2-undeliverableexception-when-orientation-change-is-happening-while-fetchi
        RxJavaPlugins.setErrorHandler { throwable: Throwable? -> }

    }

    override fun androidInjector(): AndroidInjector<Any> = androidInjector

    /**
     * Метод для генерации ГУИДа, нужен для первичного формирования fingerprint
     *
     * @return - возвращается строка содержащая ГУИД
     */
    private fun random(): String {
        var stF = UUID.randomUUID().toString()
        stF = stF.replace("-".toRegex(), "")
        stF = stF.substring(0, 32)
        Log.d(Const.LogTags.LOGTAG, "random()=$stF")

        return stF
    }

}