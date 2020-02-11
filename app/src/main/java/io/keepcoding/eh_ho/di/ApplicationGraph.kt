package io.keepcoding.eh_ho.di

import dagger.Component
import io.keepcoding.eh_ho.feature.topics.view.ui.TopicsActivity
import io.keepcoding.eh_ho.feature.topics.viewmodel.TopicViewModel
import javax.inject.Singleton


// @Component makes Dagger create a graph of dependencies

@Singleton
@Component(modules = [TopicsModule::class, LatestNewsModule::class, UtilsModule::class])
interface ApplicationGraph {

    // Add functions whose return value indicate what can be provided from this container
    fun getTopicViewModel(): TopicViewModel

    // Add here as well functions whose input argument is the entity in which Dagger can add any
    // dependency you want
    fun inject(topicsActivity: TopicsActivity)

}
