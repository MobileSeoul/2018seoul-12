package k_spot.jnm.k_spot.Post

data class PostTempLoginResponse (
        val message: String,
        val data: TempData
)

data class TempData (
        val id: String,
        val authorization: String
)
