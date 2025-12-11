package com.restraint.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.VpnService;
import android.provider.Settings;
import android.widget.Toast;


public class Permissions {
    /**
     * 检查无障碍服务是否已启用
     *
     * @param context 上下文
     * @return true如果已启用，false否则
     */
    public static boolean isAccessibility(final Context context) {
        try {
            final int accessibilityEnabled = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED); // 获取无障碍服务是否启用的设置值
            return accessibilityEnabled == 1; //  1代表已启用
        } catch (final Settings.SettingNotFoundException ignored) {
            Toast.makeText(context, "请先启用无障碍服务", Toast.LENGTH_SHORT).show();
        }
        return false;
    }


    /**
     * 跳转到无障碍设置页面
     *
     * @param context 上下文
     */
    public static void requestAccessibility(final Context context) {
        final Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        context.startActivity(intent);
    }


    /**
     * 检查是否有VPN权限
     *
     * @param context 上下文
     * @return true如果已有权限，false否则
     */
    public static boolean isVpn(final Context context) {
        final Intent intent = VpnService.prepare(context);
        return intent == null;
    }


    /**
     * 请求VPN权限
     *
     * @param activity    当前Activity
     * @param requestCode 请求码
     */
    public static void requestVpn(final Activity activity, final int requestCode) {
        final Intent intent = VpnService.prepare(activity); // 创建权限请求Intent
        if (intent != null) activity.startActivityForResult(intent, requestCode); // 启动权限请求Activity
    }
}
