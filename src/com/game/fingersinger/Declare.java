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
		
		colors = new int[5];	//»­±ÊÑÕÉ«
	}
	
	public static int getIndexOfSound(int note) {
//		Log.v("note", "Declare.note_inner_dist: " + Declare.note_inner_dist);
		Log.v("note", "note/voice: " + note + "/" + (22 - (int)(note / Declare.note_inner_dist)));
		return 22 - (int)(note / Declare.note_inner_dist);
	}
	
}
