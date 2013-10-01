/*Master branch*/
package com.game.fingersinger;


import java.util.Iterator;

import android.util.AttributeSet;
import android.util.Log;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.view.View;

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
	//private static final float TOUCH_TOLERANCE = 3;
	private boolean in_canvas = false;
	private int try_melody = 0; //用于尝试用户点击在哪一条旋律上

	// 保存Path路径的集合,用List集合来模拟栈
	//private static List<DrawPath> savePath;
	// 记录Path路径的对象
	private DrawPath dp;	//用于画已保存的路径
	private boolean can_draw;
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
		Log.v("New Start", "------------------------------------------------------------------");
		Log.v("Screen", "Width = " + Declare.screen_width+"," + Declare.screen_height);
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
		//savePath = new ArrayList<DrawPath>();
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
	
	private boolean inCanvas(float x, float y) {
		if (x < (Declare.screen_width - Declare.button_menu_horizontal)
				|| y < (Declare.screen_height - Declare.button_color_vertical)) {
			return true; 
		}
		return false;
	}
	
	private void touch_start(float x, float y) {
		Log.v("Menu status", ""+Declare.menu_status);
		in_canvas = inCanvas(x,y);
		tempoId = (int)(x / Declare.tempo_length);
		note = (int)y;
		int noteId = Declare.getIndexOfSound(note);
		
		//menu_status == 1, 画笔状态
		if (Declare.menu_status == 1 && in_canvas) {
			try_melody = 5;
			can_draw = false;
			
			for(int i = 0; i < 5; i++ ){
				if(tempoId >= 0 && tempoId <Declare.melody[i].notes.size() &&
						noteId <= Declare.getIndexOfSound((Integer)Declare.melody[i].notes.get(tempoId))+1 &&
						noteId >= Declare.getIndexOfSound((Integer)Declare.melody[i].notes.get(tempoId))-1 &&
						Declare.color_status == Declare.melody[i].color){
					add_last_edit();     //保存之前的状态
					try_melody = i;
					break;
				}	
			}
			if(try_melody == Declare.color_status){ // 按在一个音准点上，且该音准点的与当前颜色相同，调音
				can_draw = false;	//设置为不可以新画的状态
			}
			else{	//没有落在音准点上，画线
				// 可以新画一笔
				if(Declare.melody[Declare.color_status].stops.size() == 0 || tempoId >= (Declare.melody[Declare.color_status].stops.get(Declare.melody[Declare.color_status].stops.size()-1))){
					can_draw = true;
					add_last_edit();     //保存之前的状态
					if(Declare.melody[Declare.color_status].stops.size()!=0)
						Log.v("Stops store", ""+(Declare.melody[Declare.color_status].stops.get(Declare.melody[Declare.color_status].stops.size()-1)));
					if (note != 0){//已按下
						for ( int i = Declare.melody[Declare.color_status].notes.size(); i <= tempoId; i++) {
							Declare.melody[Declare.color_status].notes.add(0);	
						}
						Declare.melody[Declare.color_status].notes.set(tempoId, note);
						Declare.melody[Declare.color_status].starts.add(tempoId);
				//		Declare.drawSoundManager.playSound(noteId + Declare.color_status * 22, Declare.melody[Declare.color_status].voice);
						Declare.isSaved = false;
					}
					mPath.moveTo(tempoId * Declare.tempo_length, y);
					mX = x;
					mY = y;
				}
			}
		}
		//橡皮擦状态
		else if(Declare.menu_status == 2){  
			for(int i = 0; i < 5; i++ ){
				Log.v("Melody Size"	, ""+Declare.melody.length);
				// 检测是否点到了某条线的某个音准点上
				if(tempoId >= 0 && tempoId <Declare.melody[i].notes.size() && 
						noteId == Declare.getIndexOfSound((Integer)Declare.melody[i].notes.get(tempoId))){
					Log.v("Melody", "Melody = " +i);
					Log.v("NoteID, tempoId", ""+noteId + "," + tempoId);
					Log.v("NoteID", ""+Declare.getIndexOfSound((Integer)Declare.melody[i].notes.get(tempoId)));
					for(int j = 0; j < Declare.melody[i].starts.size(); j++){
						Log.v("Start at","start = "+Declare.melody[i].starts.get(j)+", stop = " + Declare.melody[i].stops.get(j));
						if(noteId > Declare.melody[i].starts.get(j)){
							for(int k = Declare.melody[i].starts.get(j); k < Declare.melody[i].stops.get(j); k++){
								Declare.melody[i].notes.set(k, 0);
							}
						}
					}
					
					reDraw();
					add_last_edit();
					Declare.isSaved = false;
					break;
				}
			}
			
		}
		Log.v("What is up at start", ""+Declare.melody[Declare.color_status].starts.size()+","+Declare.melody[Declare.color_status].stops.size());
	}

	private void touch_move(float x, float y) {	
		note = (int)y;
		//Declare == 1, 画笔状态 
		if(Declare.menu_status == 1 ){
			//正在画的状态
			if (can_draw && inCanvas(x, y) && x > mX){ // 起点在画布内且可画，画线在画布内，没有往回画
				tempoId = (int)(x / Declare.tempo_length);
				if (note != 0){
					for ( int i = Declare.melody[Declare.color_status].notes.size(); i <= tempoId; i++){
						Declare.melody[Declare.color_status].notes.add(0);	
					}
					Log.v("touch_move", "note: " + note);
					Declare.melody[Declare.color_status].notes.set(tempoId, note);//修改之前添加的音
					Declare.soundManager[Declare.color_status].playSound(Declare.getIndexOfSound(note) + Declare.color_status * 22, Declare.melody[Declare.color_status].voice);
				
				}
				mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
				mX = x;
				mY = y;
			}
			//调音状态
			else if(!can_draw){
				if(try_melody != 5){
					Declare.melody[try_melody].notes.set(tempoId, note);//修改之前添加的音
					reDraw();
				}
			}
		}		
		
	}

	private void touch_up() {
		if(Declare.menu_status == 1 && in_canvas){
			if (can_draw) {	//画画状态结束
				mPath.lineTo(mX, mY);
				mCanvas.drawPath(mPath, mPaint);
				Declare.melody[Declare.color_status].stops.add(Declare.melody[Declare.color_status].notes.size()-1); // 设置该段的结束标志

				Log.v("Stops add", ""+(Declare.melody[Declare.color_status].stops.size()));
				mPath = null;// 重新置空
				
				reDraw();	//重新将所有的曲线都画出来
				can_draw = false;
			}
			else {//调音状态结束
				
			}
			Log.v("What is up at end", ""+Declare.melody[Declare.color_status].starts.size()+","+Declare.melody[Declare.color_status].stops.size());
			
		}
	}
	
	private void add_last_edit() {
		Melody lastMelody = new Melody(Declare.melody[Declare.color_status]);
		Declare.LastEdit.add(lastMelody);
		Log.v("Last added", "" + Declare.LastEdit.size() + ", color = " + ((Melody)Declare.LastEdit.getLast()).color);
	}

	public void clearCanvas() {
		mBitmap = Bitmap.createBitmap(Declare.screen_width, Declare.screen_height, Bitmap.Config.ARGB_8888);
		mCanvas.setBitmap(mBitmap);// 重新设置画布，相当于清空画布
	}
	
	/**
	 * 撤销的核心思想就是将画布清空， 将保存下来的Path路径最后一个移除掉， 重新将路径画在画布上面。
	 */
	public void undo() {
		clearCanvas();
		//Log.v("cannot redo", "" + Declare.LastEdit.size() + ", color = " + ((Melody)Declare.LastEdit.getLast()).color);
		if (Declare.LastEdit.size()>0) {
			Declare.melody[((Melody)Declare.LastEdit.getLast()).color] = new Melody((Melody)Declare.LastEdit.getLast());
			
			reDraw();
			Declare.LastEdit.removeLast();
		}
	
		
	}

	public void reDraw() {
		int i,j,m;
		clearCanvas();
//		Log.v("Here", "" + Declare.color_status);
		while(Declare.melody[Declare.color_status].starts.size() > Declare.melody[Declare.color_status].stops.size()){
			Declare.melody[Declare.color_status].starts.remove(Declare.melody[Declare.color_status].starts.size()-1);
			Log.v("What is up at reDraw --adjust starts", ""+Declare.melody[Declare.color_status].starts.size()+","+Declare.melody[Declare.color_status].stops.size());		
		}
		while(Declare.melody[Declare.color_status].starts.size() < Declare.melody[Declare.color_status].stops.size()){
			Declare.melody[Declare.color_status].stops.remove(Declare.melody[Declare.color_status].stops.size()-1);
			Log.v("What is up at reDraw --adjust stops", ""+Declare.melody[Declare.color_status].starts.size()+","+Declare.melody[Declare.color_status].stops.size());		
		}
//		if(Declare.melody[Declare.color_status].starts.get(Declare.melody[Declare.color_status].starts.size()-1) == 
//				Declare.melody[Declare.color_status].starts.get(Declare.melody[Declare.color_status].starts.size()-1)){
//			Declare.melody[Declare.color_status].starts.remove(Declare.melody[Declare.color_status].starts.size()-1);
//			Declare.melody[Declare.color_status].stops.remove(Declare.melody[Declare.color_status].stops.size()-1);
//			
//		}
		for (i = 0; i < 5; i++) {
			dp = new DrawPath();
			dp.paint.setColor(Declare.colors[i]);
			if(Declare.melody[i].starts.size()!=Declare.melody[i].stops.size()){
				Log.v("What is up", ""+Declare.melody[i].starts.size()+","+Declare.melody[i].stops.size());
			}
			if( Declare.melody[i].starts.size() == 0 )
				continue;
			Iterator<Integer> start_it  = Declare.melody[i].starts.iterator();
			Iterator<Integer> stop_it = Declare.melody[i].stops.iterator(); 
			while(start_it.hasNext()){	//遍历一种颜色的所有start
				int start_at = (Integer)start_it.next();
				int end_at = (Integer)stop_it.next(); //获得一段旋律的结束tempoId
				Log.v("Color status" + Declare.color_status, "" + start_at+" , "+(end_at- start_at));
				for (m = start_at; m < end_at; m++) {
					if (Declare.melody[i].notes.get(m) != 0) {
						Log.v("Draw GET the start point", "" + m);
						note1 = Declare.melody[i].notes.get(m);
						tempoId1 = m;
						dp.path.moveTo(m * Declare.tempo_length, Declare.melody[i].notes.get(m));
						mCanvas.drawRect(m * Declare.tempo_length - 3, Declare.melody[i].notes.get(m) - 3, m * Declare.tempo_length + 3, Declare.melody[i].notes.get(m) + 3, dp.paint);
						break;
					}
				}
				m++;
				int lastnote = 0;
				for (; m < end_at; m++) {
					note = Declare.melody[i].notes.get(m);
					if (note != 0) {
						Log.v("Draw notes","x = " + m * Declare.tempo_length + ", y = " + note);
						lastnote = note;
						mCanvas.drawRect(m * Declare.tempo_length - 3, note - 3, m * Declare.tempo_length + 3, note + 3, dp.paint);
				//		dp.path.lineTo(j*tempo_length, note);
						Log.v("Draw Link dots","x1 = " + (tempoId1) * Declare.tempo_length + ", x2 = " + ((m + tempoId1) / 2.0 * Declare.tempo_length));
						dp.path.quadTo((tempoId1) * Declare.tempo_length, note1, (float) ((m + tempoId1) / 2.0 * Declare.tempo_length), (note + note1) / 2);
						note1 = note;
						tempoId1 = m;
					}
				}
				if (lastnote != 0) {
					dp.path.lineTo(m * Declare.tempo_length, lastnote);
					mCanvas.drawRect(m * Declare.tempo_length - 3, lastnote - 3, m * Declare.tempo_length + 3, lastnote + 3, dp.paint);
					
				}
				mCanvas.drawPath(dp.path, dp.paint);
			}
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
	
	public void drawPointerLine(float x) {
		Paint pointLinePaint = new Paint();
//		mCanvas.drawLine(x-2, 0, x+2, Declare.screen_height, mPaint);
		invalidate();
	}


}