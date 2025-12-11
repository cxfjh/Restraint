package com.restraint.utils;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


@SuppressLint("AccessibilityPolicy")
public class LockPhone extends AccessibilityService {
    private static final String PREFS_NAME = "LockPrefs"; // 文件名
    private static final String KEY_IS_LOCKED = "is_locked"; // 键名
    private static final String KEY_END_TIME = "end_time"; // 键名
    private static volatile boolean isLocked = false; // 锁定状态
    private CoverView coverView; // 覆盖层
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1); // 定时器
    @SuppressLint("StaticFieldLeak")
    private static LockPhone instance; // 单例
    private SharedPreferences prefs; // 存储
    public static LockPhone getInstance() { return instance; } // 获取单例


    /**
     * 创建时初始化
     */
    @Override
    public void onCreate() {
        super.onCreate();
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE); // 从SharedPreferences中读取锁定状态
    }


    /**
     * 服务连接时恢复锁定状态
     */
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        instance = this;
        restoreLockState(); // 服务连接时恢复锁定状态
    }


    /**
     * 重启时恢复锁定状态
     */
    private void restoreLockState() {
        // 重启时恢复锁定状态
        final boolean shouldBeLocked = prefs.getBoolean(KEY_IS_LOCKED, false);
        final long endTime = prefs.getLong(KEY_END_TIME, 0);

        if (shouldBeLocked) {
            final long currentTime = System.currentTimeMillis();
            if (currentTime < endTime) {
                // 计算剩余锁定时间
                final long remainingTime = endTime - currentTime;
                enableLock(remainingTime);

                // 启动VPN服务
                if (Permissions.isVpn(this)) startService(new Intent(this, Network.class));
            } else disableLock(); // 锁定已过期，清除状态
        }
    }


    /**
     * 配置服务
     * @param enableLock 是否启用锁定
     */
    private void configureService(final boolean enableLock) {
        // 监听锁定状态
        final AccessibilityServiceInfo config = new AccessibilityServiceInfo();
        if (coverView == null) coverView = new CoverView(this.getSystemService(WindowManager.class), this);

        if (enableLock) {
            // 允许拦截按键和手势
            config.flags = AccessibilityServiceInfo.FLAG_REQUEST_FILTER_KEY_EVENTS | AccessibilityServiceInfo.FLAG_REQUEST_TOUCH_EXPLORATION_MODE | AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS;
            config.eventTypes = AccessibilityEvent.TYPES_ALL_MASK; // 设置拦截所有类型事件
            coverView.addOverlay(); // 添加覆盖层拦截触摸
        } else {
            config.flags = config.eventTypes = 0;
            coverView.removeOverlay();
        }

        // 配置反馈类型
        config.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        setServiceInfo(config);
    }


    /**
     * 启用锁定
     * @param duration 锁定时长（毫秒）
     */
    public void enableLock(final long duration) {
        // 设置锁定状态
        final long currentTime = System.currentTimeMillis();
        final long endTime = currentTime + duration;

        // 保存状态
        prefs.edit().putBoolean(KEY_IS_LOCKED, true).putLong(KEY_END_TIME, endTime).apply();
        isLocked = true;
        configureService(true);

        // 启动VPN服务
        final Intent intent = new Intent(this, Network.class);
        startService(intent);

        // 设置自动解锁定时器
        executor.schedule(this::disableLock, duration, TimeUnit.MILLISECONDS);
    }


    /**
     * 禁用锁定
     */
    public void disableLock() {
        // 清除存储的状态
        prefs.edit().putBoolean(KEY_IS_LOCKED, false).remove(KEY_END_TIME).apply();
        isLocked = false;
        configureService(false);

        // 停止VPN服务
        final Intent intent = new Intent(this, Network.class);
        stopService(intent);
    }


    /**
     * 拦截窗口交互事件
     * @param event 窗口交互事件
     */
    @Override
    public void onAccessibilityEvent(final AccessibilityEvent event) {
        // 拦截窗口交互事件
        if (!isLocked) return;

        // 拦截UI点击状态变化
        if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED) performGlobalAction(GLOBAL_ACTION_BACK);
        else if (event.getEventType() == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) performGlobalAction(GLOBAL_ACTION_HOME);
    }


    /**
     * 拦截手势
     * @param gestureId 手势ID
     * @return true如果已锁定，false否则
     */
    @Override
    protected boolean onGesture(final int gestureId) { return isLocked; } // 锁定手势


    /**
     * 拦截物理按键
     * @param event 物理按键事件
     * @return true如果已锁定，false否则
     */
    @Override
    protected boolean onKeyEvent(final KeyEvent event) { return isLocked; } // 锁定物理按键


    /**
     * 忽略中断事件
     */
    @Override
    public void onInterrupt() {} // 忽略中断事件
}

