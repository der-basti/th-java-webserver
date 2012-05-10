package de.th.wildau.dsc.sne.webserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * TODO javadoc
 */
public class HttpHandler extends Thread {

	private Socket serverSocket;
	private InputStream inputStream;
	private OutputStream outputStream;
	private Configuration config;
	private Log log;

	public HttpHandler(Configuration config, Socket serverSocket)
			throws IOException {

		this.serverSocket = serverSocket;
		this.inputStream = this.serverSocket.getInputStream();
		this.outputStream = this.serverSocket.getOutputStream();
		this.config = config;
		this.log = this.config.getLog();
		this.start();
	}

	@Override
	public void run() {

		try {
			String line;
			List<String> requestList = new ArrayList<String>();
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(this.inputStream));
			PrintWriter printWriter = new PrintWriter(this.outputStream);

			while ((line = bufferedReader.readLine()) != null) {
				requestList.add(line);
				// System.out.println("request> " + line);
			}

			// ...
			// XXX write every line separate?
			printWriter.print(parseRequest(requestList));
			// printWriter.print(getResponseBody());

			bufferedReader.close();
			this.inputStream.close();
			this.outputStream.close();
			this.serverSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Support only GET requests.
	 * 
	 * @param requestList
	 */
	private String parseRequest(List<String> requestList) {

		for (String line : requestList) {
			if (Pattern.matches(line, "^GET * HTTP/1.1")) {
				File resource = findResource(line.split(" ")[1]);
				this.log.debug("find resource " + resource.getAbsolutePath());
				return generateHttpHeader(resource);
			} else if (Pattern.matches(line, "^Host: ")) {
				// XXX config.getServerName();
				//this.log.debug();
			} else if (Pattern.matches(line, "")) {
				this.log.debug("read request empty line");
			} else {
				this.log.warn("can not parse client request: " + line);
			}
		}
		this.log.warn("Return HTTP code 404");
		return generateHttpHeader(null);
	}

	private File findResource(String findResource) {
		// TODO Auto-generated method stub
		return null;
	}

	private String generateHttpHeader(File resource) {

		this.log.debug("generate http header");
		StringBuilder sb = new StringBuilder("HTTP/1.1 ");

		if (resource == null) {
			this.log.debug("http status: 404 not found");
			sb.append("404 Not Found").append(getBr());
			sb.append("Server: ").append(this.config.getServerName());
			sb.append(getEmptyLine());
		} else if (resource.exists() && resource.canRead()) {
			this.log.debug("http status: 200 ok");
			/*
			 * HTTP/1.1 200 OK Server: MyServer Content-Length: (Größe von
			 * infotext.html in Byte) Content-Language: de (nach RFC 3282 sowie
			 * RFC 1766) Connection: close Content-Type: text/html (Leerzeile)
			 * (Inhalt von infotext.html)
			 */
			sb.append("200 OK").append(getBr());
			sb.append("Server: ").append(this.config.getServerName());

			sb.append(getEmptyLine());
			sb.append(getResourceInput(resource));
		}

		return sb.toString();
	}

	private Object getResourceInput(File resource) {
		// TODO Auto-generated method stub
		return null;
	}

	private String getBr() {

		return System.getProperty("line.separator");
	}

	private String getEmptyLine() {

		return getBr() + getBr();
	}
}
