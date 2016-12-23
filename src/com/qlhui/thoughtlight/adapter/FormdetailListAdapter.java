package com.qlhui.thoughtlight.adapter;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.qlhui.thoughtlight.AshamedDetailActivity;
import com.qlhui.thoughtlight.R;
import com.qlhui.thoughtlight.imageviewer.ImageviewActivity;
import com.qlhui.thoughtlight.imageviewer.PicUtil;
import com.qlhui.thoughtlight.info.AshamedInfo;
import com.qlhui.thoughtlight.info.CommentsInfo;
import com.qlhui.thoughtlight.model.Model;
import com.qlhui.thoughtlight.net.ThreadPoolUtils;
import com.qlhui.thoughtlight.thread.HttpGetThread;
import com.qlhui.thoughtlight.utils.LoadImg;
import com.qlhui.thoughtlight.utils.LoadImg.ImageDownloadCallBack;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class FormdetailListAdapter extends MybaseAdapter {
	//protected ImageLoader imageLoader = ImageLoader.getInstance();
	DisplayImageOptions options,headoptions;		// DisplayImageOptions是用于设置图片显示的类
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	private List<AshamedInfo> list;
	private Context ctx;
	private LoadImg loadImgHeadImg;
	private LoadImg loadImgMainImg;
	private boolean upFlag = false;
	private boolean downFlag = false;
	private Holder hold;
	private  int listpos = 0;
	private  int WidowWidth = 0;
	private boolean mBusy = false;
	private SharedPreferences sp;  
	public FormdetailListAdapter(Context ctx, List<AshamedInfo> list) {
		this.list = list;
		this.ctx = ctx;
		WindowManager wm = (WindowManager) ctx

                .getSystemService(Context.WINDOW_SERVICE);
		WidowWidth  = wm.getDefaultDisplay().getWidth();	
		// 使用DisplayImageOptions.Builder()创建DisplayImageOptions
		options = new DisplayImageOptions.Builder()
			.showStubImage(R.drawable.default_content_pic)			// 设置图片下载期间显示的图片
			.showImageForEmptyUri(R.drawable.default_content_pic)	// 设置图片Uri为空或是错误的时候显示的图片
			.showImageOnFail(R.drawable.default_content_pic)		// 设置图片加载或解码过程中发生错误显示的图片	
			.cacheInMemory(true)						// 设置下载的图片是否缓存在内存中
			.cacheOnDisc(true)							// 设置下载的图片是否缓存在SD卡中
			.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
			.displayer(new RoundedBitmapDisplayer(1))	// 设置成圆角图片
			.build();									// 创建配置过得DisplayImageOption对象
		// 使用DisplayImageOptions.Builder()创建DisplayImageOptions
		sp = ctx.getSharedPreferences("likeInfo", Context.MODE_WORLD_READABLE);  
		headoptions = new DisplayImageOptions.Builder()
			.showStubImage(R.drawable.default_users_avatar)			// 设置图片下载期间显示的图片
			.showImageForEmptyUri(R.drawable.default_users_avatar)	// 设置图片Uri为空或是错误的时候显示的图片
			.showImageOnFail(R.drawable.default_users_avatar)		// 设置图片加载或解码过程中发生错误显示的图片	
			.cacheInMemory(true)						// 设置下载的图片是否缓存在内存中
			.cacheOnDisc(true)							// 设置下载的图片是否缓存在SD卡中
			.displayer(new RoundedBitmapDisplayer(20))	// 设置成圆角图片
			.build();									// 创建配置过得DisplayImageOption对象

		// 加载图片对象
	//	loadImgHeadImg = new LoadImg(ctx);
	//	loadImgMainImg = new LoadImg(ctx);
	}
	public void setFlagBusy(boolean busy) {
		this.mBusy = busy;
	}
	public void setImgloader(ImageLoader loader ) {
		this.mimageLoader = loader;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}
	
	public void pageHeadImgLoad(final int start) {	
	/*	
		hold.UserHead.setImageResource(R.drawable.default_users_avatar);
		if (list.get(arg0).getUhead().equalsIgnoreCase("")) {
			hold.UserHead.setImageResource(R.drawable.default_users_avatar);
		} else {
			hold.UserHead.setTag(Model.USERHEADURL + list.get(arg0).getUhead());
			Bitmap bitHead = loadImgHeadImg.loadImage(hold.UserHead,
					Model.USERHEADURL + list.get(arg0).getUhead(),
					new ImageDownloadCallBack() {
						@Override
						public void onImageDownload(ImageView imageView,
								Bitmap bitmap) {
							if (hold.UserHead.getTag().equals(
									Model.USERHEADURL
											+ list.get(arg0).getUhead())) {
								
								hold.UserHead.setImageBitmap(PicUtil.getRoundedCornerBitmap(bitmap,8));
							}
						}
					});
			if (bitHead != null) {
				hold.UserHead.setImageBitmap(PicUtil.getRoundedCornerBitmap(bitHead,8));
			}
		}
		*/
		/**
		 * 显示图片
		 * 参数1：图片url
		 * 参数2：显示图片的控件
		 * 参数3：显示图片的设置
		 * 参数4：监听器
		 */
		if (list.get(start).getUhead().equalsIgnoreCase("")) {
			hold.UserHead.setImageResource(R.drawable.default_users_avatar);
		} else {
			hold.UserHead.setVisibility(View.VISIBLE);
			mimageLoader.displayImage(Model.USERHEADURL + list.get(start).getUhead(), 
					hold.UserHead, headoptions, animateFirstListener);
		}
	}
	public void pageMainImgLoad(final int start) {		
		/**
		 * 显示图片
		 * 参数1：图片url
		 * 参数2：显示图片的控件
		 * 参数3：显示图片的设置
		 * 参数4：监听器
		 */
		if (list.get(start).getQimg().equalsIgnoreCase("")) {
			hold.MainImg.setVisibility(View.GONE);
		} else {
			hold.MainImg.setVisibility(View.VISIBLE);
			mimageLoader.displayImage(Model.BIMGURL + list.get(start).getQimg(), 
					hold.MainImg, options, animateFirstListener);
		}
	}
	/**
	 * 图片加载第一次显示监听器
	 * @author Administrator
	 *
	 */
	private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {
		
		static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				// 是否第一次显示
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					// 图片淡入效果
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				}
			}
		}
	}
	
	public void pageImgLoad(final int start) {
		hold.MainImg.setImageResource(R.drawable.default_content_pic);
		if (list.get(start).getQimg().equalsIgnoreCase("")) {
			hold.MainImg.setVisibility(View.GONE);
		} else {
			hold.MainImg.setVisibility(View.VISIBLE);
			hold.MainImg.setTag(Model.USERHEADURL + list.get(start).getQimg());
	
			Bitmap bitMain = loadImgMainImg.loadImage(hold.MainImg,
					Model.BIMGURL + list.get(start).getQimg(),
					new ImageDownloadCallBack() {
						@Override
						public void onImageDownload(ImageView imageView,
								Bitmap bitmap) {
						//	if (imageView.getTag().equals(
						//			Model.BIMGURL + list.get(start).getQimg())) {
								imageView.setMinimumHeight(WidowWidth*bitmap.getHeight()/bitmap.getWidth());
								imageView.setMinimumWidth(WidowWidth);
								imageView.setImageBitmap(bitmap);
							//	notifyDataSetChanged();
							//}
						}
					});
			if (bitMain != null) {
				hold.MainImg.setMinimumHeight(WidowWidth*bitMain.getHeight()/bitMain.getWidth());
				hold.MainImg.setMinimumWidth(WidowWidth);
				hold.MainImg.setImageBitmap(bitMain);
			}
		}
	}
	@Override
	public View getView(final int arg0, View arg1, ViewGroup arg2) {
		
		listpos = arg0;
		if (arg1 == null) {
			hold = new Holder();
			arg1 = View.inflate(ctx, R.layout.form_listview_item, null);
			hold.UserHead = (ImageView) arg1.findViewById(R.id.Item_UserHead);
			hold.UserName = (TextView) arg1.findViewById(R.id.Item_UserName);
			hold.MainText = (TextView) arg1.findViewById(R.id.Item_MainText);
			hold.MainImg = (ImageView) arg1.findViewById(R.id.Item_MainImg);
			hold.Up = (LinearLayout) arg1.findViewById(R.id.Item_Up);
			hold.Up_Img = (ImageView) arg1.findViewById(R.id.Item_Up_img);
			hold.Up_text = (TextView) arg1.findViewById(R.id.Item_Up_text);
			hold.Down = (LinearLayout) arg1.findViewById(R.id.Item_Down);
			hold.Down_Img = (ImageView) arg1.findViewById(R.id.Item_Down_img);
			hold.Down_text = (TextView) arg1.findViewById(R.id.Item_Down_text);
			hold.ShareNum = (TextView) arg1.findViewById(R.id.Item_ShareNum);
			hold.Share = (LinearLayout) arg1.findViewById(R.id.Item_Share);
			hold.Share_Img = (ImageView) arg1.findViewById(R.id.Item_Share_img);
			arg1.setTag(hold);
		} else {
			hold = (Holder) arg1.getTag();
		}

		hold.UserName.setText(list.get(arg0).getUname());
		hold.MainText.setText(list.get(arg0).getQvalue());
		hold.Up_text.setText(list.get(arg0).getQlike());
		hold.Down_text.setText("-" + list.get(arg0).getQunlike());
		hold.ShareNum.setText(list.get(arg0).getQshare());
		hold.Up.setTag(list.get(arg0));
		hold.Down.setTag(list.get(arg0));
		if(sp.getString(list.get(arg0).getQid(), "").equals("like"))
		{
			hold.Up.setBackgroundResource(R.drawable.button_vote_active);
			hold.Up_Img.setImageResource(R.drawable.icon_for_active);
			hold.Up_text.setTextColor(Color.RED);
			upFlag = true;
			downFlag = false;
		}
		else if(sp.getString(list.get(arg0).getQid(), "").equals("unlike"))
		{
			hold.Down.setBackgroundResource(R.drawable.button_vote_active);
			hold.Down_Img.setImageResource(R.drawable.icon_for_active);
			hold.Down_text.setTextColor(Color.RED);
			upFlag = false;
			downFlag = true;
		}
		// 设置监听
		hold.Up.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String num;
				if (upFlag || downFlag) {// 判断是否提交过
					if (!upFlag) {// 判断提交的是不是顶 如果不是则操作
						hold.Down
								.setBackgroundResource(R.drawable.button_vote_enable);
						hold.Down_Img
								.setImageResource(R.drawable.icon_for_enable);
						hold.Down_text.setTextColor(Color.parseColor("#815F3D"));
						num = String.valueOf(Integer.parseInt(((AshamedInfo) (v.getTag()))
								.getQunlike()) - 1);
						hold.Down_text.setText("-" + num);
						list.get(arg0).setQunlike(num);
						num = String.valueOf(Integer.parseInt(((AshamedInfo) (v.getTag()))
								.getQlike()) + 1);
						hold.Up_text.setText(num);
						list.get(arg0).setQlike(num);
					}
				} else {
					num = String.valueOf(Integer.parseInt(((AshamedInfo) (v.getTag()))
							.getQlike()) + 1);
					hold.Up_text.setText(num);
					list.get(arg0).setQlike(num);
				}
				upFlag = true;
				downFlag = false;
				hold.Up.setBackgroundResource(R.drawable.button_vote_active);
				hold.Up_Img.setImageResource(R.drawable.icon_for_active);
				hold.Up_text.setTextColor(Color.RED);
			//	hold.Up.setTag("0");
			//	updatevalue(list.get(arg0).getQid(),list.get(arg0).getQlike(),
			//			list.get(arg0).getQunlike());
			}
		});
		hold.Down.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String num;
				if (upFlag || downFlag) {
					if (!downFlag) {
						hold.Up.setBackgroundResource(R.drawable.button_vote_enable);
						hold.Up_Img
								.setImageResource(R.drawable.icon_against_enable);
						hold.Up_text.setTextColor(Color.parseColor("#815F3D"));
						num = String.valueOf(Integer.parseInt(list.get(arg0)
								.getQlike()) - 1);
						hold.Up_text.setText(num);
						list.get(arg0).setQlike(num);
						num = String.valueOf(Integer.parseInt(list.get(arg0)
								.getQunlike()) + 1);
						hold.Down_text.setText("-" + num);
						list.get(arg0).setQunlike(num);
					}
				} else {
					num = String.valueOf(Integer.parseInt(list.get(arg0)
							.getQunlike()) + 1);
					hold.Down_text.setText("-" + num);
					list.get(arg0).setQlike(num);
				}
				upFlag = false;
				downFlag = true;
				hold.Down.setBackgroundResource(R.drawable.button_vote_active);
				hold.Down_Img.setImageResource(R.drawable.icon_for_active);
				hold.Down_text.setTextColor(Color.RED);
				updatevalue(list.get(arg0).getQid(),list.get(arg0).getQlike(),
						list.get(arg0).getQunlike());
			}
		});
		hold.Share.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				Toast.makeText(ctx, "分享被点击", 1).show();
			}
		});
		hold.UserHead.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				Toast.makeText(ctx, "点击发送小纸条", 1).show();
			}
		});
		/*
		hold.MainImg.setOnClickListener(new View.OnClickListener() {
			public void onClick(View currview) {
			//	GoAshamedDetail (arg0)
	//			Toast.makeText(ctx, "查看大图被点击", 1).show();
				 Intent intent=new Intent(ctx, ImageviewActivity.class);  
				 Bundle bundle=new Bundle();  
				 bundle.putString("path",Model.BIMGURL + list.get(arg0).getQimg());  
				 // bundle.putString("pass", "123");  
				 //把附加的数据放到意图当中  
				intent.putExtras(bundle);  
				 //执行意图  
				ctx.startActivity(intent); 
			}
		});

		hold.Up.setTag(list.get(arg0).getQid());
		// Log.e("liuxiaowei", hold.Up.getTag().toString());
		hold.Up.setBackgroundResource(R.drawable.button_vote_enable);
		hold.Up_Img.setImageResource(R.drawable.icon_against_enable);
		hold.Up_text.setTextColor(Color.parseColor("#815F3D"));
		if (hold.Up.getTag().equals("0")) {
			hold.Up.setBackgroundResource(R.drawable.button_vote_active);
			hold.Up_Img.setImageResource(R.drawable.icon_for_active);
			hold.Up_text.setTextColor(Color.RED);
		}
		*/

		pageHeadImgLoad(arg0);
		pageMainImgLoad(arg0);

		return arg1;
	}
	private void updatevalue(String qid,String up,String down) {
		
		String endParames = Model.UPDATEVALUE + "qid=" + qid + "&qlike="
				+ up + "&qunlike=" + down;
		ThreadPoolUtils.execute(new HttpGetThread(hand, endParames));
	} 
	Handler hand = new Handler() {

		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
	
		};

	};
	static class Holder {
		ImageView UserHead;
		TextView UserName;
		TextView MainText;
		ImageView MainImg;
		LinearLayout Up;
		ImageView Up_Img;
		TextView Up_text;
		LinearLayout Down;
		ImageView Down_Img;
		TextView Down_text;
		TextView ShareNum;
		LinearLayout Share;
		ImageView Share_Img;
	}
}
