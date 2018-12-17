package k_spot.jnm.k_spot

import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import k_spot.jnm.k_spot.Get.GetSearchResultFilterResponse
import k_spot.jnm.k_spot.Get.PlaceSearchResultData
import k_spot.jnm.k_spot.Network.ApplicationController
import k_spot.jnm.k_spot.Network.NetworkService
import k_spot.jnm.k_spot.activity.SpotViewMoreActivity
import k_spot.jnm.k_spot.adapter.SearchSpotViewMoreActRecyclerAdapter
import k_spot.jnm.k_spot.db.SharedPreferenceController
import kotlinx.android.synthetic.main.activity_search_spot_view_more.*
import org.jetbrains.anko.startActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchSpotViewMoreActivity : AppCompatActivity(), View.OnClickListener {
    override fun onClick(v: View?) {
        val index: Int = search_spot_view_more_act_rv.getChildAdapterPosition(v)
        val spot_id = searchSpotItems[index].spot_id
        startActivity<SpotViewMoreActivity>("spot_id" to spot_id, "event_flag" to 0)
        Log.v("spot_id", spot_id.toString())
//        startActivity<ContentsDetail>("channel_id" to channel_id)
    }

    lateinit var networkService : NetworkService
    lateinit var searchSpotItems: ArrayList<PlaceSearchResultData>
    lateinit var searchSpotViewMoreActRecyclerAdapter: SearchSpotViewMoreActRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_spot_view_more)
        searchSpotItems = intent.getParcelableArrayListExtra<PlaceSearchResultData>("searchSpotItems")
        var keyword = intent.getStringExtra("keyword")
        searchSpotViewMoreActRecyclerAdapter = SearchSpotViewMoreActRecyclerAdapter(searchSpotItems, applicationContext, this, 0)
        search_spot_view_more_act_result_tv.text = keyword + " " + "검색결과"
        makeRecyclerView(searchSpotItems)
        setStatusBarTransparent()
        setOnClickListener(keyword)
        setInitView(keyword)
    }
    private fun setInitView(keyword: String) {
        //추후 구독 색상변경 바꾸기
        if (SharedPreferenceController.getFlag(this) == "0"){
            search_spot_view_more_act_result_tv.text = keyword + " " + "검색결과"
            search_spot_view_more_act_tv.text = "장소"
            search_spot_view_more_act_filter.setImageResource(R.drawable.filter_floating_btn)
            search_spot_view_more_act_filter_popularity_tv.text = "인기순"
            search_spot_view_more_act_filter_new_tv.text = "최신순"
            search_spot_view_more_act_filter_enter_tv.text = "검색"
            search_spot_view_more_act_filter_restaurant_btn.setImageResource(R.drawable.filter_restaurant_btn_gray)
            search_spot_view_more_act_filter_cafe_btn.setImageResource(R.drawable.filter_cafe_btn_gray)
            search_spot_view_more_act_filter_hotplace_btn.setImageResource(R.drawable.filter_hotplace_btn_gray)
            search_spot_view_more_act_filter_etc_btn.setImageResource(R.drawable.filter_etc_btn_gray)
        } else {
            search_spot_view_more_act_result_tv.text = keyword + " " + "search result"
            search_spot_view_more_act_tv.text = "Spot"
            search_spot_view_more_act_filter.setImageResource(R.drawable.filter_floating_btn_en)
            search_spot_view_more_act_filter_popularity_tv.text = "Popularity"
            search_spot_view_more_act_filter_new_tv.text = "Recent"
            search_spot_view_more_act_filter_enter_tv.text = "Search"
            search_spot_view_more_act_filter_restaurant_btn.setImageResource(R.drawable.filter_food_icon_en)
            search_spot_view_more_act_filter_cafe_btn.setImageResource(R.drawable.filter_cafe_icon_en)
            search_spot_view_more_act_filter_hotplace_btn.setImageResource(R.drawable.filter_hotsight_icon_en)
            search_spot_view_more_act_filter_etc_btn.setImageResource(R.drawable.filter_etc_icon_en)
        }
    }

    fun getSearchResultFilter(keyword: String, order: Int, is_food : Int, is_cafe : Int, is_sight : Int, is_etc : Int) {
        networkService = ApplicationController.instance.networkService
        val authorization: String = SharedPreferenceController.getAuthorization(context = applicationContext)
        val getSearchResultFilterResponse = networkService.getSearchResultFilterResponse(0, authorization, keyword, order, is_food, is_cafe, is_sight, is_etc)
        getSearchResultFilterResponse.enqueue(object : Callback<GetSearchResultFilterResponse> {
            override fun onFailure(call: Call<GetSearchResultFilterResponse>?, t: Throwable?) {
            }

            override fun onResponse(call: Call<GetSearchResultFilterResponse>?, response: Response<GetSearchResultFilterResponse>?) {
                if (response!!.isSuccessful) {
                    searchSpotItems = response!!.body()!!.data
                    if(searchSpotItems.size > 0){
                        makeRecyclerView(searchSpotItems)
                    }
                }
            }

        })
    }

    private fun setOnClickListener(keyword : String) {

        var order : Int = 0
        var is_food : Int = 0
        var is_cafe : Int = 0
        var is_sight : Int = 0
        var is_etc : Int = 0

        search_spot_view_more_act_back_btn.setOnClickListener {
            finish()
        }
        search_spot_view_more_act_filter.setOnClickListener {
            clickBoradcastTabAnimation()
//            search_spot_view_more_act_filter_on_rl.visibility = View.VISIBLE
        }
        search_spot_view_more_act_filter_cancle_btn.setOnClickListener {
            clickFilterDownAnimation()
            search_spot_view_more_act_filter_on_rl.visibility = View.GONE
        }
        search_spot_view_more_act_filter_x_btn.setOnClickListener {
            clickFilterDownAnimation()
            search_spot_view_more_act_filter_on_rl.visibility = View.GONE
        }
//##        // 검색 통신 필요 ##
        search_spot_view_more_act_filter_enter_btn.setOnClickListener {
            clickFilterDownAnimation()
            getSearchResultFilter(keyword, order, is_food, is_cafe, is_sight, is_etc)
            search_spot_view_more_act_filter_on_rl.visibility = View.GONE
        }

        // 인기순 버튼
        search_spot_view_more_act_filter_popularity_btn.setOnClickListener {
            order = 1
            search_spot_view_more_act_filter_popularity_tv.setTextColor(Color.parseColor("#40D39F"))
            search_spot_view_more_act_filter_new_tv.setTextColor(Color.parseColor("#C0C0C0"))
        }
        // 최신순 버튼
        search_spot_view_more_act_filter_new_btn.setOnClickListener {
            order = 0
            search_spot_view_more_act_filter_new_tv.setTextColor(Color.parseColor("#40D39F"))
            search_spot_view_more_act_filter_popularity_tv.setTextColor(Color.parseColor("#C0C0C0"))
        }
        // 맛집 버튼
        search_spot_view_more_act_filter_restaurant_btn.setOnClickListener {
            if(is_food == 0){
                search_spot_view_more_act_filter_restaurant_btn.setImageResource(R.drawable.filter_restaurant_btn_green)
                is_food = 1
            }else if(is_food === 1){
                search_spot_view_more_act_filter_restaurant_btn.setImageResource(R.drawable.filter_restaurant_btn_gray)
                is_food = 0
            }
        }
        // Cafe 버튼
        search_spot_view_more_act_filter_cafe_btn.setOnClickListener {
            if(is_cafe == 0){
                search_spot_view_more_act_filter_cafe_btn.setImageResource(R.drawable.filter_cafe_btn_green)
                is_cafe = 1
            }else if(is_cafe === 1){
                search_spot_view_more_act_filter_cafe_btn.setImageResource(R.drawable.filter_cafe_btn_gray)
                is_cafe = 0
            }
        }
        // 명소 버튼
        search_spot_view_more_act_filter_hotplace_btn.setOnClickListener {
            if(is_sight == 0){
                search_spot_view_more_act_filter_hotplace_btn.setImageResource(R.drawable.filter_hotplace_btn_green)
                is_sight = 1
            }else if(is_sight === 1){
                search_spot_view_more_act_filter_hotplace_btn.setImageResource(R.drawable.filter_hotplace_btn_gray)
                is_sight = 0
            }
        }
        // 기타 버튼
        search_spot_view_more_act_filter_etc_btn.setOnClickListener {
            if(is_etc == 0){
                search_spot_view_more_act_filter_etc_btn.setImageResource(R.drawable.filter_etc_btn_green)
                is_etc = 1
            }else if(is_etc === 1){
                search_spot_view_more_act_filter_etc_btn.setImageResource(R.drawable.filter_etc_btn_gray)
                is_etc = 0
            }

        }

//##        // 검색 통신 필요 ##
    }

    // 탭바를 오른쪽 방송 탭 밑으로 이동!
    private fun clickBoradcastTabAnimation(){
        val anim = AnimationUtils
                .loadAnimation(applicationContext,
                        R.anim.search_spot_view_more_act_anim)
        search_spot_view_more_act_filter_on_rl.visibility = View.VISIBLE
        search_spot_view_more_act_filter_on_rl.startAnimation(anim)
    }

    // 탭바를 오른쪽 방송 탭 밑으로 이동!
    private fun clickFilterDownAnimation(){
        val anim = AnimationUtils
                .loadAnimation(applicationContext,
                        R.anim.search_spot_view_more_act_down_anim)
        search_spot_view_more_act_filter_on_rl.visibility = View.GONE
        search_spot_view_more_act_filter_on_rl.startAnimation(anim)
    }

    private fun makeRecyclerView(searchSpotItems: ArrayList<PlaceSearchResultData>) {
        searchSpotViewMoreActRecyclerAdapter = SearchSpotViewMoreActRecyclerAdapter(searchSpotItems, applicationContext, this, 0)
        search_spot_view_more_act_rv.layoutManager = LinearLayoutManager(applicationContext)
        search_spot_view_more_act_rv.adapter = searchSpotViewMoreActRecyclerAdapter
    }

    // 상태바 투명하게 하는 함수
    private fun setStatusBarTransparent() {
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true)

        }
        if (Build.VERSION.SDK_INT >= 19) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
            window.statusBarColor = Color.TRANSPARENT
//            DrawableCompat.setTint(, "#757575")
        }
        // 밑에 두줄 아이콘 회색으로 바꾸는 코드
        val view: View? = window.decorView
        view!!.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
    }

    // 상태바 투명하게 하는 함수
    private fun setWindowFlag(bits: Int, on: Boolean) {
        val win = window
        val winParams = win.attributes
        if (on) {
            winParams.flags = winParams.flags or bits
        } else {
            winParams.flags = winParams.flags and bits.inv()
        }
        win.attributes = winParams
    }

    // 상단바 밑으로 뷰 보이게하는 코드
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val window = window
        val decorView = window.decorView
        if (Configuration.ORIENTATION_LANDSCAPE === newConfig.orientation) {
            decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.statusBarColor = Color.parseColor("#55000000") // set dark color, the icon will auto change light
            }
        } else {
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.statusBarColor = Color.parseColor("#fffafafa")
            }
        }
    }


}
