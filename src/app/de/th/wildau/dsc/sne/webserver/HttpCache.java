package de.th.wildau.dsc.sne.webserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

/**
 * Http cache (Singleton).
 * 
 * @author sne
 * 
 */
public class HttpCache {

	private static HttpCache INSTANCE;
	private final int cacheSize;
	private final int cacheTime;

	private Map<String, HttpCacheFile> cache;

	/**
	 * TODO javadoc
	 * 
	 * @param cacheSize
	 *            element size
	 * @param cacheTime
	 *            in seconds
	 */
	private HttpCache(int cacheSize, int cacheTime) {

		this.cacheSize = cacheSize;
		this.cacheTime = cacheTime;
		this.cache = Collections
				.synchronizedMap(new HashMap<String, HttpCacheFile>());
	}

	/**
	 * Use default values cacheSize:25elements cacheTime:60s
	 */
	private HttpCache() {

		this(25, 60);
	}

	public static synchronized final HttpCache getInstance() {

		if (INSTANCE == null) {
			INSTANCE = new HttpCache();
		}
		return INSTANCE;
	}

	/**
	 * Check resource (can read and is none script file). After that check cache
	 * contains.
	 * 
	 * @param resource
	 * @return
	 */
	public boolean contains(File resource) {

		if (!isScriptFile(resource))
			return INSTANCE.cache.containsKey(resource.toString());
		return false;
	}

	private boolean isScriptFile(File resource) {

		if (resource != null && resource.canRead()) {
			for (ScriptLanguage scriptLanguage : ScriptLanguage.values()) {
				if (resource.getName().toLowerCase()
						.endsWith(scriptLanguage.getFileExtension())) {
					Log.debug("Don't cache script file: " + resource.toString());
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * TODO javadoc
	 * 
	 * @param resource
	 * @param content
	 */
	public final synchronized void put(String resource, int[] content) {

		// check resource - don't cache script files

		if (!INSTANCE.cache.containsKey(resource)) {
			if (INSTANCE.cache.size() > cacheSize) {
				// clean cache
				Iterator<Entry<String, HttpCacheFile>> ite = INSTANCE.cache
						.entrySet().iterator();
				while (ite.hasNext()) {
					Entry<String, HttpCacheFile> temp = ite.next();
					if (olderThen(temp.getValue().getCacheTime(), cacheTime)) {
						INSTANCE.cache.remove(temp.getKey());
					}
				}
			}
			INSTANCE.cache.put(resource,
					new HttpCacheFile(System.currentTimeMillis(), content));
		} else {
			// in cache
			if (olderThen(INSTANCE.cache.get(resource).getCacheTime(),
					cacheTime)) {
				// reload
				List<Integer> tempContent = new ArrayList<Integer>();
				FileInputStream fis;
				byte[] buffer = new byte[1024];
				int bytes = 0;
				try {
					fis = new FileInputStream(new File(resource));
					while ((bytes = fis.read(buffer)) != -1) {
						tempContent.add(bytes);
					}

					// not the best way
					int[] intArray = new int[tempContent.size()];
					int i = 0;
					for (Integer e : tempContent) {
						intArray[i++] = e.intValue();
					}

					INSTANCE.cache.get(resource).setContent(intArray);
				} catch (IOException ex) {
					Log.error("Can not read file.", ex);
				}
			}
		}
	}

	private synchronized boolean olderThen(long timeMillis, int seconds) {

		if (TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()
				- timeMillis) > seconds) {
			return true;
		}
		return false;
	}

	/**
	 * TODO javadoc
	 * 
	 * possible null
	 * 
	 * @param resource
	 * @return
	 */
	public final int[] get(String resource) {

		if (INSTANCE.cache.containsKey(resource))
			return INSTANCE.cache.get(resource).getContent();
		return null;
	}
}
