package de.th.wildau.dsc.sne.webserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

public class HttpWriter {

	private final int httpStatusCode;
	private final ContentType contentType;

	public HttpWriter(int httpCode, ContentType contentType) {

		this.httpStatusCode = httpCode;
		this.contentType = contentType;
	}

	public void write(OutputStream outputStream, File requestResource) {

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

		writeHeader(outputStream, requestResource);
		writeBody(outputStream, requestResource);
	}

	private void writeHeader(OutputStream outputStream, File requestResource) {
		// TODO Auto-generated method stub

		String header = new String();

		switch (this.httpStatusCode) {
		case 200:
			try {
				FileInputStream fileInputStream = new FileInputStream(
						requestResource);

				while (true) {
					// read the file from filestream, and print out through the
					// client-outputstream on a byte per byte base.
					int b = fileInputStream.read();
					if (b == -1) {
						break; // end of file
					}
					outputStream.write(b);
				}
			} catch (FileNotFoundException ex) {
				Log.error(
						"Can not found resource file: "
								+ requestResource.toString(), ex.getMessage());
			} catch (IOException ex) {
				Log.error("Can not read the file.", ex.getMessage());
			}

			// output = "HTTP/1.1 200 OK\r\n" + "Content-Type: text/html\r\n"
			// + "Content-Length: " + details.length() + "\r\n" + "\r\n"
			// + details;
			break;
		case 403:
			// TODO
			/*
			 * <html><body>Forbidden<br /> You don't have permission to access /ebay/ on
			 * this server.</body></html>
			 */
			break;
		case 404:
			String errorDetail = "<h1>Foo 404</h1><h2>Error 404 File Not Found.</h2>";
			header = "HTTP/1.1 404 File Not Found" + getLineBreak()
					+ "Content-Type: text/html" + getLineBreak()
					+ "Content-Length: " + errorDetail.length()
					+ getLineBreak() + getLineBreak() + errorDetail;
			break;
		default:
			// 500 Internal Server Error
			break;
		}

		try {
			outputStream.write(header.getBytes());
			outputStream.flush();
		} catch (IOException ex) {
			Log.error("Can not write header output stream! ", ex.getMessage());
		}
	}

	private void writeBody(OutputStream outputStream, File requestResource) {
		// TODO Auto-generated method stub

		String body = new String();

		try {
			outputStream.write(body.getBytes());
			outputStream.flush();
		} catch (IOException ex) {
			Log.error("Can not write header output stream! ", ex.getMessage());
		}
	}

	private String getLineBreak() {

		// XXX return System.getProperty("line.separator");
		return "\r\n";
	}

	protected int getHttpCode() {
		return this.httpStatusCode;
	}

	protected ContentType getContentType() {
		return this.contentType;
	}
}
