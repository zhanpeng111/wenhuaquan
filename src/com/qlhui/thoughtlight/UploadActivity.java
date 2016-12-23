package com.qlhui.thoughtlight;

/**
 * 发表糗事
 * */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import com.aliyun.mbaas.oss.OSSClient;
import com.aliyun.mbaas.oss.callback.SaveCallback;
import com.aliyun.mbaas.oss.model.AccessControlList;
import com.aliyun.mbaas.oss.model.OSSException;
import com.aliyun.mbaas.oss.model.TokenGenerator;
import com.aliyun.mbaas.oss.storage.OSSBucket;
import com.aliyun.mbaas.oss.storage.OSSFile;
import com.aliyun.mbaas.oss.util.OSSLog;
import com.aliyun.mbaas.oss.util.OSSToolKit;

import com.qlhui.thoughtlight.CameralActivity.IMGCallBack;
import com.qlhui.thoughtlight.PhotoAct.IMGCallBack1;
import com.qlhui.thoughtlight.model.Model;
import com.qlhui.thoughtlight.net.ThreadPoolUtils;
import com.qlhui.thoughtlight.thread.HttpPostThread;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qlhui.thoughtlight.model.Model;
import com.qlhui.thoughtlight.net.ThreadPoolUtils;
import com.qlhui.thoughtlight.thread.HttpPostThread;
import com.qlhui.thoughtlight.utils.FileUtiles;



public class UploadActivity extends Activity {

	private ImageView mClose, mUpLoadEdit, mCamera, mAlbum,mInsertPicView,mdelete_img;
	private File sdcardTempFile;
	private final static int GET_IMG_SUCCESS = 0; 
	private ImageButton  mCameraBt, mAlbumBt;
	private TextView mTitle;
	private  Handler handlernoti=null;
	private EditText mNeirongEdit;
	private String data = "";
	private Bitmap  mLoadedBitmap= null;
	private RelativeLayout mAddimglayout;
	private static final int IMAGE_CODE = 0;
	private static final int REQUEST_CODE_CAMERA = 101;
	private String path="";
	private String muploadtype;
	private String mtid;
	static final String accessKey = "EOUetZQNHs9Hp1t3"; // 测试代码没有考虑AK/SK的安全性
	static final String screctKey = "650263";
	
	 public OSSBucket sampleBucket;

