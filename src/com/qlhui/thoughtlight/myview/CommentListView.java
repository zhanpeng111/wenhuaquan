package com.qlhui.thoughtlight.myview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * 计算listview的高度加载到scrollview中
 * 
 * @author 534429149
 * 
 */

public class CommentListView extends ListView {
	public CommentListView(Context context) {
		super(context);
	}

	public CommentListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CommentListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}

}
