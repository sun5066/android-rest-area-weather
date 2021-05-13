package github.sun5066.weather

import android.annotation.SuppressLint
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var mGeocoder: Geocoder
    private val mRestAreaNameList: MutableList<RestAreaWeather> by lazy { mutableListOf<RestAreaWeather>() }
    private val mAddressNameList: MutableList<String> by lazy { resources.getStringArray(R.array.area_list).toCollection(ArrayList()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap?) {
        mMap = map!!
        mGeocoder = Geocoder(this)

        val seoul = LatLng(37.56, 126.97)

        val markerOption = MarkerOptions()
        markerOption.position(seoul)
        markerOption.title("서울")
        markerOption.snippet("한국의 수도")
        mMap.addMarker(markerOption)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(seoul, 10F))

        mMap.setOnMapClickListener(this)
        getRestAreaApi()
    }

    override fun onMapClick(location: LatLng?) {

    }

    private fun getRestAreaApi() {
        val retrofit = Retrofit.Builder()
            .baseUrl(RestAreaConstants.URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(RestAreaService::class.java)
        service
            .getList("6514009844", "json", "20210512", "10")
            .enqueue(object : Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {}

                @SuppressLint("CheckResult")
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    Observable.just(response.body()!!.string())
                        .observeOn(Schedulers.io())
                        .subscribeOn(Schedulers.single())
                        .subscribe {
                            Log.d("123", "it: $it")

                            val json = JSONObject(it)
                            val list = json.getJSONArray("list")

                            for (i in 0 until list.length()) {
                                val obj = list[i] as JSONObject
                                val restAreaWeather =
                                    Gson().fromJson(obj.toString(), RestAreaWeather::class.java)
                                mRestAreaNameList.add(restAreaWeather)
                            }

                            Single.just(mRestAreaNameList)
                                .subscribeOn(Schedulers.single())
                                .map { Log.d("123", "Single.map() >> mRestAreaNameList: $mRestAreaNameList") }
                                .subscribe(Consumer { setMarker() })
                        }
                }
            })
    }

    private fun setMarker() {
        Log.d("123", "setMarker():::")

        runOnUiThread {
            mRestAreaNameList.forEach {
                val unitName = it.unitName
                val weatherContents = it.weatherContents

                Log.d("123", "name: ${it.addrName}")
                if (unitName.isNotEmpty()) {
                    val addressList = mGeocoder.getFromLocationName(unitName, 1)

                    addressList.forEach { address ->
                        val location = LatLng(address.latitude, address.longitude)
                        MarkerOptions().apply {
                            position(location)
                            title(unitName)
                            snippet(weatherContents)
                            mMap.addMarker(this)
                        }
                    }
                }
            }
        }
    }

}