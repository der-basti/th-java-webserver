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
 * XXX [dsc|sne] javadoc
 * 
 * @author dsc and sne
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
	 * Statische Methode, liefert die einzige Instanz dieser Klasse zurück
	 * 
	 * @param configuration
	 * 
	 */
	@Deprecated
	private Log(Configuration configuration) {

		Log.calendar = new GregorianCalendar();
		Log.logDir = configuration.getLogRoot();
		Log.logFile = new File(Log.logDir + "/" + getTime(filePattern) + ".log");
		Log.logLevel = configuration.getLogLevel();
	}

	private Log(Calendar calendar, File logDir, LogLevel logLevel) {

		Log.calendar = calendar;
		Log.logDir = logDir;
		Log.logFile = new File(Log.logDir + "/" + getTime(filePattern) + ".log");
		Log.logLevel = logLevel;
	}

	/**
	 * Static method, which returns only one instance of this class.
	 */
	@Deprecated
	public static void createInstance(Configuration configuration) {
		new Log(configuration);
		debug("instance already exists.");
	}

	public static void createInstance() {
		new Log(new GregorianCalendar(), Configuration.getLogRoot(),
				Configuration.getLogLevel());
	}

	private static synchronized void log(LogLevel logLevel, String text) {

		if (!hasLogDir()) {
			createLogDir();
		}

		if (!hasLogFile()) {
			createLogFile();
		}

		if (Log.logLevel.ordinal() >= logLevel.ordinal()) {
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
			// XXX [dsc] is PrintWriter(file) a better solution?
			printWriter = new PrintWriter(new BufferedWriter(new FileWriter(
					Log.logFile)));
			printWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Couldn't create log file.");
		}
	}

	private static void appendLogFile(LogLevel logLevel, String text) {

		try {
			// XXX [dsc] is a PrintWriter(file) a better solution?
			printWriter = new PrintWriter(new BufferedWriter(new FileWriter(
					logFile, true)));
			printWriter.print(System.getProperty("line.separator")
					+ getTime(logPattern) + " [" + logLevel.name() + "] "
					+ text);
			printWriter.flush();
		} catch (IOException e) {
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
	 * TODO javadoc
	 * 
	 * @param text
	 */
	public static void debug(String text) {

		log(LogLevel.DEBUG, text);
	}

	/**
	 * TODO javadoc
	 * 
	 * @param text
	 */
	public static void info(String text) {

		log(LogLevel.INFO, text);
	}

	/**
	 * TODO javadoc
	 * 
	 * @param text
	 */
	public static void warn(String text) {

		log(LogLevel.WARN, text);
	}

	/**
	 * TODO javadoc
	 * 
	 * @param text
	 */
	public static void error(String text) {

		log(LogLevel.ERROR, text);
	}

	/**
	 * TODO javadoc
	 * 
	 * @param text
	 * @param errorMessage
	 */
	public static void error(String text, String errorMessage) {

		log(LogLevel.ERROR, text + " [" + errorMessage + "]");
	}

	/**
	 * TODO javadoc
	 * 
	 * @param text
	 */
	public static void fatal(String text) {

		log(LogLevel.FATAL, text);
	}

	/**
	 * TODO javadoc
	 * 
	 * @param text
	 * @param fatalMessage
	 */
	public static void fatal(String text, String fatalMessage) {

		log(LogLevel.FATAL, text + " [" + fatalMessage + "]");
	}
}
