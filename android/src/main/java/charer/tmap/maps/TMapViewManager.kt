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
import com.tencent.tencentmap.mapsdk.maps.CameraUpdateFactory
import com.tencent.tencentmap.mapsdk.maps.model.CameraPosition
import com.tencent.tencentmap.mapsdk.maps.model.MyLocationStyle
import java.util.ArrayList


@Suppress("unused")
internal class TMapViewManager : ViewGroupManager<TMapView>() {
  companion object {
    const val SET_STATUS = 1
    const val FIT_BOUNDS = 2
  }

  override fun getName(): String {
    return "TMapView"
  }

  override fun createViewInstance(reactContext: ThemedReactContext): TMapView {
    return TMapView(reactContext)
  }

  override fun onDropViewInstance(view: TMapView) {
    super.onDropViewInstance(view)
    view.onDestroy()
  }

  override fun getCommandsMap(): Map<String, Int> {
    return mapOf("setStatus" to SET_STATUS,"fitBounds" to FIT_BOUNDS)
  }


  override fun receiveCommand(map: TMapView, commandId: Int, args: ReadableArray?) {
    when (commandId) {
      SET_STATUS -> map.animateTo(args)
      FIT_BOUNDS -> map.moveCamera(args)
    }
  }

  override fun addView(mapView: TMapView, child: View, index: Int) {
    // 应用布局参数
    mapView.add(child); // 添加子视图
//    mapView.addView(child, index)

    Log.d(TAG, "addView:添加子视图 ${mapView.childCount} ${index}")
    super.addView(mapView, child,index) // 子视图加入viewmanager管理

  }

  override fun removeViewAt(mapView: TMapView, index: Int) {
    Log.d(TAG, "removeViewAt:移除子视图 ${mapView.childCount} ${index}")
    mapView.remove(mapView.getChildAt(index));
    super.removeViewAt(mapView,index);
  }

  override fun getExportedCustomDirectEventTypeConstants(): Map<String, Any> {
    return MapBuilder.of(
      "onClick", MapBuilder.of("registrationName", "onTMapClick"),
      "onLongClick", MapBuilder.of("registrationName", "onTMapLongClick"),
      "onLocation", MapBuilder.of("registrationName", "onTMapLocation"),
      "onAnimateCancel", MapBuilder.of("registrationName", "onTMapAnimateCancel"),
      "onAnimateFinish", MapBuilder.of("registrationName", "onTMapAnimateFinish"),
      "onStatusChange", MapBuilder.of("registrationName", "onTMapStatusChange"),
      "onStatusChangeComplete", MapBuilder.of("registrationName", "onTMapStatusChangeComplete")
    )
  }

  @ReactProp(name = "locationEnabled")
  fun setMyLocationEnabled(view: TMapView, enabled: Boolean) {
    view.map.setMyLocationEnabled(true)
  }


  @ReactProp(name = "showsBuildings")
  fun showBuildings(view: TMapView, show: Boolean) {
    view.map.showBuilding(show)
  }


  @ReactProp(name = "showsCompass")
  fun setCompassEnabled(view: TMapView, show: Boolean) {
    view.map.uiSettings.isCompassEnabled = show
  }

  @ReactProp(name = "showsZoomControls")
  fun setZoomControlsEnabled(view: TMapView, enabled: Boolean) {
    view.map.uiSettings.isZoomGesturesEnabled = enabled
  }

  @ReactProp(name = "showsScale")
  fun setScaleControlsEnabled(view: TMapView, enabled: Boolean) {
    view.map.uiSettings.isScaleViewEnabled = enabled
  }

  @ReactProp(name = "showsLocationButton")
  fun setMyLocationButtonEnabled(view: TMapView, enabled: Boolean) {
    view.map.uiSettings.isMyLocationButtonEnabled = enabled
  }

  @ReactProp(name = "showsTraffic")
  fun setTrafficEnabled(view: TMapView, enabled: Boolean) {
    view.map.isTrafficEnabled = enabled
  }

  @ReactProp(name = "maxZoomLevel")
  fun setMaxZoomLevel(view: TMapView, zoomLevel: Float) {
    view.map.setMaxZoomLevel(zoomLevel.toInt())
  }

  @ReactProp(name = "minZoomLevel")
  fun setMinZoomLevel(view: TMapView, zoomLevel: Float) {
    view.map.setMinZoomLevel(zoomLevel.toInt())
  }

  @ReactProp(name = "zoomLevel")
  fun setZoomLevel(view: TMapView, zoomLevel: Float) {
    view.map.moveCamera(CameraUpdateFactory.zoomTo(zoomLevel))
  }

  @ReactProp(name = "mapType")
  fun setMapType(view: TMapView, mapType: Int) {
    view.map.mapType = mapType + 1
  }

  @ReactProp(name = "zoomEnabled")
  fun setZoomGesturesEnabled(view: TMapView, enabled: Boolean) {
    view.map.uiSettings.isZoomGesturesEnabled = enabled
  }

  @ReactProp(name = "scrollEnabled")
  fun setScrollGesturesEnabled(view: TMapView, enabled: Boolean) {
    view.map.uiSettings.isScrollGesturesEnabled = enabled
  }

  @ReactProp(name = "rotateEnabled")
  fun setRotateGesturesEnabled(view: TMapView, enabled: Boolean) {
    view.map.uiSettings.isRotateGesturesEnabled = enabled
  }

  @ReactProp(name = "tiltEnabled")
  fun setTiltGesturesEnabled(view: TMapView, enabled: Boolean) {
    view.map.uiSettings.isTiltGesturesEnabled = enabled
  }

  @ReactProp(name = "center")
  fun setCenter(view: TMapView, center: ReadableMap) {
    val cameraSigma = CameraUpdateFactory.newCameraPosition(CameraPosition(
      center.toLatLng(),  //中心点坐标，地图目标经纬度
      view.map.cameraPosition.zoom,  //目标缩放级别
      0f,  //目标倾斜角[0.0 ~ 45.0] (垂直地图时为0)
      0f)) //目标旋转角 0~360° (正北方为0)
    view.map.moveCamera(cameraSigma)
  }

  @ReactProp(name = "region")
  fun setRegion(view: TMapView, region: ReadableMap) {
    view.setRegion(region)
  }


  @ReactProp(name = "rotation")
  fun changeRotation(view: TMapView, rotation: Float) {
    view.map.moveCamera(CameraUpdateFactory.rotateTo(rotation, rotation))
  }


  @ReactProp(name = "locationStyle")
  fun setLocationStyle(view: TMapView, style: ReadableMap) {
    view.setLocationStyle(style)
  }

  @ReactProp(name = "locationType")
  fun setLocationStyle(view: TMapView, type: String) {
    when (type) {
      "location_rotate" -> view.setLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE)
      "location_rotate_no_center" -> view.setLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER)
      "follow_no_center" -> view.setLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW_NO_CENTER)
      "map_rotate_no_center" -> view.setLocationType(MyLocationStyle.LOCATION_TYPE_MAP_ROTATE_NO_CENTER)
    }
  }
}
