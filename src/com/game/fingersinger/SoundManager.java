package com.game.fingersinger;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;
import android.util.SparseIntArray;

public class SoundManager{
	private SoundPool mSoundPool;
	private SparseIntArray mSoundPoolMap;
	private Context mContext;
	private AudioManager mAudioManager;
	
	public void initSounds(Context theContext, int permit){
		mContext = theContext;
		mSoundPool = new SoundPool(permit, AudioManager.STREAM_MUSIC, 0);
		mSoundPoolMap = new SparseIntArray(); 
		mAudioManager = (AudioManager) theContext.getSystemService(Context.AUDIO_SERVICE);

		this.addSound(0, R.raw.voice_null);
		this.addSound(1, R.raw.back_01); 
		this.addSound(2, R.raw.back_02);  
		this.addSound(3, R.raw.back_03);  
		this.addSound(4, R.raw.back_04);  
		this.addSound(5, R.raw.back_05);   
		this.addSound(6, R.raw.back_06);   
		this.addSound(7, R.raw.back_07);  
		this.addSound(8, R.raw.back_08);  
		this.addSound(9, R.raw.back_09);  
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
		this.addSound(22, R.raw.back_22); 
		
		this.addSound(23, R.raw.mengmei_01); 
		this.addSound(24, R.raw.mengmei_02);  
		this.addSound(25, R.raw.mengmei_03);  
		this.addSound(26, R.raw.mengmei_04);  
		this.addSound(27, R.raw.mengmei_05);   
		this.addSound(28, R.raw.mengmei_06);   
		this.addSound(29, R.raw.mengmei_07);  
		this.addSound(30, R.raw.mengmei_08);  
		this.addSound(31, R.raw.mengmei_09);  
		this.addSound(32, R.raw.mengmei_10); 
		this.addSound(33, R.raw.mengmei_11);
		this.addSound(34, R.raw.mengmei_12);   
		this.addSound(35, R.raw.mengmei_13);   
		this.addSound(36, R.raw.mengmei_14); 
		this.addSound(37, R.raw.mengmei_15);  
		this.addSound(38, R.raw.mengmei_16);    
		this.addSound(39, R.raw.mengmei_17);  
		this.addSound(40, R.raw.mengmei_18);  
		this.addSound(41, R.raw.mengmei_19);  
		this.addSound(42, R.raw.mengmei_20);  
		this.addSound(43, R.raw.mengmei_21);  
		this.addSound(44, R.raw.mengmei_22);  
		
		this.addSound(45, R.raw.yujie_01); 
		this.addSound(46, R.raw.yujie_02);  
		this.addSound(47, R.raw.yujie_03);  
		this.addSound(48, R.raw.yujie_04);  
		this.addSound(49, R.raw.yujie_05);   
		this.addSound(50, R.raw.yujie_06);   
		this.addSound(51, R.raw.yujie_07);  
		this.addSound(52, R.raw.yujie_08);  
		this.addSound(53, R.raw.yujie_09);  
		this.addSound(54, R.raw.yujie_10); 
		this.addSound(55, R.raw.yujie_11);
		this.addSound(56, R.raw.yujie_12);   
		this.addSound(57, R.raw.yujie_13);   
		this.addSound(58, R.raw.yujie_14); 
		this.addSound(59, R.raw.yujie_15);  
		this.addSound(60, R.raw.yujie_16);    
		this.addSound(61, R.raw.yujie_17);  
		this.addSound(62, R.raw.yujie_18);  
		this.addSound(63, R.raw.yujie_19);  
		this.addSound(64, R.raw.yujie_20);  
		this.addSound(65, R.raw.yujie_21);  
		this.addSound(66, R.raw.yujie_22); 		
	
		this.addSound(67, R.raw.niangshou_01); 
		this.addSound(68, R.raw.niangshou_02);  
		this.addSound(69, R.raw.niangshou_03);  
		this.addSound(70, R.raw.niangshou_04);  
		this.addSound(71, R.raw.niangshou_05);   
		this.addSound(72, R.raw.niangshou_06);   
		this.addSound(73, R.raw.niangshou_07);  
		this.addSound(74, R.raw.niangshou_08);  
		this.addSound(75, R.raw.niangshou_09);  
		this.addSound(76, R.raw.niangshou_10); 
		this.addSound(77, R.raw.niangshou_11);
		this.addSound(78, R.raw.niangshou_12);   
		this.addSound(79, R.raw.niangshou_13);   
		this.addSound(80, R.raw.niangshou_14); 
		this.addSound(81, R.raw.niangshou_15);  
		this.addSound(82, R.raw.niangshou_16);    
		this.addSound(83, R.raw.niangshou_17);  
		this.addSound(84, R.raw.niangshou_18);  
		this.addSound(85, R.raw.niangshou_19);  
		this.addSound(86, R.raw.niangshou_20);  
		this.addSound(87, R.raw.niangshou_21);  
		this.addSound(88, R.raw.niangshou_22);	

		this.addSound(89, R.raw.hanzi_01); 
		this.addSound(90, R.raw.hanzi_02);  
		this.addSound(91, R.raw.hanzi_03);  
		this.addSound(92, R.raw.hanzi_04);  
		this.addSound(93, R.raw.hanzi_05);   
		this.addSound(94, R.raw.hanzi_06);   
		this.addSound(95, R.raw.hanzi_07);  
		this.addSound(96, R.raw.hanzi_08);  
		this.addSound(97, R.raw.hanzi_09);  
		this.addSound(98, R.raw.hanzi_10); 
		this.addSound(99, R.raw.hanzi_11);
		this.addSound(100, R.raw.hanzi_12);   
		this.addSound(101, R.raw.hanzi_13);   
		this.addSound(102, R.raw.hanzi_14); 
		this.addSound(103, R.raw.hanzi_15);  
		this.addSound(104, R.raw.hanzi_16);    
		this.addSound(105, R.raw.hanzi_17);  
		this.addSound(106, R.raw.hanzi_18);  
		this.addSound(107, R.raw.hanzi_19);  
		this.addSound(108, R.raw.hanzi_20);  
		this.addSound(109, R.raw.hanzi_21);  
		this.addSound(110, R.raw.hanzi_22); 
		
	}
	
	public void addSound(int index, int SoundID)
	{
		mSoundPoolMap.put(index, mSoundPool.load(mContext, SoundID, 1));
	}
	
	public void playSound(int index, float voice)
	{
		Log.v("playSound", "index:" + index + " voice:" + voice);
		float streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		//streamVolume = streamVolume / mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		mSoundPool.play(mSoundPoolMap.get(index), streamVolume * voice, streamVolume * voice, 1, 0, 1f);
	}
	
}