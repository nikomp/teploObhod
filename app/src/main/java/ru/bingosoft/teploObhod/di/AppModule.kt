package ru.bingosoft.teploObhod.di

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.bingosoft.teploObhod.App
import javax.inject.Singleton

@Module
class AppModule {
    @Provides
    @Singleton
    fun provideApplication(app: App): Context = app.applicationContext
}
