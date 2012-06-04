package de.th.wildau.dsc.sne.webserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

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
	try {
		File bodyFile = generateBody(outputStream, requestResource);
		outputStream.write(generateHeader(bodyFile.length(),
				requestResource).getBytes());
		outputStream.write(getByteArray(bodyFile));
		outputStream.flush();
	} catch (final IOException ex) {
		Log.error("Can not write response / output stream! ", ex);
	}
}

private static byte[] getByteArray(File file) throws IOException,
		UnsupportedEncodingException {

	FileInputStream fileInputStream = new FileInputStream(file);
	byte[] data = new byte[(int) file.length()];
	fileInputStream.read(data);
	fileInputStream.close();
	return new String(data, "UTF-8").getBytes();
}

	/**
	 * Internal help method, which generate the http header.
	 * 
	 * @param body
	 * @param requestResource
	 * @return String http header
	 */
	private String generateHeader(String body, File requestResource) {

		// TODO [dsc]
		String header = new String();

		switch (this.httpStatusCode) {
		case 200:
			header += "HTTP/1.1 200 OK" + getLineBreak();
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
			// TODO [dsc] implement 403 forbidden header
			break;
		case 404:
			header += "HTTP/1.1 404 File Not Found" + getLineBreak()
					+ "Content-Type: text/html" + getLineBreak()
					+ "Content-Length: " + body.length();
			break;
		default:
			// TODO [dsc] implement 500 Internal Server Error body
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
	private File generateBody(OutputStream outputStream, File requestResource) {

		// FIXME [dsc] [sne] void method and send 'file' direct

		File tempFile;
		PrintWriter tempFilePrintWriter = new PrintWriter(new BufferedWriter(
				new FileWriter(tempFile)));

		switch (this.httpStatusCode) {
		case 200:

			if (requestResource.isFile()) {

				// return the file (direct)
				if (getContentType(requestResource).startsWith("image")) {
					try {

						// XXX TEST
						if (HttpCache.getInstance().contains(
								"" + requestResource.hashCode())) {

							byte[] buffer = new byte[1024];
							int bytes = 0;
							try {
								FileInputStream fis = new FileInputStream(
										requestResource);
								while ((bytes = fis.read(buffer)) != -1) {
									outputStream.write(buffer, 0, bytes);
								}
							} catch (final Exception ex) {
								Log.error("Can not read file.", ex);
							}
						} else {
							// add to cache
							List<Integer> tempList = new ArrayList<Integer>();
							byte[] buffer = new byte[1024];
							int bytes = 0;
							try {
								FileInputStream fis = new FileInputStream(
										requestResource);
								while ((bytes = fis.read(buffer)) != -1) {
									tempList.add(bytes);
								}
							} catch (final Exception ex) {
								Log.error("Can not read file.", ex);
							}

							int[] intArray = new int[tempList.size()];
							int i = 0;
							for (Integer e : tempList) {
								intArray[i++] = e.intValue();
							}

							HttpCache.getInstance().put(
									"" + requestResource.hashCode(), intArray);
						}
						// XXX TEST

						sendBytes(new FileInputStream(requestResource),
								outputStream);
					} catch (final FileNotFoundException ex) {
						ex.printStackTrace();
					}
				} else {
					// handle script files
					for (ScriptLanguage scriptLanguage : WebServer.supportedScriptLanguages) {
						if (requestResource.getName().toLowerCase()
								.endsWith(scriptLanguage.getFileExtension())) {
							return new ScriptExecutor().execute(scriptLanguage,
									requestResource);
						}
					}
					// read the file and return it
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
					} catch (final IOException ex) {
						Log.error("Can not read file.", ex);
					} finally {
						if (bufferedReader != null) {
							try {
								bufferedReader.close();
							} catch (final IOException ex) {
								Log.error(
										"Can not close the request resource file.",
										ex);
							}
						}
					}
				}
			} else if (requestResource.isDirectory()
					&& requestResource.canRead()) {

				File.createTempFile("directorylisting", "html");
				tempFile.deleteOnExit();
				tempFilePrintWriter.print("<html><body><ul>");
				for (File file : requestResource.listFiles(new HiddenFilter())) {
					// FIXME [dsc] [sne] case sub directories
					tempFilePrintWriter.print("<li><a href=\"" + file.getName()
							+ "\">" + file.getName() + "</a></li>");
				}
				tempFilePrintWriter.print("</ul></body></html>");
			}
			break;
		case 403:
			tempFile = new File(WebServer.class.getClassLoader().getResource("403.html").toURI());
			return tempFile;
			break;
		case 404:
			tempFile = new File(WebServer.class.getClassLoader().getResource("404.html").toURI());
			return tempFile;
			break;
		case 500:
			tempFile = new File(WebServer.class.getClassLoader().getResource("500.html").toURI());
			return tempFile;
			break;
		default:
			throw new IllegalStateException("Invalid http status code.");
		}
		tempFilePrintWriter.flush();
		return tempFile;
	}
	
	
	

	private String getLineBreak() {

		// XXX [dsc] check win, lin, mac default line separator
		// return System.getProperty("line.separator");
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
		} catch (final Exception ex) {
			Log.error("Can not read file.", ex);
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
		} catch (final MalformedURLException ex) {
			Log.warn(ex.getMessage());
		} catch (final IOException ex) {
			Log.warn(ex.getMessage());
		}
		// unknown content type - browser handle it
		return "application/octet-stream";
	}

	protected int getHttpCode() {
		return this.httpStatusCode;
	}
}