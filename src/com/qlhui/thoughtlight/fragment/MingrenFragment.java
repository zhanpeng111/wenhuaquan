package com.qlhui.thoughtlight.fragment;

import java.util.ArrayList;
import java.util.List;

import com.qlhui.thoughtlight.MainTabActivity;
import com.qlhui.thoughtlight.MingrenInfoActivity;
import com.qlhui.thoughtlight.R;
import com.qlhui.thoughtlight.UserInfoActivity;
import com.qlhui.thoughtlight.info.BJXUserInfo;
import com.qlhui.thoughtlight.model.Model;
import com.qlhui.thoughtlight.myview.MyListView;
import com.qlhui.thoughtlight.myview.MyListener;
import com.qlhui.thoughtlight.myview.PullToRefreshLayout;
import com.qlhui.thoughtlight.myview.MyListView.OnRefreshListener;
import com.qlhui.thoughtlight.myview.PullableListView;
import com.qlhui.thoughtlight.net.ThreadPoolUtils;
import com.qlhui.thoughtlight.thread.HttpGetThread;
import com.qlhui.thoughtlight.utils.MyJson;
import com.qlhui.thoughtlight.adapter.MingrenAdapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MingrenFragment extends BaseFragment implements OnClickListener {

	private NearFragmentCallBack mNearFragmentCallBack;
	private View view;
	private MainTabActivity ctx;
	private LinearLayout mLinearLayout, load_progressBar;
	private LinearLayout Near_Back;
	private ImageView Near_Seting, Near_More;
	private TextView HomeNoValue;
	private PullableListView myListView;
	private MingrenAdapter mAdapter = null;
	private MyJson myJson = new MyJson();
	private List<BJXUserInfo> list = new ArrayList<BJXUserInfo>();
	private Button ListBottem = null;
	private int mStart = 0;
	private int mEnd = 5;
	private String url = null;
	private boolean flag = true;
	private boolean loadflag = false;
	private boolean listBottemFlag = true;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		   if (view == null)
		    {
			  view = inflater.inflate(R.layout.activity_near, null);
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
	
		return view;
	}

	private void initView() {
		load_progressBar = (LinearLayout) view
				.findViewById(R.id.load_progressBar);
		
	
		Near_Back = (LinearLayout) view.findViewById(R.id.Near_Back);
		Near_Seting = (ImageView) view.findViewById(R.id.Near_Seting);
		Near_More = (ImageView) view.findViewById(R.id.Near_More);
		HomeNoValue = (TextView) view.findViewById(R.id.HomeNoValue);
		Near_Back.setOnClickListener(this);
		Near_Seting.setOnClickListener(this);
		Near_More.setOnClickListener(this);
		mAdapter = new MingrenAdapter(ctx, list);
		mAdapter.setImgloader(imageLoader);
	
	//	myListView.addFooterView(ListBottem, null, false);
	//	ListBottem.setVisibility(View.GONE);
	
		url = Model.MINGRENILIST + "start=" + mStart + "&end=" + mEnd;
		ThreadPoolUtils.execute(new HttpGetThread(hand, url));
		mPullToRefreshView = (PullToRefreshLayout)view.findViewById(R.id.refresh_view);
		mPullToRefreshView.setOnRefreshListener(new MyListener()
		{

			public void onRefresh(final PullToRefreshLayout pullToRefreshLayout)
			{
				if (loadflag == true) {
					mStart = 0;
					mEnd = 10;
					url =  Model.MINGRENILIST + "start=" + mStart + "&end=" + mEnd;
			//		ListBottem.setVisibility(View.GONE);
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
		myListView =(PullableListView) view.findViewById(R.id.bjpulllist);
		myListView.setAdapter(mAdapter);
		myListView.setOnItemClickListener(new MainListOnItemClickListener());
	}

	@Override
	public void onClick(View arg0) {
		int mID = arg0.getId();
		switch (mID) {
		case R.id.Near_Back:
			mNearFragmentCallBack.callback(R.id.Near_Back);
			break;
		case R.id.Near_Seting:
			mNearFragmentCallBack.callback(R.id.Near_Seting);
			break;
		case R.id.Near_More:
			mNearFragmentCallBack.callback(R.id.Near_More);
			break;
		default:
			break;
		}

	}

	private class MainListOnItemClickListener implements OnItemClickListener {
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			Intent intent = new Intent(ctx, MingrenInfoActivity.class);
			Bundle bund = new Bundle();
			bund.putSerializable("UserInfo", list.get(arg2));
			intent.putExtra("value", bund);
			startActivity(intent);
		}
	}

	public void setCallBack(NearFragmentCallBack mNearFragmentCallBack) {
		this.mNearFragmentCallBack = mNearFragmentCallBack;
	}

	public interface NearFragmentCallBack {
		public void callback(int flag);
	}
	@Override
	public void onResume()
	{
		super.onResume();
		ctx.setActionStyle("往来无白丁",false);
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
					List<BJXUserInfo> newList = myJson.getNearUserList(result);
					if (newList != null) {
						Log.e("liuxiaowei", "newList=" + newList.size()
								+ "  list=" + list.size());
						if (newList.size() == 5) {
					//		ListBottem.setVisibility(View.VISIBLE);
							mStart += 5;
							mEnd += 5;
						} else if (newList.size() == 0) {
							if (list.size() == 0)
								HomeNoValue.setVisibility(View.VISIBLE);
					//		ListBottem.setVisibility(View.GONE);
							Toast.makeText(ctx, "已经没有了...", 1).show();
						} else {
				//			ListBottem.setVisibility(View.GONE);
						}
						if (!loadflag) {
							list.removeAll(list);
						}
						for (BJXUserInfo info : newList) {
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
			//	myListView.onRefreshComplete();
				mAdapter.notifyDataSetChanged();
				loadflag = true;
			}
		};
	};
}
