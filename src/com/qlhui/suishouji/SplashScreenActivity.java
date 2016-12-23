/**
 * 
 */
package com.qlhui.suishouji;

import com.qlhui.suishouji.db.MyDbHelper;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.qlhui.thoughtlight.GuideViewActivity;
import com.qlhui.thoughtlight.LoginActivity;
import com.qlhui.thoughtlight.MainTabActivity;
import com.qlhui.thoughtlight.R;
import com.qlhui.thoughtlight.SplashActivity;
import com.qlhui.thoughtlight.myview.MyImageView;
/**
 * @author ZHONGHUA
 * 
 */
public class SplashScreenActivity extends Activity {
	public static MyDbHelper db = null;
	CommonData commondata = CommonData.getInstance();
	MyImageView gg_person;
	MyImageView gg_family;
	MyImageView gg_work;
	private String mggtype = "";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_screen_activity);
	//	initialDBData();
	//	CommonData.getInstance().load(this);
	//	mHandler.sendEmptyMessageDelayed(SWITCH_MAINACTIVITY,1500);
		 Intent intent = getIntent();
		 mggtype = intent.getStringExtra("gg_type");
		gg_person=(MyImageView) findViewById(R.id.type_person);
		gg_person.setOnClickListener(new OnClickListener() {
       	
			@Override
				public void onClick(View v) {
				    Intent mIntent = new Intent();//MainPage
				    mIntent.putExtra("ggtype", mggtype);
				    mIntent.putExtra("ggsubtype", "1");
	                mIntent.setClass(SplashScreenActivity.this, Ggcardactivity.class);
	               
	                SplashScreenActivity.this.startActivity(mIntent);
	             //   SplashScreenActivity.this.finish();
					
				}
				});
		gg_family=(MyImageView) findViewById(R.id.type_family);
		gg_family.setOnClickListener(new OnClickListener() {
       	
			@Override
				public void onClick(View v) {
				    Intent mIntent = new Intent();//MainPage
				    mIntent.putExtra("ggtype", mggtype);
				    mIntent.putExtra("ggsubtype", "2");
	                mIntent.setClass(SplashScreenActivity.this, Ggcardactivity.class);
	               
	                SplashScreenActivity.this.startActivity(mIntent);
	               
					
				}
				});
		gg_work=(MyImageView) findViewById(R.id.type_work);
		gg_work.setOnClickListener(new OnClickListener() {
       	
			@Override
				public void onClick(View v) {
				    Intent mIntent = new Intent();//MainPage
				    mIntent.putExtra("ggtype", mggtype);
				    mIntent.putExtra("ggsubtype", "3");
	                mIntent.setClass(SplashScreenActivity.this, Ggcardactivity.class);
	               
	                SplashScreenActivity.this.startActivity(mIntent);
	          
					
				}
				});
	
	
	}
	/*
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		//activity jump
	//	Intent i=new Intent(SplashScreenActivity.this,MainActivity.class);
	//	startActivity(i);
	//	this.finish();
		return super.onTouchEvent(event);
	}
	*/
	  //*************************************************
    // Handler:跳转至不同页面
    //*************************************************
    private final static int SWITCH_MAINACTIVITY = 1000;
    private final static int SWITCH_GUIDACTIVITY = 1001;
    public Handler mHandler = new Handler(){
        public void handleMessage(Message msg) {
            switch(msg.what){
            case SWITCH_MAINACTIVITY:
                Intent mIntent = new Intent();//MainPage
          
            //    mIntent.setClass(SplashScreenActivity.this, MainActivity.class);
               
            //    SplashScreenActivity.this.startActivity(mIntent);
             //   SplashScreenActivity.this.finish();
                break;
            case SWITCH_GUIDACTIVITY:
              
                break;
            }
            super.handleMessage(msg);
        }
    };
    // 初始化人生预算
	public void initBudget() {
	
		BudgetData bData = commondata.budgetcategory.get(1);
		bData.balance = Double.valueOf("700800");//80年
		commondata.updateBudget(bData);
//		Toast.makeText(this, getString(R.string.budget_ok), 0).show();
			
		
		
	}
	
	private void initialDBData() {
		// 建立数据库
//		deleteDatabase("mydb");
		db = MyDbHelper.getInstance(this.getApplicationContext());
		Resources res = this.getResources();
		db.open();
		Cursor cursor = db.select("TBL_EXPENDITURE_CATEGORY", new String[] {
				"ID", "NAME", "BUDGET" }, null, null, null, null, null);
	
		if (cursor.moveToNext()) {
			cursor.close();
//			db.close();
			return;
		}
		initBudget();
		// 插入支出类别
		String[] arr = res.getStringArray(R.array.TBL_EXPENDITURE_CATEGORY);
		for (int i = 0; i < arr.length; i++) {
			Log.i("TEST", arr[i]);
			db.insert("TBL_EXPENDITURE_CATEGORY", new String[] { "NAME",
					"BUDGET" }, new String[] { arr[i], "0" });

		}
		// 插入支出子类别
		arr = res.getStringArray(R.array.TBL_EXPENDITURE_SUB_CATEGORY_1);
		for (int i = 0; i < arr.length; i++) {
			Log.i("TEST", arr[i]);
			db.insert("TBL_EXPENDITURE_SUB_CATEGORY", new String[] { "NAME",
					"PARENT_CATEGORY_ID" }, new String[] { arr[i], "1" });

		}
		arr = res.getStringArray(R.array.TBL_EXPENDITURE_SUB_CATEGORY_2);
		for (int i = 0; i < arr.length; i++) {
			Log.i("TEST", arr[i]);
			db.insert("TBL_EXPENDITURE_SUB_CATEGORY", new String[] { "NAME",
					"PARENT_CATEGORY_ID" }, new String[] { arr[i], "2" });

		}
		arr = res.getStringArray(R.array.TBL_EXPENDITURE_SUB_CATEGORY_3);
		for (int i = 0; i < arr.length; i++) {
			Log.i("TEST", arr[i]);
			db.insert("TBL_EXPENDITURE_SUB_CATEGORY", new String[] { "NAME",
					"PARENT_CATEGORY_ID" }, new String[] { arr[i], "3" });

		}
		arr = res.getStringArray(R.array.TBL_EXPENDITURE_SUB_CATEGORY_4);
		for (int i = 0; i < arr.length; i++) {
			Log.i("TEST", arr[i]);
			db.insert("TBL_EXPENDITURE_SUB_CATEGORY", new String[] { "NAME",
					"PARENT_CATEGORY_ID" }, new String[] { arr[i], "4" });

		}
		arr = res.getStringArray(R.array.TBL_EXPENDITURE_SUB_CATEGORY_5);
		for (int i = 0; i < arr.length; i++) {
			Log.i("TEST", arr[i]);
			db.insert("TBL_EXPENDITURE_SUB_CATEGORY", new String[] { "NAME",
					"PARENT_CATEGORY_ID" }, new String[] { arr[i], "5" });

		}
		arr = res.getStringArray(R.array.TBL_EXPENDITURE_SUB_CATEGORY_6);
		for (int i = 0; i < arr.length; i++) {
			Log.i("TEST", arr[i]);
			db.insert("TBL_EXPENDITURE_SUB_CATEGORY", new String[] { "NAME",
					"PARENT_CATEGORY_ID" }, new String[] { arr[i], "6" });

		}
		arr = res.getStringArray(R.array.TBL_EXPENDITURE_SUB_CATEGORY_7);
		for (int i = 0; i < arr.length; i++) {
			Log.i("TEST", arr[i]);
			db.insert("TBL_EXPENDITURE_SUB_CATEGORY", new String[] { "NAME",
					"PARENT_CATEGORY_ID" }, new String[] { arr[i], "7" });
		}
		arr = res.getStringArray(R.array.TBL_EXPENDITURE_SUB_CATEGORY_8);
		for (int i = 0; i < arr.length; i++) {
			Log.i("TEST", arr[i]);
			db.insert("TBL_EXPENDITURE_SUB_CATEGORY", new String[] { "NAME",
					"PARENT_CATEGORY_ID" }, new String[] { arr[i], "8" });
		}
		arr = res.getStringArray(R.array.TBL_EXPENDITURE_SUB_CATEGORY_9);
		for (int i = 0; i < arr.length; i++) {
			Log.i("TEST", arr[i]);
			db.insert("TBL_EXPENDITURE_SUB_CATEGORY", new String[] { "NAME",
					"PARENT_CATEGORY_ID" }, new String[] { arr[i], "9" });

		}

		arr = res.getStringArray(R.array.TBL_EXPENDITURE_SUB_CATEGORY_10);
		for (int i = 0; i < arr.length; i++) {
			Log.i("TEST", arr[i]);
			db.insert("TBL_EXPENDITURE_SUB_CATEGORY", new String[] { "NAME",
					"PARENT_CATEGORY_ID" }, new String[] { arr[i], "10" });

		}
		arr = res.getStringArray(R.array.TBL_EXPENDITURE_SUB_CATEGORY_11);
		for (int i = 0; i < arr.length; i++) {
			Log.i(" TEST", arr[i]);
			db.insert("TBL_EXPENDITURE_SUB_CATEGORY", new String[] { "NAME",
					"PARENT_CATEGORY_ID" }, new String[] { arr[i], "11" });

		}
		// 插入收入类别
		arr = res.getStringArray(R.array.TBL_INCOME_CATEGORY);
		for (int i = 0; i < arr.length; i++) {
			Log.i("TEST", arr[i]);
			db.insert("TBL_INCOME_CATEGORY", new String[] { "NAME" },
					new String[] { arr[i] });

		}
		// 插入子收入类别
		arr = res.getStringArray(R.array.TBL_INCOME_SUB_CATEGORY_1);
		for (int i = 0; i < arr.length; i++) {
			Log.i("TEST", arr[i]);
			db.insert("TBL_INCOME_SUB_CATEGORY", new String[] { "NAME",
					"PARENT_CATEGORY_ID" }, new String[] { arr[i], "1" });
		}
		arr = res.getStringArray(R.array.TBL_INCOME_SUB_CATEGORY_2);
		for (int i = 0; i < arr.length; i++) {
			Log.i("TEST", arr[i]);
			db.insert("TBL_INCOME_SUB_CATEGORY", new String[] { "NAME",
					"PARENT_CATEGORY_ID" }, new String[] { arr[i], "2" });

		}

		// 插入账户类别
		arr = res.getStringArray(R.array.TBL_ACCOUNT_TYPE);
		for (int i = 0; i < arr.length; i++) {
			Log.i("TEST", arr[i]);
			db.insert("TBL_ACCOUNT_TYPE", new String[] { "NAME", "POSTIVE" },
					new String[] { arr[i].substring(0, arr[i].indexOf(",")),
							arr[i].substring(arr[i].indexOf(",") + 1) });

		}
		// 插入账户子类别
		arr = res.getStringArray(R.array.TBL_ACCOUNT_SUB_TYPE_1);
		for (int i = 0; i < arr.length; i++) {
			Log.i("TEST", arr[i]);
			db.insert("TBL_ACCOUNT_SUB_TYPE", new String[] { "NAME",
					"PARENT_TYPE_ID" }, new String[] { arr[i], "1" });

		}
		arr = res.getStringArray(R.array.TBL_ACCOUNT_SUB_TYPE_2);
		for (int i = 0; i < arr.length; i++) {
			Log.i("TEST", arr[i]);
			db.insert("TBL_ACCOUNT_SUB_TYPE", new String[] { "NAME",
					"PARENT_TYPE_ID" }, new String[] { arr[i], "2" });

		}
		arr = res.getStringArray(R.array.TBL_ACCOUNT_SUB_TYPE_3);
		for (int i = 0; i < arr.length; i++) {
			Log.i("TEST", arr[i]);
			db.insert("TBL_ACCOUNT_SUB_TYPE", new String[] { "NAME",
					"PARENT_TYPE_ID" }, new String[] { arr[i], "3" });

		}
		arr = res.getStringArray(R.array.TBL_ACCOUNT_SUB_TYPE_4);
		for (int i = 0; i < arr.length; i++) {
			Log.i("TEST", arr[i]);
			db.insert("TBL_ACCOUNT_SUB_TYPE", new String[] { "NAME",
					"PARENT_TYPE_ID" }, new String[] { arr[i], "4" });

		}
		arr = res.getStringArray(R.array.TBL_ACCOUNT_SUB_TYPE_5);
		for (int i = 0; i < arr.length; i++) {
			Log.i("TEST", arr[i]);
			db.insert("TBL_ACCOUNT_SUB_TYPE", new String[] { "NAME",
					"PARENT_TYPE_ID" }, new String[] { arr[i], "5" });

		}
		// 插入账户
		arr = res.getStringArray(R.array.TBL_ACCOUNT);
		for (int i = 0; i < arr.length; i++) {
			Log.i("TEST", arr[i]);
			db.insert("TBL_ACCOUNT", new String[] { "NAME", "TYPE_ID",
					"SUB_TYPE_ID", "ACCOUNT_BALANCE" }, arr[i].split(","));
		}
		// 插入商家
		arr = res.getStringArray(R.array.TBL_STORE);
		for (int i = 0; i < arr.length; i++) {
			Log.i("TEST", arr[i]);
			db.insert("TBL_STORE", new String[] { "NAME" },
					new String[] { arr[i] });
		}
		// 插入项目
		arr = res.getStringArray(R.array.TBL_ITEM);
		for (int i = 0; i < arr.length; i++) {
			Log.i("TEST", arr[i]);
			db.insert("TBL_ITEM", new String[] { "NAME" },
					new String[] { arr[i] });
		}
		/*
		 * cursor =db.select("TBL_EXPENDITURE_CATEGORY", new
		 * String[]{"ID","NAME","BUDGET"}, null, null, null, null, null);
		 * while(cursor.moveToNext()) {
		 * Log.i("TBL_EXPENDITURE_CATEGORY",cursor.getString
		 * (0)+","+cursor.getString(1)); }
		 */
//		db.close();

	}
}
