package com.qlhui.thoughtlight;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.qlhui.thoughtlight.info.BJXUserInfo;
import com.qlhui.thoughtlight.model.Model;
import com.qlhui.thoughtlight.net.ThreadPoolUtils;
import com.qlhui.thoughtlight.thread.HttpPostThread;
import com.qlhui.thoughtlight.utils.AppConstants;
import com.qlhui.thoughtlight.utils.MyJson;
import com.qlhui.thoughtlight.utils.Util;
import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {
	private static final String TAG = LoginActivity.class.getName();
    public static String mAppid = "1104560845";
	private EditText mEtAppid = null;
	public static Tencent mTencent;
    private static Intent mPrizeIntent = null;
	private ImageView mClose;
	private Button mLogin;
    private UserInfo mInfo;
	private RelativeLayout mWeibo, mQQ,mWeixin;
	private EditText mName, mPassword;
	private TextView mRegister;
	private String NameValue = null;
	private String mOpenId = null;
	private String PasswordValue = null;
	private String UserImgUrl = null;
	private String url = null;
	private String value = null;
	private SharedPreferences sp;  
	private String userNameValue,passwordValue;  
	private EditText userName, password; 
	private MyJson myJson = new MyJson();
    private static boolean isServerSideLogin = false;
	private BJXUserInfo myinfo = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.actionbar_activity_login);
		sp = this.getSharedPreferences("userInfo", Context.MODE_WORLD_READABLE);  
		initView();
		if (TextUtils.isEmpty(mAppid)) {
			mAppid = "1104560845";
    		mEtAppid = new EditText(this);
    		mEtAppid.setText(mAppid);
			try {
				new AlertDialog.Builder(this).setTitle("请输入APP_ID")
						.setCancelable(false)
						.setIcon(android.R.drawable.ic_dialog_info)
						.setView(mEtAppid)
						.setPositiveButton("Commit", mAppidCommitListener)
						.setNegativeButton("Use Default", mAppidCommitListener)
						.show();
			} catch (Exception e) {
			}
		} else {
		    if (mTencent == null) {
		        mTencent = Tencent.createInstance(mAppid, this);
		    }
		}
	}
	private DialogInterface.OnClickListener mAppidCommitListener = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			mAppid = AppConstants.APP_ID;
			switch (which) {
			case DialogInterface.BUTTON_POSITIVE:
				// 用输入的appid
				String editTextContent = mEtAppid.getText().toString().trim();
				if (!TextUtils.isEmpty(editTextContent)) {
				    mAppid = editTextContent;
				}
				break;
			case DialogInterface.BUTTON_NEGATIVE:
				// 默认appid
				break;
			}
			mTencent = Tencent.createInstance(mAppid, LoginActivity.this);
            // 有奖分享处理
        //    handlePrizeShare();
		}
	};
	private void initView() {
	//	mClose = (ImageView) findViewById(R.id.loginClose);
		mLogin = (Button) findViewById(R.id.id_btn_login);
		mWeibo = (RelativeLayout) findViewById(R.id.sinaLayout);
		mQQ = (RelativeLayout) findViewById(R.id.qqLayout);
		mWeixin = (RelativeLayout) findViewById(R.id.wxLayout);
		mName = (EditText) findViewById(R.id.userName);
		mPassword = (EditText) findViewById(R.id.passwd);
	//	mRegister = (TextView) findViewById(R.id.register);
		MyOnClickListener my = new MyOnClickListener();
	//	mClose.setOnClickListener(my);
		mLogin.setOnClickListener(my);
		mWeibo.setOnClickListener(my);
		mQQ.setOnClickListener(my);
	//	mRegister.setOnClickListener(my);

	}

	class MyOnClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			int mId = v.getId();
			switch (mId) {
			case R.id.loginClose:
				finish();
				break;
			case R.id.id_btn_login:
				NameValue = mName.getText().toString();
				PasswordValue = mPassword.getText().toString();
				Log.e("qianpengyu", "NameValue" + NameValue + "  PasswordValue"
						+ PasswordValue);
				if (NameValue.equalsIgnoreCase(null)
						|| PasswordValue.equalsIgnoreCase(null)
						|| NameValue.equals("") || PasswordValue.equals("")) {
					Toast.makeText(LoginActivity.this, "别闹,先把帐号密码填上", 1).show();
				} else {
					login();
				}
				break;
			case R.id.button_weibo:
				break;
			case R.id.qqLayout:
				onClickLogin();
				break;
			case R.id.register:
				Intent intent = new Intent(LoginActivity.this,
						RegistetActivity.class);
				startActivityForResult(intent, 1);

			}

		}

	}
	/*
	 *  QQ登陆
	 * 
	 */
	private void onClickLogin() {
		if (!mTencent.isSessionValid()) {
			mTencent.login(this, "all", loginListener);
            isServerSideLogin = false;
			Log.d("SDKQQAgentPref", "FirstLaunch_SDK:" + SystemClock.elapsedRealtime());
		} else {
            if (isServerSideLogin) { // Server-Side 模式的登陆, 先退出，再进行SSO登陆
                mTencent.logout(this);
                mTencent.login(this, "all", loginListener);
                isServerSideLogin = false;
                Log.d("SDKQQAgentPref", "FirstLaunch_SDK:" + SystemClock.elapsedRealtime());
                return;
            }
		    mTencent.logout(this);
		//	updateUserInfo();
		//	updateLoginButton();
		}
	}
	IUiListener loginListener = new BaseUiListener() {
        @Override
        protected void doComplete(JSONObject values) {
        	Log.d("SDKQQAgentPref", "AuthorSwitch_SDK:" + SystemClock.elapsedRealtime());
        	
        	try {
				//获得的数据是JSON格式的，获得你想获得的内容
				//如果你不知道你能获得什么，看一下下面的LOG
				Log.e(TAG, "-------------"+values.toString());
				mOpenId = ((JSONObject) values).getString("openid");
		//		NameValue =  ((JSONObject) values).getString("nickname");
		//		UserImgUrl = ((JSONObject) values).getString("figureurl_qq_2");
				PasswordValue = "123456";
			
		//		reginstet();
			//	openidTextView.setText(openidString);
				Log.e(TAG, "-------------"+mOpenId);
				//access_token= ((JSONObject) response).getString("access_token");				//expires_in = ((JSONObject) response).getString("expires_in");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
            initOpenidAndToken(values);
            updateUserInfo();
         //   updateLoginButton();
        }
    };
    public static void initOpenidAndToken(JSONObject jsonObject) {
        try {
            String token = jsonObject.getString(Constants.PARAM_ACCESS_TOKEN);
            String expires = jsonObject.getString(Constants.PARAM_EXPIRES_IN);
            String openId = jsonObject.getString(Constants.PARAM_OPEN_ID);
            if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires)
                    && !TextUtils.isEmpty(openId)) {
                mTencent.setAccessToken(token, expires);
                mTencent.setOpenId(openId);
            }
        } catch(Exception e) {
        }
    }
    

	private void updateUserInfo() {
		if (mTencent != null && mTencent.isSessionValid()) {
			IUiListener listener = new IUiListener() {

				@Override
				public void onError(UiError e) {

				}

				@Override
				public void onComplete(final Object response) {
					Message msg = new Message();
					msg.obj = response;
					msg.what = 0;
					mHandler.sendMessage(msg);
					new Thread(){

						@Override
						public void run() {
							JSONObject json = (JSONObject)response;
							if(json.has("figureurl")){
								Bitmap bitmap = null;
								try {
									
									bitmap = Util.getbitmap(json.getString("figureurl_qq_2"));
								} catch (JSONException e) {

								}
								Message msg = new Message();
								msg.obj = bitmap;
								msg.what = 1;
								mHandler.sendMessage(msg);
							}
						}

					}.start();
				}

				@Override
				public void onCancel() {

				}
			};
			mInfo = new UserInfo(this, mTencent.getQQToken());
			mInfo.getUserInfo(listener);

		} else {
		//	mUserInfo.setText("");
		//	mUserInfo.setVisibility(android.view.View.GONE);
		//	mUserLogo.setVisibility(android.view.View.GONE);
		}
	}
	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				JSONObject response = (JSONObject) msg.obj;
				if (response.has("nickname")) {
					try {
					//	mUserInfo.setVisibility(android.view.View.VISIBLE);
						NameValue = response.getString("nickname");
//						UserImgUrl = ((JSONObject) values).getString("figureurl_qq_2");
						PasswordValue = "123456";
					
						loginwithopenid();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}else if(msg.what == 1){
				Bitmap bitmap = (Bitmap)msg.obj;
		//		mUserLogo.setImageBitmap(bitmap);
		//		mUserLogo.setVisibility(android.view.View.VISIBLE);
			}
		}

	};
    private class BaseUiListener implements IUiListener {

		@Override
		public void onComplete(Object response) {
            if (null == response) {
                Util.showResultDialog(LoginActivity.this, "返回为空", "登录失败");
                return;
            }
            JSONObject jsonResponse = (JSONObject) response;
            if (null != jsonResponse && jsonResponse.length() == 0) {
                Util.showResultDialog(LoginActivity.this, "返回为空", "登录失败");
                return;
            }
		//	Util.showResultDialog(LoginActivity.this, response.toString(), "登录成功");
            // 有奖分享处理
      //      handlePrizeShare();
			doComplete((JSONObject)response);
		}

		protected void doComplete(JSONObject values) {
		
		}

		@Override
		public void onError(UiError e) {
			Util.toastMessage(LoginActivity.this, "onError: " + e.errorDetail);
			Util.dismissDialog();
		}

		@Override
		public void onCancel() {
			Util.toastMessage(LoginActivity.this, "onCancel: ");
			Util.dismissDialog();
            if (isServerSideLogin) {
                isServerSideLogin = false;
            }
		}
	}
    /*
	private void reginstet() {
		url = Model.REGISTETOPEN;
		value = "{\"uname\":\"" + NameValue +"\",\"uopenid\":\"" + mOpenId + "\",\"upassword\":\"" + PasswordValue
				+ "\"}";
		Log.e("qianpengyu", value);
		ThreadPoolUtils.execute(new HttpPostThread(regishand, url, value));
	}
	*/
	private void loginwithopenid() {

		url = Model.LOGINOPEN;
	//	value = "{\"uopenid\":\"" + mOpenId + "\",\"upassword\":\""
	//			+ PasswordValue + "\"}";
		value = "{\"uname\":\"" + NameValue +"\",\"uopenid\":\"" + mOpenId + "\",\"upassword\":\"" + PasswordValue
				+ "\"}";
		Log.e("qianpengyu", value);
		ThreadPoolUtils.execute(new HttpPostThread(hand, url, value));
	}
	private void login() {

		url = Model.LOGIN;
		value = "{\"uname\":\"" + NameValue + "\",\"upassword\":\""
				+ PasswordValue + "\"}";
		Log.e("qianpengyu", value);
		ThreadPoolUtils.execute(new HttpPostThread(hand, url, value));
	}

	Handler hand = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			if (msg.what == 404) {
				Toast.makeText(LoginActivity.this, "请求失败，服务器故障", 1).show();
			} else if (msg.what == 100) {
				Toast.makeText(LoginActivity.this, "服务器无响应", 1).show();
			} else if (msg.what == 200) {
				String result = (String) msg.obj;
				Log.e("qiangpengyu", result);
				if (result.equalsIgnoreCase("NOUSER")&&url != Model.LOGINOPEN) {
					mName.setText("");
					mPassword.setText("");
					Toast.makeText(LoginActivity.this, "用户名不存在", 1).show();
					return;
				} else if (result.equalsIgnoreCase("NOPASS")) {
					mPassword.setText("");
					Toast.makeText(LoginActivity.this, "密码错误", 1).show();
					return;
				} else if (result != null) {
					Toast.makeText(LoginActivity.this, "登录成功", 1).show();
					sp = LoginActivity.this.getSharedPreferences("userInfo", Context.MODE_WORLD_READABLE); 
					  //记住用户名、密码、  
			        Editor editor = sp.edit();  
			        editor.putString("USER_NAME", NameValue);  
			        editor.putString("PASSWORD",PasswordValue);  
			        editor.putString("USER_HEAD",UserImgUrl);  
			        if(url != Model.LOGINOPEN)
			        	 editor.putBoolean("ISOPENID", false);//("USER_HEAD",UserImgUrl);  
			        else
			        	 editor.putBoolean("ISOPENID", true);
			        editor.commit(); 
					List<BJXUserInfo> newList = myJson.getNearUserList(result);
					if (newList != null) {
						Model.MYUSERINFO = newList.get(0);
					}
				//	Model.MYUSERINFO.set
					
					Intent intent = new Intent(LoginActivity.this,
							MainTabActivity.class);
			//		Bundle bund = new Bundle();
				//	bund.putSerializable("UserInfo", Model.MYUSERINFO);
				//	intent.putExtra("value", bund);
					startActivity(intent);
					
					finish();
				}
			}
		};
	};
	/*
	Handler regishand = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			if (msg.what == 404) {
				Toast.makeText(LoginActivity.this, "请求失败，服务器故障", 1).show();
			} else if (msg.what == 100) {
				Toast.makeText(LoginActivity.this, "服务器无响应", 1).show();
			} else if (msg.what == 200) {
				String result = (String) msg.obj;
				Log.e("qiangpengyu", "result:" + result);
				if (result.equals("ok")) {
					login();
				} else if (result.trim().equals("no")) {
					mName.setText("");
					mPassword.setText("");
					Toast.makeText(LoginActivity.this, "用户名已存在,请重新注册", 1)
							.show();
					return;
				} else {
					mName.setText("");
					mPassword.setText("");
					Toast.makeText(LoginActivity.this, "注册失败", 1).show();
					return;
				}

			}
		};
	};
	*/
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 1 && resultCode == 2 && data != null) {
			NameValue = data.getStringExtra("NameValue");
			mName.setText(NameValue);
		}
	}
}