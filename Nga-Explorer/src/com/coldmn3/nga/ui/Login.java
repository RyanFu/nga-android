package com.coldmn3.nga.ui;

import java.lang.ref.WeakReference;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.coldmn3.nga.R;
import com.coldmn3.nga.app.AppContext;
import com.coldmn3.nga.app.AppException;
import com.coldmn3.nga.bean.User;
import com.yulingtech.lycommon.util.AndroidUtils;
import com.yulingtech.lycommon.util.StringUtils;
import com.yulingtech.lycommon.util.ULog;
import com.yulingtech.lycommon.widget.ZakerProgressDialog;

/**
 * 登陆Activity
 * 
 * @author session
 * @date 2013-3-21
 * @version v1.0
 */
public class Login extends BaseActivity {

	private static final String LOG_TAG = "Login";

	private final static int LOGIN_SUCCESS = 1;
	private final static int LOGIN_FAILURE = 0;
	private final static int LOGIN_EXCEPTION = -1;

	private InputMethodManager imm;
	private AppContext appContext;

	private EditText accountEdit;
	private EditText pwdEditText;
	private Button btn_login;

	private ZakerProgressDialog progressDialog;

	private String action = "main";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		appContext = (AppContext) getApplication();
		imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

		((TextView) findViewById(R.id.title_text)).setText("登录验证");

		accountEdit = (EditText) findViewById(R.id.login_account);
		pwdEditText = (EditText) findViewById(R.id.login_pwd);
		btn_login = (Button) findViewById(R.id.login_btn);

		action = getIntent().getExtras().getString("action");
		if (StringUtils.isEmpty(action)) {
			action = "main";
		}
	}

	public void back(View view) {
		this.finish();
	}

	public void onLogin(View view) {
		if (!AndroidUtils.isFastDoubleClick()) {
			ULog.i(LOG_TAG, "start login...");
			imm.hideSoftInputFromWindow(btn_login.getWindowToken(), 0);

			String account = accountEdit.getText().toString();
			String pwd = pwdEditText.getText().toString();

			if (StringUtils.isEmpty(account)) {
				AndroidUtils.Toast(this, getString(R.string.msg_login_account_null));
				return;
			}

			if (StringUtils.isEmpty(pwd)) {
				AndroidUtils.Toast(this, getString(R.string.msg_login_pwd_null));
				return;
			}

			progressDialog = new ZakerProgressDialog(this, R.style.CustomProgressDialog);
			progressDialog.show();
			login(account, pwd);
		}
	}

	private void login(final String account, final String password) {
		final Handler handler = new LoginHandler(this);

		new Thread() {

			@Override
			public void run() {
				Message msg = new Message();
				try {
					User user = appContext.LoginValidation(account, password);
					if (user != null && user.getUid() != "") {
						msg.what = LOGIN_SUCCESS;
						msg.obj = user;
					} else {
						msg.what = LOGIN_FAILURE;
					}
				} catch (AppException e) {
					e.printStackTrace();
					msg.what = LOGIN_EXCEPTION;
					msg.obj = e;
				}
				handler.sendMessage(msg);
			}

		}.start();
	}

	static class LoginHandler extends Handler {
		private final WeakReference<Login> mLogin;

		public LoginHandler(Login login) {
			mLogin = new WeakReference<Login>(login);
		}

		@Override
		public void handleMessage(Message msg) {
			Login login = mLogin.get();

			if (login != null) {
				if (login.progressDialog.isShowing()) {
					login.progressDialog.dismiss();
				}
				if (msg.what == LOGIN_SUCCESS) {
					AndroidUtils.Toast(login, R.string.msg_login_success);
					// 跳转
					if (login.action.equals("main")) {
						login.startActivity(new Intent(login, Main.class));
						login.finish();
					} else {
						login.setResult(RESULT_OK);
						login.finish();
					}

				} else if (msg.what == LOGIN_FAILURE) {
					AndroidUtils.Toast(login, login.getString(R.string.msg_login_failure));
				} else if (msg.what == LOGIN_EXCEPTION) {
					((AppException) msg.obj).makeToast(login);
				}
			}

		}
	}
}
