package com.game.fingersinger;

import java.util.ArrayList;

import android.app.Application;

public class Declare extends Application {
	public static int menu_status;
	public static int color_status;
	public static int undo_status;
	public static int screen_width;
	public static int screen_height;
	public static boolean isSaved;
	public static SoundManager drawSoundManager;
	public static SoundManager playSoundManager;
	public static ArrayList[] melody;
	public static int[] colors;
	
	public static float button_menu_vertical;
	public static float button_menu_horizontal;
	public static float button_color_vertical;
	public static float button_color_horizontal;
	public static float button_undo_vertical;
	public static float button_undo_horizontal;
	public static float scale_start_x_y;
	public static float scale_start_y_x;
	public static float scale_length_vertial;
	public static float scale_length_horizontal;
	public static float note_top_dist;
	public static float note_button_dist;
	public static float note_inner_dist;
	
	public Declare(){
		menu_status = 1;
		color_status = 1;
		undo_status = 1;
		isSaved = true;
		
		drawSoundManager = new SoundManager();
		playSoundManager = new SoundManager();
		
		melody = new ArrayList[5];
		for(int i = 0; i < 5; i++){
			melody[i] = new ArrayList<Integer>();
		}		
		colors = new int[5];
	}
}
