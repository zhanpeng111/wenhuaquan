package com.qlhui.thoughtlight.adapter;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class MybaseAdapter extends BaseAdapter {
	protected ImageLoader mimageLoader;
	protected DisplayImageOptions options,headoptions;		// DisplayImageOptions是用于设置图片显示的类
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		return null;
	}

}
