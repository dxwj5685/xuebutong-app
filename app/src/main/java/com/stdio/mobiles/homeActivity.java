package com.stdio.mobiles;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputLayout;
import com.hjq.window.EasyWindow;
import com.hjq.window.draggable.SpringDraggable;
import com.kongzue.dialogx.DialogX;
import com.kongzue.dialogx.dialogs.MessageDialog;
import com.kongzue.dialogx.interfaces.OnBindView;
import com.kongzue.dialogx.interfaces.OnDialogButtonClickListener;
import com.kongzue.dialogx.util.TextInfo;
import com.kongzue.dialogxmaterialyou.style.MaterialYouStyle;
import me.jingbin.library.ByRecyclerView;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class homeActivity extends AppCompatActivity {
	
	private static final int REQUEST_CODE_FLOATING_WINDOW = 123; // 自定义的请求码
	public static TextView logtext;
	public static ScrollView loggd;
	private static String cookie = "";
	private static String uuid;
	private static String enc;
	private static ImageView imageViewl;
	private static LinearLayout load;
	private static ByRecyclerView recyclerView;
	private static boolean 重新加载 = false;
	EditText user;
	EditText pass;
	private msgad mmsgad;
	private List<msg> msgList = new ArrayList<msg>();
	private TextInputLayout useriup;
	private TextInputLayout passiup;
	private MessageDialog mMessageDialog;
	private EasyWindow with;
	
	public static String maskString(String str, int start, int end) {
		if (str == null || str.isEmpty() || start < 0 || end > str.length() || start >= end) {
			return str;
		}
		char[] chars = str.toCharArray();
		for (int i = start; i < end; i++) {
			chars[i] = '*';
		}
		return new String(chars);
	}
	
	@SuppressLint({"MissingInflatedId", "ObsoleteSdkInt"})
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		
		DialogX.init(this);
		DialogX.globalStyle = MaterialYouStyle.style();
		
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		APL(toolbar);
		
		Window window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
		window.setStatusBarColor(ContextCompat.getColor(this, R.color.gree));
		
		
		if (!(TextUtils.isEmpty(getFile("cookie.txt")))) {
			if (getFile("v1.so").contains("true")) {
				cookie = getFile("cookie.txt");
				info();
			} else {
				MessageDialog.show("cookie", "发现有历史cookie，是否导入上次记录的cookie账号", "导入", "取消")
						//.setTitleIcon(R.mipmap.i)
						.setBackgroundColor(Color.parseColor("#FDF8FC"))
						.setOkButton(new OnDialogButtonClickListener<MessageDialog>() {
							@Override
							public boolean onClick(MessageDialog baseDialog, View v) {
								cookie = getFile("cookie.txt");
								new Thread(new Runnable() {
									@Override
									public void run() {
										info();
									}
								}).start();
								return false;
							}
						})
						.setCancelButton(new OnDialogButtonClickListener<MessageDialog>() {
							@Override
							public boolean onClick(MessageDialog baseDialog, View v) {
								
								return false;
							}
						})
						.setOkTextInfo(new TextInfo().setFontColor(Color.parseColor("#E30113")).setBold(true))
						.setCancelTextInfo(new TextInfo().setFontColor(Color.parseColor("#E30113")).setBold(true));
			}
			
		}
		
		@SuppressLint({"MissingInflatedId", "LocalSuppress"})
		LinearLayout button = findViewById(R.id.sx);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				重新加载 = true;
				cookie = "";
				cookie();
			}
			
		});
		imageViewl = findViewById(R.id.image);
		
		recyclerView = findViewById(R.id.lb);
		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
		recyclerView.setLayoutManager(linearLayoutManager);
		load = findViewById(R.id.load);//列表隐藏图
