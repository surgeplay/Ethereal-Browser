/*
 *  EtherealBrowser
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
package com.gameminers.ethereal.browser.ext;

import java.io.File;
import java.text.Collator;
import java.util.Comparator;

public class VersionComparator implements Comparator<File> {
	private static final String SNAPSHOT_REGEX = "[0-9][0-9]w[0-9][0-9][a-z]";
	private static final String INITIAL_RELEASE_REGEX = "[0-9]*\\.[0-9]*";
	private static final String PATCH_RELEASE_REGEX = "[0-9]*\\.[0-9]*\\.[0-9]*";

	private Collator collator = Collator.getInstance();
	
	@Override
	public int compare(File o1, File o2) {
		String s1 = o1.getName();
		String s2 = o2.getName();
		if (s1.contains(".") && !o1.isDirectory()) {
			s1 = s1.substring(0, s1.lastIndexOf('.'));
		}
		if (s2.contains(".") && !o2.isDirectory()) {
			s2 = s2.substring(0, s2.lastIndexOf('.'));
		}
		boolean snapshot1 = s1.matches(SNAPSHOT_REGEX);
		boolean snapshot2 = s2.matches(SNAPSHOT_REGEX);
		boolean version1 = s1.matches(INITIAL_RELEASE_REGEX) || s1.matches(PATCH_RELEASE_REGEX);
		boolean version2 = s2.matches(INITIAL_RELEASE_REGEX) || s2.matches(PATCH_RELEASE_REGEX);
		if (snapshot1 != snapshot2) {
			return snapshot1 ? 1 : -1;
		} else if (snapshot1 && snapshot2) {
			int snapshotId1 = getSnapshotId(s1);
			int snapshotId2 = getSnapshotId(s2);
			return Integer.compare(snapshotId1, snapshotId2);
		} else if (s1.equals("legacy") != s2.equals("legacy")) {
			return s1.equals("legacy") ? -1 : 1;
		} else if (version1 && version2) {
			int versionCode1 = getVersionCode(s1);
			int versionCode2 = getVersionCode(s2);
			return Integer.compare(versionCode1, versionCode2);
		} else {
			return collator.compare(s1, s2);
		}
	}

	private int getSnapshotId(String s) {
		return Integer.valueOf(s.substring(0, 2)+s.substring(3, 5));
	}

	private int getVersionCode(String s) {
		System.out.println(s);
		String[] split = s.split("\\.");
		String major = split[0];
		String minor = split[1];
		String patch = (split.length >= 3 ? leftPad(split[2]) : "000");
		return Integer.parseInt(major+minor+patch);
	}
	
	private String leftPad(String string) {
		if (string.length() < 3) {
			string = "000".substring(0, 3-string.length())+string;
		}
		return string;
	}
}

