package com.sintrb.uniplugin.uvc;

import android.hardware.usb.UsbDevice;
import android.util.Log;

import com.lgh.uvccamera.usb.UsbController;
import com.serenegiant.usb.IFrameCallback;
import com.serenegiant.usb.UVCCamera;
import com.sin.uniplugin.iutils.IUException;

import com.alibaba.fastjson.JSONObject;
import com.sin.uniplugin.iutils.IUtilsModule;
import com.sin.uniplugin.iutils.usb.USBCallback;
import com.sin.uniplugin.iutils.usb.USBWrapper;

import java.nio.ByteBuffer;

import io.dcloud.feature.uniapp.annotation.UniJSMethod;
import io.dcloud.feature.uniapp.bridge.UniJSCallback;
import io.dcloud.feature.uniapp.common.UniModule;

public class IUVCModule extends IUtilsModule {
    final String TAG = "sintrb-IUVCModule";
    public static int REQUEST_CODE = 2000;

//    static USBWrapper usbWrapper;
//
//    private USBWrapper getUsbWrapper() {
//        if (usbWrapper == null) {
//            usbWrapper = USBWrapper.getInstance(mUniSDKInstance.getContext());
//        }
//        return usbWrapper;
//    }
//
//    private JSONObject getResponse(Exception e) {
//        JSONObject r = new JSONObject();
//        r.put("error", e.getMessage());
//        if (e instanceof IUException) {
//            r.put("code", ((IUException) e).getCode());
//        }
//        return r;
//    }
//
//    private JSONObject getResponse(JSONObject d) {
//        JSONObject r = new JSONObject();
//        r.put("data", d);
//        r.put("code", 0);
//        return r;
//    }

    //run ui thread
//    @UniJSMethod(uiThread = false)
//    public JSONObject test() {
//        Log.e(TAG, "test--");
//        JSONObject data = new JSONObject();
//        data.put("code", "successX");
//        data.put("tag", TAG);
//        data.put("time", System.currentTimeMillis());
//        return data;
//    }


    static public boolean isUsbCamera(UsbDevice usbDevice) {
        return usbDevice != null && 239 == usbDevice.getDeviceClass() && 2 == usbDevice.getDeviceSubclass();
    }

    @UniJSMethod(uiThread = false)
    public void getUvcDevices(JSONObject options, final UniJSCallback callback) {
        /**
         * 获取UVC设备列表
         */
        Log.e(TAG, "getUvcDevices:" + options);
        JSONObject res = getUsbWrapper().getUsbDevices(new USBWrapper.UsbDeviceChecker() {
            @Override
            public boolean check(UsbDevice dev) {
                return isUsbCamera(dev);
            }
        });
        if (callback != null) {
            callback.invoke(getResponse(res));
        }
    }


    @UniJSMethod(uiThread = false)
    public void getSnapshot(final JSONObject options, final UniJSCallback callback) {
        /**
         * 获取USB截图
         * options = {
         *     deviceId: 00, // or
         *     vendorId: 00,
         *     productId: 00,
         *     timeout: 2000,
         * }
         */
        Log.e(TAG, "getSnapshot:" + options);
        try {
            new Thread() {
                @Override
                public void run() {
                    final int timeout = options.get("timeout") == null ? 5000 : options.getInteger("timeout");
                    USBCallback openCallback = new USBCallback() {
                        @Override
                        public void deviceCallback(final USBWrapper.Device device) {
                            Log.i(TAG, " openCallback " + device);
                            if (device == null)
                                return;
                            device.close();
                            UsbController uc = new UsbController(getUsbWrapper().getUsbManager(), device.getUsbDevice());
                            UVCCamera camera = new UVCCamera();
                            camera.open(uc);
                            camera.setPreviewSize(UVCCamera.DEFAULT_PREVIEW_WIDTH, UVCCamera.DEFAULT_PREVIEW_HEIGHT, UVCCamera.DEFAULT_PREVIEW_MODE);
                            IFrameCallback cbk = new IFrameCallback() {
                                @Override
                                public void onFrame(ByteBuffer frame) {
                                    frame.clear();
//                                    Log.w(TAG, "onFrame");
                                }
                            };
                            Log.w(TAG, "GoGo!!!!");
                            if (device != null && device.isOpened) {
                                // 打开成功，开始发送
                                JSONObject res = new JSONObject();
                                res.put("name", "hello");
                                if (callback != null) {
                                    callback.invoke(res);
                                }
                            } else if (callback != null) {
                                callback.invoke(getResponse(new IUException("打开设备失败", USBWrapper.CODE_DEVICE_OPEN_FAIL)));
                            }
                        }
                    };
                    try {
                        if (options.containsKey("deviceId") && options.getInteger("deviceId") != null) {
                            getUsbWrapper().openDevice(options.getInteger("deviceId"), openCallback);
                        } else {
                            getUsbWrapper().openDevice(options.get("vendorId") == null ? 0 : options.getInteger("vendorId"), options.get("productId") == null ? 0 : options.getInteger("productId"), openCallback);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (callback != null) {
                            callback.invoke(getResponse(e));
                        }
                    }
                }
            }.start();
        } catch (Exception e) {
            e.printStackTrace();
            if (callback != null) {
                callback.invoke(getResponse(e));
            }
        }
    }
}
