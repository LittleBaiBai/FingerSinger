package com.game.fingersinger;

import android.content.Context;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

public class LongPressButton extends ImageButton {
	private int direction; //按钮方向
    private long mStartTime; //记录长按开始
    private int mRepeatCount; //重复次数计数
    private RepeatListener mListener;
    private long mInterval = 500; //Timer触发间隔，即每0.5秒算一次按下
    private int fastSpeedCount = 10;
    private int slow_interval = 500;	//长按时模拟按键的间隔
    private int flip_interval = 1000;	//翻页间隔

    public LongPressButton(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.imageButtonStyle);
        Log.v("Long","Construct");
    }

    public LongPressButton(Context context, AttributeSet attrs, int defStyle) {    	
        super(context, attrs, defStyle);
        Log.v("Long","Construct");
        setFocusable(true); //允许获得焦点
        setLongClickable(true); //启用长按事件
    }
    
    public void setRepeatListener(RepeatListener l, int d) { //实现重复按下事件listener
        mListener = l;
        mInterval = slow_interval;
        direction = d;
    }
  
    @Override
    public boolean performLongClick() {
        mStartTime = SystemClock.elapsedRealtime();
        mRepeatCount = 0;
        post(mRepeater);		//开始runnable任务
        return true;
    }
  
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {  // 处理按钮事件
        	Log.v("Long", "Key Down");
        	mListener.fastAction(direction);
            removeCallbacks(mRepeater); 
            if (mStartTime != 0) {
                doRepeat(true);		//mRepeatCount = -1， 即重新开始
                mStartTime = 0;
            }
        }
        return super.onTouchEvent(event);
    }

  
    private Runnable mRepeater = new Runnable() {  //在线程中判断重复
        public void run() {
            doRepeat(false);		// mRepeatCount++
            if (isPressed()) {
            	if(mRepeatCount < fastSpeedCount){
            		mInterval = (slow_interval - slow_interval/(fastSpeedCount+2) * mRepeatCount); //间隔变短
            		mListener.fastAction(direction);
            		Log.v("Long","Quicker "+mInterval);
            	}
            	else if(mRepeatCount >= fastSpeedCount){
            		Log.v("Longflip","flip");
            		mInterval = flip_interval;
            		mListener.flipAction(direction);
            	}
                postDelayed(this, mInterval); //计算长按后延迟下一次累加
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
        void onRepeat(View v, long duration, int repeatcount); //参数一为用户传入的Button对象，参数二为延迟的毫秒数，第三位重复次数回调。
        void fastAction(int direction);
        void flipAction(int direction);
    }
}