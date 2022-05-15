package com.sintrb.uniplugin.uvc;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.usb.UsbDevice;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSONObject;
import com.lgh.uvccamera.UVCCameraProxy;
import com.lgh.uvccamera.bean.PicturePath;
import com.lgh.uvccamera.callback.ConnectCallback;
import com.lgh.uvccamera.utils.FileUtil;
import com.serenegiant.usb.IFrameCallback;
import com.serenegiant.usb.Size;
import com.serenegiant.usb.UVCCamera;
import com.sin.android.usb.USBUtil;
import com.sin.uniplugin.iutils.IUException;
import com.sin.uniplugin.iutils.usb.USBCallback;
import com.sin.uniplugin.iutils.usb.USBWrapper;


import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.dcloud.feature.uniapp.UniSDKInstance;
import io.dcloud.feature.uniapp.annotation.UniJSMethod;
import io.dcloud.feature.uniapp.bridge.UniJSCallback;
import io.dcloud.feature.uniapp.ui.action.AbsComponentData;
import io.dcloud.feature.uniapp.ui.component.AbsVContainer;
import io.dcloud.feature.uniapp.ui.component.UniComponent;
import io.dcloud.feature.uniapp.ui.component.UniComponentProp;

public class IUVCViewer extends UniComponent<View> {
    static USBWrapper usbWrapper;
    static final String TAG = "sintrb-IUVCViewer";

    ViewGroup rootLayout = null;


    SurfaceView surfaceView = null;
    SurfaceHolder surfaceHolder = null;

    TextureView textureView = null;
    SurfaceTexture surfaceTexture = null;

    TextView tv_status = null;
    TextView tv_info = null;

    private UVCCameraProxy mUVCCamera;

    private int mDeviceId = -1;
    private int curDeviceId = -1;
    private long startTm = 0;
    private int frames = 0;

    private final int STATUS_NONE = 0;
    private final int STATUS_INITING = 1000;
    private final int STATUS_RETRY = 1100;
    private final int STATUS_CAM_GETTING = 2000;
    private final int STATUS_CAM_OPENING = 2100;
    private final int STATUS_CAM_INITING = 2200;
    private final int STATUS_CAM_DETACHED = 2300;
    private final int STATUS_WAIT_PLAY = 5000;
    private final int STATUS_PLAYING = 5100;
    private final int STATUS_STOPED = 6000;
    private final int STATUS_ERROR = -1;

    private int status = STATUS_NONE;
    private int previewSizeIndex = 0;
    private boolean userStop = false;
    private boolean bShowFps = true;
    private boolean bShowControlBar = true;

    private String snapName = null;
    private float rotation = 0;
    private JSONObject snapRet = null;

    private Size size = new Size(0, 0, 0, 0, 0);

