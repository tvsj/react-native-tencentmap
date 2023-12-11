package charer.tmap

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.tencent.tencentmap.mapsdk.maps.TencentMapInitializer

@Suppress("unused")
class SdkModule(val context: ReactApplicationContext, reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {
  override fun getName(): String {
    return "TMapSdk"
  }

//  @ReactMethod
//  fun initSDK() {
//    TencentMapInitializer.setAgreePrivacy(true);
////    apiKey?.let {
////      TencentMapInitializer.setAgreePrivacy(true);
////    }
//  }
}
