# @charer/react-native-tencentmap

腾讯地图react-native组件，使用最新 3D SDK，精力有限，仅支持 Android，参考 [react-native-amap3d](https://github.com/qiuxiang/react-native-amap3d) ,提供功能丰富且易用的接口

## Installation

```sh
npm install @charer/react-native-tencentmap
```

## Usage
修改项目Androidmanifest.xml 加入腾讯地图key
<!--设置腾讯地图-->
        <meta-data android:name="TencentMapSDK" android:value="5P5BZ-WAXWP-IKVDR-VLADM-4ZFCZ-W2FNM"/>
      <!--设置腾讯地图 end-->
<!--设置腾讯地图-->
```js
import { MapView } from '@charer/react-native-tencentmap';

<MapView style={{ flex: 1 }}
        zoomEnabled={true}
        zoomControlEnabled={true}
        myLocationEnable={true}
        locationEnabled={true}
        locationType={'location_rotate_no_center'}
        showsLocationButton={true}
        showsCompass={true}
        onLocation={(t)=>console.log('onLocationChanged',t)}
        onClick={(nativeEvent) => console.log(nativeEvent)}
        onPress={(nativeEvent) => console.log('press',nativeEvent)}
        center={{
          latitude: 22.829168,
          longitude: 108.355375
        }}
      >
        <MapView.Marker
          title="自定View"
          icon={() => (
            <View style={styles.customMarker}>
              <Text style={styles.markerText}>我爱你</Text>
            </View>
          )}
          coordinate={{
            latitude: 22.809168,
            longitude: 108.355375
          }}
        />
      </MapView>
```
## 功能
- 地图显示、点击事件
- 显示定位
- marker点、自定义、点击事件、点击显示infowindow

## 未来
-- Polyline
-- Polygon

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT
