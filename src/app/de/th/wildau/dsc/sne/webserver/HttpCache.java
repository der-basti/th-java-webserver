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
 * Http cache (singelton).
 * 
 * @author sne
 * 
 */
public class HttpCache {

	private static HttpCache INSTANCE;
	private final static int cacheSize = 25;
	private final static int cacheTime = 60;

	private Map<String, HttpCacheFile> cache;

	private HttpCache() {

		this.cache = Collections
				.synchronizedMap(new HashMap<String, HttpCacheFile>());
	}

	public static synchronized final HttpCache getInstance() {

		if (INSTANCE == null) {
			INSTANCE = new HttpCache();
		}
		return INSTANCE;
	}

	public boolean contains(String resource) {

		return INSTANCE.cache.containsKey(resource);
	}

	/**
	 * TODO javadoc
	 * 
	 * @param resource
	 * @param content
	 */
	public final synchronized void put(String resource, int[] content) {

		// don't cache script files
		if (resource != null) {
			// dynamic way - static four if checks...
//			for (ScriptLanguage scriptLanguage : ScriptLanguage.values()) {
//				if (resource.trim().toLowerCase()
//						.endsWith(scriptLanguage.getFileExtension())) {
//					Log.debug("Can not cache script file: " + resource);
//					return;
//				}
//			}
		}

		if (INSTANCE.cache.containsKey(resource)) {
			// in cache
			if (olderThen(INSTANCE.cache.get(resource).getDate(), cacheTime)) {
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
		} else {
			if (INSTANCE.cache.size() > cacheSize) {
				// clean cache
				Iterator<Entry<String, HttpCacheFile>> ite = INSTANCE.cache
						.entrySet().iterator();
				while (ite.hasNext()) {
					Entry<String, HttpCacheFile> temp = ite.next();
					if (olderThen(temp.getValue().getDate(), cacheTime)) {
						INSTANCE.cache.remove(temp.getKey());
					}
				}
			}
			INSTANCE.cache.put(resource,
					new HttpCacheFile(System.currentTimeMillis(), content));
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
