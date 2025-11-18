package com.restraint.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import java.util.Objects;


/* 设备开机启动广播接收器 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (Objects.equals(intent.getAction(), Intent.ACTION_BOOT_COMPLETED)) {
            context.startService(new Intent(context, LockPhone.class)); // 启动锁屏服务
        }
    }
}
