package de.th.wildau.dsc.sne.webserver;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "servernode")
public class ConfigurationFile {

	@XmlElement(name = "name", required = true)
	private String serverName;
	@XmlElement(name = "port", required = true)
	private int serverPort;
	@XmlElement(name = "webroot", required = true)
	private String webRoot;
	@XmlElement(name = "logroot", required = true)
	private String logRoot;
	@XmlElement(name = "loglevel", required = true)
	private String logLevel;

	// @XmlElementWrapper(name = "directoryIndex", required = false)
	// @XmlElement(name = "item")
	// private List<String> directoryIndex = new ArrayList<String>();

	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder("ConfigurationFile [");
		sb.append("serverName:").append(this.serverName);
		sb.append(" serverPort:").append(this.serverPort);
		sb.append(" webRoot:").append(this.webRoot);
		sb.append(" logRoot:").append(this.logRoot);
		sb.append(" logLevel:").append(this.logLevel);
		// sb.append(" directoryIndex:").append(Arrays.toString(this.directoryIndex.toArray()));
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

	// public List<String> getDirectoryIndex() {
	// return this.directoryIndex;
	// }
	//
	// public void setDirectoryIndex(List<String> directoryIndex) {
	// this.directoryIndex = directoryIndex;
	// }
}
