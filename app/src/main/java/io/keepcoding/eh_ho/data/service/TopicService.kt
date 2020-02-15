package io.keepcoding.eh_ho.data.service


import io.keepcoding.eh_ho.domain.ListLatestNews
import io.keepcoding.eh_ho.domain.ListTopic
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers

interface TopicService {

    /*@Headers(
        "Apy-Key:699667f923e65fac39b632b0d9b2db0d9ee40f9da15480ad5a4bcb3c1b095b7a",
        "Api-Username:jacobomd"
    )*/

    @GET("latest.json")
    fun getTopicRetrof(): Call<ListTopic>

    @GET("latest.json")
    suspend fun getTopicRetrofCour(): Response<ListTopic>

    @GET("posts.json")
    suspend fun getLatestNewsRetrofCour(): Response<ListLatestNews>
}