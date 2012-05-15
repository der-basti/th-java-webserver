package de.th.wildau.dsc.sne.webserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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

	public HttpHandler(Socket serverSocket) throws IOException {

		this.serverSocket = serverSocket;
		this.inputStream = this.serverSocket.getInputStream();
		this.outputStream = this.serverSocket.getOutputStream();
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
			}

			// ...
			String aw = parseRequest(requestList);
			printWriter.append(aw);
			printWriter.println(aw);
			printWriter.write(aw);
			// printWriter.print(getResponseBody());
			// XXX write every line separate?

			printWriter.flush();
			printWriter.close();
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

		// TODO implement
		File resource = null;
		for (String line : requestList) {
			Log.debug(">> " + line);
			if (line.startsWith("GET /")) { // ^GET /.* HTTP\\/1.1$
				resource = findResource(line.split(" ")[1]);
			} else if (Pattern.matches(line, "^Host: ")) {
				// Log.debug("request host: " + line.split(" ")[1]);
			} else if (Pattern.matches(line, "")) {
				// Log.debug("request empty line");
			} else {
				// Log.debug("can not parse client request: " + line);
			}
		}
		return generateHttpHeader(resource);
	}

	private File findResource(String resource) {

		Log.debug("finding resource: " + resource);

		File x = new File(Configuration.getWebRoot() + resource);
		if (x.isDirectory()) {
			// TODO find index...
		} else if (x.canRead()) {
			Log.error("can not read file: " + x.getAbsolutePath());
		}
		Log.debug("find resource: " + x);
		return x;
	}

	private String generateHttpHeader(File resource) {

		Log.debug("generate http header");
		StringBuilder sb = new StringBuilder("HTTP/1.1 ");

		if (resource == null) {
			Log.info("http status: 404 not found");
			
			sb.append("404 Not Found").append(getBr());
			sb.append("Server: ").append(Configuration.getServerName());
			sb.append(getBr());
		} else if (resource.exists() && resource.canRead()) {
			Log.debug("http status: 200 ok");
			/*
			 * HTTP/1.1 200 OK Server: MyServer Content-Length: (Größe von
			 * infotext.html in Byte) Content-Language: de (nach RFC 3282 sowie
			 * RFC 1766) Connection: close Content-Type: text/html (Leerzeile)
			 * (Inhalt von infotext.html)
			 */
			sb.append("200 OK").append(getBr());
			sb.append("Server: ").append(Configuration.getServerName()).append(getBr());;
			sb.append("Content-Length: " + resource.length()).append(getBr());;
			sb.append("Content-Language: de").append(getBr());; // TODO ...
			sb.append("Connection: close").append(getBr());;
			sb.append("Content-Type: text/html").append(getBr());;// TODO ...
			sb.append(getBr());

			try {
				sb.append(getResourceInput(resource));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				sb.append(e.getMessage());
			}
		}
		
		Log.debug("response: " + sb.toString());

		return sb.toString();
	}

	private Object getResourceInput(File resource) throws IOException {

		StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader(new FileReader(resource));
		String line;
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}
		return sb.toString();
	}

	private String getBr() {

		return System.getProperty("line.separator");
	}
}
