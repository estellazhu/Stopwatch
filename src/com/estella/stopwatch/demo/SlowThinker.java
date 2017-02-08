package com.estella.stopwatch.demo;

import java.util.List;
import java.util.logging.Logger;

import com.estella.stopwatch.impl.StopwatchFactory;
import com.estella.stopwatch.api.IStopwatch;

/**
 * This is a simple program that demonstrates just some of
 * the functionality of the IStopwatch interface and StopwatchFactory class.
 * Just because this class runs successfully does not mean that the assignment is
 * complete.  It is up to you to implement the methods of IStopwatch and StopwatchFactory
 *
 */
public class SlowThinker {

	/** use a logger instead of System.out.println */
	private static final Logger logger =
	    Logger.getLogger("com.estella.stopwatch.demo.SlowThinker");

	/**
	 * Run the SlowThinker demo application
	 * @param args a single argument specifying the number of threads
	 */
	public static void main(String[] args) {
		SlowThinker thinker = new SlowThinker();
		thinker.go();
	}

	/**
	 * Starts the slowthinker object
	 * It will get a stopwatch, set a number of lap times, stop the watch
	 * and then print out all the lap times
	 *
	 */
	private void go() {
		Runnable runnable = new Runnable() {
			public void run() {
				IStopwatch stopwatch = StopwatchFactory.getStopwatch(
				    "ID " + Thread.currentThread().getId());
			  stopwatch.start();
				for (int i = 0; i < 10; i++) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException ie) { /* safely ignore this */ }
					stopwatch.lap();
				}
				stopwatch.stop();
				List<Long> times = stopwatch.getLapTimes();
				logger.info(times.toString());
			}
		};
		Thread thinkerThread = new Thread(runnable);
		thinkerThread.start();
	}
}
