package de.th.wildau.dsc.sne.webserver;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Log is used to log messages for a specific application component.
 * 
 * @author dsc and sne
 * 
 */
public class Log {

	private static Calendar calendar;
	private static File logDir;
	private static File logFile;
	private static LogLevel logLevel;
	private static PrintWriter printWriter;
	private static String filePattern = "yyyy-MM-dd";
	private static String logPattern = "HH:mm:ss:SSS";

	/**
	 * Hidden constructor. Log is a singleton.
	 * 
	 * @param calendar
	 * @param logDir
	 * @param logLevel
	 */
	private Log(Calendar calendar, File logDir, LogLevel logLevel) {

		Log.calendar = calendar;
		Log.logDir = logDir;
		Log.logFile = new File(Log.logDir + "/" + getTime(filePattern) + ".log");
		Log.logLevel = logLevel;
	}

	/**
	 * Static method, which returns only one instance of this class.
	 */
	public static void createInstance() {
		new Log(new GregorianCalendar(), Configuration.getConfig()
				.getLogRootFile(), Configuration.getConfig().getLogLevelEnum());
	}

	/**
	 * The main logging function.
	 * 
	 * @param logLevel
	 * @param text
	 */
	// TODO [dsc] try to synchronize the specific object
	private static synchronized void log(LogLevel logLevel, String text) {

		if (Log.logLevel.ordinal() >= logLevel.ordinal()) {

			if (!hasLogDir()) {
				createLogDir();
			}

			if (!hasLogFile()) {
				createLogFile();
			}

			appendLogFile(logLevel, text);
		}
	}

	/**
	 * Does the log directory exist?
	 * 
	 * @return <code>true</code> if LogDir already exists<br />
	 *         <code>false</code> otherwise
	 */
	private static boolean hasLogDir() {

		return Log.logDir.exists();
	}

	/**
	 * Create log directory.
	 */
	private static void createLogDir() {

		Log.logDir.mkdirs();
	}

	/**
	 * Does the log file for the current day exist?
	 * 
	 * @return <code>true</code> if log file already exists<br />
	 *         <code>false</code> otherwise
	 */
	private static boolean hasLogFile() {

		return Log.logFile.exists();
	}

	/**
	 * Create a log file for the current day.
	 */
	private static void createLogFile() {

		try {
			// XXX [dsc] any better solutions?
			printWriter = new PrintWriter(new BufferedWriter(new FileWriter(
					Log.logFile)));
			printWriter.flush();
		} catch (final IOException e) {
			e.printStackTrace();
			System.err.println("Couldn't create log file.");
		}
	}

	private static void appendLogFile(LogLevel logLevel, String text) {

		try {
			// XXX [dsc] any better solutions?
			printWriter = new PrintWriter(new BufferedWriter(new FileWriter(
					logFile, true)));
			printWriter.print(System.getProperty("line.separator")
					+ getTime(logPattern) + " [" + logLevel.name() + "] "
					+ text);
			printWriter.flush();
		} catch (final IOException e) {
			System.err.println("Couldn't append to log file.");
		}
	}

	/**
	 * Help method, which returns the current time as a <code>String</code>.<br />
	 * <br />
	 * <b>Possible pattern/values:</b><br />
	 * y = year; M = month in year; d = day in month; h = hour in am/pm (1-12);
	 * H hour in day (0-23); m = minute in hour; s = second in minute; S =
	 * millisecond; E = day in week Text; D = day in year; F = day of week in
	 * month; w = week in year; W = week in month; a = am/pm marker; k = hour in
	 * day (1 =24); K = hour in am/pm (0-11); z = time zone; ' = escape for text <br />
	 * <br />
	 * <i>e.g. yyyy-mm-dd</i><br />
	 * 
	 * @param pattern
	 *            String
	 * @return formated time
	 */
	private static String getTime(String pattern) {

		return new SimpleDateFormat(pattern).format(calendar.getTime());
	}

	/**
	 * DEBUG indicates a detailed tracing message.
	 * 
	 * @param text
	 */
	public static void debug(String text) {

		log(LogLevel.DEBUG, text);
	}

	/**
	 * INFO is a message level for informational messages.
	 * 
	 * @param text
	 */
	public static void info(String text) {

		log(LogLevel.INFO, text);
	}

	/**
	 * WARN is a message level indicating a potential problem.
	 * 
	 * @param text
	 */
	public static void warn(String text) {

		log(LogLevel.WARN, text);
	}

	/**
	 * ERROR is a message level of a problem.
	 * 
	 * @param text
	 */
	public static void error(String text) {

		log(LogLevel.ERROR, text);
	}

	/**
	 * @see #error(String)
	 * 
	 * @param text
	 * @param errorMessage
	 */
	public static void error(String text, Exception errorMessage) {

		log(LogLevel.ERROR, text + " [" + errorMessage.getMessage() + "]");
	}

	/**
	 * FATAL is a message level of a high critical problem.
	 * 
	 * @param text
	 */
	public static void fatal(String text) {

		log(LogLevel.FATAL, text);
	}

	/**
	 * @see #fatal(String)
	 * 
	 * @param text
	 * @param fatalMessage
	 */
	public static void fatal(String text, Exception fatalMessage) {

		log(LogLevel.FATAL, text + " [" + fatalMessage.getMessage() + "]");
	}
}
