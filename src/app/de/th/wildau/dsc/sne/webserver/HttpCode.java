package de.th.wildau.dsc.sne.webserver;

public enum HttpCode {

	HTTP_200("200 OK", null), HTTP_400("400 Not Found", null);
	
	private String server;
	
	HttpCode(String server, Object foo) {
		this.server = server;
	}
	
	public String getServer() {
		return this.server;
	}
	
	public void setServer(String server) {
		
	}
}
