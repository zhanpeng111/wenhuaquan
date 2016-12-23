package com.qlhui.thoughtlight;



import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
//import com.taobao.tae.sdk.TaeSDK;
//import com.taobao.tae.sdk.callback.InitResultCallback;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class MyApplication extends Application {

	//private YWEnvType curEnviroment;
	@Override
	public void onCreate() {
		super.onCreate();
	//	SDKInitializer.initialize(this);
//		curEnviroment= OpenimInfoInitActivity
//				.getYWEnvTypeFromLocal(getSharedPreferences(
//						OpenimInfoInitActivity.LOCAL_STORE_NAME,
//						Context.MODE_PRIVATE).getString(
//						OpenimInfoInitActivity.ENVIROMENT, "test"));
//
//
//		/**
//		 * 可能的取值：
//		 * TaeSdkEnvironment.SANDBOX //沙箱
//		 * TaeSdkEnvironment.PRE//预发
//		 *  TaeSdkEnvironment.ONLINE//线上
//		 *  TaeSdkEnvironment.TEST//测试
//		 */
//		if(curEnviroment.getValue()==YWEnvType.ONLINE.getValue()){ 
//			TaeSDK.setEnvIndex( TaeSdkEnvironment.ONLINE.ordinal());
//		}else if(curEnviroment.getValue()==YWEnvType.TEST.getValue()){
//			TaeSDK.setEnvIndex(TaeSdkEnvironment.TEST.ordinal());
//		}else if(curEnviroment.getValue()==YWEnvType.PRE.getValue()){
//			TaeSDK.setEnvIndex(TaeSdkEnvironment.PRE.ordinal());
//		}

		initImageLoader(getApplicationContext());
		/*
		TaeSDK.asyncInit(this, new InitResultCallback() {

			@Override
			public void onSuccess() {
			//	Toast.makeText(MyApplication.this, "TaeSDK 初始化成功", Toast.LENGTH_SHORT)
			//	.show();
			}

			@Override
			public void onFailure(int code, String message) {
				Toast.makeText(MyApplication.this, "TaeSDK 初始化异常，code = " + code + ", info = " + message, Toast.LENGTH_SHORT)
						.show();
				Log.w("mayongge", "初始化异常，code = " + code + ", info = " + message);
			}

		});
		*/
	//	UTWrapper.setUTEnable(true);
//		YWEnvManager.prepare(this,curEnviroment);
	/*
		YWChannel.prepare(
				this,
				getSharedPreferences(OpenimInfoInitActivity.LOCAL_STORE_NAME,
						Context.MODE_PRIVATE).getString(
						OpenimInfoInitActivity.APPKEY, "23015524"));
						
		XPushManager.getInstance().init(this, new PushListener() {
			
			@Override
			public void onServiceStatus(boolean arg0) {
				// TODO Auto-generated method stub	
			}
			
			@Override
			public void onCustomPushData(Context arg0, String arg1) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void onClientIdUpdate(String arg0) {
				PushLog.e("MyApplication", "clientId:"+arg0);
			}
		});
*/
	}
	public static void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you may tune some of them,
		// or you can create default configuration by
		//  ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.writeDebugLogs() // Remove for release app
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}
}
