package com.basalbody.app.di

import android.app.Application
import android.content.Context
import com.google.gson.Gson
import com.basalbody.app.datastore.LocalDataRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }

    @Inject
    lateinit var gson: Gson

    @Provides
    @Singleton
    fun provideLocalDataRepository(context: Context, gson: Gson): LocalDataRepository {
        return LocalDataRepository(context, gson)
    }
}