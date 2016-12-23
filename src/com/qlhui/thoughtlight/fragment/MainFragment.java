package com.qlhui.thoughtlight.fragment;

import java.util.List;

import com.qlhui.thoughtlight.MainTabActivity;
import com.qlhui.thoughtlight.NearbyActivity;
import com.qlhui.thoughtlight.R;
import com.qlhui.thoughtlight.UploadActivity;
import com.qlhui.thoughtlight.info.BJXUserInfo;
import com.qlhui.thoughtlight.model.Model;
import com.qlhui.thoughtlight.myview.MyListView;
import com.qlhui.thoughtlight.myview.PagerSlidingTabStrip;
import com.qlhui.thoughtlight.myview.UploadPhotoPopupWindow;
import com.qlhui.thoughtlight.net.ThreadPoolUtils;
import com.qlhui.thoughtlight.thread.HttpPostThread;
import com.qlhui.thoughtlight.utils.MyJson;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainFragment extends BaseFragment implements OnClickListener {

	private View view;
	private MainTabActivity ctx;

	private final Handler handler = new Handler();
	private OnClickListener Mylistener;
	private PagerSlidingTabStrip tabs;
	private ViewPager pager;
	private MyPagerAdapter adapter;
	private UploadPhotoPopupWindow muploadPopWindow;
	// 热门的碎片
	private chuandaoFragment mHotFragment;
	// 精华的碎片
	private NewValueFragment mNewFragment;
	// 有图有真相的碎片
	private GongguoFragment mPictureFragment;
	// 穿越的碎片
	private HelpFragment mhelpFragment;
	// 附近的碎片
	private MingrenFragment mNearFragment;
	private RelativeLayout mMainpage;
	private BaijiaFragment mBaijiaFragment;
	private Drawable oldBackground = null;
	private int currentColor = 0xFF5161BC;
	private View mbottombar;
	private ImageView mradar;
	private SharedPreferences sp;  
	private MyJson myJson = new MyJson();
	//private String userNameValue,passwordValue;  
	private BJXUserInfo myinfo = null;


	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		   if (view == null)
		    {
			  view = inflater.inflate(R.layout.activity_mainpage, null);
			  ctx = (MainTabActivity) view.getContext();
		//	  myListView = new MyListView(ctx);
			  initView();
		    }
		    // 缓存的rootView需要判断是否已经被加过parent，如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
		    ViewGroup parent = (ViewGroup) view.getParent();
		    if (parent != null)
		    {
		      parent.removeView(view);
		    }
		
	/*	
		view = inflater.inflate(R.layout.activity_xiaozhitiao, null);
		ctx = view.getContext();
		initView();
		*/
		return view;
	}

	private void initView() {

		tabs = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
		pager = (ViewPager) view.findViewById(R.id.pager);
		adapter = new MyPagerAdapter(ctx.getSupportFragmentManager());
	//	pager = (ViewPager) view.findViewById(R.id.pager);
		mMainpage =(RelativeLayout)view.findViewById(R.id.mainpage_layout); 
		mHotFragment = new chuandaoFragment();
	
		mNewFragment = new NewValueFragment();
		
		mPictureFragment = new GongguoFragment();

		mhelpFragment = new HelpFragment();
	
		mNearFragment = new MingrenFragment();
		
		mBaijiaFragment = new BaijiaFragment();
		
		/* 注册 SDK 广播监听者
		IntentFilter iFilter = new IntentFilter();
		iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
		iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
		mReceiver = new SDKReceiver();
		registerReceiver(mReceiver, iFilter);
		*/
		pager.setAdapter(adapter);
		
		mradar = (ImageView) view.findViewById(R.id.bt_radar);
		mradar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
		    	Intent intent;
				intent = new Intent(ctx,
						NearbyActivity.class);

					startActivity(intent);
				
			}
		});
		
		mbottombar = view.findViewById(R.id.image_uploadbg);
		mbottombar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
	
	     Mylistener = new OnClickListener(){  
			        @Override  
			        public void onClick(View arg0) {  
			        	int mID = arg0.getId();
			        	Intent intent;
			    		Bundle bund = new Bundle();
			          switch (mID) {
						case R.id.image_photo:
							 intent = new Intent(ctx,
									UploadActivity.class);
					
							bund.putString("type", "chuandao");
							intent.putExtras(bund);
							startActivity(intent);
							muploadPopWindow.dismiss();
							break;
					
						case R.id.image_help:
							 intent = new Intent(ctx,
										UploadActivity.class);
					
							bund.putString("type", "shouye");
							intent.putExtras(bund);
							startActivity(intent);
							muploadPopWindow.dismiss();
							break;
						case R.id.image_close:
				
							muploadPopWindow.dismiss();
							break;	
						default:
							break;
					}
			        }         
			    };  
			    muploadPopWindow = new UploadPhotoPopupWindow(ctx,Mylistener);  
			    muploadPopWindow.showAtLocation(mMainpage, Gravity.CENTER, 0, 0);
			}
			
		});
		final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
				.getDisplayMetrics());
		pager.setPageMargin(pageMargin);
		
		tabs.setViewPager(pager);
		sp = ctx.getSharedPreferences("userInfo", Context.MODE_WORLD_READABLE);  
	
		if(!sp.getString("USER_NAME", "").equals(""))
		{
			String	url;
			if(sp.getBoolean("ISOPENID", false))
				url = Model.LOGINOPEN;
			else
				url = Model.LOGIN;
			String value = "{\"uname\":\"" + sp.getString("USER_NAME", "") + "\",\"upassword\":\""
					+ sp.getString("PASSWORD", "") + "\"}";
			Log.e("qianpengyu", value);
			ThreadPoolUtils.execute(new HttpPostThread(hand, url, value));
		}
	//	changeColor(currentColor);
	
	}


	Handler hand = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			if (msg.what == 404) {
				Toast.makeText(ctx, "请求失败，服务器故障", 1).show();
			} else if (msg.what == 100) {
				Toast.makeText(ctx, "服务器无响应", 1).show();
			} else if (msg.what == 200) {
				String result = (String) msg.obj;
				Log.e("qiangpengyu", result);
				if (result.equalsIgnoreCase("NOUSER")) {
					if(!sp.getBoolean("ISOPENID", false))
						Toast.makeText(ctx, "用户名不存在", 1).show();
					return;
				} else if (result.equalsIgnoreCase("NOPASS")) {
					Toast.makeText(ctx, "密码错误", 1).show();
					return;
				} else if (result != null) {
				//	Toast.makeText(ctx, "登录成功", 1).show();

					List<BJXUserInfo> newList = myJson.getNearUserList(result);
					if (newList != null) {
						Model.MYUSERINFO = newList.get(0);
					}
					/*
					Intent intent = new Intent(ctx,
							UserInfoActivity.class);
					Bundle bund = new Bundle();
					bund.putSerializable("UserInfo", Model.MYUSERINFO);
					intent.putExtra("value", bund);
					startActivity(intent);
					finish();
					*/
				}
			}
		};
	};
	@Override
	public void onResume()
	{
		super.onResume();
		ctx.setActionStyle("谈笑有鸿儒",true);
	}
	@Override
	public void onClick(View arg0) {

	}
	public class MyPagerAdapter extends FragmentPagerAdapter {

		private final String[] TITLES = { "传道", "解惑"};

		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return TITLES[position];
		}

		@Override
		public int getCount() {
			return TITLES.length;
		}

		@Override
		public Fragment getItem(int position) {
			Fragment currfragment = null;
			switch (position) {
			case 0:
				currfragment = mHotFragment;
				
				break;
			case 1:
				currfragment = mNewFragment;
				
				break;

			default:
				break;
			}
			//mbottombar.setVisibility(View.GONE);
			return currfragment;
		}

	}

	public interface NotesFragmentCallBack {
		public void callback(int flag);
	}

}
