package com.game.fingersinger;

import java.io.Serializable;
import java.util.ArrayList;

public class Melody implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	ArrayList<Integer> notes;
	ArrayList<Integer> starts;
	ArrayList<Integer> stops;
	float voice;
	int color;
	
	public Melody(int c) {
		notes = new ArrayList<Integer>();
		starts = new ArrayList<Integer>();
		stops = new ArrayList<Integer>();
		voice = (float)0.8;
		color = c;
	}
	
	public Melody(Melody m) {
		notes = new ArrayList<Integer>(m.notes);
		starts = new ArrayList<Integer>(m.starts);
		stops = new ArrayList<Integer>(m.stops);
		voice = m.voice;
		color = m.color;
	}
}

