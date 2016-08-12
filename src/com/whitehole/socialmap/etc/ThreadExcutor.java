package com.whitehole.socialmap.etc;

public class ThreadExcutor {

	
	public static void execute(final Runnable runnable) {
		new Thread() {
			@Override
			public void run() {
				runnable.run();
			}
		}.start();
	}
	
}
