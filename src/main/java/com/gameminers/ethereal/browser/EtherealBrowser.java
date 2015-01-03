/*
 *  Ethereal Browser
 *  Copyright (C) 2014-2015 Aesen Vismea
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

import java.io.File;
import java.util.Arrays;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.ScrollPaneConstants;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import com.gameminers.ethereal.browser.ext.VersionComparator;
import com.gameminers.ethereal.browser.listener.MainWindowListener;
import com.gameminers.ethereal.browser.listener.ModdedOpenListener;
import com.gameminers.ethereal.browser.listener.VanillaOpenListener;
import com.gameminers.ethereal.lib.Components;
import com.gameminers.ethereal.lib.Directories;
import com.gameminers.ethereal.lib.Frames;
import com.gameminers.ethereal.lib.Resources;
import com.google.gson.Gson;

public class EtherealBrowser {
	private static File minecraftDirectory;
	public static JFrame window;
	private static JTabbedPane tabs;
	public static final Gson gson = new Gson();
	public static void main(String[] args) {
		OptionParser parser = new OptionParser();
		OptionSpec<File> dir = parser.accepts("d", "The Minecraft directory; defaults to the platform-specific .minecraft").withRequiredArg().ofType(File.class);
		
		OptionSet set = parser.parse(args);
		if (set.has(dir)) {
			minecraftDirectory = set.valueOf(dir);
		} else {
			minecraftDirectory = Directories.getAppData("minecraft");
		}
		Frames.initLAF();
		window = Frames.create("Browser");
		window.addWindowListener(new MainWindowListener());
		
		tabs = new JTabbedPane();
		window.add(tabs);
		
		window.setJMenuBar(createMenuBar());
		
		tabs.addTab("", Resources.loadPNGIconAsset("iface/home"), createWelcomeScreen());
		
		window.setVisible(true);
	}
	
	private static Box createWelcomeScreen() {
		Box box = Components.createPaddedBox(BoxLayout.Y_AXIS);
		box.add(new JLabel("Welcome to Ethereal Browser!"));
		box.add(new JLabel("To get started, open the File menu and choose either Open Modded, or Open Vanilla."));
		box.add(new JLabel("If neither is enabled, no assets can be found in your Minecraft directory."));
		box.add(new JLabel("This could mean it is invalid, or you've never launched Minecraft."));
		box.add(Box.createVerticalStrut(24));
		box.add(new JLabel("The current Minecraft directory is "+Directories.tildize(getMinecraftDirectory().getAbsolutePath())));
		return box;
	}

	public static File getMinecraftDirectory() {
		return minecraftDirectory;
	}

	private static JMenuBar createMenuBar() {
		JMenuBar bar = new JMenuBar();
		bar.add(createFileMenu());
		return bar;
	}

	private static JMenu createFileMenu() {
		JMenu file = new JMenu("File");
		file.add(createOpenVanillaMenu());
		file.add(createOpenModdedMenu());
		return file;
	}

	private static JMenu createOpenModdedMenu() {
		JMenu menu = new JMenu("Open Modded");
		menu.setIcon(Resources.loadPNGIconAsset("iface/open-modded"));
		File mods = new File(minecraftDirectory, "mods");
		if (mods.exists()) {
			boolean global = false;
			File[] files = mods.listFiles();
			Arrays.sort(files, new VersionComparator());
			for (File f : files) {
				String name = f.getName();
				if (f.isDirectory() && (name.contains(".") || name.matches("[0-9][0-9]w[0-9][0-9][a-z]") )) {
					JMenuItem item = new JMenuItem(name);
					item.addActionListener(new ModdedOpenListener(f));
					menu.add(item);
				}
				if (f.isFile() && (name.endsWith(".jar") || name.endsWith(".zip"))) {
					global = true;
				}
			}
			if (global) {
				JMenuItem item = new JMenuItem("Global");
				item.addActionListener(new ModdedOpenListener(mods));
				menu.insert(item, 0);
			}
		} else {
			menu.setEnabled(false);
		}
		return menu;
	}

	private static JMenu createOpenVanillaMenu() {
		JMenu menu = new JMenu("Open Vanilla");
		menu.setIcon(Resources.loadPNGIconAsset("iface/open-vanilla"));
		File indexes = new File(minecraftDirectory, "assets/indexes");
		if (indexes.exists()) {
			File[] files = indexes.listFiles();
			Arrays.sort(files, new VersionComparator());
			for (File f : files) {
				String name = f.getName();
				if (name.endsWith(".json")) {
					JMenuItem item = new JMenuItem(name.substring(0, name.length()-5));
					item.addActionListener(new VanillaOpenListener(f, new File(minecraftDirectory, "assets/objects")));
					menu.add(item);
				}
			}
		} else {
			menu.setEnabled(false);
		}
		return menu;
	}

	public static void addDocument(String name, Icon icon, JComponent comp) {
		tabs.addTab(name, icon, new JScrollPane(comp, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED));
	}

}
