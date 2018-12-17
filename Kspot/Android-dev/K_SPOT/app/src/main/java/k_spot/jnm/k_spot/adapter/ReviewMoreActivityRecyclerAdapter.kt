package k_spot.jnm.k_spot.adapter

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import k_spot.jnm.k_spot.Get.ReviewMoreData
import k_spot.jnm.k_spot.LoginActivity
import k_spot.jnm.k_spot.R
import k_spot.jnm.k_spot.db.SharedPreferenceController
import org.jetbrains.anko.startActivity

class ReviewMoreActivityRecyclerAdapter (private var reviewMoreRecyclerAdpaterData : ArrayList<ReviewMoreData>, private var ctx: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val mainView : View = LayoutInflater.from(ctx).inflate(R.layout.rv_item_review_more_act, parent, false)
        return ReviewMoreActivityRecyclerAdapter.Holder(mainView)

    }

    override fun getItemCount(): Int {
        return reviewMoreRecyclerAdpaterData.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var holder : Holder = holder as Holder


        holder.review_more_title.text = reviewMoreRecyclerAdpaterData[position].title

        holder.review_more_content.text = reviewMoreRecyclerAdpaterData[position].content

        if(reviewMoreRecyclerAdpaterData[position].img.length > 0){
            Glide.with(ctx).load(reviewMoreRecyclerAdpaterData[position].img).into(holder.review_more_img)
        }else {
            holder.review_more_img.visibility = View.GONE
        }


        holder.review_more_writer.text = reviewMoreRecyclerAdpaterData[position].name

        holder.review_more_date.text = reviewMoreRecyclerAdpaterData[position].reg_time.toString()

        // 신고하기 버튼 클릭 시
        holder.review_more_btn.setOnClickListener {
            if(SharedPreferenceController.getAuthorization(ctx).isNullOrBlank()){
                ctx.startActivity<LoginActivity>("need_login_flag" to 1)
            }else{
                holder.review_more_report_rl.visibility = View.VISIBLE
            }
        }
        // 음란물 신고 논리 처리
        holder.review_more_first_report_btn.setOnClickListener {
            Toast.makeText(ctx,"음란물 신고 논리처리", Toast.LENGTH_SHORT).show()
            holder.review_more_first_report_btn.setBackgroundColor(Color.parseColor("#F5F5F5"))
        }

        // 사칭 및 사기 논리 처리
        holder.review_more_second_report_btn.setOnClickListener {
            Toast.makeText(ctx,"사칭 및 사기 논리처리", Toast.LENGTH_SHORT).show()
            holder.review_more_second_report_btn.setBackgroundColor(Color.parseColor("#F5F5F5"))
        }

        // 허위 사실 유포 논리 처리
        holder.review_more_third_report_btn.setOnClickListener {
            Toast.makeText(ctx,"허위 사실 유포 논리 처리", Toast.LENGTH_SHORT).show()
            holder.review_more_third_report_btn.setBackgroundColor(Color.parseColor("#F5F5F5"))
        }

        // 상업적 광고 및 판매
        holder.review_more_fourth_report_btn.setOnClickListener {
            Toast.makeText(ctx,"상업적 광고 및 판매", Toast.LENGTH_SHORT).show()
            holder.review_more_fourth_report_btn.setBackgroundColor(Color.parseColor("#F5F5F5"))
        }

        // 욕설 및 불쾌감을 주는 행위
        holder.review_more_fifth_report_btn.setOnClickListener {
            Toast.makeText(ctx,"욕설 및 불쾌감을 주는 행위", Toast.LENGTH_SHORT).show()
            holder.review_more_fifth_report_btn.setBackgroundColor(Color.parseColor("#F5F5F5"))
        }

        // 확인 버튼
        holder.review_more_report_confirm_btn.setOnClickListener {
            Toast.makeText(ctx,"확인", Toast.LENGTH_SHORT).show()
            holder.review_more_report_rl.visibility = View.GONE
            holder.review_more_first_report_btn.setBackgroundColor(Color.parseColor("#FFFFFF"))
            holder.review_more_second_report_btn.setBackgroundColor(Color.parseColor("#FFFFFF"))
            holder.review_more_third_report_btn.setBackgroundColor(Color.parseColor("#FFFFFF"))
            holder.review_more_fourth_report_btn.setBackgroundColor(Color.parseColor("#FFFFFF"))
            holder.review_more_fifth_report_btn.setBackgroundColor(Color.parseColor("#FFFFFF"))
            holder.review_more_report_receipt_btn.visibility = View.VISIBLE
        }

        // 신고가 접수되었습니다 뷰에서 확인 버튼
        holder.review_more_report_receipt_confirm_btn.setOnClickListener {
            holder.review_more_report_receipt_btn.visibility = View.GONE
        }



        // starCount 통신으로 받아와야함.
        val starCount = reviewMoreRecyclerAdpaterData[position].review_score

        // size 5의 이미지 뷰 배열 생성
        var stars: Array<ImageView?> = arrayOfNulls<ImageView>(5)
        // star 생성
        for (i in 0 until 5) {

            if (i < starCount) {

                // 별 반개를 표현 해야 할 때
                if (0.5 <= (starCount - i) && (starCount - i) <= 0.99) {
                    // 별 반개 그리는거
                    stars[i] = ImageView(ctx)
                    stars[i]!!.setImageResource(R.drawable.reveiw_page_small_star_half)
//                    stars[i]!!.setImageDrawable(R.drawable.review_page_big_star)

//                    stars[i]!!.setImageDrawable(resources.getDrawable(R.drawable.category_reveiw_small_star_half))
                } else if (0 <= (starCount - i) && (starCount - i) < 0.5) {
                    // 마지막 별이 0~0.5일 때 꽉찬 별 그리는 거
                    stars[i] = ImageView(ctx)
                    stars[i]!!.setImageResource(R.drawable.reveiw_page_small_star)
//                    stars[i]!!.setImageDrawable(resources.getDrawable(R.drawable.category_reveiw_small_star))
                } else {
                    // 꽉찬 별을 표현 해야 할 때
                    stars[i] = ImageView(ctx)
                    stars[i]!!.setImageResource(R.drawable.reveiw_page_small_star)
//                    stars[i]!!.setImageDrawable(resources.getDrawable(R.drawable.category_reveiw_small_star))
                }

            } else {
                // 빈 별
                stars[i] = ImageView(ctx)
                stars[i]!!.setImageResource(R.drawable.reveiw_page_small_star_empty)
//                stars[i]!!.setImageDrawable(resources.getDrawable(R.drawable.category_reveiw_small_star_empty))
            }
            val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            )

            // 인디케이터 점 마진 설정
            params.setMargins(3, 0, 3, 0)
            //LinearView에 뷰 생성
            holder.review_more_star!!.addView(stars[i], params)
        }




    }


    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var review_more_title : TextView = itemView!!.findViewById(R.id.review_more_act_rv_item_title)
        var review_more_content : TextView = itemView!!.findViewById(R.id.review_more_act_rv_item_content_tv)
        var review_more_img : ImageView = itemView!!.findViewById(R.id.review_more_act_rv_item_content_iv)
        var review_more_star : LinearLayout = itemView!!.findViewById(R.id.review_more_act_rv_item_star_ll)
        var review_more_writer : TextView = itemView!!.findViewById(R.id.review_more_act_rv_item_writer_tv)
        var review_more_date : TextView = itemView!!.findViewById(R.id.review_more_act_rv_item_date_tv)
        var review_more_btn : RelativeLayout = itemView!!.findViewById(R.id.review_more_act_rv_item_threedot_btn)
        var review_more_report_rl : RelativeLayout = itemView!!.findViewById(R.id.review_more_act_rv_item_report_rl)
        var review_more_first_report_btn : RelativeLayout = itemView!!.findViewById(R.id.review_more_act_rv_item_report_first_btn)
        var review_more_second_report_btn : RelativeLayout = itemView!!.findViewById(R.id.review_more_act_rv_item_report_second_btn)
        var review_more_third_report_btn : RelativeLayout = itemView!!.findViewById(R.id.review_more_act_rv_item_report_third_btn)
        var review_more_fourth_report_btn : RelativeLayout = itemView!!.findViewById(R.id.review_more_act_rv_item_report_fourth_btn)
        var review_more_fifth_report_btn : RelativeLayout = itemView!!.findViewById(R.id.review_more_act_rv_item_report_fifth_btn)
        var review_more_report_confirm_btn : RelativeLayout = itemView!!.findViewById(R.id.review_more_act_rv_item_report_confirm_btn)
        var review_more_report_receipt_btn : RelativeLayout = itemView!!.findViewById(R.id.review_more_act_rv_item_report_Receipt_rl)
        var review_more_report_receipt_confirm_btn : RelativeLayout = itemView!!.findViewById(R.id.review_more_act_rv_item_report_Receipt_corfirm_btn)


    }
}
