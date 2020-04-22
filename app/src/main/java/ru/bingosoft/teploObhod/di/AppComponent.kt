package ru.bingosoft.teploObhod.di

import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import ru.bingosoft.mapquestapp.di.DataBaseModule
import ru.bingosoft.teploObhod.App
import javax.inject.Singleton

@Singleton
@Component(
    modules = [AppModule::class,
        AndroidInjectionModule::class,
        ActivityBuilder::class,
        DataBaseModule::class,
        NetworkModule::class,
        UtilModule::class
    ]
)

interface AppComponent {
    @Component.Builder
    interface Builder {
        fun build(): AppComponent
        @BindsInstance
        fun application(application: App): Builder
    }

    fun inject(app: App)
}