package de.th.wildau.dsc.sne.webserver;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXB;

/**
 * WebServer Configuration Singleton.
 */
final class Configuration {

	private static Configuration INSTANCE;

	private final ConfigurationFile configFile;

	private Configuration(ConfigurationFile configurationFile) {

		this.configFile = configurationFile;
	}

	private Configuration(File configFile) {

		this(JAXB.unmarshal(configFile, ConfigurationFile.class));
	}

	public static synchronized void createInstance(File configFile) {

		if (INSTANCE == null) {
			INSTANCE = new Configuration(configFile);
		}
	}

	public static synchronized void createInstance() {

		if (INSTANCE == null) {
			List<String> dirListing = new ArrayList<String>();
			dirListing.add("index.html");
			dirListing.add("index.htm");

			ConfigurationFile configFile = new ConfigurationFile();
			configFile.setDirectoryIndex(dirListing);
			configFile.setLogLevel("info");
			configFile.setLogRoot(System.getProperty("user.home"));
			// configFile.setProxyHost(proxyHost);
			// configFile.setProxyPort(proxyPort);
			configFile.setServerName("Webserver by dsc and sne");
			configFile.setServerPort(1337);
			configFile.setWebRoot(System.getProperty("user.home"));

			INSTANCE = new Configuration(configFile);
		}
	}

	protected static Configuration getInstance() {

		if (INSTANCE == null) {
			throw new IllegalStateException(
					"Configuration isn't loaded. Please call createInstance()");
		}
		return INSTANCE;
	}

	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder("Configuration [");
		sb.append(this.configFile.toString());
		return sb.append("]").toString();
	}

	/**
	 * Getter for the ConfigurationFile
	 * 
	 * @return {@link ConfigurationFile}
	 */
	protected static final ConfigurationFile getConfig() {
		return getInstance().configFile;
	}
}
