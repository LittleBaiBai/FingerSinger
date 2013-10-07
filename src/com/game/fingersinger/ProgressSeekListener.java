package com.game.fingersinger;

import android.util.Log;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class ProgressSeekListener implements OnSeekBarChangeListener{
	private int mIndex;
	private int mProgress;
	private Melody mMelody;
	
	public ProgressSeekListener(SeekBar seekBar, int index, Melody melody) {
		mIndex = index;
		mProgress = (int) (melody.voice * 100);
		mMelody = melody;
	}
	
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		// TODO Auto-generated method stub
		Log.v("Progress", "onProgressChanged: " + progress);
		mProgress = progress;
		Declare.soundManager[mIndex].playSound(10, (float)mProgress / 100);
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		Log.v("Progress", "onStart: " + mProgress);
		Declare.soundManager[mIndex].playSound(10, (float)mProgress / 100);
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		Log.v("Progress", "onStop_before: " + mProgress);
		mMelody.voice = (float)mProgress / 100;
		Declare.soundManager[mIndex].playSound(10, mMelody.voice);
	}

}
