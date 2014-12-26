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

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import com.gameminers.ethereal.browser.glue.Asset;
import com.gameminers.ethereal.browser.utility.Resources;

public class AssetTreeCellRenderer extends DefaultTreeCellRenderer {
	private static final long serialVersionUID = -3990293584047882025L;
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,
				row, hasFocus);
		Object obj = ((DefaultMutableTreeNode)value).getUserObject();
		if (obj instanceof Asset) {
			Asset asset = (Asset)obj;
			String mime = asset.contentType;
			boolean retry = true;
			while (retry) {
				String ico = "mime/"+mime.replace('/', '-');
				if (Resources.getAssetURL(ico+".png") != null) {
					setIcon(Resources.getPNGIconAsset(ico));
					retry = false;
				} else {
					System.out.println("Unknown mime type "+mime);
					if (mime.endsWith("generic")) {
						setIcon(Resources.getPNGIconAsset("mime/unknown"));
						retry = false;
					} else {
						mime = mime.split("/")[0]+"/x-generic";
						retry = true;
					}
				}
			}
			setToolTipText(mime);
		} else {
			setIcon(Resources.getPNGIconAsset("mime/inode-directory"));
			setToolTipText("inode/directory");
		}
		return this;
	}
}
