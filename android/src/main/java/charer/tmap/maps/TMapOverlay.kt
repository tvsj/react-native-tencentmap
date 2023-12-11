package charer.tmap.maps


import com.tencent.tencentmap.mapsdk.maps.TencentMap
interface TMapOverlay {
  fun add(map: TencentMap)
  fun remove()
}
