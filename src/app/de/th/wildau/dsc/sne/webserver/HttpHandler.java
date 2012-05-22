package de.th.wildau.dsc.sne.webserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

/**
 * TODO javadoc
 */
public class HttpHandler extends Thread {

	private final Socket serverSocket;
	private InputStream inputStream;
	private OutputStream outputStream;

	public HttpHandler(Socket serverSocket) throws IOException {

		this.serverSocket = serverSocket;
		this.inputStream = this.serverSocket.getInputStream();
		this.outputStream = this.serverSocket.getOutputStream();
	}

	@Override
	public void run() {

		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(this.inputStream));
		try {
			// TODO better solution is with regex
			File requestResource = new File(Configuration.getWebRoot()
					+ bufferedReader.readLine().split(" ")[1]);

			Log.debug("request resource: " + requestResource.toString());
			HttpWriter httpWriter;

			if (!requestResource.exists()) {
				Log.info("Request resource doesn't exists: "
						+ requestResource.getPath());
				httpWriter = new HttpWriter(404, ContentType.TEXT_HTML);
				httpWriter.write(this.outputStream, requestResource);

			} else if (requestResource.isDirectory()) {
				Log.info("Request resource is a directory: "
						+ requestResource.toString());
				// TODO [sne] exist a index file
				// TODO return index file
				// TODO [dsc] return list (links) of files
				// else 403 forbidden
				httpWriter = new HttpWriter(403, ContentType.TEXT_HTML);
				httpWriter.write(this.outputStream, requestResource);

			} else if (requestResource.isFile()) {
				Log.info("Request resource is a file: "
						+ requestResource.toString());

				if (requestResource.isHidden()) {
					Log.warn("Can not deliver hidden files!");
					httpWriter = new HttpWriter(404, ContentType.TEXT_HTML);
					httpWriter.write(this.outputStream, null);
				} else if (requestResource.canRead()) {
					// TODO deliver file
					// TODO chek content type
					/*
					 * XXX [dsc] handle different file extensions if
					 * (path.endsWith(".zip" ) { type_is = 3; } if
					 * (path.endsWith(".jpg") || path.endsWith(".jpeg")) {
					 * type_is = 1; } if (path.endsWith(".gif")) {
					 */
					httpWriter = new HttpWriter(200, ContentType.TEXT_HTML);
					httpWriter.write(this.outputStream, requestResource);
				} else {
					// TODO 500 Internal Server Error
				}
			} else {
				httpWriter = new HttpWriter(500, ContentType.TEXT_HTML);
				httpWriter.write(this.outputStream, requestResource);
			}
		} catch (IOException ex) {
			Log.error("Can not read request: " + ex.getMessage());
		}

		closeConnection();
	}

	/**
	 * Close the current client connection.
	 */
	private void closeConnection() {

		try {
			this.inputStream.close();
			this.outputStream.close();
		} catch (IOException e) {
			Log.error(e.getMessage());
		}
		Log.info(this.serverSocket.getInetAddress().getHostName()
				+ " client connection done.");
	}
}
