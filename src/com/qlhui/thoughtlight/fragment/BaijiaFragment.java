package com.qlhui.thoughtlight.fragment;

import java.util.ArrayList;
import java.util.List;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.qlhui.thoughtlight.BaijiaActivity;
import com.qlhui.thoughtlight.ForumDetailActivity;
import com.qlhui.thoughtlight.MainTabActivity;
import com.qlhui.thoughtlight.R;
import com.qlhui.thoughtlight.UserInfoActivity;
import com.qlhui.thoughtlight.info.BaijiaInfo;
import com.qlhui.thoughtlight.info.BaijiaInfo;
import com.qlhui.thoughtlight.model.Model;
import com.qlhui.thoughtlight.myview.MyListView;
import com.qlhui.thoughtlight.myview.MyListener;
import com.qlhui.thoughtlight.myview.PullToRefreshLayout;
import com.qlhui.thoughtlight.myview.MyListView.OnRefreshListener;
import com.qlhui.thoughtlight.myview.PullableListView;
import com.qlhui.thoughtlight.net.ThreadPoolUtils;
import com.qlhui.thoughtlight.thread.HttpGetThread;
import com.qlhui.thoughtlight.utils.MyJson;
import com.qlhui.thoughtlight.adapter.BaijiaAdapter;
import com.qlhui.thoughtlight.adapter.MingrenAdapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class BaijiaFragment extends BaseFragment implements OnClickListener {

	private BaijiaFragmentCallBack mBaijiaFragmentCallBack;
	private View view;
	private MainTabActivity ctx;
	private LinearLayout mLinearLayout, load_progressBar;
	private LinearLayout Near_Back;
	private ImageView Near_Seting, Near_More;
	private TextView HomeNoValue;
	private PullableListView myListView;
	private BaijiaAdapter mAdapter = null;
	private MyJson myJson = new MyJson();
	private List<BaijiaInfo> list = new ArrayList<BaijiaInfo>();
	private Button ListBottem = null;
	private int mStart = 0;
	private int mEnd = 10;
	private String url = null;
	private boolean flag = true;
	private boolean loadflag = false;
	private boolean listBottemFlag = true;
	DisplayImageOptions options;		// 显示图片的设置
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		

		options = new DisplayImageOptions.Builder()
			.showStubImage(R.drawable.default_content_pic)
			.showImageForEmptyUri(R.drawable.default_content_pic)
			.showImageOnFail(R.drawable.default_content_pic)
			.cacheInMemory(true)
			.cacheOnDisc(true)
			.bitmapConfig(Bitmap.Config.RGB_565)	 //设置图片的解码类型
			.build();
		   if (view == null)
		    {
			  view = inflater.inflate(R.layout.activity_wenhua, null);
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

	@SuppressLint("NewApi") private void initView() {
		
		//ctx.setActionStyle("百家",false);
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
					url =  Model.BAIJIA + "start=" + mStart + "&end=" + mEnd;
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
		myListView =(PullableListView) view.findViewById(R.id.bjpulllist);
	//	((GridView) myListView).setAdapter(new ImageAdapter());			// 填充数据
		myListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			//	startImagePagerActivity(position);
			}
		});
		Near_Back = (LinearLayout) view.findViewById(R.id.Near_Back);
		Near_Seting = (ImageView) view.findViewById(R.id.Near_Seting);
		Near_More = (ImageView) view.findViewById(R.id.Near_More);
		HomeNoValue = (TextView) view.findViewById(R.id.HomeNoValue);
		Near_Back.setOnClickListener(this);
		Near_Seting.setOnClickListener(this);
		Near_More.setOnClickListener(this);
		mAdapter = new BaijiaAdapter(ctx,list);
		mAdapter.setImgloader(imageLoader);
		ListBottem = new Button(ctx);
		ListBottem.setText("点击加载更多");
		ListBottem.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (flag && listBottemFlag) {
					url = Model.BAIJIA + "start=" + mStart + "&end=" + mEnd;
					ThreadPoolUtils.execute(new HttpGetThread(hand, url));
					listBottemFlag = false;
				} else if (!listBottemFlag)
					Toast.makeText(ctx, "正在加载中...", 1).show();
			}
		});
	//	myListView.addFooterView(ListBottem, null, false);
		ListBottem.setVisibility(View.GONE);
		myListView.setAdapter(mAdapter);
		myListView.setOnTouchListener(new OnTouchListener() {
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
				//		ctx.showBottombar(true);
					} else if((y - mMotionY) < -20)
					{
				//		ctx.showBottombar(false);
					
						}
					
					mMotionY = y;
					break;
				}
				return false;
			}
		});
		myListView.setOnItemClickListener(new MainListOnItemClickListener());
		url = Model.BAIJIA + "start=" + mStart + "&end=" + mEnd;
		ThreadPoolUtils.execute(new HttpGetThread(hand, url));
	/*
		myListView.setonRefreshListener(new OnRefreshListener() {
			public void onRefresh() {
				if (loadflag == true) {
					mStart = 0;
					mEnd = 10;
					url = Model.BAIJIA + "start=" + mStart + "&end=" + mEnd;
					ListBottem.setVisibility(View.GONE);
					ThreadPoolUtils.execute(new HttpGetThread(hand, url));
					loadflag = false;
				} else {
					Toast.makeText(ctx, "正在加载中，请勿重复刷新", 1).show();
				}

			}
		});
*/
	}

	@Override
	public void onClick(View arg0) {
		int mID = arg0.getId();
		switch (mID) {
		case R.id.Near_Back:
			mBaijiaFragmentCallBack.callback(R.id.Near_Back);
			break;
		case R.id.Near_Seting:
			mBaijiaFragmentCallBack.callback(R.id.Near_Seting);
			break;
		case R.id.Near_More:
			mBaijiaFragmentCallBack.callback(R.id.Near_More);
			break;
		default:
			break;
		}

	}

	private class MainListOnItemClickListener implements OnItemClickListener {
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			
			Intent intent = new Intent(ctx, BaijiaActivity.class);
		//	Intent intent = new Intent(ctx, ForumDetailActivity.class);
			Bundle bund = new Bundle();
			bund.putSerializable("BaijiaInfo", list.get(arg2));
			intent.putExtra("value", bund);
			startActivity(intent);
		}
	}

	public void setCallBack(BaijiaFragmentCallBack mBaijiaFragmentCallBack) {
		this.mBaijiaFragmentCallBack = mBaijiaFragmentCallBack;
	}

	public interface BaijiaFragmentCallBack {
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
					List<BaijiaInfo> newList = myJson.getBaijiaList(result);
					if (newList != null) {
						Log.e("liuxiaowei", "newList=" + newList.size()
								+ "  list=" + list.size());
						if (newList.size() == 10) {
							ListBottem.setVisibility(View.VISIBLE);
							mStart += 10;
							mEnd += 10;
						} else if (newList.size() == 0) {
							if (list.size() == 0)
								HomeNoValue.setVisibility(View.VISIBLE);
							ListBottem.setVisibility(View.GONE);
							Toast.makeText(ctx, "已经没有了...", 1).show();
						} else {
							ListBottem.setVisibility(View.GONE);
						}
						if (!loadflag) {
							list.removeAll(list);
						}
						for (BaijiaInfo info : newList) {
							list.add(info);
						}
						listBottemFlag = true;
					} else {
						if (list.size() == 0)
							HomeNoValue.setVisibility(View.VISIBLE);
					}
				}
		//		mLinearLayout.setVisibility(View.VISIBLE);
				load_progressBar.setVisibility(View.GONE);
			//	myListView.onRefreshComplete();
				mAdapter.notifyDataSetChanged();
				loadflag = true;
			}
		};
	};
	@Override
	public void onResume()
	{
		super.onResume();
		ctx.setActionStyle("传承经典",false);
	}
	public class ImageAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ImageView imageView;
			if (convertView == null) {
				imageView = (ImageView) ((Activity) ctx).getLayoutInflater().inflate(R.layout.item_grid_image, parent, false);
			} else {
				imageView = (ImageView) convertView;
			}

			// 将图片显示任务增加到执行池，图片将被显示到ImageView当轮到此ImageView
			imageLoader.displayImage(Model.BAIJIAHEADURL + list.get(position).getUhead(), imageView, options);

			return imageView;
		}
	}
}
