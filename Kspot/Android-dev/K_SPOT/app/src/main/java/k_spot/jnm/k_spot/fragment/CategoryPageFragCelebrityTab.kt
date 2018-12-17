package k_spot.jnm.k_spot.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import k_spot.jnm.k_spot.Get.ChannelListData
import k_spot.jnm.k_spot.Get.GetCategoryListResponse
import k_spot.jnm.k_spot.Network.ApplicationController
import k_spot.jnm.k_spot.Network.NetworkService
import k_spot.jnm.k_spot.R
import k_spot.jnm.k_spot.adapter.CategoryPageFragRecyclerAdapter
import k_spot.jnm.k_spot.db.SharedPreferenceController
import kotlinx.android.synthetic.main.fragment_category_list_celebrity_tab_.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response



class CategoryPageFragCelebrityTab : Fragment() {

    lateinit var networkService: NetworkService
    lateinit var channelCelebrityList: ArrayList<ChannelListData>
    lateinit var categoryPageFragRecyclerAdapter: CategoryPageFragRecyclerAdapter


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_category_list_celebrity_tab_, container, false)

        channelCelebrityList = ArrayList()
        categoryPageFragRecyclerAdapter = CategoryPageFragRecyclerAdapter(channelCelebrityList, context!!)
        getCategoryList(context!!, view)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    private fun getCategoryList(ctx : Context, view : View) {
        networkService = ApplicationController.instance.networkService
        val authorization: String = SharedPreferenceController.getAuthorization(activity!!)
        val getCategoryListResponse = networkService.getCategoryList(SharedPreferenceController.getFlag(context!!).toInt(), authorization)

        getCategoryListResponse.enqueue(object : Callback<GetCategoryListResponse> {
            override fun onFailure(call: Call<GetCategoryListResponse>?, t: Throwable?) {
            }
            override fun onResponse(call: Call<GetCategoryListResponse>?, response: Response<GetCategoryListResponse>?) {
                if(response!!.isSuccessful){

                    if(response!!.body()!!.data!!.channel_celebrity_list.size == 0) {
                        Log.v("xx","Xxx")
                    }else{
                        channelCelebrityList = response!!.body()!!.data!!.channel_celebrity_list
                        categoryPageFragRecyclerAdapter = CategoryPageFragRecyclerAdapter(channelCelebrityList, ctx)
                        view.category_list_celebrity_fragment_tab_rv.layoutManager = LinearLayoutManager(ctx)
                        view.category_list_celebrity_fragment_tab_rv.adapter = categoryPageFragRecyclerAdapter
                    }

                }
            }

        })
        Log.e("유명인사 첫 리퀘스트 시작!", "t시작!")
    }
    fun requestCategoryList() {
        networkService = ApplicationController.instance.networkService
        val authorization: String = SharedPreferenceController.getAuthorization(context!!)
        val getCategoryListResponse = networkService.getCategoryList(SharedPreferenceController.getFlag(context!!).toInt(), authorization)

        getCategoryListResponse.enqueue(object : Callback<GetCategoryListResponse> {
            override fun onFailure(call: Call<GetCategoryListResponse>?, t: Throwable?) {
            }
            override fun onResponse(call: Call<GetCategoryListResponse>?, response: Response<GetCategoryListResponse>?) {
                if(response!!.isSuccessful){

                    if(response!!.body()!!.data!!.channel_celebrity_list.size == 0) {
                        Log.v("xx","Xxx")
                    }else{
                        channelCelebrityList = response!!.body()!!.data!!.channel_celebrity_list
                        categoryPageFragRecyclerAdapter = CategoryPageFragRecyclerAdapter(channelCelebrityList, context!!)
                        view!!.category_list_celebrity_fragment_tab_rv.layoutManager = LinearLayoutManager(context)
                        view!!.category_list_celebrity_fragment_tab_rv.adapter = categoryPageFragRecyclerAdapter
                    }

                }
            }

        })
    }
}
