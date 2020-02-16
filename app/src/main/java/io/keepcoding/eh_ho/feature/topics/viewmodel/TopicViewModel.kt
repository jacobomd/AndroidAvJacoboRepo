package io.keepcoding.eh_ho.feature.topics.viewmodel

import android.content.Context
import android.os.Handler
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.NetworkError
import com.google.android.material.snackbar.Snackbar
import io.keepcoding.eh_ho.R
import io.keepcoding.eh_ho.data.repository.LatestNewsRepo
import io.keepcoding.eh_ho.data.repository.TopicsRepo
import io.keepcoding.eh_ho.data.repository.UserRepo
import io.keepcoding.eh_ho.data.service.RequestError
import io.keepcoding.eh_ho.database.TopicDatabase
import io.keepcoding.eh_ho.database.TopicEntity
import io.keepcoding.eh_ho.domain.*
import io.keepcoding.eh_ho.feature.topics.view.state.TopicManagementState
import io.keepcoding.eh_ho.feature.topics.view.ui.TopicsActivity
import kotlinx.coroutines.*
import retrofit2.HttpException
import retrofit2.Response
import kotlin.coroutines.CoroutineContext


import javax.inject.Inject
import kotlin.concurrent.thread


class TopicViewModel @Inject constructor(
    private val topicsRepo: TopicsRepo,
    private val latestNewsRepo: LatestNewsRepo,
    private val db: TopicDatabase
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

            try {

                launch(Dispatchers.Main) {

                    val response: Response<ListTopic> = job.await()

                    if (response.isSuccessful) {
                        response.body().takeIf { it != null }
                            ?.let {
                                _topicManagementState.value =
                                    TopicManagementState.LoadTopicList(topicList = it.topic_list.topics)

                                //  IMPLEMENTACION QUE NO FUNCIONA DE LA PERSISTENCIA DENTRO DE RETROFIT-COROUTINES
/*                            thread {
                                db.topicDao()
                                    .insertAll(it.topic_list.topics.toEntity())
                            }*/
                            }
                            ?: run {
                                _topicManagementState.value =
                                    TopicManagementState.RequestErrorReported(RequestError(message = "Body is null"))

                                //  IMPLEMENTACION QUE NO FUNCIONA DE LA PERSISTENCIA DENTRO DE RETROFIT-COROUTINES
/*                            if (it is NetworkError) {
                                val handler = Handler(context.mainLooper)
                                thread {
                                    val latestNewList = db.topicDao().getTopics()
                                    val runnable = Runnable {
                                        if (latestNewList.isNotEmpty()) {
                                            _topicManagementState.value =
                                                TopicManagementState.LoadTopicList(topicList = latestNewList.toModel())
                                        } else {
                                            RequestError(messageId = R.string.error_network)
                                        }
                                    }
                                    handler.post(runnable)
                                }

                            } else {
                                RequestError(messageId = R.string.error_network)
                            }*/

                            }
                    } else {
                        RequestError(message = "Something error to happened")
                    }
                }
            } catch (e: HttpException) {
                Toast.makeText(
                    context.applicationContext,
                    "error ${e.message()}",
                    Toast.LENGTH_LONG
                ).show()
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

    fun onCreateTopicOptionClicked(context: Context, createTopicModel: CreateTopicModel) {
        if (isValidCreateTopicForm(model = createTopicModel)) {
            _topicManagementState.value = TopicManagementState.CreateTopicLoading
            context?.let {

                val job = async {
                    val a =
                        topicsRepo.createTopicWithRetrofitAndCourrutines(model = createTopicModel)
                    a
                }
                launch(Dispatchers.Main) {
                    val response: Response<CreateTopicModel> = job.await()

                    if (response.isSuccessful) {
                        response.body().takeIf { it != null }
                            ?.let {
                                _topicManagementState.value =
                                    TopicManagementState.CreateTopicCompleted
                                _topicManagementState.value = TopicManagementState
                                    .TopicCreatedSuccessfully(msg = context.getString(R.string.message_topic_created))

                            }
                            ?: run {
                                _topicManagementState.value =
                                    TopicManagementState.RequestErrorReported(RequestError(message = "Body is null"))
                            }
                    } else {
                        //RequestError(message = "Something error to happened")
                        _topicManagementState.value =
                            TopicManagementState.CreateTopicFormErrorReported(
                                errorMsg = getCreateTopicFormError(
                                    context,
                                    createTopicModel
                                )
                            )
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
        } else {
            _topicManagementState.value = TopicManagementState.CreateTopicFormErrorReported(
                errorMsg = getCreateTopicFormError(context, createTopicModel)
            )
        }
    }

    /*fun onCreateTopicOptionClicked(context: Context, createTopicModel: CreateTopicModel) {
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

    }*/

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

            try {

                launch(Dispatchers.Main) {

                    val response: Response<ListTopic> = job.await()

                    if (response.isSuccessful) {
                        response.body().takeIf { it != null }
                            ?.let {
                                _topicManagementState.value =
                                    TopicManagementState.LoadTopicList(topicList = it.topic_list.topics)

                                //  IMPLEMENTACION QUE NO FUNCIONA DE LA PERSISTENCIA DENTRO DE RETROFIT-COROUTINES
/*                            thread {
                                db.topicDao()
                                    .insertAll(it.topic_list.topics.toEntity())
                            }*/
                            }
                            ?: run {
                                _topicManagementState.value =
                                    TopicManagementState.RequestErrorReported(RequestError(message = "Body is null"))

                                //  IMPLEMENTACION QUE NO FUNCIONA DE LA PERSISTENCIA DENTRO DE RETROFIT-COROUTINES
                                /* if (it is NetworkError) {
                                 val handler = Handler(context.mainLooper)
                                 thread {
                                     val latestNewList = db.topicDao().getTopics()
                                     val runnable = Runnable {
                                         if (latestNewList.isNotEmpty()) {
                                             _topicManagementState.value =
                                                 TopicManagementState.LoadTopicList(topicList = latestNewList.toModel())
                                         } else {
                                             RequestError(messageId = R.string.error_network)
                                         }
                                     }
                                     handler.post(runnable)
                                 }

                             } else {
                                 RequestError(messageId = R.string.error_network)
                             }*/
                            }
                    } else {
                        RequestError(message = "Something error to happened")
                    }
                }
            } catch (e: HttpException) {
                Toast.makeText(
                    it,
                    "error ${e.message()}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun List<TopicEntity>.toModel(): List<Topic> = map { it.toModel() }

    private fun TopicEntity.toModel(): Topic =
        Topic(
            id = topicId,
            title = title,
            posts = posts,
            views = views
        )

    private fun List<Topic>.toEntity(): List<TopicEntity> = map { it.toEntity() }

    private fun Topic.toEntity(): TopicEntity = TopicEntity(
        topicId = id,
        title = title,
        date = date.toString(),
        posts = posts,
        views = views
    )


}