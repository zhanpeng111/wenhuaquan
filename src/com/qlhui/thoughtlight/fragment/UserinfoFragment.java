package com.qlhui.thoughtlight.fragment;

import java.util.ArrayList;
import java.util.List;

import com.qlhui.thoughtlight.AshamedDetailActivity;
import com.qlhui.thoughtlight.LoginActivity;
import com.qlhui.thoughtlight.MainTabActivity;
import com.qlhui.thoughtlight.MingrenInfoActivity;
import com.qlhui.thoughtlight.R;
import com.qlhui.thoughtlight.ReviseActivity;
import com.qlhui.thoughtlight.UserInfoActivity;
import com.qlhui.thoughtlight.info.AshamedInfo;
import com.qlhui.thoughtlight.info.BJXUserInfo;
import com.qlhui.thoughtlight.model.Model;
import com.qlhui.thoughtlight.myview.CommentListView;
import com.qlhui.thoughtlight.myview.MyListView;
import com.qlhui.thoughtlight.myview.MyListener;
import com.qlhui.thoughtlight.myview.PullToRefreshLayout;
import com.qlhui.thoughtlight.myview.MyListView.OnRefreshListener;
import com.qlhui.thoughtlight.myview.PullableListView;
import com.qlhui.thoughtlight.net.ThreadPoolUtils;
import com.qlhui.thoughtlight.thread.HttpGetThread;
import com.qlhui.thoughtlight.utils.LoadImg;
import com.qlhui.thoughtlight.utils.MyJson;
import com.qlhui.thoughtlight.utils.LoadImg.ImageDownloadCallBack;
import com.qlhui.thoughtlight.adapter.ArticleListAdapter;
import com.qlhui.thoughtlight.adapter.MingrenAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class UserinfoFragment extends BaseFragment implements OnClickListener {
	private BJXUserInfo info = null;
	private ImageView mUserRevise, mUserMore, mUserCamera;
	private LinearLayout mBrief, mQiushi;
	private LinearLayout mUserBrief, mUserQiushi;
	private RelativeLayout mBack,mheadlayout;
	private Boolean myflag = true;
	private TextView UserName, UserAge, UserHobbies, UserPlace, 
			UserTime, UserBrand, userinfo,usertitle;
	private LoadImg loadImgHeadImg;
	private MyJson myJson = new MyJson();
	private List<AshamedInfo> list = new ArrayList<AshamedInfo>();
	private ArticleListAdapter mAdapter = null;
	private Button ListBottem = null;
	private int mStart = 0;
	private int mEnd = 5;
	private View view;
	private MainTabActivity ctx;
	private String url = null;
	private boolean flag = true;
	private boolean loadflag = false;
	private boolean listBottemFlag = true;
	private CommentListView Detail_List;
	private LinearLayout Detail__progressBar;
	private RelativeLayout Detail_CommentsNum;
	private int mheadViewArray[] = {R.drawable.personal_desk,R.drawable.personal_default,R.drawable.personal_goldgatebridge,
			 R.drawable.personal_greenfield,R.drawable.personal_sun,R.drawable.personal_tieta,R.drawable.personal_xingkong,R.drawable.personal_duzi};
	private SharedPreferences sp;  
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		 if (view == null)
		    {
			  view = inflater.inflate(R.layout.activity_userinfo, null);
			  ctx = (MainTabActivity) view.getContext();
			  sp = ctx.getSharedPreferences("userInfo", Context.MODE_WORLD_READABLE); 
			  initView();
		    }
		    // 缓存的rootView需要判断是否已经被加过parent，如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
		    ViewGroup parent = (ViewGroup) view.getParent();
		    if (parent != null)
		    {
		      parent.removeView(view);
		    }
	
		
		 
		 
	
	//	Intent intent = ctx.getIntent();
	//	Bundle bund = intent.getBundleExtra("value");
	//	info = (BJXUserInfo) bund.getSerializable("UserInfo");
		info = Model.MYUSERINFO;
	//	initView();
		createUserInfo();
		mAdapter = new ArticleListAdapter(ctx, list);
		ListBottem = new Button(ctx);
		ListBottem.setText("点击加载更多");
		ListBottem.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (flag && listBottemFlag) {
					url = Model.UASHAMED + "uid=" + info.getUserid()
							+ "&start=" + mStart + "&end=" + mEnd;
					ThreadPoolUtils.execute(new HttpGetThread(hand, url));
					listBottemFlag = false;
				} else if (!listBottemFlag)
					Toast.makeText(ctx, "正在加载中...", 1).show();
			}
		});
		
		Detail_List.addFooterView(ListBottem, null, false);
		ListBottem.setVisibility(View.GONE);
		Detail_List.setAdapter(mAdapter);
		String endParames = Model.UASHAMED + "uid=" + info.getUserid()
				+ "&start=" + mStart + "&end=" + mEnd;
		ThreadPoolUtils.execute(new HttpGetThread(hand, endParames));
		
		return view;
	}

	private void initView() {
		// TODO Auto-generated method stub
	
		mBrief = (LinearLayout) view.findViewById(R.id.Brief);
		mheadlayout = (RelativeLayout) view.findViewById(R.id.head);
		int imgindex = sp.getInt("headimgidx", 0);
		mheadlayout.setBackgroundResource(mheadViewArray[imgindex]);
		mUserQiushi = (LinearLayout) view.findViewById(R.id.UserQiushi);
	//	mBack = (RelativeLayout) view.findViewById(R.id.UserBack);
	//	mUserRevise = (ImageView) view.findViewById(R.id.UserRevise);
		mUserCamera = (ImageView) view.findViewById(R.id.user_head);
		UserName = (TextView) view.findViewById(R.id.user_name);
	//	usertitle = (TextView) view.findViewById(R.id.Usertitle);
		
		UserAge = (TextView) view.findViewById(R.id.user_bar_age);
		UserHobbies = (TextView) view.findViewById(R.id.UserHobbies);
		UserPlace = (TextView) view.findViewById(R.id.UserPlace);
	//	UserExplain = (TextView) view.findViewById(R.id.UserExplain);
		UserTime = (TextView) view.findViewById(R.id.UserTime);
		UserBrand = (TextView) view.findViewById(R.id.UserBrand);
		userinfo = (TextView) view.findViewById(R.id.sign_text);
		Detail_List = (CommentListView) view.findViewById(R.id.Detail_List);
		Detail__progressBar = (LinearLayout) view.findViewById(R.id.Detail__progressBar);
		Detail_CommentsNum = (RelativeLayout) view.findViewById(R.id.usernoashamed);
		MyOnClick my = new MyOnClick();
		
		Detail_List.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent(ctx,
						AshamedDetailActivity.class);
				Bundle bund = new Bundle();
				bund.putSerializable("AshamedInfo", list.get(arg2));
				intent.putExtra("value", bund);
				startActivity(intent);
			}
		});
	//	mBrief.setOnClickListener(my);
	//	mQiushi.setOnClickListener(my);
	//	mUserRevise.setOnClickListener(my);
	//	mBack.setOnClickListener(my);
		mUserCamera.setOnClickListener(my);
		mheadlayout.setOnClickListener(my);
	}
	void ChangeHeadimg()
	{
		int imgindex = sp.getInt("headimgidx", 0)+1;
		if(imgindex > mheadViewArray.length-1)
			imgindex = 0;
		mheadlayout.setBackgroundResource(mheadViewArray[imgindex]);
		sp.edit().putInt("headimgidx", imgindex).commit();
	}
	class MyOnClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int mId = v.getId();
			switch (mId) {
			case R.id.Brief:
				myflag = true;
				initCont(myflag);
				break;
			case R.id.head:
				 ChangeHeadimg();
				break;
				
			case R.id.Qiushi:
				myflag = false;
				initCont(myflag);
				break;
			case R.id.UserBack:
			//	finish();
				break;
			case R.id.UserRevise:
				 Intent intent = new Intent(ctx,ReviseActivity.class);
				 startActivity(intent);
				break;
			case R.id.UserCamera:
				// Intent intent = new Intent(ctx,.class);
				// startActivity(intent);
				break;
			}

		}

	}

	@SuppressLint("HandlerLeak")
	Handler hand = new Handler() {

		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			if (msg.what == 404) {
				Toast.makeText(ctx, "请求失败，服务器故障", 1).show();
				listBottemFlag = true;
			} else if (msg.what == 100) {
				Toast.makeText(ctx, "服务器无响应", 1).show();
				listBottemFlag = true;
			} else if (msg.what == 200) {
				String result = (String) msg.obj;
				if (result != null) {
					List<AshamedInfo> newList = myJson.getAshamedList(result);
					if (newList != null) {
						if (newList.size() == 5) {
							Detail_List.setVisibility(View.VISIBLE);
							ListBottem.setVisibility(View.VISIBLE);
							mStart += 5;
							mEnd += 5;
						} else if (newList.size() == 0) {
							if (list.size() == 0)
								Detail_CommentsNum.setVisibility(View.VISIBLE);
							ListBottem.setVisibility(View.GONE);
							Detail_List.setVisibility(View.GONE);
						} else {
							Detail_List.setVisibility(View.VISIBLE);
							ListBottem.setVisibility(View.GONE);
						}
						for (AshamedInfo info : newList) {
							list.add(info);
						}
						listBottemFlag = true;
					} else {
						Detail_List.setVisibility(View.GONE);
						Detail_CommentsNum.setVisibility(View.VISIBLE);
					}
				}
				Detail__progressBar.setVisibility(View.GONE);
				mAdapter.notifyDataSetChanged();
			}
		};

	};

	private void initCont(Boolean myflag) {
		if (myflag) {
			mBrief.setBackgroundResource(R.drawable.cab_background_top_light);
			mQiushi.setBackgroundResource(R.drawable.ab_stacked_solid_light);
			mUserBrief.setVisibility(View.VISIBLE);
			mUserQiushi.setVisibility(View.GONE);
		} else {
			mBrief.setBackgroundResource(R.drawable.ab_stacked_solid_light);
			mQiushi.setBackgroundResource(R.drawable.cab_background_top_light);
			mUserBrief.setVisibility(View.GONE);
			mUserQiushi.setVisibility(View.VISIBLE);
		}
	}

	private void createUserInfo() {
		UserName.setText(info.getUname());
	//	usertitle.setText(info.getUname());
		
		if (!info.getUage().equals("null")) {
			UserAge.setText(info.getUage());
			if (info.getUsex().equals("0")) {
				UserAge.setBackgroundResource(R.drawable.nearby_gender_female);
			} else if (info.getUsex().equals("1")) {
				UserAge.setBackgroundResource(R.drawable.nearby_gender_male);
			}
		}
		if (!info.getUhobbles().equals("null")) {
			UserHobbies.setText("兴趣爱好　" + info.getUhobbles());
		}
		if (!info.getUplace().equals("null")) {
			UserPlace.setText("常出没地　" + info.getUplace());
		}
	
		if (!info.getUbrand().equals("null")) {
			UserBrand.setText("爪机品牌　" + info.getUbrand());
		}
		UserTime.setText("注册时间　" + info.getUtime());
		loadImgHeadImg = new LoadImg(ctx);
		if (!info.getUhead().equals("null")) {
		
			Bitmap bit = loadImgHeadImg.loadImage(mUserCamera,
					Model.USERHEADURL + info.getUhead(),
					new ImageDownloadCallBack() {
						public void onImageDownload(ImageView imageView,
								Bitmap bitmap) {
							mUserCamera.setImageBitmap(bitmap);
						}
					});
			if (bit != null) {
				mUserCamera.setImageBitmap(bit);
			}
		}
		else if(!sp.getString("USER_HEAD", "").equals(""))
		{
			Bitmap bit = loadImgHeadImg.loadImage(mUserCamera,
					sp.getString("USER_HEAD", ""),
					new ImageDownloadCallBack() {
						public void onImageDownload(ImageView imageView,
								Bitmap bitmap) {
							mUserCamera.setImageBitmap(bitmap);
						}
					});
			
		}
	}
	@Override
	public void onResume()
	{
		super.onResume();
		ctx.setActionStyle("惟吾德馨",false);
	}
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}


}
