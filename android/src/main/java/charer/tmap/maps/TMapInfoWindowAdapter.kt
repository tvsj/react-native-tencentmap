package charer.tmap.maps

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView

import com.tencent.tencentmap.mapsdk.maps.TencentMap
import com.tencent.tencentmap.mapsdk.maps.model.Marker

class TMapInfoWindowAdapter(
        private val context: Context,
        private val markers: HashMap<String, TMapMarker>
) : TencentMap.InfoWindowAdapter {
    private val paddingTop = context.resources.displayMetrics.density

    override fun getInfoWindow(marker: Marker): View? {
        return markers[marker.id]?.infoWindow
    }

    override fun getInfoContents(marker: Marker): View? {
        val layout = LinearLayout(context)
        layout.orientation = LinearLayout.VERTICAL

        val titleView = TextView(context)
        titleView.text = marker.title
        titleView.setTextColor(Color.parseColor("#212121"))
        layout.addView(titleView)

        val snippet = marker.snippet
        if (snippet.isNotEmpty()) {
            val snippetView = TextView(context)
            snippetView.text = snippet
            snippetView.maxEms = 12
            snippetView.setPadding(0, paddingTop.toInt(), 0, 0)
            snippetView.setTextColor(Color.parseColor("#757575"))
            layout.addView(snippetView)
        }

        return layout
    }
}

