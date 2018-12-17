package k_spot.jnm.k_spot.adapter

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import k_spot.jnm.k_spot.Get.PlaceSearchResultData
import k_spot.jnm.k_spot.R
import k_spot.jnm.k_spot.activity.SpotViewMoreActivity
import org.jetbrains.anko.startActivity


class SearchSpotViewMoreActRecyclerAdapter(private var searchSpotViewMoreItems : ArrayList<PlaceSearchResultData>, private var context: Context, private var onItemClick: View.OnClickListener, private var event_flag: Int) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val mainView : View = LayoutInflater.from(context).inflate(R.layout.rv_item_search_spot_view_more_act, parent, false)
        mainView.setOnClickListener(onItemClick)
        return SearchSpotViewMoreActRecyclerAdapter.Holder(mainView)
    }

    override fun getItemCount(): Int {
        return searchSpotViewMoreItems.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var holder : Holder = holder as Holder

        val drawable = context.getDrawable(R.drawable.search_spot_view_more_img_background) as GradientDrawable

        holder.result_image.background = drawable
        holder.result_image.clipToOutline = true
        Glide.with(context)
                .load(searchSpotViewMoreItems[position].img)
                .into(holder.result_image)

        holder.result_name.text = searchSpotViewMoreItems[position].name

        holder.result_sub_text.text = searchSpotViewMoreItems[position].description

        holder.result_address.text = searchSpotViewMoreItems[position].address_gu + " · " + searchSpotViewMoreItems[position].station

        holder.result_sub_num.text = searchSpotViewMoreItems[position].scrap_cnt.toString()
        holder.btn.setOnClickListener {
            context.startActivity<SpotViewMoreActivity>("spot_id" to searchSpotViewMoreItems[position].spot_id.toInt(), "event_flag" to event_flag)
        }
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var result_image : ImageView = itemView!!.findViewById(R.id.search_spot_view_more_act_rv_item_iv)
        var result_name : TextView = itemView!!.findViewById(R.id.search_spot_view_more_act_rv_item_title_tv)
        var result_sub_text : TextView = itemView!!.findViewById(R.id.search_spot_view_more_act_rv_item_sub_text_tv)
        var result_address : TextView = itemView!!.findViewById(R.id.search_spot_view_more_act_rv_item_address_tv)
        var result_sub_num : TextView = itemView!!.findViewById(R.id.search_spot_view_more_act_rv_item_sub_tv)
        var btn : RelativeLayout = itemView!!.findViewById(R.id.search_spot_view_more_act_rv_item_btn)

    }
}