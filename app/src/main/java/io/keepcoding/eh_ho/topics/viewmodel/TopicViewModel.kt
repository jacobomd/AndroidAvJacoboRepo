package io.keepcoding.eh_ho.topics.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.keepcoding.eh_ho.R
import io.keepcoding.eh_ho.data.CreateTopicModel
import io.keepcoding.eh_ho.data.Topic
import io.keepcoding.eh_ho.data.TopicsRepo
import io.keepcoding.eh_ho.data.UserRepo

class TopicViewModel : ViewModel() {

   /* private lateinit var _topicManagementState: MutableLiveData<TopicManagementState>
    val topicManagementState: LiveData<TopicManagementState>
        get() {
            if (!::_topicManagementState.isInitialized) {
                _topicManagementState = MutableLiveData()
            }
            return _topicManagementState
        }

    fun onViewCreatedWithNoSavedData(context: Context) {
        _topicManagementState.value = TopicManagementState.Loading
        TopicsRepo.getTopics(
            context,
            { topics ->
                _topicManagementState.value =
                    TopicManagementState.LoadTopicList(topicList = topics)
            },
            { error ->
                _topicManagementState.value =
                    TopicManagementState.RequestErrorReported(requestError = error)
            })
    }

    fun onTopicsFragmentResumed(context: Context?) {
        _topicManagementState.value = TopicManagementState.Loading
        fetchTopicsAndHandleResponse(context = context)
    }

    fun onRetryButtonClicked(context: Context?) {
        _topicManagementState.value = TopicManagementState.Loading
        fetchTopicsAndHandleResponse(context = context)
    }

    // Navigate to topic detail view and display associated data
    fun onTopicSelected(topic: Topic) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun onCreateTopicButtonClicked() {
        _topicManagementState.value = TopicManagementState.NavigateToCreateTopic
    }

    fun onLogOutOptionClicked(context: Context) {
        UserRepo.logOut(context = context)
        _topicManagementState.value = TopicManagementState.NavigateToLoginAndExit
    }

    fun onCreateTopicOptionClicked(context: Context, createTopicModel: CreateTopicModel) {
        if (isValidCreateTopicForm(model = createTopicModel)) {
            _topicManagementState.value = TopicManagementState.CreateTopicLoading
            TopicsRepo.createTopic(
                context = context,
                model = createTopicModel,
                onSuccess = { topicModel ->
                    _topicManagementState.value = TopicManagementState.CreateTopicCompleted
                    if (topicModel == createTopicModel) {
                        _topicManagementState.value =
                            TopicManagementState.TopicCreatedSuccessfully(msg = context.getString(R.string.message_topic_created))
                    } else {
                        _topicManagementState.value =
                            TopicManagementState.TopicNotCreated(createError = context.getString(R.string.error_topic_not_created))
                    }
                },
                onError = { error ->
                    _topicManagementState.value = TopicManagementState.CreateTopicCompleted
                    _topicManagementState.value =
                        TopicManagementState.RequestErrorReported(requestError = error)
                }
            )
        } else {
            _topicManagementState.value = TopicManagementState.CreateTopicFormErrorReported(
                errorMsg = getCreateTopicFormError(context, createTopicModel)
            )
        }
    }

    private fun getCreateTopicFormError(context: Context, model: CreateTopicModel): String =
        with(model) {
            when {
                title.isEmpty() -> context.getString(R.string.error_title_empty)
                content.isEmpty() -> context.getString(R.string.error_content_empty)
                else -> context.getString(R.string.error_unknown)
            }
        }


    private fun isValidCreateTopicForm(model: CreateTopicModel): Boolean =
        with(model) { title.isNotEmpty() && content.isNotEmpty() }

    private fun fetchTopicsAndHandleResponse(context: Context?) {
        context?.let {
            TopicsRepo.getTopics(it,
                { topics ->
                    _topicManagementState.value =
                        TopicManagementState.LoadTopicList(topicList = topics)
                },
                { error ->
                    _topicManagementState.value =
                        TopicManagementState.RequestErrorReported(requestError = error)
                })
        }
    }*/

}