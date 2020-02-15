package io.keepcoding.eh_ho.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import androidx.room.Room
import com.android.volley.NetworkError
import com.android.volley.Request
import com.android.volley.ServerError
import io.keepcoding.eh_ho.BuildConfig
import io.keepcoding.eh_ho.data.service.*
import io.keepcoding.eh_ho.database.TopicDatabase
import io.keepcoding.eh_ho.database.TopicEntity
import io.keepcoding.eh_ho.domain.*
import org.json.JSONObject
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.concurrent.thread
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call


@SuppressLint("StaticFieldLeak")
object TopicsRepo {

    lateinit var db: TopicDatabase
    lateinit var ctx: Context
    lateinit var retroF: Retrofit

    fun getTopics(
        onSuccess: (List<Topic>) -> Unit,
        onError: (RequestError) -> Unit
    ) {

        val username = UserRepo.getUsername(ctx)
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
                    onError.invoke(RequestError(messageId = io.keepcoding.eh_ho.R.string.error_invalid_response))
            },
            {
                it.printStackTrace()
                if (it is NetworkError) {
                    val handler = Handler(ctx.mainLooper)
                    thread {
                        val latestNewList = db.topicDao().getTopics()
                        val runnable = Runnable {
                            if (latestNewList.isNotEmpty()) {
                                onSuccess(latestNewList.toModel())
                            } else {
                                onError.invoke(
                                    RequestError(
                                        messageId = io.keepcoding.eh_ho.R.string.error_network
                                    )
                                )
                            }
                        }
                        handler.post(runnable)
                    }

                } else
                    onError.invoke(RequestError(it))
            })

        ApiRequestQueue.getRequesteQueue(ctx)
            .add(request)
    }

    fun getTopicsWithRetrofit(
        onSuccess: (List<Topic>) -> Unit,
        onError: (RequestError) -> Unit
    ) {

        thread {

            val syncResponse: Response<ListTopic> = retroF.create(TopicService::class.java)
                .getTopicRetrof()
                .execute()

            val handler = Handler(ctx.mainLooper)
            val runnable = Runnable {

                if (syncResponse.isSuccessful) {
                    syncResponse.body().takeIf { it != null }
                        ?.let {
                            onSuccess.invoke(it.topic_list.topics) }
                        ?: run { onError(RequestError(message = "Body is null")) }
                } else {
                    onError(RequestError(message = syncResponse.errorBody()?.string()))
                }
            }
            handler.post(runnable)
        }
    }

    suspend fun getTopicsWithRetrofitAndCourrutines(): Response<ListTopic> {
        var result = retroF.create(TopicService::class.java).getTopicRetrofCour()
        return result
    }

    suspend fun getLatestNewsWithRetrofitAndCourrutines(): Response<ListLatestNews> {
        var result = retroF.create(TopicService::class.java).getLatestNewsRetrofCour()
        return result
    }


    fun createTopic(
        model: CreateTopicModel,
        onSuccess: (CreateTopicModel) -> Unit,
        onError: (RequestError) -> Unit
    ) {
        val username = UserRepo.getUsername(ctx)
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
                    onError.invoke(RequestError(messageId = io.keepcoding.eh_ho.R.string.error_invalid_response))
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
                            messageId = io.keepcoding.eh_ho.R.string.error_network
                        )
                    )
                else
                    onError.invoke(RequestError(it))
            }
        )

        ApiRequestQueue.getRequesteQueue(ctx)
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