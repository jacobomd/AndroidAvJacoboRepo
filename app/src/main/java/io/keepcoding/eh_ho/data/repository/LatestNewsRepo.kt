package io.keepcoding.eh_ho.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import androidx.room.Room
import com.android.volley.NetworkError
import com.android.volley.Request
import io.keepcoding.eh_ho.R
import io.keepcoding.eh_ho.data.service.ApiRequestQueue
import io.keepcoding.eh_ho.data.service.ApiRoutes
import io.keepcoding.eh_ho.data.service.RequestError
import io.keepcoding.eh_ho.data.service.UserRequest
import io.keepcoding.eh_ho.database.LatestNewsDatabase
import io.keepcoding.eh_ho.database.LatestNewsEntity
import io.keepcoding.eh_ho.domain.LatestNews
import kotlin.concurrent.thread

@SuppressLint("StaticFieldLeak")
object LatestNewsRepo {

    lateinit var db: LatestNewsDatabase
    lateinit var ctx: Context

    fun getLatestNews(
        onSuccess: (List<LatestNews>) -> Unit,
        onError: (RequestError) -> Unit
    ) {

        val username = UserRepo.getUsername(ctx)
        val request = UserRequest(
            username,
            Request.Method.GET,
            ApiRoutes.getLatestNews(),
            null,
            {
                it?.let {
                    onSuccess.invoke(
                        LatestNews.parseLatestNews(
                            it
                        )
                    )
                    thread {
                        db.latestNewsDao().insertAll(
                            LatestNews.parseLatestNews(it).toEntity()
                        )
                    }
                }

                if (it == null)
                    onError.invoke(RequestError(messageId = R.string.error_invalid_response))
            },
            {
                it.printStackTrace()
                if (it is NetworkError) {
                    val handler = Handler(ctx.mainLooper)
                    thread {
                        val latestNewList = db.latestNewsDao().getLatestNews()
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

                } else {
                    onError.invoke(RequestError(it))
                }
            })

        ApiRequestQueue.getRequesteQueue(ctx)
            .add(request)
    }
}

private fun List<LatestNewsEntity>.toModel(): List<LatestNews> = map { it.toModel() }

private fun LatestNewsEntity.toModel(): LatestNews =
    LatestNews(

        topic_title = topic_title,
        topic_id = topic_id,
        topic_slug = topic_slug,
        username = username,
        cooked = cooked,
        created_at = created_at,
        post_number = post_number,
        score = score
    )


private fun List<LatestNews>.toEntity(): List<LatestNewsEntity> =
    map { it.toEntity() }


private fun LatestNews.toEntity(): LatestNewsEntity  =
    LatestNewsEntity(
        topic_id = topic_id,
        topic_title = topic_title,
        topic_slug = topic_slug,
        cooked = cooked,
        username = username,
        created_at = created_at,
        post_number = post_number,
        score = score
    )

