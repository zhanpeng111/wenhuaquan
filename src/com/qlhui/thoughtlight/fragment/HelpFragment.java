package com.qlhui.thoughtlight.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.qlhui.thoughtlight.AshamedDetailActivity;
import com.qlhui.thoughtlight.MainPage;
import com.qlhui.thoughtlight.R;
import com.qlhui.thoughtlight.info.AshamedInfo;
import com.qlhui.thoughtlight.model.Model;
import com.qlhui.thoughtlight.myview.MyListener;
import com.qlhui.thoughtlight.myview.PullToRefreshLayout;
import com.qlhui.thoughtlight.myview.PullableListView;
import com.qlhui.thoughtlight.myview.PullableListView.OnLoadListener;
import com.qlhui.thoughtlight.net.ThreadPoolUtils;
import com.qlhui.thoughtlight.thread.HttpGetThread;
import com.qlhui.thoughtlight.utils.MyJson;
import com.qlhui.thoughtlight.adapter.ArticleListAdapter;

/**
 * 最新的fragment
 * */
public class HelpFragment extends BaseFragment implements OnClickListener,OnLoadListener {

	private String newUrl = Model.HELP;
	private int topMeunFlag = 1;
	private View view;
	private ImageView mTopImg;
	private ImageView mSendAshamed;
	private TextView mTopMenuOne, mTopMenuTwo, mTopMenuThree;
	private PullableListView mypulllist;
	private PullToRefreshLayout mPullToRefreshView;
	private LinearLayout mLinearLayout, load_progressBar;
	private TextView HomeNoValue;
	private NiceFragmentCallBack mNiceFragmentCallBack;
	private MyJson myJson = new MyJson();
	private List<AshamedInfo> list = new ArrayList<AshamedInfo>();
	private ArticleListAdapter mAdapter = null;
	private Button ListBottem = null;
	private int mStart = 0;
	private int mEnd = 5;
	private String url = null;
	private boolean flag = true;
	private boolean isCanpullfoot = true;
	private boolean loadflag = false;
	private boolean listBottemFlag = true;
	private MainPage ctx;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		   if (view == null)
		    {
			  view = inflater.inflate(R.layout.frame_nice, null);
			  ctx = (MainPage) view.getContext();
			 //  mypulllist = new mypulllist(ctx);
		
			  initView();
		    }
		    // 缓存的rootView需要判断是否已经被加过parent，如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
		    ViewGroup parent = (ViewGroup) view.getParent();
		    if (parent != null)
		    {
		      parent.removeView(view);
		    }
	
