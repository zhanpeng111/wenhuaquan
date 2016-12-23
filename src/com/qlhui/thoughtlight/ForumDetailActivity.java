package com.qlhui.thoughtlight;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.qlhui.thoughtlight.adapter.ArticleListAdapter;
import com.qlhui.thoughtlight.adapter.BaijiaAdapter;
import com.qlhui.thoughtlight.adapter.DetailListAdapter;
import com.qlhui.thoughtlight.adapter.FormdetailListAdapter;
import com.qlhui.thoughtlight.imageviewer.ImageviewActivity;
import com.qlhui.thoughtlight.info.AshamedInfo;
import com.qlhui.thoughtlight.info.BaijiaInfo;
import com.qlhui.thoughtlight.model.Model;
import com.qlhui.thoughtlight.myview.CommentListView;
import com.qlhui.thoughtlight.myview.ImgViewerDialog;
import com.qlhui.thoughtlight.myview.ItemHeaderView;
import com.qlhui.thoughtlight.myview.ItemInfoView;
import com.qlhui.thoughtlight.net.ThreadPoolUtils;
import com.qlhui.thoughtlight.thread.HttpGetThread;
import com.qlhui.thoughtlight.utils.LoadImg;
import com.qlhui.thoughtlight.utils.MyJson;
import com.qlhui.thoughtlight.utils.LoadImg.ImageDownloadCallBack;

public class ForumDetailActivity extends BaseActivity {

	private BaijiaInfo info = null;
	private LoadImg loadImg;
	private MyJson myJson = new MyJson();
	private ImageView Detail_Back, Detail_SenComment;
	private TextView Detail_AshameID;
	private ImageView Detail_UserHead;
	private TextView Detail_UserName;
	private TextView Detail_MainText;
	private ImageView Detail_MainImg;
	private LinearLayout Detail_Up;
	private ImageView Detail_Up_Img;
	private TextView Detail_Up_text;
	private LinearLayout Detail_Down;
	private ImageView Detail_Down_Img;
	private TextView Detail_Down_text;
	private TextView Detail_ShareNum;
	private LinearLayout Detail_Share;
	private ImageView Detail_Share_Img;
	private CommentListView Detail_List;
	
	private ItemHeaderView mitem_header;
	private ItemInfoView mitem_info;
	private ListView mitem_pulllist;
	
	private LinearLayout Detail__progressBar;
	private TextView Detail_CommentsNum;
	private int commentsFlag = 0;
	private boolean upFlag = false;
	private boolean downFlag = false;
	private List<AshamedInfo> list = new ArrayList<AshamedInfo>();
	private FormdetailListAdapter mAdapter = null;
	private Button ListBottem = null;
	private int mStart = 0;
	private int mEnd =15;
	private String url = null;
	private boolean flag = true;
	private boolean listBottemFlag = true;
	private  int WidowWidth = 0;
	private ImgViewerDialog ImgDialog = null;
	private Bitmap mainbit = null;
	private SharedPreferences sp;  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	//	requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_forum_detail);
		Intent intent = getIntent();
		Bundle bund = intent.getBundleExtra("value");
		info = (BaijiaInfo) bund.getSerializable("BaijiaInfo");
		loadImg = new LoadImg(ForumDetailActivity.this);
		WindowManager wm = (WindowManager) this

                .getSystemService(Context.WINDOW_SERVICE);
		WidowWidth  = wm.getDefaultDisplay().getWidth();	
		sp = this.getSharedPreferences("likeInfo", Context.MODE_WORLD_READABLE);  
		
