package org.akk.akktuell.Activity;

import org.akk.akktuell.Model.InfoManager;

import android.view.MotionEvent;
import android.view.GestureDetector.SimpleOnGestureListener;

public class GestureScanner extends SimpleOnGestureListener {
	
	private InfoManager infoManager;
	private AKKtuellMainActivity main;

	private static int MIN_SIZE_OF_GESTURE=800;
	
	public GestureScanner(AKKtuellMainActivity main, InfoManager info) {
		this.main = main;
		infoManager = info;
	}
	
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if (velocityX >= MIN_SIZE_OF_GESTURE) {
			infoManager.decMonth();
		} else if (velocityX <= -1*MIN_SIZE_OF_GESTURE){
			infoManager.addMonth();
		}
		
		main.displayData();
		return false;
	}
}
