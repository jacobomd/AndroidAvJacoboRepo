package io.keepcoding.eh_ho.data.repository

import android.content.Context
import android.os.Handler
import androidx.room.Room
import com.android.volley.NetworkError
import com.android.volley.Request
import com.android.volley.ServerError
import io.keepcoding.eh_ho.R
import io.keepcoding.eh_ho.data.service.ApiRequestQueue
import io.keepcoding.eh_ho.data.service.ApiRoutes
import io.keepcoding.eh_ho.data.service.RequestError
import io.keepcoding.eh_ho.data.service.UserRequest
import io.keepcoding.eh_ho.database.TopicDatabase
import io.keepcoding.eh_ho.database.TopicEntity
import io.keepcoding.eh_ho.domain.CreateTopicModel
import io.keepcoding.eh_ho.domain.Topic
import org.json.JSONObject
import kotlin.concurrent.thread

object TopicsRepo {

    fun getTopics(
        context: Context,
        onSuccess: (List<Topic>) -> Unit,
        onError: (RequestError) -> Unit
    ) {

        val db: TopicDatabase = Room.databaseBuilder(
            context,
            TopicDatabase::class.java, "topic-database"
        ).build()
        val username = UserRepo.getUsername(context)
        val request = UserRequest(
            username,
            Request.Method.GET,
            ApiRoutes.getTopics(),
            null,
            {
                it?.let {
                    onSuccess.invoke(Topic.parseTopics(it))
                    thread {
                        db.topicDao()
                            .insertAll(Topic.parseTopics(it).toEntity())
                    }
                }

                if (it == null)
                    onError.invoke(RequestError(messageId = R.string.error_invalid_response))
            },
            {
                it.printStackTrace()
                if (it is NetworkError) {
                    val handler = Handler(context.mainLooper)
                    thread {
                        val latestNewList = db.topicDao().getTopics()
                        val runnable = Runnable {
                            if (latestNewList.isNotEmpty()) {
                                onSuccess(latestNewList.toModel())
                            } else {
                                onError.invoke(
                                    RequestError(
                                        messageId = R.string.error_network
                                    )
                                )
                            }
                        }
                        handler.post(runnable)
                    }

                } else
                    onError.invoke(RequestError(it))
            })

        ApiRequestQueue.getRequesteQueue(context)
            .add(request)
    }


    fun createTopic(
        context: Context,
        model: CreateTopicModel,
        onSuccess: (CreateTopicModel) -> Unit,
        onError: (RequestError) -> Unit
    ) {
        val username = UserRepo.getUsername(context)
        val request = UserRequest(
            username,
            Request.Method.POST,
            ApiRoutes.createTopic(),
            model.toJson(),
            {
                it?.let {
                    onSuccess.invoke(model)
                }

                if (it == null)
                    onError.invoke(RequestError(messageId = R.string.error_invalid_response))
            },
            {
                it.printStackTrace()

                if (it is ServerError && it.networkResponse.statusCode == 422) {
                    val body = String(it.networkResponse.data, Charsets.UTF_8)
                    val jsonError = JSONObject(body)
                    val errors = jsonError.getJSONArray("errors")
                    var errorMessage = ""

                    for (i in 0 until errors.length()) {
                        errorMessage += "${errors[i]} "
                    }

                    onError.invoke(
                        RequestError(
                            it,
                            message = errorMessage
                        )
                    )

                } else if (it is NetworkError)
                    onError.invoke(
                        RequestError(
                            it,
                            messageId = R.string.error_network
                        )
                    )
                else
                    onError.invoke(RequestError(it))
            }
        )

        ApiRequestQueue.getRequesteQueue(context)
            .add(request)
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