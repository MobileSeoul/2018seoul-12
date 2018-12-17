package k_spot.jnm.k_spot.Get

import android.os.Parcel
import android.os.Parcelable

data class GetSpotViewMoreResponse (
        val message: String,
        val data: ArrayList<SpotViewMoreData>
)

data class SpotViewMoreData (
        var spot_id: Int,
        val img: ArrayList<String>,
        val name: String,
        var description: String,
        val address: String,
        val review_score: Double,
        val review_cnt: Int,
        val line_number: String,
        val station: String,
        val prev_station: String,
        val next_station: String,
        val open_time: String,
        val close_time: String,
        val contact: String,
        var scrap_cnt: Int,
        var is_scrap: Int,
        val channel: ChannelSpotViewMoreData,
        val reviews: ArrayList<ReviewSpotViewMoreData>
)

data class ChannelSpotViewMoreData (
        val channel_id: ArrayList<String>,
        val channel_name: ArrayList<String>,
        val thumbnail_img: ArrayList<String>,
        val is_subscription: ArrayList<String>
)

data class ReviewSpotViewMoreData (
        val review_id : Int,
        val name: String,
        val title: String,
        val content: String,
        val img: String,
        val review_score: Double,
        val reg_time: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readDouble(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(review_id)
        parcel.writeString(name)
        parcel.writeString(title)
        parcel.writeString(content)
        parcel.writeString(img)
        parcel.writeDouble(review_score)
        parcel.writeString(reg_time)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ReviewSpotViewMoreData> {
        override fun createFromParcel(parcel: Parcel): ReviewSpotViewMoreData {
            return ReviewSpotViewMoreData(parcel)
        }

        override fun newArray(size: Int): Array<ReviewSpotViewMoreData?> {
            return arrayOfNulls(size)
        }
    }
}

data class ChannelRecyclerViewData (
        val channel_id: String,
        val channel_name: String,
        val thumbnail_img: String,
        var is_subscription: String
)


data class ViewPagerSpotViewMoreActData(
        val img : String
)