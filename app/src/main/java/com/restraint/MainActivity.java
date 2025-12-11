package com.restraint;

import android.app.Activity;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;
import com.restraint.utils.LockPhone;
import com.restraint.utils.ModernDialog;
import com.restraint.utils.Permissions;
import com.restraint.utils.TimeUtils;


public class MainActivity extends Activity {
    private static final int VPN_REQUEST_CODE = 1001; // VPN请求码
    private static final String PREF_NAME = "AppPreferences"; // 偏好设置名称
    private static final String KEY_FIRST_LAUNCH = "1.0.0"; // 首次启动标识
    private Vibrator vibrator;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // 检查是否为首次打开应用
        if (getSharedPreferences(PREF_NAME, MODE_PRIVATE).getBoolean(KEY_FIRST_LAUNCH, true)) {
            firstLaunch();
            getSharedPreferences(PREF_NAME, MODE_PRIVATE).edit().putBoolean(KEY_FIRST_LAUNCH, false).apply();
        }

        // 初始化时间选择器
        final TimePicker timePicker = findViewById(R.id.picker);
        timePicker.setIs24HourView(true);
        TimeUtils.initTime(timePicker);

        // 初始化振动器
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        timePicker.setOnTimeChangedListener((view, hourOfDay, minute) -> startVibration());

        // 按钮点击事件处理
        final Button timeButton = findViewById(R.id.btn);

        // 按钮点击事件处理
        timeButton.setOnClickListener(v -> {
            // 获取时间差
            final int hour = timePicker.getHour();
            final int minute = timePicker.getMinute();
            final long time = TimeUtils.TimeDifference(hour, minute);

            // 检查权限并开启锁屏
            if (!Permissions.isAccessibility(this)) {
                Toast.makeText(this, "请先授予无障碍权限", Toast.LENGTH_SHORT).show();
                Permissions.requestAccessibility(this);
            } else if (!Permissions.isVpn(this)) {
                Toast.makeText(this, "请先授予网络权限", Toast.LENGTH_SHORT).show();
                Permissions.requestVpn(this, VPN_REQUEST_CODE);
            } else ModernDialog.show(this, "自律时间", "您将在" + ((time / 1000 / 60) + 1) + "分钟内无法使用手机，是否确定？", () -> LockPhone.getInstance().enableLock(time), null);
        });
    }


    /**
     * 显示首次使用提示
     */
    private void firstLaunch() {
        final String message =
                "1. 无障碍权限：\n" +
                        "用于阻止用户进行一些操作。\n\n" +
                        "2. 网络权限：\n" +
                        "用于管理网络防止用户被其他消息干扰。\n\n" +
                        "本App是开源软件，\n" +
                        "您可以随意查看源代码。\n\n" +
                        "不会收集您的任何信息，\n" +
                        "如有顾虑可自行关闭网络。\n\n" +
                        "如有任何疑问，请联系作者：<2449579731@qq.com>";
        ModernDialog.show(this, "权限使用说明", message, null);
    }


    /**
     * 启动振动
     */
    private void startVibration() {
        try {
            // 检查振动器是否可用
            if (vibrator == null || !vibrator.hasVibrator()) return;

            // 检查振动效果是否支持
            if (VibrationEffect.createOneShot(80, VibrationEffect.DEFAULT_AMPLITUDE) == null) {
                vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK));
                return;
            }

            // 触发振动
            vibrator.vibrate(VibrationEffect.createOneShot(80, VibrationEffect.DEFAULT_AMPLITUDE));
        } catch (final Exception ignored) {}
    }
}