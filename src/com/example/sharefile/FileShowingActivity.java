package com.example.sharefile;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.util.FileOpening;
import com.example.util.FileOperation;
import com.example.util.MyTag;

public class FileShowingActivity extends ListActivity {
	private List<Map<String, Object>> mData = new ArrayList<Map<String,Object>>();
	private String mDir = Environment.getExternalStorageDirectory().getPath();
	
	private ProgressDialog pd;
	private String ip,name,password;
	private String downloadDir = "";
	private String uploadFrom = "";
	private String uploadTo = "";
	private int lisFilter = 0;
	
	private Handler mHandler = new Handler() {  
        public void handleMessage (Message msg) {//此方法在ui线程运行  
            switch(msg.what) {  
            case 0: 
            	//下载失败
            	pd.dismiss();
            	Toast.makeText(getApplication(), "Ops,下载失败了 ToT", Toast.LENGTH_LONG).show(); 
            	break; 
            case 1:  
            	//下载成功
            	pd.dismiss();            	
            	Toast.makeText(getApplication(), "下载成功", Toast.LENGTH_LONG).show(); 
            	break;
            case 2:
            	//上传成功
            	pd.dismiss();
            	Toast.makeText(getApplication(), "上传成功", Toast.LENGTH_LONG).show();
            	break;
            case 3:
            	//上传失败
            	pd.dismiss();
            	Toast.makeText(getApplication(), "Ops,上传失败了 ToT", Toast.LENGTH_LONG).show();
            	break;
            
            }  
        }  
    };

	private Runnable runnable_down = new Runnable() {

		@Override
		public void run() {
			// run()在新的线程中运行
			boolean rs = FileOperation.fileDownload(downloadDir);
			if (rs == true) {
				mHandler.obtainMessage(1).sendToTarget();
				try {
					SmbFile sf = new SmbFile(downloadDir);
					String downloadFileName = sf.getName();
					FileShowingActivity.this.startActivity(FileOpening
							.openFile(Environment.getExternalStorageDirectory()
									.getPath() + "/FileShare/" + downloadFileName));

				} catch (MalformedURLException e) {
					e.printStackTrace();
				}

			} else {
				mHandler.obtainMessage(0).sendToTarget();
			}
		}
	};

	private Runnable runnable_up = new Runnable() {
		@Override
		public void run() {
			// run()在新的线程中运行
			boolean rs = FileOperation.fileUpload(uploadFrom, uploadTo);
			if (rs == true) {
				mHandler.obtainMessage(2).sendToTarget();
				Intent intent = new Intent();
				intent.putExtra("ip", ip);
				intent.putExtra("name", name);
				intent.putExtra("password", password);
				intent.setClass(FileShowingActivity.this, FunctionSelectActivity.class);
				FileShowingActivity.this.startActivity(intent);
			} else {
				mHandler.obtainMessage(3).sendToTarget();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = this.getIntent();
		ip = intent.getStringExtra("ip");
		name = intent.getStringExtra("name");
		password = intent.getStringExtra("password");
		if( intent.getStringExtra("purpose").equals("upload")){
			uploadFrom = intent.getStringExtra("UploadFileDir");
			lisFilter = 1;
			setTitle("长按选择上传目录");
			getListView().setOnItemLongClickListener(new MyOnItemLongClickListener());
		}else{
			setTitle("远程文件");
		}
		
		mDir = "smb://" + name + ":" + password + "@" + ip;
		
		try {
			mData = getData(mDir);
		} catch (Exception e) {
			e.printStackTrace();
		}
		MyAdapter adapter = new MyAdapter(this);
		setListAdapter(adapter);
		WindowManager windowManager = getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		LayoutParams p = getWindow().getAttributes();
		Point outSize = new Point();
		display.getSize(outSize);
		p.width = outSize.x;
		p.height = outSize.y;
		getWindow().setAttributes(p);
	}

	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.file_showing, menu);
		return true;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Log.d(MyTag.myTest, (String) mData.get(position).get("info"));
		//判断点击的是文件夹还是文件
		if ((Integer) mData.get(position).get("img") == R.drawable.ex_folder) {
			//点击文件夹
			mDir = (String) mData.get(position).get("info");
			try {
				mData = getData(mDir);
			} catch (Exception e) {
				Log.e(MyTag.myTest, e.toString());
				e.printStackTrace();
			}
			MyAdapter adapter = new MyAdapter(this);
			setListAdapter(adapter);
		} else if (!(lisFilter == 1)) {// 点击文件
			downloadDir = (String) mData.get(position).get("info");

	        new Thread(runnable_down).start();//线程启动 

			if(null == pd) {
				pd = new ProgressDialog(this);
				pd.setCancelable(false);
				pd.setCanceledOnTouchOutside(false);
				pd.setTitle("下载中...");
				pd.setMessage("程序猿偷懒了，不想计算时间..."+ "\n"+"等着吧...");
			}
			pd.show();
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private List<Map<String, Object>> getData(String dir) throws Exception {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		SmbFile f = new SmbFile(dir);
		SmbFile[] files = f.listFiles();

		if (!mDir.equals(Environment.getExternalStorageDirectory().getPath())) {
			map = new HashMap<String, Object>();
			map.put("title", "返回上一级 ../");
			map.put("info", f.getParent());
			map.put("img", R.drawable.ex_folder);
			list.add(map);
		}
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				String title = files[i].getName();
				String info = files[i].getPath();
				map = new HashMap<String, Object>();
				map.put("title", title);
				map.put("info", info);
				if (files[i].isDirectory()) {
					try {
						new SmbFile(info).listFiles();//禁止访问的文件夹直接过滤掉
					} catch (Exception e) {
						continue;
					}
					map.put("img", R.drawable.ex_folder);
				} else
					map.put("img", R.drawable.ex_doc);
				
				list.add(map);
			}
		}
		return list;
	}
	
	private class MyOnItemLongClickListener implements OnItemLongClickListener {

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View v, int position, long id) {
			//判断点击的是文件夹还是文件
			if ((Integer) mData.get(position).get("img") == R.drawable.ex_folder) {
				//点击文件夹
				uploadTo = (String) mData.get(position).get("info");
		        new Thread(runnable_up).start(); //线程启动 
		        
				if(null == pd) {
					pd = new ProgressDialog(FileShowingActivity.this);
					pd.setCancelable(false);
					pd.setCanceledOnTouchOutside(false);
					pd.setTitle("上传中");
					pd.setMessage("程序猿偷懒了，不想计算时间..."+ "\n"+"等着吧...");
				}
				pd.show();
			}else {//点击文件
				Toast.makeText(getApplication(), "拜托，选一个文件夹有这么麻烦？", Toast.LENGTH_LONG).show(); 
			 }
			return false;
		}
	}
	

	private class MyAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public MyAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		public int getCount() {
			return mData.size();
		}

		public Object getItem(int arg0) {
			return null;
		}

		public long getItemId(int arg0) {
			return 0;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.activity_local_file_list, null);
				holder.img = (ImageView) convertView.findViewById(R.id.img);
				holder.title = (TextView) convertView.findViewById(R.id.title);
//				holder.info = (TextView) convertView.findViewById(R.id.info);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.img.setBackgroundResource((Integer) mData.get(position).get("img"));
			holder.title.setText((String) mData.get(position).get("title"));
//			holder.info.setText((String) mData.get(position).get("info"));
			return convertView;
		}
		
		private final class ViewHolder {
			public ImageView img;
			public TextView title;
//			public TextView info;
		}
	}

	
}
