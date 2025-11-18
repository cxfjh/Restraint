package com.restraint;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.restraint.utils.LockPhone;
import com.restraint.utils.Permissions;
import com.restraint.utils.TimeUtils;


public class MainActivity extends AppCompatActivity {
    private static final int VPN_REQUEST_CODE = 1001; // VPN请求码
    private static final String PREF_NAME = "AppPreferences"; // 偏好设置名称
    private static final String KEY_FIRST_LAUNCH = "1.0.0"; // 首次启动标识


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // 状态栏透明
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) getWindow().setDecorFitsSystemWindows(false);
        final TypedValue tv = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.windowBackground, tv, true);
        final int bgColor = tv.data;
        getWindow().getDecorView().setSystemUiVisibility((Color.red(bgColor) * 0.299 + Color.green(bgColor) * 0.587 + Color.blue(bgColor) * 0.114) / 255 > 0.5 ? View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR : 0);


        // 检查是否为首次打开应用
        if (getSharedPreferences(PREF_NAME, MODE_PRIVATE).getBoolean(KEY_FIRST_LAUNCH, true)) {
            firstLaunch();
            getSharedPreferences(PREF_NAME, MODE_PRIVATE).edit().putBoolean(KEY_FIRST_LAUNCH, false).apply();
        }


        // 初始化时间选择器
        final TimePicker timePicker = findViewById(R.id.time_picker);
        timePicker.setIs24HourView(true);
        TimeUtils.initTime(timePicker);


        // 按钮点击事件处理
        final Button timeButton = findViewById(R.id.set_time_button);


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
            } else {
                new AlertDialog.Builder(this)
                        .setMessage("您将在" + ((time / 1000 / 60) + 1) + "分钟内无法使用手机，是否确定？")
                        .setPositiveButton("开始自律", (dialog, which) -> LockPhone.getInstance().enableLock(time))
                        .setNegativeButton("还是算了", null).show();
            }
        });
    }


    /**
     * 显示首次使用提示
     */
    private void firstLaunch() {
        final String message =
                        "本App使用将申请2个必要权限\n\n" +
                        "1. 无障碍权限：\n" +
                        "用于阻止用户进行一些操作。\n\n" +
                        "2. 网络权限：\n" +
                        "用于管理网络防止用户被其他消息干扰。\n\n" +
                        "本App是开源软件，\n" +
                        "您可以随意查看源代码。\n\n" +
                        "不会收集您的任何信息，\n" +
                        "如有顾虑可自行关闭网络。\n\n" +
                        "如有任何疑问，请联系作者：<2449579731@qq.com>";
        new AlertDialog.Builder(this).setTitle("权限申请说明").setMessage(message).setPositiveButton("确定", null).setCancelable(false).show();
    }
}