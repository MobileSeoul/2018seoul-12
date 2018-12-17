package k_spot.jnm.k_spot.fragment

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.LogoutResponseCallback
import k_spot.jnm.k_spot.Get.ChannelMyPageData
import k_spot.jnm.k_spot.Get.GetMyPageResponse
import k_spot.jnm.k_spot.Get.UserMyPageData
import k_spot.jnm.k_spot.LoginActivity
import k_spot.jnm.k_spot.Network.ApplicationController
import k_spot.jnm.k_spot.Network.NetworkService
import k_spot.jnm.k_spot.R
import k_spot.jnm.k_spot.SubscribeActivity
import k_spot.jnm.k_spot.activity.MainActivity
import k_spot.jnm.k_spot.activity.UserInfoEditActivity
import k_spot.jnm.k_spot.activity.UserScrapListActivity
import k_spot.jnm.k_spot.adapter.MySubscribeRecyclerViewAdapter
import k_spot.jnm.k_spot.db.SharedPreferenceController
import kotlinx.android.synthetic.main.fragment_my_page.*
import org.jetbrains.anko.support.v4.ctx
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.startActivityForResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MyPageFragment : Fragment() {

    val REQUEST_CODE_USER_EDIT = 1002

    lateinit var networkService: NetworkService
    lateinit var userMyPageData: UserMyPageData
    lateinit var ChannelMyPageData: ArrayList<ChannelMyPageData>
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        getMyPage()
        ChannelMyPageData = ArrayList()
        userMyPageData = UserMyPageData("", "")
        return inflater.inflate(R.layout.fragment_my_page, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setOnClickListener()
        changeMyPageLanguage()

        my_page_frag_translate_btn.setOnClickListener {
            (activity as MainActivity).changeMainActivityLanguage()
        }
    }

    fun translateMyPageLanguage(){
        changeMyPageLanguage()
        //getMyPage()
    }
    fun refleshDataSet(){
        getMyPage()
    }

    private fun changeMyPageLanguage(){
        if (SharedPreferenceController.getFlag(context!!) == "0"){
            tv_my_page_title_text.text = "마이페이지"
            tv_my_page_hello_01_text.text = " 고객님,"
            tv_my_page_hello_02_text.text = "안녕하세요!"
            iv_my_page_logout.setImageResource(R.drawable.my_page_frag_logout_btn)
            tv_my_page_my_subscript_title.text = "내 구독"
            tv_my_page_more_my_subscript.text = "더보기"
            tv_my_page_scrab.text = "스크랩"
            tv_my_page_edit_information.text = "회원정보 수정"
        } else {
            tv_my_page_title_text.text = "MYPAGE"
            tv_my_page_hello_01_text.text = ","
            tv_my_page_hello_02_text.text = "Welcome!"
            iv_my_page_logout.setImageResource(R.drawable.mypage_logout_icon_en)
            tv_my_page_my_subscript_title.text = "My Subscribe"
            tv_my_page_more_my_subscript.text = "MORE"
            tv_my_page_scrab.text = "Scrap"
            tv_my_page_edit_information.text = "Edit Profile"
        }
    }


    private fun setMySubscribeRecyclerView(ChannelMyPageData: ArrayList<ChannelMyPageData>) {

        my_page_frag_my_subscribe_rv.layoutManager = LinearLayoutManager(context, 0, false)
        my_page_frag_my_subscribe_rv.adapter = MySubscribeRecyclerViewAdapter(this!!.context!!, ChannelMyPageData)
    }

    fun getMyPage() {

        networkService = ApplicationController.instance.networkService
        val authorization: String = SharedPreferenceController.getAuthorization(context = this!!.context!!)
        val getMyPageResponse = networkService.getMyPage(0, authorization)
        getMyPageResponse.enqueue(object : Callback<GetMyPageResponse> {
            override fun onFailure(call: Call<GetMyPageResponse>?, t: Throwable?) {
            }

            override fun onResponse(call: Call<GetMyPageResponse>?, response: Response<GetMyPageResponse>?) {
                if (response!!.isSuccessful) {
                    userMyPageData = response!!.body()!!.data!!.user
                    ChannelMyPageData = response!!.body()!!.data!!.channel

                    Glide.with(ctx).load(userMyPageData.profile_img).into(my_page_frag_my_info_iv)
                    my_page_frag_my_name_tv.text = userMyPageData.name
                    setMySubscribeRecyclerView(ChannelMyPageData)
                }
            }

        })
    }

    private fun setOnClickListener() {
        my_page_frag_change_my_info_bar_btn.setOnClickListener {
            //            startActivity<UserInfoEditActivity>("name" to userMyPageData.name, "image" to userMyPageData.profile_img)

            startActivityForResult<UserInfoEditActivity>(REQUEST_CODE_USER_EDIT, "name" to my_page_frag_my_name_tv.text, "image" to userMyPageData.profile_img)
        }

        my_page_frag_logout_btn.setOnClickListener {
            SharedPreferenceController.clearSPC(this!!.context!!)
            onClickLogout()
        }

        my_page_frag_my_subscribe_view_more_btn.setOnClickListener {
            startActivity<SubscribeActivity>()
        }

        my_page_frag_scrab_bar_btn.setOnClickListener {
            startActivity<UserScrapListActivity>()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_USER_EDIT){
            if (resultCode == RESULT_OK){
                val newName : String? = data!!.getStringExtra("name")
                newName?.let {
                    my_page_frag_my_name_tv.text = it
                }
                val newImage : String? = data!!.getStringExtra("image")
                newImage?.let {
                    Glide.with(ctx).load(Uri.parse(it)).into(my_page_frag_my_info_iv)
                }
            }
        }
    }

    private fun onClickLogout() {
        UserManagement.getInstance().requestLogout(object : LogoutResponseCallback() {
            override fun onCompleteLogout() {
                startActivity<LoginActivity>()
            }
        })
    }




}
