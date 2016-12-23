package com.qlhui.thoughtlight.myview;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.qlhui.thoughtlight.ForumDetailActivity;
import com.qlhui.thoughtlight.R;
import com.qlhui.thoughtlight.info.BaijiaInfo;
import com.qlhui.thoughtlight.model.Model;
import com.qlhui.thoughtlight.utils.LoadImg;

import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;


public class ItemHeaderView extends RelativeLayout {
   private TextView mthread_num;
   private TextView mfans_num;
   private ImageView mheadimg;
   private TextView mforum_name;
   private TextView mforum_authen;
   private Context mContext;
   
   public ItemHeaderView(Context p1) {
       super(p1);
       init(p1);
   }
   
   public ItemHeaderView(Context p1, AttributeSet p2) {
       super(p1, p2);
       init(p1);
   }
   
   public ItemHeaderView(Context p1, AttributeSet p2, int p3) {
       super(p1, p2, p3);
       init(p1);
   }
   
   public void init(Context p1) {
       mContext = p1;
       LayoutInflater.from(mContext).inflate(R.layout.forum_detail_header, this, true);
       setVisibility(View.VISIBLE);
       mheadimg = (ImageView)findViewById(R.id.h_forum_portrait);
       mforum_name = (TextView)findViewById(R.id.h_forum_name);
       mforum_authen = (TextView)findViewById(R.id.forum_authen);
       mfans_num = (TextView)findViewById(R.id.h_fans_num);
       mthread_num = (TextView)findViewById(R.id.h_thread_num);
   }
   
   public void setData(BaijiaInfo info,ImageLoader loadimg,DisplayImageOptions option) {
       if(info == null) {
           return;
       }
       mforum_name.setText(info.getUname());
       mforum_authen.setText(info.getUage());
       mfans_num.setText("102");
       mthread_num.setText("102");
       loadimg.displayImage(Model.BJHEADURL + info.getUhead(), mheadimg, option, null);
   }
   

}
