package com.game.fingersinger;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class Loading extends Activity {

	int MSG_INIT_OK = 1;
	int MSG_INIT_INFO = 2;
	int MSG_INIT_TIMEOUT = 9;
 
	boolean isTimeout = false;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.loading);
        //initThread();
        initDeclare();
        
        ProgressBar progress = (ProgressBar)findViewById(R.id.loading_progress);
        progress.setVisibility(View.GONE);
        
        EditText continueInfo = (EditText)findViewById(R.id.continue_text);
        continueInfo.setVisibility(View.VISIBLE);
        
        ImageView image = (ImageView)findViewById(R.id.continue_picture);
        image.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				startActivity(new Intent(getApplication(), MainActivity.class));
    			Loading.this.finish();
			}
        	
        });
        
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
		Declare.pointer_stick = (float) (Declare.screen_width * 17 / 800);
		Declare.pointer_dx = (float) (Declare.screen_width * 9 / 800);
		Declare.note_inner_dist = (float) (Declare.screen_height * 20 / 480);
		
		Declare.colors[0] = getResources().getColor(R.color.green); 
		Declare.colors[1] = getResources().getColor(R.color.yellow);
		Declare.colors[2] = getResources().getColor(R.color.red);
		Declare.colors[3] = getResources().getColor(R.color.blue);
		Declare.colors[4] = getResources().getColor(R.color.purple);
	}
	
}