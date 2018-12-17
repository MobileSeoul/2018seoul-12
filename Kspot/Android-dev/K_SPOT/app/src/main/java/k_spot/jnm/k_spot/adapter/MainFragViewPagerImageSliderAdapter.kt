package k_spot.jnm.k_spot.adapter

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import k_spot.jnm.k_spot.Get.Theme
import k_spot.jnm.k_spot.R
import k_spot.jnm.k_spot.activity.RecommendViewMoreActivity
import org.jetbrains.anko.startActivity
import java.util.*


class MainFragViewPagerImageSliderAdapter(context: Context, mResources: ArrayList<Theme>) : PagerAdapter() {

    var mContext: Context = context
    var mLayoutInflater: LayoutInflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    var mResources: ArrayList<Theme> = mResources

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
        val itemView = mLayoutInflater.inflate(R.layout.rv_item_main_frag_view_pager, container, false)
        val realPos = position % mResources.size
//        val textView = itemView.findViewById(R.id.main_frag_rv_item_tv1) as TextView
//        val textView2 = itemView.findViewById(R.id.main_frag_rv_item_tv2) as TextView
        val imageView = itemView.findViewById(R.id.main_frag_rv_item_iv) as ImageView
        // str은 DB에서 받아온 String 값
//        val str : String = mResources[realPos].main_frag_view_pager_text

//        val tokens = StringTokenizer(str)
//        // str이 "안녕! \n오늘은 \n어디를 가볼까?" 일 때
//        // token1 = "안녕!"
//        val token1 : String = tokens.nextToken("\n")
//        // token2 = "오늘은"
//        val token2 : String = tokens.nextToken("\n")
//        // token3 = "어디를 가볼까?"
//        val token3 : String = tokens.nextToken("\n")

        // text1 = "안녕! \n 오늘은"
//        val text1 : String = mResources[realPos].titleㅇㅇ
        itemView.setOnClickListener {
            mContext.startActivity<RecommendViewMoreActivity>("theme_id" to mResources[realPos].theme_id.toInt())
        }

//        textView.text = mResources[realPos].title
//        textView2.text = mResources[realPos].subtitle
        imageView.scaleType = ImageView.ScaleType.FIT_XY

        Glide.with(mContext).load(mResources[realPos].main_img).into(imageView)


        container.addView(itemView, 0)

        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as LinearLayout)
    }

}