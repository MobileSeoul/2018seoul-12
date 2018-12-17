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
import k_spot.jnm.k_spot.Get.ThemeDetailThemeContentData
import k_spot.jnm.k_spot.R
import k_spot.jnm.k_spot.activity.SpotViewMoreActivity
import k_spot.jnm.k_spot.db.SharedPreferenceController
import org.jetbrains.anko.startActivity

class RecommendViewMoreRecyclerAdapter(private var recommendViewMorePageItems: ArrayList<ThemeDetailThemeContentData>, private var ctx: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val mainView: View = LayoutInflater.from(ctx).inflate(R.layout.rv_item_recommend_view_more_act, parent, false)
        return RecommendViewMoreRecyclerAdapter.Holder(mainView)
    }

    override fun getItemCount(): Int {
        return recommendViewMorePageItems.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var holder: Holder = holder as Holder

        holder.recommend_view_more_index.text = (position + 1).toString()

        if (recommendViewMorePageItems[position].title.length > 0) {
            holder.recommend_view_more_title.text = recommendViewMorePageItems[position].title
        }

        holder.recommend_view_more_text1.text = recommendViewMorePageItems[position].description[0]
        holder.recommend_view_more_text2.text = recommendViewMorePageItems[position].description[1]
        holder.recommend_view_more_text3.text = recommendViewMorePageItems[position].description[2]

        if (recommendViewMorePageItems[position].img.length > 0) {
//            val requestOption = RequestOptions().transforms(CenterCrop(), RoundedCorners(dpToPx(12)))
            Glide.with(ctx).load(recommendViewMorePageItems[position].img).into(holder.recommend_view_more_image1)
        }
        if (SharedPreferenceController.getFlag(ctx) == "0") {
            holder.tv_spot_show_btn.text = "장소 상세 보기"
        } else {
            holder.tv_spot_show_btn.text = "see the details"
        }


        holder.recommend_view_more_spot_view_more.setOnClickListener {
            ctx.startActivity<SpotViewMoreActivity>("spot_id" to recommendViewMorePageItems[position].spot_id.toInt(), "event_flag" to 0)
        }
    }

    fun dpToPx(dp: Int): Int {
        val density = ctx.resources
                .displayMetrics
                .density
        return Math.round(dp.toFloat() * density)
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var recommend_view_more_index: TextView = itemView!!.findViewById(R.id.recommend_view_more_rv_item_index_tv)
        var recommend_view_more_title: TextView = itemView!!.findViewById(R.id.recommend_view_more_rv_item_title_tv)
        var recommend_view_more_image1: ImageView = itemView!!.findViewById(R.id.recommend_view_more_rv_item_contents_iv1)
        var recommend_view_more_spot_view_more: RelativeLayout = itemView!!.findViewById(R.id.recommend_view_more_rv_item_detail_view_btn)
        var recommend_view_more_text1: TextView = itemView!!.findViewById(R.id.recommend_view_more_rv_item_first_explain_tv)
        var recommend_view_more_text2: TextView = itemView!!.findViewById(R.id.recommend_view_more_rv_item_explain_second_tv)
        var recommend_view_more_text3: TextView = itemView!!.findViewById(R.id.recommend_view_more_rv_item_explain_third_tv)
        var tv_spot_show_btn: TextView = itemView!!.findViewById(R.id.tv_recommend_spot_detail_spot_show_btn) as TextView


    }
}