    private void setStatus(int status, String detail) {
        if (status != this.status) {
            this.status = status;
            Map<String, Object> data = new HashMap<>();
            data.put("status", status);
            data.put("detail", detail);
            if (status == STATUS_PLAYING) {
                data.put("deviceId", curDeviceId);
                data.put("previewSizeIndex", previewSizeIndex);
            }
            safeFireEvent("onStatusChange", data);
            this.displayText(detail + "(" + status + ")");
            if (this.tv_info != null) {
                if (status != STATUS_PLAYING && this.tv_info.getText().length() > 0) {
                    this.displayInfo("");
                }
                if (status == STATUS_PLAYING) {
                    startTm = 0;
                    frames = 0;
                }
            }
            Log.w(TAG, "status=" + status + " detail=" + detail);
            if (this.rootLayout != null) {
                runOnUiThread(() -> {
                    rootLayout.findViewById(R.id.btn_stop).setVisibility(status == STATUS_PLAYING ? View.VISIBLE : View.GONE);
                    rootLayout.findViewById(R.id.btn_play).setVisibility(status == STATUS_STOPED || status == STATUS_ERROR ? View.VISIBLE : View.GONE);
                    rootLayout.findViewById(R.id.sw_showfps).setVisibility(status == STATUS_PLAYING ? View.VISIBLE : View.GONE);
                    if (status == STATUS_PLAYING) {
                        // 延迟隐藏控制条
                        new Handler().postDelayed(() -> runOnUiThread(() -> {
                            if (IUVCViewer.this.status == STATUS_PLAYING) {
                                hideControlBar();
                            }
                        }), 2000);
                    }
                    updatePreviewSizes();
                });

            }
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
//        iconfont 图标  https://www.iconfont.cn/collections/detail?spm=a313x.7781069.1998910419.dc64b3430&cid=11607
        rootLayout = (RelativeLayout) RelativeLayout.inflate(context, R.layout.uvcpreview, null);
        setStatus(STATUS_INITING, "初始化组件");
        Log.i(TAG, "rootlayout " + rootLayout);
//        RelativeLayout rootlayout = new RelativeLayout(context);
        tv_status = rootLayout.findViewById(R.id.tv_status);
        tv_info = rootLayout.findViewById(R.id.tv_info);
        tv_info.setVisibility(bShowFps ? View.VISIBLE : View.GONE);
        tv_status.setText("");
        tv_info.setText("");
//        SurfaceView surfaceView = rootlayout.findViewById(R.id.sv_preview);
//        initSurfaceView(surfaceView);

        TextureView textureView = rootLayout.findViewById(R.id.tv_preview);
        initTextureView(textureView);

//        tv_status.setOnClickListener(v -> {
//            if (status == STATUS_ERROR) {
//                setStatus(STATUS_RETRY, "正在尝试打开...");
//                restart(null);
//            }
//        });

        rootLayout.findViewById(R.id.btn_play).setOnClickListener(v -> {
            this.start(null);
        });

        rootLayout.findViewById(R.id.btn_stop).setOnClickListener(v -> {
            this.stop(null);
        });

        rootLayout.findViewById(R.id.ll_control_bar).setVisibility(bShowControlBar ? View.VISIBLE : View.GONE);

        ((Spinner) rootLayout.findViewById(R.id.sp_sizes)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != previewSizeIndex)
                    setPreviewSizeIndex(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ((Switch) (rootLayout.findViewById(R.id.sw_showfps))).setChecked(bShowFps);
        ((Switch) (rootLayout.findViewById(R.id.sw_showfps))).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setShowFps(isChecked);
            }
        });
        textureView.setOnClickListener(v -> {
            if (this.bShowControlBar) {
                toggleControlBar();
            }
        });
        ((Spinner) rootLayout.findViewById(R.id.sp_devices)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position < deviceIds.size()) {
                    int ci = deviceIds.get(position);
                    if (ci != mDeviceId) {
                        setDeviceId(ci);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        updateUsbDevices();
        return rootLayout;
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
        Log.i(TAG, "initSurfaceView " + surfaceView);
        this.surfaceView = surfaceView;
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

    private TextureView initTextureView(TextureView textureView) {
        Log.i(TAG, "initTextureView " + textureView);
        this.textureView = textureView;
        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
                Log.w(TAG, "onSurfaceTextureAvailable!!!");
                if (!userStop)
                    initUvcPreview();
            }

            @Override
            public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {
                Log.w(TAG, "surfaceChanged!!!");
            }

            @Override
            public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
                Log.w(TAG, "onSurfaceTextureDestroyed!!!");
                closeCamera();
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {
//                Log.w(TAG, "onSurfaceTextureUpdated!!!");
            }
        });
        return textureView;
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

//    private SurfaceView initSurfaceView(Context context) {
//        setStatus(STATUS_INITING, "初始化组件");
//        surfaceView = initSurfaceView(new SurfaceView(context));
//        return surfaceView;
//    }

