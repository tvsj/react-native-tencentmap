package charer.tmap.maps

import android.content.ContentValues.TAG
import android.content.Context
import android.content.ContextWrapper
import android.location.Location
import android.util.Log
import android.widget.Toast
import com.tencent.map.geolocation.TencentLocation
import com.tencent.map.geolocation.TencentLocationListener
import com.tencent.map.geolocation.TencentLocationManager
import com.tencent.map.geolocation.TencentLocationRequest
import com.tencent.tencentmap.mapsdk.maps.LocationSource


class TMapLocationListener(reactContext: Context) : ContextWrapper(reactContext), LocationSource, TencentLocationListener {

  private var locationChangedListener: LocationSource.OnLocationChangedListener? =null;
  private var locationManager: TencentLocationManager = TencentLocationManager(reactContext.applicationContext)

  //用于访问腾讯定位服务的类, 周期性向客户端提供位置更新
  //创建定位请求
  private var locationRequest = TencentLocationRequest.create().setInterval(5000) // Update interval in milliseconds
    .setRequestLevel(TencentLocationRequest.REQUEST_LEVEL_GEO) // Use GPS
    .setAllowGPS(true)
    .setAllowCache(true)
    .setAllowDirection(true);


  override fun onLocationChanged(tencentLocation: TencentLocation, i: Int, s: String?) {
    if (i === TencentLocation.ERROR_OK && locationChangedListener != null) {
      // Log.d(TAG, "onLocationChanged: 地图改变了")
      val location = Location(tencentLocation.provider)
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
      locationChangedListener!!.onLocationChanged(location)
    }
  }

  override fun onStatusUpdate(s: String?, i: Int, s1: String?) {
    // Handle status updates if needed
//    Log.d(TAG, "onStatusUpdate:" + s + " " + i.toString() + "  " + s1)
  }

  override fun activate(listener: LocationSource.OnLocationChangedListener) {
    Log.d(TAG, "activate:地图 ")

    //这里我们将地图返回的位置监听保存为当前 Activity 的成员变量
    locationChangedListener = listener
    //开启定位
    val err = locationManager.requestLocationUpdates(
      this.locationRequest, this)
    when (err) {
      1 -> Toast.makeText(this,
        "设备缺少使用腾讯定位服务需要的基本条件",
        Toast.LENGTH_SHORT).show()

      2 -> Toast.makeText(this,
        "manifest 中配置的 key 不正确", Toast.LENGTH_SHORT).show()

      3 -> Toast.makeText(this,
        "自动加载libtencentloc.so失败", Toast.LENGTH_SHORT).show()

      else -> {}
    }
  }

  override fun deactivate() {
    //当不需要展示定位点时，需要停止定位并释放相关资源
    locationManager.removeUpdates(this)
    locationManager.removeLocationListener(this)
    this.locationRequest = null;
    locationChangedListener = null;
    Log.d(TAG, "deactivate: 释放资源")

  }
}

