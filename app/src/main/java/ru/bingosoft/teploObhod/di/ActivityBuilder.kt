package ru.bingosoft.teploObhod.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.bingosoft.teploObhod.ui.checkup.CheckupFragment
import ru.bingosoft.teploObhod.ui.checkuplist.CheckupListFragment

import ru.bingosoft.teploObhod.ui.login.LoginActivity
import ru.bingosoft.teploObhod.ui.mainactivity.MainActivity
import ru.bingosoft.teploObhod.ui.routeList.RouteListFragment

@Module
abstract class ActivityBuilder {

    @ContributesAndroidInjector()
    abstract fun bindOrderActivity(): RouteListFragment

    @ContributesAndroidInjector()
    abstract fun bindCheckupActivity(): CheckupFragment

    @ContributesAndroidInjector()
    abstract fun bindCheckupListFragment(): CheckupListFragment


    @ContributesAndroidInjector()
    abstract fun bindLoginActivity(): LoginActivity

    @ContributesAndroidInjector()
    abstract fun bindMainActivity(): MainActivity


}