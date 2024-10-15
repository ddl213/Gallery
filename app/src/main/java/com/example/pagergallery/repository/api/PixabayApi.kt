package com.example.pagergallery.repository.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PixabayApi {
    @GET("api/?key=26859587-d3219a1c9296afdc92716fccf")
    suspend fun getData(@Query("q") q : String?,@Query("page") page : Int,@Query("per_page") size : Int,@Query("image_type") type : String,@Query("order") order : String) : Response<Pixabay>
}