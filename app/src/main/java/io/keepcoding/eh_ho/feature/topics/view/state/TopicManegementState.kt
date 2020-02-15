package io.keepcoding.eh_ho.feature.topics.view.state

import io.keepcoding.eh_ho.data.service.RequestError
import io.keepcoding.eh_ho.domain.LatestNews
import io.keepcoding.eh_ho.domain.Topic


sealed class TopicManagementState {
    object Loading : TopicManagementState()
    class LoadTopicList(val topicList: List<Topic>) : TopicManagementState()
    class LoadLatestNewsList(val latestNewsList: List<LatestNews>) : TopicManagementState()
    class RequestErrorReported(val requestError: RequestError) : TopicManagementState()
    class CreateTopicFormErrorReported(val errorMsg: String) : TopicManagementState()
    object NavigateToCreateTopic : TopicManagementState()
    class NavigateToPostsOfTopic(val topic: Topic) : TopicManagementState()
    object NavigateToLoginAndExit : TopicManagementState()
    object CreateTopicLoading : TopicManagementState()
    object CreateTopicCompleted : TopicManagementState()
    class TopicCreatedSuccessfully(val msg: String) : TopicManagementState()
    class TopicNotCreated(val createError: String) : TopicManagementState()
    class NavigateToPostsOfTopicFromLatestNews(val latestNews: LatestNews) : TopicManagementState()


}

