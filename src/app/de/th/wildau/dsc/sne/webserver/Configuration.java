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

	private static String serverName;
	private static int port;
	private static File webRoot;
	private static File logRoot;
	private static LogLevel logLevel;
	private static List<String> directoryIndex;

	/**
	 * TODO javadoc
	 * 
	 * @param configFile
	 */
	protected Configuration(File configFile) {

		directoryIndex = new ArrayList<String>();
		parseAndSet(configFile);
	}

	// FIXME [sne] summarize the constructor

	protected Configuration(InputStream inputStream) {

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
	}

	public static void create(File configFile) {
		new Configuration(configFile);
	}

	public static void create(InputStream inputStream) {
		new Configuration(inputStream);
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
				serverName = getTagValue(node, "name");
				port = Integer.parseInt(getTagValue(node, "port"));
				webRoot = new File(getTagValue(node, "web-root"));
				logRoot = new File(getTagValue(node, "log-root"));
				logLevel = LogLevel.valueOf(getTagValue(node, "log-level")
						.toUpperCase());
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
