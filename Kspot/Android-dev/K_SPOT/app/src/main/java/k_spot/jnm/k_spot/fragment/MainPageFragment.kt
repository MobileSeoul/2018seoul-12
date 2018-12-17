package k_spot.jnm.k_spot.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import k_spot.jnm.k_spot.Get.GetMainFragResponse
import k_spot.jnm.k_spot.Get.Main
import k_spot.jnm.k_spot.Get.Theme
import k_spot.jnm.k_spot.Network.ApplicationController
import k_spot.jnm.k_spot.Network.NetworkService
import k_spot.jnm.k_spot.R
import k_spot.jnm.k_spot.SearchActivity
import k_spot.jnm.k_spot.activity.MainActivity
import k_spot.jnm.k_spot.adapter.MainFragCardViewAdapter
import k_spot.jnm.k_spot.adapter.MainFragViewPagerImageSliderAdapter
import k_spot.jnm.k_spot.db.SharedPreferenceController
import kotlinx.android.synthetic.main.fragment_main_page.*
import kotlinx.android.synthetic.main.fragment_main_page.view.*
import org.jetbrains.anko.support.v4.startActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response




class MainPageFragment : Fragment() {

    private var dotsCount: Int = 0
    private var currentPosition: Int = 0
    lateinit var MainFragViewPagerImageSliderAdapter: MainFragViewPagerImageSliderAdapter
//    lateinit var images: ArrayList<MainFragViewPagerData>
    lateinit var mainFragViewPagerItem : ArrayList<Theme>
    lateinit var mainFragRecommendSpotRecyclerItem : ArrayList<Main>
    lateinit var mainFragBestPlaceItem : ArrayList<Main>
    lateinit var mainFragEventItem : ArrayList<Main>
    lateinit var networkService: NetworkService


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {


        val view = inflater.inflate(R.layout.fragment_main_page, container, false)

        mainFragViewPagerItem = ArrayList()
        mainFragRecommendSpotRecyclerItem = ArrayList()
        mainFragBestPlaceItem = ArrayList()
        mainFragEventItem = ArrayList()
        MainFragViewPagerImageSliderAdapter = MainFragViewPagerImageSliderAdapter(this!!.context!!, mainFragViewPagerItem)
        setOnClickListener(view)
        getMainFragResonse(view)

        return view
    }
    private fun onClickChangeLanguageBtn(){
        (activity as MainActivity).changeMainActivityLanguage()
    }

    fun translateMainPageLanguage(){
        translateMainPage()
        requestMainPageResonse()
    }

    fun translateMainPage(){
        if (SharedPreferenceController.getFlag(context!!) == "0"){
            main_page_fragment_recycler1_tv1.text = "이번주 올라온"
            main_page_fragment_recycler1_tv2.text = "따끈따끈한 추천 PLACE"
            main_page_fragment_recycler2_tv1.text = "인기 장소 "
            main_page_fragment_recycler3_tv1.text = "이번 주 "
        } else {
            main_page_fragment_recycler1_tv1.text = "This week,"
            main_page_fragment_recycler1_tv2.text = "we recommend this PLACE"
            main_page_fragment_recycler2_tv1.text = "Hot place "
            main_page_fragment_recycler3_tv1.text = ""
        }
    }


    fun setOnClickListener(view : View) {
        view.main_page_fragment_search_btn.setOnClickListener {
            startActivity<SearchActivity>()
        }
    }

//    override fun onResume() {
//        super.onResume()
//
//        val handler = Handler()
//        handler.postDelayed(Runnable {
//            // Auto Scroll
//            MainFragViewPagerImageSliderAdapter!!.notifyDataSetChanged()
//            view!!.main_page_fragment_viewpager!!.setInterval(5000)
//            view!!.main_page_fragment_viewpager!!.startAutoScroll(1000)
//        }, 3000)
//    }

    override fun onPause() {
        super.onPause()
        view!!.main_page_fragment_viewpager!!.stopAutoScroll()
    }

    // ViewPager 생성 function
    fun makeViewPager(view: View, mainFragViewPagerItem: ArrayList<Theme> ) {

        // Auto Slider Adapter 적용
        MainFragViewPagerImageSliderAdapter = MainFragViewPagerImageSliderAdapter(this!!.context!!, mainFragViewPagerItem)
        view.main_page_fragment_viewpager.adapter = MainFragViewPagerImageSliderAdapter

        // 아래 세 줄 위 세 줄로 대체
        //view.homeslider = AutoScrollViewPager(this!!.context!!)
        //homeslider!!.setAdapter(MainFragViewPagerImageSliderAdapter)
        //setUiPageViewController

        // images 배열 사이즈로 인디케이터 사이즈 할당
        dotsCount = mainFragViewPagerItem.size

        // doutsCount 사이즈만큼의 이미지 뷰 배열 선언
        var dots: Array<ImageView?> = arrayOfNulls<ImageView>(dotsCount)
        // 인디케이터 점 생성
        for (i in 0 until dotsCount) {
            dots[i] = ImageView(activity)
            dots[i]!!.setImageDrawable(resources.getDrawable(R.drawable.nonselecteditem_dot))

            val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            )

            // 인디케이터 점 마진 설정
            params.setMargins(10, 0, 10, 0)
            //LinearView에 뷰 생성
            view.main_page_fragment_viewpager_count_dot_ll!!.addView(dots[i], params)
        }

