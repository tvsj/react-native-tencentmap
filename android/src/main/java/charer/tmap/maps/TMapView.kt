package charer.tmap.maps

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.view.ContextMenu
import android.view.View
import charer.tmap.toLatLng
import charer.tmap.toLatLngBounds
import charer.tmap.toWritableMap
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.WritableMap
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.events.RCTEventEmitter
import com.tencent.tencentmap.mapsdk.maps.CameraUpdateFactory
import com.tencent.tencentmap.mapsdk.maps.TencentMap
import com.tencent.tencentmap.mapsdk.maps.TencentMapInitializer
import com.tencent.tencentmap.mapsdk.maps.TextureMapView
import com.tencent.tencentmap.mapsdk.maps.model.CameraPosition
import com.tencent.tencentmap.mapsdk.maps.model.LatLng
import com.tencent.tencentmap.mapsdk.maps.model.LatLngBounds
import com.tencent.tencentmap.mapsdk.maps.model.Marker
import com.tencent.tencentmap.mapsdk.maps.model.MyLocationStyle


class TMapView(context: Context) : TextureMapView(context) {
  private var locationListener: TMapLocationListener;
  private val eventEmitter: RCTEventEmitter = (context as ThemedReactContext).getJSModule(RCTEventEmitter::class.java)

  private val markers = HashMap<String, TMapMarker>()

  //  private val lines = HashMap<String, TMapPolyline>()
  private val locationStyle by lazy {
    val locationStyle = MyLocationStyle()
    locationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW_NO_CENTER)
    locationStyle
  }
  init {
    TencentMapInitializer.setAgreePrivacy(true);
    locationListener = TMapLocationListener(context.applicationContext);
    map.setLocationSource(locationListener)

    map.setOnCameraChangeListener(object : TencentMap.OnCameraChangeListener {

      override fun onCameraChange(position: CameraPosition?) {
        emitCameraChangeEvent("onStatusChange", position)
      }

      override fun onCameraChangeFinished(position: CameraPosition?) {
        Log.d(TAG, "onCameraChangeFinished: 完成了")
        emitCameraChangeEvent("onStatusChangeComplete", position)

      }
    });



    map.setOnMapClickListener { latLng ->
      for (marker in markers.values) {
        marker.active = false
      }
      Log.d(TAG, "地图点击了 ")
      emit(id, "onClick", latLng.toWritableMap())
    }

    map.setOnMapLongClickListener { latLng ->
      emit(id, "onLongClick", latLng.toWritableMap())
    }



    map.setOnMyLocationChangeListener { location ->

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


    map.setOnMarkerClickListener(TencentMap.OnMarkerClickListener { marker ->
      markers[marker.id]?.let {
        it.active = true

        val map1 = Arguments.createMap();
        map1.putString("title", it.title)
        map1.putDouble("latitude", marker.position.latitude)
        map1.putDouble("longitude", marker.position.longitude)
        if (it.tag != null) {
          map1.putString("tag", it.tag.toString())
        }
        Log.d(TAG, "marker点击1111:到这里埌 ")
        emit(id, "onClick", map1)
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


  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    super.onSizeChanged(w, h, oldw, oldh)
    Log.d(TAG, "onSizeChanged: 改变size了"+w.toString()+" "+h.toString())
  }

  override fun onSurfaceChanged(surfaceTexture: Any?, width: Int, height: Int) {
    super.onSurfaceChanged(surfaceTexture, width, height)
    Log.d(TAG, "onSurfaceChanged: 改变surface了")
  }

  override fun hasOnClickListeners(): Boolean {
    Log.d(TAG, "hasOnClickListeners: "+super.hasOnClickListeners())
    return super.hasOnClickListeners()
  }

  override fun setOnDragListener(l: OnDragListener?) {
    super.setOnDragListener(l)

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
      child.add(map);
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

  fun moveCamera(args: ReadableArray?) {

    args?.let {
      if (args.size() > 1) {
        val latLgs = ArrayList<LatLng>();
        for (i in 0 until args.size()) {
          val mk = args.getMap(i);
          mk?.let { latLgs.add(LatLng(mk.getDouble("latitude"), mk.getDouble("longitude"))) };
        }
        val boundsBuilder = LatLngBounds.Builder();
        boundsBuilder.include(latLgs)
        val bounds = boundsBuilder.build()
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 200));
      }

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

  }

  fun setLocationType(type: Int) {
    locationStyle.myLocationType(type)
    map.setMyLocationStyle(locationStyle)

  }


}

