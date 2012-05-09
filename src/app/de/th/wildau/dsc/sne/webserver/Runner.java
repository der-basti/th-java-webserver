package de.th.wildau.dsc.sne.webserver;

import java.io.File;
import java.io.InputStream;

/**
 * Web server start class.
 * 
 * @author dsc and sne
 */
public final class Runner {

	private static Configuration config;

	/**
	 * The main method for the web server program.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		// load configuration, DON'T LOG BEFORE THE CONFIGURATION!
		if (args.length == 2 && args[0].equals("-c")) {
			File configFile = new File(args[1]);
			if (configFile.isFile() && configFile.canRead()) {
				config = new Configuration(configFile);
			} else {
				throw new IllegalArgumentException(
						"use option -c /path/to/server.conf"); // FIXME correct notation
			}
		} else if (args.length == 0) {
			InputStream is = Runner.class
					.getResourceAsStream("server.conf.default");
			config = new Configuration(is);
		} else {
			throw new IllegalArgumentException(
					"Illegal start arguments. See help.");
		}

		config.getLog().info("load server configuration");
		config.getLog().debug(config.toString());

		config.getLog().debug("instantiate web server");
		WebServer webServer = new WebServer(config);
		webServer.setPriority(Thread.MAX_PRIORITY);
		webServer.start();
	}
}
