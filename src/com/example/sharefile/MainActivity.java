package com.example.sharefile;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.util.IPTest;
import com.example.util.MyTag;
import com.example.util.NetworkUtil;
import com.example.util.ShakeListener;
import com.example.util.ShakeListener.OnShakeListener;


public class MainActivity extends Activity {
	private ShakeListener mShakeListener;
	private SensorManager mSensorManager; // ������������
	private Vibrator mVibrator;// ����
	private String domainIP;
	private boolean atForeground;//�Ƿ���ǰ̨
	
	
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Bundle bundle = msg.getData();
			String val = bundle.getString("value", "UNKNOWN");
			Log.i(MyTag.myTest, val);
			switch(msg.what) {
			case 0: //����ʧ��
				new Builder(MainActivity.this)
						.setTitle(R.string.connect_failed_title)
						.setMessage(val)
						.setNegativeButton(R.string.ok, null).create().show();
				break;
			case 1:
				new Builder(MainActivity.this)
						.setTitle(R.string.connect_succeed_title)
						.setMessage(val)
						.setNegativeButton(R.string.ok, null).create().show();
				break;
			}
		}
		
	};
	
	private Runnable runnable = new Runnable() {
		@Override
		public void run() {
	        Message msg = new Message();  
	        Bundle data = new Bundle();  
	        data.putString("value", "������ ipTest.test().equalsIgnoreCase(\"\")");  
	        msg.setData(data);
			try {
				String theIP = IPTest.testwithThrows(domainIP);

				data.putString("value", "������ �ɹ����ӵ�ָ��IP: " + theIP);
				msg.what = 1;
				msg.setData(data);
		        mHandler.sendMessage(msg);
			} catch (Exception e) {
				data.putString("value", "������ " + e.toString());
				msg.what = 0;
				msg.setData(data);
		        mHandler.sendMessage(msg);
			}
		}
	};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);//�벼���ļ�����

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    	mVibrator = (Vibrator) getApplication().getSystemService(VIBRATOR_SERVICE);
    	
    	atForeground = true;
        
        init();
    }
    
    private void init() {
    	TextView textViewMyIP = (TextView)findViewById(R.id.textView_myIP);
    	textViewMyIP.setText("�ҵ�IP : " + getMyWifiIP());
    	
    	
    	final LayoutInflater inflater = LayoutInflater.from(this);
    	ImageButton buttonInfo = (ImageButton) findViewById(R.id.button_info);
    	buttonInfo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				View view = inflater.inflate(R.layout.developer_info, null);
				new Builder(MainActivity.this).setTitle("�����ǿ�����").setView(view)
						.setNegativeButton("��Ŷ˧����> <~!!!", null).create().show();
			}
		});
    	
    	final EditText editText = (EditText)findViewById(R.id.editText_IP_input);
    	
    	ImageButton buttonSearch = (ImageButton) findViewById(R.id.button_search);
    	buttonSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String dIP = editText.getText().toString();//���벻Ϊ�գ����������û������ַ
				if(dIP.equalsIgnoreCase("")) {
					Toast.makeText(getApplicationContext(), R.string.please_shake, Toast.LENGTH_LONG).show();
					return;
				}
				domainIP = dIP;
				new Thread(runnable).start();
			}
		});
    	
    	ImageButton buttonIPReferesh = (ImageButton) findViewById(R.id.button_ip_refresh);
    	buttonIPReferesh.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
		    	TextView textViewMyIP = (TextView)findViewById(R.id.textView_myIP);
		    	textViewMyIP.setText("�ҵ�IP : " + getMyWifiIP());
			}
		});
    	
    }

	@Override
	protected void onResume() {
		super.onResume();
    	mShakeListener = new ShakeListener(this);
    	mShakeListener.setOnShakeListener(new MyOnShakeListener());

    	atForeground = true;
	}

	@Override
	protected void onPause() {
		Log.i(MyTag.myTest, "onPause()������");
		if (null != mShakeListener) {// ȡ�������� 
			mShakeListener.stop();
        }
    	atForeground = false;
		super.onPause();
	}

//	@Override
//	protected void onStop() {
//		if (null != mShakeListener) {// ȡ�������� 
//			mShakeListener.stop();
//        } 
//    	atForeground = false;
//		super.onStop();
//	}

	//��ͨ��Wifi�����Ļ����л�ȡIP��ַ����֧��GPRS��������
    private String getMyWifiIP() {
    	WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
    	// �ж��Ƿ���
		if (!manager.isWifiEnabled()) {
//			manager.setWifiEnabled(true);
			//��ת�� wifi���ý���
			startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
		}
		return NetworkUtil.intToIp(manager.getConnectionInfo().getIpAddress());
	}
	

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private class MyOnShakeListener implements OnShakeListener {
		
		@Override
		public void onShake() {
			Log.v(MyTag.myTest, "MyOnShakeListener.onShake()");
			//����ȡ��ע��
			mShakeListener.stop();
			//��200ms
			mVibrator.vibrate(100);
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, IPListActivity.class);
			MainActivity.this.startActivity(intent);
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					if(atForeground) {
						mShakeListener.start();
					}
				}
			}, 10000);
		}
	}
}
