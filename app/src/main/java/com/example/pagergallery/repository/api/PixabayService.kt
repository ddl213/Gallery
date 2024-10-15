package com.example.pagergallery.repository.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PixabayService {
    private var url = "https://pixabay.com/"
    fun createPixabayService() : PixabayApi{
        return Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PixabayApi::class.java)
    }
}