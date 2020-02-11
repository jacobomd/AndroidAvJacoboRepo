package io.keepcoding.eh_ho.di

import android.content.Context
import dagger.Module
import dagger.Provides
import io.keepcoding.eh_ho.data.repository.LatestNewsRepo
import io.keepcoding.eh_ho.database.LatestNewsDatabase
import javax.inject.Singleton


@Module
class LatestNewsModule {

    @Singleton
    @Provides
    fun providesLatestNewsRepo (context: Context, latestNewsDatabase: LatestNewsDatabase): LatestNewsRepo =
        LatestNewsRepo.apply {
            db = latestNewsDatabase
            ctx = context
        }

}