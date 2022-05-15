#### 使用说明

## 注意：Android打包需设置：minSdkVersion>=21、targetSdkVersion>=26；文件必须写为".nvue"。

添加插件之后可以只用使用`sintrb-uvcviewer`组件，该组件有以下方法：

```js
let iv = this.$refs.iv;
test(callback); // 测试用，无意义
iv.start(callback); // 开始
iv.stop(callback); // 停止
iv.restart(callback); // 重启摄像头
iv.snap(options, callback); // 截图，options无意义，可传{}
iv.getSupportedPreviewSizes(callback); // 获取当前摄像头支持的预览尺寸
iv.setPreviewSize(options, callback); // 设置当前摄像头的预览尺寸，options参数类似{index:0}
```

该组件有以下属性：

* showControlBar: bool 是否显示控制功能
* rotation: float 旋转角度
* showFps: bool 是否显示FPS
* previewSizeIndex: int 预览尺寸的索引，默认为0（一般是最高像素）
* deviceId: int UVC设备ID，即手机分配给USB设备的ID。

该组件有以下事件@onStatusChange，当状态发送变化是通过该事件进行通知，可通过事件对象event.detail.status得到当前状态，状态值定义如下

```java
private final int STATUS_NONE = 0;  // 初始状态
private final int STATUS_INITING = 1000; // 正在初始化中
private final int STATUS_RETRY = 1100;  // 正在重试中
private final int STATUS_CAM_GETTING = 2000; // 正在获取摄像头
private final int STATUS_CAM_OPENING = 2100; // 正在打开摄像头
private final int STATUS_CAM_INITING = 2200; // 摄像头初始化中
private final int STATUS_CAM_DETACHED = 2300; // 摄像头已移除
private final int STATUS_WAIT_PLAY = 5000; // 摄像头打开成，等待预览
private final int STATUS_PLAYING = 5100; // 正在预览
private final int STATUS_STOPED = 6000; // 预览已停止
private final int STATUS_ERROR = -1; // 操作出错
```


辅助模块`sintrb-uvcmodule`可用于获取当前设备所连接的USB设备，用法如下：

```js
const iuvc = uni.requireNativePlugin("sintrb-uvcmodule");
// 获取UVC设备列表(仅bDeviceClass为239、bDeviceSubclass为2的设备)
iuvc.getUvcDevices({}, res => {
	console.log(JSON.stringify(res))
});

// 获取所有USB设备列表()
iuvc.getUsbDevices({}, res => {
	console.log(JSON.stringify(res))
});
```

具体看例子里面的代码，文件必须写为.nvue。

