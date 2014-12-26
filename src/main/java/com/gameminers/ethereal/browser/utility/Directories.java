/*
 *  EtheralBrowser
 *  Copyright (C) 2014 Aesen Vismea
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.gameminers.ethereal.browser.utility;

import java.io.File;
import java.util.Arrays;

import com.gameminers.ethereal.browser.comparator.VersionComparator;

public class Directories {
	private static final boolean TILDIZE = getOS() == OperatingSystem.LINUX ||
											getOS() == OperatingSystem.MAC;
	private static final String USER_HOME;
	static {
		String home = System.getProperty("user.home");
		if (home.endsWith("/")) {
			home = home.substring(0, home.length()-1);
		}
		USER_HOME = home;
	}
	
	public static String tildize(String path) {
		return TILDIZE ? path.replace(USER_HOME, "~") : path;
	}
	
	public static OperatingSystem getOS() {
		String os = System.getProperty("os.name");
		if (os.equalsIgnoreCase("AIX"))
			return OperatingSystem.LINUX;
		if (os.equalsIgnoreCase("Digital Unix"))
			return OperatingSystem.LINUX;
		if (os.equalsIgnoreCase("FreeBSD"))
			return OperatingSystem.LINUX;
		if (os.equalsIgnoreCase("HP UX"))
			return OperatingSystem.LINUX;
		if (os.equalsIgnoreCase("Irix"))
			return OperatingSystem.LINUX;
		if (os.equalsIgnoreCase("Linux"))
			return OperatingSystem.LINUX;
		if (os.contains("OS X"))
			return OperatingSystem.MAC;
		if (os.equalsIgnoreCase("MPE/iX"))
			return OperatingSystem.LINUX;
		if (os.equalsIgnoreCase("Solaris"))
			return OperatingSystem.LINUX;
		if (os.startsWith("Windows"))
			return OperatingSystem.WINDOWS;
		return OperatingSystem.OTHER;
	}
	
	public static File getAppData(String name) {
		final String home = System.getProperty("user.home", ".");
		File dir = null;
		switch (getOS()) {
			case LINUX:
			case OTHER:
				dir = new File(home, '.' + name + '/');
				break;
			case WINDOWS:
				final String appdata = System.getenv("APPDATA");
				if (appdata != null) {
					dir = new File(appdata, "." + name + '/');
				} else {
					dir = new File(home, '.' + name + '/');
				}
				break;
			case MAC:
				dir = new File(home, "Library/Application Support/" + name);
				break;
			default:
				dir = new File(home, name + '/');
		}
		return dir;
	}

	public static File[] sort(File[] listFiles) {
		Arrays.sort(listFiles, new VersionComparator());
		return listFiles;
	}
}
