package k_spot.jnm.k_spot.Get

data class GetCategoryDetailResponse(
        val message: String,
        val data: CategoryDetailData
)
data class CategoryDetailData(
        val channel_info : ChannelInfoData,
        val place_recommended_by_channel : ArrayList<PlaceRecommendData>,
        val place_related_channel : ArrayList<PlaceOrEventRelativeData>,
        val event_related_channel : ArrayList<PlaceOrEventRelativeData>

)
data class ChannelInfoData(
        val id: Int,
        val background_img: String,
        val thumbnail_img: String,
        var subscription_cnt: Int,
        var subscription: Int,
        val name: String,
        val company: String
)
data class PlaceRecommendData(
        val spot_id: Int,
        val img: String,
        val name: String
)
data class PlaceOrEventRelativeData(
        val spot_id: Int,
        val name: String,
        val description: String,
        val address_gu: String,
        val station: String,
        val img: String,
        val scrap_cnt: Int
)