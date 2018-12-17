package k_spot.jnm.k_spot.Get

import android.os.Parcel
import android.os.Parcelable

data class GetSearchResultResponse (
        val message: String,
        val data: SearchResultData
)

data class SearchResultData (
        val channel: ArrayList<ChannelSearchResultData>,
        val place: ArrayList<PlaceSearchResultData>,
        val event: ArrayList<PlaceSearchResultData>
)
data class ChannelSearchResultData (
        val channel_id: Int,
        val name: String,
        val subscription_cnt: Int,
        val spot_cnt: Int,
        val thumbnail_img: String,
        var subscription: Int
) :Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readInt()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(channel_id)
        parcel.writeString(name)
        parcel.writeInt(subscription_cnt)
        parcel.writeInt(spot_cnt)
        parcel.writeString(thumbnail_img)
        parcel.writeInt(subscription)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ChannelSearchResultData> {
        override fun createFromParcel(parcel: Parcel): ChannelSearchResultData {
            return ChannelSearchResultData(parcel)
        }

        override fun newArray(size: Int): Array<ChannelSearchResultData?> {
            return arrayOfNulls(size)
        }
    }
}

data class PlaceSearchResultData (
        val type: Int,
        val spot_id: Int,
        val name: String,
        val description: String,
        val img: String,
        val address_gu: String,
        val station: String,
        val scrap_cnt: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readInt()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(type)
        parcel.writeInt(spot_id)
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeString(img)
        parcel.writeString(address_gu)
        parcel.writeString(station)
        parcel.writeInt(scrap_cnt)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PlaceSearchResultData> {
        override fun createFromParcel(parcel: Parcel): PlaceSearchResultData {
            return PlaceSearchResultData(parcel)
        }

        override fun newArray(size: Int): Array<PlaceSearchResultData?> {
            return arrayOfNulls(size)
        }
    }
}


//data class EventSearchResultData (
//        val type: Int,
//        val spot_id: Int,
//        val name: String,
//        val description: String,
//        val img: String,
//        val address_gu: String,
//        val station: String,
//        val scrap_cnt: Int
//)
