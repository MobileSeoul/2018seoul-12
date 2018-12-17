package k_spot.jnm.k_spot.Get

data class GetChannelViewMoreResponse(
        val message : String,
        val data : ArrayList<ViewMoreData>
)

data class ViewMoreData(
        val spot_id: Int,
        val review_score: Double,
        val name: String,
        val description: String,
        val address_gu: String,
        val station: String,
        val img: String,
        val channel: ChannelData
)

data class ChannelData(
        val channel_id: ArrayList<String>,
        val thumbnail_img: ArrayList<String>
)