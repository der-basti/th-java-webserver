package de.th.wildau.dsc.sne.webserver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * WebServer Configuration.
 * 
 * @author dsc and sne
 */
public final class Configuration {

	private String serverName;
	private int port;
	private File webRoot;
	private File logRoot;
	private LogLevel logLevel;
	private String logFilePattern;
	private String logLinePattern;
	private List<String> directoryIndex;
	
	private Log log;

	/**
	 * TODO javadoc
	 * 
	 * @param configFile
	 */
	public Configuration(File configFile) {

		this.directoryIndex = new ArrayList<String>();
		parseAndSet(configFile);
		this.log = new Log(this);
	}
	
	// FIXME [sne] summarize the constructor

	public Configuration(InputStream inputStream) {
		// TODO Auto-generated constructor stub

		File temp = null;
		try {
			temp = File.createTempFile("server.conf", ".default");
			temp.deleteOnExit();
			OutputStream out = new FileOutputStream(temp);
			int read = 0;
			byte[] bytes = new byte[1024];
			while ((read = inputStream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			inputStream.close();
			out.flush();
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (temp == null) {
			throw new IllegalStateException("Can not load configuration file.");
		}

		parseAndSet(temp);
		this.log = new Log(this);
	}

	private void parseAndSet(File configFile) {
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory
					.newDocumentBuilder();
			Document doc = documentBuilder.parse(configFile);
			doc.getDocumentElement().normalize();

			if ("server-node" != doc.getDocumentElement().getNodeName()) {
				throw new IllegalArgumentException(
						"Missing server-node element.");
			}

			NodeList nodeList = doc.getElementsByTagName("server-node");
			Node node = nodeList.item(0);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				// Element e = (Element) node;
				this.serverName = getTagValue(node, "name");
				this.port = Integer.parseInt(getTagValue(node, "port"));
				this.webRoot = new File(getTagValue(node, "web-root"));
				this.logRoot = new File(getTagValue(node, "log-root"));
				this.logLevel = LogLevel.valueOf(getTagValue(node, "log-level")
						.toUpperCase());
				this.logFilePattern = getTagValue(node, "log-file-pattern");
				this.logLinePattern = getTagValue(node, "log-line-pattern");
				// FIXME directoryIndex
				// NodeList nodes = doc.getElementsByTagName("directoryIndex");
				// for (int s = 0; s < nodes.getLength(); s++) {
				// Node iNode = nodes.item(s);
				// System.out.println(iNode.getNodeValue());
				// this.directoryIndex.add(iNode.getNodeValue());
				// }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getTagValue(Node node, String tagName) {
		return ((Element) node).getElementsByTagName(tagName).item(0)
				.getChildNodes().item(0).getNodeValue();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Configuration [");
		sb.append("serverName: ").append(this.serverName).append("; ");
		sb.append("port: ").append(this.port).append("; ");
		sb.append("webRoot: ").append(this.webRoot.getAbsolutePath())
				.append("; ");
		sb.append("logRoot: ").append(this.logRoot.getAbsolutePath())
				.append("; ");
		sb.append("logLevel: ").append(this.logLevel).append("; ");
		sb.append("logFilePattern: ").append(this.logFilePattern).append("; ");
		sb.append("logLinePattern: ").append(this.logLinePattern);
		// sb.append("directoryIndex: ").append(
		// Arrays.toString(this.directoryIndex.toArray()));
		return sb.append("]").toString();
	}

	/**
	 * TODO javadoc
	 * 
	 * @return String serverName
	 */
	public final String getServerName() {
		return this.serverName;
	}

	/**
	 * TODO javadoc
	 * 
	 * @return port
	 */
	public final int getPort() {
		return this.port;
	}

	/**
	 * TODO javadoc
	 * 
	 * @return File webRoot
	 */
	public final File getWebRoot() {
		return this.webRoot;
	}

	/**
	 * TODO javadoc
	 * 
	 * @return File logRoot
	 */
	public final File getLogRoot() {
		return this.logRoot;
	}

	/**
	 * TODO javadoc
	 * 
	 * @return LogLevel
	 */
	public final LogLevel getLogLevel() {
		return this.logLevel;
	}

	/**
	 * TODO javadoc
	 * 
	 * @return String logFilePattern
	 */
	public final String getLogFilePattern() {
		return logFilePattern;
	}

	/**
	 * TODO javadoc
	 * 
	 * @return String logLinePattern
	 */
	public final String getLogLinePattern() {
		return logLinePattern;
	}

	/**
	 * TODO javadoc
	 * 
	 * @return List<String> directoryIndex
	 */
	public final List<String> getDirectoryIndex() {
		return Collections.unmodifiableList(this.directoryIndex);
	}

	protected Log getLog() {
		return log;
	}
}
