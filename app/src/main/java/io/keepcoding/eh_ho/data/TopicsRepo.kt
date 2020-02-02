package io.keepcoding.eh_ho.data

import android.content.Context
import com.android.volley.NetworkError
import com.android.volley.Request
import com.android.volley.ServerError
import io.keepcoding.eh_ho.R
import org.json.JSONObject
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*

object TopicsRepo {

    fun getTopics(
        context: Context,
        onSuccess: (List<Topic>) -> Unit,
        onError: (RequestError) -> Unit
    ) {
        val username = UserRepo.getUsername(context)
        val request = UserRequest(
            username,
            Request.Method.GET,
            ApiRoutes.getTopics(),
            null,
            {
                it?.let {
                    onSuccess.invoke(Topic.parseTopics(it))
                }

                if (it == null)
                    onError.invoke(RequestError(messageId = R.string.error_invalid_response))
            },
            {
                it.printStackTrace()
                if (it is NetworkError)
                    onError.invoke(RequestError(messageId = R.string.error_network))
                else
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

                    onError.invoke(RequestError(it, message = errorMessage))


                }

                else if (it is NetworkError)
                    onError.invoke(RequestError(it, messageId = R.string.error_network))
                else
                    onError.invoke(RequestError(it))
            }
        )

        ApiRequestQueue.getRequesteQueue(context)
            .add(request)
    }

}

/*
   //////// METODOS PARA REALIZAR DATOS DUMMIES ANTES DE TRAERLOS DE LA API //////

    val topics: MutableList<Topic> = mutableListOf()

*//*    get() {
        if (field.isEmpty()) {
            field.addAll(dummyTopics())
        }
        return field
    }*//*

*//*    fun dummyTopics () : List<Topic> {

        val dummyList: MutableList<Topic> = mutableListOf()

        for (i in 1..50) {
            val topic: Topic = Topic(title = "Title $i", content = "Content $i")
            dummyList.add(topic)

        }

        return  dummyList
    }*//*

    fun getTopic(id: String) = topics.find { it.id == id }

    fun addTopic (title: String, content: String) {
        topics.add(
            Topic(
                title = title
            ))
    }


    private fun formatDate(date: String): Date {
        val formatter = SimpleDateFormat("dd/MM/yyyy hh:mm:ss")

        return formatter.parse(date)
            ?: throw IllegalArgumentException("Date $date has a format incorrect, try again with the format dd/MM/yyyy hh:mm:ss")
    }


    private fun dummyTopics (count: Int = 50) : List<Topic> {

        return (1..count).map {
            Topic(title = "Title $it")
        }

    }*/