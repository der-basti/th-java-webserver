package de.th.wildau.dsc.sne.webserver;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

class HttpWriter {

	private final String ENCODING = "UTF-8";
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

		Log.debug("method... HttpWriter.write()");

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
			byte[] byteArray = getByteArray(bodyFile);
			Log.debug("file length: " + bodyFile.length());
			Log.debug("byte[] length: " + byteArray.length);
			// XXX [sne] check length
			long size = byteArray.length > bodyFile.length() ? byteArray.length
					: bodyFile.length();
			outputStream
					.write(generateHeader(size, requestResource).getBytes());
			outputStream.write(byteArray);
			outputStream.flush();
		} catch (final IOException ex) {
			Log.error("Can not write response / output stream! ", ex);
		} catch (URISyntaxException e) {
			Log.error("Can't read resource from JAR file.", e);
		}
	}

	private byte[] getByteArray(File file) throws IOException,
			UnsupportedEncodingException {

		Log.debug("method... HttpWriter.getByteArray()");

		ScriptLanguage scriptLanguage;
		if ((scriptLanguage = isInterpretedFile(file)) != null) {
			return new ScriptExecutor().execute(scriptLanguage, file)
					.getBytes();
		}

		if (getContentType(file).startsWith("text")) {
			FileInputStream fileInputStream = new FileInputStream(file);
			byte[] data = new byte[(int) file.length()];
			fileInputStream.read(data);
			fileInputStream.close();
			return new String(data, ENCODING).getBytes();
		} else {
			// FIXME [sne] return images
			// XXX image case
			FileInputStream fis = new FileInputStream(file);
			byte[] byteArray = new byte[(int) file.length()];
			fis.read(byteArray);
			fis.close();
			return new String(byteArray, ENCODING).getBytes();

			// FileInputStream fis = new FileInputStream(file);
			// byte[] buffer = new byte[1024];
			// int bytes = 0;
			// long result = 0;
			// try {
			// // copy requested file into the socket's output stream.
			// while ((bytes = fis.read(buffer)) != -1) {
			// //os.write(buffer, 0, bytes);
			// result += buffer.length;
			// }
			// } catch (final Exception ex) {
			// Log.error("Can not read file.", ex);
			// }
		}
	}

	private ScriptLanguage isInterpretedFile(File file) {

		for (ScriptLanguage scriptLanguage : WebServer.supportedScriptLanguages) {
			if (file.getName().toLowerCase()
					.endsWith(scriptLanguage.getFileExtension())) {
				return scriptLanguage;
			}
		}
		return null;
	}

	/**
	 * Internal help method, which generate the http header.
	 * 
	 * @param body
	 * @param requestResource
	 * @return String http header
	 */
	private String generateHeader(long bodyLength, File requestResource) {

		Log.debug("method... HttpWriter.generateHeader()");

		// TODO [dsc]
		String header = new String("HTTP/1.1 ");

		switch (this.httpStatusCode) {
		case 200:
			header += "200 OK" + getLineBreak();
			if (!requestResource.isDirectory()) {
				header += "Content-Type: " + getContentType(requestResource)
						+ getLineBreak();
			} else {
				header += "Content-Type: text/html" + getLineBreak();
			}
			break;
		case 403:
			header += "403 Forbidden" + getLineBreak()
					+ "Content-Type: text/html" + getLineBreak();
			break;
		case 404:
			header += "404 File Not Found" + getLineBreak()
					+ "Content-Type: text/html" + getLineBreak();
			break;
		default:
			header += "500 Internal Server Error" + getLineBreak()
					+ "Content-Type: text/html" + getLineBreak();
					
			break;
		}
		header += "Content-Length: " + bodyLength;

		// add empty line
		header += getLineBreak() + getLineBreak();

		return header;
	}

	private String getLineBreak() {

		// XXX [dsc] check win, lin, mac default line separator
		// return System.getProperty("line.separator");
		return "\r\n";
	}

	/**
	 * Internal help method, which generate the http body.
	 * 
	 * @param outputStream
	 * @param requestResource
	 * @return file
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	private File generateBody(OutputStream outputStream, File requestResource)
			throws IOException, URISyntaxException {

		Log.debug("method... HttpWriter.generateBody()");
		File tempFile = null;

		switch (this.httpStatusCode) {
		case 200:

			if (requestResource.isFile()) {

				// return the file (direct)
				// if (getContentType(requestResource).startsWith("image")) {
				// try {
				//
				// // XXX TEST
				// if (HttpCache.getInstance().contains(
				// "" + requestResource.hashCode())) {
				//
				// byte[] buffer = new byte[1024];
				// int bytes = 0;
				// try {
				// FileInputStream fis = new FileInputStream(
				// requestResource);
				// while ((bytes = fis.read(buffer)) != -1) {
				// outputStream.write(buffer, 0, bytes);
				// }
				// } catch (final Exception ex) {
				// Log.error("Can not read file.", ex);
				// }
				// } else {
				// // add to cache
				// List<Integer> tempList = new ArrayList<Integer>();
				// byte[] buffer = new byte[1024];
				// int bytes = 0;
				// try {
				// FileInputStream fis = new FileInputStream(
				// requestResource);
				// while ((bytes = fis.read(buffer)) != -1) {
				// tempList.add(bytes);
				// }
				// } catch (final Exception ex) {
				// Log.error("Can not read file.", ex);
				// }
				//
				// int[] intArray = new int[tempList.size()];
				// int i = 0;
				// for (Integer e : tempList) {
				// intArray[i++] = e.intValue();
				// }
				//
				// HttpCache.getInstance().put(
				// "" + requestResource.hashCode(), intArray);
				// }
				// // XXX TEST
				//
				// sendBytes(new FileInputStream(requestResource),
				// outputStream);
				// } catch (final FileNotFoundException ex) {
				// ex.printStackTrace();
				// }
				// } else {
				// // handle script files
				// for (ScriptLanguage scriptLanguage :
				// WebServer.supportedScriptLanguages) {
				// if (requestResource.getName().toLowerCase()
				// .endsWith(scriptLanguage.getFileExtension())) {
				// return new ScriptExecutor().execute(scriptLanguage,
				// requestResource);
				// }
				// }
				// // read the file and return it
				// BufferedReader bufferedReader = null;
				// try {
				// bufferedReader = new BufferedReader(
				// new InputStreamReader(new FileInputStream(
				// requestResource)));
				// String strLine;
				// while ((strLine = bufferedReader.readLine()) != null) {
				// body += strLine;
				// }
				// bufferedReader.close();
				// } catch (final IOException ex) {
				// Log.error("Can not read file.", ex);
				// } finally {
				// if (bufferedReader != null) {
				// try {
				// bufferedReader.close();
				// } catch (final IOException ex) {
				// Log.error(
				// "Can not close the request resource file.",
				// ex);
				// }
				// }
				// }
				// }
				tempFile = requestResource;
			} else if (requestResource.isDirectory()
					&& requestResource.canRead()) {

				tempFile = File.createTempFile("directorylisting", ".html");
				tempFile.deleteOnExit();
				PrintWriter tempFilePrintWriter = new PrintWriter(
						new BufferedWriter(new FileWriter(tempFile)));
				tempFilePrintWriter.print("<html><body><ul>");
				for (File file : requestResource.listFiles(new HiddenFilter())) {
					if (file.isDirectory()) {
						tempFilePrintWriter.print("<li><a href=\""
								+ file.getName() + "/\">" + file.getName()
								+ "</a></li>");
					} else if (file.isFile()) {
						tempFilePrintWriter.print("<li><a href=\""
								+ file.getName() + "\">" + file.getName()
								+ "</a></li>");
					}
				}
				tempFilePrintWriter.print("</ul></body></html>");
				tempFilePrintWriter.flush();
				tempFilePrintWriter.close();
			}
			break;
		case 403:
			tempFile = new File(WebServer.class.getClassLoader()
					.getResource("403.html").toURI());
			break;
		case 404:
			tempFile = new File(WebServer.class.getClassLoader()
					.getResource("404.html").toURI());
			break;
		case 500:
			tempFile = new File(WebServer.class.getClassLoader()
					.getResource("500.html").toURI());
			break;
		default:
			throw new IllegalStateException("Invalid http status code.");
		}
		return tempFile;
	}

	@Deprecated
	private long sendBytes(FileInputStream fis, OutputStream os) {

		Log.debug("method... HttpWriter.sendBytes()");

		byte[] buffer = new byte[1024];
		int bytes = 0;
		long result = 0;

		try {
			// copy requested file into the socket's output stream.
			while ((bytes = fis.read(buffer)) != -1) {
				os.write(buffer, 0, bytes);
				result += buffer.length;
			}
		} catch (final Exception ex) {
			Log.error("Can not read file.", ex);
		}
		return result;
	}

	/**
	 * Help method, which find the content type of the request resource file.
	 * 
	 * @param requestResource
	 * @return content type
	 */
	private String getContentType(File requestResource) {

		Log.debug("method... HttpWriter.getContentType()");

		try {
			if (requestResource.isFile()) {
				// fix script files has content type content/unknown
				if (isInterpretedFile(requestResource) != null) {
					return "text/html";
				}
				// general
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