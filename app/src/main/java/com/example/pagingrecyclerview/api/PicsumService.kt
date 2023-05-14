package com.example.pagingrecyclerview.api

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

/**
 * @Description:
 * @Author: zouji
 * @CreateDate: 2023/5/7 14:40
 */
interface PicsumService {
    @GET("v2/list")
    suspend fun getPicsumList(): Response<ArrayList<PicsumItem>>

    companion object {
        private const val BASE_URL = "https://picsum.photos/"

        // 直接在内部提供了RetrofitClient
        fun create(): PicsumService = kotlin.run {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(PicsumService::class.java)   //传入API对象
        }
    }
}

data class PicsumItem(
    var id: String?,
    var author: String?,
    var width: Int?,
    var height: Int?,
    var download_url: String?
)