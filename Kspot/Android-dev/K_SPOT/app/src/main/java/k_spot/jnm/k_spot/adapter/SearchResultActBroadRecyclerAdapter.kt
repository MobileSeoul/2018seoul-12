package k_spot.jnm.k_spot.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import k_spot.jnm.k_spot.Delete.DeleteChannelScripteResponse
import k_spot.jnm.k_spot.Get.ChannelSearchResultData
import k_spot.jnm.k_spot.LoginActivity
import k_spot.jnm.k_spot.Network.ApplicationController
import k_spot.jnm.k_spot.Network.NetworkService
import k_spot.jnm.k_spot.Post.PostChannelSubscripeResponse
import k_spot.jnm.k_spot.R
import k_spot.jnm.k_spot.activity.CategoryDetailActivity
import k_spot.jnm.k_spot.db.SharedPreferenceController
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchResultActBroadRecyclerAdapter(private var searchBroadItems : ArrayList<ChannelSearchResultData>, private var context: Context, private var ItemCount: Int, private var onItemClick: View.OnClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val mainView : View = LayoutInflater.from(context).inflate(R.layout.rv_item_search_result_act_celeb_broad_result, parent, false)
        mainView.setOnClickListener(onItemClick)
        return SearchResultActBroadRecyclerAdapter.Holder(mainView)
    }

    override fun getItemCount(): Int {
        return ItemCount
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var holder : Holder = holder as Holder

        Glide.with(context)
                .load(searchBroadItems[position]!!.thumbnail_img)
                .into(holder.result_image)

        holder.result_name.text = searchBroadItems[position]!!.name

        holder.result_sub_num.text = searchBroadItems[position]!!.subscription_cnt.toString()

        holder.result_post_num.text = searchBroadItems[position]!!.spot_cnt.toString()


        if(searchBroadItems[position]!!.subscription == 0){
            holder.result_sub_btn_image.setImageResource(R.drawable.category_list_unsub_btn)
        }else{
            holder.result_sub_btn_image.setImageResource(R.drawable.category_list_sub_btn)
        }

        holder.btn.setOnClickListener {
            context.startActivity<CategoryDetailActivity>("channel_id" to searchBroadItems[position].channel_id.toString())
        }

        holder.result_sub_btn_btn.setOnClickListener {
//            if 조건문으로 구독 안한 flag 일 경우
//            subscription Flag 바꾸는 통신을 하고 한번 터치 시 tempFlag 값을 바꾸고
            if(searchBroadItems[position]!!.subscription == 0){
                if(SharedPreferenceController.getAuthorization(context).isNullOrBlank()){
                    context.startActivity<LoginActivity>("need_login_flag" to 1)
                }else{
                    holder.result_sub_btn_image.setImageResource(R.drawable.category_list_unsub_btn)
                    requestChannelSubscription(searchBroadItems[position].channel_id)
                    // 구독 신청 통신 필요
                    searchBroadItems[position]!!.subscription = 1
                }

            }else {
                if(SharedPreferenceController.getAuthorization(context).isNullOrBlank()){
                    context.startActivity<LoginActivity>("need_login_flag" to 1)
                }else{
                    holder.result_sub_btn_image.setImageResource(R.drawable.category_list_sub_btn)
                    deleteChannelSubscription(searchBroadItems[position].channel_id)
                    // 플래그 바꾸는 통신 필요
                    searchBroadItems[position]!!.subscription = 0
                }
            }
        }
    }


    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var result_image : ImageView = itemView!!.findViewById(R.id.search_result_act_rv_item_image_iv)
        var result_name : TextView = itemView!!.findViewById(R.id.search_result_act_rv_item_name_tv)
        var result_sub_num : TextView = itemView!!.findViewById(R.id.search_result_act_rv_item_subscriber_num_tv)
        var result_post_num : TextView = itemView!!.findViewById(R.id.search_result_act_rv_item_post_num_tv)
        var result_sub_btn_image : ImageView = itemView!!.findViewById(R.id.search_result_act_rv_item_subscribe_iv)
        var result_sub_btn_btn : RelativeLayout = itemView!!.findViewById(R.id.search_result_act_rv_item_subscribe_btn)
        var btn : RelativeLayout = itemView!!.findViewById(R.id.search_result_act_rv_item_btn)

    }

    private fun requestChannelSubscription(channel_id : Int){
        val networkService : NetworkService = ApplicationController.instance.networkService
        val postChannelSubscripeResponse = networkService.postChannelSubscripeResponse(SharedPreferenceController.getFlag(context).toInt(), SharedPreferenceController.getAuthorization(context), channel_id)
        postChannelSubscripeResponse.enqueue(object : Callback<PostChannelSubscripeResponse> {
            override fun onFailure(call: Call<PostChannelSubscripeResponse>?, t: Throwable?) {
                Log.e("구독하기 실패", t.toString())
            }
            override fun onResponse(call: Call<PostChannelSubscripeResponse>?, response: Response<PostChannelSubscripeResponse>?) {
                response?.let {
                    if (response.isSuccessful){
                        context.toast("구독")
                    }
                }
            }
        })
    }

    private fun deleteChannelSubscription(channel_id : Int){
        val networkService : NetworkService = ApplicationController.instance.networkService
        val deleteChannelScripteResponse = networkService.deleteChannelSubscripeResponse(SharedPreferenceController.getFlag(context).toInt(), SharedPreferenceController.getAuthorization(context), channel_id)
        deleteChannelScripteResponse.enqueue(object : Callback<DeleteChannelScripteResponse> {
            override fun onFailure(call: Call<DeleteChannelScripteResponse>?, t: Throwable?) {
                Log.e("구독 취소 하기 실패", t.toString())
            }

            override fun onResponse(call: Call<DeleteChannelScripteResponse>?, response: Response<DeleteChannelScripteResponse>?) {
                response?.let {
                    if (response.isSuccessful){
                        context.toast("구독 취소")
                    }
                }
            }
        })
    }
}

data class SearchResultActBroadRecyclerAdapterData(
        var Image : Int,
        var name : String,
        var sub_num : String,
        var post_num : String,
        var flag : Boolean
)