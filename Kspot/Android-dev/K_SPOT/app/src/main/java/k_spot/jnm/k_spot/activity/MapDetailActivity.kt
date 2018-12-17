package k_spot.jnm.k_spot.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageButton
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.ui.PlaceAutocomplete
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment
import com.google.android.gms.location.places.ui.PlaceSelectionListener
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import k_spot.jnm.k_spot.R
import k_spot.jnm.k_spot.adapter.InfoWindowAdapter
import k_spot.jnm.k_spot.db.SharedPreferenceController
import kotlinx.android.synthetic.main.activity_map_detail.*
import org.jetbrains.anko.locationManager
import org.jetbrains.anko.toast
import java.util.*

class MapDetailActivity : AppCompatActivity(), OnMapReadyCallback {
    val REQUEST_GPS_PERMISSIONS_REQUEST_CODE = 1003
    val PLACE_AUTOCOMPLETE_REQUEST_CODE = 1007
    private lateinit var mMap: GoogleMap

    private val fusedLocationClientInfoStatus: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }
    val placeAutocompleteFragment: PlaceAutocompleteFragment by lazy {
        fragmentManager.findFragmentById(R.id.fm_map_detail_act_place_autocomplete) as PlaceAutocompleteFragment
    }
    var currentLatitude: Double = 37.498146
    var currentLongitude: Double = 127.027653

    val mMarkerOption: MarkerOptions by lazy {
        MarkerOptions().apply {
            title("강남역")
            snippet("서울의 핫 플레이스")
            icon(BitmapDescriptorFactory.fromResource(R.drawable.google_map_circle))
        }
    }
    lateinit var mMarker: Marker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_detail)
        setStatusBarColor()
        setSearchBar()
        //placeAutoComlete()

        if (SharedPreferenceController.getFlag(this) == "0"){
            tv_map_act_title.text = "이 위치로 장소 설정"
        } else {
            tv_map_act_title.text = "Set this spot"
        }


        //gps 안 잡히면 초기 강남
        if (!checkPermissions()) {
            startLocationPermissionRequest()
        } else {
            getLastLocation()
        }

        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        setClickListener()


    }

    private fun setSearchBar() {
        placeAutocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place?) {
                currentLatitude = place!!.latLng.latitude
                currentLongitude = place!!.latLng.longitude
                placeAutocompleteFragment.setText("")

                val autoCompleteFragmentView : ViewGroup = placeAutocompleteFragment.view as ViewGroup
                val clearButtonId : Int = com.google.android.gms.location.places.R.id.place_autocomplete_clear_button
                val mClearAutoCompleteButton : View = autoCompleteFragmentView.findViewById(clearButtonId)
                mClearAutoCompleteButton.callOnClick()

                addNewMarker()
            }

            override fun onError(p0: Status?) {
            }
        })


    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnInfoWindowClickListener {
            if (it.isInfoWindowShown) {
                it.hideInfoWindow()
            }
        }


        mMap.uiSettings.isMapToolbarEnabled = true


        mMap.setInfoWindowAdapter(InfoWindowAdapter(this))

        val address = Geocoder(this, Locale.KOREAN)
                .getFromLocation(currentLatitude, currentLongitude, 2)


        mMarkerOption.position(LatLng(currentLatitude, currentLongitude)).snippet(address[0].getAddressLine(0).toString())

        mMarker = mMap.addMarker(mMarkerOption)

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(currentLatitude, currentLongitude), 15.0f))
    }


    private fun setClickListener() {
        btn_map_detail_act_gps.setOnClickListener {
            getLastLocation()
        }

        btn_map_page_setting_spot.setOnClickListener {
            val intent = Intent().apply {
                putExtra("latitude", currentLatitude)
                putExtra("longitude", currentLongitude)
            }
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    private fun setStatusBarColor() {
        val view: View? = window.decorView
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val w = window
            view!!.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }
    }

    private fun checkPermissions() =
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

    private fun startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                REQUEST_GPS_PERMISSIONS_REQUEST_CODE)
    }

    private fun getLastLocation() {
        val isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        //로딩돌리기

        if (isGPSEnabled || isNetworkEnabled) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                fusedLocationClientInfoStatus.lastLocation.addOnCompleteListener {
                    if (it.isSuccessful && it.result != null) {
                        currentLatitude = it.result.latitude
                        currentLongitude = it.result.longitude

                        addNewMarker()
                    }
                }
            }
        } else {
            if (SharedPreferenceController.getFlag(this) == "0"){
                toast("GPS를 체크해주세요.")
            } else {
                toast("please Check your GPS")
            }

        }
    }

    private fun addNewMarker() {
        mMarker.remove()

        val address = Geocoder(this, Locale.KOREAN)
                .getFromLocation(currentLatitude, currentLongitude, 2)

        mMarkerOption.position(LatLng(currentLatitude, currentLongitude))
                .snippet(address[0].getAddressLine(0).toString()).title(address[0].subLocality)
        mMarker = mMap.addMarker(mMarkerOption)

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(currentLatitude, currentLongitude), 15.0f))
    }

    private fun placeAutoComlete() {
        try {
            val intent = PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(this)
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE)
        } catch (e: GooglePlayServicesRepairableException) {
            Log.e("auto !! ", e.toString())
        } catch (e: GooglePlayServicesNotAvailableException) {
            Log.e("auto !! ", e.toString())
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                val place: Place = PlaceAutocomplete.getPlace(this, data)
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {

            } else if (resultCode == RESULT_CANCELED) {

            }
        }
    }
}
