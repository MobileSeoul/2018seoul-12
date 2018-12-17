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
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import k_spot.jnm.k_spot.Delete.DeleteChannelScripteResponse
import k_spot.jnm.k_spot.Get.ChannelListData
import k_spot.jnm.k_spot.LoginActivity
import k_spot.jnm.k_spot.Network.ApplicationController
import k_spot.jnm.k_spot.Network.NetworkService
import k_spot.jnm.k_spot.Post.PostChannelSubscripeResponse
import k_spot.jnm.k_spot.R
import k_spot.jnm.k_spot.activity.CategoryDetailActivity
import k_spot.jnm.k_spot.activity.MainActivity
import k_spot.jnm.k_spot.db.SharedPreferenceController
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CategoryPageFragRecyclerAdapter(private var categoryPageItems: ArrayList<ChannelListData>, private var ctx: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val mainView: View = LayoutInflater.from(parent!!.context).inflate(R.layout.rv_item_category_list_frag, parent, false)
        return CategoryPageFragRecyclerAdapter.Holder(mainView)
    }

    override fun getItemCount(): Int {
        return categoryPageItems.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var holder: Holder = holder as Holder

        holder.category_list_index.text = (position + 1).toString()

        val requestOptions = RequestOptions().transforms(CenterCrop(), RoundedCorners(dpToPx(2)))
        Glide.with(ctx)
                .load(categoryPageItems[position].thumbnail_img).apply(requestOptions)
                .into(holder.category_list_image)

        holder.category_list_name.text = categoryPageItems[position].name

        holder.category_list_sub_num.text = categoryPageItems[position].subscription_cnt.toString()

        holder.category_list_post_num.text = categoryPageItems[position].spot_cnt.toString()

        if(position == categoryPageItems.size - 1){
            holder.category_list_sub_bottom_line.visibility = View.GONE
        }

        // 구독된 경우
        if (categoryPageItems[position].subscription == 0) {
            if (SharedPreferenceController.getFlag(ctx) == "0") {
                holder.category_list_sub_btn_image.setImageResource(R.drawable.category_list_unsub_btn)
            } else {
                holder.category_list_sub_btn_image.setImageResource(R.drawable.category_list_unsub_btn_en)
            }
        } else {
            if (SharedPreferenceController.getFlag(ctx) == "0") {
                holder.category_list_sub_btn_image.setImageResource(R.drawable.category_list_sub_btn)
            } else {
                holder.category_list_sub_btn_image.setImageResource(R.drawable.category_list_sub_btn_en)
            }
        }

        holder.category_list_sub_btn.setOnClickListener {
            if (categoryPageItems[position].subscription == 0) {
                if (SharedPreferenceController.getFlag(ctx) == "0") {
                    holder.category_list_sub_btn_image.setImageResource(R.drawable.category_list_sub_btn)
                } else {
                    holder.category_list_sub_btn_image.setImageResource(R.drawable.category_list_sub_btn_en)
                }
                // 건너뛰기 로그인
                if(SharedPreferenceController.getAuthorization(ctx).isNullOrBlank()){
                    ctx.startActivity<LoginActivity>("need_login_flag" to 1)
                }else{
                    requestChannelSubscription(categoryPageItems[position].channel_id)
                    categoryPageItems[position].subscription = 1
                }
                //갱신
                (ctx as MainActivity).mainBottomTabAdapter.myPage.refleshDataSet()
            } else {
                if (SharedPreferenceController.getFlag(ctx) == "0") {
                    holder.category_list_sub_btn_image.setImageResource(R.drawable.category_list_unsub_btn)
                } else {
                    holder.category_list_sub_btn_image.setImageResource(R.drawable.category_list_unsub_btn_en)
                }
                // 건너뛰기 로그인
                if(SharedPreferenceController.getAuthorization(ctx).isNullOrBlank()){
                    ctx.startActivity<LoginActivity>("need_login_flag" to 1)
                }else{
                    deleteChannelSubscription(categoryPageItems[position].channel_id)
                    categoryPageItems[position].subscription = 0
                }
                (ctx as MainActivity).mainBottomTabAdapter.myPage.refleshDataSet()

            }
        }


        if (SharedPreferenceController.getFlag(ctx) == "0") {
            holder.subscribe_cnt_text.text = "구독자 "
            holder.board_cnt_text.text = "게시물 "
        } else {
            holder.subscribe_cnt_text.text = "subscriber "
            holder.board_cnt_text.text = "post "
        }
        holder.category_list_all_btn.setOnClickListener {
            ctx.startActivity<CategoryDetailActivity>("channel_id" to categoryPageItems[position].channel_id.toString())
        }
    }

    fun dpToPx(dp: Int): Int {
        val density = ctx.resources
                .displayMetrics
                .density
        return Math.round(dp.toFloat() * density)
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var category_list_index: TextView = itemView!!.findViewById(R.id.category_list_fragment_rv_item_num_tv)
        var category_list_image: ImageView = itemView!!.findViewById(R.id.category_list_fragment_rv_item_image_iv)
        var category_list_name: TextView = itemView!!.findViewById(R.id.category_list_fragment_rv_item_name_tv)
        var category_list_sub_num: TextView = itemView!!.findViewById(R.id.category_list_fragment_rv_item_subscriber_num_tv)
        var category_list_post_num: TextView = itemView!!.findViewById(R.id.category_list_fragment_rv_item_post_num_tv)
        var category_list_sub_btn_image: ImageView = itemView!!.findViewById(R.id.category_list_fragment_rv_item_subscribe_iv)
        var category_list_all_btn: RelativeLayout = itemView!!.findViewById(R.id.category_list_fragment_rv_item_rl)
        var category_list_sub_btn: RelativeLayout = itemView!!.findViewById(R.id.category_list_fragment_rv_item_subscribe_btn)

        val subscribe_cnt_text: TextView = itemView!!.findViewById(R.id.category_list_fragment_rv_item_subscriber_tv) as TextView
        val board_cnt_text: TextView = itemView!!.findViewById(R.id.category_list_fragment_rv_item_post_tv) as TextView
        var category_list_sub_bottom_line: View = itemView!!.findViewById(R.id.category_list_fragment_rv_item_bottom_line)

    }

    private fun requestChannelSubscription(channel_id: Int) {
        val networkService: NetworkService = ApplicationController.instance.networkService
        val postChannelSubscripeResponse = networkService.postChannelSubscripeResponse(SharedPreferenceController.getFlag(ctx).toInt(), SharedPreferenceController.getAuthorization(ctx), channel_id)
        postChannelSubscripeResponse.enqueue(object : Callback<PostChannelSubscripeResponse> {
            override fun onFailure(call: Call<PostChannelSubscripeResponse>?, t: Throwable?) {
                Log.e("구독하기 실패", t.toString())
            }

            override fun onResponse(call: Call<PostChannelSubscripeResponse>?, response: Response<PostChannelSubscripeResponse>?) {
                response?.let {
                    if (response.isSuccessful) {
                        if (SharedPreferenceController.getFlag(ctx) == "0") {
                            ctx.toast("구독")
                        } else {
                            ctx.toast("Subscribe")
                        }
                    }
                }
            }
        })
    }

    private fun deleteChannelSubscription(channel_id: Int) {
        val networkService: NetworkService = ApplicationController.instance.networkService
        val deleteChannelScripteResponse = networkService.deleteChannelSubscripeResponse(SharedPreferenceController.getFlag(ctx).toInt(), SharedPreferenceController.getAuthorization(ctx), channel_id)
        deleteChannelScripteResponse.enqueue(object : Callback<DeleteChannelScripteResponse> {
            override fun onFailure(call: Call<DeleteChannelScripteResponse>?, t: Throwable?) {
                Log.e("구독 취소 하기 실패", t.toString())
            }

            override fun onResponse(call: Call<DeleteChannelScripteResponse>?, response: Response<DeleteChannelScripteResponse>?) {
                response?.let {
                    if (response.isSuccessful) {
                        if (SharedPreferenceController.getFlag(ctx) == "0") {
                            ctx.toast("구독 취소")
                        } else {
                            ctx.toast("Subscribe Cancel")
                        }
                    }
                }
            }
        })
    }
}
