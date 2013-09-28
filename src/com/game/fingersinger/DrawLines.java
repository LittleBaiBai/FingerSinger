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
	private Paint mBitmapPaint;// �����Ļ���
	private Paint mPaint;// ��ʵ�Ļ���
	private float mX, mY;// ��ʱ������
	private float startX; //ÿ��������X������
	private int tempoId; //���ߵ�ǰ���ڽ���
	private int note; //��Ļ���һ����⵽�ĵ������
	private float tempoX;
	private static final float TOUCH_TOLERANCE = 3;
	private boolean candraw = false;

	private int tempo_length = 40;

	// ����Path·���ļ���,��List������ģ��ջ
	private static List<DrawPath> savePath;
	// ��¼Path·���Ķ���
	private DrawPath dp;

	private class DrawPath {
		public Path path;// ·��
		public Paint paint;// ����
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
		// ����һ��һ�λ��Ƴ�����ͼ��
		mCanvas = new Canvas(mBitmap);
		mBitmapPaint = new Paint(Paint.DITHER_FLAG);
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);// �������Ե
		mPaint.setStrokeCap(Paint.Cap.SQUARE);// ��״
		mPaint.setStrokeWidth(8);// ���ʿ��
		mPaint.setColor(0xFF000000);// ������ɫ
		mPaint.setColor(Declare.color_status);// ������ɫ
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
		// ��ǰ���Ѿ���������ʾ����
		canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
		mPaint.setColor(Declare.colors[Declare.color_status]);// ������ɫ
		Log.v("Color",""+Declare.colors[Declare.color_status]);
		
		if (mPath != null) {
			// ʵʱ����ʾ
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
			if((int)x/tempo_length != tempoId && note!=0){//�ѽ�����һ����
				for( int i = Declare.melody[Declare.color_status].size(); i <= tempoId; i++){
					Declare.melody[Declare.color_status].add(0);	
				}
				Declare.melody[Declare.color_status].add(tempoId, note);// �����һ����
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
				// ��x1,y1��x2,y2��һ�����������ߣ���ƽ��(ֱ����mPath.lineToҲ�ǿ��Ե�)
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
			// ��һ��������·����������(�൱����ջ����)
			savePath.add(dp);
			mPath = null;// �����ÿ�
			for(int i = 0; i < Declare.melody[Declare.color_status].size();i++){
				Log.v("KMelody", ""+(Integer)Declare.melody[Declare.color_status].get(i));
			}
			undo();
			reDraw();
		}
	}

	/**
	 * �����ĺ���˼����ǽ�������գ� ������������Path·�����һ���Ƴ����� ���½�·�����ڻ������档
	 */
	public void undo() {
		mBitmap = Bitmap.createBitmap(Declare.screen_width, Declare.screen_height, Bitmap.Config.ARGB_8888);
		mCanvas.setBitmap(mBitmap);// �������û������൱����ջ���
		// ��ջ������������ͼƬ�б����Ļ�����ʹ����������³�ʼ���ķ������ø÷����Ὣ������յ�...
		if (savePath != null && savePath.size() > 0) {
			// �Ƴ����һ��path,�൱�ڳ�ջ����
			savePath.remove(savePath.size() - 1);

			Iterator<DrawPath> iter = savePath.iterator();
			while (iter.hasNext()) {
				DrawPath drawPath = iter.next();
				mCanvas.drawPath(drawPath.path, drawPath.paint);
			}
			invalidate();// ˢ��
			
			/* �����ﱣ��ͼƬ������Ϊ�˷���,����ͼƬ������֤ */
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
	 * �����ĺ���˼����ǽ�������·�����浽����һ����������(ջ)�� Ȼ���redo�ļ�������ȡ����˶��� ���ڻ������漴�ɡ�
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

	@Override
	public void onClick(View v) {
		
		
	}

	

}

