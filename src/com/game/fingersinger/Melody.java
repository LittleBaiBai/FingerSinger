package com.game.fingersinger;

import java.util.ArrayList;

public class Melody {
	ArrayList notes;
	ArrayList stops;
	double voice;
	int color;
	public Melody(int c){
		notes = new ArrayList();
		stops = new ArrayList();
		voice = 0.8;
		color = c;
	}
	public Melody(Melody m){
		notes = new ArrayList(m.notes);
		stops = new ArrayList(m.stops);
		voice = m.voice;
		color = m.color;
	}
}

