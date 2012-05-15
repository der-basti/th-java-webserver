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
		try {
			ServerSocket server = new ServerSocket(Configuration.getPort());
			Log.debug("bound port " + Configuration.getPort());
			while (true) {
				Socket serverSocket = server.accept();
				Log.debug("server connection started ("
						+ serverSocket.getInetAddress() + ")");
				HttpHandler http = new HttpHandler(serverSocket);
				http.start();
				Log.debug("server connection done ("
						+ serverSocket.getInetAddress() + ")");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
