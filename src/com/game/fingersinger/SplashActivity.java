package com.game.fingersinger;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

public class SplashActivity extends Activity {
	boolean isFirstIn = false;

	private static final int GO_HOME = 1000;
	private static final int GO_GUIDE = 1001;
	// 延迟3秒
	private static final long SPLASH_DELAY_MILLIS = 3000;

	private static final String SHAREDPREFERENCES_NAME = "first_pref";

	private static ImageView loading;
	private static AnimationSet as;
	
	/**
	 * Handler:跳转到不同界面
	 */
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GO_HOME:
				goHome();
				break;
			case GO_GUIDE:
				goGuide();
				break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		//隐去标题栏（应用程序的名字）  
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //隐去状态栏部分(电池等图标和一切修饰部分)

		setContentView(R.layout.splash);
        loading = (ImageView) findViewById(R.id.loading_picture);
        
        init();

	}

	private void init() {
		// 读取SharedPreferences中需要的数据
		// 使用SharedPreferences来记录程序的使用次数
		SharedPreferences preferences = getSharedPreferences(
				SHAREDPREFERENCES_NAME, MODE_PRIVATE);
		// 取得相应的值，如果没有该值，说明还未写入，用true作为默认值
		isFirstIn = preferences.getBoolean("isFirstIn", true);
		
		initDeclare();		
		  
        as = new AnimationSet(true);//定义一个新AnimationSet
        
		AlphaAnimation aa = new AlphaAnimation(1.0f, 0.0f);  
		aa.setDuration(3000);
		as.addAnimation(aa);
		
		ScaleAnimation sa = new ScaleAnimation(0.1f, 3.0f, 0.1f, 3.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//		结束x坐标伸缩尺寸，结束y坐标伸缩尺寸，x轴的百分比，y轴的百分比)//全员float可，
		sa.setDuration(4000);
		as.addAnimation(sa);
		
        loading.startAnimation(as);
        
		// 判断程序与第几次运行，如果是第一次运行则跳转到引导界面，否则跳转到主界面
		if (!isFirstIn) {
			// 使用Handler的postDelayed方法，3秒后执行跳转到MainActivity
			mHandler.sendEmptyMessageDelayed(GO_HOME, SPLASH_DELAY_MILLIS);
		} else {
			mHandler.sendEmptyMessageDelayed(GO_GUIDE, SPLASH_DELAY_MILLIS);
		}

	}

	private void goHome() {
		Intent intent = new Intent(SplashActivity.this, MainActivity.class);
		SplashActivity.this.startActivity(intent);
		SplashActivity.this.finish();
	}

	private void goGuide() {
		Intent intent = new Intent(SplashActivity.this, GuideActivity.class);
		SplashActivity.this.startActivity(intent);
		SplashActivity.this.finish();
	}

	private void initDeclare() {
		Declare.menu_status = 1;
		Declare.color_status = 0;
		
		for (int i = 0; i < 5; i++){
        	Declare.soundManager[i] = new SoundManager();
        	Declare.soundManager[i].initSounds(getBaseContext(), i);
        }
		
		Declare.screen_width = getWindowManager().getDefaultDisplay().getWidth();
		Declare.screen_height = getWindowManager().getDefaultDisplay().getHeight();
		
		Declare.pointer_unpress = (float) (Declare.screen_width * 42 / 800);
		Declare.note_inner_dist = (float) (Declare.screen_height * 20 / 480);
		Declare.note_top_dist = (float) (Declare.screen_height * 23 / 480);
		Declare.note_button_dist = (float) (Declare.screen_height * 336 / 480);
		Declare.draw_height = (float) (Declare.screen_height * 366 / 480);
		Declare.tempo_length = (int)(Declare.screen_height * 40 / 480);
		Declare.pointerInScreen = (int) 0;
		
		Declare.colors[0] = getResources().getColor(R.color.blue); 
		Declare.colors[1] = getResources().getColor(R.color.yellow);
		Declare.colors[2] = getResources().getColor(R.color.purple);
		Declare.colors[3] = getResources().getColor(R.color.orange);
		Declare.colors[4] = getResources().getColor(R.color.green);
	}
	
}