package de.th.wildau.dsc.sne.webserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.regex.Pattern;

/**
 * A handler which is invoked to process HTTP exchanges. Each HTTP exchange is
 * handled by one of these handlers.
 * 
 * Handle the given request and generate an appropriate response.
 * 
 * @author sne
 * 
 */
class HttpHandler implements Runnable {

	private Socket socket;

	protected HttpHandler(Socket socket) {
		this.socket = socket;
	}

	/**
	 * Process the request.
	 */
	@Override
	public void run() {

		if (this.socket == null) {
			throw new IllegalStateException("Missing server socket.");
		}

		try {
			InputStream input = socket.getInputStream();
			OutputStream output = socket.getOutputStream();
			processRequest(input, output);
			closeConnection();
		} catch (IOException ex) {
			Log.error("Http handle exception!", ex.getMessage());
		}
	}

	private void processRequest(InputStream input, OutputStream output) {

		try {
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(input));
			File requestResource = null;

			// FIXME take a long time
			// String readLine;
			// while ((readLine = bufferedReader.readLine()) != null) {
			// Log.debug("Request header ["
			// + this.socket.getInetAddress().getHostName() + "]: "
			// + readLine); // support GET HTTP 1.0 & 1.1 requests
			// if (Pattern.matches("^GET /*.* HTTP/1.[0,1]", readLine)) {
			// // requestResource = new File(Configuration.getWebRoot()
			// // + readLine.split(" ")[1]);
			// } else if (Pattern.matches("^POST /. HTTP/1.?", readLine)) {
			// Log.warn("Doesn't support POST requests!");
			// break;
			// }
			// }

			String firstRequestLine = bufferedReader.readLine();
			Log.debug("first request line: " + firstRequestLine);
			if (Pattern.matches("^GET /*.* HTTP/1.[0,1]", firstRequestLine)) {
				requestResource = new File(Configuration.getWebRoot()
						+ firstRequestLine.split(" ")[1]);
			}

			if (requestResource == null) {
				Log.fatal(
						"Doesn't find a resource string in the request header.",
						firstRequestLine);
				return;
			}

			Log.debug("request resource: " + requestResource.toString());
			HttpWriter httpWriter = null;

			if (!requestResource.exists()) {
				Log.info("Request resource doesn't exists: "
						+ requestResource.getPath());

				httpWriter = new HttpWriter(404); //
				httpWriter.write(output, requestResource);
			} else if (requestResource.isDirectory()) {
				Log.info("Request resource is a directory: "
						+ requestResource.toString());

				if (requestResource.canRead()) {

					// TODO [sne] exist a index file
					// TODO return index file
					// TODO [dsc] return list (links) of files
					httpWriter = new HttpWriter(200);
					httpWriter.write(output, requestResource);
				} else {
					httpWriter = new HttpWriter(403);
					httpWriter.write(output, requestResource);
				}
			} else if (requestResource.isFile()) {
				Log.info("Request resource is a file: "
						+ requestResource.toString());

				if (requestResource.isHidden()) {
					Log.warn("Can not deliver hidden files.");
					httpWriter = new HttpWriter(404);
					httpWriter.write(output, null);
				} else if (requestResource.canRead()) {
					httpWriter = new HttpWriter(200);
					httpWriter.write(output, requestResource);
				} else {
					Log.warn("request resource is a file, but can not handle it.");
					httpWriter = new HttpWriter(500);
					httpWriter.write(output, requestResource);
				}
			} else {

			}
		} catch (IOException ex) {
			Log.error("Can not read request: " + ex.getMessage());
		} catch (Exception ex) {
			Log.fatal("Catch processRequest() exception!", ex.getMessage());
		}
	}

	/**
	 * Close the current client connection.
	 */
	private void closeConnection() {

		try {
			PrintWriter out = new PrintWriter(this.socket.getOutputStream(),
					true);
			// check is output socket closed
			if (out.checkError()) {
				this.socket.getOutputStream().close();
			}
			this.socket.getInputStream().close();
		} catch (IOException ex) {
			Log.error("Close connection.", ex.getMessage());
		}
		Log.info(this.socket.getInetAddress().getHostName()
				+ " client connection done.");
	}
}
