package com.game.fingersinger;

import java.util.ArrayList;
import java.util.LinkedList;

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
	public static Melody[] melody;
	public static LinkedList LastEdit;
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
		melody = new Melody[5];
		for(int i = 0; i< 5; i++){
			melody[i] = new Melody(i);
		}
		drawSoundManager = new SoundManager();
		playSoundManager = new SoundManager();
		
		
		LastEdit = new LinkedList<Melody>();
		colors = new int[5];	//»­±ÊÑÕÉ«
	}
}
