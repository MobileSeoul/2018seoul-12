package k_spot.jnm.k_spot.activity

import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.view.WindowManager
import k_spot.jnm.k_spot.Get.GetUserScapListResponse
import k_spot.jnm.k_spot.Get.ViewMoreData
import k_spot.jnm.k_spot.Network.ApplicationController
import k_spot.jnm.k_spot.R
import k_spot.jnm.k_spot.adapter.ViewMoreRecyclerViewAdapter
import k_spot.jnm.k_spot.db.SharedPreferenceController
import kotlinx.android.synthetic.main.activity_user_scrap_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserScrapListActivity : AppCompatActivity() {

    val userScrapDataList : ArrayList<ViewMoreData> by lazy {
        ArrayList<ViewMoreData>()
    }

    val userScrapRecyclerViewAdapter : ViewMoreRecyclerViewAdapter by lazy {
        ViewMoreRecyclerViewAdapter(this, userScrapDataList)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_scrap_list)
        setStatusBarTransparent()
        setViewAndClickListener()

        requestUserScapData()



    }

    private fun requestUserScapData(){
        val networkService = ApplicationController.instance.networkService
        val getUserScapListResponse = networkService.getUserScapListResponse(SharedPreferenceController.getFlag(this).toInt(),
                SharedPreferenceController.getAuthorization(this))
        getUserScapListResponse.enqueue(object : Callback<GetUserScapListResponse>{
            override fun onFailure(call: Call<GetUserScapListResponse>?, t: Throwable?) {
                Log.e("유저 스크랩 리스트 요청 실패", t.toString())
            }
            override fun onResponse(call: Call<GetUserScapListResponse>?, response: Response<GetUserScapListResponse>?) {
                response?.let {
                    if (response.isSuccessful){
                        response.body()?.let {
                            userScrapDataList.addAll(it.data)
                            setRecyclerView()
                        }

                    }
                }
            }
        })
    }


    private fun setRecyclerView(){
        rv_user_scrap_list_act_list.layoutManager = LinearLayoutManager(this)
        rv_user_scrap_list_act_list.adapter = userScrapRecyclerViewAdapter
    }

    private fun setViewAndClickListener(){

        if (SharedPreferenceController.getFlag(this) == "0"){
            tv_user_scrap_list_act_title.text = "스크랩"
        }else{
            tv_user_scrap_list_act_title.text = "Scrap"
        }


        btn_user_scrap_list_act_back.setOnClickListener {
            finish()
        }
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
