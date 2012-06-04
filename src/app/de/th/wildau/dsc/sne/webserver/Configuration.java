package de.th.wildau.dsc.sne.webserver;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.JAXB;

/**
 * WebServer Configuration Singelton.
 * 
 * @author dsc and sne
 */
final class Configuration {

	private static Configuration INSTANCE;

	private final String serverName;
	private final int port;
	private final File webRoot;
	private final File logRoot;
	private final LogLevel logLevel;
	private final List<String> directoryIndex;

	private Configuration(ConfigurationFile configurationFile) {

		this.serverName = configurationFile.getServerName();
		this.port = configurationFile.getServerPort();
		this.webRoot = new File(configurationFile.getWebRoot());
		this.logRoot = new File(configurationFile.getLogRoot());
		this.logLevel = LogLevel.valueOf(configurationFile.getLogLevel()
				.toUpperCase());
		List<String> tempList = new ArrayList<String>();
		for (String str : configurationFile.getDirectoryIndex()) {
			tempList.add(str);
		}
		this.directoryIndex = Collections.unmodifiableList(tempList);
	}

	private Configuration(File configFile) {

		this(JAXB.unmarshal(configFile, ConfigurationFile.class));
	}

	public static synchronized void createInstance(File configFile) {

		if (INSTANCE == null) {
			INSTANCE = new Configuration(configFile);
		}
	}

	public static synchronized void createInstance(InputStream inputStream) {

		if (INSTANCE == null) {
			try {
				File temp = File.createTempFile("server.conf", ".default");
				temp.deleteOnExit();
				createInstance(temp);
			} catch (final IOException ex) {
				throw new IllegalStateException(
						"Can not load default configuration. "
								+ ex.getMessage());
			}
		}
	}

	protected static Configuration getInstance() {

		if (INSTANCE == null) {
			throw new IllegalStateException(
					"No configuration are loaded. Please call createInstance()");
		}
		return INSTANCE;
	}

	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder("Configuration [");
		sb.append("server-name: ").append(getInstance().serverName)
				.append("; ");
		sb.append("server-port: ").append(getInstance().port).append("; ");
		sb.append("web-root: ").append(getInstance().webRoot.getAbsolutePath())
				.append("; ");
		sb.append("log-root: ").append(getInstance().logRoot.getAbsolutePath())
				.append("; ");
		sb.append("log-level: ").append(getInstance().logLevel).append("; ");
		sb.append("directory-index: ").append(
				Arrays.toString(getInstance().directoryIndex.toArray()));
		return sb.append("]").toString();
	}

	/**
	 * TODO javadoc
	 * 
	 * @return String serverName
	 */
	public static final String getServerName() {
		return getInstance().serverName;
	}

	/**
	 * TODO javadoc
	 * 
	 * @return port
	 */
	public static final int getPort() {
		return getInstance().port;
	}

	/**
	 * TODO javadoc
	 * 
	 * @return File webRoot
	 */
	public static final File getWebRoot() {
		return getInstance().webRoot;
	}

	/**
	 * TODO javadoc
	 * 
	 * @return File logRoot
	 */
	public static final File getLogRoot() {
		return getInstance().logRoot;
	}

	/**
	 * TODO javadoc
	 * 
	 * @return LogLevel
	 */
	public static final LogLevel getLogLevel() {
		return getInstance().logLevel;
	}

	/**
	 * TODO javadoc
	 * 
	 * @return List<String> directoryIndex
	 */
	public static final List<String> getDirectoryIndex() {
		return Collections.unmodifiableList(getInstance().directoryIndex);
	}
}
