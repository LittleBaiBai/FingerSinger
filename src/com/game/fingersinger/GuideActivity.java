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

	// �???��????��?��??
	private ImageView[] dots;

	// 记�??�???????�?�?�?
	private int currentIndex;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.guide);

		// ???�????页�??
		initViews();

		// ???�????�???��?????
		initDots();
	}

	private void initViews() {
		LayoutInflater inflater = LayoutInflater.from(this);

		views = new ArrayList<View>();
		// ???�????�?导�?��?????�?
		views.add(inflater.inflate(R.layout.guide_1, null));
		views.add(inflater.inflate(R.layout.guide_2, null));
		views.add(inflater.inflate(R.layout.guide_3, null));
		views.add(inflater.inflate(R.layout.guide_4, null));
		views.add(inflater.inflate(R.layout.guide_5, null));
		views.add(inflater.inflate(R.layout.guide_6, null));

		// ???�????Adapter
		vpAdapter = new ViewPagerAdapter(views, this);
		
		vp = (ViewPager) findViewById(R.id.viewpager);
		vp.setAdapter(vpAdapter);
		// �?�????�?
		vp.setOnPageChangeListener(this);
	}

	private void initDots() {
		LinearLayout ll = (LinearLayout) findViewById(R.id.ll);

		dots = new ImageView[views.size()];

		// �???????�?�???��?��??
		for (int i = 0; i < views.size(); i++) {
			dots[i] = (ImageView) ll.getChildAt(i);
			dots[i].setEnabled(true);// ??��?�为??��??
		}

		currentIndex = 0;
		dots[currentIndex].setEnabled(false);// 设置为�?��?��????��??�???��??
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

	// �?�???��?��????��????��?????
	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	// �?�????页�?��??�???��?��?????
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	// �???��??页�?��?????�???��?????
	@Override
	public void onPageSelected(int arg0) {
		// 设置�???��????��??�???��??
		setCurrentDot(arg0);
	}

}
