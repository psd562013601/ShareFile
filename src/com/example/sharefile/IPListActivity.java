package com.example.sharefile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.util.FindUsefulIP;
import com.example.util.MyTag;
import com.example.util.NetworkUtil;

public class IPListActivity extends ListActivity{
	private List<Map<String, Object>> mData;
	private Thread mThread;
	private List<String> iplist;
	private ProgressDialog pd;

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
            switch(msg.what) {  
			case 1:
				pd.dismiss();
				mData = getData(iplist);

				setListAdapter(new MyAdapter(getApplication()));

				final LayoutParams p = getWindow().getAttributes();
				Point outSize = new Point();
				getWindowManager().getDefaultDisplay().getSize(outSize);
				p.width = outSize.x;
				p.height = outSize.y;
				
				getWindow().setAttributes(p);
				break;
			case 0:
				pd.dismiss();
				Toast.makeText(getApplication(), "Ops, û���ҵ����õ�ַ", Toast.LENGTH_LONG).show();
				IPListActivity.this.finish();
				break;
			}
		}
		
	};
	
	private Runnable runnable = new Runnable() {

		@Override
		public void run() {
        	WifiManager wm = (WifiManager)getSystemService(Context.WIFI_SERVICE);
            DhcpInfo dhcp = wm.getDhcpInfo();
            
			String ip = NetworkUtil.intToIp(dhcp.ipAddress);

            FindUsefulIP fufi = new FindUsefulIP(ip);
            try {
    			fufi.seek();
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
            iplist = fufi.getIpList();
            if(iplist.isEmpty()){
            	mHandler.obtainMessage(0).sendToTarget();//��ȡIPΪ��  
            	return; 
            }
            mHandler.obtainMessage(1).sendToTarget();//��ȡIP�ɹ�����ui�̷߳���MSG_SUCCESS��ʶ  
		}  
		
	};
	
	public IPListActivity() {
		iplist = new ArrayList<String>();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		String title = "���ҵ���ͼ���..";

		if(mThread == null) {  
            mThread = new Thread(runnable);  
            mThread.start();//�߳�����  
        } 
		
		if(null == pd) {
			pd = new ProgressDialog(this);
		}
		pd.setTitle("����ɨ��,��ȴ�~");
		pd.setMessage("������Գ���㣬��Լ��Ҫ10�롣");
		
		pd.show();
		this.setTitle(title);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Log.d(MyTag.myTest, (String) mData.get(position).get("ip"));
		String ip = (String) mData.get(position).get("ip");
		Intent intent = new Intent();
		intent.putExtra("ip", ip);
		intent.setClass(IPListActivity.this, LoginActivity.class);
		this.startActivity(intent);  
		this.finish();
		this.onDestroy();
	}

	private List<Map<String,Object>> getData(List<String> iplist){
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		if(iplist != null){
			for(int i=0;i<iplist.size();i++){
				map = new HashMap<String, Object>();
				map.put("img", R.drawable.iplist);
				map.put("ip", iplist.get(i));
				list.add(map);
			}
		}
		return list;
	}

	private class MyAdapter extends BaseAdapter {

		private LayoutInflater mInflater;

		public MyAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return mData.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.activity_ip_list, null);
				holder.img = (ImageView) convertView.findViewById(R.id.ip_list_img);
				holder.ip = (TextView) convertView.findViewById(R.id.test_view_IP);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.img.setBackgroundResource((Integer) mData.get(position).get("img"));
			holder.ip.setText((String) mData.get(position).get("ip"));
			return convertView;
		}

		private final class ViewHolder {
			public ImageView img;
			public TextView ip;
		}

	}
}
