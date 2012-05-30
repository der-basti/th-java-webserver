package de.th.wildau.dsc.sne.webserver;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
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

		Log.info("instantiating web server");
		try {
			ServerSocket server = new ServerSocket(Configuration.getPort());
			Log.debug("bound port " + Configuration.getPort());

			int corePoolSize = Runtime.getRuntime().availableProcessors();
			int maxPoolSize = corePoolSize + 1;
			ArrayBlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<Runnable>(
					maxPoolSize);
			List<HttpHandler> worker = Collections
					.synchronizedList(new ArrayList<HttpHandler>());
			long keepAliveTime = 5;
			// XXX [sne] http://www.ibm.com/developerworks/library/j-jtp0730/
			ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
					corePoolSize, maxPoolSize, keepAliveTime,
					TimeUnit.SECONDS, workQueue);
			threadPool.prestartAllCoreThreads();

			// TODO [sne] implement script execution
			ScriptExecutor scriptExecutor = new ScriptExecutor();
			ScriptExecutor.getSupportedScriptLanguages();

			while (true) {
				Socket socket;
				try {
					socket = server.accept();
					Log.info(socket.getInetAddress().getHostName()
							+ " client request");
					Log.debug("current threads: " + threadPool.getActiveCount());
					threadPool.execute(new HttpHandler(socket));
				} catch (IOException ex) {
					Log.error("Connection failed!", ex);
				} catch (Exception ex) {
					Log.fatal("Unknown error!", ex);
				}
			}
		} catch (IOException ex) {
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
		} catch (ParseException ex) {
			throw new IllegalStateException("Can not parse start arguments. "
					+ ex.getMessage());
		}

		try {
			if (cmd.hasOption("c")) {
				File configFile = new File(cmd.getOptionValue("c"));
				if (configFile.isFile() && configFile.canRead()) {
					Configuration.create(configFile);
				}
			} else {
				// load default configuration
				InputStream is = WebServer.class
						.getResourceAsStream("server.conf.default");
				Configuration.create(is);
			}

			Log.createInstance();
			Log.info("loaded server configuration");
			Log.debug(Configuration.getInstance().toString());
		} catch (Exception ex) {
			System.err.println("Can not load server configuration file.");
		}
	}
}
