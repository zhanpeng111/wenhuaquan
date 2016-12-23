package com.qlhui.thoughtlight.imageviewer;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.qlhui.thoughtlight.BaseActivity;
import com.qlhui.thoughtlight.R;
import com.qlhui.thoughtlight.BaseActivity.AnimateFirstDisplayListener;
import com.qlhui.thoughtlight.model.Model;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class ImageviewActivity extends BaseActivity {
	private int window_width, window_height;
	private DragImageView dragImageView;
	private ImageView msavebtn;
	private int state_height;
	DisplayImageOptions options;		// DisplayImageOptions是用于设置图片显示的类
	private ViewTreeObserver viewTreeObserver;
	private ProgressBar mProgressBar;
    private ProgressDialog mSaveDialog = null;  
    private Bitmap mBitmap;  
    private String mImgUrl;  
    private String mFileName;  
    private String mFilePath;  
    private String mSaveMessage; 
    private final static String ALBUM_PATH  
    = Environment.getExternalStorageDirectory() + "/beijixing/"; 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_imageviewer);
		/** ��ȡ��Ҋ����߶� **/
		WindowManager manager = getWindowManager();
		window_width = manager.getDefaultDisplay().getWidth();
		window_height = manager.getDefaultDisplay().getHeight();
	//	loadingCircleView = (LoadingCircleView) findViewById(R.id.loading_cirle_view);
		 options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.default_content_pic)			// 设置图片下载期间显示的图片
		.showImageForEmptyUri(R.drawable.default_content_pic)	// 设置图片Uri为空或是错误的时候显示的图片
		.showImageOnFail(R.drawable.default_content_pic)		// 设置图片加载或解码过程中发生错误显示的图片	
		.cacheInMemory(true)						// 设置下载的图片是否缓存在内存中
		.cacheOnDisc(true)							// 设置下载的图片是否缓存在SD卡中
		.imageScaleType(ImageScaleType.EXACTLY)
		.displayer(new RoundedBitmapDisplayer(0))	// 设置成圆角图片
		.build();	
		dragImageView = (DragImageView) findViewById(R.id.div_main);
		msavebtn = (ImageView) findViewById(R.id.saveBtn);
		msavebtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				  new Thread(connectNet).start();  
			}
			
			
		});
		//获取数据  
		Bundle bundle=this.getIntent().getExtras();  
	//	AsynImageLoader asynImageLoader = new AsynImageLoader();  
		mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
		mProgressBar.setVisibility(View.VISIBLE);
	//	asynImageLoader.showImageAsyn(dragImageView,window_width,window_height,  (String) bundle.get("path"), R.drawable.default_content_pic);  
		mImgUrl = (String) bundle.get("path");
		imageLoader.displayImage((String) bundle.get("path"), 
				dragImageView, options, new AnimateFirstDisplayListener(){
			
			final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				mProgressBar.setVisibility(View.GONE);
				if (loadedImage != null) {
					ImageView imageView = (ImageView) view;
					// 是否第一次显示
					boolean firstDisplay = !displayedImages.contains(imageUri);
					if (firstDisplay) {
						// 图片淡入效果
						FadeInBitmapDisplayer.animate(imageView, 500);
						displayedImages.add(imageUri);
					}
				}
			}
			
		});
	
	//	Bitmap bmp = BitmapUtil.ReadBitmapById(this, (String) bundle.get("path"),
	//			window_width, window_height);
		// ����ͼƬ
	//	dragImageView.setImageBitmap(bmp);
		dragImageView.setmActivity(this);//ע��Activity.
		/** ����״̬���߶� **/
		viewTreeObserver = dragImageView.getViewTreeObserver();
		viewTreeObserver
				.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

					@Override
					public void onGlobalLayout() {
						if (state_height == 0) {
							// ��ȡ״�����߶�
							Rect frame = new Rect();
							getWindow().getDecorView()
									.getWindowVisibleDisplayFrame(frame);
							state_height = frame.top;
							dragImageView.setScreen_H(window_height-state_height);
							dragImageView.setScreen_W(window_width);
						}

					}
				});

	}
	 /** 
     * 保存文件 
     * @param bm 
     * @param fileName 
     * @throws IOException 
     */  
    public void saveFile(Bitmap bm, String fileName) throws IOException {  
        File dirFile = new File(ALBUM_PATH);  
        if(!dirFile.exists()){  
            dirFile.mkdir();  
        }  
        File myCaptureFile = new File(ALBUM_PATH + fileName);  
        mFilePath = ALBUM_PATH+fileName;
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));  
        bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);  
        bos.flush();  
        bos.close();  

    }  
    
    /** 
     * Get image from newwork 
     * @param path The path of image 
     * @return InputStream 
     * @throws Exception 
     */  
    public InputStream getImageStream(String path) throws Exception{  
        URL url = new URL(path);  
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();  
        conn.setConnectTimeout(5 * 1000);  
        conn.setRequestMethod("GET");  
        if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){  
            return conn.getInputStream();  
        }  
        return null;  
    }
    /* 
     * 连接网络 
     * 由于在4.0中不允许在主线程中访问网络，所以需要在子线程中访问 
     */  
    private Runnable connectNet = new Runnable(){  
        @Override  
        public void run() {  
            try {  
             //   String filePath = "http://img.my.csdn.net/uploads/201402/24/1393242467_3999.jpg";  
                mFileName  = System.currentTimeMillis() + ".jpg";// 糗事图片
  
  
                //******** 方法2：取得的是InputStream，直接从InputStream生成bitmap ***********/  
                mBitmap = BitmapFactory.decodeStream(getImageStream(mImgUrl));  
                //********************************************************************/  
            	new Thread(saveFileRunnable).start(); 
                // 发送消息，通知handler在主线程中更新UI  
       //         connectHanlder.sendEmptyMessage(0);  
        //        Log.d(TAG, "set image ...");  
            } catch (Exception e) {  
                Toast.makeText(ImageviewActivity.this,"无法链接网络！", 1).show();  
                e.printStackTrace();  
            }  
  
        }  
  
    };  
    private Handler messageHandler = new Handler() {  
        @Override  
        public void handleMessage(Message msg) {  
      //      mSaveDialog.dismiss();  
           // Log.d(TAG, mSaveMessage);  
        	if(mBitmap!=null)
        	{
        		
        	 	msavebtn.setImageResource(R.drawable.username_correct); 
            	msavebtn.setOnClickListener(null);
        	}
       
            Toast.makeText(ImageviewActivity.this, mSaveMessage, Toast.LENGTH_SHORT).show();  
        }  
    };  
	  private Runnable saveFileRunnable = new Runnable(){  
	        @Override  
	        public void run() {  
	            try {  
	                saveFile(mBitmap, mFileName);  
	                mSaveMessage = "图片保存成功:"+mFilePath;  
	            } catch (IOException e) {  
	                mSaveMessage = "图片保存失败！";  
	                e.printStackTrace();  
	            }  
	            messageHandler.sendMessage(messageHandler.obtainMessage());  
	        }  
	  
	    };  


	/**
	 * ��ȡ������Դ��ͼƬ
	 * 
	 * @param context
	 * @param resId
	 * @return
	 */
	public static Bitmap ReadBitmapById(Context context, int resId) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		// ��ȡ��ԴͼƬ
		InputStream is = context.getResources().openRawResource(resId);
		return BitmapFactory.decodeStream(is, null, opt);
	}

}