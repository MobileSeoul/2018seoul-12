package k_spot.jnm.k_spot.Get

data class GetMapPageSpotDataResponse(
        val message : String,
        val data : ArrayList<MapPageSpotData>
)


data class MapPageSpotData(
        val spot_id: Int,
        val review_score: Double,
        val name: String,
        val description: String,
        val address_gu: String,
        val station: String,
        val img: String,
        val distance : Double,
        val channel: ChannelData
)