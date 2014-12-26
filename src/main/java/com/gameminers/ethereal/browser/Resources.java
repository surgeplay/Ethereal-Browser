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
package com.gameminers.ethereal.browser;

import java.awt.Image;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

public class Resources {
	private static Map<String, Object> cache = new HashMap<>();
	
	/**
	 * @param name The name of the asset, including extension.
	 * @return The URL of the asset on the classpath.
	 */
	public static URL getAssetURL(String name) {
		return ClassLoader.getSystemResource("assets/"+name);
	}
	
	/**
	 * Loads a PNG asset right here, right now, off of the classpath.
	 * <p>
	 * Unlike {@link #getPNGAsset}, this method does not cache.
	 * This is useful for resources that are only used once and do not need to be cached.
	 * @param basename The base name of the asset, without the extension.
	 * @return The freshly-loaded Image from this asset, or null if it does not exist
	 */
	public static Image loadPNGAsset(String basename) {
		try {
			return ImageIO.read(getAssetURL(basename+".png"));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Loads a collection of PNG assets, right here, right now, off of the classpath.
	 * <p>
	 * Unlike {@link #getPNGAsset}, this method does not cache.
	 * This is useful for resources that are only used once and do not need to be cached.
	 * @param basenames The base names of the assets, without their extensions.
	 * @return A List of loaded images, in the same order as they were passed.
	 */
	public static List<Image> loadPNGAsset(String... basenames) {
		if (basenames.length == 0) throw new ArrayIndexOutOfBoundsException("loadPNGAsset needs at least one argument");
		List<Image> images = new ArrayList<Image>(basenames.length);
		for (String basename : basenames) {
			images.add(loadPNGAsset(basename));
		}
		return images;
	}
	
	/**
	 * Loads a PNG asset right here, right now, off of the classpath, and wraps it in an Icon.
	 * <p>
	 * Unlike {@link #getPNGIconAsset}, this method does not cache.
	 * This is useful for resources that are only used once and do not need to be cached.
	 * @param basename The base name of the asset, without the extension.
	 * @return The freshly-loaded Image from this asset, inside an Icon
	 */
	public static Icon loadPNGIconAsset(String basename) {
		return new ImageIcon(loadPNGAsset(basename));
	}
	
	/**
	 * Loads a collection of PNG assets, using the cache if they are in the cache.
	 * <p>
	 * Unlike {@link #loadPNGAsset}, this method <b>does</b> cache.
	 * This is useful for images that are used in multiple places.
	 * @param basenames The base names of the assets, without their extensions.
	 * @return A List of loaded images, in the same order as they were passed.
	 */
	public static List<Image> getPNGAsset(String... basenames) {
		if (basenames.length == 0) throw new ArrayIndexOutOfBoundsException("loadPNGAsset needs at least one argument");
		List<Image> images = new ArrayList<Image>(basenames.length);
		for (String basename : basenames) {
			images.add(getPNGAsset(basename));
		}
		return images;
	}
	
	/**
	 * Loads a PNG asset, using the cache if it is in the cache.
	 * <p>
	 * Unlike {@link #loadPNGAsset}, this method <b>does</b> cache.
	 * This is useful for images that are used in multiple places.
	 * @param basename The base names of the asset, without it's extension.
	 * @return The loaded Image
	 */
	public static Image getPNGAsset(String basename) {
		String key = "Image:"+basename;
		if (!cache.containsKey(key)) {
			cache.put(key, loadPNGAsset(basename));
		}
		return (Image)cache.get(key);
	}
	
	/**
	 * Loads a PNG asset, using the cache if it is in the cache.
	 * <p>
	 * Unlike {@link #loadPNGAsset}, this method <b>does</b> cache.
	 * This is useful for images that are used in multiple places.
	 * <p>
	 * The returned Icon object is <b>not</b> cached, and will be created fresh.
	 * This is to prevent conflicts, such as where two buttons are using the same Icon,
	 * and one Icon has it's image changed, which messes up the appearance of the other button.
	 * @param basename The base names of the asset, without it's extension.
	 * @return The loaded Image
	 */
	public static Icon getPNGIconAsset(String basename) {
		String key = "Image:"+basename;
		if (!cache.containsKey(key)) {
			cache.put(key, loadPNGAsset(basename));
		}
		return new ImageIcon((Image)cache.get(key));
	}
}
