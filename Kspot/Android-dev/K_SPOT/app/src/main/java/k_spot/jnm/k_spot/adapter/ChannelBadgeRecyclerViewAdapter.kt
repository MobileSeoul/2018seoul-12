package k_spot.jnm.k_spot.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import k_spot.jnm.k_spot.Get.ChannelData
import k_spot.jnm.k_spot.R
import k_spot.jnm.k_spot.activity.CategoryDetailActivity
import org.jetbrains.anko.startActivity

class ChannelBadgeRecyclerViewAdapter(val ctx : Context, val dataList : ChannelData) : RecyclerView.Adapter<ChannelBadgeRecyclerViewAdapter.Holder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(ctx).inflate(R.layout.rv_item_view_more_activity_channel_badge_list, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int = dataList.channel_id.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        Glide.with(ctx).load(dataList.thumbnail_img[position]).into(holder.img)

        holder.img.setOnClickListener {
            ctx.startActivity<CategoryDetailActivity>("channel_id" to dataList.channel_id[position])
            Log.e("채널 아이디는?", dataList.channel_id[position])
        }
    }

    inner class Holder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val img : ImageView = itemView.findViewById(R.id.iv_rv_item_channel_badge_img) as ImageView
    }
}