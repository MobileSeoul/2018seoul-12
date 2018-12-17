package k_spot.jnm.k_spot

import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import k_spot.jnm.k_spot.Get.GetSpotViewReviewMoreResponse
import k_spot.jnm.k_spot.Get.ReviewMoreData
import k_spot.jnm.k_spot.Get.SpotViewReviewMoreData
import k_spot.jnm.k_spot.Network.ApplicationController
import k_spot.jnm.k_spot.Network.NetworkService
import k_spot.jnm.k_spot.adapter.ReviewMoreActivityRecyclerAdapter
import k_spot.jnm.k_spot.db.SharedPreferenceController
import kotlinx.android.synthetic.main.activity_review_more.*
import org.jetbrains.anko.startActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReviewMoreActivity : AppCompatActivity() {

    lateinit var reviewMoreActivityRecyclerAdapter: ReviewMoreActivityRecyclerAdapter
    lateinit var SpotViewReviewMoreData : SpotViewReviewMoreData
    lateinit var ReviewMoreData : java.util.ArrayList<ReviewMoreData>
    lateinit var networkService: NetworkService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review_more)
        var spot_id=intent.getIntExtra("spot_id",0)
        Log.v("123123",spot_id.toString())
        getSpotReViewMore(spot_id)
        setOnClickListenr(spot_id)

        // 상태바 투명 fun
        setStatusBarTransparent()
    }

    fun getSpotReViewMore(spot_id : Int) {
        networkService = ApplicationController.instance.networkService
        Log.v("123123",spot_id.toString())
        val authorization: String = SharedPreferenceController.getAuthorization(context = applicationContext)
        val getSpotViewReviewMoreResponse = networkService.getSpotReViewMore(0, authorization, spot_id)
        getSpotViewReviewMoreResponse.enqueue(object : Callback<GetSpotViewReviewMoreResponse> {
            override fun onFailure(call: Call<GetSpotViewReviewMoreResponse>?, t: Throwable?) {
                Log.v("123123","통신실패")
            }
            override fun onResponse(call: Call<GetSpotViewReviewMoreResponse>?, response: Response<GetSpotViewReviewMoreResponse>?) {
                if (response!!.isSuccessful) {
                    SpotViewReviewMoreData = response!!.body()!!.data!!
                    ReviewMoreData = ArrayList()
                    ReviewMoreData = response!!.body()!!.data!!.reviews

                    review_more_act_review_num_tv.text = SpotViewReviewMoreData.spot_review.review_cnt.toString()

                    review_more_act_score_tv.text = SpotViewReviewMoreData.spot_review.review_score.toString()

                    Log.v("123123",SpotViewReviewMoreData.spot_review.review_score.toString())

                    makeReviewMoreStar(SpotViewReviewMoreData.spot_review.review_score)

                    // 리뷰 리사이클러뷰 가져오기
                    makeRecyclerView(ReviewMoreData)
                }
            }

        })
    }

    private fun makeRecyclerView(reviewSpotViewMoreData : ArrayList<ReviewMoreData>) {

        val mRecyclerView = review_more_act_rv as RecyclerView
//        val mRecyclerView = view.findViewById(R.id.main_page_fragment_rv1) as RecyclerView
        mRecyclerView.setHasFixedSize(true)
        reviewMoreActivityRecyclerAdapter = ReviewMoreActivityRecyclerAdapter(reviewSpotViewMoreData, applicationContext)
//        reviewMoreActivityRecyclerAdapter.setOnItemClickListener()
        mRecyclerView.layoutManager = LinearLayoutManager(applicationContext)
        mRecyclerView.adapter = reviewMoreActivityRecyclerAdapter
    }

    fun setOnClickListenr(spot_id : Int) {
        review_more_act_back_btn.setOnClickListener {
            finish()
        }

        review_more_act_review_write_btn.setOnClickListener {
            if(SharedPreferenceController.getAuthorization(applicationContext).isNullOrBlank()){
                startActivity<LoginActivity>("need_login_flag" to 1)
            }else{
                startActivity<ReviewWriteActivity>("spot_id" to spot_id)
            }
        }
    }

    // 큰 별 리뷰 만들기
    fun makeReviewMoreStar(starCount : Double) {
        // size 5의 이미지 뷰 배열 생성
        var stars: Array<ImageView?> = arrayOfNulls<ImageView>(5)
        // star 생성
        for (i in 0 until 5) {

            if (i < starCount) {

                // 별 반개를 표현 해야 할 때
                if (0.5 <= (starCount - i) && (starCount - i) <= 0.99) {
                    // 별 반개 그리는거
                    stars[i] = ImageView(applicationContext)
                    stars[i]!!.setImageDrawable(resources.getDrawable(R.drawable.category_reveiw_big_halfstar))
                } else if (0 <= (starCount - i) && (starCount - i) < 0.5) {
                    // 마지막 별이 0~0.5일 때 꽉찬 별 그리는 거
                    stars[i] = ImageView(applicationContext)
                    stars[i]!!.setImageDrawable(resources.getDrawable(R.drawable.category_reveiw_big_star_empty))
                } else {
                    // 꽉찬 별을 표현 해야 할 때
                    stars[i] = ImageView(applicationContext)
                    stars[i]!!.setImageDrawable(resources.getDrawable(R.drawable.category_reveiw_big_star))
                }

            } else {
                // 빈 별
                stars[i] = ImageView(applicationContext)
                stars[i]!!.setImageDrawable(resources.getDrawable(R.drawable.category_reveiw_big_star_empty))
            }
            val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            )

            // 인디케이터 점 마진 설정
            params.setMargins(3, 0, 3, 0)
            //LinearView에 뷰 생성
            review_more_act_review_star_ll!!.addView(stars[i], params)
        }


        //
        review_more_act_score_tv.text = starCount.toString()
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
