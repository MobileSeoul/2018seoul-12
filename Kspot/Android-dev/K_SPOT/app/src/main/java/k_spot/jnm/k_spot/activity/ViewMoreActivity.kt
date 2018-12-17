package k_spot.jnm.k_spot.activity

import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.view.WindowManager
import k_spot.jnm.k_spot.Get.GetChannelViewMoreResponse
import k_spot.jnm.k_spot.Get.ViewMoreData
import k_spot.jnm.k_spot.Network.ApplicationController
import k_spot.jnm.k_spot.Network.NetworkService
import k_spot.jnm.k_spot.R
import k_spot.jnm.k_spot.adapter.ViewMoreRecyclerViewAdapter
import k_spot.jnm.k_spot.db.SharedPreferenceController
import kotlinx.android.synthetic.main.activity_view_more.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ViewMoreActivity : AppCompatActivity() {

    val viewMoreMoreDataList : ArrayList<ViewMoreData> by lazy {
        ArrayList<ViewMoreData>()
    }

    val viewMoreRecyclerViewAdapter : ViewMoreRecyclerViewAdapter by lazy {
        ViewMoreRecyclerViewAdapter(this, viewMoreMoreDataList)
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_more)
        setStatusBarTransparent()

        if (intent.getIntExtra("is_event", 0) == 0){
            if (SharedPreferenceController.getFlag(this) == "0"){
                tv_view_more_act_title.text = "장소"
            } else {
                tv_view_more_act_title.text = "SPOT"
            }

        } else if (intent.getIntExtra("is_event", 0) == 1){

            if (SharedPreferenceController.getFlag(this) == "0"){
                tv_view_more_act_title.text = "이벤트"
            } else {
                tv_view_more_act_title.text = "EVENT"
            }
        }

        setOnClickListener()

        requestViewMore(intent.getIntExtra("channel_id", 0), intent.getIntExtra("is_event", 0))

    }

    private fun setOnClickListener() {
        btn_view_more_act_back.setOnClickListener {
            finish()
        }
    }



    private fun requestViewMore(channel_id : Int, is_event : Int){
        val networkService : NetworkService = ApplicationController.instance.networkService
        val getChannelViewMoreResponse = networkService.getChannelViewMoreResponse(SharedPreferenceController.getFlag(this).toInt(), SharedPreferenceController.getAuthorization(this), channel_id, is_event)
        getChannelViewMoreResponse.enqueue(object : Callback<GetChannelViewMoreResponse> {
            override fun onFailure(call: Call<GetChannelViewMoreResponse>?, t: Throwable?) {
                Log.e("카테고리 더보기 페이지 실패", t.toString())
            }

            override fun onResponse(call: Call<GetChannelViewMoreResponse>?, response: Response<GetChannelViewMoreResponse>?) {
                response?.let {
                    if (response.isSuccessful){
                        viewMoreMoreDataList.addAll(response.body()!!.data)
                        setRecyclerView()
                    }
                }
            }
        })
    }

    private fun setRecyclerView(){
        rv_view_more_act_list.layoutManager = LinearLayoutManager(this)
        rv_view_more_act_list.adapter = viewMoreRecyclerViewAdapter
    }


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
}
