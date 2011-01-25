/*
 * org.openmicroscopy.shoola.agents.treeviewer.actions.ImportAction 
 *
 *------------------------------------------------------------------------------
 *  Copyright (C) 2006-2009 University of Dundee. All rights reserved.
 *
 *
 * 	This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *------------------------------------------------------------------------------
 */
package org.openmicroscopy.shoola.agents.treeviewer.actions;



//Java imports
import java.awt.event.ActionEvent;
import javax.swing.Action;

//Third-party libraries

//Application-internal dependencies
import org.openmicroscopy.shoola.agents.treeviewer.IconManager;
import org.openmicroscopy.shoola.agents.treeviewer.browser.Browser;
import org.openmicroscopy.shoola.agents.treeviewer.cmd.CreateCmd;
import org.openmicroscopy.shoola.agents.treeviewer.view.TreeViewer;
import org.openmicroscopy.shoola.agents.util.browser.TreeImageDisplay;
import org.openmicroscopy.shoola.util.ui.UIUtilities;

import pojos.DatasetData;
import pojos.ProjectData;
import pojos.ScreenData;

/** 
 * Action to import the images.
 *
 * @author  Jean-Marie Burel &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:j.burel@dundee.ac.uk">j.burel@dundee.ac.uk</a>
 * @author Donald MacDonald &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:donald@lifesci.dundee.ac.uk">donald@lifesci.dundee.ac.uk</a>
 * @version 3.0
 * <small>
 * (<b>Internal version:</b> $Revision: $Date: $)
 * </small>
 * @since 3.0-Beta4
 */
public class ImportAction
	extends TreeViewerAction
{

	/** The name of the action. */
    private static final String NAME = "Import...";
    
    /** The description of the action. */
    private static final String DESCRIPTION = "Import the selected images.";
    
    /** The type of node to create. */
    private int nodeType;
    
    /** 
     * Sets the action enabled depending on the state of the {@link Browser}.
     * @see TreeViewerAction#onBrowserStateChange(Browser)
     */
    protected void onBrowserStateChange(Browser browser)
    {
        if (browser == null) return;
        switch (browser.getState()) {
	        case Browser.LOADING_DATA:
	        case Browser.LOADING_LEAVES:
	        //case Browser.COUNTING_ITEMS:  
	            setEnabled(false);
	            break;
	        default:
	        	onDisplayChange(browser.getLastSelectedDisplay());
        }
    }
    
    /**
     * Sets the action enabled depending on the selected node.
     * @see TreeViewerAction#onDisplayChange(TreeImageDisplay)
     */
    protected void onDisplayChange(TreeImageDisplay selectedDisplay)
    {
    	Browser browser = model.getSelectedBrowser();
    	setEnabled(false);
        if (browser == null) 
        	return;
        if (selectedDisplay == null) {
        	setEnabled(true);
        	return;
        }
        TreeImageDisplay[] nodes = browser.getSelectedDisplays();
        if (nodes != null && nodes.length > 1) {
        	setEnabled(false);
        	return;
        }
        Object ho = selectedDisplay.getUserObject();
        if (ho instanceof ProjectData || ho instanceof ScreenData || 
        		ho instanceof DatasetData)
        {
        	setEnabled(model.isUserOwner(ho));
        	nodeType = CreateCmd.IMAGE;
        }
    }
    
    /**
     * Creates a new instance.
     * 
     * @param model Reference to the Model. Mustn't be <code>null</code>.
     */
	public ImportAction(TreeViewer model)
	{
		super(model);
		name = NAME;  
		putValue(Action.SHORT_DESCRIPTION, 
				UIUtilities.formatToolTipText(DESCRIPTION));
		IconManager im = IconManager.getInstance();
		putValue(Action.SMALL_ICON, im.getIcon(IconManager.IMPORTER));
	}

	/**
     * Creates a {@link CreateCmd} command to execute the action.
     * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
     */
    public void actionPerformed(ActionEvent e)
    {
       CreateCmd cmd = new CreateCmd(model, CreateCmd.IMAGE);
       cmd.execute();
    }
    
}
