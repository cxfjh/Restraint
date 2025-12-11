package com.restraint.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import java.util.Objects;


public class BootReceiver extends BroadcastReceiver {
    /**
     * 监听开机广播
     * @param context 上下文
     * @param intent 意图
     */
    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (Objects.equals(intent.getAction(), Intent.ACTION_BOOT_COMPLETED)) {
            context.startService(new Intent(context, LockPhone.class)); // 启动锁屏服务
        }
    }
}
