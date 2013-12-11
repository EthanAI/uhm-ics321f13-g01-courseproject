package edu.hawaii.ics321f13.util;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;

/**
 * Used for getting information and other metrics on the results ie: how long it takes to get a result from the program
 *
 */
public class GlobalStopWatch {
	
	private static StopWatch timer = null;
	private static volatile boolean isRunning = false;
	
	public static synchronized void start() {
		timer = new StopWatch();
		isRunning = true;
		timer.start();
	}
	
	public static synchronized void stop() {
		if(isRunning) {
			timer.stop();
			isRunning = false;
		}
	}
	
	public static synchronized long getElapsedTime(TimeUnit returnedUnit) {
		return returnedUnit.convert(timer.getNanoTime(), TimeUnit.NANOSECONDS);
	}
	
	public static void printElapsedTime(String prefix, String suffix, TimeUnit outputUnit) {
		long elapsed = getElapsedTime(Objects.requireNonNull(outputUnit));
		System.out.println((prefix != null ? prefix : "") + elapsed + " " 
				+ outputUnit.toString().toLowerCase() + (suffix != null ? suffix : ""));
	}
	
}
