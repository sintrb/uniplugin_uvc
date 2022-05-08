package com.sintrb.uniplugin.uvc;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.usb.UsbDevice;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSONObject;
import com.lgh.uvccamera.UVCCameraProxy;
import com.lgh.uvccamera.bean.PicturePath;
import com.lgh.uvccamera.callback.ConnectCallback;
import com.serenegiant.usb.IFrameCallback;
import com.serenegiant.usb.Size;
import com.serenegiant.usb.UVCCamera;
import com.sin.android.usb.USBUtil;
import com.sin.uniplugin.iutils.IUException;
import com.sin.uniplugin.iutils.usb.USBCallback;
import com.sin.uniplugin.iutils.usb.USBWrapper;


import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.dcloud.feature.uniapp.UniSDKInstance;
import io.dcloud.feature.uniapp.annotation.UniJSMethod;
import io.dcloud.feature.uniapp.bridge.UniJSCallback;
import io.dcloud.feature.uniapp.ui.action.AbsComponentData;
import io.dcloud.feature.uniapp.ui.component.AbsVContainer;
import io.dcloud.feature.uniapp.ui.component.UniComponent;
import io.dcloud.feature.uniapp.ui.component.UniComponentProp;

public class IUVCViewer extends UniComponent<View> {
    static USBWrapper usbWrapper;
    static final String TAG = "IUVCViewer";

    SurfaceView surfaceView = null;
    Paint paint = new Paint();
    SurfaceHolder surfaceHolder = null;

    TextView tv_status = null;
    TextView tv_info = null;

    private UVCCameraProxy mUVCCamera;

    private int mDeviceId = -1;
    private long startTm = 0;
    private int frames = 0;

    private final int STATUS_NONE = 0;
    private final int STATUS_INITING = 1000;
    private final int STATUS_CAM_GETTING = 2000;
    private final int STATUS_CAM_OPENING = 2100;
    private final int STATUS_CAM_INITING = 2200;
    private final int STATUS_CAM_DETACHED = 2300;
    private final int STATUS_WAIT_PLAY = 5000;
    private final int STATUS_PLAYING = 5100;
    private final int STATUS_STOPED = 6000;
    private final int STATUS_ERROR = -1;

    private int status = STATUS_NONE;
    private int previewSizeIndex = -1;
    private boolean userStop = false;
    private boolean showFps = true;

    private void setStatus(int status, String detail) {
        if (status != this.status) {
            this.status = status;
            Map<String, Object> data = new HashMap<>();
            data.put("status", status);
            data.put("detail", detail);
            safeFireEvent("onStatusChange", data);
            this.displayText(detail + "(" + status + ")");
            Log.w(TAG, "status=" + status + " detail=" + detail);
        }
    }

    private USBWrapper getUsbWrapper() {
        if (usbWrapper == null) {
            usbWrapper = USBWrapper.getInstance(mUniSDKInstance.getContext());
        }
        return usbWrapper;
    }

    public IUVCViewer(UniSDKInstance instance, AbsVContainer parent, AbsComponentData basicComponentData) {
        super(instance, parent, basicComponentData);
    }


    @Override
    protected View initComponentHostView(Context context) {
//        return initTextureView(context);
        setStatus(STATUS_INITING, "初始化组件");
        RelativeLayout lay = (RelativeLayout) RelativeLayout.inflate(context, R.layout.uvcpreview, null);
        Log.e(TAG, "lay " + lay);
//        RelativeLayout lay = new RelativeLayout(context);
        SurfaceView surfaceView = lay.findViewById(R.id.sv_preview);
        tv_status = lay.findViewById(R.id.tv_status);
        tv_info = lay.findViewById(R.id.tv_info);
        tv_info.setVisibility(showFps ? View.VISIBLE : View.GONE);
        Log.e(TAG, "surfaceView " + surfaceView);
        initSurfaceView(surfaceView);
        return lay;
    }

    @Override
    protected void onFinishLayout() {
        super.onFinishLayout();
        if (tv_info != null)
            tv_info.setTextSize(Math.max(getLayoutWidth() / 60, 10));
        if (tv_status != null)
            tv_status.setTextSize(Math.max(getLayoutWidth() / 50, 10));
    }

