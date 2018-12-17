//package k_spot.jnm.k_spot.adapter
//
//import android.content.Context
//import android.support.v7.widget.RecyclerView
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.RelativeLayout
//import android.widget.TextView
//import k_spot.jnm.k_spot.R
//
//class SearchActTagRecyclerAdapter(val ctx: Context, val myDataset: ArrayList<SearchActivityHashTagData>) : RecyclerView.Adapter<SearchActTagRecyclerAdapter.ViewHolder>() {
//
//    lateinit var mDataset: ArrayList<SearchActivityHashTagData>
//
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchActTagRecyclerAdapter.ViewHolder {
//        mDataset = myDataset
//        val v = LayoutInflater.from(ctx).inflate(R.layout.rv_item_search_act_hash_tag, parent, false)
//        val vh = ViewHolder(v)
//        return vh
//    }
//
//    override fun getItemCount(): Int {
//        mDataset = myDataset
//        return mDataset.size
//    }
//
//    override fun onBindViewHolder(holder: SearchActTagRecyclerAdapter.ViewHolder, position: Int) {
//        val dp = ctx.resources.displayMetrics.density
//        val rootLayoutParams : RelativeLayout.LayoutParams = holder.rl.layoutParams as RelativeLayout.LayoutParams
//        if (position == 0) {
//            rootLayoutParams.leftMargin = (16*dp).toInt()
//        }
//
//        holder.tag.text = mDataset[position].tag
//
//    }
//
//    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
//        val rl : RelativeLayout = itemView.findViewById(R.id.search_act_rv_item_rl) as RelativeLayout
//        val tag : TextView = itemView.findViewById(R.id.search_act_rv_item_tv) as TextView
//    }
//}
//
//data class SearchActivityHashTagData (
//        val tag : String
//)