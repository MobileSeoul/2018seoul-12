package k_spot.jnm.k_spot.Get

data class GetMyPageResponse (
        val message: String,
        val data: MyPageResponseData
)

data class MyPageResponseData (
        val user: UserMyPageData,
        val channel: ArrayList<ChannelMyPageData>
)

data class ChannelMyPageData (
        val channel_id: Int,
        val kor_name: String,
        val eng_name: String,
        val background_img: String
)

data class UserMyPageData (
        val name: String,
        val profile_img: String
)
