package com.qlhui.thoughtlight;

import com.qlhui.suishouji.CommonData;
import com.qlhui.suishouji.db.MyDbHelper;
import com.qlhui.thoughtlight.fragment.BaijiaFragment;
import com.qlhui.thoughtlight.fragment.MainFragment;
import com.qlhui.thoughtlight.fragment.MingrenFragment;
import com.qlhui.thoughtlight.fragment.GongguoFragment;
import com.qlhui.thoughtlight.fragment.UserinfoFragment;
import com.qlhui.thoughtlight.info.BJXUserInfo;
import com.qlhui.thoughtlight.model.Model;
import com.qlhui.thoughtlight.myview.UploadPhotoPopupWindow;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

/**
 * @author yangyu
 *	功能描述：自定义TabHost
 */
public class MainTabActivity extends FragmentActivity{  	
	//定义FragmentTabHost对象
	private FragmentTabHost mTabHost;
	private final Handler handler = new Handler();
	private OnClickListener Mylistener;
	//定义一个布局
	private LayoutInflater layoutInflater;
	private TextView mActionbartitle;
	private RelativeLayout mBottombar;
	private LinearLayout mroot;
	private ImageButton mUploadbt;
	private UploadPhotoPopupWindow muploadPopWindow;
	public static MyDbHelper db = null;
	//定义数组来存放Fragment界面
	private Class fragmentArray[] = {MainFragment.class,MingrenFragment.class,GongguoFragment.class,BaijiaFragment.class,UserinfoFragment.class};
	
