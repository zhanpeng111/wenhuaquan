package com.qlhui.thoughtlight.myview;
import java.util.Timer;
import java.util.TimerTask;

import com.qlhui.thoughtlight.R;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * 更多详解见博客http://blog.csdn.net/zhongkejingwang/article/details/38868463
 * 
 * @author 陈靖
 */
public class PullToRefreshLayout extends RelativeLayout
{
	public static final String TAG = "PullToRefreshLayout";
	// 初始状态
	public static final int INIT = 0;
	// 释放刷新
	public static final int RELEASE_TO_REFRESH = 1;
	// 正在刷新
	public static final int REFRESHING = 2;
	// 操作完毕
	public static final int DONE = 3;
	// 当前状态
	private int state = INIT;
	// 刷新回调接口
	private OnRefreshListener mListener;
	// 刷新成功
	public static final int SUCCEED = 0;
	// 刷新失败
	public static final int FAIL = 1;
	// 按下Y坐标，上一个事件点Y坐标
	private float downY, lastY;
	
	// pull state
	private static final int PULL_UP_STATE = 0;
	private static final int PULL_DOWN_STATE = 1;
	
	private int mPullState;
	/**
	 * list or grid
	 */
	private AdapterView<?> mAdapterView;
	// 下拉的距离。注意：pullDownY和pullUpY不可能同时不为0
	public float pullDownY = 0;
	/**
	 * last y
	 */
	private int mLastMotionY;
	// 释放刷新的距离
	private float refreshDist = 200;

	private MyTimer timer;
	// 回滚速度
	public float MOVE_SPEED = 8;
	// 第一次执行布局
	private boolean isLayout = false;
	// 在刷新过程中滑动操作
	private boolean isTouch = false;
	// 手指滑动距离与下拉头的滑动距离比，中间会随正切函数变化
	private float radio = 2;

	// 下拉箭头的转180°动画
	private RotateAnimation rotateAnimation;
	// 均匀旋转动画
	private RotateAnimation refreshingAnimation;

	// 下拉头
	private View refreshView;
	// 下拉的箭头
	private View pullView;
	// 正在刷新的图标
	private View refreshingView;
	// 刷新结果图标
	private View refreshStateImageView;
	// 刷新结果：成功或失败
	private TextView refreshStateTextView;

	// 实现了Pullable接口的View
	private View pullableView;
	// 过滤多点触碰
	private int mEvents;

	/**
	 * 执行自动回滚的handler
	 */
	Handler updateHandler = new Handler()
	{

		@Override
		public void handleMessage(Message msg)
		{
			// 回弹速度随下拉距离moveDeltaY增大而增大
			MOVE_SPEED = (float) (8 + 5 * Math.tan(Math.PI / 2
					/ getMeasuredHeight() * (pullDownY)));
			if (!isTouch)
			{
				// 正在刷新，且没有往上推的话则悬停，显示"正在刷新..."
				if (state == REFRESHING && pullDownY <= refreshDist)
				{
					pullDownY = refreshDist;
					timer.cancel();
				}

			}
			if (pullDownY > 0)
				pullDownY -= MOVE_SPEED;
			if (pullDownY <= 0)
			{
				// 已完成回弹
				pullDownY = 0;
				pullView.clearAnimation();
				// 隐藏下拉头时有可能还在刷新，只有当前状态不是正在刷新时才改变状态
				if (state != REFRESHING)
					changeState(INIT);
				timer.cancel();
			}
			// 刷新布局,会自动调用onLayout
			requestLayout();
		}

	};

	public void setOnRefreshListener(OnRefreshListener listener)
	{
		mListener = listener;
	}

	public PullToRefreshLayout(Context context)
	{
		super(context);
		initView(context);
	}

	public PullToRefreshLayout(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initView(context);
	}

	public PullToRefreshLayout(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		initView(context);
	}

	private void initView(Context context)
	{
		timer = new MyTimer(updateHandler);
		rotateAnimation = (RotateAnimation) AnimationUtils.loadAnimation(
				context, R.anim.reverse_anim);
		refreshingAnimation = (RotateAnimation) AnimationUtils.loadAnimation(
				context, R.anim.rotating);
		// 添加匀速转动动画
		LinearInterpolator lir = new LinearInterpolator();
		rotateAnimation.setInterpolator(lir);
		refreshingAnimation.setInterpolator(lir);
	
		
	}
	/**
	 * init AdapterView like ListView,GridView and so on;or init ScrollView
	 * 
	 * @description hylin 2012-7-30下午8:48:12
	 */
	private void initContentAdapterView() {
		int count = getChildCount();
		if (count < 2) {
			throw new IllegalArgumentException(
					"this layout must contain 3 child views,and AdapterView or ScrollView must in the second position!");
		}
		View view = null;
		for (int i = 0; i < count ; i++) {
			view = getChildAt(i);
			if (view instanceof AdapterView<?>) {
				mAdapterView = (AdapterView<?>) view;
			}

		}
		if (mAdapterView == null ) {
			throw new IllegalArgumentException(
					"must contain a AdapterView or ScrollView in this layout!");
		}
	}
	private void hide()
	{
		timer.schedule(5);
	}