    private SurfaceView initSurfaceView(SurfaceView surfaceView) {
        this.surfaceView = surfaceView;
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(2);
        paint.setStyle(Paint.Style.STROKE);
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                Log.w(TAG, "surfaceCreated!!!");
                if (!userStop)
                    initUvcPreview();
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
                Log.w(TAG, "surfaceChanged!!!");
            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
                Log.w(TAG, "surfaceDestroyed!!!");
                closeCamera();
            }
        });
        return surfaceView;
    }

    private void safeFireEvent(String type, Map<String, Object> detail) {
        final Map<String, Object> ret = new HashMap<>();
        ret.put("detail", detail);
        UniSDKInstance instance = getUniInstance();
        if (instance != null) {
            instance.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    fireEvent(type, ret);
                }
            });
        }
    }

    private SurfaceView initSurfaceView(Context context) {
        setStatus(STATUS_INITING, "初始化组件");
        surfaceView = initSurfaceView(new SurfaceView(context));

        return surfaceView;
    }

    @UniComponentProp(name = "deviceId")
    public void setDeviceId(int deviceId) {
        Log.w(TAG, "deviceId: " + deviceId);
        if (deviceId != mDeviceId) {
            this.mDeviceId = deviceId;
            if (this.surfaceHolder != null) {
                initUvcPreview();
            }
        }
    }

    @UniComponentProp(name = "previewSizeIndex")
    public void setPreviewSizeIndex(int index) {
        Log.w(TAG, "setPreviewSizeIndex: " + index);
        if (index != previewSizeIndex) {
            previewSizeIndex = index;
            if (this.status == STATUS_PLAYING)
                this.restart(null);
        }
    }

    @UniComponentProp(name = "showFps")
    public void setShowFps(boolean show) {
        Log.w(TAG, "setShowFps: " + show);
        showFps = show;
        if (tv_info != null) {
            tv_info.setVisibility(showFps ? View.VISIBLE : View.GONE);
        }
    }


    static protected JSONObject getException(Exception e) {
        JSONObject r = new JSONObject();
        r.put("error", e.getMessage());
        if (e instanceof IUException) {
            r.put("code", ((IUException) e).getCode());
        }
        return r;
    }

    static protected JSONObject getResponse(Object d) {
        JSONObject r = new JSONObject();
        r.put("data", d);
        r.put("code", 0);
        return r;
    }

    static protected void handleReturn(Object data, UniJSCallback callback) {
        if (callback == null)
            return;
        if (data instanceof Exception) {
            callback.invoke(getException((Exception) data));
        } else {
            callback.invoke(getResponse(data));
        }
    }

    @UniJSMethod
    public String test() {
        Log.w(TAG, "test");
        return "ok Test";
    }

    @UniJSMethod
    public void start(UniJSCallback callback) {
        Log.w(TAG, "start");
        userStop = false;
        _start(callback);
    }

    public void _start(UniJSCallback callback) {
        Log.w(TAG, "_start");
        if (this.status == STATUS_ERROR || this.status == STATUS_STOPED || this.status == STATUS_NONE)
            this.initUvcPreview();
        handleReturn("ok", callback);
    }

    @UniJSMethod
    public void stop(UniJSCallback callback) {
        Log.w(TAG, "stop");
        userStop = true;
        _stop(callback);
    }

    public void _stop(UniJSCallback callback) {
        Log.w(TAG, "_stop");
        this.closeCamera();
        handleReturn("ok", callback);
    }

    @UniJSMethod
    public void restart(final UniJSCallback callback) {
        Log.w(TAG, "restart");
        new Thread(new Runnable() {
            @Override
            public void run() {
                _stop(null);
                int wait = 30;
                while (status != STATUS_STOPED && wait > 0) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    --wait;
                }
                _start(callback);
            }
        }).start();
    }

    @UniJSMethod
    public void getSupportedPreviewSizes(UniJSCallback callback) {
        // 获取支持的尺寸
        Log.w(TAG, "getSupportedPreviewSizes");
        try {
            JSONObject ret = new JSONObject();
            if (mUVCCamera != null) {
                List<JSONObject> items = new ArrayList<>();
                List<Size> sizes = mUVCCamera.getSupportedPreviewSizes();
                Log.w(TAG, "Sizes: " + sizes);
                if (sizes.size() > 0) {
                    Size size = sizes.get(sizes.size() - 1);
                    mUVCCamera.setPreviewSize(size.width, size.height);
                }
                for (Size size : sizes
                ) {
                    JSONObject sj = new JSONObject();
                    sj.put("width", size.width);
                    sj.put("height", size.height);
                    sj.put("index", size.index);
                    sj.put("type", size.type);
                    sj.put("frame_type", size.frame_type);
                    sj.put("fps", size.fps);
                    items.add(sj);
                }
                ret.put("items", items);
                ret.put("count", items.size());
                Log.w(TAG, "getSupportedPreviewSizes " + ret);
                handleReturn(ret, callback);
            } else {
                throw new RuntimeException("UVC设备未初始化完成");
            }
        } catch (Exception e) {
            handleReturn(e, callback);
        }
    }

    @UniJSMethod
    public void setPreviewSize(JSONObject options, UniJSCallback callback) {
        Log.w(TAG, "setPreviewSize: " + options);
        try {
            if (mUVCCamera != null) {
                JSONObject ret = new JSONObject();
                int index = options.containsKey("index") ? options.getIntValue("index") : -1;
                if (index != previewSizeIndex) {
                    previewSizeIndex = index;
                }
                restart(callback);
            } else {
                throw new RuntimeException("UVC设备未初始化完成");
            }
        } catch (Exception e) {
            handleReturn(e, callback);
        }
    }

    private boolean pureColoseCamera() {
        if (mUVCCamera != null) {
            Log.w(TAG, "closeCamera mUVCCamera=" + mUVCCamera);
            mUVCCamera.closeCamera();
            mUVCCamera = null;
            return true;
        }
        return false;
    }

    public void closeCamera() {
        if (pureColoseCamera()) {
            setStatus(STATUS_STOPED, "摄像头已关闭");
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        closeCamera();
        Log.w(TAG, "destroy");
    }

    public boolean isUsbCamera(UsbDevice usbDevice) {
        return IUVCModule.isUsbCamera(usbDevice);
    }

    private void initUvcPreview() {
        // 初始化摄像头预览
        pureColoseCamera();
        surfaceHolder = surfaceView.getHolder();
        setStatus(STATUS_CAM_GETTING, "获取摄像头...");
        USBCallback openCallback = new USBCallback() {
            @Override
            public void deviceCallback(final USBWrapper.Device device) {
                Log.i(TAG, " openCallback " + device);
                if (device == null) {
                    setStatus(STATUS_ERROR, "摄像头不存在或无权访问!!!");
                    return;
                }
                setStatus(STATUS_CAM_OPENING, "正在打开摄像头" + device.getUsbDevice().getDeviceId() + "...");
                new Thread(() -> {
                    setStatus(STATUS_CAM_INITING, "摄像头初始化" + device.getUsbDevice().getDeviceId() + "...");
                    Log.w(TAG, "Start...");
                    if (mUVCCamera != null) {
                        mUVCCamera.closeDevice();
                        mUVCCamera = null;
                    }
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    mUVCCamera = new UVCCameraProxy(getContext());
                    mUVCCamera.getConfig().isDebug(true)
                            .setPicturePath(PicturePath.APPCACHE)
                            .setDirName("uvccamera");
//
                    UsbDevice usb = device.getUsbDevice();
                    if (device.isOpened)
                        device.close();
                    mUVCCamera.setConnectCallback(new ConnectCallback() {
                        @Override
                        public void onAttached(UsbDevice usbDevice) {
                            Log.w(TAG, "onAttached");
                            mUVCCamera.requestPermission(usbDevice);
                        }

                        @Override
                        public void onGranted(UsbDevice usbDevice, boolean granted) {
                            Log.w(TAG, "onGranted");
                            if (granted) {
                                mUVCCamera.connectDevice(usbDevice);
                            }
                        }

                        @Override
                        public void onConnected(UsbDevice usbDevice) {
                            Log.w(TAG, "onConnected");
                            try {
                                mUVCCamera.openCamera();
                            } catch (Exception e) {
                                e.printStackTrace();
                                pureColoseCamera();
                                setStatus(STATUS_ERROR, "打开摄像头失败:" + e.getMessage());
                            }
                        }

                        @Override
                        public void onCameraOpened() {
                            Log.w(TAG, "onCameraOpened");
                            if (!mUVCCamera.isCameraOpen()) {
                                setStatus(STATUS_ERROR, "打开摄像头" + mDeviceId + "失败!");
                                return;
                            }
                            List<Size> sizes = mUVCCamera.getSupportedPreviewSizes();
                            Log.w(TAG, "Sizes: " + sizes);
                            if (sizes.size() > 0) {
                                if (previewSizeIndex < 0 || previewSizeIndex >= sizes.size()) {
                                    previewSizeIndex = sizes.size() - 1;
                                }
                                Size size = sizes.get(previewSizeIndex);
                                mUVCCamera.setPreviewSize(size.width, size.height);
                            }
                            setStatus(STATUS_WAIT_PLAY, "准备预览...");

                            try {
                                mUVCCamera.getUVCCamera().setPreviewDisplay(surfaceHolder);
                                mUVCCamera.startPreview();
                            } catch (Exception e) {
                                setStatus(STATUS_ERROR, "预览失败:" + e.getMessage());
                            }

                            mUVCCamera.getUVCCamera().setFrameCallback(new IFrameCallback() {
                                @Override
                                public void onFrame(ByteBuffer frame) {
                                    if (status != STATUS_PLAYING) {
                                        setStatus(STATUS_PLAYING, "预览成功!!!");
                                    }
//                                    int lenght = frame.capacity();
//                                    byte[] yuv = new byte[lenght];
//                                    frame.get(yuv);
//                                    if (mPreviewCallback != null) {
//                                        mPreviewCallback.onPreviewFrame(yuv);
//                                    }
//                                    if (isTakePhoto) {
//                                        LogUtil.i("take picture");
//                                        isTakePhoto = false;
//                                        savePicture(yuv, PICTURE_WIDTH, PICTURE_HEIGHT, mPreviewRotation);
//                                    }
//                                    Log.i(TAG, "onFrame");
                                    long now = System.currentTimeMillis();
                                    if (startTm == 0) {
                                        startTm = now;
                                        frames = 0;
                                    } else {
                                        ++frames;
                                        if ((now - startTm) > 3000) {
                                            int fps = (int) ((frames * 1000.0) / (now - startTm));
                                            String info = "" + fps + "fps";
                                            displayInfo(info);
                                            Log.i(TAG, info);
                                            startTm = now;
                                            frames = 0;
                                        }
                                    }
                                }
                            }, UVCCamera.PIXEL_FORMAT_YUV420SP);
                        }

                        @Override
                        public void onDetached(UsbDevice usbDevice) {
                            mUVCCamera.closeCamera();
                            setStatus(STATUS_CAM_DETACHED, "摄像头已移除!!!");
                            Log.w(TAG, "onDetached");
                            _stop(null);
                        }
                    });

//                    mUVCCamera.setPreviewSurface(surfaceView);
                    mUVCCamera.connectDevice(usb);
                    Log.w(TAG, "END!!!!");
                }).start();
                Log.w(TAG, "GoGo!!!!");
            }
        };
        List<UsbDevice> usbs = USBUtil.getUsbDevices(getContext(), -1, -1);
        int deviceId = mDeviceId;
        for (int i = 0; i < usbs.size(); ++i) {
            UsbDevice dev = usbs.get(i);
            if (mDeviceId == -1) {
                // 不限制
                if (isUsbCamera(dev)) {
                    // 找到
                    deviceId = dev.getDeviceId();
                    break;
                }
            } else if (mDeviceId == dev.getDeviceId()) {
                // 命中
                deviceId = dev.getDeviceId();
                break;
            }
        }
        try {
            if (deviceId >= 0) {
                getUsbWrapper().openDevice(deviceId, openCallback);
            } else {
                throw new IUException("不存在设备" + deviceId, 1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
            setStatus(STATUS_ERROR, "打开失败:" + e.getMessage());
        }
    }

    private void displayText(final String text) {
        UniSDKInstance instance = getUniInstance();
        if (instance == null || tv_status == null) {
            return;
        }
        instance.runOnUiThread(() -> {
            if (status == STATUS_PLAYING) {
                tv_status.setVisibility(View.GONE);
                Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
            } else {
                tv_status.setVisibility(View.VISIBLE);
                tv_status.setText(text);
            }
        });
    }

    private void displayInfo(String info) {
        UniSDKInstance instance = getUniInstance();
        if (instance == null || tv_info == null) {
            return;
        }
        instance.runOnUiThread(() -> {
            tv_info.setVisibility(View.VISIBLE);
            tv_info.setText(info);
        });
    }
}

