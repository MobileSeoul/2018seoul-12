package k_spot.jnm.k_spot.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
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
import k_spot.jnm.k_spot.Get.ChannelMyPageData
import k_spot.jnm.k_spot.R
import k_spot.jnm.k_spot.activity.CategoryDetailActivity
import org.jetbrains.anko.startActivity


class MySubscribeRecyclerViewAdapter(val ctx : Context, val dataList : ArrayList<ChannelMyPageData>) : RecyclerView.Adapter<MySubscribeRecyclerViewAdapter.Holder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(ctx).inflate(R.layout.rv_item_my_page_fragment, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val dp = ctx.resources.displayMetrics.density
        val rootLayoutParams : RelativeLayout.LayoutParams = holder.rl2.layoutParams as RelativeLayout.LayoutParams
        if (position == dataList.size - 1) {
            rootLayoutParams.rightMargin = (16*dp).toInt()
        }

        val requestOptions = RequestOptions().transforms(CenterCrop(), RoundedCorners(dpToPx(15)))
        Glide.with(ctx).load(dataList[position].background_img).apply(requestOptions).into(holder.img_btn)

        holder.img_btn.setOnClickListener {
            ctx.startActivity<CategoryDetailActivity>("channel_id" to dataList[position].channel_id.toString())
        }

        holder.engTitle.text = dataList[position].eng_name

        holder.korTitle.text = dataList[position].kor_name
    }

    fun dpToPx(dp: Int): Int {
        val density = ctx.resources
                .displayMetrics
                .density
        return Math.round(dp.toFloat() * density)
    }

    inner class Holder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val img_btn : ImageView = itemView.findViewById(R.id.my_page_frag_rv_item_img_btn) as ImageView
        val engTitle : TextView = itemView.findViewById(R.id.my_page_frag_rv_item_english_tv) as TextView
        val korTitle : TextView = itemView.findViewById(R.id.my_page_frag_rv_item_hangeul_tv) as TextView
        val rl2 : RelativeLayout = itemView.findViewById(R.id.my_page_frag_rv_item_rl) as RelativeLayout

    }
}