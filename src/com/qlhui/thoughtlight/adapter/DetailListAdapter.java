package com.qlhui.thoughtlight.adapter;

import java.util.List;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.qlhui.thoughtlight.R;
import com.qlhui.thoughtlight.adapter.ArticleListAdapter.Holder;
import com.qlhui.thoughtlight.info.CommentsInfo;
import com.qlhui.thoughtlight.model.Model;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class DetailListAdapter extends MybaseAdapter {
	DisplayImageOptions options,headoptions;		// DisplayImageOptions是用于设置图片显示的类

	private List<CommentsInfo> list;
	private Context ctx;
	private Holder hold;
	public DetailListAdapter(Context ctx, List<CommentsInfo> list) {
		this.ctx = ctx;
		this.list = list;
		headoptions = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.default_users_avatar)			// 设置图片下载期间显示的图片
		.showImageForEmptyUri(R.drawable.default_users_avatar)	// 设置图片Uri为空或是错误的时候显示的图片
		.showImageOnFail(R.drawable.default_users_avatar)		// 设置图片加载或解码过程中发生错误显示的图片	
		.cacheInMemory(true)						// 设置下载的图片是否缓存在内存中
		.cacheOnDisc(true)							// 设置下载的图片是否缓存在SD卡中
		.displayer(new RoundedBitmapDisplayer(20))	// 设置成圆角图片
		.build();									// 创建配置过得DisplayImageOption对象

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
	public void setImgloader(ImageLoader loader ) {
		this.mimageLoader = loader;
	}
	public void pageHeadImgLoad(final int start) {	
		
			if (list.get(start).getUhead().equalsIgnoreCase("")) {
				hold.UserIcon.setImageResource(R.drawable.default_users_avatar);
			} else {
				mimageLoader.displayImage(Model.USERHEADURL + list.get(start).getUhead(), 
						hold.UserIcon, headoptions, null);
			}
		}
	@Override
	public View getView(final int arg0, View arg1, ViewGroup arg2) {

		if (arg1 == null) {
			hold = new Holder();
			arg1 = View.inflate(ctx, R.layout.comment_list_item, null);
			hold.UserName = (TextView) arg1
					.findViewById(R.id.Detail_Item_UserName);
			hold.UserIcon = (ImageView) arg1
					.findViewById(R.id.userIcon);
			
			hold.Num = (TextView) arg1.findViewById(R.id.Detail_Item_Num);
			hold.Value = (TextView) arg1.findViewById(R.id.Detail_Item_Value);
			arg1.setTag(hold);
		} else {
			hold = (Holder) arg1.getTag();
		}
		pageHeadImgLoad(arg0);
		hold.UserName.setText(list.get(arg0).getUname());
		hold.Num.setText("" + (arg0 + 1));
		hold.Value.setText(list.get(arg0).getCvalue());
		hold.UserName.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Toast.makeText(ctx, list.get(arg0).getUname(), 1).show();
			}
		});
		return arg1;
	}

	static class Holder {
		TextView UserName;
		ImageView UserIcon;
		TextView Num;
		TextView Value;
	}

}
