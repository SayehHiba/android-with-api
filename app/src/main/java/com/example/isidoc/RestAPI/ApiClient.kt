package com.example.isidoc.RestAPI

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class ApiClient {
    companion object {

        var BASE_URL = "http://digitalisi.tn:8080/bonita/"
        private var builder: Retrofit.Builder? = null
        fun RetrofitSingleton(): Retrofit? {
            if(builder==null){
                builder=Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create())
            }
            return builder?.build()
        }
    }



}