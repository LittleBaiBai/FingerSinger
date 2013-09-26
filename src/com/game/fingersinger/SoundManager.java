package com.game.fingersinger;

import java.util.HashMap;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

public class SoundManager{
	private SoundPool mSoundPool;
	private HashMap mSoundPoolMap;
	private Context mContext;
	private AudioManager mAudioManager;
	
	public void initSounds(Context theContext, int permit){
		mContext = theContext;
		mSoundPool = new SoundPool(permit, AudioManager.STREAM_MUSIC, 0);
		mSoundPoolMap = new HashMap<Integer, Integer>(); 
		mAudioManager = (AudioManager) theContext.getSystemService(Context.AUDIO_SERVICE);

		Log.v("initSound", " R.raw.back_1:" + R.raw.back_1);
		this.addSound(1, R.raw.back_1); 
		this.addSound(2, R.raw.back_2);  
		this.addSound(3, R.raw.back_3);  
		this.addSound(4, R.raw.back_4);  
		this.addSound(5, R.raw.back_5);   
		this.addSound(6, R.raw.back_6);   
		this.addSound(7, R.raw.back_7);  
		this.addSound(8, R.raw.back_8);  
		this.addSound(9, R.raw.back_9);  
		this.addSound(10, R.raw.back_10); 
		this.addSound(11, R.raw.back_11);
		this.addSound(12, R.raw.back_12);   
		this.addSound(13, R.raw.back_13);   
		this.addSound(14, R.raw.back_14); 
		this.addSound(15, R.raw.back_15);  
		this.addSound(16, R.raw.back_16);    
		this.addSound(17, R.raw.back_17);  
		this.addSound(18, R.raw.back_18);  
		this.addSound(19, R.raw.back_19);  
		this.addSound(20, R.raw.back_20);  
		this.addSound(21, R.raw.back_21);  
		
	}
	
	public void addSound(int index, int SoundID)
	{
		Log.v("addSound", "index:" + index + " SoundID:" + SoundID);
		mSoundPoolMap.put(index, mSoundPool.load(mContext, SoundID, 1));
	}
	
	public void playSound(int index)
	{
		float streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		streamVolume = streamVolume / mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		mSoundPool.play((Integer)mSoundPoolMap.get(index), streamVolume, streamVolume, 1, 0, 1f);
	}
	
//	public void playLoopedSound(int index)
//	{
//		float streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
//		streamVolume = streamVolume/mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
//		mSoundPool.play((Integer)mSoundPoolMap.get(index),streamVolume,streamVolume,1,-1,1f);
//	}
	

}