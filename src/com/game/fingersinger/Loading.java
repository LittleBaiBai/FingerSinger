package com.game.fingersinger;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

public class Loading extends Activity {

	int MSG_INIT_OK = 1;
	RelativeLayout loading;
	private int count = 70;
	private boolean isContinue;
	private int[] ids = {R.drawable.loading_01, R.drawable.loading_02, R.drawable.loading_03, R.drawable.loading_04, R.drawable.loading_05,
		  				R.drawable.loading_06, R.drawable.loading_07, R.drawable.loading_08, R.drawable.loading_09, R.drawable.loading_09, 
		  				R.drawable.loading_09, R.drawable.loading_09, R.drawable.loading_09, R.drawable.loading_10, R.drawable.loading_11, 
		  				R.drawable.loading_12, R.drawable.loading_13, R.drawable.loading_14, R.drawable.loading_15, R.drawable.loading_16, 
		  				R.drawable.loading_17, R.drawable.loading_17, R.drawable.loading_17, R.drawable.loading_17, R.drawable.loading_17, 
		  				R.drawable.loading_18, R.drawable.loading_19, R.drawable.loading_20, R.drawable.loading_21, R.drawable.loading_22, 
		  				R.drawable.loading_23, R.drawable.loading_24, R.drawable.loading_25, R.drawable.loading_25, R.drawable.loading_25, 
		  				R.drawable.loading_25, R.drawable.loading_25, R.drawable.loading_26, R.drawable.loading_27, R.drawable.loading_28, 
		  				R.drawable.loading_29, R.drawable.loading_30, R.drawable.loading_31, R.drawable.loading_32, R.drawable.loading_33, 
		  				R.drawable.loading_33, R.drawable.loading_33, R.drawable.loading_33, R.drawable.loading_33, R.drawable.loading_34, 
		  				R.drawable.loading_35, R.drawable.loading_36, R.drawable.loading_37, R.drawable.loading_38, R.drawable.loading_39, 
		  				R.drawable.loading_40, R.drawable.loading_41, R.drawable.loading_41, R.drawable.loading_41, R.drawable.loading_41, 
		  				R.drawable.loading_41, R.drawable.loading_42, R.drawable.loading_43, R.drawable.loading_44, R.drawable.loading_45,
		  				R.drawable.loading_46, R.drawable.loading_47, R.drawable.loading_48, R.drawable.loading_48, R.drawable.loading_48};
	  
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.loading);
        loading = (RelativeLayout) findViewById(R.id.loading_layout);
        isContinue = true;
        play();
        
        initDeclare();
        
        ProgressBar progress = (ProgressBar)findViewById(R.id.loading_progress);
        progress.setVisibility(View.GONE);
        
        EditText continueInfo = (EditText)findViewById(R.id.continue_text);
        continueInfo.setVisibility(View.VISIBLE);
        
        loading.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				startActivity(new Intent(getApplication(), MainActivity.class));
    			Loading.this.finish();
			}
        	
        });
//        Message message = new Message(); 
//        message.what = MSG_INIT_OK;                    
//        handler.sendMessage(message);
        
    }
    
    @SuppressLint("HandlerLeak")
	private Handler handler = new Handler(){
        
        public void handleMessage(Message msg)
        {
        	loading.setBackgroundDrawable(getResources().getDrawable(ids[msg.arg1]));
        }
      };
  
    private void play() {
    	Thread thread = new Thread(){
    	
    		@Override
    		public void run()
    		{
    			int i = 0;
    			while(isContinue)
    			{
    				if (i >= count) {
    					i = 0;
    					try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
    				}
    				Message message = new Message(); 
    			    message.what = MSG_INIT_OK;  
    			    message.arg1 = i;
    			    handler.sendMessage(message);
    			    i++;
    				try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
    			}
    		}
    	};
    	thread.start();      
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
		
		Declare.button_menu_vertical = (float) (Declare.screen_height * 70 / 480);
		Declare.button_menu_horizontal = (float) (Declare.screen_width * 70 / 800);
		Declare.button_color_vertical = (float) (Declare.screen_height * 45 / 480);
		Declare.button_color_horizontal = (float) (Declare.screen_width * 73 / 800);
		Declare.button_undo_vertical = (float) (Declare.screen_height * 41 / 480);
		Declare.button_undo_horizontal = (float) (Declare.screen_width * 62 / 800);
		Declare.scale_start_x_y = (float) (Declare.screen_height * 11 / 480);
		Declare.scale_length_vertical = (float) (Declare.screen_height * 28 / 480);
		Declare.pointer_pressed = (float) (Declare.screen_width * 49 / 800);
		Declare.pointer_unpress = (float) (Declare.screen_width * 42 / 800);
		Declare.note_inner_dist = (float) (Declare.screen_height * 20 / 480);
		
		Declare.tempo_length = (int)(Declare.screen_height * 40 / 480);
		Declare.pointerInScreen = (int) Declare.button_menu_horizontal;
		
		Declare.colors[0] = getResources().getColor(R.color.green); 
		Declare.colors[1] = getResources().getColor(R.color.yellow);
		Declare.colors[2] = getResources().getColor(R.color.red);
		Declare.colors[3] = getResources().getColor(R.color.blue);
		Declare.colors[4] = getResources().getColor(R.color.purple);
	}
	
}