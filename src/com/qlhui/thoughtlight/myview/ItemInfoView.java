/**
  * Generated by smali2java 1.0.0.558
  * Copyright (C) 2013 Hensence.com
  */

package com.qlhui.thoughtlight.myview;

import com.qlhui.thoughtlight.ForumDetailActivity;
import com.qlhui.thoughtlight.R;
import com.qlhui.thoughtlight.info.BaijiaInfo;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;


public class ItemInfoView extends LinearLayout {
    private TextView minfo_brief_content;
    
    public ItemInfoView(Context ctx) {
        super(ctx);
        init(ctx);
    }
    
    public ItemInfoView(Context ctx, AttributeSet p2) {
        super(ctx, p2);
        init(ctx);
    }
    
    public void init(Context ctx) {
        setOrientation(0x1);
       
        LayoutInflater.from(ctx).inflate(R.layout.forum_detail_info, this, true);
        setVisibility(View.VISIBLE);
        minfo_brief_content = (TextView)findViewById(R.id.info_brief_content);
    }
    
    public void setData(BaijiaInfo p1) {
    	minfo_brief_content.setText(p1.getUexplain());
    }
 
}