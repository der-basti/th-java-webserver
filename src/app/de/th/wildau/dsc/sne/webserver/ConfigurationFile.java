package de.th.wildau.dsc.sne.webserver;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * JAXB file for the server configuration.
 * 
 * @author David Schwertfeger [dsc] & Sebastian Nemak [sne]
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "server-node")
public class ConfigurationFile implements Cloneable {

	@XmlElement(name = "server-name", required = true)
	private String serverName;
	@XmlElement(name = "server-port", required = true)
	private int serverPort;
	@XmlElement(name = "web-root", required = true)
	private String webRoot;
	@XmlElement(name = "log-root", required = true)
	private String logRoot;
	@XmlElement(name = "log-level", required = true)
	private String logLevel;
	@XmlElement(name = "proxy-host", required = false)
	private String proxyHost;
	@XmlElement(name = "proxy-port", required = false)
	private int proxyPort;
	// XXX http status pages
	// XXX gzip
	@XmlElementWrapper(name = "directory-index", required = false)
	@XmlElement(name = "item")
	private List<String> directoryIndex = new ArrayList<String>();

	@Override
	protected ConfigurationFile clone() {

		ConfigurationFile configurationFile = new ConfigurationFile();
		configurationFile.setWebRoot(this.webRoot);
		configurationFile.setServerPort(this.serverPort);
		configurationFile.setServerName(this.serverName);
		configurationFile.setProxyPort(this.proxyPort);
		configurationFile.setProxyHost(this.proxyHost);
		configurationFile.setLogRoot(this.logRoot);
		configurationFile.setLogLevel(this.logLevel);
		configurationFile.setDirectoryIndex(Collections
				.unmodifiableList(this.directoryIndex));
		return configurationFile;
	}

	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder("ConfigurationFile [");
		sb.append("server-name:").append(this.serverName);
		sb.append(" server-port:").append(this.serverPort);
		sb.append(" web-root:").append(this.webRoot);
		sb.append(" log-root:").append(this.logRoot);
		sb.append(" log-level:").append(this.logLevel);
		sb.append(" proxy-host:").append(this.proxyHost);
		sb.append(" proxy-port:").append(this.proxyPort);
		sb.append(" directory-index:").append(
				Arrays.toString(this.directoryIndex.toArray()));
		return sb.append("]").toString();
	}

	public String getServerName() {
		return this.serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public int getServerPort() {
		return this.serverPort;
	}

	public void setServerPort(int port) {
		this.serverPort = port;
	}

	public String getWebRoot() {
		return this.webRoot;
	}

	public File getWebRootFile() {
		return new File(this.webRoot);
	}

	public void setWebRoot(String webRoot) {
		this.webRoot = webRoot;
	}

	public String getLogRoot() {
		return this.logRoot;
	}

	public File getLogRootFile() {
		return new File(this.logRoot);
	}

	public void setLogRoot(String logRoot) {
		this.logRoot = logRoot;
	}

	public String getLogLevel() {
		return this.logLevel;
	}

	public LogLevel getLogLevelEnum() {
		return LogLevel.valueOf(this.logLevel.toUpperCase());
	}

	public void setLogLevel(String logLevel) {
		this.logLevel = logLevel;
	}

	public final String getProxyHost() {
		return proxyHost;
	}

	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}

	public int getProxyPort() {
		return proxyPort;
	}

	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
	}

	public List<String> getDirectoryIndex() {
		return this.directoryIndex;
	}

	public void setDirectoryIndex(List<String> directoryIndex) {
		this.directoryIndex = directoryIndex;
	}
}
