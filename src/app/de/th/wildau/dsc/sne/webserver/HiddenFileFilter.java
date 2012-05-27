package de.th.wildau.dsc.sne.webserver;

import java.io.File;

/**
 * This file filter doesn't accept file names start with '.' and hidden files.
 * 
 * @author sne
 * 
 */
public class HiddenFileFilter implements java.io.FileFilter {

	@Override
	public boolean accept(File file) {
		if (file.isFile() && file.canRead()) {
			if (file.getName().startsWith(".") || file.isHidden()) {
				return false;
			}
		}
		return true;
	}

}
