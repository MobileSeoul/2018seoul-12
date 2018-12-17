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
import k_spot.jnm.k_spot.Get.PlaceSearchResultData
import k_spot.jnm.k_spot.R
import k_spot.jnm.k_spot.activity.SpotViewMoreActivity
import org.jetbrains.anko.startActivity

class SearchResultActSpotRecyclerAdapter(private var searchSpotItems: ArrayList<PlaceSearchResultData>, private var context: Context, private var ItemCount: Int, private var spotOrEventFlag: Int) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val mainView: View = LayoutInflater.from(context).inflate(R.layout.rv_item_search_result_act_spot, parent, false)
        return SearchResultActSpotRecyclerAdapter.Holder(mainView)
    }

    override fun getItemCount(): Int {
        return ItemCount
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var holder: Holder = holder as Holder

        // 통신 시 주석 제거
//        Glide.with(context)
//                .load(categoryPageItems[position].Image)
//                .into(holder.category_list_image)

        holder.result_name.text = searchSpotItems[position]!!.name

        holder.result_address.text = searchSpotItems[position]!!.address_gu + " · " + searchSpotItems[position]!!.station

        holder.result_icon_image.setImageResource(R.drawable.search_page_place_restaurant_icon)

        // spot일 경우
        if (spotOrEventFlag == 0) {
            holder.btn.setOnClickListener {
                val spot_id = searchSpotItems[position].spot_id
                Log.v("spot_id", spot_id.toString())
                context.startActivity<SpotViewMoreActivity>("spot_id" to spot_id, "event_flag" to 0)
            }
        } else {
            holder.btn.setOnClickListener {
                val spot_event_id = searchSpotItems[position].spot_id
                Log.v("spot_event_id", spot_event_id.toString())
                context.startActivity<SpotViewMoreActivity>("spot_id" to spot_event_id, "event_flag" to 1)

            }
        }


        // 아이콘 네 개 논리처리
        //맛집
        if(searchSpotItems.size > 0){
            if(searchSpotItems[position].type == 0){
                holder.result_icon_image.setImageResource(R.drawable.search_page_place_restaurant_icon)
            } // 카페
            else if(searchSpotItems[position].type == 1){
                holder.result_icon_image.setImageResource(R.drawable.search_page_place_cafe_icon)
            } // 명소
            else if(searchSpotItems[position].type == 2) {
                holder.result_icon_image.setImageResource(R.drawable.search_page_place_hotplace_icon)
            } // 생일
            else if(searchSpotItems[position].type == 3){
                holder.result_icon_image.setImageResource(R.drawable.search_page_event_birthday_icon)
            } // 기념
            else if(searchSpotItems[position].type == 4){
                holder.result_icon_image.setImageResource(R.drawable.search_page_event_firecracker_icon)
            } // 이벤트 기타
            else if(searchSpotItems[position].type == 5){
                holder.result_icon_image.setImageResource(R.drawable.search_page_event_etc_icon)
            } // 기타
            else if(searchSpotItems[position].type == 6){
                holder.result_icon_image.setImageResource(R.drawable.search_page_place_etc_icon)
            }

        }

    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var result_icon_image: ImageView = itemView!!.findViewById(R.id.search_result_rv_item_icon_iv)
        var result_name: TextView = itemView!!.findViewById(R.id.search_result_rv_item_title_tv)
        var result_address: TextView = itemView!!.findViewById(R.id.search_result_rv_item_spot_tv)
        var btn: RelativeLayout = itemView!!.findViewById(R.id.search_result_rv_item_rl)
    }
}
