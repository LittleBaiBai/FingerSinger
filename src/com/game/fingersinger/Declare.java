package com.game.fingersinger;

import java.util.LinkedList;

import android.app.Application;
import android.util.Log;

public class Declare extends Application {
	public static int menu_status;
	public static int color_status;
	public static int undo_status;
	public static int screen_width;
	public static int screen_height;
	public static boolean isSaved;
	public static SoundManager[] soundManager;
	public static int pointerInScreen;
	public static int pointerInMelody;
	public static Melody[] melody;
	public static LinkedList<Melody> LastEdit;
	public static int[] colors;
	public static int tempo_length;
	
	public static float button_menu_vertical;
	public static float button_menu_horizontal;
	public static float button_color_vertical;
	public static float button_color_horizontal;
	public static float button_undo_vertical;
	public static float button_undo_horizontal;
	public static float scale_start_x_y;
	public static float scale_start_y_x;
	public static float scale_length_vertical;
	public static float scale_length_horizontal;
	public static float note_top_dist;
	public static float note_button_dist;
	public static float note_inner_dist;
	public static float pointer_pressed;
	public static float pointer_unpress;
	public static float pointer_stick;
	public static float pointer_dx;
	
	public Declare(){
		menu_status = 1;
		color_status = 1;
		undo_status = 1;
		isSaved = true;
		tempo_length = 40;
		
		soundManager = new SoundManager[5];
		
		pointerInScreen = 0;
		pointerInMelody = 0;
		
		melody = new Melody[5];
		for (int i = 0; i < 5; i++) {
			melody[i] = new Melody(i);
		}		
		LastEdit = new LinkedList<Melody>();
		colors = new int[5];	//»­±ÊÑÕÉ«
	}
	
	public static int getIndexOfSound(int note) {
//		if (note < Declare.note_top_dist) {
//			Log.v("SoundIndex", note + " / 21: <" + Declare.note_top_dist);
//			return 21;
//		}
//		else if (note >= Declare.note_button_dist) {
//			Log.v("SoundIndex", note + " / 1: >=" + Declare.note_button_dist);
//			return 1;
//		}
//		else {
//			int temp = 20 - (int)((note - Declare.note_top_dist) / Declare.note_inner_dist);
//			Log.v("SoundIndex", note + " / " + temp);
//			return 20 - (int)((note - Declare.note_top_dist) / Declare.note_inner_dist);
//		}
		Log.v("note", "Declare.note_inner_dist: " + Declare.note_inner_dist);
		Log.v("note", "note/voice: " + note + "/" + (22 - (int)(note / Declare.note_inner_dist)));
		return 22 - (int)(note / Declare.note_inner_dist);
	}
	
}
