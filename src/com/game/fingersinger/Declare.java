package com.game.fingersinger;

import android.app.Application;
import android.util.Log;

public class Declare extends Application {
	
	public static SoundManager[] soundManager;
//	public static Melody[] melody;
//	public static LinkedList<Melody> LastEdit;
	public static int[] colors;
	public static int melody_start;	
	public static int pointerInScreen;
	public static int tempo_length;
	public static int menu_status;
	public static int color_status;
	public static int screen_width;
	public static int screen_height;
	public static boolean isSaved;
	public static boolean moveCanvas;
	public static float speed;
	
	public static float note_top_dist;
	public static float note_button_dist;
	public static float note_inner_dist;
	public static float draw_height;
	public static float pointer_unpress;
	
	
	public Declare(){
		menu_status = 1;
		color_status = 1;
		isSaved = true;
		speed = (float)0.8;
		moveCanvas = false;
		melody_start = 0;
		soundManager = new SoundManager[5];
		
		colors = new int[5];	//������ɫ
	}
	
	public static int getIndexOfSound(int note) {
		if (note < Declare.note_top_dist) {
			Log.v("SoundIndex", note + " / 22: <" + Declare.note_top_dist);
			return 22;
		}
		else if (note >= Declare.note_button_dist && note < Declare.draw_height) {
			Log.v("SoundIndex", note + " / 1: >=" + Declare.note_button_dist);
			return 1;
		}
		else if (note > Declare.draw_height){
			return 0;
		}
		else {
//			int temp = 20 - (int)((note - Declare.note_top_dist) / Declare.note_inner_dist);
//			Log.v("SoundIndex", note + " / " + temp);
			return 21 - (int)((note - Declare.note_top_dist) / Declare.note_inner_dist);
		}
//		Log.v("note", "Declare.note_inner_dist: " + Declare.note_inner_dist);
//		Log.v("note", "note/voice: " + note + "/" + (22 - (int)(note / Declare.note_inner_dist)));
//		return 22 - (int)(note / Declare.note_inner_dist);
	}
	
}
