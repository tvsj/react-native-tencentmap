package charer.tmap.maps

import android.content.ContentValues.TAG
import android.content.Context
import android.location.Location
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import charer.tmap.toLatLng
import charer.tmap.toLatLngBounds
import charer.tmap.toWritableMap
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.WritableMap
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.events.RCTEventEmitter
import com.tencent.map.geolocation.TencentLocation
import com.tencent.map.geolocation.TencentLocationListener
import com.tencent.map.geolocation.TencentLocationManager
import com.tencent.map.geolocation.TencentLocationRequest
import com.tencent.tencentmap.mapsdk.maps.CameraUpdateFactory
import com.tencent.tencentmap.mapsdk.maps.LocationSource
import com.tencent.tencentmap.mapsdk.maps.LocationSource.OnLocationChangedListener
import com.tencent.tencentmap.mapsdk.maps.TencentMap
import com.tencent.tencentmap.mapsdk.maps.TencentMapInitializer
import com.tencent.tencentmap.mapsdk.maps.TextureMapView
import com.tencent.tencentmap.mapsdk.maps.model.CameraPosition
import com.tencent.tencentmap.mapsdk.maps.model.Marker
import com.tencent.tencentmap.mapsdk.maps.model.MyLocationStyle


class TMapView(context: Context) : TextureMapView(context) ,LocationSource, TencentLocationListener {
  private lateinit var locationChangedListener: OnLocationChangedListener;
  private var locationManager: TencentLocationManager = TencentLocationManager(context)
  //用于访问腾讯定位服务的类, 周期性向客户端提供位置更新
  //创建定位请求
  private var locationRequest = TencentLocationRequest.create();
  
  private val eventEmitter: RCTEventEmitter = (context as ThemedReactContext).getJSModule(RCTEventEmitter::class.java)

