# @charer/react-native-tencentmap

腾讯地图react-native组件，使用最新 3D SDK，精力有限，仅支持 Android，参考 [react-native-amap3d](https://github.com/qiuxiang/react-native-amap3d) ,提供功能丰富且易用的接口

## Installation

```sh
npm install @charer/react-native-tencentmap
```

## Usage

```js
import { MapView } from '@charer/react-native-tencentmap';

// ...

<MapView style={{ flex: 1 }}
        zoomEnabled={true}
        zoomControlEnabled={true}
        showsUserLocation={true}
        showsCompass={true}
        showsScale={true}
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

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT
