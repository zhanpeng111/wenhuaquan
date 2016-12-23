package com.qlhui.thoughtlight.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.qlhui.thoughtlight.R;
import com.qlhui.thoughtlight.adapter.ArticleListAdapter.Holder;
import com.qlhui.thoughtlight.info.BaijiaInfo;
import com.qlhui.thoughtlight.info.BJXUserInfo;
import com.qlhui.thoughtlight.model.Model;
import com.qlhui.thoughtlight.utils.LoadImg;
import com.qlhui.thoughtlight.utils.LoadImg.ImageDownloadCallBack;

public class BaijiaAdapter extends MybaseAdapter {
	DisplayImageOptions headoptions;

	private List<BaijiaInfo> list;
	private Context ctx;
	private LoadImg loadImgHeadImg;
	private Holder hold;
	public BaijiaAdapter(Context ctx, List<BaijiaInfo> list) {
		this.ctx = ctx;
		this.list = list;

		headoptions = new DisplayImageOptions.Builder()
			.showStubImage(R.drawable.default_users_avatar)			// 设置图片下载期间显示的图片
			.showImageForEmptyUri(R.drawable.default_users_avatar)	// 设置图片Uri为空或是错误的时候显示的图片
			.showImageOnFail(R.drawable.default_users_avatar)		// 设置图片加载或解码过程中发生错误显示的图片	
			.cacheInMemory(true)						// 设置下载的图片是否缓存在内存中
			.cacheOnDisc(true)							// 设置下载的图片是否缓存在SD卡中
			.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
			.displayer(new RoundedBitmapDisplayer(20))	// 设置成圆角图片
			.build();									// 创建配置过得DisplayImageOption对象

	//	loadImgHeadImg = new LoadImg(ctx);
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
	public void pageNearImgLoad(final int start) {		
		/**
		 * 显示图片
		 * 参数1：图片url
		 * 参数2：显示图片的控件
		 * 参数3：显示图片的设置
		 * 参数4：监听器
		 */
		if (list.get(start).getUhead().equalsIgnoreCase("")) {
			hold.Near_Img.setImageResource(R.drawable.default_users_avatar);
		} else {
			hold.Near_Img.setVisibility(View.VISIBLE);
			mimageLoader.displayImage(Model.BJHEADURL + list.get(start).getUhead(), 
					hold.Near_Img, headoptions, null);
		}
	}
	@Override
	public View getView(final int arg0, View arg1, ViewGroup arg2) {

		if (arg1 == null) {
			hold = new Holder();
			arg1 = View.inflate(ctx, R.layout.item_grid_image, null);
			hold.Near_UserName = (TextView) arg1
					.findViewById(R.id.forum_name);
			hold.Near_Distance = (TextView) arg1
					.findViewById(R.id.forum_rank);
			hold.Near_Sex = (TextView) arg1.findViewById(R.id.forum_rank);
			hold.Near_UserInfo = (TextView) arg1
					.findViewById(R.id.forum_intro);
			hold.Near_Img = (ImageView) arg1.findViewById(R.id.forum_image);
			arg1.setTag(hold);
		} else {
			hold = (Holder) arg1.getTag();
		}
		hold.Near_UserName.setText(list.get(arg0).getUname());
		hold.Near_Distance.setText("0.1km | 1天前");
		hold.Near_Sex.setBackgroundResource(R.drawable.ic_label_green);
		hold.Near_Sex.setText("0");
		hold.Near_Sex.setVisibility(View.VISIBLE);
		if (!list.get(arg0).getUage().equalsIgnoreCase("null")) {
			hold.Near_Sex.setText(list.get(arg0).getUage());
			if (list.get(arg0).getUsex().equals("0")) {
				hold.Near_Sex
						.setBackgroundResource(R.drawable.ic_label_green);
			} else {
				hold.Near_Sex
						.setBackgroundResource(R.drawable.ic_label_green);
			}
		} else {
			hold.Near_Sex.setVisibility(View.GONE);
		}
		hold.Near_UserInfo.setVisibility(View.VISIBLE);
		if (!list.get(arg0).getUexplain().equalsIgnoreCase("null")) {
			hold.Near_UserInfo.setText(list.get(arg0).getUexplain());
		} else {
			hold.Near_UserInfo.setVisibility(View.GONE);
		}
		pageNearImgLoad(arg0);
		/*
		hold.Near_Img.setImageResource(R.drawable.default_users_avatar);
		if (list.get(arg0).getUhead().equalsIgnoreCase("")) {
			hold.Near_Img.setImageResource(R.drawable.default_users_avatar);
		} else {
			hold.Near_Img.setTag(Model.USERHEADURL + list.get(arg0).getUhead());
			Bitmap bitHead = loadImgHeadImg.loadImage(hold.Near_Img,
					Model.USERHEADURL + list.get(arg0).getUhead(),
					new ImageDownloadCallBack() {
						@Override
						public void onImageDownload(ImageView imageView,
								Bitmap bitmap) {
							if (hold.Near_Img.getTag().equals(
									Model.USERHEADURL
											+ list.get(arg0).getUhead())) {
								hold.Near_Img.setImageBitmap(bitmap);
							}
						}
					});
			if (bitHead != null) {
				hold.Near_Img.setImageBitmap(bitHead);
			}
			
		}
		 */
		return arg1;
	}

	static class Holder {
		TextView Near_UserName;// 用户名
		TextView Near_Distance;// 距离和上次登录格式(0.1km | 1天前)
		TextView Near_Sex;// text是年龄 背景是性别
		TextView Near_UserInfo;// 用户的简介
		ImageView Near_Img;// 用户头像

	}

}