	    static {
	        OSSClient.setGlobalDefaultTokenGenerator(new TokenGenerator() { // 设置全局默认加签器
	            @Override
	            public String generateToken(String httpMethod, String md5, String type, String date,
	                    String ossHeaders, String resource) {

	                String content = httpMethod + "\n" + md5 + "\n" + type + "\n" + date + "\n" + ossHeaders
	                        + resource;

	                return OSSToolKit.generateToken(accessKey, screctKey, content);
	            }
	        });
	        OSSClient.setGlobalDefaultACL(AccessControlList.PUBLIC_READ); // 设置全局默认bucket访问权限

	        /*
	         * 如果需要采用服务端加签的方式，可以直接在tokenGenerator中向你的业务服务器发起
	         * 同步http post请求，把相关字段拼接之后发过去，然后获得加签结果。
	         *
	         *
	        OSSClient.setGlobalDefaultTokenGenerator(new TokenGenerator() {
	            @Override
	            public String generateToken(String httpMethod, String md5, String type, String date,
	                    String ossHeaders, String resource) {

	                String content = httpMethod + "\n" + md5 + "\n" + type + "\n" + date + "\n" + ossHeaders
	                        + resource;

	                List<NameValuePair> params = new ArrayList<NameValuePair>();
	                params.add(new BasicNameValuePair("content", content));
	                HttpPost post = new HttpPost("http://localhost/oss");
	                String sign = null;

	                try {
	                    post.setEntity(new UrlEncodedFormEntity(params));
	                    HttpResponse response = new DefaultHttpClient().execute(post);
	                    sign = EntityUtils.toString(response.getEntity()).trim();
	                } catch (Exception ignore) {
	                }
	                Log.d("OSS_Test", "[genToken] - remote: " + sign);
	                return sign;
	            }
	        });

	         ********************************************************
	         * 以下是加签服务器的代码示例： (nginx + lua脚本)
	         *
	         * local access_key = "ak";
	         * local screct_key = "sk";
	         * local sign_str;
	         *
	         * ngx.req.read_body();
	         * --local body = ngx.req.get_body_data();
	         *
	         * local args, err = ngx.req.get_post_args();
	         * for key, val in pairs(args) do
	         *         if key == "content" then
	         *                 sign_str = val;
	         *         end
	         * end
	         *
	         * local sign_result = ngx.encode_base64(ngx.hmac_sha1(screct_key, sign_str));
	         *
	         * ngx.say("OSS "..access_key..":"..sign_result);
	         ********************************************************/
	    }
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_publishimg);
		registerReceiver(this);
		initOSS();
		initView();
	}

	private void initOSS() {
		
		 OSSLog.enableLog(true);

	        OSSClient.setApplicationContext(getApplicationContext()); // 传入应用程序context

	        // 开始单个Bucket的设置
	        sampleBucket = new OSSBucket("beijixing");
	        sampleBucket.setBucketHostId("oss-cn-hangzhou.aliyuncs.com"); // 可以在这里设置数据中心域名或者cname域名
	        //sampleBucket.setBucketACL(AccessControlList.PUBLIC_READ_WRITE); // 如果这个Bucket跟全局默认的权限设置不一致
	        sampleBucket.setBucketACL(AccessControlList.PUBLIC_READ_WRITE);
	        // sampleBucket.setBucketHostId("oss-cn-hangzhou.aliyuncs.com"); // 如果这个Bucket跟全局默认的数据中心不一致，就需要单独设置
	        // sampleBucket.setBucketTokenGen(new TokenGenerator() {...}); // 如果这个Bucket跟全局默认的加签方法不一致，就需要单独设置

	}
	private void initView() {
		Bundle bundle = this.getIntent().getExtras();
	        //接收name值
		muploadtype = bundle.getString("type");
		// 获取关闭按钮id
		mClose = (ImageView) findViewById(R.id.close);
		mTitle = (TextView) findViewById(R.id.title);
		
		MyOnclickListener mOnclickListener = new MyOnclickListener();
		// 发表按钮
		mUpLoadEdit = (ImageView) findViewById(R.id.UpLoadEdit);
		// 相机按钮
		mCameraBt = (ImageButton) findViewById(R.id.id_from_cemera);
		// 图片按钮
		mAlbumBt = (ImageButton) findViewById(R.id.id_from_albumn);
		mdelete_img = (ImageView) findViewById(R.id.delete_img);
		
		mAddimglayout = (RelativeLayout) findViewById(R.id.id_add_img_layout);
		
		mInsertPicView = (ImageView) findViewById(R.id.addImage);

		mNeirongEdit = (EditText) findViewById(R.id.neirongEdit);
		if(muploadtype.equals("chuandao"))
		{
			mNeirongEdit.setHint(R.string.type_chuandao);
			mTitle.setText(R.string.upload_photo);
			mtid = "1";
		}

		else if(muploadtype.equals("shouye"))
		{
			mNeirongEdit.setHint(R.string.type_shouye);
			mTitle.setText(R.string.upload_shouye);
			mtid = "2";
		}
		else 
		{
			mNeirongEdit.setHint(R.string.type_yihuo);
			mTitle.setText(R.string.upload_help);
			mtid = "3";
		}
		 handlernoti = new Handler() { 
	    	 
		        @Override 
		        public void handleMessage(Message msg) { 
		            super.handleMessage(msg); 
		 
		            switch (msg.what) { 
		            case  GET_IMG_SUCCESS:
		            
		            	displayImageUri(path);
		            	break;
		           
		            	
		            }
		           
		        } 
		    }; 
		sdcardTempFile = new File(Environment.getExternalStorageDirectory(),
				"Android/data/com.qlhui.taizhankj/tmp_"
						+ SystemClock.currentThreadTimeMillis() + ".jpg");
		mClose.setOnClickListener(mOnclickListener);
		mUpLoadEdit.setOnClickListener(mOnclickListener);
		mCameraBt.setOnClickListener(mOnclickListener);
		mAlbumBt.setOnClickListener(mOnclickListener);
		mdelete_img.setOnClickListener(mOnclickListener);
		CameralActivity.setIMGcallback(new IMGCallBack() {

			@Override
			public void callback(String data,String imgpath) {
				UploadActivity.this.data = data;
				path = imgpath;
			  	Message message = new Message( );
		   	    message.what = GET_IMG_SUCCESS;
		   		handlernoti.sendMessage(message);
			
			}
		});
		PhotoAct.setIMGcallback(new IMGCallBack1() {


			@Override
			public void callback(String data, Bitmap bm,Intent imgdata) {
				UploadActivity.this.data = data;
				UploadActivity.this.mLoadedBitmap = bm;

				path = getImagePath(imgdata);
				Message message = new Message( );
		   	    message.what = GET_IMG_SUCCESS;
		   		handlernoti.sendMessage(message);
				
			}
		});
	}

	private class MyOnclickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			int ID = v.getId();
			switch (ID) {
			case R.id.close:
				UploadActivity.this.finish();
				break;
			case R.id.UpLoadEdit:
				if (Model.MYUSERINFO != null) {
					sendMeth();
				} else {
					Intent intent = new Intent(UploadActivity.this,
							LoginActivity.class);
					startActivity(intent);
				}
				break;
			case R.id.id_from_cemera:
			//	Intent intent = new Intent(UploadActivity.this,
			//			CameralActivity.class);
			//	startActivity(intent);
				startToCameraActivity();
				break;
			case R.id.delete_img:
				mAddimglayout.setVisibility(View.INVISIBLE);
				data = "";
				path="";
				mInsertPicView.setImageBitmap(null);
				break;	
				
			case R.id.id_from_albumn:
				Intent intent3 = new Intent(UploadActivity.this, PhotoAct.class);
				startActivity(intent3);
			//	Intent getAlbum = new Intent(Intent.ACTION_GET_CONTENT);
			//	getAlbum.setType("image/*");
			//	startActivityForResult(getAlbum, IMAGE_CODE);
				
				break;
			}
		}
	}
	private void startToCameraActivity() {
		if (FileUtiles.isSdCardAvailable()) {
			final Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			final Uri picUri = Uri.fromFile(sdcardTempFile);
			i.putExtra(MediaStore.EXTRA_OUTPUT, picUri);
			startActivityForResult(i, REQUEST_CODE_CAMERA);
		}
		else {
			Toast.makeText(this, R.string.pls_insert_sdcard, Toast.LENGTH_SHORT)
					.show();
		}
	}
	
	private String getImagePath(Intent data) {
		String path = null;
		if (data != null) {
			Uri originalUri = data.getData();
			Cursor cursor = null;
			try {
				cursor = managedQuery(originalUri,
						new String[] { MediaStore.Images.Media.DATA }, null,
						null, null);
				if (cursor != null && cursor.getCount() > 0) {
					int index = cursor
							.getColumnIndex(MediaStore.Images.Media.DATA);
					cursor.moveToFirst();
					path = cursor.getString(index);
				}
			} catch (Exception e) {
				Log.e("beijixing", "getImagePath:" + e.toString());

			} finally {
				if (Integer.parseInt(Build.VERSION.SDK) < 14) {
					if (cursor != null) {
						cursor.close();
					}
				}
			}
		}
		return path;
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == IMAGE_CODE) {
	
			path = getImagePath(data);
			if(path==null)
				Toast.makeText(UploadActivity.this, "图片太大,请重新选择",
								Toast.LENGTH_SHORT).show();
		/*	
			BitmapFactory.Options op = new BitmapFactory.Options();
			op.inSampleSize = 5;
			Bitmap bit = BitmapFactory.decodeFile(path,op);
			try {  
                FileOutputStream fileOutputStream = new FileOutputStream(path);  
                bit.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);  
                fileOutputStream.flush();
                fileOutputStream.close();  
                System.out.println("saveBmp is here");  
	            } catch (Exception e) {  
	                        e.printStackTrace();  
	        }  
			
			*/
			if (path != null) {
				displayImageUri(path);
			}
		}
		if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_CAMERA) {
	
			path = sdcardTempFile.getAbsolutePath();
			if(path==null)
				Toast.makeText(UploadActivity.this, "图片太大，请重新选择",
								Toast.LENGTH_SHORT).show();
			if (path != null) {
				displayImageUri(path);
			}
			
		}
	}	
	
	private void displayImageUri(String path) {
		mAddimglayout.setVisibility(View.VISIBLE);
		BitmapFactory.Options op = new BitmapFactory.Options();
		op.inSampleSize = 3;
		Bitmap bit = BitmapFactory.decodeFile(path,op);
		WindowManager wm = (WindowManager) this

                .getSystemService(Context.WINDOW_SERVICE);
		int WidowWidth  = wm.getDefaultDisplay().getWidth();	
		mInsertPicView.setMinimumHeight(WidowWidth*bit.getHeight()/bit.getWidth());
		mInsertPicView.setMinimumWidth(100);
	
		mInsertPicView.setImageBitmap(bit);
		
	}
	private void registerReceiver(Context context) {
	//	IntentFilter filter = new IntentFilter();
	//	filter.addAction(Constants.Intent.ACTION);
	//	context.registerReceiver(broadcastReceiver, filter);
	}
	/*
	public void uploadFile() {
		//path = 
		Upload uploadInstance = TaeSDK.getService(Upload.class);
		uploadInstance.upload(path);

	}
	*/
    public void doUploadFile(String imgname) throws Exception {
        OSSFile ossFile = new OSSFile(sampleBucket, "Valueimg/"+imgname);
        ossFile.setUploadFilePath(path, "image/jpg");
        ossFile.uploadInBackground(new SaveCallback() {

            @Override
            public void onProgress(String arg0, int arg1, int arg2) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onFailure(String arg0, OSSException arg1) {
                // TODO Auto-generated method stub
        //		Toast.makeText(UploadActivity.this, "上传失败",
		//				Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onSuccess(String arg0) {
                // TODO Auto-generated method stub
    	//		Toast.makeText(UploadActivity.this, "上传成功 已复制到剪切板",
			//			Toast.LENGTH_SHORT).show();
            }
        });
    }
    /*
	private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

		@SuppressLint("NewApi") @SuppressWarnings("deprecation")
		@Override
		public void onReceive(Context context, Intent intent) {

			int state = intent.getIntExtra(Constants.Intent.STATE, -1);
			UploadState uploadState = UploadState.valueOf(state);
			if (uploadState == null) {
				return;
			}
			switch (uploadState) {
			case SUCCESS: // 上传已经完成
				String result = intent
						.getStringExtra(Constants.Intent.RESULT_URI);
				ClipboardManager clipboard;
				 clipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
				 clipboard.setText(result);
			//	resultUrl.setText(result);
				Toast.makeText(UploadActivity.this, "上传成功 已复制到剪切板",
						Toast.LENGTH_SHORT).show();
				break;
			case FAIL: // 上传失败
				String failMessage = intent
						.getStringExtra(Constants.Intent.FAIL_MESSAGE);
				Toast.makeText(UploadActivity.this, "上传出错 " + failMessage,
						Toast.LENGTH_SHORT).show();
				break;

			}

		}
		
	};
	*/
	@Override
	protected void onDestroy() {
		super.onDestroy();
	//	unregisterReceiver(broadcastReceiver);
	}

	private void sendMeth() {
		if (mNeirongEdit.getText().toString().equals("")) {
			Toast.makeText(UploadActivity.this, "请先填写文字再提交", 1).show();
			return;
		}
		//uploadFile();
	
		String uid = Model.MYUSERINFO.getUserid();// 用户ID
		String tid = mtid;// 糗事类型ID
		String qlabel = "公告";// 名人标签
		if(mtid.equals("102"))
			qlabel= "经验";// 名人标签
		String qimg = "";
		String qvalue = mNeirongEdit.getText().toString();// 糗事内容
		/*格式化内容中的回车 换行*/
		qvalue = qvalue.replaceAll ("\r", "\\\\r").replaceAll ("\n", "\\\\n");
	//	qlabel = qlabel.replaceAll ("\r", "\\\\r").replaceAll ("\n", "\\\\n");
		if(!data.equals(""))
		  qimg = System.currentTimeMillis() + ".png";// 糗事图片
		if (!path.equalsIgnoreCase("")) {
			qimg = System.currentTimeMillis() + ".png";// 糗事图片
		
			try {
				doUploadFile(qimg);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		String Json = "{\"uid\":\"" + uid + "\"," + "\"tid\":\"" + tid + "\","
				+ "\"qimg\":\"" + qimg + "\"," + "\"qvalue\":\"" + qvalue + "\"," + "\"qlabel\":\"" + qlabel
				+ "\"," + "\"qlike\":\"0\"," + "\"qunlike\":\"0\"}";
		ThreadPoolUtils.execute(new HttpPostThread(hand, Model.ADDVALUE, Json));
		if(!data.equals(""))
		ThreadPoolUtils.execute(new HttpPostThread(hand, Model.ADDVLIMG, Json,
				data));
		UploadActivity.this.finish();
	}
	
	Handler hand = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			if (msg.what == 200) {
				String result = (String) msg.obj;
				if (result != null && result.equals("ok")) {
					Toast.makeText(UploadActivity.this, "发送成功", 1).show();
					UploadActivity.this.finish();
				}
			}
		};
	};
	 
}
