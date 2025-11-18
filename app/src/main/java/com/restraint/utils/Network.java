package com.restraint.utils;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.VpnService;
import android.os.ParcelFileDescriptor;


@SuppressLint("VpnServicePolicy")
public class Network extends VpnService {
    private ParcelFileDescriptor vpnInterface;


    /**
     * 网络流量拦截服务
     *
     * @param intent  启动参数
     * @param flags   启动标志
     * @param startId 启动ID
     * @return 服务状态
     */
    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        new Thread(() -> {
            try {
                // 创建 VPN 配置
                final Builder builder = new Builder();
                builder.addAddress("10.0.0.2", 32); // VPN 地址
                builder.addDnsServer("8.8.8.8");    // DNS 服务器
                builder.addRoute("0.0.0.0", 0);     // 拦截所有流量

                // 创建 VPN 接口
                final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()), PendingIntent.FLAG_IMMUTABLE);

                // 设置 VPN 接口
                vpnInterface = builder.setSession("网络流量拦截服务").setConfigureIntent(pendingIntent).establish();

                // 保持服务运行
                Thread.sleep(Long.MAX_VALUE);
            } catch (Exception ignored) {
            } finally {
                try {
                    if (vpnInterface != null) {
                        vpnInterface.close();
                        vpnInterface = null;
                    }
                } catch (final Exception ignored) {}
            }
        }).start();

        return START_STICKY;
    }


    /**
     * 销毁服务
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (vpnInterface != null) {
                vpnInterface.close();
                vpnInterface = null;
            }
        } catch (Exception ignored) {}
    }
}
