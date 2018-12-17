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
import k_spot.jnm.k_spot.Get.ChannelRecyclerViewData
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

class SpotViewMoreActCardViewAdapter(val ctx : Context, val myDataset : ArrayList<ChannelRecyclerViewData>) : RecyclerView.Adapter<SpotViewMoreActCardViewAdapter.ViewHolder>() {

    lateinit var mDataset: ArrayList<ChannelRecyclerViewData>


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder{
        mDataset = myDataset
        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.rv_item_view_more_act, parent, false)
        val vh = ViewHolder(v)
        return vh
    }

    override fun getItemCount(): Int {
        mDataset = myDataset
        return mDataset.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val dp = ctx.resources.displayMetrics.density
        val rootLayoutParams : RelativeLayout.LayoutParams = holder.rl.layoutParams as RelativeLayout.LayoutParams
        if (position == 0) {
            rootLayoutParams.leftMargin = (16*dp).toInt()
        }

        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.title.setText(mDataset[position].channel_name)
        Glide.with(ctx).load(mDataset[position].thumbnail_img).into(holder.mImageView)

        // sub가 안됐을 때
        if(mDataset[position].is_subscription == "0"){
            if (SharedPreferenceController.getFlag(ctx) == "0"){
                holder.subscribeBtn.setImageResource(R.drawable.category_list_unsub_btn)
            } else {
                holder.subscribeBtn.setImageResource(R.drawable.category_list_unsub_btn_en)
            }
        }else{
            if (SharedPreferenceController.getFlag(ctx) == "0"){
                holder.subscribeBtn.setImageResource(R.drawable.category_list_sub_btn)
            } else {
                holder.subscribeBtn.setImageResource(R.drawable.category_list_sub_btn_en)
            }
        }

        holder.subscribeBtn.setOnClickListener {
            // sub가 안됐을 때
            if(mDataset[position].is_subscription == "0"){
                if(SharedPreferenceController.getAuthorization(ctx).isNullOrBlank()){
                    ctx.startActivity<LoginActivity>("need_login_flag" to 1)
                }else{
                    if (SharedPreferenceController.getFlag(ctx) == "0"){
                        holder.subscribeBtn.setImageResource(R.drawable.category_list_sub_btn)
                    } else {
                        holder.subscribeBtn.setImageResource(R.drawable.category_list_sub_btn_en)
                    }
                    mDataset[position].is_subscription = "1"
                    requestChannelSubscription(mDataset[position].channel_id.toInt())
                }
            }else{
                if(SharedPreferenceController.getAuthorization(ctx).isNullOrBlank()){
                    ctx.startActivity<LoginActivity>("need_login_flag" to 1)
                }else{
                    if (SharedPreferenceController.getFlag(ctx) == "0"){
                        holder.subscribeBtn.setImageResource(R.drawable.category_list_unsub_btn)
                    } else {
                        holder.subscribeBtn.setImageResource(R.drawable.category_list_unsub_btn_en)
                    }
                    mDataset[position].is_subscription = "0"
                    deleteChannelSubscription(mDataset[position].channel_id.toInt())
                }
            }
        }

        holder.rl.setOnClickListener {
            ctx.startActivity<CategoryDetailActivity>("channel_id" to mDataset[position].channel_id)
        }
        if (SharedPreferenceController.getFlag(ctx) == "0"){

        } else {

        }
    }

    private fun requestChannelSubscription(channel_id : Int){
        val networkService : NetworkService = ApplicationController.instance.networkService
        val postChannelSubscripeResponse = networkService.postChannelSubscripeResponse(0, SharedPreferenceController.getAuthorization(ctx), channel_id)
        postChannelSubscripeResponse.enqueue(object : Callback<PostChannelSubscripeResponse> {
            override fun onFailure(call: Call<PostChannelSubscripeResponse>?, t: Throwable?) {
                Log.e("구독하기 실패", t.toString())
            }
            override fun onResponse(call: Call<PostChannelSubscripeResponse>?, response: Response<PostChannelSubscripeResponse>?) {
                response?.let {
                    if (response.isSuccessful){
                    }
                }
            }
        })
    }

    private fun deleteChannelSubscription(channel_id : Int){
        val networkService : NetworkService = ApplicationController.instance.networkService
        val deleteChannelScripteResponse = networkService.deleteChannelSubscripeResponse(0, SharedPreferenceController.getAuthorization(ctx), channel_id)
        deleteChannelScripteResponse.enqueue(object : Callback<DeleteChannelScripteResponse> {
            override fun onFailure(call: Call<DeleteChannelScripteResponse>?, t: Throwable?) {
                Log.e("구독 취소 하기 실패", t.toString())
            }

            override fun onResponse(call: Call<DeleteChannelScripteResponse>?, response: Response<DeleteChannelScripteResponse>?) {
                response?.let {
                    if (response.isSuccessful){
                    }
                }
            }
        })
    }

    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val rl : RelativeLayout = itemView.findViewById(R.id.view_more_act_rv_item_card_view) as RelativeLayout
        val mImageView : ImageView = itemView.findViewById(R.id.view_more_act_rv_item_broadcast_iv) as ImageView
        val title : TextView = itemView.findViewById(R.id.view_more_act_rv_item_broadcast_title_tv) as TextView
        val subscribeBtn : ImageView = itemView.findViewById(R.id.view_more_act_rv_item_subscribe_tv) as ImageView
    }
}