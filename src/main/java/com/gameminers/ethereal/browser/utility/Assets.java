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
package com.gameminers.ethereal.browser.utility;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import com.gameminers.ethereal.browser.EtherealBrowser;
import com.gameminers.ethereal.browser.ext.AssetTreeCellRenderer;
import com.gameminers.ethereal.browser.glue.Asset;
import com.gameminers.ethereal.browser.glue.AssetIndex;

public class Assets {

	/**
	 * Builds a JTree from an AssetIndex.
	 * @param idx The index to base the tree off of.
	 * @return The built tree.
	 */
	public static JTree buildTree(AssetIndex idx, Path fileBase) {
		JTree tree = new JTree();
		Map<String, DefaultMutableTreeNode> createdPaths = new HashMap<>();
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(idx.name);
		tree.setCellRenderer(new AssetTreeCellRenderer());
		tree.setModel(new DefaultTreeModel(root));
		for (Map.Entry<String, Asset> en : idx.objects.entrySet()) {
			DefaultMutableTreeNode previous = root;
			String[] elements = en.getKey().split("/");
			String path = "";
			for (String s : elements) {
				path += s+"/";
				DefaultMutableTreeNode child;
				if (createdPaths.containsKey(path)) {
					child = createdPaths.get(path);
				} else {
					child = new DefaultMutableTreeNode(s);
					createdPaths.put(path, child);
				}
				previous.add(child);
				previous = child;
			}
			previous.setAllowsChildren(false);
			previous.setUserObject(en.getValue());
			if (fileBase != null) {
				Asset asset = en.getValue();
				try {
					String loc = asset.hash.substring(0, 2)+"/"+asset.hash;
					asset.contentType = Files.probeContentType(fileBase.resolve(loc));
					if (asset.contentType == null) {
						asset.contentType = "unknown";
					}
				} catch (IOException e) {
					e.printStackTrace();
					asset.contentType = "unknown";
				}
			}
		}
		tree.expandRow(0);
		return tree;
	}

	public static AssetIndex load(String name, String in) {
		AssetIndex idx = EtherealBrowser.gson.fromJson(in, AssetIndex.class);
		idx.name = name;
		for (Map.Entry<String, Asset> en : idx.objects.entrySet()) {
			en.getValue().path = en.getKey();
			String[] split = en.getKey().split("/");
			en.getValue().name = split[split.length-1];
		}
		return idx;
	}

}
