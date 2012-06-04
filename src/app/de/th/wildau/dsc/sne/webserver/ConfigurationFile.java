package de.th.wildau.dsc.sne.webserver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * JAXB file for the server configuration.
 * 
 * @author sne
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "server-node")
public class ConfigurationFile {

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
	// XXX proxy config
	// XXX http status pages
	// XXX gzip
	@XmlElementWrapper(name = "directory-index", required = false)
	@XmlElement(name = "item")
	private List<String> directoryIndex = new ArrayList<String>();

	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder("ConfigurationFile [");
		sb.append("server-name:").append(this.serverName);
		sb.append(" server-port:").append(this.serverPort);
		sb.append(" web-root:").append(this.webRoot);
		sb.append(" log-root:").append(this.logRoot);
		sb.append(" log-level:").append(this.logLevel);
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

	public void setWebRoot(String webRoot) {
		this.webRoot = webRoot;
	}

	public String getLogRoot() {
		return this.logRoot;
	}

	public void setLogRoot(String logRoot) {
		this.logRoot = logRoot;
	}

	public String getLogLevel() {
		return this.logLevel;
	}

	public void setLogLevel(String logLevel) {
		this.logLevel = logLevel;
	}

	public List<String> getDirectoryIndex() {
		return this.directoryIndex;
	}

	public void setDirectoryIndex(List<String> directoryIndex) {
		this.directoryIndex = directoryIndex;
	}
}
