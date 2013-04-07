package com.coldmn3.nga.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.coldmn3.nga.R;
import com.coldmn3.nga.adapter.ListViewAccountAdapter;
import com.coldmn3.nga.app.AppContext;

public class Account extends BaseActivity {

	private View addAccountFooter;
	private ListViewAccountAdapter adapter;
	private ListView listView;
	private AppContext appContext;
	private List<String> account_data;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.account);

		appContext = (AppContext) getApplication();

		((TextView) findViewById(R.id.title_text)).setText("账号管理");
		
		listView = (ListView) findViewById(R.id.account_list);
		addAccountFooter = getLayoutInflater().inflate(R.layout.account_add_item, null);
		
		account_data = new ArrayList<String>(appContext.getUserList().keySet());
		adapter = new ListViewAccountAdapter(this, account_data);
		listView.addFooterView(addAccountFooter);
		listView.setAdapter(adapter);
		
		addAccountFooter.setBackgroundResource(R.drawable.listitem_selector_holo_blue);
		addAccountFooter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Account.this, Login.class);
				intent.putExtra("action", "add");
				startActivityForResult(intent, 1);
			}
		});

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				appContext.setCurrentUserName(account_data.get(position));
				appContext.setUserProperty("currentUser", appContext.getCurrentUserName());
				adapter.notifyDataSetChanged();
			}
		});
	}
	
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == 1 && resultCode == RESULT_OK) {
			account_data.clear();
			account_data.addAll(appContext.getUserList().keySet());
			adapter.notifyDataSetChanged();
		}
	}



	public void back(View view) {
		this.finish();
	}

	public void clear(View view) {
		confirmDel("", 1);

	}

	private void confirmDel(String name, final int flag) {
		Builder builder = new Builder(this);
		if (flag == 1) {
			builder.setMessage("是否清空登陆信息？");
		} else if (flag == 2) {
			builder.setMessage("是否删除此账号？");
		}
		builder.setTitle("删除");
		builder.setPositiveButton("确认", new android.content.DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				if (flag == 1) {
					appContext.cleanLoginInfo();
					appContext.getUserList().clear();
					account_data.clear();
					appContext.setCurrentUserName("");
					adapter.notifyDataSetChanged();

					Intent intent = new Intent(Account.this, Login.class);
					intent.putExtra("action", "main");
					startActivity(intent);
					Account.this.finish();
				} else if (flag == 2) {
				}
				dialog.dismiss();
			}
		});
		builder.setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}

}
