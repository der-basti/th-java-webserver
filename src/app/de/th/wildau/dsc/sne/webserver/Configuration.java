package de.th.wildau.dsc.sne.webserver;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.JAXB;

/**
 * WebServer Configuration.
 * 
 * @author dsc and sne
 */
final class Configuration {

	private static Configuration instance;

	private static String serverName;
	private static int port;
	private static File webRoot;
	private static File logRoot;
	private static LogLevel logLevel;
	private static List<String> directoryIndex = new ArrayList<String>();

	private Configuration(ConfigurationFile configurationFile) {

		serverName = configurationFile.getServerName();
		port = configurationFile.getServerPort();
		webRoot = new File(configurationFile.getWebRoot());
		logRoot = new File(configurationFile.getLogRoot());
		logLevel = LogLevel.valueOf(configurationFile.getLogLevel()
				.toUpperCase());
		// for (String str : configurationFile.getDirectoryIndex()) {
		// directoryIndex.add(str);
		// }
	}

	private Configuration(File configFile) {

		this(JAXB.unmarshal(configFile, ConfigurationFile.class));
	}

	public static void create(File configFile) {
		instance = new Configuration(configFile);
	}

	public static void create(InputStream inputStream) {

		try {
			File temp = File.createTempFile("server.conf", ".default");
			temp.deleteOnExit();
			instance = new Configuration(temp);
		} catch (IOException ex) {
			throw new IllegalStateException(
					"Can not load default configuration. " + ex.getMessage());
		}
	}

	protected static Configuration getInstance() {
		return instance;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Configuration [");
		sb.append("serverName: ").append(serverName).append("; ");
		sb.append("port: ").append(port).append("; ");
		sb.append("webRoot: ").append(webRoot.getAbsolutePath()).append("; ");
		sb.append("logRoot: ").append(logRoot.getAbsolutePath()).append("; ");
		sb.append("logLevel: ").append(logLevel).append("; ");
		// sb.append("directoryIndex: ").append(
		// Arrays.toString(this.directoryIndex.toArray()));
		return sb.append("]").toString();
	}

	/**
	 * TODO javadoc
	 * 
	 * @return String serverName
	 */
	public static final String getServerName() {
		return serverName;
	}

	/**
	 * TODO javadoc
	 * 
	 * @return port
	 */
	public static final int getPort() {
		return port;
	}

	/**
	 * TODO javadoc
	 * 
	 * @return File webRoot
	 */
	public static final File getWebRoot() {
		return webRoot;
	}

	/**
	 * TODO javadoc
	 * 
	 * @return File logRoot
	 */
	public static final File getLogRoot() {
		return logRoot;
	}

	/**
	 * TODO javadoc
	 * 
	 * @return LogLevel
	 */
	public static final LogLevel getLogLevel() {
		return logLevel;
	}

	/**
	 * TODO javadoc
	 * 
	 * @return List<String> directoryIndex
	 */
	public static final List<String> getDirectoryIndex() {
		return Collections.unmodifiableList(directoryIndex);
	}
}
