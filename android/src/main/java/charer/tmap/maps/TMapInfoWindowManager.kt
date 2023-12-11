package charer.tmap.maps

import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.ViewGroupManager

class TMapInfoWindowManager : ViewGroupManager<TMapInfoWindow>() {
    override fun getName(): String {
        return "TMapInfoWindow"
    }

    override fun createViewInstance(reactContext: ThemedReactContext): TMapInfoWindow {
        return TMapInfoWindow(reactContext)
    }
}
