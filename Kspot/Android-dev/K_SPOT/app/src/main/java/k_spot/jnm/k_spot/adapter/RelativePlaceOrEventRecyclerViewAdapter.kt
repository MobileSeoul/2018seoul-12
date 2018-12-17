package k_spot.jnm.k_spot.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import k_spot.jnm.k_spot.Get.PlaceOrEventRelativeData
import k_spot.jnm.k_spot.R
import k_spot.jnm.k_spot.activity.SpotViewMoreActivity
import org.jetbrains.anko.startActivity

class RelativePlaceOrEventRecyclerViewAdapter(val ctx : Context, val dataList : ArrayList<PlaceOrEventRelativeData>) : RecyclerView.Adapter<RelativePlaceOrEventRecyclerViewAdapter.Holder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(ctx).inflate(R.layout.rv_item_category_detail_relative_spot_event, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.title.text = dataList[position].name
        holder.content.text = dataList[position].description
        holder.address.text = dataList[position].address_gu + " · " + dataList[position].station
        holder.likeCnt.text = dataList[position].scrap_cnt.toString()

        val requestOptions = RequestOptions().transforms(CenterCrop(), RoundedCorners(dpToPx(12)))
        Glide.with(ctx).load(dataList[position].img).apply(requestOptions).into(holder.img)

        holder.whole_btn.setOnClickListener {
            ctx.startActivity<SpotViewMoreActivity>("spot_id" to dataList[position].spot_id, "event_flag" to 1)
        }

    }
    fun dpToPx(dp: Int): Int {
        val density = ctx.resources
                .displayMetrics
                .density
        return Math.round(dp.toFloat() * density)
    }
    inner class Holder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val img : ImageView = itemView.findViewById(R.id.rv_item_categoty_detail_relative_spot_img) as ImageView
        val title : TextView = itemView.findViewById(R.id.rv_item_categoty_detail_relative_spot_title) as TextView
        val content : TextView = itemView.findViewById(R.id.rv_item_categoty_detail_relative_spot_content) as TextView
        val address : TextView = itemView.findViewById(R.id.rv_item_categoty_detail_relative_spot_address) as TextView
        val likeCnt : TextView = itemView.findViewById(R.id.rv_item_categoty_detail_relative_spot_like_cnt) as TextView
        val whole_btn : LinearLayout = itemView.findViewById(R.id.rv_item_categoty_detail_relative_spot_whole_btn) as LinearLayout
    }
}