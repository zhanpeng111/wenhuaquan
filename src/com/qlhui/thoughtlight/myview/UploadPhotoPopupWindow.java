
package com.qlhui.thoughtlight.myview;

import com.qlhui.thoughtlight.R;

import android.widget.PopupWindow;
import android.view.View;
import android.widget.ImageView;
import android.view.animation.Animation;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

public class UploadPhotoPopupWindow extends PopupWindow {
    private View conentView;
    private ImageView mimagephoto;
    private ImageView mimagehelp,mimagebaijia;
    private ImageView mimagecross,mimageclose;
    private Animation mrotatein;
    private Animation mrotateout;
    private Animation mscalein1;
    private Animation mscaleout1;
    private Animation mscalein2;
    private Animation mscaleout2;
    
    public UploadPhotoPopupWindow(Activity context, View.OnClickListener p2) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context  
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
        conentView = inflater.inflate(R.layout.popup_mainpage_upload, null);  

        mimagephoto = (ImageView)conentView.findViewById(R.id.image_photo);
     //   mimagecross = (ImageView)conentView.findViewById(R.id.image_cross);
        mimagehelp = (ImageView)conentView.findViewById(R.id.image_help);
        
        mimageclose = (ImageView)conentView.findViewById(R.id.image_close);
        mimagephoto.setOnClickListener(p2);
  //      mimagecross.setOnClickListener(p2);
        mimagehelp.setOnClickListener(p2);
        mimageclose.setOnClickListener(p2);
        mrotatein = AnimationUtils.loadAnimation(context, R.anim.rotate_in);
        mrotateout = AnimationUtils.loadAnimation(context, R.anim.rotate_out);
        mscalein1 = AnimationUtils.loadAnimation(context, R.anim.scale_in_1);
        mscaleout1 = AnimationUtils.loadAnimation(context, R.anim.scale_out_1);
        mscalein2 = AnimationUtils.loadAnimation(context, R.anim.scale_in_1);
        mscaleout2 = AnimationUtils.loadAnimation(context, R.anim.scale_out_1);
        mrotatein.setFillAfter(true);
        mrotateout.setFillAfter(true);
        mrotatein.setFillEnabled(true);
        mrotateout.setFillEnabled(true);
        mimagephoto.startAnimation(mscalein1);
    //    mimagecross.startAnimation(mscalein2);
        mimagehelp.startAnimation(mscalein2);
        mimageclose.startAnimation(mrotatein);
        setContentView(conentView);
        setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        setHeight(WindowManager.LayoutParams.MATCH_PARENT);
        setFocusable(true);
        ColorDrawable a = new ColorDrawable(0x73000000);
        setAnimationStyle(R.style.AnimAlpha);
        setBackgroundDrawable(a);
      //  a.setOnTouchListener(new atm(this));
    }
}
