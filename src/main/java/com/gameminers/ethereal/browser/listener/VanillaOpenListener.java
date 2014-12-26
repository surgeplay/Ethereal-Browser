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
package com.gameminers.ethereal.browser.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import com.gameminers.ethereal.browser.EtherealBrowser;
import com.gameminers.ethereal.browser.glue.AssetIndex;
import com.gameminers.ethereal.browser.utility.Assets;
import com.gameminers.ethereal.browser.utility.Dialogs;
import com.gameminers.ethereal.browser.utility.Resources;

public class VanillaOpenListener implements ActionListener {
	private final Path index;
	private final Path objects;
	public VanillaOpenListener(File index, File objects) {
		this.index = index.toPath();
		this.objects = objects.toPath();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			String basename = index.toFile().getName().replace(".json", "");
			AssetIndex idx = Assets.load(basename, new String(Files.readAllBytes(index), "UTF-8"));
			EtherealBrowser.addDocument(basename, Resources.getPNGIconAsset("iface/vanilla"),
					Assets.buildTree(idx, objects));
		} catch (Exception e1) {
			Dialogs.showErrorDialog("An error occurred while loading "+index.getFileName(), e1);
		}
	}

}
