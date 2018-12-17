package k_spot.jnm.k_spot.Get

import java.util.*

data class GetSpotViewReviewMoreResponse (
        val message: String,
        val data: SpotViewReviewMoreData
)
data class SpotViewReviewMoreData (
        val spot_review: SpotReview,
        val reviews: ArrayList<ReviewMoreData>
)

data class ReviewMoreData (
        val name: String,
        val title: String,
        val content: String,
        val img: String,
        val review_score: Double,
        val reg_time: String
)

data class SpotReview (
        val review_score: Double,
        val review_cnt: Int
)
