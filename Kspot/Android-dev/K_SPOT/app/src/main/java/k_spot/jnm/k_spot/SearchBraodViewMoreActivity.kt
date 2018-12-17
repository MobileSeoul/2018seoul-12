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
import k_spot.jnm.k_spot.Get.ChannelSearchResultData
import k_spot.jnm.k_spot.adapter.SearchResultActBroadRecyclerAdapter
import k_spot.jnm.k_spot.db.SharedPreferenceController
import kotlinx.android.synthetic.main.activity_search_braod_view_more.*

class SearchBraodViewMoreActivity : AppCompatActivity(), View.OnClickListener {
    override fun onClick(v: View?) {
        val index: Int = search_broad_view_more_act_rv.getChildAdapterPosition(v)
        val channel_id = searchBroadItems[index].channel_id
        Log.v("channel_id", channel_id.toString())
//        startActivity<ContentsDetail>("channel_id" to channel_id)
    }

    lateinit var searchBroadItems: ArrayList<ChannelSearchResultData>
    lateinit var searchResultActBroadRecyclerAdapter: SearchResultActBroadRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_braod_view_more)
        searchBroadItems = intent.getParcelableArrayListExtra<ChannelSearchResultData>("searchBroadItems")
        searchResultActBroadRecyclerAdapter = SearchResultActBroadRecyclerAdapter(searchBroadItems, applicationContext, searchBroadItems.size, this)
        var keyword = intent.getStringExtra("keyword")
        search_broad_view_more_act_result_tv.text = keyword + " " + "검색결과"
        makeRecyclerView(searchBroadItems)
        setStatusBarTransparent()
        setOnClickListener()
        setInitView(keyword)
    }

    private fun setInitView(keyword: String) {
        //추후 구독 색상변경 바꾸기
        if (SharedPreferenceController.getFlag(this) == "0"){
            search_broad_view_more_act_result_tv.text = keyword + " " + "검색결과"
            search_broad_view_more_act_tv.text = "연예인 / 방송"
        } else {
            search_broad_view_more_act_result_tv.text = keyword + " " + "search result"
            search_broad_view_more_act_tv.text = "Celebrity / BroadCast"
        }
    }

    private fun setOnClickListener() {
        search_broad_view_more_act_back_btn.setOnClickListener {
            finish()
        }
    }

    private fun makeRecyclerView(searchBroadItems : ArrayList<ChannelSearchResultData>) {
        searchResultActBroadRecyclerAdapter = SearchResultActBroadRecyclerAdapter(searchBroadItems, applicationContext, searchBroadItems.size, this)
        search_broad_view_more_act_rv.layoutManager = LinearLayoutManager(applicationContext)
        search_broad_view_more_act_rv.adapter = searchResultActBroadRecyclerAdapter
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
