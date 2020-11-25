package com.soldemom.navermapactivity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnSuccessListener
import com.soldemom.navermapactivity.testFrag.TestVM
import kotlinx.android.synthetic.main.activity_splash.*


class SplashActivity : AppCompatActivity() {
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private var longitude = 0.0
    private var latitude = 0.0
    lateinit var viewModel : TestVM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val animation = AnimationUtils.loadAnimation(this,R.anim.alpha)
        splash_title.animation = animation
//        viewModel = ViewModelProvider(this).get(TestVM::class.java)
        viewModel = ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(TestVM::class.java)


    }

    override fun onStart() {
        super.onStart()
        checkLocationPermission()
    }

    private fun checkLocationPermission() {
        val accessLocation = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        if (accessLocation == PackageManager.PERMISSION_GRANTED) {
            checkLocationSetting()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), GPS_UTIL_LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == GPS_UTIL_LOCATION_PERMISSION_REQUEST_CODE) {
            for (i in permissions.indices) {
                if (Manifest.permission.ACCESS_FINE_LOCATION == permissions[i]) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        checkLocationSetting()
                    } else {
                        val builder = AlertDialog.Builder(this)
                        builder.setTitle("위치 권한이 꺼져있습니다.")
                        builder.setMessage("[권한] 설정에서 위치 권한을 허용해야 합니다.")
                        builder.setPositiveButton("설정으로 가기") { dialog, which ->
                            val intent = Intent()
                            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            val uri = Uri.fromParts("package", packageName, null)
                            intent.data = uri
                            startActivity(intent)
                        }.setNegativeButton("종료") { dialog, which -> finish() }
                        val alert = builder.create()
                        alert.show()
                    }
                    break
                }
            }
        }
    }

    private fun checkLocationSetting() {
        //필요로하는 정보를 정의하는 객체
        locationRequest = LocationRequest.create()
        // 우선순위
        locationRequest.setPriority(DEFAULT_LOCATION_REQUEST_PRIORITY)
        // 위치를 요청하는 간격
        locationRequest.setInterval(DEFAULT_LOCATION_REQUEST_INTERVAL)
        // 위치 요청 간격2
        locationRequest.setFastestInterval(DEFAULT_LOCATION_REQUEST_FAST_INTERVAL)
        val settingsClient = LocationServices.getSettingsClient(this)
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest).setAlwaysShow(true)
        settingsClient.checkLocationSettings(builder.build())
            .addOnSuccessListener(this, object : OnSuccessListener<LocationSettingsResponse> {
                @SuppressLint("MissingPermission")
                override fun onSuccess(locationSettingsResponse: LocationSettingsResponse) {
                    fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this@SplashActivity)
                    fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null)
                }
            }) //안드로이드 서비스에 위치 서비스가 활성화 되어있지 않을 경우 failure가 뜸
            .addOnFailureListener(this@SplashActivity) { e ->
                val statusCode = (e as ApiException).statusCode
                when (statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        val rae = e as ResolvableApiException
                        // 환경설정을 통해 위치기능을 킬 수 있는 팝업창을 띄워줌.
                        rae.startResolutionForResult(this@SplashActivity, GPS_UTIL_LOCATION_RESOLUTION_REQUEST_CODE)
                    } catch (sie: IntentSender.SendIntentException) {
                        Log.w(TAG, "unable to start resolution for result due to " + sie.localizedMessage)
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                        val errorMessage = "location settings are inadequate, and cannot be fixed here. Fix in Settings."
                        Log.e(TAG, errorMessage)
                    }
                }
            }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GPS_UTIL_LOCATION_RESOLUTION_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                checkLocationSetting()
            } else {
                finish()
            }
        }
    }

    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            longitude = locationResult.lastLocation.longitude
            latitude = locationResult.lastLocation.latitude
            fusedLocationProviderClient!!.removeLocationUpdates(this)
            val intent = Intent(this@SplashActivity, LoginActivity::class.java)
//            intent.putExtra("latitude", latitude)
//            intent.putExtra("longitude", longitude)
            viewModel.latitude = latitude
            viewModel.longitude = longitude
            Log.d("위경도","Splash에서 위도 : ${viewModel.latitude} , 경도 : ${viewModel.longitude} ")

            startActivity(intent)
            finish()
        }

        override fun onLocationAvailability(locationAvailability: LocationAvailability) {
            super.onLocationAvailability(locationAvailability)
            Log.i(TAG, "onLocationAvailability - $locationAvailability")
        }
    }

    companion object {
        private val TAG = SplashActivity::class.java.simpleName
        private const val GPS_UTIL_LOCATION_PERMISSION_REQUEST_CODE = 100
        private const val GPS_UTIL_LOCATION_RESOLUTION_REQUEST_CODE = 101

        //폰의 전력을 가장 효율적으로 사용하면서 원하는 정도의 위치를 얻어올 수 있는 우선순위
        // 가장 높은것은 LocationRequest.PRIORITY_HIGH_ACCURACY (네비같은데에서 사용)
        const val DEFAULT_LOCATION_REQUEST_PRIORITY = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY

        // 위치 변경 시 10초 ~ 20초 사이로 위치정보 갱신할 수 있도록 함.
        const val DEFAULT_LOCATION_REQUEST_INTERVAL = 20000L
        const val DEFAULT_LOCATION_REQUEST_FAST_INTERVAL = 10000L
    }
}