package com.estella.stopwatch.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.TimeUnit;

import com.estella.stopwatch.api.IStopwatch;

public class Stopwatch implements IStopwatch {
  private final String id;
  private final List<Long> lapTimeList;
  private final Object lock;
  private long lastLapTime = 0;
  private Boolean running;
  
  /**
   * Constructs a new stopwatch with the id.
   * @throws IllegalArgumentException if <code>id</code> is empty or null.
   */
  Stopwatch(String id) {
    if (id == null || id.trim().length() == 0) {
      throw new IllegalArgumentException("Error: id cannot be empty or null.");
    } else {
      this.id = id;
      lapTimeList = Collections.synchronizedList(new ArrayList<Long>());
      running = false;
      lock = new Object();
    }
  }
  
  private long getCurTimeInNanoSec() {
    return System.nanoTime();
  }
  
  /**
   * Check whether the stopwatch is running
   * @return true - is running, false - not running.
   */
  public boolean isRunning() {
    return running;
  }
  
  /**
   * Returns the Id of this stopwatch
   * @return the Id of this stopwatch.  Will never be empty or null.
   */
  @Override
  public String getId() {
    return id;
  }
  
  /**
   * Starts the stopwatch.
   * @throws IllegalStateException if called when the stopwatch is already running
   */
  @Override
  public void start() {
    synchronized(lock) {
      if (running) {
        throw new IllegalStateException("The stopwatch is already running.");
      } else {
        running = true;
        if (lapTimeList.isEmpty()) {
          lastLapTime = getCurTimeInNanoSec();
        } else {
          lastLapTime = getCurTimeInNanoSec() - lapTimeList.remove(lapTimeList.size()-1);
        }
      }
    }
  }
  
  /**
   * Record the latest lap time, and add the lap time to the list of lap times.
   * @return true if this lap time successfully added to the list.
   */
  private boolean addLap(long lastLapTime) {
    long curTime = getCurTimeInNanoSec();
    Boolean added = lapTimeList.add(curTime - lastLapTime);
    this.lastLapTime = curTime;
    return added;
  }
  
  /**
   * Stores the time elapsed since the last time lap() was called
   * or since start() was called if this is the first lap.
   * @throws IllegalStateException if called when the stopwatch isn't running
   */
  @Override
  public void lap() {
    synchronized (lock) {
      if (!running) {
        throw new IllegalStateException("Sorry, the stopwatch isn't running.");
      } else {
        addLap(lastLapTime);
      }
    }
  }

  /**
   * Stops the stopwatch (and records one final lap).
   * @throws IllegalStateException if called when the stopwatch isn't running
   */
  @Override
  public void stop() {
    synchronized (lock) {
      if (!running) {
        throw new IllegalStateException("Sorry, the stopwatch isn't running.");
      } else {
        addLap(lastLapTime);
        running = false;
      }
    }
  }

  /**
   * Resets the stopwatch.  If the stopwatch is running, this method stops the
   * watch and resets it.  This also clears all recorded laps.
   */
  @Override
  public void reset() {
    synchronized (lock) {
      if (running) {
        running = false;
      }
      lapTimeList.clear();
    }
  }

  /**
   * Returns a list of lap times (in milliseconds).  This method can be called at
   * any time and will not throw an exception.
   * @return a list of recorded lap times or an empty list if no times are recorded.
   */
  @Override
  public List<Long> getLapTimes() {
    return new ArrayList<Long>(lapTimeList);
  }
  
  /**
   * Compares this Stopwatch to the specified object. The result is true if and only if the 
   * argument is not null and is a Stopwatch object that has the same id, state, and list 
   * of lap times. 
   * 
   * @param o - The object to compare this Stopwatch against
   * @return  true if the given object represents a Stopwatch equivalent to this Stopwatch, 
   *          false otherwise
   */
  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Stopwatch)) {
      return false;
    }
    if (this == o) { return true; }
    Stopwatch sw = (Stopwatch) o;
    if (!this.id.equals(sw.getId())) { 
      return false;
    }
    if (!this.lapTimeList.equals(sw.getLapTimes())) {
      return false;
    }
    if (this.running != sw.isRunning()) {
      return false;
    }
    return true;
  }
  
  /**
   * Returns a hash code value for the Stopwatch object
   * 
   * @return  a hash code value for the Stopwatch object
   */
  @Override
  public int hashCode() {
    int result = 17;
    result = 31 * result + (id == null ? 0 : id.hashCode());
    result = 31 * result + (lapTimeList.hashCode());
    result = 31 * result + (running.hashCode());
    return result;
  }
  
  /**
   * Returns a string representation of the Stopwatch object.
   * 
   * e.g. Stopwatch Id - 1
   *      Stopwatch State - stop
   *      List of lap times as below.
   *      Lap - 1: 13 ms.
   *      Lap - 2: 1 ms.
   *      Lap - 3: 30 ms.
   *      Lap - 4: 100 ms.
   *      
   * @return a string representation of the Stopwatch object.
   */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Stopwatch Id - " + id + "\n");
    sb.append("Stopwatch State - " + (running ? "running" : "stop (not running)") + "\n");
    sb.append("List of lap times as below.\n");
    if (getLapTimes().size() == 0) {
      sb.append("No laps so far...\n");
    } else {
      ListIterator<Long> litr = getLapTimes().listIterator();
      while (litr.hasNext()) {
        int lapNum = litr.nextIndex() + 1;
        long lapTime = TimeUnit.MILLISECONDS.convert(litr.next(), TimeUnit.NANOSECONDS);
        sb.append("Lap - " + lapNum + ": " + lapTime + " ms.\n");
      }
    }
    return sb.toString();
  }
}