package de.th.wildau.dsc.sne.webserver;

import java.io.File;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * HttpCache (Singleton).
 */
public class HttpCache {

	protected static final int CACHE_SIZE = 10;
	protected static final long CACHE_TIME_MS = 60 * 1000; /* each 60 s */

	private static long LAST_CACHE;
	private static HttpCache INSTANCE;

	private final int cacheSize;
	private Queue<String> queue;
	private Map<String, byte[]> cache;

	/**
	 * Hidden constructor. HttpCache is a Singleton.
	 * 
	 * @param cacheSize
	 *            element size
	 * @param cacheTime
	 *            in seconds
	 */
	private HttpCache(int cacheSize) {

		this.cacheSize = cacheSize;
		this.queue = new ConcurrentLinkedQueue<String>();
		this.cache = new ConcurrentHashMap<String, byte[]>();
	}

	/**
	 * Caches max {@link #CACHE_SIZE} elements.
	 * 
	 * @return {@link HttpCache}
	 */
	public static synchronized final HttpCache getInstance() {
		return getInstance(CACHE_SIZE);
	}

	/**
	 * Singleton call point.
	 * 
	 * @return {@link HttpCache}
	 */
	public static synchronized final HttpCache getInstance(int cacheSize) {

		if (INSTANCE == null) {
			INSTANCE = new HttpCache(cacheSize);
		}
		return INSTANCE;
	}

	public byte[] getValue(File resource) {
		return INSTANCE.cache.get(resource.toString());
	}

	/**
	 * 
	 * 
	 * @param resource
	 * @param header
	 * @param body
	 */
	public void put(File resource, byte[] header, byte[] body) {

		if (isInterpretedFile(resource)) {
			Log.debug("Don't cache interpreted files.");
			return;
		}

		if (body != null && body.length > 5000000) {
			Log.debug("Don't cache files over 5 MB.");
			return;
		}

		// Java 7, as part of NIO.2, has added the WatchService API

		byte[] content = new byte[header.length + body.length];
		int headerLength = header.length;

		for (int i = 0; i < content.length; ++i) {
			content[i] = i < headerLength ? header[i] : body[i - headerLength];
		}

		put(resource, content);
	}

	private boolean isInterpretedFile(File file) {

		for (ScriptLanguage scriptLanguage : WebServer.supportedScriptLanguages) {
			if (file.getName().toLowerCase()
					.endsWith(scriptLanguage.getFileExtension())) {
				return true;
			}
		}
		return false;
	}

	private void put(File resource, byte[] content) {

		if (INSTANCE.cache.size() >= INSTANCE.cacheSize) {
			Log.debug("max cache size reached");
			String remove = INSTANCE.queue.poll();
			INSTANCE.cache.remove(remove);
		}
		INSTANCE.queue.add(resource.getName());
		INSTANCE.cache.put(resource.toString(), content);
	}

	public boolean contains(File resource) {

		if (LAST_CACHE <= 0) {
			LAST_CACHE = System.currentTimeMillis();
		}
		if (CACHE_TIME_MS < (System.currentTimeMillis() - LAST_CACHE)) {
			Log.debug("reset http cache");
			LAST_CACHE = System.currentTimeMillis();
			INSTANCE.cache.clear();
			return false;
		}
		return INSTANCE.cache.containsKey(resource.toString());
	}
}
