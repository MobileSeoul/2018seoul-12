package k_spot.jnm.k_spot.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import k_spot.jnm.k_spot.Get.BroadcastData
import k_spot.jnm.k_spot.R


class SubscribeActRecyclerViewAdapter (private var subscribeActItems : ArrayList<BroadcastData>, private var ctx: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val mainView : View = LayoutInflater.from(parent!!.context).inflate(R.layout.rv_item_subscribe_activity, parent, false)
        return Holder(mainView)
    }

    override fun getItemCount(): Int {
        return subscribeActItems.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        var holder : Holder = holder as Holder

        // rv 이미지 가져오기
        Glide.with(ctx)
                .load(subscribeActItems[position].thumbnail_img)
                .into(holder.image)

        holder.name.text = subscribeActItems[position].name

        if(subscribeActItems[position].new_post_check != 0){
            holder.new_contents_flag.visibility = View.VISIBLE
        }else{
            holder.new_contents_flag.visibility = View.INVISIBLE
        }
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image : ImageView = itemView!!.findViewById(R.id.subscribe_act_rv_item_iv)
        var name : TextView = itemView!!.findViewById(R.id.subscribe_act_rv_item_tv)
        var new_contents_flag : ImageView = itemView!!.findViewById(R.id.subscribe_act_rv_item_new_contents_iv)
    }
}