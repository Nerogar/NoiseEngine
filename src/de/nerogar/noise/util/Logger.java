package de.nerogar.noise.util;

import java.io.*;
import java.util.*;

public class Logger {

	private static class LogStream {
		public int minLogLevel;
		public int maxLogLevel;
		public PrintStream stream;

		public LogStream(int minLogLevel, int maxLogLevel, PrintStream stream) {
			this.minLogLevel = minLogLevel;
			this.maxLogLevel = maxLogLevel;
			this.stream = stream;
		}
	}

	/**
	 * Information to find bugs during development.
	 */
	public static final int DEBUG = 0;

	/**
	 * More important than debug information. 
	 */
	public static final int INFO = 1;

	/**
	 * Warnings about unexpected behavior.
	 */
	public static final int WARNING = 2;

	/**
	 * Problems that can cause a crash.
	 */
	public static final int ERROR = 4;

	private static List<LogStream> logStreams;

	/**
	 * @param minLogLevel the minimum loglevel to print on this stream 
	 * @param stream the printStream for message output
	 */
	public static void addStream(int minLogLevel, PrintStream stream) {
		logStreams.add(new LogStream(minLogLevel, ERROR, stream));
	}

	/**
	 * @param minLogLevel the minimum loglevel to print on this stream
	 * @param maxLogLevel the maximum loglevel to print on this stream
	 * @param stream the printStream for message output
	 */
	public static void addStream(int minLogLevel, int maxLogLevel, PrintStream stream) {
		logStreams.add(new LogStream(minLogLevel, maxLogLevel, stream));
	}

	/**
	 * removes any stream that is equal to the specified stream.
	 * @param stream the stream to remove
	 * @return true, if a stream was removed, false otherwise
	 */
	public static boolean removeStream(PrintStream stream) {
		return logStreams.removeIf((a) -> a.stream.equals(stream));
	}

	/**
	 * prints the message to all attached streams with the correct log level
	 * 
	 * @param logLevel the loglevel for this message
	 * @param msg the message as a String
	 */
	public static void log(int logLevel, String msg) {
		logStreams.forEach((logStream) -> {
			if (logLevel >= logStream.minLogLevel && logLevel <= logStream.maxLogLevel) {
				print(logStream.stream, logLevel, msg);
			}
		});
	}

	/**
	 * calls </code>.toString()</code> on msg and logs it
	 * 
	 * @param logLevel the loglevel for this message
	 * @param msg the Object to log
	 */
	public static void log(int logLevel, Object msg) {
		logStreams.forEach((logStream) -> {
			if (logLevel >= logStream.minLogLevel && logLevel <= logStream.maxLogLevel) {
				if (msg instanceof Object[]) {
					print(logStream.stream, logLevel, Arrays.deepToString((Object[]) msg));
				} else {
					print(logStream.stream, logLevel, msg.toString());
				}
			}
		});
	}

	private static void print(PrintStream stream, int logLevel, String msg) {
		stream.println("[" + logLevel + "] " + msg);
	}

	static {
		logStreams = new ArrayList<LogStream>();
	}

}
