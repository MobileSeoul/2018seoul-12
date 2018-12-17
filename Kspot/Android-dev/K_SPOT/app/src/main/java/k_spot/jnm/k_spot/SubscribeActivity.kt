package k_spot.jnm.k_spot

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import k_spot.jnm.k_spot.Get.BroadcastData
import k_spot.jnm.k_spot.Get.GetUserSubscribeResponse
import k_spot.jnm.k_spot.Network.ApplicationController
import k_spot.jnm.k_spot.Network.NetworkService
import k_spot.jnm.k_spot.adapter.SubscribeActRecyclerViewAdapter
import k_spot.jnm.k_spot.db.SharedPreferenceController
import kotlinx.android.synthetic.main.activity_subscribe.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SubscribeActivity : AppCompatActivity() {

    lateinit var networkService: NetworkService

    // tabFlag가 true일 땐 연예인 탭 활성화
    // tabFlag가 false일 땐 방송 탭 활성화
    var tabFlag = true

    // 초록색
    var tabActiveColor = Color.parseColor("#40D39F")

    // 검정색
    var tabUnActiveColor = Color.parseColor("#000000")

    lateinit var subscribeActBroadCastTabItems: ArrayList<BroadcastData>
    lateinit var subscribeActCelebTabItems: ArrayList<BroadcastData>
    lateinit var subscribeActRecyclerViewAdapter: SubscribeActRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subscribe)
        subscribeActCelebTabItems = ArrayList()
        subscribeActBroadCastTabItems = ArrayList()
        subscribeActRecyclerViewAdapter = SubscribeActRecyclerViewAdapter(subscribeActCelebTabItems, applicationContext)
        getSubscribeCeleb()
        setOnClickListener()

        setInitLanguage()
    }

    private fun setInitLanguage(){
        if (SharedPreferenceController.getFlag(this) == "0"){
            tv_subscripe_title.text = "구독"
            subscribe_act_celeb_tv.text = "연예인"
            subscribe_act_broadcast_tv.text = "방송"
        } else {
            tv_subscripe_title.text = "Subscribe"
            subscribe_act_celeb_tv.text = "Celebrity"
            subscribe_act_broadcast_tv.text = "Broadcast "
        }
    }

    fun setOnClickListener() {

        // 방송 탭 버튼
        subscribe_act_broadcast_btn.setOnClickListener {

            // tabFlag = true (연예인 탭 활성화)
            // 연예인 탭이 활성화 돼있는 경우에만 애니메이션 구동
            if(tabFlag == true){

                // 탭바를 오른쪽 방송 탭 밑으로 이동!
                clickBoradcastTabAnimation()

                // 방송 tv를 Green으로 0.3초동안 서서히 변경!
                convertGreenAnimation(subscribe_act_broadcast_tv)

                // 연예인 tv를 Black으로 0.3초동안 서서히 변경!
                convertBlackAnimation(subscribe_act_celeb_tv)

                tabFlag = false

                if(subscribeActBroadCastTabItems.size  != 0){
                    makeSubscribeActRecyclerView(subscribeActBroadCastTabItems)
                }
            }
        }

        // 연예인 탭 버튼
        subscribe_act_celeb_btn.setOnClickListener {

            // tabFlag = false (방송 탭 활성화)
            // 방송 탭이 활성화 돼있는 경우에만 애니메이션 구동
            if(tabFlag == false) {

                // 탭바를 왼쪽 연예인 탭 밑으로 0.3초동안 이동!
                clickCelebTabAnimation()

                // 연예인 tv를 Green으로 0.3초동안 서서히 변경!
                convertGreenAnimation(subscribe_act_celeb_tv)

                // 방송 tv를 Black으로 0.3초동안 서서히 변경!
                convertBlackAnimation(subscribe_act_broadcast_tv)

                tabFlag = true

                if(subscribeActBroadCastTabItems.size  != 0){
                    makeSubscribeActRecyclerView(subscribeActCelebTabItems)
                }

            }
        }

        // 백 버튼
        subscribe_act_back_btn.setOnClickListener {
            finish()
        }




    }

    // 통신
    private fun getSubscribeCeleb() {
        val authorization: String = SharedPreferenceController.getAuthorization(context = applicationContext)
        networkService = ApplicationController.instance.networkService
        val getUserSubscribeResponse = networkService.getUserSubscribe(SharedPreferenceController.getFlag(this).toInt(), authorization)
        getUserSubscribeResponse.enqueue(object : Callback<GetUserSubscribeResponse> {
            override fun onFailure(call: Call<GetUserSubscribeResponse>?, t: Throwable?) {
            }
            override fun onResponse(call: Call<GetUserSubscribeResponse>?, response: Response<GetUserSubscribeResponse>?) {
                if(response!!.isSuccessful){
                    // 검색 결과 없을 시
                    if(response!!.body()!!.data!!.celebrity.size == 0 && response!!.body()!!.data!!.broadcast.size == 0) {
                        subscribe_act_noting_result.visibility = View.VISIBLE
                        subscribe_act_rv.visibility = View.GONE
                    }
                    subscribeActBroadCastTabItems = response!!.body()!!.data!!.broadcast
                    subscribeActCelebTabItems = response!!.body()!!.data!!.celebrity

                    if(subscribeActBroadCastTabItems.size  != 0){
                        makeSubscribeActRecyclerView(subscribeActCelebTabItems)
                    }

                }
            }

        })
    }

    // 탭바를 오른쪽 방송 탭 밑으로 이동!
    private fun clickBoradcastTabAnimation(){
        val anim = AnimationUtils
                .loadAnimation(applicationContext,
                        R.anim.subscribe_tab_convert_broadcast_anim)
        subscribe_act_tab_line.startAnimation(anim)
        subscribe_act_tab_line.visibility = View.GONE
    }

    private fun clickCelebTabAnimation(){
        val anotherAnim = AnimationUtils
                .loadAnimation(applicationContext,
                        R.anim.subscribe_tab_convert_celeb_anim)
        subscribe_act_tab_line.startAnimation(anotherAnim)
        subscribe_act_tab_line.visibility = View.VISIBLE
    }

    // tv를 Green으로 0.3초동안 서서히 변경!
    private fun convertGreenAnimation(textView: TextView){
        val convertGreenAnimation = ValueAnimator.ofObject(ArgbEvaluator(), tabUnActiveColor, tabActiveColor)
        convertGreenAnimation.duration = 300
        convertGreenAnimation.addUpdateListener { animator -> textView.setTextColor(animator.animatedValue as Int)}
        convertGreenAnimation.start()
    }

    // tv를 Black으로 0.3초동안 서서히 변경!
    private fun convertBlackAnimation(textView: TextView){
        val convertBlackAnimation = ValueAnimator.ofObject(ArgbEvaluator(), tabActiveColor, tabUnActiveColor)
        convertBlackAnimation.duration = 300
        convertBlackAnimation.addUpdateListener { animator -> textView.setTextColor(animator.animatedValue as Int)}
        convertBlackAnimation.start()
    }

    private fun makeSubscribeActRecyclerView(subscribeActItems : ArrayList<BroadcastData>) {
        subscribeActRecyclerViewAdapter = SubscribeActRecyclerViewAdapter(subscribeActItems, applicationContext)
        subscribe_act_rv.layoutManager = LinearLayoutManager(applicationContext)
        subscribe_act_rv.adapter = subscribeActRecyclerViewAdapter
    }


}