    @UniComponentProp(name = "deviceId")
    public void setDeviceId(int deviceId) {
        Log.w(TAG, "deviceId: " + deviceId);
        if (deviceId != mDeviceId) {
            this.mDeviceId = deviceId;
            if (mUVCCamera != null) {
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
        bShowFps = show;
        if (tv_info != null) {
            tv_info.setVisibility(bShowFps ? View.VISIBLE : View.GONE);
        }
        Switch sb = rootLayout != null ? (Switch) (rootLayout.findViewById(R.id.sw_showfps)) : null;
        if (sb != null && sb.isChecked() != show) {
            sb.setChecked(show);
        }
    }

    @UniComponentProp(name = "rotation")
    public void setRotation(float rotation) {
        Log.w(TAG, "setRotation: " + rotation);
        this.rotation = rotation;
        if (surfaceView != null) {
            surfaceView.setRotation(rotation);
        } else {
            textureView.setRotation(rotation);
        }
    }

    @UniComponentProp(name = "showControlBar")
    public void setShowControlBar(boolean show) {
        Log.w(TAG, "setShowControlBar: " + show);
        bShowControlBar = show;
        if (bShowControlBar) {
            showControlBar();
        } else {
            hideControlBar();
        }
    }

    static protected JSONObject getException(Exception e) {
        JSONObject r = new JSONObject();
        r.put("error", e.getMessage());
        r.put("code", -1);
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
        if (this.status == STATUS_ERROR || this.status == STATUS_STOPED || this.status == STATUS_NONE || this.status == STATUS_RETRY)
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

                if (textureView != null) {
                    runOnUiThread(() -> {
                        textureView.setVisibility(View.GONE);
                    });
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    userStop = false;
                    runOnUiThread(() -> {
                        textureView.setVisibility(View.VISIBLE);
                    });
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    _start(callback);
                } else {
                    _start(callback);
                }
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
        /**
         * 设置预览尺寸options={"index": 1}
         */
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

    @UniJSMethod
    public void snap(JSONObject options, final UniJSCallback callback) {
        /**
         * 截图
         */
        Log.w(TAG, "snap");
        if (status != STATUS_PLAYING) {
            handleReturn(new RuntimeException("设备未打开无法截图"), callback);
            return;
        }

        this.snapRet = null;
        this.snapName = options != null && !TextUtils.isEmpty(options.getString("name")) ? options.getString("name") : UUID.randomUUID().toString() + ".jpg";
        new Thread(() -> {
            try {
                int maxWait = 10 * 1000 / 10;
                while (snapRet == null && maxWait > 0) {
                    --maxWait;
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (snapRet != null) {
                    handleReturn(snapRet, callback);
                    snapRet = null;
                } else {
                    throw new RuntimeException("截图超时");
                }
            } catch (Exception e) {
                handleReturn(e, callback);
            }
        }).start();
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
        if (surfaceView != null) {
            surfaceView.setRotation(rotation);
            surfaceHolder = surfaceView.getHolder();
        } else {
            textureView.setRotation(rotation);
            surfaceTexture = textureView.getSurfaceTexture();
        }
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
//                                mUVCCamera.setPreviewSize(size.width, size.height);
                                mUVCCamera.getUVCCamera().setPreviewSize(size.width, size.height, size.frame_type);
                            }
                            setStatus(STATUS_WAIT_PLAY, "准备预览...");

                            try {
                                if (surfaceView != null) {
                                    mUVCCamera.getUVCCamera().setPreviewDisplay(surfaceHolder);
                                } else {
                                    mUVCCamera.getUVCCamera().setPreviewTexture(surfaceTexture);
                                }
                                mUVCCamera.startPreview();
                            } catch (Exception e) {
                                setStatus(STATUS_ERROR, "预览失败:" + e.getMessage());
                            }

                            mUVCCamera.getUVCCamera().setFrameCallback(new IFrameCallback() {
                                @Override
                                public void onFrame(ByteBuffer frame) {
                                    if (status != STATUS_PLAYING) {
                                        setStatus(STATUS_PLAYING, "预览成功!!!");
                                        Map<String, Object> data = new HashMap<>();
                                        data.put("deivce", device.toJson());
//                                        data.put("detail", detail);
                                        safeFireEvent("onPlayed", data);
                                    }

                                    // 截图
                                    if (snapName != null) {
                                        byte[] yuv = new byte[frame.capacity()];
                                        frame.get(yuv);
                                        Size size = mUVCCamera.getPreviewSize();
                                        Log.i(TAG, "snap " + snapName + " with " + size.width + "x" + size.height + " @" + rotation);
                                        File file = FileUtil.getSDCardFile("snap", snapName);
                                        int width = size.width;
                                        int height = size.height;
//                                        if (((int) rotation) % 180 == 90) {
//                                            width = size.height;
//                                            height = size.width;
//                                        }
                                        String path = FileUtil.saveYuv2Jpeg(file, yuv, width, height, rotation);
                                        Log.i(TAG, "snap " + path);
                                        JSONObject ret = new JSONObject();
                                        ret.put("name", snapName);
                                        ret.put("path", "file://" + path);
                                        ret.put("width", width);
                                        ret.put("height", height);
                                        ret.put("size", file.length());
                                        snapName = null;
                                        snapRet = ret;
                                    }
                                    // End截图

                                    // fps
                                    if (bShowFps) {
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
                                    // end fps
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
        curDeviceId = mDeviceId;
        for (int i = 0; i < usbs.size(); ++i) {
            UsbDevice dev = usbs.get(i);
            if (mDeviceId == -1) {
                // 不限制
                if (isUsbCamera(dev)) {
                    // 找到
                    curDeviceId = dev.getDeviceId();
                    break;
                }
            } else if (mDeviceId == dev.getDeviceId()) {
                // 命中
                curDeviceId = dev.getDeviceId();
                break;
            }
        }
        try {
            if (curDeviceId >= 0) {
                getUsbWrapper().openDevice(curDeviceId, openCallback);
            } else {
                throw new IUException("不存在设备" + curDeviceId, 1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
            setStatus(STATUS_ERROR, "打开失败:" + e.getMessage());
        }
    }


    private void runOnUiThread(Runnable run) {
        UniSDKInstance instance = getUniInstance();
        if (instance == null || tv_status == null) {
            return;
        }
        instance.runOnUiThread(run);
    }

    private void displayText(final String text) {
        runOnUiThread(() -> {
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
        runOnUiThread(() -> {
            tv_info.setVisibility(View.VISIBLE);
            tv_info.setText(info);
        });
    }

    void showControlBar() {
        if (rootLayout != null) {
            View v = rootLayout.findViewById(R.id.ll_control_bar);
            if (v != null && v.getVisibility() == View.GONE) {
                v.setVisibility(View.VISIBLE);
                Log.i(TAG, "showControlBar");

                updateUsbDevices();
            }
        }
    }

    void hideControlBar() {
        if (rootLayout != null) {
            View v = rootLayout.findViewById(R.id.ll_control_bar);
            if (v != null && v.getVisibility() == View.VISIBLE) {
                v.setVisibility(View.GONE);
                Log.i(TAG, "hideCOntrolBar");
            }
        }
    }

    private void toggleControlBar() {
        if (rootLayout.findViewById(R.id.ll_control_bar).getVisibility() == View.GONE) {
            showControlBar();
        } else {
            hideControlBar();
        }
    }

    private void updatePreviewSizes() {
        Spinner sp_sizes = (Spinner) rootLayout.findViewById(R.id.sp_sizes);
        if (status == STATUS_PLAYING && mUVCCamera != null && mUVCCamera.isCameraOpen()) {
            ArrayList<String> list = new ArrayList<>();
            List<Size> sizes = mUVCCamera.getSupportedPreviewSizes();
            for (int i = 0; i < sizes.size(); ++i) {
                Size size = sizes.get(i);
                list.add(size.width + "x" + size.height + "[" + size.type + "]");
            }
            ArrayAdapter<String> adp = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, list);
            sp_sizes.setAdapter(adp);
            sp_sizes.setVisibility(View.VISIBLE);
            if (previewSizeIndex < sizes.size()) {
                sp_sizes.setSelection(previewSizeIndex);
            }
        } else {
            sp_sizes.setVisibility(View.GONE);
        }
    }

    private List<Integer> deviceIds = new ArrayList<>();

    private void updateUsbDevices() {
        Spinner sp_usbs = (Spinner) rootLayout.findViewById(R.id.sp_devices);
        ArrayList<String> list = new ArrayList<>();
        deviceIds.clear();
        List<UsbDevice> usbs = USBUtil.getUsbDevices(getContext(), -1, -1);
        int curUsb = -1;
        for (int i = 0; i < usbs.size(); ++i) {
            UsbDevice dev = usbs.get(i);
            if (!isUsbCamera(dev)) {
                // 找到
                continue;
            }
            deviceIds.add(dev.getDeviceId());
            String name = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                name = dev.getProductName();
            }
            if (TextUtils.isEmpty(name)) {
                name = dev.getDeviceId() + "";
            }
            list.add(name);
            if (dev.getDeviceId() == mDeviceId) {
                curUsb = list.size() - 1;
            }
        }
        if (list.size() > 1) {
            ArrayAdapter<String> adp = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, list);
            sp_usbs.setAdapter(adp);
            sp_usbs.setVisibility(View.VISIBLE);
            if (curUsb >= 0) {
                sp_usbs.setSelection(curUsb);
            }
        } else {
            sp_usbs.setVisibility(View.GONE);
        }
    }
}