  private val markers = HashMap<String, TMapMarker>()
//  private val lines = HashMap<String, TMapPolyline>()
  private val locationStyle by lazy {
    val locationStyle = MyLocationStyle()
    locationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW_NO_CENTER)
    locationStyle
  }
  
  private var mContext = context;
  
  init {
    TencentMapInitializer.setAgreePrivacy(true);
    map.setLocationSource(this)
    
    //设置定位周期（位置监听器回调周期）为3s
    locationRequest.interval = 3000;
    
    map.setOnMapClickListener { latLng ->
      for (marker in markers.values) {
        marker.active = false
      }

      emit(id, "onClick", latLng.toWritableMap())
    }

    map.setOnMapLongClickListener { latLng ->
      emit(id, "onLongClick", latLng.toWritableMap())
    }

    map.setOnMyLocationChangeListener { location ->
      Log.d(TAG, "OnMyLocationChangeListener:位置变化监听 ")
      
      val event = Arguments.createMap()
      event.putDouble("latitude", location.latitude)
      event.putDouble("longitude", location.longitude)
      event.putDouble("accuracy", location.accuracy.toDouble())
      event.putDouble("altitude", location.altitude)
      event.putDouble("heading", location.bearing.toDouble())
      event.putDouble("speed", location.speed.toDouble())
      event.putDouble("timestamp", location.time.toDouble())
      emit(id, "onLocation", event)
     

    }

    map.setOnMarkerClickListener(TencentMap.OnMarkerClickListener {  marker ->
      markers[marker.id]?.let {
        it.active = true
      
        val map = Arguments.createMap();
        map.putString("title", it.title)
        map.putDouble("latitude", marker.position.latitude)
        map.putDouble("longitude", marker.position.longitude)
        if(it.tag !=null){
          map.putString("tag", it.tag.toString())
        }
        emit(it.id, "onClick",map)
      }
      true
    })

    map.setOnMarkerDragListener(object : TencentMap.OnMarkerDragListener {
      override fun onMarkerDragStart(marker: Marker) {
        emit(markers[marker.id]?.id, "onDragStart")
      }

      override fun onMarkerDrag(marker: Marker) {
        emit(markers[marker.id]?.id, "onDrag")
      }

      override fun onMarkerDragEnd(marker: Marker) {
        emit(markers[marker.id]?.id, "onDragEnd", marker.position.toWritableMap())
      }
    })
//    map.setOnCameraChangeListener(TencentMap.OnCameraChangeListener{
//      fun onCameraChange(position: CameraPosition?){
//        emitCameraChangeEvent("onStatusChange", position)
//      }
//
//      fun onCameraChangeFinished(position: CameraPosition?){
//        emitCameraChangeEvent("onStatusChangeComplete", position)
//
//      }
//    });


//    map.setOnInfoWindowClickListener { marker ->
//      emit(markers[marker.id]?.id, "onInfoWindowPress")
//    }

//    map.setOnPolylineClickListener { polyline ->
//      emit(lines[polyline.id]?.id, "onPress")
//    }

//    map.setOnMultiPointClickListener { item ->
//      val slice = item.customerId.split("_")
//      val data = Arguments.createMap()
//      data.putInt("index", slice[1].toInt())
//      emit(slice[0].toInt(), "onItemClick", data)
//      false
//    }

    map.setInfoWindowAdapter(TMapInfoWindowAdapter(context, markers))
  }

  override fun activate(onLocationChangedListener: OnLocationChangedListener) {
    //这里我们将地图返回的位置监听保存为当前 Activity 的成员变量
    locationChangedListener = onLocationChangedListener
    //开启定位
    val err = locationManager.requestLocationUpdates(
      locationRequest,this,Looper.myLooper())
    Log.d(TAG, "activate情况: "+err)
    when (err) {
      1 -> Toast.makeText(context,
        "设备缺少使用腾讯定位服务需要的基本条件",
        Toast.LENGTH_SHORT).show()

      2 -> Toast.makeText(context,
        "manifest 中配置的 key 不正确", Toast.LENGTH_SHORT).show()

      3 -> Toast.makeText(context,
        "自动加载libtencentloc.so失败", Toast.LENGTH_SHORT).show()

      else -> {}
    }
  }

  override fun deactivate() {
    //当不需要展示定位点时，需要停止定位并释放相关资源
//    locationManager.removeUpdates(this.context)
//    locationManager = null
    locationRequest = null
//    locationChangedListener = null
  }
  fun emitCameraChangeEvent(event: String, position: CameraPosition?) {
    position?.let {
      val data = Arguments.createMap()
      data.putMap("center", it.target.toWritableMap())
      data.putDouble("zoomLevel", it.zoom.toDouble())
      data.putDouble("tilt", it.tilt.toDouble())
      data.putDouble("rotation", it.bearing.toDouble())
      if (event == "onStatusChangeComplete") {
        data.putMap("region", map.projection.visibleRegion.latLngBounds.toWritableMap())
      }
      emit(id, event, data)
    }
  }

  fun emit(id: Int?, event: String, data: WritableMap = Arguments.createMap()) {
    id?.let {
      eventEmitter.receiveEvent(id, event, data)
    }
  }

  fun add(child: View) {
    if (child is TMapOverlay) {
      child.add(map)
      if (child is TMapMarker) {
        markers[child.marker?.id!!] = child
      }
//      if (child is TMapPolyline) {
//        lines[child.polyline?.id!!] = child
//      }
    }
  }

  fun remove(child: View) {
    if (child is TMapOverlay) {
      child.remove()
      if (child is TMapMarker) {
        markers.remove(child.marker?.id)
      }
//      if (child is TMapPolyline) {
//        lines.remove(child.polyline?.id)
//      }
    }
  }

  private val animateCallback = object : TencentMap.CancelableCallback {
    override fun onCancel() {
      emit(id, "onAnimateCancel")
    }

    override fun onFinish() {
      emit(id, "onAnimateFinish")
    }
  }

  fun animateTo(args: ReadableArray?) {
    val currentCameraPosition = map.cameraPosition
    val status = args?.getMap(0)!!
    val duration = args.getInt(1)

    var center = currentCameraPosition.target
    var zoomLevel = currentCameraPosition.zoom
    var tilt = currentCameraPosition.tilt
    var rotation = currentCameraPosition.bearing

    if (status.hasKey("center")) {
      center = status.getMap("center")!!.toLatLng()
    }

    if (status.hasKey("zoomLevel")) {
      zoomLevel = status.getDouble("zoomLevel").toFloat()
    }

    if (status.hasKey("tilt")) {
      tilt = status.getDouble("tilt").toFloat()
    }

    if (status.hasKey("rotation")) {
      rotation = status.getDouble("rotation").toFloat()
    }

    val cameraUpdate = CameraUpdateFactory.newCameraPosition(
      CameraPosition(center, zoomLevel, tilt, rotation))
    map.animateCamera(cameraUpdate, duration.toLong(), animateCallback)
  }

  fun setRegion(region: ReadableMap) {
    map.moveCamera(CameraUpdateFactory.newLatLngBounds(region.toLatLngBounds(), 0))
  }

  fun setLimitRegion(region: ReadableMap) {
//    map.setMapStatusLimits(region.toLatLngBounds())
  }

  fun setMyLocationEnabled(enabled: Boolean) {
    map.isMyLocationEnabled = enabled
  }



  fun setLocationStyle(style: ReadableMap) {
    if (style.hasKey("fillColor")) {
      locationStyle.fillColor(style.getInt("fillColor"))
    }

    if (style.hasKey("strokeColor")) {
      locationStyle.strokeColor(style.getInt("strokeColor"))
    }

    if (style.hasKey("strokeWidth")) {
      locationStyle.strokeWidth(style.getDouble("strokeWidth").toInt())
    }

//    if (style.hasKey("image")) {
//      val drawable = context.resources.getIdentifier(
//        style.getString("image"), "drawable", context.packageName)
//      locationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(drawable))
//    }
  }

  fun setLocationType(type: Int) {
    locationStyle.myLocationType(type)
    map.setMyLocationStyle(locationStyle)

  }

  override fun onLocationChanged(tencentLocation: TencentLocation, i: Int, s: String?) {
    if (i === TencentLocation.ERROR_OK ) {
      val location: Location = Location(tencentLocation.provider)
      //设置经纬度
      location.latitude = tencentLocation.latitude;
      location.longitude = tencentLocation.longitude
      //设置精度，这个值会被设置为定位点上表示精度的圆形半径
      location.accuracy = tencentLocation.accuracy
      //设置定位标的旋转角度，注意 tencentLocation.getBearing() 只有在 gps 时才有可能获取
//      location.setBearing((float) tencentLocation.getBearing());
      //设置定位标的旋转角度，注意 tencentLocation.getDirection() 返回的方向，仅来自传感器方向，如果是gps，则直接获取gps方向
      location.bearing = tencentLocation.bearing
      //将位置信息返回给地图
      locationChangedListener.onLocationChanged(location)
    }
  }

  override fun onStatusUpdate(p0: String?, p1: Int, p2: String?) {
    Log.d(TAG, "onStatusUpdate: "+p0)
  }


}

