package de.th.wildau.dsc.sne.webserver;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXB;

/**
 * WebServer Configuration Singelton.
 * 
 * @author dsc and sne
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
		sb.append(this.configFile.toString());
		return sb.append("]").toString();
	}

	/**
	 * TODO javadoc
	 * 
	 * @return
	 */
	protected static final ConfigurationFile getConfig() {
		return getInstance().configFile;
	}
}
