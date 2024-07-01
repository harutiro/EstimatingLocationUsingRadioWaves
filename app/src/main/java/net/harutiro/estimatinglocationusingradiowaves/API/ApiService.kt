package net.harutiro.estimatinglocationusingradiowaves.API

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
interface ApiService {
    @Multipart
    @POST("/api/signals/submit")
    fun submitSignalData(
        @Part wifiData: MultipartBody.Part?,
        @Part bleData: MultipartBody.Part?
    ): Call<ResponseBody>
}
