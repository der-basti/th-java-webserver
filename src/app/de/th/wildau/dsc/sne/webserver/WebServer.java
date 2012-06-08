package de.th.wildau.dsc.sne.webserver;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

/**
 * TODO javadoc
 * 
 * @author
 */
public class WebServer {

	protected static List<ScriptLanguage> supportedScriptLanguages;

	/**
	 * The main method for the web server program.
	 * 
	 * @param args
	 */
	public static void main(String[] startArguments) {

		new WebServer(startArguments);
	}

	public WebServer(String[] startArguments) {

		loadConfiguration(startArguments);

		// find scripting languages
		supportedScriptLanguages = Collections.unmodifiableList(ScriptExecutor
				.getSupportedScriptLanguages());
		Log.debug("Supported Script Languages "
				+ Arrays.toString(supportedScriptLanguages.toArray()));

		Log.info("instantiating web server");
		try {
			ServerSocket server = new ServerSocket(Configuration.getConfig()
					.getServerPort());
			Log.debug("bound port " + Configuration.getConfig().getServerPort());

			int corePoolSize = Runtime.getRuntime().availableProcessors();
			int maxPoolSize = (2 * corePoolSize) + 1;
			ArrayBlockingQueue<Runnable> workerQueue = new ArrayBlockingQueue<Runnable>(
					maxPoolSize);
			long keepAliveTime = 5;
			// XXX [sne] http://www.ibm.com/developerworks/library/j-jtp0730/
			ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
					corePoolSize, maxPoolSize, keepAliveTime, TimeUnit.SECONDS,
					workerQueue);
			threadPool.prestartAllCoreThreads();

			while (true) {
				Socket socket;
				try {
					socket = server.accept();
					Log.info(socket.getInetAddress().getHostName()
							+ " client request");
					threadPool.execute(new HttpHandler(socket));
					Log.debug("current threads: " + threadPool.getActiveCount());
				} catch (final IOException ex) {
					Log.error("Connection failed!", ex);
				} catch (final RejectedExecutionException ex) {
					// http://stackoverflow.com/questions/1519725/why-does-executors-newcachedthreadpool-throw-java-util-concurrent-rejectedexecut
					Log.fatal(
							"java.util.concurrent.RejectedExecutionException",
							ex);
				} catch (final Exception ex) {
					Log.fatal("Unknown error!", ex);
				}
			}
		} catch (final IOException ex) {
			Log.fatal("Can not start the server!", ex);
			System.err.println("Can not start the server! " + ex.getMessage());
		}
	}

	/**
	 * Load the web server configuration. <b>DON'T USE LOG BEFORE THIS METHOD
	 * CALL!</b>
	 * 
	 * @param startArguments
	 */
	private static void loadConfiguration(String[] startArguments) {

		Options options = new Options();
		options.addOption("c", true, "specify server configuration file");
		HelpFormatter helpFormatter = new HelpFormatter();
		helpFormatter.printHelp("WebServer", options);
		CommandLineParser parser = new PosixParser();
		CommandLine cmd;
		try {
			cmd = parser.parse(options, startArguments);
		} catch (final ParseException ex) {
			throw new IllegalStateException("Can not parse start arguments. "
					+ ex.getMessage());
		}

		try {
			if (cmd.hasOption("c")) {
				File configFile = new File(cmd.getOptionValue("c"));
				if (configFile.isFile() && configFile.canRead()) {
					Configuration.createInstance(configFile);
				}
			} else {
				// load default configuration
				InputStream is = WebServer.class
						.getResourceAsStream("server.conf.default");
				Configuration.createInstance(is);
			}

			Log.createInstance();
			Log.info("loaded server configuration");
			Log.debug(Configuration.getInstance().toString());

			if (Configuration.getConfig().getProxyHost() != null) {
				System.setProperty("http.proxyHost", Configuration.getConfig()
						.getProxyHost());
				System.setProperty("http.proxyPort", String
						.valueOf(Configuration.getConfig().getProxyPort()));
			}
		} catch (final Exception ex) {
			System.err.println("Can not load server configuration file.");
		}
	}
}
