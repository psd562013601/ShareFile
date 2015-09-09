package com.example.util;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class ShakeListener implements SensorEventListener {
	private static final int SPEED_SHRESHOLD = 3000; // �ٶ���ֵ����ҡ���ٶȴﵽ��ֵ���������
	private static final int UPTATETIME_INTERVAL = 80; // ���μ���ʱ����
	private SensorManager mSensorManager; // ������������
	private OnShakeListener onShakeListener; // ������Ӧ������
	private Context mContext; // ������
	private float lastX;
	private float lastY;
	private float lastZ; // �ֻ���һ��λ��ʱ������Ӧ����
	private long lastUpdateTime; // �ϴμ��ʱ��

	public ShakeListener(Context mContext) {
		this.mContext = mContext;
		mSensorManager = (SensorManager) this.mContext.getSystemService(Context.SENSOR_SERVICE);
		
		start();
	}

	public void start() {
		mSensorManager.registerListener(this,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_GAME);

		Log.v(MyTag.myTest, "mSensorManagerע��");
	}
	
	public void stop() {
		mSensorManager.unregisterListener(this);
		Log.v(MyTag.myTest, "mSensorManagerȡ��ע��");
	}

	public void setOnShakeListener(OnShakeListener onShakeListener) {
		this.onShakeListener = onShakeListener;
	}


	// ������������ñ仯����
	@Override
	public void onSensorChanged(SensorEvent event) {
		long currUpdateTime = System.currentTimeMillis();
		long timeInterval = currUpdateTime - lastUpdateTime;
		if (timeInterval < UPTATETIME_INTERVAL)// ʱ������С
			return;
		lastUpdateTime = currUpdateTime;

		// �������
		float x = event.values[0];
		float y = event.values[1];
		float z = event.values[2];
		// ���x,y,z�ı仯ֵ
		float deltaX = x - lastX;
		float deltaY = y - lastY;
		float deltaZ = z - lastZ;
		// �����ڵ�������last����
		lastX = x;
		lastY = y;
		lastZ = z;
		// sqrt ���������˫���Ƶ�ƽ����
		double speed = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ
				* deltaZ) / timeInterval * 10000;

        // һ����������������������ٶȴﵽ40�ʹﵽ��ҡ���ֻ���״̬��  
        int medumValue = 19;// ���� i9250��ô�ζ����ᳬ��20��û�취��ֻ����19��  
        if (Math.abs(x) > medumValue || Math.abs(y) > medumValue || Math.abs(z) > medumValue) {
        	
        }
        
		// �ﵽ�ٶȷ�ֵ��������ʾ
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
