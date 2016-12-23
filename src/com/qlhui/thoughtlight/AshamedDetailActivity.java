package com.qlhui.thoughtlight;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.qlhui.thoughtlight.imageviewer.ImageviewActivity;
import com.qlhui.thoughtlight.info.AshamedInfo;
import com.qlhui.thoughtlight.info.CommentsInfo;
import com.qlhui.thoughtlight.model.Model;
import com.qlhui.thoughtlight.myview.ImgViewerDialog;
import com.qlhui.thoughtlight.myview.CommentListView;
import com.qlhui.thoughtlight.net.ThreadPoolUtils;
import com.qlhui.thoughtlight.thread.HttpGetThread;
import com.qlhui.thoughtlight.utils.LoadImg;
import com.qlhui.thoughtlight.utils.LoadImg.ImageDownloadCallBack;
import com.qlhui.thoughtlight.utils.MyJson;
import com.qlhui.thoughtlight.adapter.DetailListAdapter;

public class AshamedDetailActivity extends BaseActivity {

	private AshamedInfo info = null;
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
	private LinearLayout Detail__progressBar;
	private TextView Detail_CommentsNum;
	private int commentsFlag = 0;
	private boolean upFlag = false;
	private boolean downFlag = false;
	private List<CommentsInfo> list = new ArrayList<CommentsInfo>();
	private DetailListAdapter mAdapter = null;
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
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_ashamed_detail);
		Intent intent = getIntent();
		Bundle bund = intent.getBundleExtra("value");
		info = (AshamedInfo) bund.getSerializable("AshamedInfo");
		loadImg = new LoadImg(AshamedDetailActivity.this);
		WindowManager wm = (WindowManager) this

                .getSystemService(Context.WINDOW_SERVICE);
		WidowWidth  = wm.getDefaultDisplay().getWidth();	
		sp = this.getSharedPreferences("likeInfo", Context.MODE_WORLD_READABLE);  
		initView();
		options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.default_content_pic)			// 设置图片下载期间显示的图片
		.showImageForEmptyUri(R.drawable.default_content_pic)	// 设置图片Uri为空或是错误的时候显示的图片
		.showImageOnFail(R.drawable.default_content_pic)		// 设置图片加载或解码过程中发生错误显示的图片	
		.cacheInMemory(true)						// 设置下载的图片是否缓存在内存中
		.cacheOnDisc(true)							// 设置下载的图片是否缓存在SD卡中
		.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
		.displayer(new RoundedBitmapDisplayer(1))	// 设置成圆角图片
		.build();		
		headoptions = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.default_users_avatar)			// 设置图片下载期间显示的图片
		.showImageForEmptyUri(R.drawable.default_users_avatar)	// 设置图片Uri为空或是错误的时候显示的图片
		.showImageOnFail(R.drawable.default_users_avatar)		// 设置图片加载或解码过程中发生错误显示的图片	
		.cacheInMemory(true)						// 设置下载的图片是否缓存在内存中
		.cacheOnDisc(true)							// 设置下载的图片是否缓存在SD卡中
		.displayer(new RoundedBitmapDisplayer(20))	// 设置成圆角图片
		.build();	
		// 创建配置过得DisplayImageOption对象
		addInformation();
		addImg();

		mAdapter = new DetailListAdapter(AshamedDetailActivity.this, list);
		mAdapter.setImgloader(imageLoader);
		ListBottem = new Button(AshamedDetailActivity.this);
		ListBottem.setText("点击加载更多");
		ListBottem.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (flag && listBottemFlag) {
					url = Model.COMMENTS + "qid=" + info.getQid() + "&start="
							+ mStart + "&end=" + mEnd;
					ThreadPoolUtils.execute(new HttpGetThread(hand, url));
					listBottemFlag = false;
				} else if (!listBottemFlag)
					Toast.makeText(AshamedDetailActivity.this, "正在加载中...", 1)
							.show();
			}
		});
		Detail_List.addFooterView(ListBottem, null, false);
		ListBottem.setVisibility(View.GONE);
		Detail_List.setAdapter(mAdapter);
		String endParames = Model.COMMENTS + "qid=" + info.getQid() + "&start="
				+ mStart + "&end=" + mEnd;
		ThreadPoolUtils.execute(new HttpGetThread(hand, endParames));
	}
	private void updatevalue(String up,String down) {
		
		String endParames = Model.UPDATEVALUE + "qid=" + info.getQid() + "&qlike="
				+ up + "&qunlike=" + down;
		ThreadPoolUtils.execute(new HttpGetThread(handlike, endParames));
	} 
	private void initView() {
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
	}

	// ����¼��ļ�����Ӧ
	private class MyOnClickListner implements View.OnClickListener {
		public void onClick(View arg0) {
			int mID = arg0.getId();
			Editor editor = sp.edit();  
			switch (mID) {
			case R.id.Detail_Back:
				AshamedDetailActivity.this.finish();
				break;
			case R.id.Detail_SenComment:
				if (Model.MYUSERINFO != null) {
					Intent intent = new Intent(AshamedDetailActivity.this,
							SendCommentActivity.class);
					Bundle bund = new Bundle();
					bund.putSerializable("AshamedInfo", info);
					intent.putExtra("value", bund);
					startActivity(intent);
				} else {
					Intent intent = new Intent(AshamedDetailActivity.this,
							LoginActivity.class);
					startActivity(intent);
				}

				break;
			case R.id.Detail_UserHead:
				Toast.makeText(AshamedDetailActivity.this, "点击发送小纸条", 1).show();
				break;
			case R.id.Detail_MainImg:
			//	ShowImgDialog();
				/**/
				 Intent intent=new Intent(AshamedDetailActivity.this, ImageviewActivity.class);  
				 Bundle bundle=new Bundle();  
				// bundle.putString("path", LoadImg.GetImgUrl());  
				 bundle.putString("path", Model.BIMGURL + info.getQimg());  
				
				 // bundle.putString("pass", "123");  
				 //把附加的数据放到意图当中  
				intent.putExtras(bundle);  
				 //执行意图  
				 startActivity(intent);  

				break;
			case R.id.Detail_Up:
				downFlag = false;
				upFlag = true;
				if(sp.getString(info.getQid(), "").equals("")
					||sp.getString(info.getQid(), "").equals("unlike"))
				{	
					upOrDown();	
			        editor.putString(info.getQid(), "like");  		      
			        editor.commit(); 
				}
				
				break;
			case R.id.Detail_Down:
				upFlag = false;
				downFlag = true;
				if(sp.getString(info.getQid(), "").equals("")
						||sp.getString(info.getQid(), "").equals("like"))
				{		
					upOrDown();	 
					editor.putString(info.getQid(),"unlike");  		      
			        editor.commit(); 
				}
				break;
			case R.id.Detail_Share:
				info.setQshare(String.valueOf(Integer.parseInt(info.getQshare()) + 1));
				Detail_ShareNum.setText(String.valueOf(info.getQshare()));
				Toast.makeText(AshamedDetailActivity.this, "分享被点击", 1).show();
				break;
			default:
				break;
			}
		}
		private void ShowImgDialog() {
			if (ImgDialog == null){
				ImgDialog = ImgViewerDialog.createDialog(AshamedDetailActivity.this);
				ImgDialog.setImgbit(mainbit);
			}
			
			ImgDialog.show();
		}
		private void upOrDown() {
			String upNum = info.getQlike();
			String downNum = info.getQunlike();
			int num1 = Integer.parseInt(upNum);
			int num2 = Integer.parseInt(downNum);
			Detail_Up.setBackgroundResource(R.drawable.button_vote_enable);
			Detail_Up_Img.setImageResource(R.drawable.icon_for_enable);
			Detail_Up_text.setTextColor(Color.parseColor("#815F3D"));
			Detail_Down.setBackgroundResource(R.drawable.button_vote_enable);
			Detail_Down_Img.setImageResource(R.drawable.icon_against_enable);
			Detail_Down_text.setTextColor(Color.parseColor("#815F3D"));

			if (upFlag) {
				Detail_Up.setBackgroundResource(R.drawable.button_vote_active);
				Detail_Up_Img.setImageResource(R.drawable.icon_for_active);
				Detail_Up_text.setTextColor(Color.RED);
				if (commentsFlag == 0) {
					upNum = String.valueOf(num1 + 1);
					commentsFlag = 1;
				} else if (commentsFlag == 2) {
					upNum = String.valueOf(num1 + 1);
					downNum = String.valueOf(num2 - 1);
					commentsFlag = 1;
				}
			} else if (downFlag) {
				Detail_Down
						.setBackgroundResource(R.drawable.button_vote_active);
				Detail_Down_Img
						.setImageResource(R.drawable.icon_against_active);
				Detail_Down_text.setTextColor(Color.RED);
				if (commentsFlag == 0) {
					downNum = String.valueOf(num2 + 1);
					commentsFlag = 2;
				} else if (commentsFlag == 1) {
					downNum = String.valueOf(num2 + 1);
					upNum = String.valueOf(num1 - 1);
					commentsFlag = 2;
				}
			}
			info.setQlike(upNum);
			info.setQunlike(downNum);
			Detail_Up_text.setText(upNum);
			if (!downNum.endsWith("0")) {
				Detail_Down_text.setText("-" + downNum);
			} else {
				Detail_Down_text.setText(downNum);
			}
			updatevalue(upNum,downNum);
		}
	}
	Handler handlike = new Handler() {

		public void handleMessage(android.os.Message msg) {
			
			
		}
	};
	@SuppressLint("HandlerLeak")
	Handler hand = new Handler() {

		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			if (msg.what == 404) {
				Toast.makeText(AshamedDetailActivity.this, "请求失败，服务器故障", 1)
						.show();
				listBottemFlag = true;
			} else if (msg.what == 100) {
				Toast.makeText(AshamedDetailActivity.this, "服务器无响应", 1).show();
				listBottemFlag = true;
			} else if (msg.what == 200) {
				String result = (String) msg.obj;
				if (result != null) {
					List<CommentsInfo> newList = myJson
							.getAhamedCommentsList(result);
					if (newList != null) {
						if (newList.size() == 15) {
							Detail_List.setVisibility(View.VISIBLE);
							ListBottem.setVisibility(View.VISIBLE);
							mStart += 15;
							mEnd += 15;
						} else if (newList.size() == 0) {
							if (list.size() == 0)
								Detail_CommentsNum.setVisibility(View.VISIBLE);
							ListBottem.setVisibility(View.GONE);
						//	Toast.makeText(AshamedDetailActivity.this,
						//			"已经没有了...", 1).show();
						} else {
							Detail_List.setVisibility(View.VISIBLE);
							ListBottem.setVisibility(View.GONE);
						}
						for (CommentsInfo info : newList) {
							list.add(info);
						}
						listBottemFlag = true;
					} else {
						Detail_CommentsNum.setVisibility(View.VISIBLE);
					}
				}
				Detail__progressBar.setVisibility(View.GONE);
				mAdapter.notifyDataSetChanged();
			}
		};

	};
	public void pageMainImgLoad() {		
		/**
		 * 显示图片
		 * 参数1：图片url
		 * 参数2：显示图片的控件
		 * 参数3：显示图片的设置
		 * 参数4：监听器
		 */
		if (!info.getQimg().equalsIgnoreCase("")) {
	
			Detail_MainImg.setVisibility(View.VISIBLE);
			imageLoader.displayImage(Model.BIMGURL + info.getQimg(), 
					Detail_MainImg, options, animateFirstListener);
		} else {
			Detail_MainImg.setVisibility(View.GONE);
		}
	}
	
	public void pageHeadImgLoad() {	
	
		  if(info.getUhead().equalsIgnoreCase("")){
			    Detail_UserHead.setImageResource(R.drawable.default_users_avatar);
			} else {
				Detail_UserHead.setVisibility(View.VISIBLE);
				imageLoader.displayImage(Model.USERHEADURL + info.getUhead(), 
						Detail_UserHead, headoptions, animateFirstListener);
			}
		}
	private void addImg() {
	
	
		pageHeadImgLoad();
		pageMainImgLoad();
		/*
		if (!info.getQimg().equalsIgnoreCase("")) {
			Detail_MainImg.setVisibility(View.VISIBLE);
			Detail_MainImg.setTag(Model.QIMGURL + info.getQimg());
			mainbit = loadImg.loadImage(Detail_MainImg,
					Model.BIMGURL + info.getQimg(),
					new ImageDownloadCallBack() {
						public void onImageDownload(ImageView imageView,
								Bitmap bitmap) {
							imageView.setMinimumHeight(WidowWidth*bitmap.getHeight()/bitmap.getWidth());
							imageView.setMinimumWidth(WidowWidth);
							imageView.setImageBitmap(bitmap);
						}
					});
			if (mainbit != null) {
			   //  int minHeight = wm.getDefaultDisplay().getHeight();
				Detail_MainImg.setMinimumHeight(WidowWidth*mainbit.getHeight()/mainbit.getWidth());
				Detail_MainImg.setMinimumWidth(WidowWidth);
				Detail_MainImg.setImageBitmap(mainbit);
			}
		} else {
			Detail_MainImg.setVisibility(View.GONE);
		}
*/
	}

	private void addInformation() {
		Detail_AshameID.setText("正能量ID" + info.getQid());
		Detail_UserName.setText(info.getUname());
		Detail_MainText.setText(info.getQvalue());
		Detail_Up_text.setText(info.getQlike());
		Detail_Down_text.setText("-" + info.getQunlike());
		Detail_ShareNum.setText(info.getQshare());
		
		if(sp.getString(info.getQid(), "").equals("like"))
		{
			Detail_Up.setBackgroundResource(R.drawable.button_vote_active);
			Detail_Up_Img.setImageResource(R.drawable.icon_for_active);
			Detail_Up_text.setTextColor(Color.RED);
			commentsFlag = 1;
			
		}
		else if(sp.getString(info.getQid(), "").equals("unlike"))
		{
			Detail_Down.setBackgroundResource(R.drawable.button_vote_active);
			Detail_Down_Img.setImageResource(R.drawable.icon_for_active);
			Detail_Down_text.setTextColor(Color.RED);
			commentsFlag = 2;
		}
	}
}
