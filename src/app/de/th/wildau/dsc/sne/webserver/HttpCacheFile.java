package de.th.wildau.dsc.sne.webserver;

public class HttpCacheFile {

	private long cacheTime;
	private int[] content;

	public HttpCacheFile(long cacheTime, int[] content) {

		this.cacheTime = cacheTime;
		this.content = content;
	}

	public long getCacheTime() {
		return this.cacheTime;
	}

	public void setCacheTime(long cacheTime) {
		this.cacheTime = cacheTime;
	}

	public int[] getContent() {
		return this.content;
	}

	public void setContent(int[] content) {
		this.content = content;
	}
}
