package com.game.fingersinger;

import android.util.Log;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class ProgressSeekListener implements OnSeekBarChangeListener{
	private int mIndex;
	private int mProgress;
	
	public ProgressSeekListener(SeekBar seekBar, int index) {
		mIndex = index;
		mProgress = seekBar.getProgress();
	}
	
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// TODO Auto-generated method stub
		Log.v("Progress", "onProgressChanged: " + progress);
		mProgress = progress;
		Declare.soundManager[mIndex].playSound(10 + 22 * mIndex, (float)mProgress / 100);
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		Log.v("Progress", "onStart: " + mProgress);
		Declare.soundManager[mIndex].playSound(10 + 22 * mIndex, Declare.melody[mIndex].voice);
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		Log.v("Progress", "onStop_before: " + mProgress);
		Declare.melody[mIndex].voice = (float)mProgress / 100;
		Declare.soundManager[mIndex].playSound(10 + 22 * mIndex, Declare.melody[mIndex].voice);
		Log.v("Progress", "onStop_after: " + Declare.melody[mIndex].voice);
	}

}
