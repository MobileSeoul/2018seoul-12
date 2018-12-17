package k_spot.jnm.k_spot.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.view.WindowManager
import com.bumptech.glide.Glide
import k_spot.jnm.k_spot.Delete.DeleteChannelScripteResponse
import k_spot.jnm.k_spot.Get.ChannelInfoData
import k_spot.jnm.k_spot.Get.GetCategoryDetailResponse
import k_spot.jnm.k_spot.Get.PlaceOrEventRelativeData
import k_spot.jnm.k_spot.Get.PlaceRecommendData
import k_spot.jnm.k_spot.LoginActivity
import k_spot.jnm.k_spot.Network.ApplicationController
import k_spot.jnm.k_spot.Network.NetworkService
import k_spot.jnm.k_spot.Post.PostChannelSubscripeResponse
import k_spot.jnm.k_spot.R
import k_spot.jnm.k_spot.adapter.PlaceRecommendRecyclerViewAdapter
import k_spot.jnm.k_spot.adapter.RelativePlaceOrEventRecyclerViewAdapter
import k_spot.jnm.k_spot.db.SharedPreferenceController
import kotlinx.android.synthetic.main.activity_detailed_entertainer_page.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CategoryDetailActivity : AppCompatActivity() {

    lateinit var channelInfoData : ChannelInfoData
    var channel_id : Int = 1

    val placeRecommendData : ArrayList<PlaceRecommendData> by lazy {
        ArrayList<PlaceRecommendData>()
    }
    val placePlaceRecommendRecyclerViewAdapter : PlaceRecommendRecyclerViewAdapter by lazy {
        PlaceRecommendRecyclerViewAdapter(this, placeRecommendData)
    }
    val relativePlaceDataList : ArrayList<PlaceOrEventRelativeData> by lazy {
        ArrayList<PlaceOrEventRelativeData>()
    }
    val relativePlaceRecyclerViewAdapter : RelativePlaceOrEventRecyclerViewAdapter by lazy {
        RelativePlaceOrEventRecyclerViewAdapter(this, relativePlaceDataList)
    }

    val relativeEventDataList : ArrayList<PlaceOrEventRelativeData> by lazy {
        ArrayList<PlaceOrEventRelativeData>()
    }
    val relativeEventRecyclerViewAdapter : RelativePlaceOrEventRecyclerViewAdapter by lazy {
        RelativePlaceOrEventRecyclerViewAdapter(this, relativeEventDataList)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_detailed_entertainer_page)
        hideActionBar()
        setStatusBarColor()


        channel_id = intent.getStringExtra("channel_id").toInt()


        //인텐트 받기
        requestDataList(channel_id)

    }
    private fun changeLanguage(){
        if (SharedPreferenceController.getFlag(this) == "0"){
            btn_category_detail_subscrip.setImageResource(R.drawable.search_page_unsub_btn)
        }

    }

    private fun requestDataList(channel_id : Int){
        val networkService : NetworkService = ApplicationController.instance.networkService
        val getCategoryDetailResponse = networkService.getCategotyDetailResponse(SharedPreferenceController.getFlag(this).toInt(),SharedPreferenceController.getAuthorization(this), channel_id)
        getCategoryDetailResponse.enqueue(object : Callback<GetCategoryDetailResponse>{
            override fun onFailure(call: Call<GetCategoryDetailResponse>?, t: Throwable?) {
                Log.e("카테고리 상세보기 페이지", t.toString())
            }

            override fun onResponse(call: Call<GetCategoryDetailResponse>?, response: Response<GetCategoryDetailResponse>?) {
                response?.let {
                    if (response.isSuccessful) {
                        //상세페이지 정보
                        channelInfoData = response.body()!!.data.channel_info
                        setInitView()
                        //추천 스팟
                        placeRecommendData.addAll(response.body()!!.data.place_recommended_by_channel)
                        setRecommendSpotRecyclerView()

                        //관련 스팟
                        relativePlaceDataList.addAll(response.body()!!.data.place_related_channel)
                        setRelativeSpotRecyclerView()
                        //이벤트 스팟
                        relativeEventDataList.addAll(response.body()!!.data.event_related_channel)
                        setRelativeEventRecyclerView()


                        //클릭 리스너
                        setInitClickListener()
                    }
                }
            }
        })
    }



    private fun requestChannelSubscription(channel_id : Int){
        val networkService : NetworkService = ApplicationController.instance.networkService
        val postChannelSubscripeResponse = networkService.postChannelSubscripeResponse(SharedPreferenceController.getFlag(this).toInt(), SharedPreferenceController.getAuthorization(this), channel_id)
        postChannelSubscripeResponse.enqueue(object : Callback<PostChannelSubscripeResponse>{
            override fun onFailure(call: Call<PostChannelSubscripeResponse>?, t: Throwable?) {
                Log.e("구독하기 실패", t.toString())
            }
            override fun onResponse(call: Call<PostChannelSubscripeResponse>?, response: Response<PostChannelSubscripeResponse>?) {
                response?.let {
                    if (response.isSuccessful){
                        channelInfoData.subscription = 1

                        if (SharedPreferenceController.getFlag(this@CategoryDetailActivity) == "0"){
                            btn_category_detail_subscrip.setImageResource(R.drawable.category_detail_sub_btn)
                            toast("구독")
                        } else {
                            btn_category_detail_subscrip.setImageResource(R.drawable.star_page_sub_btn_en)
                            toast("Subscribe Complete")
                        }
                        channelInfoData.subscription_cnt = channelInfoData.subscription_cnt + 1
                        tv_category_detail_subscripe_cnt.text = String.format("%,d", channelInfoData.subscription_cnt)

                    }
                }
            }
        })
    }

    private fun deleteChannelSubscription(channel_id : Int){
        val networkService : NetworkService = ApplicationController.instance.networkService
        val deleteChannelScripteResponse = networkService.deleteChannelSubscripeResponse(SharedPreferenceController.getFlag(this).toInt(), SharedPreferenceController.getAuthorization(this), channel_id)
        deleteChannelScripteResponse.enqueue(object : Callback<DeleteChannelScripteResponse> {
            override fun onFailure(call: Call<DeleteChannelScripteResponse>?, t: Throwable?) {
                Log.e("구독 취소 하기 실패", t.toString())
            }

            override fun onResponse(call: Call<DeleteChannelScripteResponse>?, response: Response<DeleteChannelScripteResponse>?) {
                response?.let {
                    if (response.isSuccessful){
                        channelInfoData.subscription = 0

                        if (SharedPreferenceController.getFlag(this@CategoryDetailActivity) == "0"){
                            btn_category_detail_subscrip.setImageResource(R.drawable.category_detail_unsub_btn)
                            toast("구독 취소")
                        } else {
                            btn_category_detail_subscrip.setImageResource(R.drawable.star_page_unsub_btn_en)
                            toast("Subscribe Cancel")
                        }

                        channelInfoData.subscription_cnt = channelInfoData.subscription_cnt - 1
                        tv_category_detail_subscripe_cnt.text = String.format("%,d", channelInfoData.subscription_cnt)
                    }
                }
            }
        })
    }


    private fun setInitView(){
        tv_category_detail_title_name.text = channelInfoData.name
        tv_category_detail_company_name.text = channelInfoData.company
        Glide.with(this).load(channelInfoData.thumbnail_img).into(iv_category_detail_mark_img)
        Glide.with(this).load(channelInfoData.background_img).into(iv_category_detail_top_img)
        tv_category_detail_subscripe_cnt.text = String.format("%,d", channelInfoData.subscription_cnt)

//        tv_category_detail_recommend_spot_title.text = "${channelInfoData.name}'s 추천 장소"

        //추후 구독 색상변경 바꾸기
        if (SharedPreferenceController.getFlag(this) == "0"){
            if (channelInfoData.subscription == 0){
                btn_category_detail_subscrip.setImageResource(R.drawable.category_detail_unsub_btn)
            } else {
                btn_category_detail_subscrip.setImageResource(R.drawable.category_detail_sub_btn)
            }
            tv_category_detail_recommend_spot_title.text = "${channelInfoData.name}'s 추천 장소"
            tv_category_detail_recommend_spot_title_comment.text = "사람들이 많이 찾는 장소를 확인해 보세요!"
            tv_category_detail_relative_spot_title.text = "관련된 장소"
            btn_category_detail_spot_more_btn.text = "더보기"
            tv_category_detail_event_more_title.text = "관련된 이벤트"
            btn_category_detail_event_more_btn.text = "더보기"
        } else {
            if (channelInfoData.subscription == 0){
                btn_category_detail_subscrip.setImageResource(R.drawable.star_page_unsub_btn_en)
            } else {
                btn_category_detail_subscrip.setImageResource(R.drawable.star_page_sub_btn_en)
            }

            tv_category_detail_recommend_spot_title.text = "${channelInfoData.name}’s recommended place"
            tv_category_detail_recommend_spot_title_comment.text = "Check out the places people are looking for!"
            tv_category_detail_relative_spot_title.text = "related place"
            btn_category_detail_spot_more_btn.text = "MORE"
            tv_category_detail_event_more_title.text = "related event"
            btn_category_detail_event_more_btn.text = "MORE"
        }
    }

    private fun setInitClickListener(){
        btn_category_detail_spot_more_btn.setOnClickListener {
            startActivity<ViewMoreActivity>("channel_id" to channel_id, "is_event" to 0)
        }

        btn_category_detail_event_more_btn.setOnClickListener {
            startActivity<ViewMoreActivity>("channel_id" to channel_id, "is_event" to 1)
        }

        btn_category_detail_subscrip.setOnClickListener {
            setSubscriptionBtnClickListener()
        }
        iv_category_detail_back_btn.setOnClickListener {
            finish()
        }
    }

    //데이터 전송 후 부터 사용
    private fun setSubscriptionBtnClickListener(){
        if (channelInfoData.subscription == 0) {
            if(SharedPreferenceController.getAuthorization(applicationContext).isNullOrBlank()){
                startActivity<LoginActivity>("need_login_flag" to 1)
            }else{
                requestChannelSubscription(channel_id)
            }
        } else {
            if(SharedPreferenceController.getAuthorization(applicationContext).isNullOrBlank()){
                startActivity<LoginActivity>("need_login_flag" to 1)
            }else{
                deleteChannelSubscription(channel_id)
            }
        }
    }


    private fun setRecommendSpotRecyclerView(){
        rv_category_detail_recommend_spot_list.layoutManager = LinearLayoutManager(this, 0, false)
        rv_category_detail_recommend_spot_list.adapter = placePlaceRecommendRecyclerViewAdapter
    }

    private fun setRelativeSpotRecyclerView(){
        rv_category_detail_relative_spot_list.layoutManager = LinearLayoutManager(this)
        rv_category_detail_relative_spot_list.adapter = relativePlaceRecyclerViewAdapter
    }
    private fun setRelativeEventRecyclerView(){
        rv_category_detail_relative_event_list.layoutManager = LinearLayoutManager(this)
        rv_category_detail_relative_event_list.adapter = relativeEventRecyclerViewAdapter
    }

    private fun setStatusBarColor() {
        val view: View? = window.decorView
        view!!.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)

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

    private fun hideActionBar(){
        supportActionBar?.let {
            it.hide()
        }
    }
}