		options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.default_users_avatar)			// 设置图片下载期间显示的图片
		.showImageForEmptyUri(R.drawable.default_users_avatar)	// 设置图片Uri为空或是错误的时候显示的图片
		.showImageOnFail(R.drawable.default_users_avatar)		// 设置图片加载或解码过程中发生错误显示的图片	
		.cacheInMemory(true)						// 设置下载的图片是否缓存在内存中
		.cacheOnDisc(true)							// 设置下载的图片是否缓存在SD卡中
		.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
		.displayer(new RoundedBitmapDisplayer(20))	// 设置成圆角图片 
		.build();	

	//	addInformation();
	//	addImg();

		mAdapter = new FormdetailListAdapter(ForumDetailActivity.this, list);
		mAdapter.setImgloader(imageLoader);
		initView();// 创建配置过得DisplayImageOption对象
	//	Detail_List.setAdapter(mAdapter);
		url = Model.TUIJIAN + "start=" + 0 + "&end=" + 4;
		ThreadPoolUtils.execute(new HttpGetThread(hand, url));
	}
	/*
	private void updatevalue(String up,String down) {
		
		String endParames = Model.UPDATEVALUE + "qid=" + info.getQid() + "&qlike="
				+ up + "&qunlike=" + down;
		ThreadPoolUtils.execute(new HttpGetThread(hand, endParames));
	} 
	*/
	private void initView() {
	//	Detail_AshameID=(TextView) findViewById(R.id.Detail_AshameID);
	//	Detail_AshameID.setText(info.getUname());
		mitem_header= (ItemHeaderView) findViewById(R.id.item_header);
		mitem_info= (ItemInfoView) findViewById(R.id.item_info);
		mitem_pulllist= (ListView) findViewById(R.id.pulllist);
		mitem_header.setData(info,imageLoader,options);
		mitem_info.setData(info);
		mitem_pulllist.setAdapter(mAdapter);
		
		/*
		MyOnClickListner myOnclick = new MyOnClickListner();
		Detail_Back = (ImageView) findViewById(R.id.Detail_Back);
		Detail_SenComment = (ImageView) findViewById(R.id.Detail_SenComment);
		Detail_AshameID = (TextView) findViewById(R.id.Detail_AshameID);
		Detail_UserHead = (ImageView) findViewById(R.id.Detail_UserHead);
		Detail_UserName = (TextView) findViewById(R.id.Detail_UserName);
		Detail_MainText = (TextView) findViewById(R.id.Detail_MainText);
		Detail_MainImg = (ImageView) findViewById(R.id.Detail_MainImg);
		Detail_Up = (LinearLayout) findViewById(R.id.Detail_Up);
		Detail_Up_Img = (ImageView) findViewById(R.id.Detail_Up_Img);
		Detail_Up_text = (TextView) findViewById(R.id.Detail_Up_text);
		Detail_Down = (LinearLayout) findViewById(R.id.Detail_Down);
		Detail_Down_Img = (ImageView) findViewById(R.id.Detail_Down_Img);
		Detail_Down_text = (TextView) findViewById(R.id.Detail_Down_text);
		Detail_ShareNum = (TextView) findViewById(R.id.Detail_ShareNum);
		Detail_Share = (LinearLayout) findViewById(R.id.Detail_Share);
		Detail_Share_Img = (ImageView) findViewById(R.id.Detail_Share_Img);
		Detail_List = (CommentListView) findViewById(R.id.Detail_List);
		Detail__progressBar = (LinearLayout) findViewById(R.id.Detail__progressBar);
		Detail_CommentsNum = (TextView) findViewById(R.id.Detail_CommentsNum);
		Detail_Back.setOnClickListener(myOnclick);
		Detail_SenComment.setOnClickListener(myOnclick);
		Detail_UserHead.setOnClickListener(myOnclick);
		Detail_MainImg.setOnClickListener(myOnclick);
		Detail_Up.setOnClickListener(myOnclick);
		Detail_Down.setOnClickListener(myOnclick);
		Detail_Share.setOnClickListener(myOnclick);
		*/
	}


	@SuppressLint("HandlerLeak")
	Handler hand = new Handler() {

		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			if (msg.what == 404) {
				Toast.makeText(ForumDetailActivity.this, "请求失败，服务器故障", 1)
						.show();
				listBottemFlag = true;
			} else if (msg.what == 100) {
				Toast.makeText(ForumDetailActivity.this, "服务器无响应", 1).show();
				listBottemFlag = true;
			} else if (msg.what == 200) {
				String result = (String) msg.obj;
				if (result != null) {
					List<AshamedInfo> newList = myJson
							.getAshamedList(result);
					if (newList != null) {
						if (newList.size() == 4) {
						//	Detail_List.setVisibility(View.VISIBLE);
						//	ListBottem.setVisibility(View.VISIBLE);
						//	mStart += 15;
						//	mEnd += 15;
						} else if (newList.size() == 0) {
						//	if (list.size() == 0)
						//		Detail_CommentsNum.setVisibility(View.VISIBLE);
						//	ListBottem.setVisibility(View.GONE);
						//	Toast.makeText(ForumDetailActivity.this,
						//			"已经没有了...", 1).show();
						} else {
					//		Detail_List.setVisibility(View.VISIBLE);
					//		ListBottem.setVisibility(View.GONE);
						}
						for (AshamedInfo info : newList) {
							list.add(info);
						}
						listBottemFlag = true;
					} else {
					//	Detail_CommentsNum.setVisibility(View.VISIBLE);
					}
				}
			//	Detail__progressBar.setVisibility(View.GONE);
				mAdapter.notifyDataSetChanged();
			}
		};

	};
	


}
