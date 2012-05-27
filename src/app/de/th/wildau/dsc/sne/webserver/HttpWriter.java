package de.th.wildau.dsc.sne.webserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;

class HttpWriter {

	private final int httpStatusCode;

	/**
	 * TODO javadoc
	 * 
	 * @param httpCode
	 */
	protected HttpWriter(int httpCode) {

		this.httpStatusCode = httpCode;
	}

	/**
	 * TODO javadoc
	 * 
	 * @param outputStream
	 * @param requestResource
	 */
	protected void write(OutputStream outputStream, File requestResource) {

		switch (this.httpStatusCode) {
		case 200:
			Log.debug("HTTP success 200 - OK");
			break;
		case 403:
			Log.debug("HTTP error 403 - Forbidden");
			break;
		case 404:
			Log.debug("HTTP error 404 - Not Found");
			break;
		default:
			Log.fatal("Invalid http status code: " + this.httpStatusCode);
			break;
		}

		Log.debug("ContextType: " + getContentType(requestResource));

		// generate and append the response
		String tempBody = generateBody(outputStream, requestResource);
		String response = generateHeader(tempBody, requestResource) + tempBody;

		try {
			outputStream.write(response.getBytes());
			outputStream.flush();
		} catch (IOException ex) {
			Log.error("Can not write response / output stream! ",
					ex.getMessage());
		}
	}

	/**
	 * Internal help method, which generate the http header.
	 * 
	 * @param body
	 * @param requestResource
	 * @return String http header
	 */
	private String generateHeader(String body, File requestResource) {

		String header = new String();

		switch (this.httpStatusCode) {
		case 200:
			header += "HTTP/1.1 200 OK" + getLineBreak();
			// XXX
			if (!requestResource.isDirectory()) {
				header += "Content-Type: " + getContentType(requestResource)
						+ getLineBreak();
			} else {
				header += "Content-Type: text/html" + getLineBreak();
			}
			header += "Content-Length: " + requestResource.length()
					+ getLineBreak();
			break;
		case 403:
			// TODO
			/*
			 * <html><body>Forbidden<br /> You don't have permission to access
			 * /foo/bar/ on this server.</body></html>
			 */
			break;
		case 404:
			header += "HTTP/1.1 404 File Not Found" + getLineBreak()
					+ "Content-Type: text/html" + getLineBreak()
					+ "Content-Length: " + body.length();
			break;
		default:
			// TODO 500 Internal Server Error
			body += "<h1>500 Internal Server Error</h1>";
			break;
		}

		// add empty line
		header += getLineBreak() + getLineBreak();

		return header;
	}

	/**
	 * Internal help method, which generate the http body.
	 * 
	 * @param outputStream
	 * @param requestResource
	 * @return http body string
	 */
	private String generateBody(OutputStream outputStream, File requestResource) {

		String body = new String();

		switch (this.httpStatusCode) {
		case 200:

			if (requestResource.isFile()) {
				if (getContentType(requestResource).startsWith("image")) {
					try {
						sendBytes(new FileInputStream(requestResource),
								outputStream);
					} catch (FileNotFoundException ex) {
						ex.printStackTrace();
					}
				} else {
					BufferedReader bufferedReader = null;
					try {
						bufferedReader = new BufferedReader(
								new InputStreamReader(new FileInputStream(
										requestResource)));
						String strLine;
						while ((strLine = bufferedReader.readLine()) != null) {
							body += strLine;
						}
						bufferedReader.close();
					} catch (IOException ex) {
						Log.error("Can not read file.", ex.getMessage());
					} finally {
						if (bufferedReader != null) {
							// XXX bufferedReader.close();
						}
					}
				}
			} else if (requestResource.isDirectory()) {
				body += "<html><body><ul>";
				for (File file : requestResource
						.listFiles(new HiddenFileFilter())) {
					body += "<li><a href=\"" + file.getName() + "\">"
							+ file.getName() + "</a></li>";
				}
				body += "</ul></body></html>";
			}
			break;
		case 404:
		default:
			// XXX good solution?
			body += "<html><body>";
			body += "<h1>Error 404</h1><h2>File Not Found.</h2>";
			body += "</body></html>";
			break;
		}

		return body;
	}

	private String getLineBreak() {

		// XXX return System.getProperty("line.separator");
		return "\r\n";
	}

	@Deprecated
	private void sendBytes(FileInputStream fis, OutputStream os) {
		byte[] buffer = new byte[1024];
		int bytes = 0;

		try {
			// copy requested file into the socket's output stream.
			while ((bytes = fis.read(buffer)) != -1) {
				os.write(buffer, 0, bytes);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Help method, which find the content type of the request resource file.
	 * 
	 * @param requestResource
	 * @return content type
	 */
	private String getContentType(File requestResource) {

		try {
			if (requestResource.isFile()) {
				return requestResource.toURI().toURL().openConnection()
						.getContentType();
			}
		} catch (MalformedURLException ex) {
			Log.warn(ex.getMessage());
		} catch (IOException ex) {
			Log.warn(ex.getMessage());
		}
		// unknown content type - browser handle it
		return "application/octet-stream";
	}

	protected int getHttpCode() {
		return this.httpStatusCode;
	}
}
