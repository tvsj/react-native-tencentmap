package charer.tmap

import android.content.res.Resources
import android.graphics.Bitmap
import android.view.View
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.common.ResizeOptions
import com.facebook.imagepipeline.request.BasePostprocessor
import com.facebook.imagepipeline.request.ImageRequestBuilder

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.WritableMap
import com.facebook.react.views.imagehelper.ImageSource
import com.tencent.tencentmap.mapsdk.maps.model.BitmapDescriptor
import com.tencent.tencentmap.mapsdk.maps.model.BitmapDescriptorFactory
import com.tencent.tencentmap.mapsdk.maps.model.LatLng
import com.tencent.tencentmap.mapsdk.maps.model.LatLngBounds
import kotlin.math.abs

fun Float.toPx(): Int {
  return (this * Resources.getSystem().displayMetrics.density).toInt()
}

fun ReadableMap.toLatLng(): LatLng {
  return LatLng(getDouble("latitude"), getDouble("longitude"))
}

fun ReadableArray.toLatLngList(): ArrayList<LatLng> {
  return ArrayList((0 until size()).map { getMap(it)!!.toLatLng() })
}

fun LatLng.toWritableMap(): WritableMap {
  val map = Arguments.createMap()
  map.putDouble("latitude", latitude)
  map.putDouble("longitude", longitude)
  return map
}

fun LatLngBounds.toWritableMap(): WritableMap {
  val map = Arguments.createMap()
  map.putDouble("latitude", abs((southwest.latitude + northeast.latitude) / 2))
  map.putDouble("longitude", abs((southwest.longitude + northeast.longitude) / 2))
  map.putDouble("latitudeDelta", abs(southwest.latitude - northeast.latitude))
  map.putDouble("longitudeDelta", abs(southwest.longitude - northeast.longitude))
  return map
}

fun ReadableMap.toLatLngBounds(): LatLngBounds {
  val latitude = getDouble("latitude")
  val longitude = getDouble("longitude")
  val latitudeDelta = getDouble("latitudeDelta")
  val longitudeDelta = getDouble("longitudeDelta")
  return LatLngBounds(
    LatLng(latitude - latitudeDelta / 2, longitude - longitudeDelta / 2),
    LatLng(latitude + latitudeDelta / 2, longitude + longitudeDelta / 2)
  )
}
fun View.fetchImage(source: ReadableMap, callback: BitmapDescriptor.() -> Unit) {
  val uri = ImageSource(context, source.getString("uri")).uri
  val request = ImageRequestBuilder.newBuilderWithSource(uri).let {
    it.postprocessor = object : BasePostprocessor() {
      override fun process(bitmap: Bitmap) {
        BitmapDescriptorFactory.fromBitmap(bitmap).callback()
      }
    }
    if (source.hasKey("width") && source.hasKey("height")) {
      it.resizeOptions = ResizeOptions.forDimensions(
        source.getInt("width").toInt(),
        source.getInt("height").toInt()
      )
    }
    it.build()
  }
  Fresco.getImagePipeline().fetchDecodedImage(request, this)
}
