package io.keepcoding.eh_ho.topics.view.state

import io.keepcoding.eh_ho.data.RequestError
import io.keepcoding.eh_ho.data.Topic

sealed class TopicManagementState {
    object Loading : TopicManagementState()
    class LoadTopicList(val topicList: List<Topic>) : TopicManagementState()
    class RequestErrorReported(val requestError: RequestError) : TopicManagementState()
}

