package ru.bingosoft.teploObhod.di

import dagger.Module
import dagger.Provides
import ru.bingosoft.teploObhod.App
import ru.bingosoft.teploObhod.util.PhotoHelper
import ru.bingosoft.teploObhod.util.SharedPrefSaver
import ru.bingosoft.teploObhod.util.Toaster
import javax.inject.Singleton

@Module
class UtilModule {
    @Provides
    @Singleton
    fun provideToaster(application: App): Toaster {
        return Toaster(application.applicationContext)
    }

    @Provides
    @Singleton
    fun provideSharedPrefSaver(application: App): SharedPrefSaver {
        return SharedPrefSaver(application.applicationContext)
    }

    @Provides
    @Singleton
    fun providePhotoHelper(): PhotoHelper {
        return PhotoHelper()
    }
}