```vue
<template>
	<view style="display: flex;flex-direction: column; font-size: 12px;">
		<view class="previews" v-if="show">
			<view class="preview-wrap">
				<sintrb-uvcviewer ref="iv" class="preview" :rotation="rotation" @onStatusChange="onStatusChange">
				</sintrb-uvcviewer>
			</view>
		</view>
		<view v-if="previewSizeList.length" style="display: flex; flex-direction: row; flex-wrap: wrap;">
			<view :class="{selected:previewSizeI === i}" @tap="previewSizeI = i"
				style="border: 2rpx solid #eee; padding: 5rpx;" v-for="s,i in previewSizeList">{{s.width}}x{{s.height}}
			</view>
		</view>
		<view class="flex btns">
			<button class="grow1 button" size="mini" type="default" @tap="show = !show">{{show?"关闭":"显示"}}</button>
			<button class="grow1 button" size="mini" type="default" @tap="doIVAction('test')">测试</button>
			<button class="grow1 button" size="mini" type="default" @tap="doIVAction('start')">开始</button>
			<button class="grow1 button" size="mini" type="default" @tap="doIVAction('stop')">停止</button>
			<button class="grow1 button" size="mini" type="default" @tap="getSnap()">截图</button>
			<button class="grow1 button" size="mini" type="default" @tap="rotation = (rotation + 90) % 360">旋转</button>
			<button class="grow1 button" size="mini" type="default" @tap="getSupportedPreviewSizes()">获取支持的尺寸</button>
			<button class="grow1 button" size="mini" type="default" @tap="getUvcDevices()">获取USB设备列表</button>
		</view>

		<scroll-view v-if="images.length" scroll-x="true" style="flex-direction: row;margin-top: 5px;">
			<view style="display: flex;flex-direction: row;">
				<image v-for="it,ix in images" @tap="viewImg(it,ix)" :key="it.key" :src="it.src" mode="heightFix"
					style="max-width: 60px; height: 60px;border: 1px solid red; margin-right: 1px;"></image>
			</view>
		</scroll-view>
		<scroll-view class="logs" scroll-y="true" style="flex-direction: column;margin-top: 5px;">
			<view style="display: flex;flex-direction: column;">
				<view v-for="l in logs"
					style="margin-top: 1rpx; font-size: 8px; width: auto; border: 1rpx solid #EEEEEE; padding: 10rpx;">
					<text>{{l}}</text>
				</view>
			</view>
		</scroll-view>
	</view>
</template>

<script>
	const iuvc = uni.requireNativePlugin("sintrb-uvcmodule")
	export default {
		data() {
			return {
				show: true,
				previewSizeI: -1,
				previewSizeList: [],
				rotation: 0,
				logs: [],
				images: [],
			}
		},
		computed: {

		},
		watch: {
			previewSizeI() {
				// console.log("previewSizeI", this.previewSizeI);
				this.addLog("previewSizeI " + this.previewSizeI);
				// let size = this.previewSizeList[this.previewSizeI];
				this.doIVAction("setPreviewSize", {
					index: this.previewSizeI
				});
			}
		},
		methods: {
			getUvcDevices() {
				iuvc.getUvcDevices({}, res => {
					res.data.devices.map(dev => {
						let ndev = JSON.parse(JSON.stringify(dev));
						dev.showJson = false;
						dev.showPreview = false;
						return dev;
					})

					console.log(JSON.stringify(res))
					this.devices = res.data.devices
					this.addLog(res);
				});
			},
			getSupportedPreviewSizes() {
				this.doIVAction("getSupportedPreviewSizes", null, res => {
					if (res && res.data) {
						this.previewSizeList = res.data.items;
					}
				})
			},
			getSnap() {
				this.doIVAction("snap", {}, res => {
					this.addLog(res);
					if (res && res.data) {
						this.addImg(res.data.path);
					}
				})
			},
			async doIVAction(action, options, cbk) {
				let iv = this.$refs.iv;
				if (!iv) {
					this.res = "没有iv " + Object.keys(this.$refs).join(",")
					return;
				}
				let func = iv[action];
				if (!func) {
					this.addLog("没有iv." + action + " " + Object.keys(iv).join(","));
					return;
				}
				// this.res = 'R ' + action + ' : ' + func;
				let args = [];
				if (options) {
					args.push(options);
				}
				args.push(res => {
					this.addLog(res);
					if (cbk) {
						cbk(res);
					}
				})
				this.res = args;
				try {
					func.apply(iv, args);
				} catch (e) {
					this.addLog("ERR " + e);
				}
			},
			onStatusChange(e) {
				this.addLog(e.detail);
				if (e.detail.status === 5100 && !this.previewSizeList.length) {
					// 预览成功，获取分辨率
					this.getSupportedPreviewSizes()
				}
			},
			addLog(l) {
				if (typeof(l) !== "string") {
					l = JSON.stringify(l);
				}
				this.logs.unshift(l);
			},
			viewImg(it, ix) {
				uni.previewImage({
					urls: this.images.map(r => r.src),
					index: ix,
				})
			},
			addImg(img) {
				this.images.splice(0, 0, {
					src: img,
					key: Date.now(),
				})
				this.addLog(img);
			},
		}
	}
</script>

<style lang="scss">
	.mini-btn {
		padding: 5rpx;
	}

	.btns {
		display: flex;
		flex-direction: row;
		flex-wrap: wrap;
		align-items: center;
	}

	.button {
		// width: 100rpx;
		padding: 3px 5px;
	}

	.previews {
		display: flex;
		flex-direction: row;
		flex-wrap: wrap;
		align-items: center;
		justify-content: center;
		margin-bottom: 10px;
	}

	.preview-wrap {
		background: black;
		margin: 2px;
		min-width: 320px;
		min-height: 240px;
	}

	.preview {
		width: 400px;
		height: 300px;
	}

	.selected {
		background: red;
	}

	.logs {
		// border: 1rpx solid #eee;
		// padding: 5px;
		// margin: 5px;
	}
</style>

```