package io.keepcoding.eh_ho.topics.view.state

import io.keepcoding.eh_ho.data.RequestError
import io.keepcoding.eh_ho.data.Topic

sealed class TopicManagementState {
    object Loading : TopicManagementState()
    class LoadTopicList(val topicList: List<Topic>) : TopicManagementState()
    class RequestErrorReported(val requestError: RequestError) : TopicManagementState()
    class CreateTopicFormErrorReported(val errorMsg: String) : TopicManagementState()
    object NavigateToCreateTopic : TopicManagementState()
    class NavigateToPostsOfTopic(val topic: Topic) : TopicManagementState()
    object NavigateToLoginAndExit : TopicManagementState()
    object CreateTopicLoading : TopicManagementState()
    object CreateTopicCompleted : TopicManagementState()
    class TopicCreatedSuccessfully(val msg: String) : TopicManagementState()
    class TopicNotCreated(val createError: String) : TopicManagementState()

}

