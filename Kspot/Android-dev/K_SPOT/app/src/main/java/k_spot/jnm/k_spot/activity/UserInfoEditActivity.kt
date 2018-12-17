package k_spot.jnm.k_spot.activity

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import com.bumptech.glide.Glide
import k_spot.jnm.k_spot.Network.ApplicationController
import k_spot.jnm.k_spot.Post.PostUserInfoResponse
import k_spot.jnm.k_spot.R
import k_spot.jnm.k_spot.db.SharedPreferenceController
import k_spot.jnm.k_spot.dialog.PictureSelectedDialog
import kotlinx.android.synthetic.main.activity_user_info_edit.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream

class UserInfoEditActivity : AppCompatActivity() {
    val MY_PERMISSIONS_REQUEST_READ_CONTACTS: Int = 1001
    val MY_PERMISSIONS_REQUEST_READ_EXT_STORAGE: Int = 1002
    val REQUEST_CODE_SELECT_IMAGE: Int = 1004
    private var mImage: MultipartBody.Part? = null
    lateinit var initName: String
    var newName: String? = null
    var newImage: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_user_info_edit)
        setStatusBarTransparent()
        setClickListener()
        initName = intent.getStringExtra("name")

        setUserInfoView(initName, intent.getStringExtra("image"))

        changeLanguage()

    }

    private fun setUserInfoView(name: String, image_url: String) {
        et_user_info_edit_user_name.setText(name)
        Glide.with(this).load(image_url).into(iv_user_info_edit_user_image)
        tv_user_info_edit_user_name_length_count.text = "${name.length}/20"

        et_user_info_edit_user_name.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                tv_user_info_edit_user_name_length_count.text = "${s!!.length}/20"
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

    }

    fun setDefaultUserImage() {
        iv_user_info_edit_user_image.setImageResource(R.drawable.mypage_default_profile_img)
    }

    fun setSeletedPictureOption() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = android.provider.MediaStore.Images.Media.CONTENT_TYPE
        intent.data = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE)
    }

    private fun setClickListener() {
        btn_user_info_edit_selected_picture_option.setOnClickListener {
            val dialog: Dialog = PictureSelectedDialog(this)
            dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()
        }

        btn_user_info_edit_act_back.setOnClickListener {
            finish()
        }

        btn_user_info_edit_complete.setOnClickListener {
            resquestUserInfoChange()
        }
    }

    //여기서 이미지를 MultipartBody.Part로 만들어준다!!! 서버로 보내는 것이므로 이미지를 byte 처리
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SELECT_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                data?.let {
                    var seletedPictureUri = it.data
                    val options = BitmapFactory.Options()
                    val inputStream: InputStream = contentResolver.openInputStream(seletedPictureUri)
                    val bitmap = BitmapFactory.decodeStream(inputStream, null, options)
                    val byteArrayOutputStream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutputStream)
                    val photoBody = RequestBody.create(MediaType.parse("image/jpg"), byteArrayOutputStream.toByteArray())
                    mImage = MultipartBody.Part.createFormData("profile_img", File(seletedPictureUri.toString()).name, photoBody)

                    newImage = seletedPictureUri.toString()

                    Glide.with(this@UserInfoEditActivity).load(seletedPictureUri).thumbnail(0.1f).into(iv_user_info_edit_user_image)
                }

            }
        }
    }


    private fun resquestUserInfoChange() {
        val name = et_user_info_edit_user_name.text.toString()
        //여기서!!! RequestBody형식으로 String을 맵핑해서 보낸다!!! String을 Request 타입으로 바꿔서 보낸것!
        val userName = RequestBody.create(MediaType.parse("text/plain"), name)

        if (mImage != null) {
            val networkService = ApplicationController.instance.networkService
            val postUserInfoResponse = networkService.postUserInfoResponse(SharedPreferenceController.getFlag(this).toInt(), SharedPreferenceController.getAuthorization(this),
                    mImage, userName)
            postUserInfoResponse.enqueue(object : Callback<PostUserInfoResponse> {
                override fun onFailure(call: Call<PostUserInfoResponse>?, t: Throwable?) {
                    Log.e("사진 전송 에러", t.toString())
                }

                override fun onResponse(call: Call<PostUserInfoResponse>?, response: Response<PostUserInfoResponse>?) {
                    response?.let {
                        if (response.isSuccessful) {
                            newName = name
                            if (SharedPreferenceController.getFlag(this@UserInfoEditActivity) == "0"){
                                toast("완료")
                            } else {
                                toast("complete")
                            }


                            val intent = Intent()
                            intent.putExtra("name", newName)
                            intent.putExtra("image", newImage)
                            setResult(Activity.RESULT_OK, intent)


                            finish()
                        }
                    }
                }
            })
        } else if (mImage == null && name != initName) {
            val networkService = ApplicationController.instance.networkService
            val postUserInfoResponse = networkService.postUserInfoResponse(SharedPreferenceController.getFlag(this).toInt(), SharedPreferenceController.getAuthorization(this),
                    null, userName)
            postUserInfoResponse.enqueue(object : Callback<PostUserInfoResponse> {
                override fun onFailure(call: Call<PostUserInfoResponse>?, t: Throwable?) {
                    Log.e("사진 전송 에러", t.toString())
                }

                override fun onResponse(call: Call<PostUserInfoResponse>?, response: Response<PostUserInfoResponse>?) {
                    response?.let {
                        if (response.isSuccessful) {
                            newName = name
                            if (SharedPreferenceController.getFlag(this@UserInfoEditActivity) == "0"){
                                toast("완료")
                            } else {
                                toast("complete")
                            }
                            val intent = Intent()
                            intent.putExtra("name", newName)
                            setResult(Activity.RESULT_OK, intent)

                            finish()
                        }
                    }
                }
            })
        } else {
            finish()
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_READ_EXT_STORAGE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    val intent = Intent(Intent.ACTION_PICK)
//                    intent.type = android.provider.MediaStore.Images.Media.CONTENT_TYPE
//                    intent.data = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
//                    startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE)
                    setSeletedPictureOption()
                } else {
                    requestReadExternalStoragePermission()
                }
                return
            }
        }
    }

    fun requestReadExternalStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), MY_PERMISSIONS_REQUEST_READ_EXT_STORAGE)
            }
        } else {
            setSeletedPictureOption()
        }
    }


    private fun setWindowFlag(bits: Int, on: Boolean) {
        val win = window
        val winParams = win.attributes
        if (on) {
            winParams.flags = winParams.flags or bits
        } else {
            winParams.flags = winParams.flags and bits.inv()
        }
        win.attributes = winParams
    }

    private fun setStatusBarTransparent() {
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true)
        }
        if (Build.VERSION.SDK_INT >= 19) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
            window.statusBarColor = Color.TRANSPARENT
        }

        val view: View? = window.decorView
        view!!.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val window = window
        val decorView = window.decorView
        if (Configuration.ORIENTATION_LANDSCAPE === newConfig.orientation) {
            decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.statusBarColor = Color.parseColor("#55000000") // set dark color, the icon will auto change light
            }
        } else {
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.statusBarColor = Color.parseColor("#fffafafa")
            }
        }
    }

    private fun changeLanguage(){
        if(SharedPreferenceController.getFlag(this) == "0"){
            tv_user_info_edit_title.text = "회원정보 수정"
            btn_user_info_edit_complete.text = "완료"
            tv_nickname.text = "닉네임"
        } else {
            tv_user_info_edit_title.text = "Edit Profile"
            btn_user_info_edit_complete.text = "complete"
            tv_nickname.text = "Nickname"
        }
    }


}
