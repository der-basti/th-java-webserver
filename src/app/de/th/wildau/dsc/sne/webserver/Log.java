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

	private Calendar calendar;
	private File logDir;
	private File logFile;
	private LogLevel logLevel;
	private PrintWriter printWriter;
	// XXX [dsc] make configurable?
	private String filePattern = "yyyy-MM-dd";
	private String logPattern = "hh:mm:ss:SSS";

	/**
	 * TODO javadoc
	 * 
	 * @param configuration
	 * 
	 */
	public Log(Configuration configuration) {

		this.calendar = new GregorianCalendar();
		this.logDir = configuration.getLogRoot();
		this.logFile = new File(this.logDir + "/" + getTime(this.filePattern)
				+ ".log");
		this.logLevel = configuration.getLogLevel();
	}

	private synchronized void log(LogLevel logLevel, String text) {

		if (!hasLogDir()) {
			createLogDir();
		}

		if (!hasLogFile()) {
			createLogFile();
		}

		if (this.logLevel.ordinal() >= logLevel.ordinal()) {
			appendLogFile(logLevel, text);
		}
	}

	/**
	 * Does the log directory exist?
	 * 
	 * @return <code>true</code> if LogDir already exists<br />
	 *         <code>false</code> otherwise
	 */
	private boolean hasLogDir() {

		return this.logDir.exists();
	}

	/**
	 * Create log directory.
	 */
	private void createLogDir() {

		this.logDir.mkdirs();
	}

	/**
	 * Does the log file for the current day exist?
	 * 
	 * @return <code>true</code> if log file already exists<br />
	 *         <code>false</code> otherwise
	 */
	private boolean hasLogFile() {

		return this.logFile.exists();
	}

	/**
	 * Create a log file for the current day.
	 */
	private void createLogFile() {

		try {
			// XXX [dsc] is a PrintWriter(file) a better solution?
			this.printWriter = new PrintWriter(new BufferedWriter(
					new FileWriter(this.logFile)));
			this.printWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Couldn't create log file.");
		}
	}

	private void appendLogFile(LogLevel logLevel, String text) {

		try {
			// XXX [dsc] is a PrintWriter(file) a better solution?
			this.printWriter = new PrintWriter(new BufferedWriter(
					new FileWriter(this.logFile, true)));
			this.printWriter.print(System.getProperty("line.separator")
					+ getTime(this.logPattern) + " [" + logLevel.name() + "] "
					+ text);
			this.printWriter.flush();
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
	private String getTime(String pattern) {

		return new SimpleDateFormat(pattern).format(this.calendar.getTime());
	}

	/**
	 * TODO javadoc
	 * 
	 * @param text
	 */
	public void debug(String text) {

		log(LogLevel.DEBUG, text);
	}

	/**
	 * TODO javadoc
	 * 
	 * @param text
	 */
	public void info(String text) {

		log(LogLevel.INFO, text);
	}

	/**
	 * TODO javadoc
	 * 
	 * @param text
	 */
	public void warn(String text) {

		log(LogLevel.WARN, text);
	}

	/**
	 * TODO javadoc
	 * 
	 * @param text
	 */
	public void error(String text) {

		log(LogLevel.ERROR, text);
	}

	/**
	 * TODO javadoc
	 * 
	 * @param text
	 */
	public void fatal(String text) {

		log(LogLevel.FATAL, text);
	}
}
