package de.th.wildau.dsc.sne.webserver;

import java.io.File;

/**
 * This file filter doesn't accept file names which start with '.' and hidden files.
 */
public class HiddenFilter implements java.io.FileFilter {

	@Override
	public boolean accept(File file) {
		if (file.canRead()) {
			if (file.getName().startsWith(".") || file.isHidden()) {
				return false;
			}
		}
		return true;
	}

}
