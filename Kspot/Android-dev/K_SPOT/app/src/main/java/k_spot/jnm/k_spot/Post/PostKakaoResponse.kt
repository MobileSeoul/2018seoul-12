package k_spot.jnm.k_spot.Post

data class PostKakaoResponse (
        val message: String,
        val data: KakaoData
)

data class KakaoData (
        val id: Int,
        val authorization: String
)
