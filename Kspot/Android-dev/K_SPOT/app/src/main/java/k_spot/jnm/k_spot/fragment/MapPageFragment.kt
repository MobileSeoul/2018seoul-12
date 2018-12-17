package k_spot.jnm.k_spot.fragment

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.widget.NestedScrollView
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import k_spot.jnm.k_spot.Get.GetMapPageSpotDataResponse
import k_spot.jnm.k_spot.Get.MapPageSpotData
import k_spot.jnm.k_spot.Network.ApplicationController
import k_spot.jnm.k_spot.R
import k_spot.jnm.k_spot.SearchActivity
import k_spot.jnm.k_spot.activity.MainActivity
import k_spot.jnm.k_spot.activity.MapDetailActivity
import k_spot.jnm.k_spot.adapter.MapPageRecyclerViewAdapter
import k_spot.jnm.k_spot.data.FilterOptionData
import k_spot.jnm.k_spot.data.SpotLanguageName
import k_spot.jnm.k_spot.db.SharedPreferenceController
import kotlinx.android.synthetic.main.fragment_map_page.*
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.startActivityForResult
import org.jetbrains.anko.support.v4.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapPageFragment : Fragment() {
    private val MAP_ACTIVITY_CODE = 1000
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 7778

    private val locationManager: LocationManager by lazy {
        context!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    private val mapPageRecyclerViewAdapter: MapPageRecyclerViewAdapter by lazy {
        MapPageRecyclerViewAdapter(context!!, spotDataListFromGPS)
    }

    private val spotDataListFromGPS: ArrayList<MapPageSpotData> by lazy {
        ArrayList<MapPageSpotData>()
    }

    private val fusedLocationClientInfoStatus: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context!!)
    }

    private val filterOption: FilterOptionData by lazy {
        FilterOptionData(0, 1.0, 37.498146, 127.027653, 1, 1, 1, 1, 1)
    }
    private val filterOptionTemp: FilterOptionData by lazy {
        FilterOptionData(0, 1.0, 37.498146, 127.027653, 1, 1, 1, 1, 1)
    }

    private val distanceArrayList: ArrayList<Double> by lazy {
        arrayListOf<Double>(1.0, 0.5, 0.3)
    }
    private var currentDistanceOptionIndex: Int = 0

    private lateinit var currentSpotName: String

    private val spotName: ArrayList<SpotLanguageName> by lazy {
        arrayListOf(
                SpotLanguageName("내 주변", "Around me"),
                SpotLanguageName("강서구", "gangseogu"),
                SpotLanguageName("양천구", "yangcheongu"),
                SpotLanguageName("구로구", "gurogu"),
                SpotLanguageName("영등포구", "yeongdeungpogu"),
                SpotLanguageName("금천구", "geumcheongu"),
                SpotLanguageName("관악구", "gwanakgu"),
                SpotLanguageName("동작구", "dongjakgu"),
                SpotLanguageName("용산구", "yongsangu"),
                SpotLanguageName("마포구", "mapogu"),
                SpotLanguageName("서대문구", "seodaemungu"),
                SpotLanguageName("강남구", "gangnamgu"),
                SpotLanguageName("서초구", "seochogu"),
                SpotLanguageName("중구", "junggu"),
                SpotLanguageName("은평구", "eunpyeonggu"),
                SpotLanguageName("종로구", "jongrogu"),
                SpotLanguageName("성북구", "seongbukgu"),
                SpotLanguageName("강북구", "gangbukgu"),
                SpotLanguageName("도봉구", "dobonggu"),
                SpotLanguageName("노원구", "nowongu"),
                SpotLanguageName("중랑구", "jungranggu"),
                SpotLanguageName("동대문구", "dongdaemungu"),
                SpotLanguageName("성동구", "seongdonggu"),
                SpotLanguageName("광진구", "gwangjingu"),
                SpotLanguageName("송파구", "songpagu"),
                SpotLanguageName("강동구", "gangdonggu")

        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_map_page, container, false)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setRecyclerViewAdapter()
        setTranslateLanguage()
        //번역관련
        if (SharedPreferenceController.getFlag(context!!) == "0") {
            currentSpotName = "강남구"
        } else {
            currentSpotName = "gangnamgu"
        }

        setTranslateMapTextBtn()
        setTranslateFilterSource()
        setTranslateText()

        //
        setFilterBtnVisible()
        setFilterOption()
        filterOptionListener()

        setMapAddressGuClickListener()

        btn_map_page_my_spot.setOnClickListener {
            startActivityForResult<MapDetailActivity>(MAP_ACTIVITY_CODE)
        }

        //일단 초기지역 후 -> 내 위치
        requestSpotDataFromSpot(currentSpotName)

        if (!checkPermissions()) {
            startLocationPermissionRequest()
        } else {
            getLastLocation()
        }

        btn_map_page_search.setOnClickListener {
            startActivity<SearchActivity>()
        }

        setTranslateLanguage()
    }


    private fun setTranslateLanguage() {
        btn_map_page_translation.setOnClickListener {
            (activity as MainActivity).changeMainActivityLanguage()
        }
    }
    fun translateMapPageLanguage(){
        currentSpotName = findTranslateWord()

        setTranslateMapTextBtn()
        setTranslateFilterSource()
        setTranslateText()

        //통신도 다시해주기
        if (currentSpotName == "내 주변" || currentSpotName == "Around me"){
            requestSpotDataFromGPS(currentSpotName)
        } else {
            requestSpotDataFromSpot(currentSpotName)
        }
    }

    //시간나면 애니메이션 처리
    private fun setFilterBtnVisible() {
        ns_map_page_scroll.setOnScrollChangeListener { v: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
            if (oldScrollY < scrollY) {
                btn_map_page_open_filtering.visibility = View.INVISIBLE
            } else {
                btn_map_page_open_filtering.visibility = View.VISIBLE
            }
        }
    }

    private fun setFilterOption() {
        //정렬
        when (filterOption.order_option) {
            0 -> {
                tv_map_page_filter_is_popular.setTextColor(Color.parseColor("#40D39F"))
                tv_map_page_filter_is_new.setTextColor(Color.parseColor("#E0E0E0"))
            }
            1 -> {
                tv_map_page_filter_is_popular.setTextColor(Color.parseColor("#E0E0E0"))
                tv_map_page_filter_is_new.setTextColor(Color.parseColor("#40D39F"))
            }
        }
        //거리
        if (SharedPreferenceController.getFlag(context!!) == "0") {
            when (filterOption.distance) {
                1.0 -> tv_map_page_filter_distance.text = "1.0Km까지 설정"
                0.5 -> tv_map_page_filter_distance.text = "0.5Km까지 설정"
                else -> tv_map_page_filter_distance.text = "0.3Km까지 설정"
            }
        } else {
            when (filterOption.distance) {
                1.0 -> tv_map_page_filter_distance.text = "Set up to 1.0km"
                0.5 -> tv_map_page_filter_distance.text = "Set up to 0.5km"
                else -> tv_map_page_filter_distance.text = "Set up to 0.3km"
            }
        }

        setInitFilterImage()
    }
    private fun setInitFilterImage(){
        //레스토랑
        when (filterOption.is_food) {
            1 -> {
                if (SharedPreferenceController.getFlag(context!!) == "0") {
                    btn_map_page_filter_is_restaurant.setImageResource(R.drawable.filter_restaurant_btn_green)
                } else {
                    btn_map_page_filter_is_restaurant.setImageResource(R.drawable.filter_food_icon_green_en)
                }
            }
            0 -> {
                if (SharedPreferenceController.getFlag(context!!) == "0") {
                    btn_map_page_filter_is_restaurant.setImageResource(R.drawable.filter_restaurant_btn_gray)
                } else {
                    btn_map_page_filter_is_restaurant.setImageResource(R.drawable.filter_food_icon_en)
                }

            }
        }
        //카페
        when (filterOption.is_cafe) {
            1 -> {
                if (SharedPreferenceController.getFlag(context!!) == "0") {
                    btn_map_page_filter_is_cafe.setImageResource(R.drawable.filter_cafe_btn_green)
                } else {
                    btn_map_page_filter_is_cafe.setImageResource(R.drawable.filter_cafe_icon_green_en)
                }
            }
            0 -> {
                if (SharedPreferenceController.getFlag(context!!) == "0") {
                    btn_map_page_filter_is_cafe.setImageResource(R.drawable.filter_cafe_btn_gray)
                } else {
                    btn_map_page_filter_is_cafe.setImageResource(R.drawable.filter_cafe_icon_en)
                }
            }
        }
        //핫 플레이스
        when (filterOption.is_sights) {
            1 -> {
                if (SharedPreferenceController.getFlag(context!!) == "0") {
                    btn_map_page_filter_is_hot_place.setImageResource(R.drawable.filter_hotplace_btn_green)
                } else {
                    btn_map_page_filter_is_hot_place.setImageResource(R.drawable.filter_hotsight_icon_green_en)
                }
            }
            0 -> {
                if (SharedPreferenceController.getFlag(context!!) == "0") {
                    btn_map_page_filter_is_hot_place.setImageResource(R.drawable.filter_hotplace_btn_gray)
                } else {
                    btn_map_page_filter_is_hot_place.setImageResource(R.drawable.filter_hotsight_icon_en)
                }

            }
        }
        //이벤트
        when (filterOption.is_event) {
            1 -> {
                if (SharedPreferenceController.getFlag(context!!) == "0") {
                    btn_map_page_filter_is_event.setImageResource(R.drawable.filter_event_btn_green)
                } else {
                    btn_map_page_filter_is_event.setImageResource(R.drawable.filter_event_icon_green_en)
                }
            }
            0 -> {
                if (SharedPreferenceController.getFlag(context!!) == "0") {
                    btn_map_page_filter_is_event.setImageResource(R.drawable.filter_event_btn_gray)
                } else {
                    btn_map_page_filter_is_event.setImageResource(R.drawable.filter_event_icon_en)
                }
            }
        }
        when (filterOption.is_etc) {
            1 -> {
                if (SharedPreferenceController.getFlag(context!!) == "0") {
                    btn_map_page_filter_is_etc.setImageResource(R.drawable.filter_etc_btn_green)
                } else {
                    btn_map_page_filter_is_etc.setImageResource(R.drawable.filter_etc_icon_green_en)
                }
            }
            0 -> {
                if (SharedPreferenceController.getFlag(context!!) == "0") {
                    btn_map_page_filter_is_etc.setImageResource(R.drawable.filter_etc_btn_gray)
                } else {
                    btn_map_page_filter_is_etc.setImageResource(R.drawable.filter_etc_icon_en)
                }
            }
        }
    }


    private fun filterOptionListener() {
        btn_map_page_filter_distance_left.setOnClickListener {
            if (currentDistanceOptionIndex == 2) {
                currentDistanceOptionIndex = 0
            } else {
                currentDistanceOptionIndex++
            }
            if (SharedPreferenceController.getFlag(context!!) == "0") {
                tv_map_page_filter_distance.text = "${distanceArrayList[currentDistanceOptionIndex]}Km까지 설정"
            } else {
                tv_map_page_filter_distance.text = "Set up to ${distanceArrayList[currentDistanceOptionIndex]}km"
            }
            filterOptionTemp.distance = distanceArrayList[currentDistanceOptionIndex]
        }
        btn_map_page_filter_distance_right.setOnClickListener {
            if (currentDistanceOptionIndex == 0) {
                currentDistanceOptionIndex = 2
            } else {
                currentDistanceOptionIndex--
            }
            if (SharedPreferenceController.getFlag(context!!) == "0") {
                tv_map_page_filter_distance.text = "${distanceArrayList[currentDistanceOptionIndex]}Km까지 설정"
            } else {
                tv_map_page_filter_distance.text = "Set up to ${distanceArrayList[currentDistanceOptionIndex]}km"
            }
            filterOptionTemp.distance = distanceArrayList[currentDistanceOptionIndex]
        }


        btn_map_page_filter_is_popular.setOnClickListener {
            filterOptionTemp.order_option = 0
            tv_map_page_filter_is_popular.setTextColor(Color.parseColor("#40D39F"))
            tv_map_page_filter_is_new.setTextColor(Color.parseColor("#E0E0E0"))
        }
        btn_map_page_filter_is_new.setOnClickListener {
            filterOptionTemp.order_option = 1
            tv_map_page_filter_is_popular.setTextColor(Color.parseColor("#E0E0E0"))
            tv_map_page_filter_is_new.setTextColor(Color.parseColor("#40D39F"))
        }

        btn_map_page_filter_is_restaurant.setOnClickListener {
            if (filterOptionTemp.is_food == 1) {
                if (SharedPreferenceController.getFlag(context!!) == "0") {
                    btn_map_page_filter_is_restaurant.setImageResource(R.drawable.filter_restaurant_btn_gray)
                } else {
                    btn_map_page_filter_is_restaurant.setImageResource(R.drawable.filter_food_icon_en)
                }
                filterOptionTemp.is_food = 0
            } else {
                if (SharedPreferenceController.getFlag(context!!) == "0") {
                    btn_map_page_filter_is_restaurant.setImageResource(R.drawable.filter_restaurant_btn_green)
                } else {
                    btn_map_page_filter_is_restaurant.setImageResource(R.drawable.filter_food_icon_green_en)
                }
                filterOptionTemp.is_food = 1
            }
        }

        btn_map_page_filter_is_cafe.setOnClickListener {
            if (filterOptionTemp.is_cafe == 1) {
                if (SharedPreferenceController.getFlag(context!!) == "0") {
                    btn_map_page_filter_is_cafe.setImageResource(R.drawable.filter_cafe_btn_gray)
                } else {
                    btn_map_page_filter_is_cafe.setImageResource(R.drawable.filter_cafe_icon_en)
                }
                filterOptionTemp.is_cafe = 0
            } else {
                if (SharedPreferenceController.getFlag(context!!) == "0") {
                    btn_map_page_filter_is_cafe.setImageResource(R.drawable.filter_cafe_btn_green)
                } else {
                    btn_map_page_filter_is_cafe.setImageResource(R.drawable.filter_cafe_icon_green_en)
                }
                filterOptionTemp.is_cafe = 1
            }
        }

        btn_map_page_filter_is_hot_place.setOnClickListener {
            if (filterOptionTemp.is_sights == 1) {

                if (SharedPreferenceController.getFlag(context!!) == "0") {
                    btn_map_page_filter_is_hot_place.setImageResource(R.drawable.filter_hotplace_btn_gray)
                } else {
                    btn_map_page_filter_is_hot_place.setImageResource(R.drawable.filter_hotsight_icon_en)
                }

                filterOptionTemp.is_sights = 0
            } else {
                if (SharedPreferenceController.getFlag(context!!) == "0") {
                    btn_map_page_filter_is_hot_place.setImageResource(R.drawable.filter_hotplace_btn_green)
                } else {
                    btn_map_page_filter_is_hot_place.setImageResource(R.drawable.filter_hotsight_icon_green_en)
                }

                filterOptionTemp.is_sights = 1
            }
        }

        btn_map_page_filter_is_event.setOnClickListener {
            if (filterOptionTemp.is_event == 1) {
                if (SharedPreferenceController.getFlag(context!!) == "0") {
                    btn_map_page_filter_is_event.setImageResource(R.drawable.filter_event_btn_gray)
                } else {
                    btn_map_page_filter_is_event.setImageResource(R.drawable.filter_event_icon_en)
                }
                filterOptionTemp.is_event = 0
            } else {
                if (SharedPreferenceController.getFlag(context!!) == "0") {
                    btn_map_page_filter_is_event.setImageResource(R.drawable.filter_event_btn_green)
                } else {
                    btn_map_page_filter_is_event.setImageResource(R.drawable.filter_event_icon_green_en)
                }
                filterOptionTemp.is_event = 1
            }
        }

        btn_map_page_filter_is_etc.setOnClickListener {
            if (filterOptionTemp.is_etc == 1) {

                if (SharedPreferenceController.getFlag(context!!) == "0") {
                    btn_map_page_filter_is_etc.setImageResource(R.drawable.filter_etc_btn_gray)
                } else {
                    btn_map_page_filter_is_etc.setImageResource(R.drawable.filter_etc_icon_en)
                }
                filterOptionTemp.is_etc = 0
            } else {
                if (SharedPreferenceController.getFlag(context!!) == "0") {
                    btn_map_page_filter_is_etc.setImageResource(R.drawable.filter_etc_btn_green)
                } else {
                    btn_map_page_filter_is_etc.setImageResource(R.drawable.filter_etc_icon_green_en)
                }
                filterOptionTemp.is_etc = 1
            }
        }

        btn_map_page_filter_apply_btn.setOnClickListener {
            filterOption.run {
                order_option = filterOptionTemp.order_option
                distance = filterOptionTemp.distance
                is_food = filterOptionTemp.is_food
                is_cafe = filterOptionTemp.is_cafe
                is_sights = filterOptionTemp.is_sights
                is_event = filterOptionTemp.is_event
                is_etc = filterOptionTemp.is_etc
            }

            btn_map_page_close_filtering.callOnClick()
            if (SharedPreferenceController.getFlag(context!!) == "0") {
                toast("필터가 적용되었습니다.\n원하는 위치를 선택해주세요.")
            } else {
                toast("filter is applied.\nplease choose a spot")
            }
            mapSpotFilterDownAnimation()
        }

        btn_map_page_close_filtering.setOnClickListener {
            filterOptionTemp.run {
                order_option = filterOption.order_option
                distance = filterOption.distance
                is_food = filterOption.is_food
                is_cafe = filterOption.is_cafe
                is_sights = filterOption.is_sights
                is_event = filterOption.is_event
                is_etc = filterOption.is_etc
            }

            when (filterOption.distance) {
                1.0 -> currentDistanceOptionIndex = 0
                0.5 -> currentDistanceOptionIndex = 1
                0.3 -> currentDistanceOptionIndex = 2
            }

            setFilterOption()
            mapSpotFilterDownAnimation()
            (activity as MainActivity).showBottomPageTab()
        }

        btn_map_page_open_filtering.setOnClickListener {
            (activity as MainActivity).hideBottomPageTab()

            mapSpotFilterUpAnimation()
//            ll_map_page_filtering.visibility = View.VISIBLE
//            btn_map_page_close_filtering.visibility = View.VISIBLE
        }

    }

    // 탭바를 오른쪽 방송 탭 밑으로 이동!
    private fun mapSpotFilterDownAnimation(){
        val anim = AnimationUtils
                .loadAnimation(context,
                        R.anim.search_spot_view_more_act_down_anim)
        rl_map_page_open_filtering_rl.visibility = View.GONE
        rl_map_page_open_filtering_backgound.visibility = View.GONE
        rl_map_page_open_filtering_rl.startAnimation(anim)
    }

    private fun mapSpotFilterUpAnimation(){
        val anim = AnimationUtils
                .loadAnimation(context,
                        R.anim.search_spot_view_more_act_anim)
        rl_map_page_open_filtering_rl.visibility = View.VISIBLE
        rl_map_page_open_filtering_backgound.visibility = View.VISIBLE
        rl_map_page_open_filtering_rl.startAnimation(anim)
    }


    private fun getLastLocation() {
        val isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        //로딩돌리기

        if (isGPSEnabled || isNetworkEnabled) {
            if (ActivityCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (SharedPreferenceController.getFlag(context!!) == "0") {
                    toast("탐색 중..")
                } else {
                    toast("Searching..")
                }
                fusedLocationClientInfoStatus.lastLocation.addOnCompleteListener {
                    if (it.isSuccessful && it.result != null) {
                        filterOption.latitude = it.result.latitude
                        filterOption.longitude = it.result.longitude

                        if (SharedPreferenceController.getFlag(context!!) == "0") {
                            currentSpotName = "내 주변"
                        } else {
                            currentSpotName = "Around me"
                        }
                        requestSpotDataFromGPS(currentSpotName)

                    }
                }
            }
        } else {
            if (SharedPreferenceController.getFlag(context!!) == "0") {
                toast("GPS를 체크해주세요")
            } else {
                currentSpotName = "Please Check your GPS"
            }
        }
    }

    private fun requestSpotDataFromSpot(address_gu: String) {
        val networkService = ApplicationController.instance.networkService
        val getMapPageResponseFromSpot = networkService.getMapPageResponseFromSpot(SharedPreferenceController.getFlag(context!!).toInt(), SharedPreferenceController.getAuthorization(context!!),
                address_gu, filterOption.order_option, filterOption.is_food, filterOption.is_cafe, filterOption.is_sights, filterOption.is_event, filterOption.is_etc)
        getMapPageResponseFromSpot.enqueue(object : Callback<GetMapPageSpotDataResponse> {
            override fun onFailure(call: Call<GetMapPageSpotDataResponse>?, t: Throwable?) {
                Log.e("맵 기반 통신 실패", t.toString())
            }

            override fun onResponse(call: Call<GetMapPageSpotDataResponse>?, responseSpot: Response<GetMapPageSpotDataResponse>?) {
                responseSpot?.let {
                    if (responseSpot.isSuccessful) {
                        if (spotDataListFromGPS.isNotEmpty()) {
                            spotDataListFromGPS.clear()
                            //(rv_map_page_my_around_k_spot.adapter as MapPageRecyclerViewAdapter).clearDataList()
                            spotDataListFromGPS.addAll(responseSpot.body()!!.data)
                        } else {
                            spotDataListFromGPS.addAll(responseSpot.body()!!.data)
                        }
                        tv_map_page_subtitle.text = "$address_gu K-spot"
                        setRecyclerViewAdapter()
                    }
                }
            }
        })

    }


    private fun requestSpotDataFromGPS(title: String) {
        val networkService = ApplicationController.instance.networkService
        val getMapPageDataFromGPSResponse = networkService.getMapPageDataFromGPSResponse(SharedPreferenceController.getFlag(context!!).toInt(), SharedPreferenceController.getAuthorization(context!!),
                filterOption.distance, filterOption.latitude, filterOption.longitude, filterOption.is_food, filterOption.is_cafe, filterOption.is_sights, filterOption.is_event, filterOption.is_etc)
        getMapPageDataFromGPSResponse.enqueue(object : Callback<GetMapPageSpotDataResponse> {
            override fun onFailure(call: Call<GetMapPageSpotDataResponse>?, t: Throwable?) {
                Log.e("위치 기반 통신 실패", t.toString())
            }

            override fun onResponse(call: Call<GetMapPageSpotDataResponse>?, responseSpot: Response<GetMapPageSpotDataResponse>?) {
                responseSpot?.let {
                    if (responseSpot.isSuccessful) {
                        if (spotDataListFromGPS.isNotEmpty()) {
                            spotDataListFromGPS.clear()
                            spotDataListFromGPS.addAll(responseSpot.body()!!.data)
                        } else {
                            spotDataListFromGPS.addAll(responseSpot.body()!!.data)
                        }
                        tv_map_page_subtitle.text = "$title K-spot"
                        setRecyclerViewAdapter()
                    }
                }
            }
        })

    }

    private fun setRecyclerViewAdapter() {
        rv_map_page_my_around_k_spot.layoutManager = LinearLayoutManager(context)
        rv_map_page_my_around_k_spot.adapter = mapPageRecyclerViewAdapter

        if (spotDataListFromGPS.size >= 1){
            cardview_map_page_no_data_message.visibility = View.GONE
        } else {
            cardview_map_page_no_data_message.visibility = View.VISIBLE
        }
        if (SharedPreferenceController.getFlag(context!!) == "0"){
            tv_map_page_no_data_message.text = "주변에 K-spot이 없습니다 :("
        } else {
            tv_map_page_no_data_message.text = "Around, there is no K-spot :("
        }
    }

    private fun checkPermissions() =
            ActivityCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED


    private fun startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                REQUEST_PERMISSIONS_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MAP_ACTIVITY_CODE) {
            if (resultCode == RESULT_OK) {
                filterOption.latitude = data!!.getDoubleExtra("latitude", 37.498146)
                filterOption.longitude = data!!.getDoubleExtra("longitude", 127.027653)
                if (SharedPreferenceController.getFlag(context!!) == "0") {
                    currentSpotName = "내 주변"
                } else {
                    currentSpotName = "Around me"
                }
                requestSpotDataFromGPS(currentSpotName)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            when {
                (grantResults[0] == PackageManager.PERMISSION_GRANTED) -> getLastLocation()
                else -> {
                    if (SharedPreferenceController.getFlag(context!!) == "0") {
                        toast("권한 요청 실패")
                    } else {
                        toast("Permission request failed")
                    }

                }
            }
        }

    }


    private fun setMapAddressGuClickListener() {
        btn_map_page_spot_gangseo.setOnClickListener {
            if (SharedPreferenceController.getFlag(context!!) == "0") {
                currentSpotName = "강서구"
            } else {
                currentSpotName = "gangseogu"
            }
            requestSpotDataFromSpot(currentSpotName)
        }
        btn_map_page_spot_yangcheon.setOnClickListener {
            if (SharedPreferenceController.getFlag(context!!) == "0") {
                currentSpotName = "양천구"
            } else {
                currentSpotName = "yangcheongu"
            }
            requestSpotDataFromSpot(currentSpotName)
        }
        btn_map_page_spot_gulo.setOnClickListener {
            if (SharedPreferenceController.getFlag(context!!) == "0") {
                currentSpotName = "구로구"
            } else {
                currentSpotName = "gurogu"
            }
            requestSpotDataFromSpot(currentSpotName)
        }
        btn_map_page_spot_yeongdeungpo.setOnClickListener {
            if (SharedPreferenceController.getFlag(context!!) == "0") {
                currentSpotName = "영등포구"
            } else {
                currentSpotName = "yeongdeungpogu"
            }
            requestSpotDataFromSpot(currentSpotName)
        }
        btn_map_page_spot_geumcheon.setOnClickListener {
            if (SharedPreferenceController.getFlag(context!!) == "0") {
                currentSpotName = "금천구"
            } else {
                currentSpotName = "geumcheongu"
            }
            requestSpotDataFromSpot(currentSpotName)
        }
        btn_map_page_spot_dongjag.setOnClickListener {
            if (SharedPreferenceController.getFlag(context!!) == "0") {
                currentSpotName = "동작구"
            } else {
                currentSpotName = "dongjakgu"
            }
            requestSpotDataFromSpot(currentSpotName)
        }
        btn_map_page_spot_seocho.setOnClickListener {
            if (SharedPreferenceController.getFlag(context!!) == "0") {
                currentSpotName = "서초구"
            } else {
                currentSpotName = "seochogu"
            }
            requestSpotDataFromSpot(currentSpotName)
        }
        btn_map_page_spot_gangnam.setOnClickListener {
            if (SharedPreferenceController.getFlag(context!!) == "0") {
                currentSpotName = "강남구"
            } else {
                currentSpotName = "gangnamgu"
            }
            requestSpotDataFromSpot(currentSpotName)
        }
        btn_map_page_spot_songpa.setOnClickListener {
            if (SharedPreferenceController.getFlag(context!!) == "0") {
                currentSpotName = "송파구"
            } else {
                currentSpotName = "songpagu"
            }
            requestSpotDataFromSpot(currentSpotName)
        }
        btn_map_page_spot_gangdong.setOnClickListener {
            if (SharedPreferenceController.getFlag(context!!) == "0") {
                currentSpotName = "강동구"
            } else {
                currentSpotName = "gangdonggu"
            }
            requestSpotDataFromSpot(currentSpotName)
        }
        btn_map_page_spot_mapo.setOnClickListener {
            if (SharedPreferenceController.getFlag(context!!) == "0") {
                currentSpotName = "마포구"
            } else {
                currentSpotName = "mapogu"
            }
            requestSpotDataFromSpot(currentSpotName)
        }
        btn_map_page_spot_yongsan.setOnClickListener {
            if (SharedPreferenceController.getFlag(context!!) == "0") {
                currentSpotName = "용산구"
            } else {
                currentSpotName = "yongsangu"
            }
            requestSpotDataFromSpot(currentSpotName)
        }
        btn_map_page_spot_seongdong.setOnClickListener {
            if (SharedPreferenceController.getFlag(context!!) == "0") {
                currentSpotName = "성동구"
            } else {
                currentSpotName = "seongdonggu"
            }
            requestSpotDataFromSpot(currentSpotName)
        }
        btn_map_page_spot_gwangjin.setOnClickListener {
            if (SharedPreferenceController.getFlag(context!!) == "0") {
                currentSpotName = "광진구"
            } else {
                currentSpotName = "gwangjingu"
            }
            requestSpotDataFromSpot(currentSpotName)
        }
        btn_map_page_spot_junggu.setOnClickListener {
            if (SharedPreferenceController.getFlag(context!!) == "0") {
                currentSpotName = "중구"
            } else {
                currentSpotName = "junggu"
            }
            requestSpotDataFromSpot(currentSpotName)
        }
        btn_map_page_spot_seodaemun.setOnClickListener {
            if (SharedPreferenceController.getFlag(context!!) == "0") {
                currentSpotName = "서대문구"
            } else {
                currentSpotName = "seodaemungu"
            }
            requestSpotDataFromSpot(currentSpotName)
        }
        btn_map_page_spot_eunpyeong.setOnClickListener {
            if (SharedPreferenceController.getFlag(context!!) == "0") {
                currentSpotName = "은평구"
            } else {
                currentSpotName = "eunpyeonggu"
            }
            requestSpotDataFromSpot(currentSpotName)
        }
        btn_map_page_spot_jonglo.setOnClickListener {
            if (SharedPreferenceController.getFlag(context!!) == "0") {
                currentSpotName = "종로구"
            } else {
                currentSpotName = "jongrogu"
            }
            requestSpotDataFromSpot(currentSpotName)
        }
        btn_map_page_spot_seongbug.setOnClickListener {
            if (SharedPreferenceController.getFlag(context!!) == "0") {
                currentSpotName = "성북구"
            } else {
                currentSpotName = "seongbukgu"
            }
            requestSpotDataFromSpot(currentSpotName)
        }
        btn_map_page_spot_dongdaemun.setOnClickListener {
            if (SharedPreferenceController.getFlag(context!!) == "0") {
                currentSpotName = "동대문구"
            } else {
                currentSpotName = "dongdaemungu"
            }
            requestSpotDataFromSpot(currentSpotName)
        }
        btn_map_page_spot_junglang.setOnClickListener {
            if (SharedPreferenceController.getFlag(context!!) == "0") {
                currentSpotName = "중랑구"
            } else {
                currentSpotName = "jungranggu"
            }
            requestSpotDataFromSpot(currentSpotName)
        }
        btn_map_page_spot_gangbug.setOnClickListener {
            if (SharedPreferenceController.getFlag(context!!) == "0") {
                currentSpotName = "강북구"
            } else {
                currentSpotName = "gangbukgu"
            }
            requestSpotDataFromSpot(currentSpotName)
        }
        btn_map_page_spot_dobong.setOnClickListener {
            if (SharedPreferenceController.getFlag(context!!) == "0") {
                currentSpotName = "도봉구"
            } else {
                currentSpotName = "dobonggu"
            }
            requestSpotDataFromSpot(currentSpotName)
        }
        btn_map_page_spot_nowon.setOnClickListener {
            if (SharedPreferenceController.getFlag(context!!) == "0") {
                currentSpotName = "노원구"
            } else {
                currentSpotName = "nowongu"
            }
            requestSpotDataFromSpot(currentSpotName)
        }
        btn_map_page_spot_gwanag.setOnClickListener {
            if (SharedPreferenceController.getFlag(context!!) == "0") {
                currentSpotName = "관악구"
            } else {
                currentSpotName = "gwanakgu"
            }
            requestSpotDataFromSpot(currentSpotName)
        }
    }

    private fun setTranslateFilterSource() {
        if (SharedPreferenceController.getFlag(context!!) == "0") {
            btn_map_page_open_filtering.setImageResource(R.drawable.filter_floating_btn)
            tv_map_page_filter_is_popular.text = "인기순"
            tv_map_page_filter_is_new.text = "최신순"
            tv_map_page_filter_distance.text = "${distanceArrayList[currentDistanceOptionIndex]}Km까지 설정"
        } else {
            btn_map_page_open_filtering.setImageResource(R.drawable.filter_floating_btn_en)
            tv_map_page_filter_is_popular.text = "popularity"
            tv_map_page_filter_is_new.text = "recent"
            tv_map_page_filter_distance.text = "Set up to ${distanceArrayList[currentDistanceOptionIndex]}km"
        }
        setInitFilterImage()
    }

    private fun setTranslateText() {
        if (SharedPreferenceController.getFlag(context!!) == "0") {
            tv_map_page_message_select_location.text = "보고싶은 지역을 선택해주세요"
            tv_map_page_filter_apply_btn.text = "확인"

        } else {
            tv_map_page_message_select_location.text = "Please select the area you want to see"
            tv_map_page_filter_apply_btn.text = "Check"

        }
        tv_map_page_subtitle.text = "$currentSpotName K-spot"
    }

    private fun findTranslateWord() : String{
        Log.e("currentSpotName 이름:" , currentSpotName.toString())
        val spotLanguageName = spotName.find {
            if (SharedPreferenceController.getFlag(context!!) == "1") {
                it.ko == currentSpotName
            } else {
                it.en == currentSpotName
            }
        }
        Log.e("스팟 이름:" , spotLanguageName.toString())
        if (SharedPreferenceController.getFlag(context!!) == "1") {
            spotLanguageName?.let {
                return spotLanguageName!!.en
            }
            return "gangnamgu"
        } else {
            spotLanguageName?.let {
                return spotLanguageName!!.ko
            }
            return "강남구"
        }
    }

    private fun setTranslateMapTextBtn() {
        if (SharedPreferenceController.getFlag(context!!) == "1") {
            btn_map_page_spot_gangseo.setImageResource(R.drawable.gangseo_en)
            btn_map_page_spot_yangcheon.setImageResource(R.drawable.yangcheon_en)
            btn_map_page_spot_gulo.setImageResource(R.drawable.gulo_en)
            btn_map_page_spot_yeongdeungpo.setImageResource(R.drawable.yeongdeungpo_en)
            btn_map_page_spot_geumcheon.setImageResource(R.drawable.geumcheon_en)
            btn_map_page_spot_dongjag.setImageResource(R.drawable.dongjag_en)
            btn_map_page_spot_seocho.setImageResource(R.drawable.seocho_en)
            btn_map_page_spot_gangnam.setImageResource(R.drawable.gangnam_en)
            btn_map_page_spot_songpa.setImageResource(R.drawable.songpa_en)
            btn_map_page_spot_gangdong.setImageResource(R.drawable.gangdong_en)
            btn_map_page_spot_mapo.setImageResource(R.drawable.mapo_en)
            btn_map_page_spot_yongsan.setImageResource(R.drawable.yongsan_en)
            btn_map_page_spot_seongdong.setImageResource(R.drawable.seongdong_en)
            btn_map_page_spot_gwangjin.setImageResource(R.drawable.gwangjin_en)
            btn_map_page_spot_junggu.setImageResource(R.drawable.junggu_en)
            btn_map_page_spot_seodaemun.setImageResource(R.drawable.seodaemun_en)
            btn_map_page_spot_eunpyeong.setImageResource(R.drawable.eunpyeong_en)
            btn_map_page_spot_jonglo.setImageResource(R.drawable.jonglo_en)
            btn_map_page_spot_seongbug.setImageResource(R.drawable.seongdong_en)
            btn_map_page_spot_dongdaemun.setImageResource(R.drawable.dongdaemun_en)
            btn_map_page_spot_junglang.setImageResource(R.drawable.junglang_en)
            btn_map_page_spot_gangbug.setImageResource(R.drawable.gangbug_en)
            btn_map_page_spot_dobong.setImageResource(R.drawable.dobong_en)
            btn_map_page_spot_nowon.setImageResource(R.drawable.nowon_en)
            btn_map_page_spot_gwanag.setImageResource(R.drawable.gwanag_en)
        } else {
            btn_map_page_spot_gangseo.setImageResource(R.drawable.gangseo)
            btn_map_page_spot_yangcheon.setImageResource(R.drawable.yangcheon)
            btn_map_page_spot_gulo.setImageResource(R.drawable.gulo)
            btn_map_page_spot_yeongdeungpo.setImageResource(R.drawable.yeongdeungpo)
            btn_map_page_spot_geumcheon.setImageResource(R.drawable.geumcheon)
            btn_map_page_spot_dongjag.setImageResource(R.drawable.dongjag)
            btn_map_page_spot_seocho.setImageResource(R.drawable.seocho)
            btn_map_page_spot_gangnam.setImageResource(R.drawable.gangnam)
            btn_map_page_spot_songpa.setImageResource(R.drawable.songpa)
            btn_map_page_spot_gangdong.setImageResource(R.drawable.gangdong)
            btn_map_page_spot_mapo.setImageResource(R.drawable.mapo)
            btn_map_page_spot_yongsan.setImageResource(R.drawable.yongsan)
            btn_map_page_spot_seongdong.setImageResource(R.drawable.seongdong)
            btn_map_page_spot_gwangjin.setImageResource(R.drawable.gwangjin)
            btn_map_page_spot_junggu.setImageResource(R.drawable.junggu)
            btn_map_page_spot_seodaemun.setImageResource(R.drawable.seodaemun)
            btn_map_page_spot_eunpyeong.setImageResource(R.drawable.eunpyeong)
            btn_map_page_spot_jonglo.setImageResource(R.drawable.jonglo)
            btn_map_page_spot_seongbug.setImageResource(R.drawable.seongdong)
            btn_map_page_spot_dongdaemun.setImageResource(R.drawable.dongdaemun)
            btn_map_page_spot_junglang.setImageResource(R.drawable.junglang)
            btn_map_page_spot_gangbug.setImageResource(R.drawable.gangbug)
            btn_map_page_spot_dobong.setImageResource(R.drawable.dobong)
            btn_map_page_spot_nowon.setImageResource(R.drawable.nowon)
            btn_map_page_spot_gwanag.setImageResource(R.drawable.gwanag)
        }

    }


}


//if (ActivityCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
//    fusedLocationClientInfoStatus.lastLocation
//            .addOnCompleteListener {
//                if (it.isSuccessful){
//                    toast("성공!!!")
//                }
//            }
//}