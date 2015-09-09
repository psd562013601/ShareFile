package com.example.sharefile;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
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

import com.example.util.FileOpening;

public class LocalFileListActivity extends ListActivity {

	private List<Map<String, Object>> mData;
	private String mDir = Environment.getExternalStorageDirectory().getPath() + "/FileShare";
	private String ip;
	private String name;
	private String password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setTitle(R.string.local_File_after_upload);
		Intent intent = this.getIntent();
		ip = intent.getStringExtra("ip");
		name = intent.getStringExtra("name");
		password = intent.getStringExtra("password");

		if( intent.getStringExtra("purpose").equals("upload")){
			mDir = Environment.getExternalStorageDirectory().getPath();
			getListView().setOnItemLongClickListener(new MyOnItemLongClickListener());
			setTitle(R.string.local_File);
		}
		mData = getData();
		MyAdapter adapter = new MyAdapter(this);
		setListAdapter(adapter);

		WindowManager m = getWindowManager();
		Display display = m.getDefaultDisplay();
		LayoutParams p = getWindow().getAttributes();
		Point outSize = new Point();
		display.getSize(outSize);
		p.width = outSize.x;
		p.height = outSize.y;
		getWindow().setAttributes(p);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Log.d("MyListView4-click", (String) mData.get(position).get("info"));
		if ((Integer) mData.get(position).get("img") == R.drawable.ex_folder) {
			mDir = (String) mData.get(position).get("info");
			mData = getData();
			MyAdapter adapter = new MyAdapter(this);
			setListAdapter(adapter);
		} else {
			startActivity(FileOpening.openFile((String) mData.get(position).get("info")));
//			Intent intent = new Intent();
//			intent.putExtra("out",(String) mData.get(position).get("info"));//getString(R.string.dialog_read_from_dir));
//			intent.setClass(LocalFileManage.this,Sysout.class);
//			LocalFileManage.this.startActivity(intent); 
		}
	}

	private List<Map<String, Object>> getData() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		File f = new File(mDir);
		File[] files = f.listFiles();

		if (!mDir.equals(Environment.getExternalStorageDirectory().getPath())) {
			map = new HashMap<String, Object>();
			map.put("title", "Back to ../");
			map.put("info", f.getParent());
			map.put("img", R.drawable.ex_folder);
			list.add(map);
		}
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				map = new HashMap<String, Object>();
				map.put("title", files[i].getName());
				map.put("info", files[i].getPath());
				if (files[i].isDirectory())
					map.put("img", R.drawable.ex_folder);
				else
					map.put("img", R.drawable.ex_doc);
				list.add(map);
			}
		}
		return list;
	}

	private class MyOnItemLongClickListener implements OnItemLongClickListener  {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			if (! ((Integer) mData.get(position).get("img") == R.drawable.ex_folder) ) {
				Intent intent = new Intent();
				intent.putExtra("purpose", "upload");
				intent.putExtra("ip", ip);
				intent.putExtra("name", name);
				intent.putExtra("password", password);
				intent.putExtra("UploadFileDir",(String) mData.get(position).get("info"));
				intent.setClass(LocalFileListActivity.this, FileShowingActivity.class);
				LocalFileListActivity.this.startActivity(intent);
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
				holder.info = (TextView) convertView.findViewById(R.id.info);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.img.setBackgroundResource((Integer) mData.get(position).get(
					"img"));
			holder.title.setText((String) mData.get(position).get("title"));
			holder.info.setText((String) mData.get(position).get("info"));
			return convertView;
		}
		
		private final class ViewHolder {
			public ImageView img;
			public TextView title;
			public TextView info;
		}
	}

}
