/*
 * org.openmicroscopy.shoola.agents.hiviewer.view.RegExFinderMng
 *
 *------------------------------------------------------------------------------
 *
 *  Copyright (C) 2004 Open Microscopy Environment
 *      Massachusetts Institute of Technology,
 *      National Institutes of Health,
 *      University of Dundee
 *
 *
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with this library; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *------------------------------------------------------------------------------
 */

package org.openmicroscopy.shoola.agents.hiviewer.search;


//Java imports
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;

//Third-party libraries

//Application-internal dependencies
import org.openmicroscopy.shoola.agents.hiviewer.cmd.FindRegExCmd;
import org.openmicroscopy.shoola.agents.hiviewer.view.HiViewer;

/** 
 * Control of the {@link RegExFinder} view.
 *
 * @author  Jean-Marie Burel &nbsp;&nbsp;&nbsp;&nbsp;
 * 				<a href="mailto:j.burel@dundee.ac.uk">j.burel@dundee.ac.uk</a>
 * @author  <br>Andrea Falconi &nbsp;&nbsp;&nbsp;&nbsp;
 * 				<a href="mailto:a.falconi@dundee.ac.uk">
 * 					a.falconi@dundee.ac.uk</a>
 * @version 2.2
 * <small>
 * (<b>Internal version:</b> $Revision$ $Date$)
 * </small>
 * @since OME2.2
 */
class RegExFinderMng
    implements ActionListener
{
    
    /** Action command ID for the Find button. */
    private static final int        FIND = 0;
    
    /** Action command ID for the Cancel button. */
    private static final int        CANCEL = 1;
    
    private RegExFinder view;

    private HiViewer    model;
    
    private int         index;
    
    RegExFinderMng(RegExFinder view, HiViewer model, int index)
    {
        this.view = view;
        this.model = model;
        this.index = index;
        attachListeners();
    }
    
    /** Attach listeners to the components. */
    private void attachListeners()
    {
        attachButtonListeners(view.find, FIND);
        attachButtonListeners(view.cancel, CANCEL);
    }
    
    /** Attach an {@link ActionListener} to an {@link JButton}. */
    private void attachButtonListeners(JButton button, int id)
    {
        button.addActionListener(this);
        button.setActionCommand(""+id);
    }
    
    /** Find the specified regular expression. */
    private void findRegEx()
    {
        String regEx = view.regExField.getText();
        cancel();
        FindRegExCmd cmd = new FindRegExCmd(model, regEx, index);
        cmd.execute();
    }
    
    /** Close and dispose. */
    private void cancel()
    {
        view.setVisible(false);
        view.dispose();
    }
    
    /** Handle event fired by JButton. */
    public void actionPerformed(ActionEvent e)
    {
        try {
            int index = Integer.parseInt(e.getActionCommand());
            switch (index) { 
                case FIND:
                    findRegEx(); break;
                case CANCEL:
                    cancel();  
            }
        } catch(NumberFormatException nfe) {
            throw new Error("Invalid Action ID "+e.getActionCommand(), nfe);
        } 
    }
    
}
