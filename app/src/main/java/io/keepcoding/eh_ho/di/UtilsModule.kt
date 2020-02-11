package io.keepcoding.eh_ho.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import io.keepcoding.eh_ho.database.LatestNewsDatabase
import io.keepcoding.eh_ho.database.TopicDatabase
import javax.inject.Singleton

@Module
class UtilsModule(private val context: Context) {

    @Singleton
    @Provides
    fun provideApplicationContext(): Context = context

    @Singleton
    @Provides
    fun provideTopicDb(): TopicDatabase = Room.databaseBuilder(
        context, TopicDatabase::class.java, "topic-database"
    ).build()

    @Singleton
    @Provides
    fun provideLatestNewsDb(): LatestNewsDatabase = Room.databaseBuilder(
        context, LatestNewsDatabase::class.java, "latestNews-database"
    ).build()

}