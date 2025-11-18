package com.restraint.utils;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.View;
import android.view.WindowManager;


/* 全屏透明覆盖层 */
public class CoverView {
    private View overlayView;
    private final WindowManager windowManager;
    private final Context context;


    public CoverView(final WindowManager windowManager, final Context context) {
        this.windowManager = windowManager;
        this.context = context;
    }


    /**
     * 添加全屏透明覆盖层
     */
    public void addOverlay() {
        if (overlayView != null) return;
        overlayView = new View(context);
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSPARENT);
        windowManager.addView(overlayView, params);
    }


    /**
     * 移除全屏透明覆盖层
     */
    public void removeOverlay() {
        /*移除覆盖层*/
        if (overlayView != null) {
            windowManager.removeView(overlayView);
            overlayView = null;
        }
    }
}
