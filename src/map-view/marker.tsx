import * as React from "react";
import * as PropTypes from "prop-types";
import { Platform, requireNativeComponent, StyleSheet, ViewPropTypes, View } from "react-native";
import { LatLng, Point } from "../types";
import { LatLngPropType, PointPropType, mapEventsPropType } from "../prop-types";
import Component from "./component";

const style = StyleSheet.create({
  overlay: {
    position: "absolute",
  },
});

export interface MarkerProps {
  /**
   * 坐标
   */
  coordinate: LatLng;

  /**
   * 标题，作为默认的选中弹出显示
   */
  title?: string;

  /**
   * 描述，显示在标题下方
   */
  description?: string;

  /**
   * 默认图标颜色
   */
  color?: string;

  /**
   * 自定义图标
   */
  icon?: () => React.ReactElement;

  /**
   * 自定义图片，对应原生图片名称
   */
  image?: string;

  /**
   * 透明度 [0, 1]
   */
  opacity?: number;

  /**
   * 是否可拖拽
   */
  draggable?: boolean;

  /**
   * 是否平贴地图
   */
  flat?: boolean;

  /**
   * 层级
   */
  zIndex?: number;


  /**
   * 是否选中，选中时将显示信息窗体，一个地图只能有一个正在选中的 marker
   */
  active?: boolean;

  /**
   * 是否禁用点击，默认不禁用
   */
  clickDisabled?: boolean;

  /**
   * 是否禁用弹出窗口，默认不禁用
   */
  infoWindowDisabled?: boolean;

  /**
   * 自定义 InfoWindow
   */
  children?: React.ReactChild;

  /**
   * 点击事件
   */
  onPress?: () => void;

  /**
   * 拖放开始事件
   */
  onDragStart?: () => void;

  /**
   * 拖放进行事件，类似于 mousemove，在结束之前会不断调用
   */
  onDrag?: () => void;

  /**
   * 拖放结束事件，最终坐标将传入参数
   */
  onDragEnd?: (coordinate: LatLng) => void;

  /**
   * 信息窗体点击事件
   *
   * 注意，对于自定义信息窗体，该事件是无效的
   */
  onInfoWindowPress?: () => void;
}

const events = ["onInfoWindowPress", "onPress", "onDrag", "onDragEnd", "onDragStart"];

/**
 * @ignore
 */
export default class Marker extends Component<MarkerProps> {
  static propTypes = {
    ...ViewPropTypes,
    ...mapEventsPropType(events),
    coordinate: LatLngPropType.isRequired,
    title: PropTypes.string,
    description: PropTypes.string,
    color: Platform.select({
      android: PropTypes.oneOf([
        "azure",
        "blue",
        "cyan",
        "green",
        "magenta",
        "orange",
        "red",
        "rose",
        "violet",
        "yellow",
      ]),
      ios: PropTypes.oneOf(["red", "green", "purple"]),
    }),
    icon: PropTypes.func,
    image: PropTypes.string,
    opacity: PropTypes.number,
    draggable: PropTypes.bool,
    flat: PropTypes.bool,
    zIndex: PropTypes.number,
    anchor: PointPropType,
    centerOffset: PointPropType,
    active: PropTypes.bool,
    clickDisabled: PropTypes.bool,
    infoWindowDisabled: PropTypes.bool,
  };

  nativeComponent = "TMapMarker";
  icon = null;

  componentDidUpdate() {
    if (this.icon && Platform.OS === "android") {
      setTimeout(() => this.call("update"), 0);
    }
  }

  active() {
    this.call("active");
  }

  update() {
    this.call("update");
  }

  lockToScreen(x: number, y: number) {
    this.call("lockToScreen", [x, y]);
  }

  renderCustomMarker(icon: () => React.ReactElement) {
    if (icon) {
      this.icon = <View style={style.overlay}>{icon()}</View>;
      return this.icon;
    }
    return null;
  }

  /* eslint-disable class-methods-use-this */
  renderInfoWindow(view: React.ReactChild) {
    if (view) {
      // @ts-ignore
      return <InfoWindow style={style.overlay}>{view}</InfoWindow>;
    }
    return null;
  }

  render() {
    const props = {
      ...this.props,
      ...this.handlers(events),
    };
    return (
      <TMapMarker {...props}>
        {this.renderCustomMarker(this.props.icon)}
        {this.renderInfoWindow(this.props.children)}
      </TMapMarker>
    );
  }
}

// @ts-ignore
const TMapMarker = requireNativeComponent("TMapMarker", Marker);
// @ts-ignore
const InfoWindow = requireNativeComponent("TMapInfoWindow", { propTypes: { ...ViewPropTypes } });
