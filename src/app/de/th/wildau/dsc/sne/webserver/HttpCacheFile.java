package de.th.wildau.dsc.sne.webserver;

public class HttpCacheFile {

	private final long date;
	// private final String hash; // XXX ?
	private int[] content;

	public HttpCacheFile(long date, int[] content) {

		this.date = date;
		this.content = content;
	}

	public final long getDate() {
		return this.date;
	}

	public final int[] getContent() {
		return this.content;
	}
	
	public void setContent(int[] content) {
		this.content = content;
	}
}
