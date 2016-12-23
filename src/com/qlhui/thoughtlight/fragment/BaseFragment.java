package com.qlhui.thoughtlight.fragment;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;
import com.qlhui.thoughtlight.myview.PullToRefreshLayout;

import android.support.v4.app.Fragment;
import android.widget.ListView;

public class BaseFragment extends Fragment{
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	protected int mMotionY;
	protected PullToRefreshLayout mPullToRefreshView;


}
