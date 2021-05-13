package github.sun5066.weather

import com.google.gson.annotations.SerializedName

data class RestAreaWeather (
    @SerializedName(value = "unitName") val unitName: String,
    @SerializedName(value = "weatherContents") val weatherContents: String,
    @SerializedName(value = "addrName") val addrName: String
)