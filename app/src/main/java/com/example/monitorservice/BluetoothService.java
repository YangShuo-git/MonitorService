package com.example.monitorservice;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class BluetoothService extends Service {
    private static final String TAG = "BluetoothService";
    private Timer timer; // 定时器，创建一个线程
    private BluetoothBinder bluetoothBinder = new BluetoothBinder();

    public BluetoothService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");

        checkBluetooth();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBinder");
        return bluetoothBinder;
        // TODO: Return the communication channel to the service.
        // throw new UnsupportedOperationException("Not yet implemented");
    }

    public class BluetoothBinder extends Binder {

    }

    private void  checkBluetooth(){
        if (timer == null){
            timer = new Timer();

            // 设置定时任务（线程）
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    // 1、创建消息对象 Message，其消息标识符为1
                    Message msg = MainActivity.handler.obtainMessage(1);

                    // 2、需要先用bundle封装消息，然后把bundle设置进Message中
                    Bundle bundle = new Bundle();
                    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    if (bluetoothAdapter != null){
                        boolean flag = bluetoothAdapter.isEnabled();
                        if (flag){
                            bundle.putString("blue_status", "蓝牙已经打开");
                        } else {
                            bundle.putString("blue_status", "蓝牙已经关闭");
                        }
                    } else {
                        bundle.putString("blue_status", "蓝牙异常");
                    }
                    msg.setData(bundle);

                    // 3、发送消息对象，发送成功的话，MainActivity的Handler就会回调handleMessage()
                    MainActivity.handler.sendMessage(msg);
                    Log.d(TAG, "checkBluetooth(): " + msg);
                }
            };

            // 延迟1s开始监测，之后每隔5s执行一次
            timer.schedule(task,1*1000,10*1000);
        }
    }
}