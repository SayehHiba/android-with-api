package com.example.isidoc.RestAPI

import com.example.isidoc.model.*
import com.google.gson.JsonObject
import okhttp3.Cookie
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*


interface ApiService {



    @FormUrlEncoded
    @POST("loginservice")
    suspend fun Login(@Field("username") username: String?, @Field("password") password: String?,@Field("redirect") red: Boolean): Response<ResponseBody>

    @GET("API/system/session/unusedId")
    suspend fun getId(@Header("Cookie") cookie:String):Response<UserId>

    @GET("portal/custom-page/API/bpm/category")
    suspend fun getProcessCategory(@Header("Cookie") cookie:String,@Query("p") p:String):Response<ArrayList<Category>>

    @GET("API/bpm/process")
    suspend fun getProcessList(@Header("Cookie") cookie:String,@Query("f") f:String):Response<ArrayList<Process>>

    @GET("API/bpm/process/{idProcess}/contract")
    suspend fun getProcessContract(@Header("Cookie") cookie:String,@Path("idProcess") idProcess:String):Response<Contract>


    @Headers("Content-Type:application/json","Content-Length:<calculated when request is sent>","Host:<calculated when request is sent>","Accept-Encoding:gzip, deflate, br")
    @POST("API/bpm/process/{idProcess}/instantiation")
    suspend fun submit(@Header("Cookie") cookie:String,@Header("X-Bonita-API-Token") tokenHeader:String,@Path("idProcess") idProcess:String,@Body body:String): Response<submit>


    @GET("API/form/mapping?c=1&p=0&f=processDefinitionId={idProcess}&f=type=PROCESS_START")
    suspend fun startForm(@Header("Cookie") cookie:String,@Path("idProcess") idUser:String):Response<startForm>
}