	//定义数组来存放按钮图片
	private int mnormalViewArray[] = {R.drawable.ic_tab_index_normal,R.drawable.ic_tab_search_normal,R.drawable.ic_tab_publish_normal,
									 R.drawable.ic_tab_message_normal,R.drawable.ic_tab_profile_normal};
	//定义数组来存放按钮图片
		private int mpressViewArray[] = {R.drawable.ic_tab_index_pressed,R.drawable.ic_tab_search_pressed,R.drawable.ic_tab_publish_pressed,
										 R.drawable.ic_tab_message_pressed,R.drawable.ic_tab_profile_pressed};
	//Tab选项卡的文字
	private String mTextviewArray[] = {"首页", "星文化", "好友", "附近", "我的"};
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_tab_layout);
        initialDBData();
        CommonData.getInstance().load(this);
        initView();
    }
	 
	/**
	 * 初始化组件
	 */
	private void initView(){
		// 自定义actionbar的布局
		setActionBarLayout( R.layout.actionbar_port_layout );
	
		//实例化布局对象
		layoutInflater = LayoutInflater.from(this);
		mActionbartitle = (TextView)findViewById(R.id.Title);
		mUploadbt = (ImageButton)findViewById(R.id.noteBtnId);
		
		setActionStyle("文化驿站",true);
	//	mroot = (LinearLayout)findViewById(R.id.root);
		//实例化TabHost对象，得到TabHost
		mTabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
		mBottombar = (RelativeLayout)findViewById(R.id.tabwidget);
		
		mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);	
		mTabHost.setOnTabChangedListener(new OnTabChangedListener()); // 选择监听器  
		//得到fragment的个数
		int count = fragmentArray.length;	
				
		for(int i = 0; i < count; i++){	
			//为每一个Tab按钮设置图标、文字和内容
			TabSpec tabSpec = mTabHost.newTabSpec(mTextviewArray[i]).setIndicator(getTabItemView(i));
			//将Tab按钮添加进Tab选项卡中
			mTabHost.addTab(tabSpec, fragmentArray[i], null);
		//	mTabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.ic_tab_index_pressed);
		//	mTabHost.getTabWidget().getChildAt(i).set
		}
		//设置Tab按钮的背景
	//	mTabHost.getTabWidget().getChildAt(0).setBackgroundResource(R.drawable.selector_tab_background);

	}
	 class OnTabChangedListener implements OnTabChangeListener { 
		 
		 
	        @Override 
	        public void onTabChanged(String tabId) { 
	        //	int count = fragmentArray.length;	
	        	int j = mTabHost.getCurrentTab();
	    		for(int i = 0; i < mTabHost.getTabWidget().getChildCount(); i++){	   
    				ImageView imageView = (ImageView) mTabHost.getTabWidget().getChildAt(i).findViewById(R.id.navi_icon);

	    			if(i == j)
	    			{
			    		imageView.setImageResource(mpressViewArray[i]);
	    				
	    			}
	    			else
	    			{
	    				
	    				imageView.setImageResource(mnormalViewArray[i]);
	    			}
	    		
	    		}
	    		System.out.println("tabid " + tabId); 
	        } 
	    } 
	 
	 /**
		 * 设置ActionBar的标题
		 * @param title 标题string
		 * 
		 * */
	   public void setActionStyle( String title,boolean isshowaction ){
		   mActionbartitle.setText(title);
		   if(isshowaction)
			   mUploadbt.setVisibility(View.VISIBLE);
		   else
			   mUploadbt.setVisibility(View.GONE);
		}
	/**
	 * 设置ActionBar的布局
	 * @param layoutId 布局Id
	 * 
	 * */
	@SuppressLint("NewApi") public void setActionBarLayout( int layoutId ){
		ActionBar actionBar = getActionBar( );
		if( null != actionBar ){
			actionBar.setDisplayShowHomeEnabled( false );
			actionBar.setDisplayShowCustomEnabled(true);

			LayoutInflater inflator = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View v = inflator.inflate(layoutId, null);
			ActionBar.LayoutParams layout = new ActionBar.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
			actionBar.setCustomView(v,layout);
		}
	}
	/**
	 * 实现onClick方法，在这里面监听actionbar中按钮的点击事件 
	 * 
	 * */
	public void onClick( View v ){
		switch( v.getId( ) ){
			case R.id.noteBtnId:{
	
			
			     Mylistener = new OnClickListener(){  
					        @Override  
					        public void onClick(View arg0) {  
					        	int mID = arg0.getId();
					        	Intent intent;
					    		Bundle bund = new Bundle();
					          switch (mID) {
								case R.id.image_photo:
									 intent = new Intent(MainTabActivity.this,
											UploadActivity.class);
							
									bund.putString("type", "chuandao");
									intent.putExtras(bund);
									startActivity(intent);
									muploadPopWindow.dismiss();
									break;
								/*
								case R.id.image_cross:
									 intent = new Intent(MainTabActivity.this,
												UploadActivity.class);
							
									bund.putString("type", "shouye");
									intent.putExtras(bund);
									startActivity(intent);
							//		Toast.makeText(MainTabActivity.this, "即将推出敬请期待", 1).show();
									muploadPopWindow.dismiss();
									break;
									*/
								case R.id.image_help:
									 intent = new Intent(MainTabActivity.this,
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
					    muploadPopWindow = new UploadPhotoPopupWindow(MainTabActivity.this,Mylistener);  
					    muploadPopWindow.showAtLocation(mTabHost, Gravity.CENTER, 0, 0);
			
			}
			break;
			case R.id.userinfobt:
				if (Model.MYUSERINFO != null) {
					Intent intent = new Intent(MainTabActivity.this,
							UserInfoActivity.class);
					Bundle bund = new Bundle();
					bund.putSerializable("UserInfo", Model.MYUSERINFO);
					intent.putExtra("value", bund);
					startActivity(intent);
				} else {
					Intent intent = new Intent(MainTabActivity.this,
							LoginActivity.class);
					startActivity(intent);
				}
				break;
			default:{
				
			}
			break;
		}
	}
	public void showBottombar(boolean isshow) {
		Animation up = AnimationUtils.loadAnimation(this,
				R.anim.actionbar_up);
		// bar 向下的动画
		Animation down = AnimationUtils.loadAnimation(this,
				R.anim.actionbar_down);
		if(isshow)
		{
			if (mBottombar.getVisibility() == View.GONE) {
				mBottombar.startAnimation(up);
				mBottombar.setVisibility(View.VISIBLE);
			}
			
		}
		else
		{
			if (mBottombar.getVisibility() == View.VISIBLE) {
				mBottombar.startAnimation(down);
				mBottombar.setVisibility(View.GONE);
			}
		}

	}	

	private void initialDBData() {
		// 建立数据库
//		deleteDatabase("mydb");
		db = MyDbHelper.getInstance(this.getApplicationContext());
		Resources res = this.getResources();
		db.open();
		Cursor cursor = db.select("TBL_EXPENDITURE_CATEGORY", new String[] {
				"ID", "NAME", "BUDGET" }, null, null, null, null, null);
	
		if (cursor.moveToNext()) {
			cursor.close();
//			db.close();
			return;
		}
		// 插入支出类别
		String[] arr = res.getStringArray(R.array.TBL_EXPENDITURE_CATEGORY);
		for (int i = 0; i < arr.length; i++) {
			Log.i("TEST", arr[i]);
			db.insert("TBL_EXPENDITURE_CATEGORY", new String[] { "NAME",
					"BUDGET" }, new String[] { arr[i], "0" });

		}
		// 插入支出子类别
		arr = res.getStringArray(R.array.TBL_EXPENDITURE_SUB_CATEGORY_1);
		for (int i = 0; i < arr.length; i++) {
			Log.i("TEST", arr[i]);
			db.insert("TBL_EXPENDITURE_SUB_CATEGORY", new String[] { "NAME",
					"PARENT_CATEGORY_ID" }, new String[] { arr[i], "1" });

		}
		arr = res.getStringArray(R.array.TBL_EXPENDITURE_SUB_CATEGORY_2);
		for (int i = 0; i < arr.length; i++) {
			Log.i("TEST", arr[i]);
			db.insert("TBL_EXPENDITURE_SUB_CATEGORY", new String[] { "NAME",
					"PARENT_CATEGORY_ID" }, new String[] { arr[i], "2" });

		}
		arr = res.getStringArray(R.array.TBL_EXPENDITURE_SUB_CATEGORY_3);
		for (int i = 0; i < arr.length; i++) {
			Log.i("TEST", arr[i]);
			db.insert("TBL_EXPENDITURE_SUB_CATEGORY", new String[] { "NAME",
					"PARENT_CATEGORY_ID" }, new String[] { arr[i], "3" });

		}
		arr = res.getStringArray(R.array.TBL_EXPENDITURE_SUB_CATEGORY_4);
		for (int i = 0; i < arr.length; i++) {
			Log.i("TEST", arr[i]);
			db.insert("TBL_EXPENDITURE_SUB_CATEGORY", new String[] { "NAME",
					"PARENT_CATEGORY_ID" }, new String[] { arr[i], "4" });

		}
		arr = res.getStringArray(R.array.TBL_EXPENDITURE_SUB_CATEGORY_5);
		for (int i = 0; i < arr.length; i++) {
			Log.i("TEST", arr[i]);
			db.insert("TBL_EXPENDITURE_SUB_CATEGORY", new String[] { "NAME",
					"PARENT_CATEGORY_ID" }, new String[] { arr[i], "5" });

		}
		arr = res.getStringArray(R.array.TBL_EXPENDITURE_SUB_CATEGORY_6);
		for (int i = 0; i < arr.length; i++) {
			Log.i("TEST", arr[i]);
			db.insert("TBL_EXPENDITURE_SUB_CATEGORY", new String[] { "NAME",
					"PARENT_CATEGORY_ID" }, new String[] { arr[i], "6" });

		}
		arr = res.getStringArray(R.array.TBL_EXPENDITURE_SUB_CATEGORY_7);
		for (int i = 0; i < arr.length; i++) {
			Log.i("TEST", arr[i]);
			db.insert("TBL_EXPENDITURE_SUB_CATEGORY", new String[] { "NAME",
					"PARENT_CATEGORY_ID" }, new String[] { arr[i], "7" });
		}
		arr = res.getStringArray(R.array.TBL_EXPENDITURE_SUB_CATEGORY_8);
		for (int i = 0; i < arr.length; i++) {
			Log.i("TEST", arr[i]);
			db.insert("TBL_EXPENDITURE_SUB_CATEGORY", new String[] { "NAME",
					"PARENT_CATEGORY_ID" }, new String[] { arr[i], "8" });
		}
		arr = res.getStringArray(R.array.TBL_EXPENDITURE_SUB_CATEGORY_9);
		for (int i = 0; i < arr.length; i++) {
			Log.i("TEST", arr[i]);
			db.insert("TBL_EXPENDITURE_SUB_CATEGORY", new String[] { "NAME",
					"PARENT_CATEGORY_ID" }, new String[] { arr[i], "9" });

		}

		arr = res.getStringArray(R.array.TBL_EXPENDITURE_SUB_CATEGORY_10);
		for (int i = 0; i < arr.length; i++) {
			Log.i("TEST", arr[i]);
			db.insert("TBL_EXPENDITURE_SUB_CATEGORY", new String[] { "NAME",
					"PARENT_CATEGORY_ID" }, new String[] { arr[i], "10" });

		}
		arr = res.getStringArray(R.array.TBL_EXPENDITURE_SUB_CATEGORY_11);
		for (int i = 0; i < arr.length; i++) {
			Log.i(" TEST", arr[i]);
			db.insert("TBL_EXPENDITURE_SUB_CATEGORY", new String[] { "NAME",
					"PARENT_CATEGORY_ID" }, new String[] { arr[i], "11" });

		}
		// 插入收入类别
		arr = res.getStringArray(R.array.TBL_INCOME_CATEGORY);
		for (int i = 0; i < arr.length; i++) {
			Log.i("TEST", arr[i]);
			db.insert("TBL_INCOME_CATEGORY", new String[] { "NAME" },
					new String[] { arr[i] });

		}
		// 插入子收入类别
		arr = res.getStringArray(R.array.TBL_INCOME_SUB_CATEGORY_1);
		for (int i = 0; i < arr.length; i++) {
			Log.i("TEST", arr[i]);
			db.insert("TBL_INCOME_SUB_CATEGORY", new String[] { "NAME",
					"PARENT_CATEGORY_ID" }, new String[] { arr[i], "1" });
		}
		arr = res.getStringArray(R.array.TBL_INCOME_SUB_CATEGORY_2);
		for (int i = 0; i < arr.length; i++) {
			Log.i("TEST", arr[i]);
			db.insert("TBL_INCOME_SUB_CATEGORY", new String[] { "NAME",
					"PARENT_CATEGORY_ID" }, new String[] { arr[i], "2" });

		}

		// 插入账户类别
		arr = res.getStringArray(R.array.TBL_ACCOUNT_TYPE);
		for (int i = 0; i < arr.length; i++) {
			Log.i("TEST", arr[i]);
			db.insert("TBL_ACCOUNT_TYPE", new String[] { "NAME", "POSTIVE" },
					new String[] { arr[i].substring(0, arr[i].indexOf(",")),
							arr[i].substring(arr[i].indexOf(",") + 1) });

		}
		// 插入账户子类别
		arr = res.getStringArray(R.array.TBL_ACCOUNT_SUB_TYPE_1);
		for (int i = 0; i < arr.length; i++) {
			Log.i("TEST", arr[i]);
			db.insert("TBL_ACCOUNT_SUB_TYPE", new String[] { "NAME",
					"PARENT_TYPE_ID" }, new String[] { arr[i], "1" });

		}
		arr = res.getStringArray(R.array.TBL_ACCOUNT_SUB_TYPE_2);
		for (int i = 0; i < arr.length; i++) {
			Log.i("TEST", arr[i]);
			db.insert("TBL_ACCOUNT_SUB_TYPE", new String[] { "NAME",
					"PARENT_TYPE_ID" }, new String[] { arr[i], "2" });

		}
		arr = res.getStringArray(R.array.TBL_ACCOUNT_SUB_TYPE_3);
		for (int i = 0; i < arr.length; i++) {
			Log.i("TEST", arr[i]);
			db.insert("TBL_ACCOUNT_SUB_TYPE", new String[] { "NAME",
					"PARENT_TYPE_ID" }, new String[] { arr[i], "3" });

		}
		arr = res.getStringArray(R.array.TBL_ACCOUNT_SUB_TYPE_4);
		for (int i = 0; i < arr.length; i++) {
			Log.i("TEST", arr[i]);
			db.insert("TBL_ACCOUNT_SUB_TYPE", new String[] { "NAME",
					"PARENT_TYPE_ID" }, new String[] { arr[i], "4" });

		}
		arr = res.getStringArray(R.array.TBL_ACCOUNT_SUB_TYPE_5);
		for (int i = 0; i < arr.length; i++) {
			Log.i("TEST", arr[i]);
			db.insert("TBL_ACCOUNT_SUB_TYPE", new String[] { "NAME",
					"PARENT_TYPE_ID" }, new String[] { arr[i], "5" });

		}
		// 插入账户
		arr = res.getStringArray(R.array.TBL_ACCOUNT);
		for (int i = 0; i < arr.length; i++) {
			Log.i("TEST", arr[i]);
			db.insert("TBL_ACCOUNT", new String[] { "NAME", "TYPE_ID",
					"SUB_TYPE_ID", "ACCOUNT_BALANCE" }, arr[i].split(","));
		}
		// 插入商家
		arr = res.getStringArray(R.array.TBL_STORE);
		for (int i = 0; i < arr.length; i++) {
			Log.i("TEST", arr[i]);
			db.insert("TBL_STORE", new String[] { "NAME" },
					new String[] { arr[i] });
		}
		// 插入项目
		arr = res.getStringArray(R.array.TBL_ITEM);
		for (int i = 0; i < arr.length; i++) {
			Log.i("TEST", arr[i]);
			db.insert("TBL_ITEM", new String[] { "NAME" },
					new String[] { arr[i] });
		}
		/*
		 * cursor =db.select("TBL_EXPENDITURE_CATEGORY", new
		 * String[]{"ID","NAME","BUDGET"}, null, null, null, null, null);
		 * while(cursor.moveToNext()) {
		 * Log.i("TBL_EXPENDITURE_CATEGORY",cursor.getString
		 * (0)+","+cursor.getString(1)); }
		 */
//		db.close();

	}
	@Override
	public void onResume()
	{
		super.onResume();
		setActionStyle("文化驿站",true);
	}
	private BJXUserInfo getUserinfo()
	{
		
		return Model.MYUSERINFO;
	}
	
	/**
	 * 给Tab按钮设置图标和文字
	 */
	private View getTabItemView(int index){
		View view = layoutInflater.inflate(R.layout.tab_item_view, null);
	
		ImageView imageView = (ImageView) view.findViewById(R.id.navi_icon);
		imageView.setImageResource(mnormalViewArray[index]);
		
		//TextView textView = (TextView) view.findViewById(R.id.textview);		
	//	textView.setText(mTextviewArray[index]);
	
		return view;
	}

	
}
