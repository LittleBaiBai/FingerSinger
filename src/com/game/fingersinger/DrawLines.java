/*Master branch*/
package com.game.fingersinger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;



public class DrawLines extends View{
	private Bitmap mBitmap;	//画布的bitmap
	private Paint mBitmapPaint;// 画布的画笔
	private Canvas mCanvas;	//画布
	private Path mPath;	//真实的时的路径
	private Paint mPaint;// 真实的画笔
	private float mX, mY;// 临时点坐标
	private int tempoId; //曲线当前点所在节拍
	private int note, note1; //曲线当前点的音高
	private int tempoId1;
	private static final float TOUCH_TOLERANCE = 3;
	private boolean candraw = false;
	private int tempo_length = 30;//节拍之间的最短距离

	// 保存Path路径的集合,用List集合来模拟栈
	private static List<DrawPath> savePath;
	// 记录Path路径的对象
	private DrawPath dp;	//用于画已保存的路径
	private class DrawPath {
		public Path path;// 路径
		public Paint paint;// 画笔
		public DrawPath(){
			path = new Path();
			paint = new Paint();
			paint.setAntiAlias(true);
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeJoin(Paint.Join.ROUND);// 设置外边缘
			paint.setStrokeCap(Paint.Cap.SQUARE);// 形状
			paint.setStrokeWidth(8);// 画笔宽度
		}
	}
	public DrawLines(Context context,AttributeSet attrs) {
		super(context);
	}
	public DrawLines(Context context) {
		super(context);
		Log.v("In Constructor","here");
		mBitmap = Bitmap.createBitmap(Declare.screen_width, Declare.screen_height,
				Bitmap.Config.ARGB_8888);
		// 保存一次一次绘制出来的图形
		mCanvas = new Canvas(mBitmap);
		mBitmapPaint = new Paint(Paint.DITHER_FLAG);
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);// 设置外边缘
		mPaint.setStrokeCap(Paint.Cap.SQUARE);// 形状
		mPaint.setStrokeWidth(8);// 画笔宽度
		mPaint.setColor(Declare.color_status);// 画笔颜色
		savePath = new ArrayList<DrawPath>();
		setWillNotDraw(false);
	}
	
	@Override
	
	public void onDraw(Canvas canvas) {
		
		canvas.drawColor(Color.TRANSPARENT);
		// 将前面已经画过得显示出来
		canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
		mPaint.setColor(Declare.colors[Declare.color_status]);// 选择画笔颜色
		
		if (mPath != null) {
			// 实时的显示
			canvas.drawPath(mPath, mPaint);
		}
	}
	
	private boolean inCanvas(float x, float y){
		if(Declare.menu_status == 1 && (x < (Declare.screen_width - Declare.button_menu_horizontal) || y < (Declare.screen_height - Declare.button_color_vertical))){
			return true; 
		}
		return false;
	}
	
	private void touch_start(float x, float y) {
		candraw = false;
		if (inCanvas(x, y)){
			candraw = true;
			add_last_edit();
			tempoId = (int)(x/tempo_length);
			note = (int)y;
			Log.v("tempoID", "" + tempoId);
			if(note!=0){//已按下
				for( int i = Declare.melody[Declare.color_status].notes.size(); i <= tempoId; i++){
					Declare.melody[Declare.color_status].notes.add(0);	
				}
				Declare.melody[Declare.color_status].notes.set(tempoId, note);
			}
			mPath.moveTo(tempoId*tempo_length, y);
			mX = x;
			mY = y;
		}
	}

	private void touch_move(float x, float y) {
		tempoId = (int)(x/tempo_length);
		note = (int)y;
		if(candraw && inCanvas(x, y) && x > mX){ // 起点在画布内，画线在画布内，没有往回画
			if(note!=0){
				for( int i = Declare.melody[Declare.color_status].notes.size(); i <= tempoId; i++){
					Declare.melody[Declare.color_status].notes.add(0);	
				}
					Declare.melody[Declare.color_status].notes.set(tempoId, note);//修改之前添加的音
			}
			mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
			mX = x;
			mY = y;
		
		}
		
	}

	private void touch_up() {
		if(candraw){
			mPath.lineTo(mX, mY);
			mCanvas.drawPath(mPath, mPaint);
			Declare.melody[Declare.color_status].stops.add(Declare.melody[Declare.color_status].notes.size()-1); // 设置该段的结束标志
			Log.v("stop size", "" +(Declare.melody[Declare.color_status].stops.size()-1));
			mPath = null;// 重新置空
			mBitmap = Bitmap.createBitmap(Declare.screen_width, Declare.screen_height, Bitmap.Config.ARGB_8888);
			mCanvas.setBitmap(mBitmap);// 重新设置画布，相当于清空画布
			reDraw();	//重新将所有的曲线都画出来
		}
	}
	
	private void add_last_edit(){
		Melody lastMelody = new Melody(Declare.melody[Declare.color_status]);
		Declare.LastEdit.add(lastMelody);
		Log.v("Last added", "" + Declare.LastEdit.size() + ", color = " + ((Melody)Declare.LastEdit.getLast()).color);
	}

	/**
	 * 撤销的核心思想就是将画布清空， 将保存下来的Path路径最后一个移除掉， 重新将路径画在画布上面。
	 */
	public void undo() {
		mBitmap = Bitmap.createBitmap(Declare.screen_width, Declare.screen_height, Bitmap.Config.ARGB_8888);
		mCanvas.setBitmap(mBitmap);// 重新设置画布，相当于清空画布
		Log.v("cannot redo", "" + Declare.LastEdit.size() + ", color = " + ((Melody)Declare.LastEdit.getLast()).color);
		if(Declare.LastEdit.size()>0){
			Declare.melody[((Melody)Declare.LastEdit.getLast()).color] = new Melody((Melody)Declare.LastEdit.getLast());
			
			reDraw();
			Declare.LastEdit.removeLast();
		}
	
		
	}

	/**
	 * 重做的核心思想就是将撤销的路径保存到另外一个集合里面(栈)， 然后从redo的集合里面取出最顶端对象， 画在画布上面即可。
	 */
	public void redo() {
		// TODO
	}
	
	private void reDraw(){
		int i,j;
		Log.v("Here", ""+Declare.color_status);
		for(i = 0; i < 5; i++){
			dp = new DrawPath();
			dp.paint.setColor(Declare.colors[i]);
			for(j = 0; j < Declare.melody[i].notes.size(); j++){
				if((Integer)Declare.melody[i].notes.get(j) != 0){
					Log.v("Draw GET the start point",""+j);
					note1 = (Integer)Declare.melody[i].notes.get(j);
					tempoId1 = j;
					dp.path.moveTo(j*tempo_length, (Integer)Declare.melody[i].notes.get(j));
					break;
				}
			}
			j++;
			int lastnote = 0;
			for(; j < Declare.melody[i].notes.size(); j++){
				note = (Integer)Declare.melody[i].notes.get(j);
				if(note != 0){
					Log.v("Draw notes","x = " + j*tempo_length + ", y = " + note);
					lastnote = note;
					mCanvas.drawRect(j*tempo_length-5, note-5, j*tempo_length+5, note+5, dp.paint);
			//		dp.path.lineTo(j*tempo_length, note);
					Log.v("Draw Link dots","x1 = " + (tempoId1)*tempo_length + ", x2 = " + ((j+tempoId1)/2.0*tempo_length));
					dp.path.quadTo((tempoId1)*tempo_length,note1,(float) ((j+tempoId1)/2.0*tempo_length), (note+note1)/2);
					note1 = note;
					tempoId1 = j;
				}
			}
			if(lastnote!=0){
				dp.path.lineTo(j*tempo_length, lastnote);
			}
			mCanvas.drawPath(dp.path, dp.paint);
		}
		invalidate();// 刷新
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// 每次down下去重新new一个Path
			mPath = new Path();
			// 每一次记录的路径对象是不一样的
			dp = new DrawPath();
			dp.path = mPath;
			dp.paint = mPaint;
			touch_start(x, y);
			invalidate();
			break;
		case MotionEvent.ACTION_MOVE:
			touch_move(x, y);
			invalidate();
			break;
		case MotionEvent.ACTION_UP:
			touch_up();
			invalidate();
			break;
		}
		return true;
	}
	
	public void drawPointerLine(float x){
		Paint pointLinePaint = new Paint();
//		mCanvas.drawLine(x-2, 0, x+2, Declare.screen_height, mPaint);
		invalidate();
	}


}
