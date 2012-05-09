package de.th.wildau.dsc.sne.webserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * TODO javadoc
 * 
 */
public class WebServer extends Thread {

	private final Configuration config;

	/**
	 * TODO javadoc
	 * 
	 * @param config
	 */
	public WebServer(Configuration config) {
		this.config = config;
	}

	@Override
	public void run() {
		try {
			ServerSocket server = new ServerSocket(this.config.getPort());
			while (true) {
				Socket serverSocket = server.accept();
				this.config.getLog().debug(
						"server connection start ("
								+ serverSocket.getInputStream().toString()
								+ ")");
				new HttpHandler(this.config, serverSocket);
				this.config.getLog().debug(
						"server connection done ("
								+ serverSocket.getInputStream().toString()
								+ ")");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
