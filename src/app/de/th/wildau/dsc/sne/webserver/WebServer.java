package de.th.wildau.dsc.sne.webserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * TODO javadoc
 * 
 */
public class WebServer extends Thread {

	@Override
	public void run() {

		Log.info("instantiating web server");
		try {
			ServerSocket server = new ServerSocket(Configuration.getPort());
			Log.debug("bound port " + Configuration.getPort());
			while (true) {
				try {
					Socket serverSocket = server.accept();
					Log.info(serverSocket.getInetAddress().getHostName()
							+ " client request");
					HttpHandler http = new HttpHandler(serverSocket);
					http.start();
				} catch (IOException ex) {
					Log.error("Connection failed!", ex.getMessage());
				}
			}
		} catch (IOException ex) {
			Log.fatal("Can not start the server!", ex.getMessage());
		}
	}
}
