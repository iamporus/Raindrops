package com.room.raindrops.utils;

public class Logger {

	public static void log(final String data) {

		if (Constants.LOG)
			System.out.println("Raindrops: " + data);
	}


}
