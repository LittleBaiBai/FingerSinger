/*Master branch*/
package com.game.fingersinger;

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
	private boolean candraw = false;

	// ����Path·���ļ���,��List������ģ��ջ
	//private static List<DrawPath> savePath;
	// ��¼Path·���Ķ���
	private DrawPath dp;	//���ڻ��ѱ����·��
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
		if (Declare.menu_status == 1 && 
				(x < (Declare.screen_width - Declare.button_menu_horizontal) 
				|| y < (Declare.screen_height - Declare.button_color_vertical))) {
			return true; 
		}
		return false;
	}
	
	private void touch_start(float x, float y) {
		candraw = false;
		if (inCanvas(x, y)) {
			candraw = true;
			add_last_edit();
			tempoId = (int)(x / Declare.tempo_length);
			note = (int)y;
			Log.v("tempoID", "" + tempoId);
			if (note != 0){//�Ѱ���
				for ( int i = Declare.melody[Declare.color_status].notes.size(); i <= tempoId; i++) {
					Declare.melody[Declare.color_status].notes.add(0);	
				}
				Declare.melody[Declare.color_status].notes.set(tempoId, note);
				Declare.drawSoundManager.playSound(Declare.getIndexOfSound(note) + Declare.color_status * 22, Declare.melody[Declare.color_status].voice);
				Declare.isSaved = false;
			}
			mPath.moveTo(tempoId * Declare.tempo_length, y);
			mX = x;
			mY = y;
		}
		else if(Declare.menu_status == 2){  //��Ƥ��״̬
			for(int i = 0; i < 5; i++ ){
				int noteId = Declare.getIndexOfSound(note);
				
				Log.v("Melody Size"	, ""+Declare.melody.length);
			//	
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
					clearCanvas();
					reDraw();
					add_last_edit();
					Declare.isSaved = false;
					break;
				}
			}
			
		}

	}

	private void touch_move(float x, float y) {
		tempoId = (int)(x / Declare.tempo_length);
		note = (int)y;
		if (candraw && inCanvas(x, y) && x > mX){ // ����ڻ����ڣ������ڻ����ڣ�û�����ػ�
			if (note != 0){
				for ( int i = Declare.melody[Declare.color_status].notes.size(); i <= tempoId; i++){
					Declare.melody[Declare.color_status].notes.add(0);	
				}
				Log.v("touch_move", "note: " + note);
				Declare.melody[Declare.color_status].notes.set(tempoId, note);//�޸�֮ǰ��ӵ���
				Declare.drawSoundManager.playSound(Declare.getIndexOfSound(note) + Declare.color_status * 22, Declare.melody[Declare.color_status].voice);
			}
			mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
			mX = x;
			mY = y;
		
		}
		
	}

	private void touch_up() {
		if (candraw) {
			mPath.lineTo(mX, mY);
			mCanvas.drawPath(mPath, mPaint);
			Declare.melody[Declare.color_status].stops.add(Declare.melody[Declare.color_status].notes.size()); // ������һ�εĿ�ʼ��־
			mPath = null;// �����ÿ�
			clearCanvas();
			reDraw();	//���½����е����߶�������
		}
	}
	
	private void add_last_edit() {
		Melody lastMelody = new Melody(Declare.melody[Declare.color_status]);
		Declare.LastEdit.add(lastMelody);
		Log.v("Last added", "" + Declare.LastEdit.size() + ", color = " + ((Melody)Declare.LastEdit.getLast()).color);
	}

	public void clearCanvas() {
		mBitmap = Bitmap.createBitmap(Declare.screen_width, Declare.screen_height,
				Bitmap.Config.ARGB_8888);
		// ����һ��һ�λ��Ƴ�����ͼ��
		mCanvas.setBitmap(mBitmap);// �������û������൱����ջ���
	}
	
	/**
	 * �����ĺ���˼����ǽ�������գ� ������������Path·�����һ���Ƴ����� ���½�·�����ڻ������档
	 */
	public void undo() {
		clearCanvas();
		Log.v("cannot redo", "" + Declare.LastEdit.size() + ", color = " + ((Melody)Declare.LastEdit.getLast()).color);
		if (Declare.LastEdit.size()>0) {
			Declare.melody[((Melody)Declare.LastEdit.getLast()).color] = new Melody((Melody)Declare.LastEdit.getLast());
			
			reDraw();
			Declare.LastEdit.removeLast();
		}
	
		
	}

	/**
	 * �����ĺ���˼����ǽ�������·�����浽����һ����������(ջ)�� Ȼ���redo�ļ�������ȡ����˶��� ���ڻ������漴�ɡ�
	 */
	public void redo() {
		// TODO
	}
	
	public void reDraw() {
		int i,j;
		Log.v("Here", "" + Declare.color_status);
		for (i = 0; i < 5; i++) {
			dp = new DrawPath();
			dp.paint.setColor(Declare.colors[i]);
			for (j = 0; j < Declare.melody[i].notes.size(); j++) {
				if (Declare.melody[i].notes.get(j) != 0) {
					Log.v("Draw GET the start point", "" + j);
					note1 = Declare.melody[i].notes.get(j);
					tempoId1 = j;
					dp.path.moveTo(j * Declare.tempo_length, Declare.melody[i].notes.get(j));
					break;
				}
			}
			j++;
			int lastnote = 0;
			for (; j < Declare.melody[i].notes.size(); j++) {
				note = Declare.melody[i].notes.get(j);
				if (note != 0) {
					Log.v("Draw notes","x = " + j * Declare.tempo_length + ", y = " + note);
					lastnote = note;
					mCanvas.drawRect(j * Declare.tempo_length - 5, note - 5, j * Declare.tempo_length + 5, note + 5, dp.paint);
			//		dp.path.lineTo(j*tempo_length, note);
					Log.v("Draw Link dots","x1 = " + (tempoId1) * Declare.tempo_length + ", x2 = " + ((j + tempoId1) / 2.0 * Declare.tempo_length));
					dp.path.quadTo((tempoId1) * Declare.tempo_length, note1, (float) ((j + tempoId1) / 2.0 * Declare.tempo_length), (note + note1) / 2);
					note1 = note;
					tempoId1 = j;
				}
			}
			if (lastnote != 0) {
				dp.path.lineTo(j * Declare.tempo_length, lastnote);
			}
			mCanvas.drawPath(dp.path, dp.paint);
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
