package k_spot.jnm.k_spot.Get

data class GetUserSubscribeResponse (
        val message: String,
        val data: UserSubscribeData
)

data class UserSubscribeData (
        val celebrity: ArrayList<BroadcastData>,
        val broadcast: ArrayList<BroadcastData>
)

data class BroadcastData (
        val channel_id: Int,
        val name: String,
        val thumbnail_img: String,
        val new_post_check: Int
)

