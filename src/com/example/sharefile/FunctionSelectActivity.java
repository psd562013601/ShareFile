package com.example.sharefile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;

public class FunctionSelectActivity extends Activity{
	private String ip;
	private String name;
	private String password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_function_select);
		
		Intent intent = this.getIntent();
		ip = intent.getStringExtra("ip");
		name = intent.getStringExtra("name");
		password = intent.getStringExtra("password");
		

		ImageButton buttonLocal =  (ImageButton)findViewById(R.id.imageButton_local_file);		
		buttonLocal.setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.putExtra("purpose", "upload");
				intent.putExtra("ip", ip);
				intent.putExtra("name", name);
				intent.putExtra("password", password);
				intent.setClass(FunctionSelectActivity.this, LocalFileListActivity.class);
				FunctionSelectActivity.this.startActivity(intent);	
				
			}
		});
		
		ImageButton buttonRemote = (ImageButton)findViewById(R.id.imageButton_remote_file);
		buttonRemote.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("purpose", "download");
				intent.putExtra("ip", ip);
				intent.putExtra("name", name);
				intent.putExtra("password", password);
				intent.setClass(FunctionSelectActivity.this, FileShowingActivity.class);
				FunctionSelectActivity.this.startActivity(intent);	
				
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.function_select, menu);
		return true;
	}

}
