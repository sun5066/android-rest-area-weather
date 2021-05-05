package github.sun5066.weather

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class MainActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapClickListener,
    GoogleMap.OnMapLongClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var mGeocoder: Geocoder
    private lateinit var mRestAreaList: MutableList<Address>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap?) {
        mMap = map!!
        mGeocoder = Geocoder(this)

        try {
//            mRestAreaList = mGeocoder.getFromLocationName("고창고인돌휴게소", 10)
//            Log.d("123", "mRestAreaList: $mRestAreaList")
        } catch (e: IOException) {
            e.printStackTrace()
        }
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

    override fun onMapLongClick(p0: LatLng?) {

    }


    private fun getRestAreaApi() {
        val retrofit = Retrofit.Builder()
            .baseUrl(RestAreaConstants.URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(RestAreaService::class.java)
        service
            .getList("6514009844", "json", "20210501", "10")
            .enqueue(object : Callback<ResponseBody> {

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.d("123", "실패")
                }

                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    Log.d("123", "call")
                    Log.d("123", "response: $response")
                }
            })
    }
}