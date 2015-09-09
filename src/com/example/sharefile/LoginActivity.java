package com.example.sharefile;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

import jcifs.UniAddress;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbSession;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.example.util.MyTag;

public class LoginActivity extends Activity {
	
	private String name="";
	private String password = "";
	private InetAddress mIp = null;
	
	
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
			case 0:
				new Builder(LoginActivity.this).setTitle("登录失败")
						.setMessage("您输入的用户名或密码错误。")
						.setNegativeButton("返回", null).create().show();
				break;
			case 1:
				Intent intent = new Intent();
				intent.putExtra("ip", mIp.getHostAddress());
				intent.putExtra("name", name);
				intent.putExtra("password", password);
				intent.setClass(LoginActivity.this, FunctionSelectActivity.class);
				LoginActivity.this.startActivity(intent);
				break;
			}
				
		}
	};
	
	private Runnable runnable = new Runnable() {
		
		@Override
		public void run() {
			String tempIP = mIp.getHostAddress();
			UniAddress myDomain = new UniAddress(mIp);
		    NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(tempIP, name , password);
		    System.out.println("NtlmPasswordAuthentication   " + auth.toString());
			try {
				SmbSession.logon(myDomain,auth); //若成功建立smb连接，启动文件浏览
				mHandler.obtainMessage(1).sendToTarget();			
			} catch (SmbException e) {
//				Log.i(MyTag.myTest, Arrays.toString(e.getStackTrace()));
				e.printStackTrace();
				mHandler.obtainMessage(0).sendToTarget();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		

		Intent intent = getIntent();
		final String tempIP = intent.getStringExtra("ip");

		setTitle("登录 " + tempIP);
		
		Button buttonLogin = (Button)findViewById(R.id.button_login);
		buttonLogin.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				name = ((EditText) findViewById(R.id.editText_login_name)).getText().toString();
				password = ((EditText) findViewById(R.id.editText_login_password)).getText().toString();

				Log.i(MyTag.myTest, name + "@" + password);
				
				try {
					mIp = InetAddress.getByName(tempIP);
					Log.i(MyTag.myTest, mIp.toString());
				} catch (UnknownHostException e) {
					Log.i(MyTag.myTest, e.toString());
				} catch(Exception e) {
					Log.i(MyTag.myTest, e.toString());
				}
				
				new Thread(runnable).start();
			}
		});

		Button buttonReturn = (Button) findViewById(R.id.button_return);
		buttonReturn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.login_menu, menu);
		return true;
	}
}
