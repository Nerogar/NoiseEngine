package de.nerogar.noise.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Logger {

	private static final String[] LOG_LEVEL_STRINGS = {
			"debug",
			"info",
			"warning",
			"error"
	};

	/**
	 * Information to find bugs during development.
	 */
	public static final  int         DEBUG        = 0;
	private static final PrintStream DEBUG_STREAM = new LogStream(DEBUG);

	/**
	 * More important than debug information.
	 */
	public static final  int         INFO        = 1;
	private static final PrintStream INFO_STREAM = new LogStream(INFO);

	/**
	 * Warnings about unexpected behavior.
	 */
	public static final  int         WARNING        = 2;
	private static final PrintStream WARNING_STREAM = new LogStream(WARNING);

	/**
	 * Problems that can cause a crash.
	 */
	public static final  int         ERROR        = 3;
	private static final PrintStream ERROR_STREAM = new LogStream(ERROR);

	private static List<LogOutStream> logStreams;
	private static boolean    printTimestamp = false;
	private static DateFormat dateFormat     = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/**
	 * acitvates or deactivates the output of timestamps in log messages
	 *
	 * @param print the new printTimestamp value
	 */
	public static void setPrintTimestamp(boolean print) {
		printTimestamp = print;
	}

	/**
	 * @param minLogLevel the minimum loglevel to print on this stream
	 * @param stream      the printStream for message output
	 */
	public static void addStream(int minLogLevel, PrintStream stream) {
		logStreams.add(new LogOutStream(minLogLevel, ERROR, stream));
	}

	/**
	 * @param minLogLevel the minimum loglevel to print on this stream
	 * @param maxLogLevel the maximum loglevel to print on this stream
	 * @param stream      the printStream for message output
	 */
	public static void addStream(int minLogLevel, int maxLogLevel, PrintStream stream) {
		logStreams.add(new LogOutStream(minLogLevel, maxLogLevel, stream));
	}

	/**
	 * removes any stream that is equal to the specified stream.
	 *
	 * @param stream the stream to remove
	 * @return true, if a stream was removed, false otherwise
	 */
	public static boolean removeStream(PrintStream stream) {
		return logStreams.removeIf((a) -> a.stream.equals(stream));
	}

	/**
	 * returns a {@link PrintStream} for easy debug logging
	 *
	 * @return the debug stream
	 */
	public static PrintStream getDebugStream() {
		return DEBUG_STREAM;
	}

	/**
	 * returns a {@link PrintStream} for easy info logging
	 *
	 * @return the info stream
	 */
	public static PrintStream getInfoStream() {
		return INFO_STREAM;
	}

	/**
	 * returns a {@link PrintStream} for easy warning logging
	 *
	 * @return the warning stream
	 */
	public static PrintStream getWarningStream() {
		return WARNING_STREAM;
	}

	/**
	 * returns a {@link PrintStream} for easy error logging
	 *
	 * @return the error stream
	 */
	public static PrintStream getErrorStream() {
		return ERROR_STREAM;
	}

	/**
	 * prints the message to all attached streams with the correct log level
	 *
	 * @param logLevel the loglevel for this message
	 * @param msg      the message as a String
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
	 * @param msg      the Object to log
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
		if (printTimestamp) {
			Date date = new Date();
			stream.println(dateFormat.format(date) + " [" + LOG_LEVEL_STRINGS[logLevel] + "] " + msg);
		} else {
			stream.println("[" + LOG_LEVEL_STRINGS[logLevel] + "] " + msg);
		}
	}

	static {
		logStreams = new ArrayList<LogOutStream>();
	}

	/**
	 * A class to store the output streams
	 */
	private static class LogOutStream {
		public int         minLogLevel;
		public int         maxLogLevel;
		public PrintStream stream;

		public LogOutStream(int minLogLevel, int maxLogLevel, PrintStream stream) {
			this.minLogLevel = minLogLevel;
			this.maxLogLevel = maxLogLevel;
			this.stream = stream;
		}
	}

	/**
	 * a stream that does nothing
	 */
	private static class NullStream extends OutputStream {

		@Override
		public void write(int b) throws IOException {
			//do nothing
		}

		@Override
		public void flush() throws IOException {
			//do nothing
		}

		@Override
		public void close() throws IOException {
			//do nothing
		}

	}

	/**
	 * A stream fpr logging different datatypes
	 */
	private static class LogStream extends PrintStream {

		private int logLevel;

		public LogStream(int logLevel) {
			super(new NullStream());
			this.logLevel = logLevel;
		}

		@Override
		public void println() {
			log(logLevel, "\n");
		}

		@Override
		public void print(boolean b) {
			log(logLevel, String.valueOf(b));
		}

		@Override
		public void print(char c) {
			log(logLevel, String.valueOf(c));
		}

		@Override
		public void print(int i) {
			log(logLevel, String.valueOf(i));
		}

		@Override
		public void print(long l) {
			log(logLevel, String.valueOf(l));
		}

		@Override
		public void print(float f) {
			log(logLevel, String.valueOf(f));
		}

		@Override
		public void print(double d) {
			log(logLevel, String.valueOf(d));
		}

		@Override
		public void print(char[] s) {
			log(logLevel, String.valueOf(s));
		}

		@Override
		public void print(String s) {
			log(logLevel, s);
		}

		@Override
		public void print(Object obj) {
			log(logLevel, obj);
		}

		@Override
		public void println(boolean b) {
			print(b);
		}

		@Override
		public void println(char c) {
			print(c);
		}

		@Override
		public void println(int i) {
			print(i);
		}

		@Override
		public void println(long l) {
			println(l);
		}

		@Override
		public void println(float f) {
			print(f);
		}

		@Override
		public void println(double d) {
			print(d);
		}

		@Override
		public void println(char[] s) {
			print(s);
		}

		@Override
		public void println(String s) {
			print(s);
		}

		@Override
		public void println(Object obj) {
			print(obj);
		}

		@Override
		public PrintStream printf(String format, Object... args) {
			return format(format, args);
		}

		@Override
		public PrintStream printf(Locale l, String format, Object... args) {
			return format(l, format, args);
		}

		@Override
		public PrintStream append(CharSequence csq) {
			print(csq.toString());
			return this;
		}

		@Override
		public PrintStream format(String format, Object... args) {
			log(logLevel, String.format(format, args));
			return this;
		}

		@Override
		public PrintStream format(Locale l, String format, Object... args) {
			log(logLevel, String.format(l, format, args));
			return this;
		}

		@Override
		public PrintStream append(CharSequence csq, int start, int end) {
			print(csq.subSequence(start, end).toString());
			return this;
		}

		@Override
		public PrintStream append(char c) {
			write(c);
			return this;
		}

		@Override
		public void write(int c) {
			log(c, String.valueOf(c));
		}

		@Override
		public void flush() {
			//do nothing
		}

		@Override
		public void close() {
			//do nothing
		}

	}

}
