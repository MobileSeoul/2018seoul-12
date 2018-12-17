package k_spot.jnm.k_spot.adapter

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import k_spot.jnm.k_spot.Get.ViewPagerSpotViewMoreActData
import k_spot.jnm.k_spot.R
import java.util.*

class SpotViewMoreActAutoScrollAdapter(context: Context, mResources: ArrayList<ViewPagerSpotViewMoreActData>) : PagerAdapter() {

    var mContext : Context = context
    var mLayoutInflater : LayoutInflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    var mResources : ArrayList<ViewPagerSpotViewMoreActData> = mResources

    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }


    override fun getCount(): Int {
        return Integer.MAX_VALUE
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as LinearLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView = mLayoutInflater.inflate(R.layout.rv_item_view_more_act_view_pager, container, false)


        val realPos = position % mResources.size
        val imageView = itemView.findViewById(R.id.spot_view_more_act_rv_item_iv) as ImageView

//        // str은 DB에서 받아온 String 값
//        val str : String = mResources[realPos].img

        Glide.with(mContext).load(mResources[realPos].img).into(imageView)

        container.addView(itemView, 0)

        return itemView


    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as LinearLayout)
    }

}
