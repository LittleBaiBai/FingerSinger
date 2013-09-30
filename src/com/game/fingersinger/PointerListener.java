package com.game.fingersinger;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

public class PointerListener extends View implements OnTouchListener {
	private float scroll_area;
	private float mx;
	private float my;
	private float lastX;
	private ImageView pointer;

	public PointerListener(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public PointerListener(Context context, ImageView view) {
		super(context);
		pointer = view;
		scroll_area = 50;
		lastX = Declare.button_menu_horizontal;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		mx = (float) event.getRawX();  
		my = (float) event.getRawY();  
		int event_action = event.getAction(); 
		Log.v("Pointer", "Action" + event_action + " x/y: " + mx + "/" + my);
		
		switch (event.getAction()) {  
		case MotionEvent.ACTION_DOWN:   //按下
			Log.v("Pointer", "Action_down" + event_action + " x/y: " + mx + "/" + my);
			if (my > Declare.screen_height - Declare.pointer_pressed && mx < lastX + Declare.pointer_unpress && mx > lastX){
				pointer.setImageDrawable(getResources().getDrawable(R.drawable.pointer_pressdown));
			}
			Log.v("pointer_x", "mx/onDown_lastx: " + mx + "/" + lastX);
			break;
        case MotionEvent.ACTION_MOVE:
    		Log.v("Pointer", "Action_move" + event_action + " x/y: " + mx + "/" + my);
    		picMove(mx, 0);  
            break; 
        case MotionEvent.ACTION_UP:
    		Log.v("Pointer", "Action_up" + event_action + " x/y: " + mx + "/" + my);
        	picMove(mx, 0);  
            pointer.setImageDrawable(getResources().getDrawable(R.drawable.pointer_unpress));
            break;     
        }    
		return true;
		
	}
	
	private void picMove(float x, float y) {  
        float positionX = x - Declare.pointer_pressed/2, positionY = y;  
         
//        button_stick.setDrawingCacheEnabled(true);
//        Bitmap bmp_stick = Bitmap.createBitmap(button_stick.getDrawingCache());
//        button_stick.setDrawingCacheEnabled(false);
//        
//        button_pointer.setDrawingCacheEnabled(true);
//        Bitmap bmp_pointer = Bitmap.createBitmap(button_pointer.getDrawingCache());
//        button_pointer.setDrawingCacheEnabled(false);
       
        if (positionX > ( Declare.screen_width - Declare.button_color_horizontal - Declare.pointer_pressed - scroll_area)) {
        	if (positionX > ( Declare.screen_width - Declare.button_color_horizontal - Declare.pointer_pressed)) {
        		positionX = Declare.screen_width - Declare.button_color_horizontal - Declare.pointer_pressed;  
        	}
        	//向右滚动画布
        	
        	
        }  
        else if (positionX <= Declare.button_menu_horizontal + scroll_area) {
        	if (positionX <= Declare.button_menu_horizontal) {
        		positionX = Declare.button_menu_horizontal;
        	}
        	//向左滚动画布
        	
        	
        }
        if (positionX == lastX) return;
        Log.v("pointer_x", "before: x/position/lastx: " + x + "/" + positionX + "/" + lastX);
        //pointer.setLayoutParams(new AbsoluteLayout.LayoutParams((int)Declare.pointer_pressed, Declare.screen_height, (int) positionX, (int) positionY)); 
        Animation  animation = new TranslateAnimation(lastX - Declare.button_menu_horizontal, positionX - Declare.button_menu_horizontal, 0, positionY);
        animation.setFillAfter(true);
        pointer.startAnimation(animation);
        lastX = positionX;
        Log.v("pointer_x", "after: x/position/lastx: " + x + "/" + positionX + "/" + lastX);
        Log.v("pointer_x", " ");
    }  
}
