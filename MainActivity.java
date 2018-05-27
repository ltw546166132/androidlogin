package com.example.androidload;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.storage.OnObbStateChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class MainActivity extends Activity {private final int RETURN_VALUE = 0;
	private String path = "http://192.168.0.100:8080/Weblogin/Upload";
	private EditText et_name;
	private EditText et_password;
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case RETURN_VALUE:
				Toast.makeText(getApplicationContext(), (String)msg.obj, Toast.LENGTH_SHORT).show();
				break;
	
			default:
				break;
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		et_name = (EditText) findViewById(R.id.username);
		et_name.setText("abc啦啦啦");
		et_password = (EditText) findViewById(R.id.password);
		Button bt_login = (Button) findViewById(R.id.login);
		Button bt_postlogin = (Button) findViewById(R.id.loginpost);
		bt_login.setOnClickListener(new BtnOnclicklistener());
		bt_postlogin.setOnClickListener(new BtnOnclicklistener());
	}

	static class HttpUtils{
		private static byte[] byteArrays;
		private static String returnValue;
		public static String getValue(InputStream in) {
			ByteArrayOutputStream bao = new ByteArrayOutputStream();
			byte[] emp = new byte[1024];
			int tagcode = -1;
			try {
				while((tagcode=in.read(emp))!=-1) {
					bao.write(emp, 0, emp.length);
				}
				byteArrays = bao.toByteArray();
				returnValue = new String(byteArrays,"utf-8");
			} catch (Exception e) {
				// TODO: handle exception
			}
			return returnValue;
		}
	}
	
	class BtnOnclicklistener implements OnClickListener{

		@Override
		public void onClick(final View v) {
			// TODO Auto-generated method stub
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						if(v.getId()==R.id.login) {
							try {
								String username = et_name.getText().toString();
								String password = et_password.getText().toString();
								String urlpath = path+"?username="+URLEncoder.encode(username, "utf-8")+"&password="+URLEncoder.encode(password, "utf-8");
								Log.w("urlpath", urlpath);
								URL url = new URL(urlpath);
								HttpURLConnection openConnection = (HttpURLConnection) url.openConnection();
								openConnection.setRequestMethod("GET");
								openConnection.setConnectTimeout(5*1000);
								if(openConnection.getResponseCode()==200) {
									InputStream inputStream = openConnection.getInputStream();
									String returnvalue = HttpUtils.getValue(inputStream);
									Message msg = Message.obtain();
									msg.obj = returnvalue;
									msg.what = RETURN_VALUE;
									handler.sendMessage(msg);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}else if(v.getId()==R.id.loginpost) {
							try {
								String username = et_name.getText().toString();
								String password = et_password.getText().toString();
								String params = "username="+URLEncoder.encode(username, "utf-8")+"&password="+URLEncoder.encode(password, "utf-8");
								URL url = new URL(path);
								HttpURLConnection connection = (HttpURLConnection) url.openConnection();
								connection.setRequestMethod("POST");
								connection.setConnectTimeout(10000);
								//向服务器发送要传递的数据类型
								connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
								//向服务器发送要传递的数据长度
								connection.setRequestProperty("Content-Length", String.valueOf(params.length()));
								connection.setDoOutput(true);
								//通过流把请求体写到服务器
								connection.getOutputStream().write(params.getBytes());
								if(connection.getResponseCode()==200) {
									InputStream inputStream = connection.getInputStream();
									String value = HttpUtils.getValue(inputStream);
									Message message = Message.obtain();
									message.obj = value;
									message.what = RETURN_VALUE;
									handler.sendMessage(message);
								}
									
							} catch (Exception e) {
								// TODO: handle exception
							}
							
						}
						
					}
				}).start();
			}
		
		}
	
}
