package k_spot.jnm.k_spot.Get

data class GetCategoryListResponse (
        val message: String,
        val data: CategoryListData
)

data class CategoryListData (
        val channel_celebrity_list: ArrayList<ChannelListData>,
        val channel_broadcast_list: ArrayList<ChannelListData>
)

data class ChannelListData (
        val channel_id: Int,
        val subscription_cnt: Int,
        val spot_cnt: Int,
        val thumbnail_img: String,
        var subscription: Int,
        val name: String
)
