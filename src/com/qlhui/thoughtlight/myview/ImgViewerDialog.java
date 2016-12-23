
/**************************************************************************************
* [Project]
*       MyProgressDialog
* [Package]
*       com.lxd.widgets
* [FileName]
*       CustomProgressDialog.java
* [Copyright]
*       Copyright 2012 LXD All Rights Reserved.
* [History]
*       Version          Date              Author                        Record
*--------------------------------------------------------------------------------------
*       1.0.0           2012-4-27         lxd (rohsuton@gmail.com)        Create
**************************************************************************************/
	
package com.qlhui.thoughtlight.myview;


import com.qlhui.thoughtlight.R;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;


/********************************************************************
 * [Summary]
 *       TODO ���ڴ˴���Ҫ����������ʵ�ֵĹ��ܡ���Ϊ����ע����Ҫ��Ϊ����IDE����������tip��������ؼ�����Ҫ
 * [Remarks]
 *       TODO ���ڴ˴���ϸ������Ĺ��ܡ����÷�����ע������Լ���������Ĺ�ϵ.
 *******************************************************************/

public class ImgViewerDialog extends Dialog {
	private Context context = null;
	private static ImgViewerDialog customProgressDialog = null;
	private ImageView mimageView;
	public ImgViewerDialog(Context context){
		super(context);
		this.context = context;
	}
	
	public ImgViewerDialog(Context context, int theme) {
        super(context, theme);
    }
	
	public static ImgViewerDialog createDialog(Context context){
		customProgressDialog = new ImgViewerDialog(context,R.style.Dialog_Fullscreen);
		customProgressDialog.setContentView(R.layout.imgviewerdialog);
		customProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;

		return customProgressDialog;
	}
 
    public void onWindowFocusChanged(boolean hasFocus){
    	
    	if (customProgressDialog == null){
    		return;
    	}
    	
  
     //   AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getBackground();
     //   animationDrawable.start();
    }
 
    /**
     * 
     * [Summary]
     *       setTitile ����
     * @param strTitle
     * @return
     *
     */
    public void setImgbit(Bitmap img){
	  	mimageView = (ImageView) customProgressDialog.findViewById(R.id.div_main);
    	mimageView.setImageBitmap(img);
    	
    }



}