		return view;
	}

	private void initView() {
		load_progressBar = (LinearLayout) view
				.findViewById(R.id.load_progressBar);
		mPullToRefreshView = (PullToRefreshLayout)view.findViewById(R.id.refresh_view);
		mPullToRefreshView.setOnRefreshListener(new MyListener()
		{

			public void onRefresh(final PullToRefreshLayout pullToRefreshLayout)
			{
				if (loadflag == true) {
					mStart = 0;
					mEnd = 10;
					url = newUrl + "start=" + mStart + "&end=" + mEnd;
					ListBottem.setVisibility(View.GONE);
					ThreadPoolUtils.execute(new HttpGetThread(hand, url));
					loadflag = false;
				} else {
					Toast.makeText(ctx, "正在加载中，请勿重复刷新", 1).show();
				}
				mPullToRefreshView.postDelayed(new Runnable() {
					
					@Override
					public void run() {	
						mPullToRefreshView.refreshFinish(PullToRefreshLayout.SUCCEED);
					}
				},1000);
			
			}

		});;
		mypulllist = (PullableListView)view.findViewById(R.id.pulllist);
		mypulllist.setOnLoadListener(this);

		mTopImg = (ImageView) view.findViewById(R.id.Menu);
		mSendAshamed = (ImageView) view.findViewById(R.id.SendAshamed);
		mTopMenuOne = (TextView) view.findViewById(R.id.TopMenuOne);
		mTopMenuTwo = (TextView) view.findViewById(R.id.TopMenuTwo);
		mTopMenuThree = (TextView) view.findViewById(R.id.TopMenuThree);
		HomeNoValue = (TextView) view.findViewById(R.id.HomeNoValue);
		mTopImg.setOnClickListener(this);
		mSendAshamed.setOnClickListener(this);
		mTopMenuOne.setOnClickListener(this);
		mTopMenuTwo.setOnClickListener(this);
		mTopMenuThree.setOnClickListener(this);
		createTextColor();
		switch (topMeunFlag) {
		case 1:
			mTopMenuOne.setTextColor(Color.WHITE);
			mTopMenuOne.setBackgroundResource(R.drawable.top_tab_active);
			break;
		case 2:
			mTopMenuTwo.setTextColor(Color.WHITE);
			mTopMenuTwo.setBackgroundResource(R.drawable.top_tab_active);
			break;
		case 3:
			mTopMenuThree.setTextColor(Color.WHITE);
			mTopMenuThree.setBackgroundResource(R.drawable.top_tab_active);
			break;
		}
		mAdapter = new ArticleListAdapter(ctx, list);
		mAdapter.setImgloader(imageLoader);
		ListBottem = new Button(ctx);
		ListBottem.setText("点击加载更多");
		ListBottem.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (flag && listBottemFlag) {
					url = newUrl + "start=" + mStart + "&end=" + mEnd;
					ThreadPoolUtils.execute(new HttpGetThread(hand, url));
					listBottemFlag = false;
				} else if (!listBottemFlag)
					Toast.makeText(ctx, "加载中请稍候...", 1).show();
			}
		});
	//	mypulllist.addFooterView(ListBottem, null, false);
		ListBottem.setVisibility(View.GONE);
		mypulllist.setAdapter(mAdapter);
		mypulllist.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				//View view2 = findViewById(R.id.bottom_bar);
				// bar 向上的动画
			
				// 记录点击时 y 的坐标
				int y = (int) event.getY();
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					// 第一次点击是 ACTION_DOWN 事件，把值保存起来
					mMotionY = y;
					break;
				case MotionEvent.ACTION_MOVE:
					// 当你滑动屏幕时是 ACTION_MOVE 事件，在这里做逻辑处理
					// （y - mMotionY） 的正负就代表了 向上和向下
					if ((y - mMotionY) > 20) {
						ctx.showBottombar(true);
					} else if((y - mMotionY) < -20)
					{
						ctx.showBottombar(false);
					
						}
					
					mMotionY = y;
					break;
				}
				return false;
			}
		});
	
		mypulllist.setOnItemClickListener(new MainListOnItemClickListener());
		url = newUrl+ "start=" + mStart + "&end=" + mEnd;
		ThreadPoolUtils.execute(new HttpGetThread(hand, url));
		/*
		mypulllist.setonRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				if (loadflag == true) {
					mStart = 0;
					mEnd = 5;
					url = newUrl + "start=" + mStart + "&end=" + mEnd;
					ListBottem.setVisibility(View.GONE);
					ThreadPoolUtils.execute(new HttpGetThread(hand, url));
					loadflag = false;
				} else {
					Toast.makeText(ctx, "正在刷新，请勿重复刷新", 1).show();
				}
			}
		});
		*/
	}

	@Override
	public void onClick(View arg0) {
		int mID = arg0.getId();
		switch (mID) {
		case R.id.Menu:
			mNiceFragmentCallBack.callback(R.id.Menu);
			break;
		case R.id.SendAshamed:
			mNiceFragmentCallBack.callback(R.id.SendAshamed);
			break;
		case R.id.TopMenuOne:
			createTextColor();
			mTopMenuOne.setTextColor(Color.WHITE);
			mTopMenuOne.setBackgroundResource(R.drawable.top_tab_active);
			if (topMeunFlag != 1) {
				newUrl = Model.NEW;
				topMeunFlag = 1;
				createListModel();
			}
			break;
		case R.id.TopMenuTwo:
			createTextColor();
			mTopMenuTwo.setTextColor(Color.WHITE);
			mTopMenuTwo.setBackgroundResource(R.drawable.top_tab_active);
			if (topMeunFlag != 2) {
				newUrl = Model.ZHOU;
				topMeunFlag = 2;
				createListModel();
			}
			break;
		case R.id.TopMenuThree:
			createTextColor();
			mTopMenuThree.setTextColor(Color.WHITE);
			mTopMenuThree.setBackgroundResource(R.drawable.top_tab_active);
			if (topMeunFlag != 3) {
				newUrl = Model.YUE;
				topMeunFlag = 3;
				createListModel();
			}
			break;
		default:
			break;
		}
	}

	private void createListModel() {
		ListBottem.setVisibility(View.GONE);
		mLinearLayout.setVisibility(View.GONE);
		load_progressBar.setVisibility(View.VISIBLE);
		loadflag = false;
		mStart = 0;
		mEnd = 5;
		url = newUrl + "start=" + mStart + "&end=" + mEnd;
		ThreadPoolUtils.execute(new HttpGetThread(hand, url));
	}

	private class MainListOnItemClickListener implements OnItemClickListener {
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			GoAshamedDetail(arg2);
		}
	}
	public void GoAshamedDetail(int arg2)
	{
		Intent intent = new Intent(ctx, AshamedDetailActivity.class);
		Bundle bund = new Bundle();
		bund.putSerializable("AshamedInfo", list.get(arg2));
		intent.putExtra("value", bund);
		startActivity(intent);
		
	}
	@SuppressWarnings("deprecation")
	private void createTextColor() {
		Drawable background = new BitmapDrawable();
		mTopMenuOne.setTextColor(Color.parseColor("#815F3D"));
		mTopMenuTwo.setTextColor(Color.parseColor("#815F3D"));
		mTopMenuThree.setTextColor(Color.parseColor("#815F3D"));
		mTopMenuOne.setBackgroundDrawable(background);
		mTopMenuTwo.setBackgroundDrawable(background);
		mTopMenuThree.setBackgroundDrawable(background);
		HomeNoValue.setVisibility(View.GONE);
	}

	public void setCallBack(NiceFragmentCallBack mNiceFragmentCallBack) {
		this.mNiceFragmentCallBack = mNiceFragmentCallBack;
	}

	public interface NiceFragmentCallBack {
		public void callback(int flag);
	}

	Handler hand = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			if (msg.what == 404) {
				Toast.makeText(ctx, "找不到地址", 1).show();
				listBottemFlag = true;
			} else if (msg.what == 100) {
				Toast.makeText(ctx, "传输失败", 1).show();
				listBottemFlag = true;
			} else if (msg.what == 200) {
				String result = (String) msg.obj;
				if (result != null) {
					List<AshamedInfo> newList = myJson.getAshamedList(result);
					if (newList != null) {
						if (newList.size() == 10) {
							ListBottem.setVisibility(View.VISIBLE);
							mStart += 10;
							mEnd += 10;
							isCanpullfoot = true;
						} else if (newList.size() == 0) {
							if (list.size() == 0)
								HomeNoValue.setVisibility(View.VISIBLE);
							ListBottem.setVisibility(View.GONE);
							isCanpullfoot = false;
							Toast.makeText(ctx, "已经没有了...", 1).show();
						} else {
							isCanpullfoot = false;
							ListBottem.setVisibility(View.GONE);
						}
						if (!loadflag) {
							list.removeAll(list);
						}
						for (AshamedInfo info : newList) {
							list.add(info);
						}
						listBottemFlag = true;
					} else {
						if (list.size() == 0)
							HomeNoValue.setVisibility(View.VISIBLE);
					}
				}
			//	mLinearLayout.setVisibility(View.VISIBLE);
				load_progressBar.setVisibility(View.GONE);
			//	mypulllist.onRefreshComplete();
				mAdapter.notifyDataSetChanged();
				loadflag = true;
			}
		};
	};
	@Override  
	   public void setUserVisibleHint(boolean isVisibleToUser) {  
	       super.setUserVisibleHint(isVisibleToUser);  
	       if (isVisibleToUser) {  
	    	   if(ctx!=null)
	    		   ctx.showBottombar(false);
	       } else {  
	   	//	ctx.showBottombar(false);
	       }  
	   }

	@Override
	public void onLoad(PullableListView pullableListView) {
		// TODO Auto-generated method stub
		new Handler()
		{
			@Override
			public void handleMessage(Message msg)
			{
				if(isCanpullfoot)
				{
					if (flag && listBottemFlag) {
						url = newUrl + "start=" + mStart + "&end=" + mEnd;
						ThreadPoolUtils.execute(new HttpGetThread(hand, url));
						listBottemFlag = false;
					} else if (!listBottemFlag)
						Toast.makeText(ctx, "正在加载中...", 1).show();
				}
				// 千万别忘了告诉控件加载完毕了哦！
				mypulllist.finishLoading();
		//		if(!isCanpullfoot)
		//			Toast.makeText(ctx, "已经没有了...", 1).show();
			}
		}.sendEmptyMessageDelayed(0, 1000);
	}  
}
