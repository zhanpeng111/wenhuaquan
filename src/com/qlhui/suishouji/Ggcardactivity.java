package com.qlhui.suishouji;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.qlhui.suishouji.db.MyDbHelper;
import com.qlhui.suishouji.db.MyDbInfo;
import com.qlhui.thoughtlight.MainTabActivity;
import com.qlhui.thoughtlight.R;
import com.qlhui.thoughtlight.utils.Bean;



public class Ggcardactivity extends Activity {
	private List<String> list = null;
	private MyDbHelper db = null;
    private ListView listView;
	private String[] ggcardstr = null;
    private ArrayList<Bean> beans = new ArrayList<Bean>();
    private String mggtype = "";
    private String mggsubtype = "";
    CommonData commondata = CommonData.getInstance();
	private Calendar calendar = Calendar.getInstance();
	private String  value="1";
	final static int GONG_MODE = 0;
	final static int GUO_MODE = 1;
	final static int EDIT_MODE = 2;
	private int type = GONG_MODE;//操作类型0收入、1支出、2编辑
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ggcardmain);
        Intent intent = getIntent();
        mggtype = intent.getStringExtra("ggtype");
        mggsubtype = intent.getStringExtra("ggsubtype");
        listView = (ListView) findViewById(R.id.listView);
        
    	db = MainTabActivity.db;
    	updateInfo(1);
    /*
    	for (int i = 0; i < 10; i++) {
            Bean bean = new Bean();
            bean.setTitle("I am the " + i + " CardView ！");
            bean.setImgId(i%2==0?R.drawable.ggtype_2:R.drawable.ggtype_1);
            beans.add(bean);
        }
        listView.setAdapter(new ListViewAdapter(beans));
        */
    }

	
	/**更新类别数据*/
	private void updateInfo(int position){
		Cursor cursor = null;
		String strWhere = "<>1"; 
		int account_num = 0;
		list = new ArrayList<String>();
		if(mggtype.equals("guo"))
			cursor = db.select(MyDbInfo.getTableNames()[1], MyDbInfo.getFieldNames()[1], 
				MyDbInfo.getFieldNames()[1][2] + "=?", new String[]{mggsubtype},
				null, null, null);
		else//guo
			cursor = db.select(MyDbInfo.getTableNames()[3], MyDbInfo.getFieldNames()[3], 
					MyDbInfo.getFieldNames()[3][2] + "=?", new String[]{mggsubtype},
					null, null, null);
		
		ggcardstr = new String[cursor.getCount()];
		while (cursor.moveToNext()) {
		    Bean bean = new Bean();
		    String newStr = null;
		    String oldStr = cursor.getString(1);
	
			if(oldStr.endsWith("==1"))
			{
				bean.setggvalue(1);
				if(mggtype.equals("guo"))
				{
					bean.setImgId(R.drawable.guo1);
					
				}
				else
				{

					bean.setImgId(R.drawable.g1);
					
				}
			}
			
			else if(oldStr.endsWith("==10"))
			{
				bean.setggvalue(10);
				if(mggtype.equals("guo"))
				{
					bean.setImgId(R.drawable.guo10);
					
				}
				else
				{

					bean.setImgId(R.drawable.g10);
					
				}
			}
			
			else if(oldStr.endsWith("==100"))
			{
				bean.setggvalue(100);
				if(mggtype.equals("guo"))
				{
					bean.setImgId(R.drawable.guo100);
					
				}
				else
				{		
					bean.setImgId(R.drawable.g100);
					
				}
			}
				
			if(oldStr.indexOf("=")!=-1)
				 newStr = oldStr.substring(0,oldStr.indexOf("="));
			bean.setTitle(newStr);
			
			beans.add(bean);
			
			 
			//list.add(cursor.getString(1));
		//	ggcardstr[account_num] = cursor.getString(0);
		//	account_num++;
		}
		 listView.setAdapter(new ListViewAdapter(beans));
		 
		 listView.setOnItemClickListener(new OnItemClickListener(){
			 
	            @Override
	            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
	                    long arg3) {
	                // TODO Auto-generated method stub
	            	
	            	saveInfo(beans.get(arg2));
	            	/*
	                if(beans.get(arg2).getggvalue()==1)
	                {
	                    Intent intent = new Intent("com.wps.android.LINEARLAYOUT");
	                    startActivity(intent);
	                }
	                if(beans.get(arg2).getggvalue()==10)
	                {
	                    Intent intent = new Intent("com.wps.android.ABSOLUTELAYOUT");
	                    startActivity(intent);
	                }
	                if(beans.get(arg2).getggvalue()==100)
	                {
	                    Intent intent = new Intent("com.wps.android.TABLELAYOUT");
	                    startActivity(intent);
	                }
	               */
	            }
	             
	        });
	//	adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, list);
	//	adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	}

	private void saveInfo(Bean bean){
		if(value.equals("") || value == null || Double.parseDouble(value) <= 0){
			Toast.makeText(getApplicationContext(), getString(R.string.input_message),
			Toast.LENGTH_SHORT).show();
			return;
		}
		
		int tableId = 0;
		String fieldNames[] = null;
		String values[] = null;
	
		switch (mggtype) {
		case "guo":
			tableId = 9;
			fieldNames = new String[]{"AMOUNT", "EXPENDITURE_CATEGORY_ID", "EXPENDITURE_SUB_CATEGORY_ID", "ACCOUNT_ID", "STORE_ID", "ITEM_ID", "DATE", "MEMO"};
			values = new String[]{
					String.valueOf(bean.getggvalue()),
					"ooo",
					"ppp",
					"guo",
					"eee",
					format(calendar.getTime()),
					bean.getTitle()
			};
			updataAccount(mggtype,String.valueOf(bean.getggvalue()));
			break;
		case "gong":
			tableId = 10;
			fieldNames = new String[]{"AMOUNT", "INCOME_CATEGORY_ID", "INCOME_SUB_CATEGORY_ID", "ACCOUNT_ID", "ITEM_ID", "DATE", "MEMO"};
			values = new String[]{
					String.valueOf(bean.getggvalue()),
					"ooo",
					"ppp",
					"gong",
					"eee",
					format(calendar.getTime()),
					bean.getTitle()
			};
			updataAccount(mggtype,String.valueOf(bean.getggvalue()));
			break;
	
		}
		db.insert(MyDbInfo.getTableNames()[tableId], fieldNames,values);
		Toast.makeText(getApplicationContext(), getString(R.string.save_message),Toast.LENGTH_SHORT).show();
/*
		if(type == EDIT_MODE){
			db.update(MyDbInfo.getTableNames()[tableId], fieldNames, values, "ID=" + data.infoId, null);
			Toast.makeText(getApplicationContext(), getString(R.string.edit_message),Toast.LENGTH_SHORT).show();
		}else{
			db.insert(MyDbInfo.getTableNames()[tableId], fieldNames,values);
			Toast.makeText(getApplicationContext(), getString(R.string.save_message),Toast.LENGTH_SHORT).show();
		}
*/
//		exit();
	}
	private String format(Date date){
		String str = "";
		SimpleDateFormat ymd = null;
		ymd = new SimpleDateFormat("yyyy-MM-dd");
		str = ymd.format(date); 
		return str;
	}
	private void updataAccount(String type,String value){
		Iterator<AccountData> iteratorSort = commondata.account.values().iterator();
		while (iteratorSort.hasNext()){
			AccountData data = iteratorSort.next();
		//	if(data.id == Integer.parseInt(accountId[account_spn.getSelectedItemPosition()]))
			{
				if(type == "gong"){
					data.balance = data.balance+Double.parseDouble(value);//value
					commondata.updateAccount(data);
				}else if(type == "guo"){
					data.balance = data.balance-Double.parseDouble(value);
					commondata.updateAccount(data);
				}
				return;
			}
		}
		
	}
    /*ListView适配器**/
    public class ListViewAdapter extends BaseAdapter {
        ArrayList<Bean> beans = new ArrayList<Bean>();

        public ListViewAdapter(ArrayList<Bean> beans) {
            this.beans = beans;
        }

        public int getCount() {
            return beans.size();
        }

        public Object getItem(int position) {
            return beans.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder=null;
            LayoutInflater inflater = (LayoutInflater)Ggcardactivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            /*View复用*/
            if (convertView == null){
                convertView=inflater.inflate(R.layout.ggcarditem_list, null);
                holder=new ViewHolder();
                // 通过findViewById()方法实例R.layout.item_list内各组件
                holder.text = (TextView) convertView.findViewById(R.id.title);
                holder.image = (ImageView) convertView.findViewById(R.id.img);
                convertView.setTag(holder);
            }
            holder=(ViewHolder)convertView.getTag();
            // 给holder中的控件进行赋值
            Bean bean=beans.get(position);
            holder.text.setText(bean.getTitle());
            holder.image.setImageResource(bean.getImgId());
            return convertView;
        }
    }

    class ViewHolder{
        ImageView image;
        TextView text;
        int ggvalue;
    }
	}