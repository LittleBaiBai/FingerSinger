package com.game.fingersinger;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class GuideActivity extends Activity implements OnPageChangeListener {

	private ViewPager vp;
	private ViewPagerAdapter vpAdapter;
	private List<View> views;

	// åº???¨å????¹å?¾ç??
	private ImageView[] dots;

	// è®°å??å½???????ä¸?ä½?ç½?
	private int currentIndex;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.guide);

		// ???å§????é¡µé??
		initViews();

		// ???å§????åº???¨å?????
		initDots();
	}

	private void initViews() {
		LayoutInflater inflater = LayoutInflater.from(this);

		views = new ArrayList<View>();
		// ???å§????å¼?å¯¼å?¾ç?????è¡?
		views.add(inflater.inflate(R.layout.guide_1, null));
		views.add(inflater.inflate(R.layout.guide_2, null));
		views.add(inflater.inflate(R.layout.guide_3, null));
		views.add(inflater.inflate(R.layout.guide_4, null));
		views.add(inflater.inflate(R.layout.guide_5, null));
		views.add(inflater.inflate(R.layout.guide_6, null));

		// ???å§????Adapter
		vpAdapter = new ViewPagerAdapter(views, this);
		
		vp = (ViewPager) findViewById(R.id.viewpager);
		vp.setAdapter(vpAdapter);
		// ç»?å®????è°?
		vp.setOnPageChangeListener(this);
	}

	private void initDots() {
		LinearLayout ll = (LinearLayout) findViewById(R.id.ll);

		dots = new ImageView[views.size()];

		// å¾???????å¾?å°???¹å?¾ç??
		for (int i = 0; i < views.size(); i++) {
			dots[i] = (ImageView) ll.getChildAt(i);
			dots[i].setEnabled(true);// ??½è?¾ä¸º??°è??
		}

		currentIndex = 0;
		dots[currentIndex].setEnabled(false);// è®¾ç½®ä¸ºç?½è?²ï????³é??ä¸???¶æ??
	}

	private void setCurrentDot(int position) {
		if (position < 0 || position > views.size() - 1
				|| currentIndex == position) {
			return;
		}

		dots[position].setEnabled(false);
		dots[currentIndex].setEnabled(true);

		currentIndex = position;
	}

	// å½?æ»???¨ç?¶æ????¹å????¶è?????
	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	// å½?å½????é¡µé?¢è??æ»???¨æ?¶è?????
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	// å½???°ç??é¡µé?¢è?????ä¸???¶è?????
	@Override
	public void onPageSelected(int arg0) {
		// è®¾ç½®åº???¨å????¹é??ä¸???¶æ??
		setCurrentDot(arg0);
	}

}
