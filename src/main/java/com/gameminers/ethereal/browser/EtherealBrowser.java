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

import java.io.File;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

public class EtherealBrowser {
	private static File minecraftDirectory;
	public static void main(String[] args) {
		OptionParser parser = new OptionParser();
		OptionSpec<File> dir = parser.accepts("d", "The Minecraft directory; defaults to the platform-specific .minecraft").withRequiredArg().ofType(File.class);
		
		OptionSet set = parser.parse(args);
		if (set.has(dir)) {
			minecraftDirectory = set.valueOf(dir);
		} else {
			minecraftDirectory = Paths.getAppData("minecraft");
		}
		initLAF();
		JFrame window = createWindow();
		
		JTabbedPane tabs = new JTabbedPane();
		window.add(tabs);
		
		window.setJMenuBar(createMenuBar());
		
		tabs.addTab("", Resources.loadPNGIconAsset("iface/home"), createWelcomeScreen());
		
		window.setVisible(true);
	}

	private static void initLAF() {
		try {
			// try to set GTK+ first so we don't look hideous on Linux
			// XXX - on a Windows system, will this set GTK+ if it's installed?
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
		} catch (Exception e) {
			try {
				// otherwise, do what this JVM thinks is best
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception ex) {}
		}
	}

	private static JFrame createWindow() {
		JFrame window = new JFrame("Ethereal Browser");
		window.setIconImages(Resources.loadPNGAsset("iface/browser", "iface/browser-32"));
		window.setSize(854, 480);
		window.setLocationByPlatform(true);
		window.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		window.addWindowListener(new MainWindowListener());
		return window;
	}
	
	private static Box createWelcomeScreen() {
		Box box = Components.createPaddedBox(BoxLayout.Y_AXIS);
		box.add(new JLabel("Welcome to Ethereal Browser!"));
		box.add(new JLabel("To get started, open the File menu and choose either Open Modded, or Open Vanilla."));
		box.add(new JLabel("If neither is enabled, no assets can be found in your Minecraft directory."));
		box.add(new JLabel("This could mean it is invalid, or you've never launched Minecraft."));
		box.add(Box.createVerticalStrut(24));
		box.add(new JLabel("The current Minecraft directory is "+Paths.tildize(getMinecraftDirectory().getAbsolutePath())));
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
			for (File f : Paths.sort(mods.listFiles())) {
				String name = f.getName();
				if (f.isDirectory() && (name.contains(".") || name.matches("[0-9][0-9]w[0-9][0-9][a-z]") )) {
					menu.add(new JMenuItem(name));
				}
				if (f.isFile() && (name.endsWith(".jar") || name.endsWith(".zip"))) {
					global = true;
				}
			}
			if (global) {
				menu.insert(new JMenuItem("Global"), 0);
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
			for (File f : Paths.sort(indexes.listFiles())) {
				String name = f.getName();
				if (name.endsWith(".json")) {
					menu.add(new JMenuItem(name.substring(0, name.length()-5)));
				}
			}
		} else {
			menu.setEnabled(false);
		}
		return menu;
	}

}
