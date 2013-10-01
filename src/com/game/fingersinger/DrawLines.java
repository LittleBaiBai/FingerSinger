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
	private Bitmap mBitmap;	//������bitmap
	private Paint mBitmapPaint;// �����Ļ���
	private Canvas mCanvas;	//����
	private Path mPath;	//��ʵ��ʱ��·��
	private Paint mPaint;// ��ʵ�Ļ���
	private float mX, mY;// ��ʱ������
	private int tempoId; //���ߵ�ǰ�����ڽ���
	private int note, note1; //���ߵ�ǰ�������
	private int tempoId1;
	//private static final float TOUCH_TOLERANCE = 3;
	private boolean in_canvas = false;
	private int try_melody = 0; //���ڳ����û��������һ��������

	// ����Path·���ļ���,��List������ģ��ջ
	//private static List<DrawPath> savePath;
	// ��¼Path·���Ķ���
	private DrawPath dp;	//���ڻ��ѱ����·��
	private boolean can_draw;
	private class DrawPath {
		public Path path;// ·��
		public Paint paint;// ����
		public DrawPath(){
			path = new Path();
			paint = new Paint();
			paint.setAntiAlias(true);
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeJoin(Paint.Join.ROUND);// �������Ե
			paint.setStrokeCap(Paint.Cap.SQUARE);// ��״
			paint.setStrokeWidth(8);// ���ʿ��
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
		// ����һ��һ�λ��Ƴ�����ͼ��
		mCanvas = new Canvas(mBitmap);
		mBitmapPaint = new Paint(Paint.DITHER_FLAG);
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);// �������Ե
		mPaint.setStrokeCap(Paint.Cap.SQUARE);// ��״
		mPaint.setStrokeWidth(8);// ���ʿ��
		mPaint.setColor(Declare.color_status);// ������ɫ
		//savePath = new ArrayList<DrawPath>();
		setWillNotDraw(false);
	}
	
	@Override
	
	public void onDraw(Canvas canvas) {
		
		canvas.drawColor(Color.TRANSPARENT);
		// ��ǰ���Ѿ���������ʾ����
		canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
		mPaint.setColor(Declare.colors[Declare.color_status]);// ѡ�񻭱���ɫ
		
		if (mPath != null) {
			// ʵʱ����ʾ
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
		
		//menu_status == 1, ����״̬
		if (Declare.menu_status == 1 && in_canvas) {
			try_melody = 5;
			can_draw = false;
			
			for(int i = 0; i < 5; i++ ){
				if(tempoId >= 0 && tempoId <Declare.melody[i].notes.size() &&
						noteId <= Declare.getIndexOfSound((Integer)Declare.melody[i].notes.get(tempoId))+1 &&
						noteId >= Declare.getIndexOfSound((Integer)Declare.melody[i].notes.get(tempoId))-1 &&
						Declare.color_status == Declare.melody[i].color){
					add_last_edit();     //����֮ǰ��״̬
					try_melody = i;
					break;
				}	
			}
			if(try_melody == Declare.color_status){ // ����һ����׼���ϣ��Ҹ���׼����뵱ǰ��ɫ��ͬ������
				can_draw = false;	//����Ϊ�������»���״̬
			}
			else{	//û��������׼���ϣ�����
				// �����»�һ��
				if(Declare.melody[Declare.color_status].stops.size() == 0 || tempoId >= (Declare.melody[Declare.color_status].stops.get(Declare.melody[Declare.color_status].stops.size()-1))){
					can_draw = true;
					add_last_edit();     //����֮ǰ��״̬
					if(Declare.melody[Declare.color_status].stops.size()!=0)
						Log.v("Stops store", ""+(Declare.melody[Declare.color_status].stops.get(Declare.melody[Declare.color_status].stops.size()-1)));
					if (note != 0){//�Ѱ���
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
		//��Ƥ��״̬
		else if(Declare.menu_status == 2){  
			for(int i = 0; i < 5; i++ ){
				Log.v("Melody Size"	, ""+Declare.melody.length);
				// ����Ƿ�㵽��ĳ���ߵ�ĳ����׼����
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
		//Declare == 1, ����״̬ 
		if(Declare.menu_status == 1 ){
			//���ڻ���״̬
			if (can_draw && inCanvas(x, y) && x > mX){ // ����ڻ������ҿɻ��������ڻ����ڣ�û�����ػ�
				tempoId = (int)(x / Declare.tempo_length);
				if (note != 0){
					for ( int i = Declare.melody[Declare.color_status].notes.size(); i <= tempoId; i++){
						Declare.melody[Declare.color_status].notes.add(0);	
					}
					Log.v("touch_move", "note: " + note);
					Declare.melody[Declare.color_status].notes.set(tempoId, note);//�޸�֮ǰ��ӵ���
					Declare.soundManager[Declare.color_status].playSound(Declare.getIndexOfSound(note) + Declare.color_status * 22, Declare.melody[Declare.color_status].voice);
				
				}
				mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
				mX = x;
				mY = y;
			}
			//����״̬
			else if(!can_draw){
				if(try_melody != 5){
					Declare.melody[try_melody].notes.set(tempoId, note);//�޸�֮ǰ��ӵ���
					reDraw();
				}
			}
		}		
		
	}

	private void touch_up() {
		if(Declare.menu_status == 1 && in_canvas){
			if (can_draw) {	//����״̬����
				mPath.lineTo(mX, mY);
				mCanvas.drawPath(mPath, mPaint);
				Declare.melody[Declare.color_status].stops.add(Declare.melody[Declare.color_status].notes.size()-1); // ���øöεĽ�����־

				Log.v("Stops add", ""+(Declare.melody[Declare.color_status].stops.size()));
				mPath = null;// �����ÿ�
				
				reDraw();	//���½����е����߶�������
				can_draw = false;
			}
			else {//����״̬����
				
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
		mCanvas.setBitmap(mBitmap);// �������û������൱����ջ���
	}
	
	/**
	 * �����ĺ���˼����ǽ�������գ� ������������Path·�����һ���Ƴ����� ���½�·�����ڻ������档
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
			while(start_it.hasNext()){	//����һ����ɫ������start
				int start_at = (Integer)start_it.next();
				int end_at = (Integer)stop_it.next(); //���һ�����ɵĽ���tempoId
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
		invalidate();// ˢ��
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// ÿ��down��ȥ����newһ��Path
			mPath = new Path();
			// ÿһ�μ�¼��·�������ǲ�һ����
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