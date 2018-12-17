package k_spot.jnm.k_spot.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import k_spot.jnm.k_spot.R
import k_spot.jnm.k_spot.activity.UserInfoEditActivity
import k_spot.jnm.k_spot.db.SharedPreferenceController
import kotlinx.android.synthetic.main.dialog_edit_select_picture_option_message.*

class PictureSelectedDialog(val ctx: Context) : Dialog(ctx){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_edit_select_picture_option_message)
        if (SharedPreferenceController.getFlag(ctx) == "0"){
            tv_edit_picture_option_dialog_album.text = "앨범에서 사진 선택"
            tv_edit_picture_option_dialog_default.text = "기본 이미지 선택"
            tv_edit_picture_option_dialog_cancel.text = "취소"
        } else {
            tv_edit_picture_option_dialog_album.text = "Select image from album"
            tv_edit_picture_option_dialog_default.text = "Default image"
            tv_edit_picture_option_dialog_cancel.text = "Cancel"
        }
        setOptionClickListener()
    }


    private fun setOptionClickListener(){
        btn_edit_picture_option_dialog_album.setOnClickListener {
            (ctx as UserInfoEditActivity).requestReadExternalStoragePermission()
            dismiss()
        }

        btn_edit_picture_option_dialog_default.setOnClickListener {
            (ctx as UserInfoEditActivity).setDefaultUserImage()
            dismiss()
        }

        btn_edit_picture_option_dialog_cancel.setOnClickListener {
            dismiss()
        }
    }
}