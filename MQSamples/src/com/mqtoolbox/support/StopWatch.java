package com.mqtoolbox.support;

import java.sql.Timestamp;
import java.util.GregorianCalendar;

public class StopWatch {
	private long startTime = -1;
	private long endTime = -1;
	private long timeTaken = -1;
	private Timestamp startTS = null;
	private Timestamp endTS = null;

	public StopWatch() {
		warmUp();
	}

	/**
	 * Get the current timestamp
	 * 
	 * @return
	 */
	public String getCurrTimestamp() {
		return String.format("%,9d ms", System.currentTimeMillis());
	}

	/**
	 * Start the stopwatch
	 */
	public void start() {
		this.startTime = System.currentTimeMillis();
		this.startTS = new Timestamp(GregorianCalendar.getInstance().getTimeInMillis());
		this.endTime=-1;
		this.timeTaken=-1;
	}

	/**
	 * Stop the stopwatch
	 */
	public void stop() {
		this.endTime = System.currentTimeMillis();
		this.endTS = new Timestamp(GregorianCalendar.getInstance().getTimeInMillis());

		if (this.startTime != -1 && this.endTime != -1)
			this.timeTaken = this.endTime - this.startTime;
	}

	/**
	 * Return the time taken
	 * 
	 * @return
	 */
	public long getTimeTaken() {
		return timeTaken < 0 ? 0 : timeTaken;
	}

	/**
	 * Return the end time stamp
	 * 
	 * @return
	 */
	public String getStartTimestamp() {
		return this.startTS == null ? new String("") : this.startTS.toString();
	}

	public String getEndTimestamp() {
		return this.endTS == null ? new String("") : this.endTS.toString();
	}

	/**
	 * Format the time taken
	 * 
	 * @return
	 */
	public String formatTimeTaken() {
		return String.format("%,9d ms", this.getTimeTaken());
	}

	/**
	 * Format the time taken
	 * 
	 * @return
	 */
	public String formatTimeTaken(long... timeTakenArgs) {
		long timeTaken = 0;
		for (long val : timeTakenArgs)
			timeTaken += val;
		return String.format("%,11d ms", timeTaken);
	}

	/**
	 * Format the time taken
	 * 
	 * @return
	 */
	public String formatInProgressTimeTaken() {
		if (this.startTime == -1)
			return "Stopwatch not yet started";

		long toTime = this.endTime != -1 ? this.endTime : System.currentTimeMillis();

		long timeTaken = toTime - startTime;

		return String.format("%,11d ms", timeTaken);
	}

	/**
	 * Format the time taken
	 * 
	 * @param msg Write this message with the time
	 * @return
	 */
	public String formatInProgressTimeTaken(String msg) {
		if (this.startTime == -1)
			return "Stopwatch not yet started";

		long toTime = this.endTime != -1 ? this.endTime : System.currentTimeMillis();

		long timeTaken = toTime - startTime;

		timeTaken = timeTaken < 0 ? 0 : timeTaken;

		return String.format("%-30s : %,11d ms", msg, timeTaken);
	}

	/**
	 * Format a time taken message and write it to the monitor
	 * 
	 * @param str String to display with the time
	 */
	public String formatCommandComplete(String str) {
		return str + " (" + this.formatTimeTaken().trim() + ")";
	}

	/**
	 * Format a time taken message and write it to the monitor
	 */
	public String formatCommandComplete() {
		return this.formatCommandComplete("Command complete");
	}

	/**
	 * Warm up the class before using for the first time
	 */
	public final void warmUp() {
		start();
		stop();
		
//		// Reset back to start. Needed as some functions assum this is initialized.
//		startTime = -1;
//		endTime = -1;
//		timeTaken = -1;
	}
}