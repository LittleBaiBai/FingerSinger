package com.game.fingersinger;

import android.content.Context;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

public class LongPressButton extends ImageButton {
	private int direction; //��ť����
    private long mStartTime; //��¼������ʼ
    private int mRepeatCount; //�ظ���������
    private RepeatListener mListener;
    private long mInterval = 500; //Timer�����������ÿ0.5����һ�ΰ���
    private int fastSpeedCount = 10;
    private int slow_interval = 500;	//����ʱģ�ⰴ���ļ��
    private int flip_interval = 1000;	//��ҳ���

    public LongPressButton(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.imageButtonStyle);
        Log.v("Long","Construct");
    }

    public LongPressButton(Context context, AttributeSet attrs, int defStyle) {    	
        super(context, attrs, defStyle);
        Log.v("Long","Construct");
        setFocusable(true); //�����ý���
        setLongClickable(true); //���ó����¼�
    }
    
    public void setRepeatListener(RepeatListener l, int d) { //ʵ���ظ������¼�listener
        mListener = l;
        mInterval = slow_interval;
        direction = d;
    }
  
    @Override
    public boolean performLongClick() {
        mStartTime = SystemClock.elapsedRealtime();
        mRepeatCount = 0;
        post(mRepeater);		//��ʼrunnable����
        return true;
    }
  
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {  // ����ť�¼�
        	Log.v("Long", "Key Down");
        	mListener.fastAction(direction);
            removeCallbacks(mRepeater); 
            if (mStartTime != 0) {
                doRepeat(true);		//mRepeatCount = -1�� �����¿�ʼ
                mStartTime = 0;
            }
        }
        return super.onTouchEvent(event);
    }

  
    private Runnable mRepeater = new Runnable() {  //���߳����ж��ظ�
        public void run() {
            doRepeat(false);		// mRepeatCount++
            if (isPressed()) {
            	if(mRepeatCount < fastSpeedCount){
            		mInterval = (slow_interval - slow_interval/(fastSpeedCount+2) * mRepeatCount); //������
            		mListener.fastAction(direction);
            		Log.v("Long","Quicker "+mInterval);
            	}
            	else if(mRepeatCount >= fastSpeedCount){
            		Log.v("Longflip","flip");
            		mInterval = flip_interval;
            		mListener.flipAction(direction);
            	}
                postDelayed(this, mInterval); //���㳤�����ӳ���һ���ۼ�
            }
        }
    };

    private  void doRepeat(boolean last) {
        long now = SystemClock.elapsedRealtime();
        if (mListener != null) {
            mListener.onRepeat(this, now - mStartTime, last ? -1 : mRepeatCount++);
        }
    }
    
    public interface RepeatListener {
        void onRepeat(View v, long duration, int repeatcount); //����һΪ�û������Button���󣬲�����Ϊ�ӳٵĺ�����������λ�ظ������ص���
        void fastAction(int direction);
        void flipAction(int direction);
    }
}