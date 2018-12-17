package k_spot.jnm.k_spot.adapter

import android.content.Context
import android.view.View
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import k_spot.jnm.k_spot.R
import org.jetbrains.anko.layoutInflater

class InfoWindowAdapter(val ctx : Context) : GoogleMap.InfoWindowAdapter{
    override fun getInfoContents(marker: Marker?): View? {
        return null
    }

    override fun getInfoWindow(marker: Marker?): View {
        val view = ctx.layoutInflater.inflate(R.layout.map_info_window_layout, null)

        if (marker != null){
            (view.findViewById(R.id.map_info_window_title) as TextView).text = marker.title
            (view.findViewById(R.id.map_info_window_content) as TextView).text = marker.snippet
        }

        return view
    }

}