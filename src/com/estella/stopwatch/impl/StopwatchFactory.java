package com.estella.stopwatch.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.estella.stopwatch.api.IStopwatch;

/**
 * The StopwatchFactory is a thread-safe factory class for IStopwatch objects.
 * It maintains references to all created IStopwatch objects and provides a
 * convenient method for getting a list of those objects.
 *
 */
public class StopwatchFactory {
  private final static ConcurrentHashMap<String, IStopwatch> watchMap = new ConcurrentHashMap<>();
  private final static Object lock = new Object();
  
	/**
	 * Creates and returns a new IStopwatch object
	 * @param id The identifier of the new object
	 * @return The new IStopwatch object
	 * @throws IllegalArgumentException if <code>id</code> is empty, null, or already
   *     taken.
	 */
	public static IStopwatch getStopwatch(String id) {
		if (id == null || id.trim().length() == 0) {
		  throw new IllegalArgumentException("Error: id cannot be empty or null");
		}
		synchronized (lock) {
		  if (watchMap.containsKey(id)) {
		    throw new IllegalArgumentException("This id has already been taken.");
		  } else {
		    IStopwatch newWatch = new Stopwatch(id);
		    watchMap.put(id, newWatch);
		    return newWatch;
		  }
    }
	}

	/**
	 * Returns a list of all created stopwatches
	 * @return a List of all creates IStopwatch objects.  Returns an empty
	 * list if no IStopwatches have been created.
	 */
	public static List<IStopwatch> getStopwatches() {
		return new ArrayList<IStopwatch>(watchMap.values());
	}
}
