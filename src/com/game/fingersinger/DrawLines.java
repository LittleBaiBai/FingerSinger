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

public class DrawLines extends View implements OnClickListener{
	private Bitmap mBitmap, tempBitmap;;
	private Canvas mCanvas;
	private Path mPath;
	private Paint mBitmapPaint;// 画布的画笔
	private Paint mPaint;// 真实的画笔
	private float mX, mY;// 临时点坐标
	private float startX; //每条曲线在X轴的起点
	private int tempoId; //曲线当前所在节拍
	private int note; //屏幕最后一个检测到的点的音高
	private float tempoX;
	private static final float TOUCH_TOLERANCE = 3;
	private boolean candraw = false;

	private int tempo_length = 40;

	// 保存Path路径的集合,用List集合来模拟栈
	private static List<DrawPath> savePath;
	// 记录Path路径的对象
	private DrawPath dp;

	private class DrawPath {
		public Path path;// 路径
		public Paint paint;// 画笔
	}
	public DrawLines(Context context,AttributeSet attrs) {
		super(context);
	}
	public DrawLines(Context context) {
		super(context);
		
		mBitmap = Bitmap.createBitmap(Declare.screen_width, Declare.screen_height,
				Bitmap.Config.ARGB_8888);
		if(mBitmap == null){
			Log.v("Error","mBitmap is null");
		}
		// 保存一次一次绘制出来的图形
		mCanvas = new Canvas(mBitmap);
		mBitmapPaint = new Paint(Paint.DITHER_FLAG);
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);// 设置外边缘
		mPaint.setStrokeCap(Paint.Cap.SQUARE);// 形状
		mPaint.setStrokeWidth(8);// 画笔宽度
		mPaint.setColor(0xFF000000);// 画笔颜色
		mPaint.setColor(Declare.color_status);// 画笔颜色
		savePath = new ArrayList<DrawPath>();
		setWillNotDraw(false);
		if(mBitmap!=null){
	//		Log.v("RIGHT","mBitmap not null");
		}
	}
	
	@Override
	
	public void onDraw(Canvas canvas) {
	//	Log.v("B","screemWidth = " + screenWidth);
	//	Log.v("B","screemHeight = " + screenHeight);
		
		canvas.drawColor(Color.TRANSPARENT);
		// 将前面已经画过得显示出来
		canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
		mPaint.setColor(Declare.colors[Declare.color_status]);// 画笔颜色
		Log.v("Color",""+Declare.colors[Declare.color_status]);
		
		if (mPath != null) {
			// 实时的显示
			canvas.drawPath(mPath, mPaint);
			
		}
		
	}
	
	private boolean inCanvas(float x, float y){
		if(x < (Declare.screen_width - Declare.button_menu_horizontal) || y < (Declare.screen_height - Declare.button_color_vertical)){
			return true; 
		}
		return false;
	}
	
	private void touch_start(float x, float y) {
		candraw = false;
		if (inCanvas(x, y)){
			candraw = true;
			Log.v("validTouch", "" + x + ", " + y);
			mPath.moveTo(x, y);
			mX = x;
			mY = y;
		}
	}

	private void touch_move(float x, float y) {
		if(candraw && inCanvas(x, y) && x > mX){
//			Log.v("Enter","x = "+x + ", y = " + y);
			if((int)x/tempo_length != tempoId && note!=0){//已进入下一节拍
				for( int i = Declare.melody[Declare.color_status].size(); i <= tempoId; i++){
					Declare.melody[Declare.color_status].add(0);	
				}
				Declare.melody[Declare.color_status].add(tempoId, note);// 添加了一个音
				Declare.drawSoundManager.playSound(Declare.getIndexOfSound(note) + Declare.color_status * 22);
				//Log.v("What did you store???", ""+Declare.melody[Declare.color_status].get(tempoId));
				//Log.v("Actual x and y", "x = " + tempoX + " y = " + note);
			}
			tempoId = (int)x/tempo_length;
			tempoX = x;
			note = (int)y;
			mPath.lineTo(x, y);
		//	mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
			mX = x;
			mY = y;
		/*	float dx = Math.abs(x - mX);
			float dy = Math.abs(mY - y);
			if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
				// 从x1,y1到x2,y2画一条贝塞尔曲线，更平滑(直接用mPath.lineTo也是可以的)
				mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
				mX = x;
				mY = y;
			}*/
		}
		
	}

	private void touch_up() {
		if(candraw){
			mPath.lineTo(mX, mY);
			mCanvas.drawPath(mPath, mPaint);
			// 将一条完整的路径保存下来(相当于入栈操作)
			savePath.add(dp);
			mPath = null;// 重新置空
			for(int i = 0; i < Declare.melody[Declare.color_status].size();i++){
				Log.v("KMelody", ""+(Integer)Declare.melody[Declare.color_status].get(i));
			}
			undo();
			reDraw();
		}
	}

	/**
	 * 撤销的核心思想就是将画布清空， 将保存下来的Path路径最后一个移除掉， 重新将路径画在画布上面。
	 */
	public void undo() {
		mBitmap = Bitmap.createBitmap(Declare.screen_width, Declare.screen_height, Bitmap.Config.ARGB_8888);
		mCanvas.setBitmap(mBitmap);// 重新设置画布，相当于清空画布
		// 清空画布，但是如果图片有背景的话，则使用上面的重新初始化的方法，用该方法会将背景清空掉...
		if (savePath != null && savePath.size() > 0) {
			// 移除最后一个path,相当于出栈操作
			savePath.remove(savePath.size() - 1);

			Iterator<DrawPath> iter = savePath.iterator();
			while (iter.hasNext()) {
				DrawPath drawPath = iter.next();
				mCanvas.drawPath(drawPath.path, drawPath.paint);
			}
			invalidate();// 刷新
			
			/* 在这里保存图片纯粹是为了方便,保存图片进行验证 */
			String fileUrl = Environment.getExternalStorageDirectory()
					.toString() + "/android/data/test.png";
			try {
				FileOutputStream fos = new FileOutputStream(new File(fileUrl));
				mBitmap.compress(CompressFormat.PNG, 100, fos);
				fos.flush();
				fos.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		tempBitmap = Bitmap.createBitmap(Declare.screen_width, Declare.screen_height,
				Bitmap.Config.ARGB_8888);
	}

	/**
	 * 重做的核心思想就是将撤销的路径保存到另外一个集合里面(栈)， 然后从redo的集合里面取出最顶端对象， 画在画布上面即可。
	 */
	public void redo() {
		// TODO
	}
	
	private void reDraw(){
		Path drawPath = new Path();
	//	mCanvas.setBitmap(mBitmap);
		int i;
		Log.v("Here", ""+Declare.melody[Declare.color_status].size());
		for(i = 0; i < Declare.melody[Declare.color_status].size(); i++){
			if((Integer)Declare.melody[Declare.color_status].get(i) != 0){
				Log.v("GET the start point","1");
				drawPath.moveTo(i*tempo_length, (Integer)Declare.melody[Declare.color_status].get(i));
				break;		
			}
		}
		i++;
		int note1, note2, lastnote = 0;
		for(; i < Declare.melody[Declare.color_status].size(); i++){
			note1 = (Integer)Declare.melody[Declare.color_status].get(i-1);
			note2 = (Integer)Declare.melody[Declare.color_status].get(i);
			if((Integer)Declare.melody[Declare.color_status].get(i) != 0){
				Log.v("Draw notes","1");
				lastnote = note2;
				mCanvas.drawRect(i*tempo_length-5, note2-5, i*tempo_length+5, note2+5, mPaint);
				drawPath.lineTo(i*tempo_length, note2);
			//	drawPath.quadTo((i-1)*tempo_length,note1,(float) ((i-0.5)*tempo_length), (note1+note2)/2);
			}
		}
		if(lastnote!=0){
			drawPath.lineTo(i*tempo_length, lastnote);
		}
		mCanvas.drawPath(drawPath, mPaint);
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

	@Override
	public void onClick(View v) {
		
		
	}

	

}

