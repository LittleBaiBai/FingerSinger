package com.game.fingersinger;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class Loading extends Activity {

	private static ImageView loading;
	private static AnimationSet as;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.loading);
        loading = (ImageView) findViewById(R.id.loading_picture);
        
        initDeclare();

        ProgressBar progress = (ProgressBar)findViewById(R.id.loading_progress);
        progress.setVisibility(View.GONE);
//        
//        EditText continueInfo = (EditText)findViewById(R.id.continue_text);
//        continueInfo.setVisibility(View.VISIBLE);
//        
        as = new AnimationSet(true);//定义一个新AnimationSet
		
		AlphaAnimation aa = new AlphaAnimation(1.0f, 0.0f);  
		aa.setDuration(2000);
		as.addAnimation(aa);
		
		ScaleAnimation sa = new ScaleAnimation(0.1f, 3.0f, 0.1f, 3.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//		结束x坐标伸缩尺寸，结束y坐标伸缩尺寸，x轴的百分比，y轴的百分比)//全员float可，
		sa.setDuration(3000);
		as.addAnimation(sa);
		
        loading.startAnimation(as);
        
		new Thread(new Runnable() {

			@Override
			public void run() {
				
				try {
					Thread.sleep(1800);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				startActivity(new Intent(getApplication(), MainActivity.class));
				Loading.this.finish();
	            
			}
    	
		}).start();

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