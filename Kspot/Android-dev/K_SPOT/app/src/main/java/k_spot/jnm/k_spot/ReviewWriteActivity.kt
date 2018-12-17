package k_spot.jnm.k_spot

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
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
import android.widget.EditText
import android.widget.Toast
import com.bumptech.glide.Glide
import k_spot.jnm.k_spot.Network.ApplicationController
import k_spot.jnm.k_spot.Network.NetworkService
import k_spot.jnm.k_spot.Post.PostSpotReviewWriteResponse
import k_spot.jnm.k_spot.db.SharedPreferenceController
import kotlinx.android.synthetic.main.activity_review_write.*
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
import java.io.FileNotFoundException
import java.io.InputStream

class ReviewWriteActivity : AppCompatActivity() {
    val MY_PERMISSIONS_REQUEST_READ_EXT_STORAGE : Int = 2001
    val REQUEST_CODE_SELECT_IMAGE : Int = 2002

    var isTitle: Boolean = false
    var isScore: Boolean = false
    var isText: Boolean = false
    var isIMG: Boolean = false
    private val REQ_CODE_SELECT_IMAGE = 100
    lateinit var networkService: NetworkService
    var spot_id : Int = 0


    private var image: MultipartBody.Part? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review_write)
        spot_id = intent.getIntExtra("spot_id", 0)
        // Content EditText의 수 바꾸기
        changeContentsEditTextNum()

        setStatusBarTransparent()

        setOnClickListener(spot_id)
        Log.e("스팟 아이디 값은 " , spot_id.toString())
    }

    fun postReviewWrite() {
        networkService = ApplicationController.instance.networkService
        var title = RequestBody.create(MediaType.parse("text/plain"), review_write_act_title_edit_text.text.toString())
        var description = RequestBody.create(MediaType.parse("text/plain"), review_write_act_content_posting_et.text.toString())

        val postSpotReviewWriteResponse = networkService.postSpotReviewWriteResponse(0, SharedPreferenceController.getAuthorization(applicationContext),
                spot_id, title, description, image, review_write_act_star_tv.text.toString().toDouble())
        postSpotReviewWriteResponse.enqueue(object : Callback<PostSpotReviewWriteResponse> {
            override fun onFailure(call: Call<PostSpotReviewWriteResponse>?, t: Throwable?) {
                Log.e("리뷰작성 실패", t.toString())
            }

            override fun onResponse(call: Call<PostSpotReviewWriteResponse>?, response: Response<PostSpotReviewWriteResponse>?) {
                if (response!!.isSuccessful) {
                    toast("리뷰 작성 성공")
                    finish()
                }
            }
        })
    }

    // Content EditText의 수 바꾸기
    fun changeContentsEditTextNum() {

        val editTextContent = findViewById<EditText>(R.id.review_write_act_content_posting_et)
        editTextContent.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (editTextContent.text.toString().isNotBlank()) {
                    isText = true
                    review_write_act_display_editText_num_tv.text = editTextContent.text.toString().length.toString()
                    if (editTextContent.text.toString().length >= 1000) {
                        Toast.makeText(applicationContext, "글자 수가 1000자를 초과하였습니다.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    isText = false
                }
            }

        })

    }


    // 이미지 뷰 바꾸고 클릭 됐을 때 이미지 뷰 선택 창으로 이동
    fun changeImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = android.provider.MediaStore.Images.Media.CONTENT_TYPE
        intent.data = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        startActivityForResult(intent, REQ_CODE_SELECT_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQ_CODE_SELECT_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                data?.let {
                    var  seletedPictureUri = it.data
                    val options = BitmapFactory.Options()
                    val inputStream : InputStream = contentResolver.openInputStream(seletedPictureUri)
                    val bitmap = BitmapFactory.decodeStream(inputStream, null, options)
                    val byteArrayOutputStream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutputStream)
                    val photoBody = RequestBody.create(MediaType.parse("image/jpg"), byteArrayOutputStream.toByteArray())

                    image = MultipartBody.Part.createFormData("review_img", File(seletedPictureUri.toString()).name, photoBody)

                    Glide.with(this@ReviewWriteActivity).load(seletedPictureUri).thumbnail(0.1f).into(review_write_act_upload_pic_iv)
                }
            }
        }
    }

    // 상태바 투명하게 하는 함수
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
//            DrawableCompat.setTint(, "#757575")
        }
        // 밑에 두줄 아이콘 회색으로 바꾸는 코드
        val view: View? = window.decorView
        view!!.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
    }

    // 상태바 투명하게 하는 함수
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

    // 상단바 밑으로 뷰 보이게하는 코드
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

    // 큰 별 리뷰 만들기
    fun makeReviewWriteMoreStar(starCount: Double) {
        if (starCount == 0.0) {
            review_write_act_star_0btn.setImageResource(R.drawable.review_page_left_star)
            review_write_act_star_0_5btn.setImageResource(R.drawable.review_page_center_star)
            review_write_act_star_1_right_star.setImageResource(R.drawable.review_page_right_star)
            review_write_act_star_2_left_star.setImageResource(R.drawable.review_page_left_star)
            review_write_act_star_1_5btn.setImageResource(R.drawable.review_page_center_star)
            review_write_act_star_2_right_star.setImageResource(R.drawable.review_page_right_star)
            review_write_act_star_3_left_star.setImageResource(R.drawable.review_page_left_star)
            review_write_act_star_2_5btn.setImageResource(R.drawable.review_page_center_star)
            review_write_act_star_3_right_star.setImageResource(R.drawable.review_page_right_star)
            review_write_act_star_4_left_star.setImageResource(R.drawable.review_page_left_star)
            review_write_act_star_3_5btn.setImageResource(R.drawable.review_page_center_star)
            review_write_act_star_4_right_star.setImageResource(R.drawable.review_page_right_star)
            review_write_act_star_5_left_star.setImageResource(R.drawable.review_page_left_star)
            review_write_act_star_4_5btn.setImageResource(R.drawable.review_page_center_star)
            review_write_act_star_5_right_star.setImageResource(R.drawable.review_page_right_star)

        } else if (starCount == 0.5) {
            review_write_act_star_0btn.setImageResource(R.drawable.review_page_halfstar_left)
            review_write_act_star_0_5btn.setImageResource(R.drawable.review_page_halfstar_center)
            review_write_act_star_1_right_star.setImageResource(R.drawable.review_page_halfstar_right)
            review_write_act_star_2_left_star.setImageResource(R.drawable.review_page_left_star)
            review_write_act_star_1_5btn.setImageResource(R.drawable.review_page_center_star)
            review_write_act_star_2_right_star.setImageResource(R.drawable.review_page_right_star)
            review_write_act_star_3_left_star.setImageResource(R.drawable.review_page_left_star)
            review_write_act_star_2_5btn.setImageResource(R.drawable.review_page_center_star)
            review_write_act_star_3_right_star.setImageResource(R.drawable.review_page_right_star)
            review_write_act_star_4_left_star.setImageResource(R.drawable.review_page_left_star)
            review_write_act_star_3_5btn.setImageResource(R.drawable.review_page_center_star)
            review_write_act_star_4_right_star.setImageResource(R.drawable.review_page_right_star)
            review_write_act_star_5_left_star.setImageResource(R.drawable.review_page_left_star)
            review_write_act_star_4_5btn.setImageResource(R.drawable.review_page_center_star)
            review_write_act_star_5_right_star.setImageResource(R.drawable.review_page_right_star)
        } else if (starCount == 1.0) {
            review_write_act_star_0btn.setImageResource(R.drawable.review_page_fullstar_left)
            review_write_act_star_0_5btn.setImageResource(R.drawable.review_page_fullstar_center)
            review_write_act_star_1_right_star.setImageResource(R.drawable.review_page_fullstar_right)
            review_write_act_star_2_left_star.setImageResource(R.drawable.review_page_left_star)
            review_write_act_star_1_5btn.setImageResource(R.drawable.review_page_center_star)
            review_write_act_star_2_right_star.setImageResource(R.drawable.review_page_right_star)
            review_write_act_star_3_left_star.setImageResource(R.drawable.review_page_left_star)
            review_write_act_star_2_5btn.setImageResource(R.drawable.review_page_center_star)
            review_write_act_star_3_right_star.setImageResource(R.drawable.review_page_right_star)
            review_write_act_star_4_left_star.setImageResource(R.drawable.review_page_left_star)
            review_write_act_star_3_5btn.setImageResource(R.drawable.review_page_center_star)
            review_write_act_star_4_right_star.setImageResource(R.drawable.review_page_right_star)
            review_write_act_star_5_left_star.setImageResource(R.drawable.review_page_left_star)
            review_write_act_star_4_5btn.setImageResource(R.drawable.review_page_center_star)
            review_write_act_star_5_right_star.setImageResource(R.drawable.review_page_right_star)

        } else if (starCount == 1.5) {
            review_write_act_star_0btn.setImageResource(R.drawable.review_page_fullstar_left)
            review_write_act_star_0_5btn.setImageResource(R.drawable.review_page_fullstar_center)
            review_write_act_star_1_right_star.setImageResource(R.drawable.review_page_fullstar_right)
            review_write_act_star_2_left_star.setImageResource(R.drawable.review_page_fullstar_left)
            review_write_act_star_1_5btn.setImageResource(R.drawable.review_page_halfstar_center)
            review_write_act_star_2_right_star.setImageResource(R.drawable.review_page_halfstar_right)
            review_write_act_star_3_left_star.setImageResource(R.drawable.review_page_left_star)
            review_write_act_star_2_5btn.setImageResource(R.drawable.review_page_center_star)
            review_write_act_star_3_right_star.setImageResource(R.drawable.review_page_right_star)
            review_write_act_star_4_left_star.setImageResource(R.drawable.review_page_left_star)
            review_write_act_star_3_5btn.setImageResource(R.drawable.review_page_center_star)
            review_write_act_star_4_right_star.setImageResource(R.drawable.review_page_right_star)
            review_write_act_star_5_left_star.setImageResource(R.drawable.review_page_left_star)
            review_write_act_star_4_5btn.setImageResource(R.drawable.review_page_center_star)
            review_write_act_star_5_right_star.setImageResource(R.drawable.review_page_right_star)

        } else if (starCount == 2.0) {
            review_write_act_star_0btn.setImageResource(R.drawable.review_page_fullstar_left)
            review_write_act_star_0_5btn.setImageResource(R.drawable.review_page_fullstar_center)
            review_write_act_star_1_right_star.setImageResource(R.drawable.review_page_fullstar_right)
            review_write_act_star_2_left_star.setImageResource(R.drawable.review_page_fullstar_left)
            review_write_act_star_1_5btn.setImageResource(R.drawable.review_page_fullstar_center)
            review_write_act_star_2_right_star.setImageResource(R.drawable.review_page_fullstar_right)
            review_write_act_star_3_left_star.setImageResource(R.drawable.review_page_left_star)
            review_write_act_star_2_5btn.setImageResource(R.drawable.review_page_center_star)
            review_write_act_star_3_right_star.setImageResource(R.drawable.review_page_right_star)
            review_write_act_star_4_left_star.setImageResource(R.drawable.review_page_left_star)
            review_write_act_star_3_5btn.setImageResource(R.drawable.review_page_center_star)
            review_write_act_star_4_right_star.setImageResource(R.drawable.review_page_right_star)
            review_write_act_star_5_left_star.setImageResource(R.drawable.review_page_left_star)
            review_write_act_star_4_5btn.setImageResource(R.drawable.review_page_center_star)
            review_write_act_star_5_right_star.setImageResource(R.drawable.review_page_right_star)

        } else if (starCount == 2.5) {
            review_write_act_star_0btn.setImageResource(R.drawable.review_page_fullstar_left)
            review_write_act_star_0_5btn.setImageResource(R.drawable.review_page_fullstar_center)
            review_write_act_star_1_right_star.setImageResource(R.drawable.review_page_fullstar_right)
            review_write_act_star_2_left_star.setImageResource(R.drawable.review_page_fullstar_left)
            review_write_act_star_1_5btn.setImageResource(R.drawable.review_page_fullstar_center)
            review_write_act_star_2_right_star.setImageResource(R.drawable.review_page_fullstar_right)
            review_write_act_star_3_left_star.setImageResource(R.drawable.review_page_fullstar_left)
            review_write_act_star_2_5btn.setImageResource(R.drawable.review_page_halfstar_center)
            review_write_act_star_3_right_star.setImageResource(R.drawable.review_page_halfstar_right)
            review_write_act_star_4_left_star.setImageResource(R.drawable.review_page_left_star)
            review_write_act_star_3_5btn.setImageResource(R.drawable.review_page_center_star)
            review_write_act_star_4_right_star.setImageResource(R.drawable.review_page_right_star)
            review_write_act_star_5_left_star.setImageResource(R.drawable.review_page_left_star)
            review_write_act_star_4_5btn.setImageResource(R.drawable.review_page_center_star)
            review_write_act_star_5_right_star.setImageResource(R.drawable.review_page_right_star)

        } else if (starCount == 3.0) {
            review_write_act_star_0btn.setImageResource(R.drawable.review_page_fullstar_left)
            review_write_act_star_0_5btn.setImageResource(R.drawable.review_page_fullstar_center)
            review_write_act_star_1_right_star.setImageResource(R.drawable.review_page_fullstar_right)
            review_write_act_star_2_left_star.setImageResource(R.drawable.review_page_fullstar_left)
            review_write_act_star_1_5btn.setImageResource(R.drawable.review_page_fullstar_center)
            review_write_act_star_2_right_star.setImageResource(R.drawable.review_page_fullstar_right)
            review_write_act_star_3_left_star.setImageResource(R.drawable.review_page_fullstar_left)
            review_write_act_star_2_5btn.setImageResource(R.drawable.review_page_fullstar_center)
            review_write_act_star_3_right_star.setImageResource(R.drawable.review_page_fullstar_right)
            review_write_act_star_4_left_star.setImageResource(R.drawable.review_page_left_star)
            review_write_act_star_3_5btn.setImageResource(R.drawable.review_page_center_star)
            review_write_act_star_4_right_star.setImageResource(R.drawable.review_page_right_star)
            review_write_act_star_5_left_star.setImageResource(R.drawable.review_page_left_star)
            review_write_act_star_4_5btn.setImageResource(R.drawable.review_page_center_star)
            review_write_act_star_5_right_star.setImageResource(R.drawable.review_page_right_star)

        } else if (starCount == 3.5) {
            review_write_act_star_0btn.setImageResource(R.drawable.review_page_fullstar_left)
            review_write_act_star_0_5btn.setImageResource(R.drawable.review_page_fullstar_center)
            review_write_act_star_1_right_star.setImageResource(R.drawable.review_page_fullstar_right)
            review_write_act_star_2_left_star.setImageResource(R.drawable.review_page_fullstar_left)
            review_write_act_star_1_5btn.setImageResource(R.drawable.review_page_fullstar_center)
            review_write_act_star_2_right_star.setImageResource(R.drawable.review_page_fullstar_right)
            review_write_act_star_3_left_star.setImageResource(R.drawable.review_page_fullstar_left)
            review_write_act_star_2_5btn.setImageResource(R.drawable.review_page_fullstar_center)
            review_write_act_star_3_right_star.setImageResource(R.drawable.review_page_fullstar_right)
            review_write_act_star_4_left_star.setImageResource(R.drawable.review_page_fullstar_left)
            review_write_act_star_3_5btn.setImageResource(R.drawable.review_page_halfstar_center)
            review_write_act_star_4_right_star.setImageResource(R.drawable.review_page_halfstar_right)
            review_write_act_star_5_left_star.setImageResource(R.drawable.review_page_left_star)
            review_write_act_star_4_5btn.setImageResource(R.drawable.review_page_center_star)
            review_write_act_star_5_right_star.setImageResource(R.drawable.review_page_right_star)

        } else if (starCount == 4.0) {
            review_write_act_star_0btn.setImageResource(R.drawable.review_page_fullstar_left)
            review_write_act_star_0_5btn.setImageResource(R.drawable.review_page_fullstar_center)
            review_write_act_star_1_right_star.setImageResource(R.drawable.review_page_fullstar_right)
            review_write_act_star_2_left_star.setImageResource(R.drawable.review_page_fullstar_left)
            review_write_act_star_1_5btn.setImageResource(R.drawable.review_page_fullstar_center)
            review_write_act_star_2_right_star.setImageResource(R.drawable.review_page_fullstar_right)
            review_write_act_star_3_left_star.setImageResource(R.drawable.review_page_fullstar_left)
            review_write_act_star_2_5btn.setImageResource(R.drawable.review_page_fullstar_center)
            review_write_act_star_3_right_star.setImageResource(R.drawable.review_page_fullstar_right)
            review_write_act_star_4_left_star.setImageResource(R.drawable.review_page_fullstar_left)
            review_write_act_star_3_5btn.setImageResource(R.drawable.review_page_fullstar_center)
            review_write_act_star_4_right_star.setImageResource(R.drawable.review_page_fullstar_right)
            review_write_act_star_5_left_star.setImageResource(R.drawable.review_page_left_star)
            review_write_act_star_4_5btn.setImageResource(R.drawable.review_page_center_star)
            review_write_act_star_5_right_star.setImageResource(R.drawable.review_page_right_star)

        } else if (starCount == 4.5) {
            review_write_act_star_0btn.setImageResource(R.drawable.review_page_fullstar_left)
            review_write_act_star_0_5btn.setImageResource(R.drawable.review_page_fullstar_center)
            review_write_act_star_1_right_star.setImageResource(R.drawable.review_page_fullstar_right)
            review_write_act_star_2_left_star.setImageResource(R.drawable.review_page_fullstar_left)
            review_write_act_star_1_5btn.setImageResource(R.drawable.review_page_fullstar_center)
            review_write_act_star_2_right_star.setImageResource(R.drawable.review_page_fullstar_right)
            review_write_act_star_3_left_star.setImageResource(R.drawable.review_page_fullstar_left)
            review_write_act_star_2_5btn.setImageResource(R.drawable.review_page_fullstar_center)
            review_write_act_star_3_right_star.setImageResource(R.drawable.review_page_fullstar_right)
            review_write_act_star_4_left_star.setImageResource(R.drawable.review_page_fullstar_left)
            review_write_act_star_3_5btn.setImageResource(R.drawable.review_page_fullstar_center)
            review_write_act_star_4_right_star.setImageResource(R.drawable.review_page_fullstar_right)
            review_write_act_star_5_left_star.setImageResource(R.drawable.review_page_fullstar_left)
            review_write_act_star_4_5btn.setImageResource(R.drawable.review_page_halfstar_center)
            review_write_act_star_5_right_star.setImageResource(R.drawable.review_page_halfstar_right)

        } else if (starCount == 5.0) {
            review_write_act_star_0btn.setImageResource(R.drawable.review_page_fullstar_left)
            review_write_act_star_0_5btn.setImageResource(R.drawable.review_page_fullstar_center)
            review_write_act_star_1_right_star.setImageResource(R.drawable.review_page_fullstar_right)
            review_write_act_star_2_left_star.setImageResource(R.drawable.review_page_fullstar_left)
            review_write_act_star_1_5btn.setImageResource(R.drawable.review_page_fullstar_center)
            review_write_act_star_2_right_star.setImageResource(R.drawable.review_page_fullstar_right)
            review_write_act_star_3_left_star.setImageResource(R.drawable.review_page_fullstar_left)
            review_write_act_star_2_5btn.setImageResource(R.drawable.review_page_fullstar_center)
            review_write_act_star_3_right_star.setImageResource(R.drawable.review_page_fullstar_right)
            review_write_act_star_4_left_star.setImageResource(R.drawable.review_page_fullstar_left)
            review_write_act_star_3_5btn.setImageResource(R.drawable.review_page_fullstar_center)
            review_write_act_star_4_right_star.setImageResource(R.drawable.review_page_fullstar_right)
            review_write_act_star_5_left_star.setImageResource(R.drawable.review_page_fullstar_left)
            review_write_act_star_4_5btn.setImageResource(R.drawable.review_page_fullstar_center)
            review_write_act_star_5_right_star.setImageResource(R.drawable.review_page_fullstar_right)
        }
    }

    fun setOnClickListener(spot_id: Int) {

        // 이미지 바꾸기
        review_write_act_upload_pic_btn.setOnClickListener {
            requestReadExternalStoragePermission()

        }

        // View에 그림 보여주기
        review_write_act_upload_pic_iv.setOnClickListener {
//            review_write_act_upload_pic_iv.setImageBitmap(null)
//            isIMG = false
//            checkUploadedContent()
        }

        review_write_act_finish_btn.setOnClickListener {
            postReviewWrite()

        }

        review_write_act_back_btn.setOnClickListener {
            finish()
        }

        review_write_act_star_0btn.setOnClickListener {
            review_write_act_star_tv.text = "0"
            makeReviewWriteMoreStar(0.0)
        }
        review_write_act_star_0_5btn.setOnClickListener {
            review_write_act_star_tv.text = "0.5"
            makeReviewWriteMoreStar(0.5)
        }
        review_write_act_star_1btn.setOnClickListener {
            review_write_act_star_tv.text = "1"
            makeReviewWriteMoreStar(1.0)
        }
        review_write_act_star_1_5btn.setOnClickListener {
            review_write_act_star_tv.text = "1.5"
            makeReviewWriteMoreStar(1.5)
        }
        review_write_act_star_2btn.setOnClickListener {
            review_write_act_star_tv.text = "2"
            makeReviewWriteMoreStar(2.0)
        }
        review_write_act_star_2_5btn.setOnClickListener {
            review_write_act_star_tv.text = "2.5"
            makeReviewWriteMoreStar(2.5)
        }
        review_write_act_star_3btn.setOnClickListener {
            review_write_act_star_tv.text = "3"
            makeReviewWriteMoreStar(3.0)
        }
        review_write_act_star_3_5btn.setOnClickListener {
            review_write_act_star_tv.text = "3.5"
            makeReviewWriteMoreStar(3.5)
        }
        review_write_act_star_4btn.setOnClickListener {
            review_write_act_star_tv.text = "4"
            makeReviewWriteMoreStar(4.0)
        }
        review_write_act_star_4_5btn.setOnClickListener {
            review_write_act_star_tv.text = "4.5"
            makeReviewWriteMoreStar(4.5)
        }
        review_write_act_star_5btn.setOnClickListener {
            review_write_act_star_tv.text = "5"
            makeReviewWriteMoreStar(5.0)
        }


    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_READ_EXT_STORAGE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    val intent = Intent(Intent.ACTION_PICK)
                    intent.type = android.provider.MediaStore.Images.Media.CONTENT_TYPE
                    intent.data = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE)
                } else {
                    requestReadExternalStoragePermission()
                }
                return
            }
        }
    }

    private fun requestReadExternalStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), MY_PERMISSIONS_REQUEST_READ_EXT_STORAGE)
            }
        } else {
            changeImage()
        }
    }

}