        // 첫번째 indicater를 selecteditem_dot로 활성화
        dots[0]!!.setImageDrawable(resources.getDrawable(R.drawable.selecteditem_dot))

        // Viewpager가 Chager될 때 마다 인디케이터 점 변환
        view.main_page_fragment_viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            // 페이지가 선택되었을 때
            override fun onPageSelected(position: Int) {
                currentPosition = position
                for (i in 0 until dotsCount) {
                    dots[i]!!.setImageDrawable(resources.getDrawable(R.drawable.nonselecteditem_dot))

                }
                if (dots != null) {
                    dots[currentPosition % dotsCount
                    ]!!.setImageDrawable(resources.getDrawable(R.drawable.selecteditem_dot))
                }
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })

    }

    fun makeCardView(recyclerView: RecyclerView, dataSet : ArrayList<Main>, spotOrEvent: Int) {
        val mRecyclerView = recyclerView
//        val mRecyclerView = view.findViewById(R.id.main_page_fragment_rv1) as RecyclerView
        mRecyclerView.setHasFixedSize(true)

        val mLayoutManager = GridLayoutManager(this.getContext(), 1, GridLayoutManager.HORIZONTAL, false)

        mRecyclerView.layoutManager = mLayoutManager

        val mAdapter = MainFragCardViewAdapter(this!!.context!!, dataSet, spotOrEvent)
        mRecyclerView.adapter = mAdapter
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        translateMainPage()

        main_page_fragment_translation_btn.setOnClickListener {
            onClickChangeLanguageBtn()
        }

    }

    fun getMainFragResonse(view: View) {

        networkService = ApplicationController.instance.networkService
        val authorization: String = SharedPreferenceController.getAuthorization(context = this!!.context!!)
        val getMainFragResponse = networkService.getMainFrag(SharedPreferenceController.getFlag(context!!).toInt(), authorization)
        getMainFragResponse.enqueue(object : Callback<GetMainFragResponse> {
            override fun onFailure(call: Call<GetMainFragResponse>?, t: Throwable?) {
            }

            override fun onResponse(call: Call<GetMainFragResponse>?, response: Response<GetMainFragResponse>?) {
                if (response!!.isSuccessful) {
                    mainFragViewPagerItem = response!!.body()!!.data!!.theme

                    mainFragRecommendSpotRecyclerItem = response!!.body()!!.data!!.main_recommand_spot
                    mainFragBestPlaceItem = response!!.body()!!.data!!.main_best_place
                    mainFragEventItem = response!!.body()!!.data!!.main_best_event

                    val mRecyclerView1 = view.findViewById(R.id.main_page_fragment_rv1) as RecyclerView
                    // 첫 번째 CardView 생성 function
                    makeCardView(mRecyclerView1, mainFragRecommendSpotRecyclerItem, 0)

                    val mRecyclerView2 = view.findViewById(R.id.main_page_fragment_rv2) as RecyclerView

                    makeCardView(mRecyclerView2, mainFragRecommendSpotRecyclerItem, 0)

                    val mRecyclerView3 = view.findViewById(R.id.main_page_fragment_rv3) as RecyclerView

                    makeCardView(mRecyclerView3, mainFragEventItem, 1)
                    makeViewPager(view ,mainFragViewPagerItem)
                    // Auto Scroll
                    MainFragViewPagerImageSliderAdapter!!.notifyDataSetChanged()
                    view.main_page_fragment_viewpager!!.setInterval(5000)
                    view.main_page_fragment_viewpager!!.startAutoScroll(1000)

                }
            }

        })
    }

    fun requestMainPageResonse() {
        networkService = ApplicationController.instance.networkService
        val authorization: String = SharedPreferenceController.getAuthorization(context = this!!.context!!)
        val getMainFragResponse = networkService.getMainFrag(SharedPreferenceController.getFlag(context!!).toInt(), authorization)
        getMainFragResponse.enqueue(object : Callback<GetMainFragResponse> {
            override fun onFailure(call: Call<GetMainFragResponse>?, t: Throwable?) {
            }

            override fun onResponse(call: Call<GetMainFragResponse>?, response: Response<GetMainFragResponse>?) {
                if (response!!.isSuccessful) {
                    main_page_fragment_viewpager_count_dot_ll!!.removeAllViews()
                    mainFragViewPagerItem = response!!.body()!!.data!!.theme

                    mainFragRecommendSpotRecyclerItem = response!!.body()!!.data!!.main_recommand_spot
                    mainFragBestPlaceItem = response!!.body()!!.data!!.main_best_place
                    mainFragEventItem = response!!.body()!!.data!!.main_best_event



                    // 첫 번째 CardView 생성 function
                    makeCardView(main_page_fragment_rv1, mainFragRecommendSpotRecyclerItem, 0)

                    makeCardView(main_page_fragment_rv2, mainFragRecommendSpotRecyclerItem, 0)

                    makeCardView(main_page_fragment_rv3, mainFragEventItem, 1)
                    makeViewPager(view!! ,mainFragViewPagerItem)
                    // Auto Scroll
                    MainFragViewPagerImageSliderAdapter!!.notifyDataSetChanged()
                    view!!.main_page_fragment_viewpager!!.setInterval(5000)
                    view!!.main_page_fragment_viewpager!!.startAutoScroll(1000)
                }
            }

        })
    }


}
