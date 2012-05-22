package de.th.wildau.dsc.sne.webserver;

public enum HttpStatusCode {

	CODE_200("200 OK", null), CODE_404("404 Not Found", null);
	
	private String server;
	
	HttpStatusCode(String server, Object foo) {
		this.server = server;
	}
	
	public String getServer() {
		return this.server;
	}
	
	public void setServer(String server) {
		
	}
}