/*
        recyclerView.setOnRefreshListener(new ByRecyclerView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                msgList.clear();
                wo();
                recyclerView.setRefreshing(false);
            }
        });*/
		
		
		LinearLayout login = findViewById(R.id.login);
		login.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mMessageDialog = MessageDialog.show("账号登录", null, "登录", "取消")
						.setBackgroundColor(Color.parseColor("#FDF8FC"))
						.setCustomView(new OnBindView<MessageDialog>(R.layout.edtext) {
							@Override
							public void onBind(MessageDialog dialog, View v) {
								
								useriup = v.findViewById(R.id.useriup);
								passiup = v.findViewById(R.id.passiup);
								user = v.findViewById(R.id.user);
								pass = v.findViewById(R.id.pass);
								if (getFile("user.so") != null || getFile("pass.so") != null) {
									user.setText(getFile("user.so"));
									pass.setText(getFile("pass.so"));
								}
							}
						})
						.setOkButton(new OnDialogButtonClickListener<MessageDialog>() {
							@Override
							public boolean onClick(MessageDialog baseDialog, View v) {
								Log.w("user", user.getText().toString());
								Log.w("pass", pass.getText().toString());
								login(user.getText().toString(), pass.getText().toString());
								return true;
							}
						})
						.setCancelButton(new OnDialogButtonClickListener<MessageDialog>() {
							@Override
							public boolean onClick(MessageDialog baseDialog, View v) {
								
								return false;
							}
						})
						.setOkTextInfo(new TextInfo().setFontColor(Color.parseColor("#E30113")).setBold(true))
						.setCancelTextInfo(new TextInfo().setFontColor(Color.parseColor("#E30113")).setBold(true));
			}
		});
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
			Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + this.getPackageName()));
			startActivityForResult(intent, REQUEST_CODE_FLOATING_WINDOW);
		} else {
			logbt();
		}
	}
	
	private void APL(Toolbar toolbar) {
		OkHttpClient okHttpClient = new OkHttpClient();
		Request request = new Request.Builder().url("https://v1.hitokoto.cn/").build();
		Call call = okHttpClient.newCall(request);
		call.enqueue(new Callback() {
			@Override
			public void onFailure(@NotNull Call call, @NotNull IOException e) {
				e.printStackTrace();
			}
			
			@Override
			public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
				if (response.isSuccessful()) {
					if (response.body().toString() != null) {
						try {
							JSONObject jsonObject = new JSONObject(response.body().string());
							if (jsonObject.has("hitokoto")) {
								String hitokoto = jsonObject.getString("hitokoto");
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										toolbar.setSubtitle(hitokoto);
									}
								});
							} else {
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										toolbar.setSubtitle("Stdio 工作室");
									}
								});
							}
						} catch (JSONException e) {
							throw new RuntimeException(e);
						}
					} else {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								toolbar.setSubtitle("Stdio 工作室");
							}
						});
					}
				} else {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							toolbar.setSubtitle("Stdio 工作室");
						}
					});
				}
				
			}
		});
	}
	
	private void logbt() {
		//ToDO 设置Log悬浮窗
		with = EasyWindow.with(this.getApplication());
		with.setContentView(R.layout.log);
		with.setYOffset(900);
		with.setDraggable(new SpringDraggable());
		with.setGravity(Gravity.START | Gravity.TOP);
		//with.setAnimStyle(android.R.style.Animation_Translucent);
		with.setOnClickListener(R.id.zlog, new EasyWindow.OnClickListener<CardView>() {
			@Override
			public void onClick(EasyWindow<?> window, CardView view) {
				CardView logCardView = (CardView) with.findViewById(R.id.logbt);
				CardView logo = (CardView) with.findViewById(R.id.zlog);
				logCardView.setVisibility(View.VISIBLE);
				logo.setVisibility(View.GONE);
			}
		});
		with.setOnClickListener(R.id.slog, new EasyWindow.OnClickListener<ImageView>() {
			@Override
			public void onClick(EasyWindow<?> window, ImageView view) {
				CardView logCardView = (CardView) with.findViewById(R.id.logbt);
				CardView logo = (CardView) with.findViewById(R.id.zlog);
				logCardView.setVisibility(View.GONE);
				logo.setVisibility(View.VISIBLE);
			}
		});
		with.show();
		logtext = (TextView) with.findViewById(R.id.logtext);
		loggd = (ScrollView) with.findViewById(R.id.loggd);
		
	}
	
	//账号密码登录
	private void login(String user, String pass) {
		OkHttpClient okHttpClient = new OkHttpClient();
		RequestBody formBody = new FormBody.Builder()
				.add("fid", "-1")
				.add("uname", user)
				.add("password", pass)
				.add("refer", "https://i.chaoxing.com")
				.add("t", "true")
				.add("forbidotherlogin", "0")
				.add("validate", "")
				.add("doubleFactorLogin", "0")
				.add("independentId", "0")
				.build();
		Request request = new Request.Builder()
				.url("https://passport2.chaoxing.com/fanyalogin")
				.post(formBody)
				.build();
		Call call = okHttpClient.newCall(request);
		call.enqueue(new Callback() {
			@Override
			public void onFailure(@NotNull Call call, @NotNull IOException e) {
			
			}
			
			@Override
			public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
				try {
					String respBody = response.body().string();
					JSONObject jsonObject = new JSONObject(respBody);
					boolean status = jsonObject.optBoolean("status", false);
					if (status) {
						mMessageDialog.dismiss();
						setFile(user, "user.so");
						setFile(pass, "pass.so");
						// 从响应头提取所有Set-Cookie
						Headers headers = response.headers();
						StringBuilder cookieBuilder = new StringBuilder();
						for (int i = 0; i < headers.size(); i++) {
							if (headers.name(i).equalsIgnoreCase("Set-Cookie")) {
								String cookieValue = headers.value(i).split(";")[0];
								cookieBuilder.append(cookieValue).append(";");
							}
						}
						cookie = cookieBuilder.toString();
						Log.w("login-cookie", cookie);
						
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								setFile(cookie, "cookie.txt");
								Calendar calendar = Calendar.getInstance();
								SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ", Locale.getDefault());
								String currentTime = sdf.format(calendar.getTime());
								logtext.setText(logtext.getText() + "\nCD:" + currentTime + "账号登录成功 HTTP/1.1");
								loggd.scrollTo(0, loggd.getChildAt(0).getHeight());
								new Thread(new Runnable() {
									@Override
									public void run() {
										info();
										Log.w("------cookie", String.valueOf(cookie));
									}
								}).start();
							}
						});
					} else {
						String mes = jsonObject.optString("msg2", "登录失败");
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								passiup.setError(mes);
								Calendar calendar = Calendar.getInstance();
								SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ", Locale.getDefault());
								String currentTime = sdf.format(calendar.getTime());
								logtext.setText(logtext.getText() + "\nCD:" + currentTime + mes + " HTTP/1.1");
								loggd.scrollTo(0, loggd.getChildAt(0).getHeight());
							}
						});
					}
				} catch (JSONException e) {
					Log.e("login", "JSON解析失败: " + e.getMessage());
				}
			}
		});
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		
		if (id == R.id.bout) {
			startActivity(new Intent(homeActivity.this, SetActivity.class));
			return true;
		} else if (id == R.id.zc) {
			MessageDialog.show("使用文档", "本应用仅供用户学习或参考使用，版权归原作者所有，仅在非商业用途下使用。\n\n本应用目前只支持自动刷视频，自动刷音频，自动判断是否已完成任务点，自动点击播放视频，自动跳转下一节任务点，自动播放视频静音，支持拦截课堂测试题，支持秒过PPT，WORD等任务点，支持秒过文章任务点，暂不支持题库秒过，后期我们也会逐渐更新，目前实现刷所有课程的视频,ppt,word,pdf任务点\n\n目前可以实现本机挂后台运行，也可以配合VMOS云手机/红手指，可以实现24小时挂云端不停机刷课，实现手机自由，具体使用教程可以进群:538234048\n\n谢谢！", "确定", "视频教程", "文档教程")
					//.setTitleIcon(R.mipmap.i)
					.setBackgroundColor(Color.parseColor("#FDF8FC"))
					.setOkButton(new OnDialogButtonClickListener<MessageDialog>() {
						@Override
						public boolean onClick(MessageDialog baseDialog, View v) {
							
							return false;
						}
					})
					.setCancelButton(new OnDialogButtonClickListener<MessageDialog>() {
						@Override
						public boolean onClick(MessageDialog baseDialog, View v) {
							if (MainActivity.vide_address != null) {
								startActivity(new android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(MainActivity.vide_address)));
							}
							return false;
						}
					})
					.setOtherButton(new OnDialogButtonClickListener<MessageDialog>() {
						@Override
						public boolean onClick(MessageDialog baseDialog, View v) {
							if (MainActivity.API_address != null) {
								startActivity(new android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(MainActivity.API_address)));
							}
							return false;
						}
					})
					.setOkTextInfo(new TextInfo().setFontColor(Color.parseColor("#E30113")).setBold(true))
					.setCancelTextInfo(new TextInfo().setFontColor(Color.parseColor("#E30113")).setBold(true))
					.setOtherTextInfo(new TextInfo().setFontColor(Color.parseColor("#E30113")).setBold(true));
			
			return true;
		} else if (id == R.id.ds) {
			startActivity(new Intent(this, sponsor.class));
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	//ToDO 获取学习通cookie
	protected void cookie() {
		OkHttpClient okHttpClient = new OkHttpClient();
		Request request = new Request.Builder().url("https://passport2.chaoxing.com/login?fid=&newversion=true&refer=http://i.chaoxing.com").get().build();
		Call call = okHttpClient.newCall(request);
		call.enqueue(new Callback() {
			@Override
			public void onFailure(@NonNull Call call, @NonNull IOException e) {
				Log.w("------", "错误" + e.toString());
			}
			
			@Override
			public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
				String data = response.body().string();
				Pattern uuidpa = Pattern.compile(" <input type = \"hidden\" value=\"(.*?)\" id = \"uuid\"/>");
				Matcher uuidma = uuidpa.matcher(data);
				Pattern encpa = Pattern.compile(" <input type = \"hidden\" value=\"(.*?)\" id = \"enc\"/>");
				Matcher encma = encpa.matcher(data);
				if (uuidma.find() && encma.find()) {
					uuid = uuidma.group(1);
					enc = encma.group(1);
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Log.w("key", enc + "\n" + uuid);
							String url = "https://passport2.chaoxing.com/createqr?uuid=" + uuid + "&fid=-1";
							Glide.with(homeActivity.this)
									.load(url)
									.into(imageViewl);
							Handler handler = new Handler();
							handler.postDelayed(new Runnable() {
								@Override
								public void run() {
									重新加载 = false;
									key("https://passport2.chaoxing.com/getauthstatus?enc=" + enc + "&uuid=" + uuid);
								}
							}, 100);
						}
					});
				}
			}
		});
	}
	
	//检测二维码
	private void key(String url) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				
				OkHttpClient okHttpClient = new OkHttpClient();
				Request request = new Request.Builder()
						.url(url)
						.get()
						.build();
				Call call = okHttpClient.newCall(request);
				call.enqueue(new Callback() {
					@Override
					public void onFailure(@NonNull Call call, @NonNull IOException e) {
					
					}
					
					@Override
					public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
						try {
							String data = response.body().string();
							JSONObject jsonObject = new JSONObject(data);
							String msg = jsonObject.getString("mes");
							if (msg.equals("二维码已失效")) {
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										//获取当前时间
										Calendar calendar = Calendar.getInstance();
										SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ", Locale.getDefault());
										String currentTime = sdf.format(calendar.getTime());
										logtext.setText(logtext.getText() + "\nCD:" + currentTime + msg + " HTTP/1.1");
										loggd.scrollTo(0, loggd.getChildAt(0).getHeight());
									}
								});
							} else if (msg.equals("已扫描")) {
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										//获取当前时间
										Calendar calendar = Calendar.getInstance();
										SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ", Locale.getDefault());
										String currentTime = sdf.format(calendar.getTime());
										logtext.setText(logtext.getText() + "\nCD:" + currentTime + msg + " HTTP/1.1");
										loggd.scrollTo(0, loggd.getChildAt(0).getHeight());
									}
								});
								if (重新加载 == false) {
									key("https://passport2.chaoxing.com/getauthstatus?enc=" + enc + "&uuid=" + uuid);
								}
							} else if (msg.equals("验证通过")) {
								
								Headers headers = response.headers();
								
								//获取个人信息cookie
								String uf = "uf=([^;]*)";
								String UID = "UID=([^;]*)";
								String vc = "vc=([^;]*)";
								Pattern pattern_uf = Pattern.compile(uf);
								Pattern pattern_UID = Pattern.compile(UID);
								Pattern pattern_vc = Pattern.compile(vc);
								Matcher matcher_uf = pattern_uf.matcher(String.valueOf(headers));
								Matcher matcher_UID = pattern_UID.matcher(String.valueOf(headers));
								Matcher matcher_vc = pattern_vc.matcher(String.valueOf(headers));
								if (matcher_uf.find()) {
									String ufValue = matcher_uf.group(1); // 获取匹配到的值
									cookie = cookie + "uf=" + ufValue + ";";
								}
								if (matcher_UID.find()) {
									String UIDValue = matcher_UID.group(1);
									cookie = cookie + "UID=" + UIDValue + ";";
								}
								if (matcher_vc.find()) {
									String vcValue = matcher_vc.group(1);
									cookie = cookie + "vc=" + vcValue + ";";
								}
								
								//获取个人课程cookie
								String _uid = "_uid=([^;]*)";
								String _d = "_d=([^;]*)";
								String vc3 = "vc3=([^;]*)";
								Pattern pattern_uid = Pattern.compile(_uid);
								Pattern pattern_d = Pattern.compile(_d);
								Pattern pattern_vc3 = Pattern.compile(vc3);
								Matcher matcher_uid = pattern_uid.matcher(String.valueOf(headers));
								Matcher matcher_d = pattern_d.matcher(String.valueOf(headers));
								Matcher matcher_vc3 = pattern_vc3.matcher(String.valueOf(headers));
								if (matcher_uid.find()) {
									String uidValue = matcher_uid.group(1); // 获取匹配到的值
									cookie = cookie + "_uid=" + uidValue + ";";
								}
								if (matcher_d.find()) {
									String _dValue = matcher_d.group(1);
									cookie = cookie + "_d=" + _dValue + ";";
								}
								if (matcher_vc3.find()) {
									String vc3Value = matcher_vc3.group(1);
									cookie = cookie + "vc3=" + vc3Value + ";";
								}
								
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										setFile(cookie, "cookie.txt");//记录cookie
										//获取当前时间
										Calendar calendar = Calendar.getInstance();
										SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ", Locale.getDefault());
										String currentTime = sdf.format(calendar.getTime());
										logtext.setText(logtext.getText() + "\nCD:" + currentTime + msg + " HTTP/1.1");
										loggd.scrollTo(0, loggd.getChildAt(0).getHeight());
										new Thread(new Runnable() {
											@Override
											public void run() {
												info();
												Log.w("------cookie", String.valueOf(cookie));
											}
										}).start();
										
									}
								});
								
							} else {
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										//获取当前时间
										Calendar calendar = Calendar.getInstance();
										SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ", Locale.getDefault());
										String currentTime = sdf.format(calendar.getTime());
										logtext.setText(logtext.getText() + "\nCD:" + currentTime + msg + " HTTP/1.1");
										loggd.scrollTo(0, loggd.getChildAt(0).getHeight());
									}
								});
								if (重新加载 == false) {
									key("https://passport2.chaoxing.com/getauthstatus?enc=" + enc + "&uuid=" + uuid);
								}
							}
						} catch (IOException | JSONException e) {
							e.printStackTrace();
						}
					}
				});
				
			}
		}).start();
		
	}
	
	//获取个人信息
	private void info() {
		OkHttpClient okHttpClient = new OkHttpClient();
		Request request = new Request.Builder()
				.url("https://sso.chaoxing.com/apis/login/userLogin4Uname.do")
				.addHeader("Cookie", cookie)
				.get()
				.build();
		Call call = okHttpClient.newCall(request);
		call.enqueue(new Callback() {
			@Override
			public void onFailure(@NonNull Call call, @NonNull IOException e) {
			
			}
			
			@Override
			public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
				String data = response.body().string();
				if (data.contains("请重新登录")) {
					deleteFile("cookie.txt");
				} else {
					try {
						Log.w("------", data);
						JSONObject jsonObject = new JSONObject(data);
						JSONObject msg = jsonObject.getJSONObject("msg");
						String pic = msg.optString("pic");
						Log.w("------", pic);
						String uname = msg.optString("uname");
						String schoolname = msg.optString("schoolname");
						String phone = msg.optString("phone");
						String name = msg.optString("name");
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Glide.with(homeActivity.this)
										.load(pic.replace("psize=100_100c", "psize=640_640c"))
										.into(imageViewl);
								TextView textView1 = findViewById(R.id.name);
								textView1.setText(maskString(name, 1, name.length()) + "/" + maskString(uname, 5, 8));
								TextView textView2 = findViewById(R.id.phone);
								textView2.setText("Phone:" + maskString(phone, 3, 9));
								TextView textView3 = findViewById(R.id.school);
								textView3.setText(schoolname);
								msgList.clear();
								wo();
								
								
							}
						});
					} catch (JSONException e) {
						throw new RuntimeException(e);
					}
				}
			}
		});
	}
	
	//获取我的课程
	private void wo() {
		OkHttpClient okHttpClient = new OkHttpClient();
		RequestBody formBody = new FormBody.Builder()
				.add("courseType", "1")
				.add("courseFolderId", "0")
				.add("query", "")
				.add("superstarClass", "0")
				.build();
		Request request = new Request.Builder()
				.url("https://mooc2-ans.chaoxing.com/mooc2-ans/visit/courselistdata")
				.addHeader("Cookie", cookie)
				.addHeader("User-Agent", "Mozilla/5.0 (Linux; Android 12; Pixel 5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Mobile Safari/537.36")
				.addHeader("Referer", "https://mooc2-ans.chaoxing.com/mooc2-ans/visit/interaction")
				.post(formBody)
				.build();
		Call call = okHttpClient.newCall(request);
		call.enqueue(new Callback() {
			@Override
			public void onFailure(@NonNull Call call, @NonNull IOException e) {
				Log.e("课程", e.toString());
				
			}
			
			
			@Override
			public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
				String html = response.body().string();
				Log.w("课程", html);
				parseCourseListHtml(html);
			}
		});
	}
	
	// 解析新版课程列表HTML
	private void parseCourseListHtml(String html) {
		try {
			// 使用正则从HTML中提取课程信息
			// 课程链接格式: courseId=xxx&clazzid=xxx&cpi=xxx
			Pattern coursePattern = Pattern.compile("courseId=(\\d+)&clazzid=(\\d+)&cpi=(\\d+)");
			// 课程名称
			Pattern namePattern = Pattern.compile("class=\"course-name[^\"]*\"[^>]*title=\"([^\"]+)\"");
			// 老师名称
			Pattern teacherPattern = Pattern.compile("class=\"bottom-[^\"]*teacher[^\"]*\"[^>]*>([^<]+)<");
			// 课程图片
			Pattern imgPattern = Pattern.compile("<img[^>]+class=\"course-cover\"[^>]+src=\"([^\"]+)\"");
			
			Matcher courseMatcher = coursePattern.matcher(html);
			Matcher nameMatcher = namePattern.matcher(html);
			
			// 备用：直接用Jsoup风格的简单解析
			// 新版API返回的HTML中每个课程是一个li.course-item
			Pattern itemPattern = Pattern.compile(
				"<li[^>]*class=\"[^\"]*course[^\"]*\"[^>]*>[\\s\\S]*?" +
				"courseId=(\\d+)[&\"]" +
				"[\\s\\S]*?" +
				"clazzid=(\\d+)" +
				"[\\s\\S]*?" +
				"cpi=(\\d+)" +
				"[\\s\\S]*?" +
				"title=\"([^\"]*?)\"" +
				"[\\s\\S]*?" +
				"</li>"
			);
			
			Matcher itemMatcher = itemPattern.matcher(html);
			boolean found = false;
			while (itemMatcher.find()) {
				found = true;
				String courseId = itemMatcher.group(1);
				String clazzId = itemMatcher.group(2);
				String cpi = itemMatcher.group(3);
				String name = itemMatcher.group(4);
				Log.w("课程解析", "courseId=" + courseId + " clazzId=" + clazzId + " cpi=" + cpi + " name=" + name);
				msgList.add(new msg("", "", "", name != null ? name : "未知课程", "", courseId, cpi, clazzId, clazzId, cookie));
			}
			
			// 如果上面的正则没匹配到，尝试更宽松的匹配
			if (!found) {
				// 匹配所有courseId/clazzid/cpi组合
				Pattern linkPattern = Pattern.compile("courseId=(\\d+)&amp;clazzid=(\\d+)&amp;cpi=(\\d+)");
				Matcher linkMatcher = linkPattern.matcher(html);
				// 匹配所有title
				Pattern titlePattern = Pattern.compile("title=\"([^\"]{2,})\"");
				Matcher titleMatcher = titlePattern.matcher(html);
				
				List<String[]> courses = new ArrayList<>();
				while (linkMatcher.find()) {
					courses.add(new String[]{linkMatcher.group(1), linkMatcher.group(2), linkMatcher.group(3)});
				}
				List<String> titles = new ArrayList<>();
				while (titleMatcher.find()) {
					String t = titleMatcher.group(1);
					if (t != null && !t.isEmpty() && !t.contains("class") && !t.contains("{")) {
						titles.add(t);
					}
				}
				
				for (int i = 0; i < courses.size(); i++) {
					String[] c = courses.get(i);
					String title = i < titles.size() ? titles.get(i) : "课程" + (i + 1);
					Log.w("课程解析2", "courseId=" + c[0] + " clazzId=" + c[1] + " cpi=" + c[2] + " name=" + title);
					msgList.add(new msg("", "", "", title, "", c[0], c[2], c[1], c[1], cookie));
					found = true;
				}
			}
			
			boolean finalFound = found;
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (finalFound && !msgList.isEmpty()) {
						load.setVisibility(View.GONE);
						mmsgad = new msgad(msgList, homeActivity.this);
						recyclerView.setAdapter(mmsgad);
					} else {
						load.setVisibility(View.VISIBLE);
						Log.e("课程", "未能解析到课程数据");
					}
				}
			});
		} catch (Exception e) {
			Log.e("课程", "解析课程列表失败: " + e.getMessage());
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					load.setVisibility(View.VISIBLE);
				}
			});
		}
	}
	
	// 记录cookike
	public void setFile(String str, String file) {
		FileOutputStream fos = null;
		BufferedWriter writer = null;
		
		try {
			fos = openFileOutput(file, Context.MODE_PRIVATE);
			writer = new BufferedWriter(new OutputStreamWriter(fos));
			try {
				writer.write(str);
				Log.w("file", "记录成功");
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	//读取cookie
	public String getFile(String file) {
		FileInputStream fis = null;
		BufferedReader reader = null;
		StringBuilder content = new StringBuilder();
		try {
			fis = openFileInput(file);
			reader = new BufferedReader(new InputStreamReader(fis));
			String str;
			
			while ((str = reader.readLine()) != null) {
				content.append(str);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return content.toString();
	}
	
	public boolean deleteFile(String fileName) {
		File file = new File(getFilesDir(), fileName);
		if (file.exists()) {
			boolean deleted = file.delete();
			if (deleted) {
				Log.w("file", "删除成功");
			} else {
				Log.w("file", "删除失败");
			}
		} else {
			Log.w("file", "文件不存在");
		}
		return false;
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE_FLOATING_WINDOW) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(this)) {
				logbt();
			} else {
				// 用户未授予悬浮窗权限
				MessageDialog.show("权限申请", "本应用需要悬浮窗权限，因为有日志面板的写入，然后本机挂后台刷课也需要悬浮窗权限，需要正常给悬浮窗权限才可以使用\n\n悬浮窗权限不会获取您的个人信息，请放心使用", "好的")
						//.setTitleIcon(R.mipmap.i)
						.setBackgroundColor(Color.parseColor("#FDF8FC"))
						.setOkButton(new OnDialogButtonClickListener<MessageDialog>() {
							@Override
							public boolean onClick(MessageDialog baseDialog, View v) {
								Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + homeActivity.this.getPackageName()));
								startActivityForResult(intent, REQUEST_CODE_FLOATING_WINDOW);
								return false;
							}
						})
						.setOkTextInfo(new TextInfo().setFontColor(Color.parseColor("#E30113")).setBold(true));
			}
		}
	}
	
	
	@Override
	protected void onStop() {
		super.onStop();
		// 在这里执行退出应用时的清理操作
	}
	
	//ToDO 摧毁应用时执行
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
		} else {
			with.cancel();
		}
		
	}
	
}
