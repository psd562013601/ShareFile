package com.example.util;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class ShakeListener implements SensorEventListener {
	private static final int SPEED_SHRESHOLD = 3000; // 速度阈值，当摇晃速度达到这值后产生作用
	private static final int UPTATETIME_INTERVAL = 80; // 两次检测的时间间隔
	private SensorManager mSensorManager; // 传感器管理器
	private OnShakeListener onShakeListener; // 重力感应监听器
	private Context mContext; // 上下文
	private float lastX;
	private float lastY;
	private float lastZ; // 手机上一个位置时重力感应坐标
	private long lastUpdateTime; // 上次检测时间

	public ShakeListener(Context mContext) {
		this.mContext = mContext;
		mSensorManager = (SensorManager) this.mContext.getSystemService(Context.SENSOR_SERVICE);
		
		start();
	}

	public void start() {
		mSensorManager.registerListener(this,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_GAME);

		Log.v(MyTag.myTest, "mSensorManager注册");
	}
	
	public void stop() {
		mSensorManager.unregisterListener(this);
		Log.v(MyTag.myTest, "mSensorManager取消注册");
	}

	public void setOnShakeListener(OnShakeListener onShakeListener) {
		this.onShakeListener = onShakeListener;
	}


	// 重力传感器获得变化数据
	@Override
	public void onSensorChanged(SensorEvent event) {
		long currUpdateTime = System.currentTimeMillis();
		long timeInterval = currUpdateTime - lastUpdateTime;
		if (timeInterval < UPTATETIME_INTERVAL)// 时间间隔过小
			return;
		lastUpdateTime = currUpdateTime;

		// 获得坐标
		float x = event.values[0];
		float y = event.values[1];
		float z = event.values[2];
		// 获得x,y,z的变化值
		float deltaX = x - lastX;
		float deltaY = y - lastY;
		float deltaZ = z - lastZ;
		// 将现在的坐标变成last坐标
		lastX = x;
		lastY = y;
		lastZ = z;
		// sqrt 返回最近的双近似的平方根
		double speed = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ
				* deltaZ) / timeInterval * 10000;

        // 一般在这三个方向的重力加速度达到40就达到了摇晃手机的状态。  
        int medumValue = 19;// 三星 i9250怎么晃都不会超过20，没办法，只设置19了  
        if (Math.abs(x) > medumValue || Math.abs(y) > medumValue || Math.abs(z) > medumValue) {
        	
        }
        
		// 达到速度阀值，发出提示
		if (SPEED_SHRESHOLD <= speed) {
			onShakeListener.onShake();
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}
	
	public interface OnShakeListener {
		public void onShake();
	}
}
