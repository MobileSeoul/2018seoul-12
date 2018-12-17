package k_spot.jnm.k_spot

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.view.WindowManager
import k_spot.jnm.k_spot.Get.ChannelSearchResultData
import k_spot.jnm.k_spot.Get.GetSearchResultResponse
import k_spot.jnm.k_spot.Get.PlaceSearchResultData
import k_spot.jnm.k_spot.Network.ApplicationController
import k_spot.jnm.k_spot.Network.NetworkService
import k_spot.jnm.k_spot.adapter.SearchResultActBroadRecyclerAdapter
import k_spot.jnm.k_spot.adapter.SearchResultActSpotRecyclerAdapter
import k_spot.jnm.k_spot.db.SharedPreferenceController
import kotlinx.android.synthetic.main.activity_search_result.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchResultActivity : AppCompatActivity(), View.OnClickListener {
    override fun onClick(v: View?) {
        val id = v!!.getId()
        Log.v("id",id.toString())
        val broadIndex: Int = search_result_act_celeb_borad_rv.getChildAdapterPosition(v)
        if (searchBroadItems.size != 0) {
            val channel_id = searchBroadItems[broadIndex].channel_id
            Log.v("channel_id", channel_id.toString())
        }


    }

    lateinit var searchBroadItems: ArrayList<ChannelSearchResultData>
    lateinit var searchSpotItems: ArrayList<PlaceSearchResultData>
    lateinit var searchEventItems: ArrayList<PlaceSearchResultData>
    lateinit var searchResultActBroadRecyclerAdapter: SearchResultActBroadRecyclerAdapter
    lateinit var searchResultActSpotRecyclerAdapter: SearchResultActSpotRecyclerAdapter
    lateinit var networkService: NetworkService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_result)

        var keyword = intent.getStringExtra("keyword")
        search_result_act_result_tv.text = keyword.toString()
        getSearchResult(keyword)
        searchBroadItems = ArrayList()
        searchSpotItems = ArrayList()
        searchEventItems = ArrayList()
        searchResultActBroadRecyclerAdapter = SearchResultActBroadRecyclerAdapter(searchBroadItems, applicationContext, 0, this)
        searchResultActSpotRecyclerAdapter = SearchResultActSpotRecyclerAdapter(searchSpotItems, applicationContext, 0, 0)
        setStatusBarTransparent()
        setOnClickListener(keyword)
        setInitView(keyword)
    }

    private fun setInitView(keyword: String) {
        //추후 구독 색상변경 바꾸기
        if (SharedPreferenceController.getFlag(this) == "0"){
            search_result_act_result_tv.text = keyword + " " + "검색결과"
            search_result_act_celeb_borad_tv.text = "연예인 / 방송"
            search_result_act_celeb_borad_see_more_tv.text = "더보기"
            search_result_act_spot_tv.text = "장소"
            search_result_act_spot_see_more_tv.text = "더보기"
            search_result_act_event_tv.text = "이벤트"
            search_result_act_event_see_more_tv.text = "더보기"
            search_result_act_no_search_result_tv.text = "검색 결과 없음"
        } else {
            search_result_act_result_tv.text = keyword + " " + "search result"
            search_result_act_celeb_borad_tv.text = "Celebrity / Broadcast"
            search_result_act_celeb_borad_see_more_tv.text = "view more"
            search_result_act_spot_tv.text = "Spot"
            search_result_act_spot_see_more_tv.text = "view more"
            search_result_act_event_tv.text = "Event"
            search_result_act_event_see_more_tv.text = "view more"
            search_result_act_no_search_result_tv.text = "There is no search result"
        }
    }

    private fun setOnClickListener(keyword : String) {
        search_result_act_celeb_borad_see_more_btn.setOnClickListener {
            var intent2 = Intent(applicationContext, SearchBraodViewMoreActivity::class.java)
            intent2.putExtra("keyword", keyword)
            intent2.putParcelableArrayListExtra("searchBroadItems", searchBroadItems)
            startActivity(intent2)
        }
        search_result_act_spot_see_more_btn.setOnClickListener {
            var intent3 = Intent(applicationContext, SearchSpotViewMoreActivity::class.java)
            intent3.putParcelableArrayListExtra("searchSpotItems", searchSpotItems)
            intent3.putExtra("keyword", keyword)
            startActivity(intent3)
        }
        search_result_act_event_see_more_btn.setOnClickListener {
            var intent4 = Intent(applicationContext, SearchEventViewMoreActivity::class.java)
            intent4.putExtra("keyword", keyword)
            intent4.putParcelableArrayListExtra("searchEventItems", searchEventItems)
            startActivity(intent4)
        }
        search_result_act_back_btn.setOnClickListener {
            finish()
        }
    }

    fun getSearchResult(keyword: String) {
        var keyword: String = keyword
        networkService = ApplicationController.instance.networkService
        val authorization: String = SharedPreferenceController.getAuthorization(context = applicationContext)
        val getSearchResultResponse = networkService.getSearchResult(SharedPreferenceController.getFlag(this).toInt(), authorization, keyword)
        getSearchResultResponse.enqueue(object : Callback<GetSearchResultResponse> {
            override fun onFailure(call: Call<GetSearchResultResponse>?, t: Throwable?) {
                Log.v("39393939393", "검색 실패")
            }
            override fun onResponse(call: Call<GetSearchResultResponse>?, response: Response<GetSearchResultResponse>?) {
                if (response!!.isSuccessful) {

                    searchBroadItems = response!!.body()!!.data!!.channel
                    searchSpotItems = response!!.body()!!.data!!.place
                    searchEventItems = response!!.body()!!.data!!.event

                    if(searchBroadItems.size == 0 && searchSpotItems.size == 0 && searchEventItems.size == 0){
                        search_result_act_all_scroll_view.visibility = View.GONE
                        search_result_act_no_search_result_rl.visibility = View.VISIBLE
                        search_result_act_result_iv.visibility = View.GONE
                    }
                    Log.v("39393939393", searchBroadItems.size.toString() + searchSpotItems.size.toString() + searchEventItems.size.toString())

                    if (searchBroadItems.size != 0) {
                        // 첫 번째 CardView 생성 function
                        if (searchBroadItems.size == 1) {
                            makeRecyclerView(searchBroadItems, 1)
                        } else if (searchBroadItems.size >= 2) {
                            makeRecyclerView(searchBroadItems, 2)
                        }

                        if (searchSpotItems.size == 0 && searchEventItems.size == 0) {
                            search_result_act_celeb_borad_under_bar.visibility = View.GONE
                        }

                    } else {
                        search_result_act_celeb_borad_all_rl.visibility = View.GONE
                    }
                    if (searchSpotItems.size != 0) {
                        if (searchSpotItems.size == 1) {
                            // 첫 번째 CardView 생성 function
                            makeRecyclerView1(searchSpotItems, 1)
                        } else if (searchSpotItems.size == 2) {
                            makeRecyclerView1(searchSpotItems, 2)
                        } else if (searchSpotItems.size == 3) {
                            makeRecyclerView1(searchSpotItems, 3)
                        } else if (searchSpotItems.size >= 4) {
                            makeRecyclerView1(searchSpotItems, 4)
                        }

                        if (searchEventItems.size == 0) {
                            search_result_act_spot_under_bar.visibility = View.GONE
                        }

                    } else {
                        search_result_act_spot_all_rl.visibility = View.GONE
                    }
                    if (searchEventItems.size != 0) {
                        if (searchEventItems.size == 1) {
                            // 첫 번째 CardView 생성 function
                            makeRecyclerView2(searchEventItems, 1)
                        } else if (searchEventItems.size == 2) {
                            makeRecyclerView2(searchEventItems, 2)
                        } else if (searchEventItems.size == 3) {
                            makeRecyclerView2(searchEventItems, 3)
                        } else if (searchEventItems.size >= 4) {
                            makeRecyclerView2(searchEventItems, 4)
                        }
                    } else {
                        search_result_act_event_all_rl.visibility = View.GONE
                    }
                }
            }

        })
    }

    fun requestSearchResult(keyword: String) {
        var keyword: String = keyword
        networkService = ApplicationController.instance.networkService
        val authorization: String = SharedPreferenceController.getAuthorization(context = applicationContext)
        val getSearchResultResponse = networkService.getSearchResult(0, authorization, keyword)
        getSearchResultResponse.enqueue(object : Callback<GetSearchResultResponse> {
            override fun onFailure(call: Call<GetSearchResultResponse>?, t: Throwable?) {
                Log.v("39393939393", "검색 실패")
            }
            override fun onResponse(call: Call<GetSearchResultResponse>?, response: Response<GetSearchResultResponse>?) {
                if (response!!.isSuccessful) {

                    searchBroadItems = response!!.body()!!.data!!.channel
                    searchSpotItems = response!!.body()!!.data!!.place
                    searchEventItems = response!!.body()!!.data!!.event

                    if(searchBroadItems.size == 0 && searchSpotItems.size == 0 && searchEventItems.size == 0){
                        search_result_act_all_scroll_view.visibility = View.GONE
                        search_result_act_no_search_result_rl.visibility = View.VISIBLE
                        search_result_act_result_iv.visibility = View.GONE
                    }
                    Log.v("39393939393", searchBroadItems.size.toString() + searchSpotItems.size.toString() + searchEventItems.size.toString())

                    if (searchBroadItems.size != 0) {
                        // 첫 번째 CardView 생성 function
                        if (searchBroadItems.size == 1) {
                            makeRecyclerView(searchBroadItems, 1)
                        } else if (searchBroadItems.size >= 2) {
                            makeRecyclerView(searchBroadItems, 2)
                        }

                        if (searchSpotItems.size == 0 && searchEventItems.size == 0) {
                            search_result_act_celeb_borad_under_bar.visibility = View.GONE
                        }

                    } else {
                        search_result_act_celeb_borad_all_rl.visibility = View.GONE
                    }
                    if (searchSpotItems.size != 0) {
                        if (searchSpotItems.size == 1) {
                            // 첫 번째 CardView 생성 function
                            makeRecyclerView1(searchSpotItems, 1)
                        } else if (searchSpotItems.size == 2) {
                            makeRecyclerView1(searchSpotItems, 2)
                        } else if (searchSpotItems.size == 3) {
                            makeRecyclerView1(searchSpotItems, 3)
                        } else if (searchSpotItems.size >= 4) {
                            makeRecyclerView1(searchSpotItems, 4)
                        }

                        if (searchEventItems.size == 0) {
                            search_result_act_spot_under_bar.visibility = View.GONE
                        }

                    } else {
                        search_result_act_spot_all_rl.visibility = View.GONE
                    }
                    if (searchEventItems.size != 0) {
                        if (searchEventItems.size == 1) {
                            // 첫 번째 CardView 생성 function
                            makeRecyclerView2(searchEventItems, 1)
                        } else if (searchEventItems.size == 2) {
                            makeRecyclerView2(searchEventItems, 2)
                        } else if (searchEventItems.size == 3) {
                            makeRecyclerView2(searchEventItems, 3)
                        } else if (searchEventItems.size >= 4) {
                            makeRecyclerView2(searchEventItems, 4)
                        }
                    } else {
                        search_result_act_event_all_rl.visibility = View.GONE
                    }
                }
            }

        })
    }


    private fun makeRecyclerView(searchBroadItems: ArrayList<ChannelSearchResultData>, itemCount: Int) {
        searchResultActBroadRecyclerAdapter = SearchResultActBroadRecyclerAdapter(searchBroadItems, applicationContext, itemCount, this)
        search_result_act_celeb_borad_rv.layoutManager = LinearLayoutManager(applicationContext)
        search_result_act_celeb_borad_rv.adapter = searchResultActBroadRecyclerAdapter
    }

    private fun makeRecyclerView1(searchSpotItems: ArrayList<PlaceSearchResultData>, itemCount: Int) {

        searchResultActSpotRecyclerAdapter = SearchResultActSpotRecyclerAdapter(searchSpotItems, applicationContext, itemCount, 0)
        search_result_act_spot_rv.layoutManager = LinearLayoutManager(applicationContext)
        search_result_act_spot_rv.adapter = searchResultActSpotRecyclerAdapter
    }

    private fun makeRecyclerView2(searchEventItems: ArrayList<PlaceSearchResultData>, itemCount: Int) {
        searchResultActSpotRecyclerAdapter = SearchResultActSpotRecyclerAdapter(searchEventItems, applicationContext, itemCount, 1)
        search_result_act_event_rv.layoutManager = LinearLayoutManager(applicationContext)
        search_result_act_event_rv.adapter = searchResultActSpotRecyclerAdapter
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
