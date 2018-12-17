package k_spot.jnm.k_spot

import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import k_spot.jnm.k_spot.Get.BroadcastSearchViewData
import k_spot.jnm.k_spot.Get.EventSearchViewData
import k_spot.jnm.k_spot.Get.GetSearchViewResponse
import k_spot.jnm.k_spot.Network.ApplicationController
import k_spot.jnm.k_spot.Network.NetworkService
import k_spot.jnm.k_spot.activity.CategoryDetailActivity
import k_spot.jnm.k_spot.activity.SpotViewMoreActivity
import k_spot.jnm.k_spot.activity.ViewMoreActivity
import k_spot.jnm.k_spot.db.SharedPreferenceController
import kotlinx.android.synthetic.main.activity_search.*
import org.jetbrains.anko.sdk25.coroutines.textChangedListener
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SearchActivity : AppCompatActivity() {

    lateinit var networkService: NetworkService

    lateinit var celebrity: ArrayList<BroadcastSearchViewData>
    lateinit var broadcast: ArrayList<BroadcastSearchViewData>
    lateinit var event: ArrayList<EventSearchViewData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        celebrity = ArrayList()
        broadcast = ArrayList()
        event = ArrayList()
        setInitView()

//        val celebRecyclerView : RecyclerView = findViewById(R.id.search_act_search_recommend_celeb_rv)
//        makeRecommededHashTag(celebRecyclerView)
//
//        val broadcastRecyclerView : RecyclerView = findViewById(R.id.search_act_search_recommend_broadcast_rv)
//        makeRecommededHashTag(broadcastRecyclerView)
//
//        val eventRecyclerView : RecyclerView = findViewById(R.id.search_act_search_recommend_event_rv)
//        makeRecommededHashTag(eventRecyclerView)

        setStatusBarTransparent()


        getSearchView()

        setOnClickListener()

        setOnEnterListener()
    }

    private fun setInitView() {
        //추후 구독 색상변경 바꾸기
        if (SharedPreferenceController.getFlag(this) == "0") {
            search_act_what_search_tv.text = "무엇을 찾으시나요?"
            search_act_search_edit_text.hint = "검색어를 입력해주세요."
            search_act_search_recommend_celeb_tv.text = "연예인"
            search_act_search_broadcast_tv.text = "방송"
            search_act_search_event_tv.text = "이벤트"
            search_act_search_tv.text = "검색하기"
        } else {
            search_act_what_search_tv.text = "What are you looking for?"
            search_act_search_edit_text.hint = "Please enter your key word."
            search_act_search_recommend_celeb_tv.text = "Celebrity"
            search_act_search_broadcast_tv.text = "Broadcast"
            search_act_search_event_tv.text = "Event"
            search_act_search_tv.text = "Search"
        }
    }

    fun setOnClickListener() {
        search_act_back_btn.setOnClickListener {
            finish()
        }


        search_act_search_recommend_celeb_rl.setOnClickListener {
            if (celebrity.size >= 1) {
                var channel_id = celebrity[0].channel_id
                startActivity<CategoryDetailActivity>("channel_id" to channel_id.toString())
            }
        }

        search_act_search_recommend_celeb_rl2.setOnClickListener {
            if (celebrity.size >= 2) {
                var channel_id = celebrity[1].channel_id
                startActivity<CategoryDetailActivity>("channel_id" to channel_id.toString())
            }
        }

        search_act_search_recommend_broadcast_rl.setOnClickListener {
            if (broadcast.size >= 1) {
                var channel_id = broadcast[0].channel_id
                startActivity<CategoryDetailActivity>("channel_id" to channel_id.toString())
            }
        }

        search_act_search_recommend_broadcast_rl2.setOnClickListener {
            if (broadcast.size >= 2) {
                var channel_id = broadcast[1].channel_id
                startActivity<CategoryDetailActivity>("channel_id" to channel_id.toString())

            }
        }

        search_act_search_recommend_broadcast_rl3.setOnClickListener {
            if (broadcast.size >= 3) {
                var channel_id = broadcast[2].channel_id
                startActivity<CategoryDetailActivity>("channel_id" to channel_id.toString())
            }
        }

        search_act_search_recommend_event_rl.setOnClickListener {
            if (event.size >= 1) {
                var spot_id = event[0].spot_id
                startActivity<SpotViewMoreActivity>("spot_id" to spot_id, "eventFlag" to 1)
            }
        }

        search_act_search_recommend_event_rl2.setOnClickListener {
            if (event.size >= 2) {
                var spot_id = event[1].spot_id
                startActivity<ViewMoreActivity>("spot_id" to spot_id, "eventFlag" to 1)
            }
        }

        search_act_search_edit_text.textChangedListener {
            afterTextChanged {
                if (search_act_search_edit_text.text.toString().length > 0) {
                    search_act_search_btn.setBackgroundColor(Color.parseColor("#40D39F"))
                } else {
                    search_act_search_btn.setBackgroundColor(Color.parseColor("#C5C5C5"))
                }
            }
        }

        search_act_search_btn.setOnClickListener {

            var keyword = search_act_search_edit_text.text.toString()
            if (keyword.length == 0) {
                toast("검색어를 입력해주세요!")
            } else {
                startActivity<SearchResultActivity>("keyword" to keyword)
                false
            }
        }

    }

    fun setOnEnterListener() {
        search_act_search_edit_text.setOnKeyListener { v, keyCode, event ->
            if((event.action == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {

                var keyword = search_act_search_edit_text.text.toString()
                if (keyword.length == 0) {
                    toast("검색어를 입력해주세요!")
                } else {
                    startActivity<SearchResultActivity>("keyword" to keyword)
                    false
                }

                true
            }
            false
        }
    }



//    edittext.setOnKeyListener(new View.OnKeyListener() {
//        @Override
//        public boolean onKey(View v, int keyCode, KeyEvent event) {
//            //Enter key Action
//            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
//                //Enter키눌렀을떄 처리
//                return true;
//            }
//            return false;
//        }
//    });


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

//    private fun makeRecommededHashTag(recyclerView: RecyclerView) {
//        val mRecyclerView = recyclerView
////        val mRecyclerView = view.findViewById(R.id.main_page_fragment_rv1) as RecyclerView
//        mRecyclerView.setHasFixedSize(true)
//
//        val mLayoutManager = GridLayoutManager(applicationContext, 1, GridLayoutManager.HORIZONTAL, false)
//
//        mRecyclerView.layoutManager = mLayoutManager
//
//        var myDataset = ArrayList<SearchActivityHashTagData>()
//
//        myDataset.add(SearchActivityHashTagData("블랙핑크"))
//        myDataset.add(SearchActivityHashTagData("블랙핑크"))
//        myDataset.add(SearchActivityHashTagData("블랙핑크"))
//        myDataset.add(SearchActivityHashTagData("블랙핑크"))
//        myDataset.add(SearchActivityHashTagData("블랙핑크"))
//        myDataset.add(SearchActivityHashTagData("블랙핑크"))
//        myDataset.add(SearchActivityHashTagData("블랙핑크"))
//
//
//        val mAdapter = SearchActTagRecyclerAdapter(applicationContext, myDataset)
//        mRecyclerView.adapter = mAdapter
//    }

    private fun getSearchView() {
        networkService = ApplicationController.instance.networkService
        val authorization: String = SharedPreferenceController.getAuthorization(context = applicationContext)
        val getSearchViewResponse = networkService.getSearchView(SharedPreferenceController.getFlag(this).toInt(), authorization)
        getSearchViewResponse.enqueue(object : Callback<GetSearchViewResponse> {
            override fun onFailure(call: Call<GetSearchViewResponse>?, t: Throwable?) {
            }

            override fun onResponse(call: Call<GetSearchViewResponse>?, response: Response<GetSearchViewResponse>?) {
                if (response!!.isSuccessful) {


                    broadcast = response!!.body()!!.data!!.broadcast
                    celebrity = response!!.body()!!.data!!.celebrity
                    event = response!!.body()!!.data!!.event

                    when (celebrity.size) {
                        0 -> {
                            search_act_search_recommend_celeb_tv1.visibility = View.GONE
                            search_act_search_recommend_celeb_tv2.visibility = View.GONE
                        }
                        1 -> {
                            search_act_search_recommend_celeb_tv1.text = celebrity[0].name
                            search_act_search_recommend_celeb_tv2.visibility = View.GONE
                        }

                        2 -> {
                            search_act_search_recommend_celeb_tv1.text = celebrity[0].name
                            search_act_search_recommend_celeb_tv2.text = celebrity[1].name
                        }
                    }

                    when (broadcast.size) {
                        0 -> {
                            search_act_search_recommend_broadcast_tv2.visibility = View.GONE
                            search_act_search_recommend_broadcast_tv2.visibility = View.GONE
                            search_act_search_recommend_broadcast_tv3.visibility = View.GONE
                        }
                        1 -> {
                            search_act_search_recommend_broadcast_tv1.text = broadcast[0].name
                            search_act_search_recommend_broadcast_tv2.visibility = View.GONE
                            search_act_search_recommend_broadcast_tv3.visibility = View.GONE
                        }

                        2 -> {
                            search_act_search_recommend_broadcast_tv1.text = broadcast[0].name
                            search_act_search_recommend_broadcast_tv2.text = broadcast[1].name
                            search_act_search_recommend_broadcast_tv3.visibility = View.GONE
                        }

                        3 -> {
                            search_act_search_recommend_broadcast_tv1.text = broadcast[0].name
                            search_act_search_recommend_broadcast_tv2.text = broadcast[1].name
                            search_act_search_recommend_broadcast_tv3.text = broadcast[2].name
                        }
                    }


                    when (event.size) {
                        0 -> {
                            search_act_search_recommend_event_tv1.visibility = View.GONE
                            search_act_search_recommend_event_tv2.visibility = View.GONE
                        }
                        1 -> {
                            search_act_search_recommend_event_tv1.text = event[0].name
                            search_act_search_recommend_event_tv2.visibility = View.GONE
                        }

                        2 -> {
                            search_act_search_recommend_event_tv1.text = event[0].name
                            search_act_search_recommend_event_tv2.text = event[1].name
                        }
                    }
                }
            }

        })
    }
}
