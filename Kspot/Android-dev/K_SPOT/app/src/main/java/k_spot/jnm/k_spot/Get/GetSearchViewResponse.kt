package k_spot.jnm.k_spot.Get

data class GetSearchViewResponse (
        val message: String,
        val data: SearchViewData
)

data class SearchViewData (
        val celebrity: ArrayList<BroadcastSearchViewData>,
        val broadcast: ArrayList<BroadcastSearchViewData>,
        val event: ArrayList<EventSearchViewData>
)

data class BroadcastSearchViewData (
        val channel_id: Int,
        val name: String
)

data class EventSearchViewData (
        val spot_id: Int,
        val name: String
)
