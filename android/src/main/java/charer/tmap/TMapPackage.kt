package charer.tmap

import com.facebook.react.ReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.uimanager.ViewManager
import charer.tmap.maps.*

class TMapPackage : ReactPackage {
  override fun createNativeModules(reactContext: ReactApplicationContext): List<NativeModule> {
    return listOf(
      SdkModule(reactContext,reactContext),
    )
  }


  override fun createViewManagers(reactContext: ReactApplicationContext): List<ViewManager<*, *>> {
    return listOf(
      TMapViewManager(),
      TMapMarkerManager(),
      TMapInfoWindowManager(),
      // PolylineManager(),
//       PolygonManager(),
//       CircleManager(),
      // HeatMapManager(),
      // MultiPointManager()
    )
  }
}
