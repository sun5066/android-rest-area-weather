package github.sun5066.weather

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface RestAreaService {

    // key=test type=json sdate=20210504 stdHour=19
    @GET("openapi/restinfo/restWeatherList?")
    fun getList(
        @Query("stdHour") stdHour: String,
        @Query("sdate") sdate: String,
        @Query("type") type: String,
        @Query("key") key: String
    ): Call<ResponseBody>
}