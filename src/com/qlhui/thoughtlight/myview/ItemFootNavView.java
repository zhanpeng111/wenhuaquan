package com.qlhui.thoughtlight.myview;


import com.qlhui.thoughtlight.R;

import android.widget.LinearLayout;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import android.view.View;
import android.content.Context;
import android.util.AttributeSet;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.widget.Toast;
import android.content.res.Resources;
import android.content.Intent;

public class ItemFootNavView extends LinearLayout {
   private ViewGroup mfn_care_box;
   private TextView mfn_care_btn;
   private ImageView micon_brief_care;
   private ViewGroup mfn_enter_box;
   private View mfootline;
   private Context mContext;
   private boolean bdl = false;
   
   public ItemFootNavView(Context p1) {
       super(p1);
       init(p1);
   }
   
   public ItemFootNavView(Context p1, AttributeSet p2) {
       super(p1, p2);
       init(p1);
   }
   
   public void init(Context p1) {
       mContext = p1;
       LayoutInflater.from(mContext).inflate(R.layout.forum_detail_foot_nav, this, true);
       setOrientation(0x1);
       setVisibility(0x8);
       
       mfn_care_box = (ViewGroup)findViewById(R.id.fn_care_box);
       mfn_care_btn = (TextView)findViewById(R.id.fn_care_btn);
       micon_brief_care = (ImageView)findViewById(R.id.icon_brief_care);
       mfn_enter_box = (ViewGroup)findViewById(R.id.fn_enter_box);
       mfootline = findViewById(R.id.foot_line);
   }
   
}
