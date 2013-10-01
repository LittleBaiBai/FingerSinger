package com.game.fingersinger;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class TestView extends View {

	private GestureDetector gd;
	private int scrollingOffset;
	private ImageButton pointer;
	
	public TestView(Context context, AttributeSet attrs) {
		super(context, attrs);
		Log.v("InTestView","In constructor");
		pointer = (ImageButton) findViewById(R.id.pointer);
		gd = new GestureDetector(context, new InnerGestureListener());
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		canvas.translate(scrollingOffset, 0);
		super.onDraw(canvas);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Log.v("InTestView","In onTouch");
		// TODO Auto-generated method stub
		return gd.onTouchEvent(event);
	}
	
	class InnerGestureListener extends GestureDetector.SimpleOnGestureListener {

		@Override
		public boolean onDown(MotionEvent e) {
			return true;
		}
		
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			Log.v("InTestView","In onScroll");
			scrollingOffset += -distanceX;
			invalidate();
			return super.onScroll(e1, e2, distanceX, distanceY);
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			Log.v("InTestView","In onFling");
			return super.onFling(e1, e2, velocityX, velocityY);
		}
		
	}

}