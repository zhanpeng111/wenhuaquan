package com.qlhui.thoughtlight;


import java.util.List;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qlhui.thoughtlight.fragment.BaijiaFragment;
import com.qlhui.thoughtlight.fragment.CrossFragment;
import com.qlhui.thoughtlight.fragment.HelpFragment;
import com.qlhui.thoughtlight.fragment.chuandaoFragment;
import com.qlhui.thoughtlight.fragment.MingrenFragment;
import com.qlhui.thoughtlight.fragment.NewValueFragment;
import com.qlhui.thoughtlight.fragment.NotesFragment;
import com.qlhui.thoughtlight.fragment.GongguoFragment;
import com.qlhui.thoughtlight.info.BJXUserInfo;
import com.qlhui.thoughtlight.model.Model;
import com.qlhui.thoughtlight.myview.PagerSlidingTabStrip;
import com.qlhui.thoughtlight.myview.UploadPhotoPopupWindow;
import com.qlhui.thoughtlight.net.ThreadPoolUtils;
import com.qlhui.thoughtlight.thread.HttpGetThread;
import com.qlhui.thoughtlight.thread.HttpPostThread;
import com.qlhui.thoughtlight.utils.MyJson;

public class MainPage extends FragmentActivity {

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

	/**
	 * 构造广播监听类，监听 SDK key 验证以及网络异常广播
	
	public class SDKReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			String s = intent.getAction();
		//	Log.d(LTAG, "action: " + s);
		//	TextView text = (TextView) findViewById(R.id.text_Info);
		//	text.setTextColor(Color.RED);
			if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
				Toast.makeText(MainPage.this, "key 验证出错! ", 1).show();
				//text.setText("key 验证出错! 请在 AndroidManifest.xml 文件中检查 key 设置");
			} else if (s
					.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
				Toast.makeText(MainPage.this, "络出错", 1).show();
				//text.setText("网络出错");
			}
		}

	private SDKReceiver mReceiver;
		}
 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mainpage);

		tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		pager = (ViewPager) findViewById(R.id.pager);
		adapter = new MyPagerAdapter(getSupportFragmentManager());
		pager = (ViewPager) findViewById(R.id.pager);
		mMainpage =(RelativeLayout)findViewById(R.id.mainpage_layout); 
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
		
		mradar = (ImageView) findViewById(R.id.bt_radar);
		mradar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
		    	Intent intent;
				intent = new Intent(MainPage.this,
						NearbyActivity.class);

					startActivity(intent);
				
			}
		});
		
		mbottombar = findViewById(R.id.image_uploadbg);
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
							 intent = new Intent(MainPage.this,
									UploadActivity.class);
					
							bund.putString("type", "chuandao");
							intent.putExtras(bund);
							startActivity(intent);
							muploadPopWindow.dismiss();
							break;
						
						case R.id.image_help:
							 intent = new Intent(MainPage.this,
										UploadActivity.class);
					
							bund.putString("type", "jiehuo");
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
			    muploadPopWindow = new UploadPhotoPopupWindow(MainPage.this,Mylistener);  
			    muploadPopWindow.showAtLocation(mMainpage, Gravity.CENTER, 0, 0);
			}
			
		});
		final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
				.getDisplayMetrics());
		pager.setPageMargin(pageMargin);
		
		tabs.setViewPager(pager);
		sp = this.getSharedPreferences("userInfo", Context.MODE_WORLD_READABLE);  
		if(!sp.getString("USER_NAME", "").equals(""))
		{
			String	url = Model.LOGIN;
			String value = "{\"uname\":\"" + sp.getString("USER_NAME", "") + "\",\"upassword\":\""
					+ sp.getString("PASSWORD", "") + "\"}";
			Log.e("qianpengyu", value);
			ThreadPoolUtils.execute(new HttpPostThread(hand, url, value));
		}

		changeColor(currentColor);
	}
	
	Handler hand = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			if (msg.what == 404) {
				Toast.makeText(MainPage.this, "请求失败，服务器故障", 1).show();
			} else if (msg.what == 100) {
				Toast.makeText(MainPage.this, "服务器无响应", 1).show();
			} else if (msg.what == 200) {
				String result = (String) msg.obj;
				Log.e("qiangpengyu", result);
				if (result.equalsIgnoreCase("NOUSER")) {
			
					Toast.makeText(MainPage.this, "用户名不存在", 1).show();
					return;
				} else if (result.equalsIgnoreCase("NOPASS")) {
					Toast.makeText(MainPage.this, "密码错误", 1).show();
					return;
				} else if (result != null) {
					Toast.makeText(MainPage.this, "登录成功", 1).show();

					List<BJXUserInfo> newList = myJson.getNearUserList(result);
					if (newList != null) {
						Model.MYUSERINFO = newList.get(0);
					}
					/*
					Intent intent = new Intent(MainPage.this,
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
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case R.id.action_contact:
			if (Model.MYUSERINFO != null) {
				Intent intent = new Intent(MainPage.this,
						UserInfoActivity.class);
				Bundle bund = new Bundle();
				bund.putSerializable("UserInfo", Model.MYUSERINFO);
				intent.putExtra("value", bund);
				startActivity(intent);
			} else {
				Intent intent = new Intent(MainPage.this,
						LoginActivity.class);
				startActivity(intent);
			}
			return true;

		}

		return super.onOptionsItemSelected(item);
	}

	@SuppressLint("NewApi") private void changeColor(int newColor) {

		tabs.setIndicatorColor(newColor);

		// change ActionBar color just if an ActionBar is available
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

			Drawable colorDrawable = new ColorDrawable(newColor);
			Drawable bottomDrawable = getResources().getDrawable(R.drawable.actionbar_bottom);
			LayerDrawable ld = new LayerDrawable(new Drawable[] { colorDrawable, bottomDrawable });

			if (oldBackground == null) {

				if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
					ld.setCallback(drawableCallback);
				} else {
					getActionBar().setBackgroundDrawable(ld);
				}

			} else {

				TransitionDrawable td = new TransitionDrawable(new Drawable[] { oldBackground, ld });

				// workaround for broken ActionBarContainer drawable handling on
				// pre-API 17 builds
				// https://github.com/android/platform_frameworks_base/commit/a7cc06d82e45918c37429a59b14545c6a57db4e4
				if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
					td.setCallback(drawableCallback);
				} else {
					getActionBar().setBackgroundDrawable(td);
				}

				td.startTransition(200);

			}

			oldBackground = ld;

			// http://stackoverflow.com/questions/11002691/actionbar-setbackgrounddrawable-nulling-background-from-thread-handler
			getActionBar().setDisplayShowTitleEnabled(false);
			getActionBar().setDisplayShowTitleEnabled(true);

		}

		currentColor = newColor;

	}

	public void onColorClicked(View v) {

		int color = Color.parseColor(v.getTag().toString());
		changeColor(color);

	}
	public void showBottombar(boolean isshow) {
		Animation up = AnimationUtils.loadAnimation(this,
				R.anim.actionbar_up);
		// bar 向下的动画
		Animation down = AnimationUtils.loadAnimation(this,
				R.anim.actionbar_down);
		if(isshow)
		{
			if (mbottombar.getVisibility() == View.GONE) {
				mbottombar.startAnimation(up);
				mbottombar.setVisibility(View.VISIBLE);
			}
			
		}
		else
		{
			if (mbottombar.getVisibility() == View.VISIBLE) {
				mbottombar.startAnimation(down);
				mbottombar.setVisibility(View.GONE);
			}
		}

	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 取消监听 SDK 广播
		//unregisterReceiver(mReceiver);
	}
	@Override
	protected void onSaveInstanceState(Bundle outState) {
	//	super.onSaveInstanceState(outState);
	//	outState.putInt("currentColor", currentColor);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		currentColor = savedInstanceState.getInt("currentColor");
		changeColor(currentColor);
	}

	private Drawable.Callback drawableCallback = new Drawable.Callback() {
		@SuppressLint("NewApi") @Override
		public void invalidateDrawable(Drawable who) {
			getActionBar().setBackgroundDrawable(who);
		}

		@Override
		public void scheduleDrawable(Drawable who, Runnable what, long when) {
			handler.postAtTime(what, when);
		}

		@Override
		public void unscheduleDrawable(Drawable who, Runnable what) {
			handler.removeCallbacks(what);
		}
	};

	public class MyPagerAdapter extends FragmentPagerAdapter {

		private final String[] TITLES = { "传道解惑", "学以致用", "星文化", "星名人"};

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
			case 2:
				currfragment = mBaijiaFragment;//mhelpFragment
				break;
			case 3:
				currfragment = mNearFragment;
				break;
			case 4:
				currfragment =mNearFragment;
				break;
		
			default:
				break;
			}
			//mbottombar.setVisibility(View.GONE);
			return currfragment;
		}

	}

}