	/**
	 * 完成刷新操作，显示刷新结果。注意：刷新完成后一定要调用这个方法
	 */
	/**
	 * @param refreshResult
	 *            PullToRefreshLayout.SUCCEED代表成功，PullToRefreshLayout.FAIL代表失败
	 */
	public void refreshFinish(int refreshResult)
	{
		refreshingView.clearAnimation();
		refreshingView.setVisibility(View.GONE);
		switch (refreshResult)
		{
		case SUCCEED:
			// 刷新成功
			refreshStateImageView.setVisibility(View.VISIBLE);
			refreshStateTextView.setText(R.string.refresh_succeed);
			refreshStateImageView
					.setBackgroundResource(R.drawable.refresh_succeed);
			break;
		case FAIL:
		default:
			// 刷新失败
			refreshStateImageView.setVisibility(View.VISIBLE);
			refreshStateTextView.setText(R.string.refresh_fail);
			refreshStateImageView
					.setBackgroundResource(R.drawable.refresh_failed);
			break;
		}
		// 刷新结果停留1秒
		new Handler()
		{
			@Override
			public void handleMessage(Message msg)
			{
				changeState(DONE);
				hide();
			}
		}.sendEmptyMessageDelayed(0, 500);
	}

	private void changeState(int to)
	{
		state = to;
		switch (state)
		{
		case INIT:
			// 下拉布局初始状态
			refreshStateImageView.setVisibility(View.GONE);
			refreshStateTextView.setText(R.string.pull_to_refresh);
			pullView.clearAnimation();
			pullView.setVisibility(View.VISIBLE);
			break;
		case RELEASE_TO_REFRESH:
			// 释放刷新状态
			refreshStateTextView.setText(R.string.release_to_refresh);
			pullView.startAnimation(rotateAnimation);
			break;
		case REFRESHING:
			// 正在刷新状态
			pullView.clearAnimation();
			refreshingView.setVisibility(View.VISIBLE);
			pullView.setVisibility(View.INVISIBLE);
			refreshingView.startAnimation(refreshingAnimation);
			refreshStateTextView.setText(R.string.refreshing);
			break;
		case DONE:
			// 刷新完毕，啥都不做
			break;
		}
	}
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		// footer view 在此添加保证添加到linearlayout中的最后
		//addFooterView();
		initContentAdapterView();
	}
	/*
	 * （非 Javadoc）由父控件决定是否分发事件，防止事件冲突
	 * 
	 * @see android.view.ViewGroup#dispatchTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onTouchEvent(MotionEvent ev)
	{
		switch (ev.getActionMasked())
		{
		case MotionEvent.ACTION_DOWN:
			downY = ev.getY();
			lastY = downY;
			timer.cancel();
			mEvents = 0;
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
		case MotionEvent.ACTION_POINTER_UP:
			// 过滤多点触碰
			mEvents = -1;
			break;
		case MotionEvent.ACTION_MOVE:
			if (mEvents == 0)
			{
				if (((Pullable) pullableView).canPullDown())
				{
					// 可以下拉，正在加载时不能下拉
					// 对实际滑动距离做缩小，造成用力拉的感觉
					pullDownY = pullDownY + (ev.getY() - lastY) / radio;
					if (pullDownY < 0)
					{
						pullDownY = 0;
					}
					if (pullDownY > getMeasuredHeight())
						pullDownY = getMeasuredHeight();
					if (state == REFRESHING)
					{
						// 正在刷新的时候触摸移动
						isTouch = true;
					}
				}
			} else
				mEvents = 0;
			lastY = ev.getY();
			// 根据下拉距离改变比例
			radio = (float) (2 + 2 * Math.tan(Math.PI / 2 / getMeasuredHeight()
					* (pullDownY)));
			if (mPullState == PULL_DOWN_STATE) 
				requestLayout();
			if (pullDownY <= refreshDist && state == RELEASE_TO_REFRESH)
			{
				// 如果下拉距离没达到刷新的距离且当前状态是释放刷新，改变状态为下拉刷新
				changeState(INIT);
			}
			if (pullDownY >= refreshDist && state == INIT)
			{
				// 如果下拉距离达到刷新的距离且当前状态是初始状态刷新，改变状态为释放刷新
				changeState(RELEASE_TO_REFRESH);
			}
			// 因为刷新和加载操作不能同时进行，所以pullDownY和pullUpY不会同时不为0，因此这里用(pullDownY +
			// Math.abs(pullUpY))就可以不对当前状态作区分了
			if ((pullDownY) > 8)
			{
				// 防止下拉过程中误触发长按事件和点击事件
				ev.setAction(MotionEvent.ACTION_CANCEL);
			}
			break;
			
		case MotionEvent.ACTION_UP:
			if (pullDownY > refreshDist)
				// 正在刷新时往下拉（正在加载时往上拉），释放后下拉头（上拉头）不隐藏
				isTouch = false;
			if (state == RELEASE_TO_REFRESH)
			{
				changeState(REFRESHING);
				// 刷新操作
				if (mListener != null)
					mListener.onRefresh(this);
			}
			if (mPullState == PULL_DOWN_STATE) 
				hide();
			
		default:
			break;
		}
		// 事件分发交给父类
		super.onTouchEvent(ev);
		return true;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent e) {
		int y = (int) e.getRawY();
		switch (e.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// 首先拦截down事件,记录y坐标
			mLastMotionY = y;
			break;
		case MotionEvent.ACTION_MOVE:
			// deltaY > 0 是向下运动,< 0是向上运动
			int deltaY = y - mLastMotionY;
			if (isRefreshViewScroll(deltaY)) {
				return true;
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			break;
		}
		return false;
	}
	/**
	 * 是否应该到了父View,即PullToRefreshView滑动
	 * 
	 * @param deltaY
	 *            , deltaY > 0 是向下运动,< 0是向上运动
	 * @return
	 */
	private boolean isRefreshViewScroll(int deltaY) {
		if (state == REFRESHING) {
			return false;
		}
		//对于ListView和GridView
		if (mAdapterView != null) {
			// 子view(ListView or GridView)滑动到最顶端
			if (deltaY > 0) {

				View child = mAdapterView.getChildAt(0);
				if (child == null) {
					// 如果mAdapterView中没有数据,不拦截
					return false;
				}
				if (mAdapterView.getFirstVisiblePosition() == 0
						&& child.getTop() == 0) {
					mPullState = PULL_DOWN_STATE;
					return true;
				}
				int top = child.getTop();
				int padding = mAdapterView.getPaddingTop();
				if (mAdapterView.getFirstVisiblePosition() == 0
						&& Math.abs(top - padding) <= 8) {//这里之前用3可以判断,但现在不行,还没找到原因
					mPullState = PULL_DOWN_STATE;
					return true;
				}

			} else if (deltaY < 0) {
				View lastChild = mAdapterView.getChildAt(mAdapterView
						.getChildCount() - 1);
				if (lastChild == null) {
					// 如果mAdapterView中没有数据,不拦截
					return false;
				}
				// 最后一个子view的Bottom小于父View的高度说明mAdapterView的数据没有填满父view,
				// 等于父View的高度说明mAdapterView已经滑动到最后
				if (lastChild.getBottom() <= getHeight()
						&& mAdapterView.getLastVisiblePosition() == mAdapterView
								.getCount() - 1) {
				//	state = PULL_UP_STATE;
					return false;
				}
			}
		}

		return false;
	}
	private void initView()
	{
		// 初始化下拉布局
		pullView = refreshView.findViewById(R.id.pull_icon);
		refreshStateTextView = (TextView) refreshView
				.findViewById(R.id.state_tv);
		refreshingView = refreshView.findViewById(R.id.refreshing_icon);
		refreshStateImageView = refreshView.findViewById(R.id.state_iv);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b)
	{
		if (!isLayout)
		{
			// 这里是第一次进来的时候做一些初始化
			refreshView = getChildAt(0);
			pullableView = getChildAt(1);
			isLayout = true;
			initView();
			refreshDist = ((ViewGroup) refreshView).getChildAt(0)
					.getMeasuredHeight();
		}
		// 改变子控件的布局，这里直接用(pullDownY + pullUpY)作为偏移量，这样就可以不对当前状态作区分
		refreshView.layout(0,
				(int) (pullDownY) - refreshView.getMeasuredHeight(),
				refreshView.getMeasuredWidth(), (int) (pullDownY));
		pullableView.layout(0, (int) (pullDownY),
				pullableView.getMeasuredWidth(), (int) (pullDownY)
						+ pullableView.getMeasuredHeight());
	}

	class MyTimer
	{
		private Handler handler;
		private Timer timer;
		private MyTask mTask;

		public MyTimer(Handler handler)
		{
			this.handler = handler;
			timer = new Timer();
		}

		public void schedule(long period)
		{
			if (mTask != null)
			{
				mTask.cancel();
				mTask = null;
			}
			mTask = new MyTask(handler);
			timer.schedule(mTask, 0, period);
		}

		public void cancel()
		{
			if (mTask != null)
			{
				mTask.cancel();
				mTask = null;
			}
		}

		class MyTask extends TimerTask
		{
			private Handler handler;

			public MyTask(Handler handler)
			{
				this.handler = handler;
			}

			@Override
			public void run()
			{
				handler.obtainMessage().sendToTarget();
			}

		}
	}

	/**
	 * 刷新加载回调接口
	 * 
	 * @author chenjing
	 * 
	 */
	public interface OnRefreshListener
	{
		/**
		 * 刷新操作
		 */
		void onRefresh(PullToRefreshLayout pullToRefreshLayout);

	}

}
