package charer.tmap.maps

import android.content.ContentValues.TAG
import android.util.Log
import android.view.View
import charer.tmap.toLatLng
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.common.MapBuilder
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.ViewGroupManager
import com.facebook.react.uimanager.annotations.ReactProp
import com.tencent.tencentmap.mapsdk.maps.TencentMap

@Suppress("unused")
internal class TMapMarkerManager : ViewGroupManager<TMapMarker>() {
  override fun getName(): String {
    return "TMapMarker"
  }

  override fun createViewInstance(reactContext: ThemedReactContext): TMapMarker {
    return TMapMarker(reactContext)
  }

  override fun addView(marker: TMapMarker, view: View, index: Int) {
    when (view) {
      is TMapInfoWindow ->  marker.infoWindow = view
      else -> {
        super.addView(marker, view, index)
      }

    }
  }


  override fun getExportedCustomDirectEventTypeConstants(): Map<String, Any>? {
    return MapBuilder.of(
      "onPress", MapBuilder.of("registrationName", "onTMapPress"),
      "onDragStart", MapBuilder.of("registrationName", "onTMapDragStart"),
      "onDrag", MapBuilder.of("registrationName", "onTMapDrag"),
      "onDragEnd", MapBuilder.of("registrationName", "onTMapDragEnd"),
      "onInfoWindowPress", MapBuilder.of("registrationName", "onTMapInfoWindowPress")
    )
  }

  companion object {
    const val UPDATE = 1
    const val ACTIVE = 2
    const val LOCK_TO_SCREEN = 3
  }

  override fun getCommandsMap(): Map<String, Int> {
    return mapOf(
      "update" to UPDATE,
      "active" to ACTIVE,
      "lockToScreen" to LOCK_TO_SCREEN
    )
  }


  override fun receiveCommand(marker: TMapMarker, commandId: Int, args: ReadableArray?) {
    when (commandId) {
      UPDATE -> marker.updateIcon()
      ACTIVE -> marker.active = true
      LOCK_TO_SCREEN -> marker.lockToScreen(args)
    }
  }

  @ReactProp(name = "title")
  fun setTitle(marker: TMapMarker, title: String) {
    marker.title = title
  }

  @ReactProp(name = "description")
  fun setSnippet(marker: TMapMarker, description: String) {
    marker.snippet = description
  }

  @ReactProp(name = "coordinate")
  fun setCoordinate(marker: TMapMarker, coordinate: ReadableMap) {
    Log.d(TAG, "setCoordinate:设置 ")
    marker.position = coordinate.toLatLng()
  }

  @ReactProp(name = "flat")
  fun setFlat(marker: TMapMarker, flat: Boolean) {
    marker.flat = flat
  }

  @ReactProp(name = "opacity")
  override fun setOpacity(marker: TMapMarker, opacity: Float) {
    marker.opacity = opacity
  }

  @ReactProp(name = "draggable")
  fun setDraggable(marker: TMapMarker, draggable: Boolean) {
    marker.draggable = draggable
  }

  @ReactProp(name = "clickDisabled")
  fun setClickDisabled(marker: TMapMarker, disabled: Boolean) {
    marker.clickDisabled = disabled
  }

  @ReactProp(name = "infoWindowDisabled")
  fun setInfoWindowEnable(marker: TMapMarker, disabled: Boolean) {
    marker.infoWindowDisabled = disabled
  }

  @ReactProp(name = "active")
  fun setSelected(marker: TMapMarker, active: Boolean) {
    marker.active = active
  }

  @ReactProp(name = "color")
  fun setIcon(marker: TMapMarker, icon: String) {
    marker.setIconColor(icon)
  }

  @ReactProp(name = "image")
  fun setImage(marker: TMapMarker, image: String) {
    marker.setImage(image)
  }

  @ReactProp(name = "zIndex")
  fun setZIndez(marker: TMapMarker, zIndex: Float) {
    marker.zIndex = zIndex
  }

  @ReactProp(name = "anchor")
  fun setAnchor(view: TMapMarker, coordinate: ReadableMap) {
    view.setAnchor(coordinate.getDouble("x"), coordinate.getDouble("y"))
  }
}
