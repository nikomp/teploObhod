package ru.bingosoft.teploObhod.api

import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*
import ru.bingosoft.teploObhod.models.Models

interface ApiService {
    @POST("/defaultauthentication/auth/login")
    @Headers("Content-Type: application/json")
    fun getAuthentication(
        @Body lp: RequestBody
    ): Single<Models.Uuid>

    @POST("/accesseditor/login/authorize")
    @Headers("Content-Type: application/json")
    fun getAuthorization(
        @Body uuid: RequestBody
    ): Single<Models.Token>

    /*@GET("procs/androidAPI.php")
    fun getListRoute(
        @Query("action") action: String
    ): Single<Models.OrderList>*/

    @POST("/registryservice/plugins/execute/UnloadEmployeeDataForTheCurrentDayFromByPassCommand")
    @Headers("Content-Type: application/json")
    fun getListRoute(
        @Body empty: RequestBody
    ): Single<Models.DataList>

    @GET("procs/androidAPI.php")
    fun getInfoAboutCurrentUser(
        @Query("action") action: String
    ): Single<Models.User>

    @GET("procs/androidAPI.php")
    fun getCheckups(
        @Query("action") action: String
    ): Single<Models.CheckupList>

    @GET("procs/androidAPI.php")
    fun getCheckupGuide(
        @Query("action") action: String
    ): Single<Models.CheckupGuideList>

    @GET("procs/androidAPI.php")
    fun sendMessageToAdmin(
        @Query("action") action: String,
        @Query("codeMessage") codeMessage: Int
    ): Single<Models.CheckupGuideList>

    @POST("procs/androidAPI.php")
    @Multipart
    fun doReverseSync(
        @Part("action") action: RequestBody?,
        @Part("jsonData") jsonData: RequestBody?,
        @Part file: MultipartBody.Part?
        //@Part("filemap") filemap: RequestBody?
    ): Single<Models.SimpleMsg>


    @POST("procs/androidAPI.php")
    @Multipart
    fun sendTrackingUserLocation(
        @Part("action") action: RequestBody?,
        @Part("jsonData") jsonData: RequestBody?
    ): Single<Models.SimpleMsg>

    @POST("procs/androidAPI.php")
    @FormUrlEncoded
    fun saveGCMToken(
        @Field("action") action: String,
        @Field("token") token: String
    ): Single<Models.SimpleMsg>

    @GET("procs/androidAPI.php")
    fun saveUserLocation(
        @Query("action") action: String,
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): Single<Models.SimpleMsg>
}