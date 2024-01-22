package charer.tmap.maps

import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Handler
import android.util.Log
import android.view.View
import charer.tmap.toPx

import com.facebook.react.bridge.ReadableArray
import com.facebook.react.views.view.ReactViewGroup
import com.tencent.tencentmap.mapsdk.maps.TencentMap
import com.tencent.tencentmap.mapsdk.maps.model.BitmapDescriptor
import com.tencent.tencentmap.mapsdk.maps.model.BitmapDescriptorFactory
import com.tencent.tencentmap.mapsdk.maps.model.LatLng
import com.tencent.tencentmap.mapsdk.maps.model.Marker
import com.tencent.tencentmap.mapsdk.maps.model.MarkerOptions

class TMapMarker(context: Context) : ReactViewGroup(context), TMapOverlay {
  companion object {
    private val COLORS = mapOf(
      "AZURE" to BitmapDescriptorFactory.HUE_AZURE,
      "BLUE" to BitmapDescriptorFactory.HUE_BLUE,
      "CYAN" to BitmapDescriptorFactory.HUE_CYAN,
      "GREEN" to BitmapDescriptorFactory.HUE_GREEN,
      "MAGENTA" to BitmapDescriptorFactory.HUE_MAGENTA,
      "ORANGE" to BitmapDescriptorFactory.HUE_ORANGE,
      "RED" to BitmapDescriptorFactory.HUE_RED,
      "ROSE" to BitmapDescriptorFactory.HUE_ROSE,
      "VIOLET" to BitmapDescriptorFactory.HUE_VIOLET,
      "YELLOW" to BitmapDescriptorFactory.HUE_YELLOW
    )
  }

  private var icon: View? = null
  private var bitmapDescriptor: BitmapDescriptor? = null
  private var anchorU: Float = 0.5f
  private var anchorV: Float = 1f
  var infoWindow: TMapInfoWindow? = null

  var marker: Marker? = null
    private set

  var position: LatLng? = null
    set(value) {
      field = value
      marker?.position = value
    }

  var zIndex: Float = 0.0f
    set(value) {
      field = value
      marker?.zIndex = value.toInt()
    }

  // 气泡infowindow标题
  var title = ""
    set(value) {
      field = value
      marker?.title = value
    }

  // 气泡内容
  var snippet = ""
    set(value) {
      field = value
      marker?.snippet = value
    }

  var flat: Boolean = false
    set(value) {
      field = value
      marker?.isFastLoad = value
    }

  var opacity: Float = 1f
    set(value) {
      field = value
      marker?.alpha = value
    }

  var draggable: Boolean = false
    set(value) {
      field = value
      marker?.isDraggable = value
    }

  var clickDisabled: Boolean = false
    set(value) {
      field = value
      marker?.isClickable = !value
    }

  var infoWindowDisabled: Boolean = false
    set(value) {
      field = value
      marker?.isInfoWindowEnable = !value
    }

  var active: Boolean = false
    set(value) {
      field = value
      if (value) {
        marker?.showInfoWindow()
      } else {
        marker?.hideInfoWindow()
      }
    }

  override fun addView(child: View, index: Int) {
    super.addView(child, index)
    icon = child
    icon?.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ -> updateIcon() }
  }

  override fun add(map: TencentMap) {
    var position1 = LatLng(position);
    marker = map.addMarker(MarkerOptions(position1)
      .icon(bitmapDescriptor)
      .alpha(opacity)
      .draggable(draggable)
      .anchor(anchorU, anchorV)
      .infoWindowEnable(!infoWindowDisabled)
      .title(title)
      .fastLoad(false)
      .snippet(snippet)
      .zIndex(zIndex))

    this.clickDisabled = clickDisabled
    this.active = active
  }

  override fun remove() {
    marker?.remove()
  }

  fun setIconColor(color: String) {
    bitmapDescriptor = COLORS[color.toUpperCase()]?.let {
      BitmapDescriptorFactory.defaultMarker(it)
    }
    marker?.setIcon(bitmapDescriptor)
  }

  fun updateIcon() {
    icon?.let {
      if (it.width != 0 && it.height != 0) {
        Log.d(TAG, "updateIcon: 更新图标")
        val bitmap = Bitmap.createBitmap(
          it.width, it.height, Bitmap.Config.ARGB_8888)
        it.draw(Canvas(bitmap))
        bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bitmap)
        marker?.setIcon(bitmapDescriptor)
      }
    }
  }

  fun setImage(name: String) {
    Handler().postDelayed({
      val drawable = context.resources.getIdentifier(name, "drawable", context.packageName)
      bitmapDescriptor = BitmapDescriptorFactory.fromResource(drawable)
      marker?.setIcon(bitmapDescriptor)
    }, 0)
  }

  fun setAnchor(x: Double, y: Double) {
    anchorU = x.toFloat()
    anchorV = y.toFloat()
    marker?.setAnchor(anchorU, anchorV)
  }

  fun lockToScreen(args: ReadableArray?) {
    if (args != null) {
      val x = args.getDouble(0).toFloat().toPx()
      val y = args.getDouble(1).toFloat().toPx()
      marker?.setFixingPoint(x,y)
    }
  }
}
