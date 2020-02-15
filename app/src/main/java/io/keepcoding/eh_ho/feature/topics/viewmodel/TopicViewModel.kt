package io.keepcoding.eh_ho.feature.topics.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.keepcoding.eh_ho.R
import io.keepcoding.eh_ho.data.repository.LatestNewsRepo
import io.keepcoding.eh_ho.data.repository.TopicsRepo
import io.keepcoding.eh_ho.data.repository.UserRepo
import io.keepcoding.eh_ho.data.service.RequestError
import io.keepcoding.eh_ho.domain.*
import io.keepcoding.eh_ho.feature.topics.view.state.TopicManagementState
import kotlinx.coroutines.*
import retrofit2.Response
import kotlin.coroutines.CoroutineContext


import javax.inject.Inject


class TopicViewModel @Inject constructor(
    private val topicsRepo: TopicsRepo,
    private val latestNewsRepo: LatestNewsRepo
) : ViewModel(), CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.IO


    private lateinit var _topicManagementState: MutableLiveData<TopicManagementState>
    val topicManagementState: LiveData<TopicManagementState>
        get() {
            if (!::_topicManagementState.isInitialized) {
                _topicManagementState = MutableLiveData()
            }
            return _topicManagementState
        }

    /*fun onViewCreatedWithNoSavedData(context: Context?) {
        _topicManagementState.value = TopicManagementState.Loading
        context?.let {
            // topicsRepo.getTopics(
            topicsRepo.getTopicsWithRetrofit(
                //topicsRepo.getTopicsWithRetrofitAndCourrutines(
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

    fun onViewCreatedWithNoSavedData(context: Context?) {

        _topicManagementState.value = TopicManagementState.Loading
        context?.let {

            val job = async {
                val a =
                    topicsRepo.getTopicsWithRetrofitAndCourrutines()
                a
            }

            launch(Dispatchers.Main) {

                val response: Response<ListTopic> = job.await()

                if (response.isSuccessful) {
                    response.body().takeIf { it != null }
                        ?.let {
                            _topicManagementState.value =
                                TopicManagementState.LoadTopicList(topicList = it.topic_list.topics)
                        }
                        ?: run {
                            _topicManagementState.value =
                                TopicManagementState.RequestErrorReported(RequestError(message = "Body is null"))
                        }
                } else {
                    RequestError(message = "Something error to happened")
                }
            }
        }
    }

    fun onTopicsFragmentResumed(context: Context?) {
        _topicManagementState.value = TopicManagementState.Loading
        fetchTopicsAndHandleResponse(context = context)

    }

    fun onTopicSelected(topic: Topic) {
        _topicManagementState.value = TopicManagementState.NavigateToPostsOfTopic(topic)
    }

    fun onRetryButtonClicked(context: Context?) {
        _topicManagementState.value = TopicManagementState.Loading
        fetchTopicsAndHandleResponse(context = context)

    }


    fun onSwipeRefreshLayoutClicked(context: Context?) {
        _topicManagementState.value = TopicManagementState.Loading
        fetchTopicsAndHandleResponse(context = context)
    }

    fun onCreateTopicButtonClicked() {
        _topicManagementState.value = TopicManagementState.NavigateToCreateTopic
    }

    fun onLatestNewResumed(context: Context?) {
        _topicManagementState.value = TopicManagementState.Loading
        fetchLatestNewsAndHandleResponse(context = context)
    }

    fun onSwipeRefreshLayoutLatestNewsClicked(context: Context?) {
        _topicManagementState.value = TopicManagementState.Loading
        fetchLatestNewsAndHandleResponse(context = context)
    }


    fun onLatestNewSelected(latestNews: LatestNews) {
        _topicManagementState.value = TopicManagementState.Loading
        _topicManagementState.value =
            TopicManagementState.NavigateToPostsOfTopicFromLatestNews(latestNews)
    }


    fun onLogOutOptionClicked(context: Context) {
        UserRepo.logOut(context = context)
        _topicManagementState.value = TopicManagementState.NavigateToLoginAndExit

    }

    fun onCreateTopicOptionClicked(context: Context, createTopicModel: CreateTopicModel) {
        if (isValidCreateTopicForm(model = createTopicModel)) {
            _topicManagementState.value = TopicManagementState.CreateTopicLoading
            topicsRepo.createTopic(
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
        with(model) {
            title.isNotEmpty() && content.isNotEmpty()
        }


    private fun fetchTopicsAndHandleResponse(context: Context?) {
        context?.let {

            val job = async {
                val a =
                    topicsRepo.getTopicsWithRetrofitAndCourrutines()
                a
            }

            launch(Dispatchers.Main) {

                val response: Response<ListTopic> = job.await()

                if (response.isSuccessful) {
                    response.body().takeIf { it != null }
                        ?.let {
                            _topicManagementState.value =
                                TopicManagementState.LoadTopicList(topicList = it.topic_list.topics)
                        }
                        ?: run {
                            _topicManagementState.value =
                                TopicManagementState.RequestErrorReported(RequestError(message = "Body is null"))
                        }
                } else {
                    RequestError(message = "Something error to happened")
                }
            }
        }
    }

    private fun fetchLatestNewsAndHandleResponse(context: Context?) {

        context?.let {

            val job = async {
                val a =
                    topicsRepo.getLatestNewsWithRetrofitAndCourrutines()
                a
            }

            launch(Dispatchers.Main) {

                val response: Response<ListLatestNews> = job.await()

                if (response.isSuccessful) {
                    response.body().takeIf { it != null }
                        ?.let {
                            _topicManagementState.value =
                                TopicManagementState.LoadLatestNewsList(latestNewsList = it.latest_posts)
                        }
                        ?: run {
                            _topicManagementState.value =
                                TopicManagementState.RequestErrorReported(RequestError(message = "Body is null"))
                        }
                } else {
                    RequestError(message = "Something error to happened")
                }
            }

            /*latestNewsRepo.getLatestNews(

                { latestNews ->
                    _topicManagementState.value =
                        TopicManagementState.LoadLatestNewsList(latestNewsList = latestNews)
                },
                { error ->
                    _topicManagementState.value =
                        TopicManagementState.RequestErrorReported(requestError = error)
                }
            )*/
        }
    }


}