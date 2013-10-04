package com.game.fingersinger;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class ScaleView extends View{
	//private Canvas mCanvas;
	private Paint mPaint;
	
	public ScaleView(Context context, AttributeSet attrs) {
		super(context);
	}
	
	public ScaleView(Context context) {
		super(context);
		
		//mCanvas = new Canvas();
		mPaint = new Paint();
		mPaint.setColor(Color.LTGRAY);// »­±ÊÑÕÉ«
		
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		
		float x = Declare.tempo_length;
		while (x < Declare.screen_width - Declare.button_color_horizontal) {
			//canvas.drawLine(x, Declare.scale_start_x_y, x, Declare.scale_start_x_y + Declare.scale_length_vertical, Declare.scale_start_x_y + Declare.scale_length_vertical);
			canvas.drawRect(x, Declare.scale_start_x_y, x + 3, Declare.scale_start_x_y + Declare.scale_length_vertical, mPaint);
			x += Declare.tempo_length;
		}
		
	}
	
//	protected void reDraw(Canvas canvas, int start) {
//
//		float x = Declare.tempo_length + start;
//		while (x < Declare.screen_width - Declare.button_color_horizontal) {
//			//canvas.drawLine(x, Declare.scale_start_x_y, x, Declare.scale_start_x_y + Declare.scale_length_vertical, Declare.scale_start_x_y + Declare.scale_length_vertical);
//			canvas.drawRect(x, Declare.scale_start_x_y, x + 3, Declare.scale_start_x_y + Declare.scale_length_vertical, mPaint);
//			x += Declare.tempo_length;
//		}
//	}
	
}
