/*
 * org.openmicroscopy.shoola.agents.treeviewer.ClassifierPathsLoader
 *
 *------------------------------------------------------------------------------
 *  Copyright (C) 2006 University of Dundee. All rights reserved.
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

package org.openmicroscopy.shoola.agents.treeviewer;


//Java imports
import java.util.Set;

//Third-party libraries

//Application-internal dependencies
import org.openmicroscopy.shoola.agents.treeviewer.clsf.Classifier;
import org.openmicroscopy.shoola.env.data.views.CallHandle;
import org.openmicroscopy.shoola.env.data.views.DataManagerView;
import pojos.ExperimenterData;

/** 
 * Loads the CategoryGroup/Category paths containing the specified image
 * if the mode is {@link Classifier#DECLASSIFY_MODE} or loads
 * the available CategoryGroup/Category paths if the mode is 
 * {@link Classifier#CLASSIFY_MODE}.
 * This class calls the <code>loadClassificationPaths</code> method in the
 * <code>DataManagerView</code>.
 *
 * @author  Jean-Marie Burel &nbsp;&nbsp;&nbsp;&nbsp;
 * 				<a href="mailto:j.burel@dundee.ac.uk">j.burel@dundee.ac.uk</a>
 * @version 2.2
 * <small>
 * (<b>Internal version:</b> $Revision$ $Date$)
 * </small>
 * @since OME2.2
 */
public class ClassifierPathsLoader
    extends ClassifierLoader
{

    /** The id of the images to classify or declassify. */
    private Set        imageIDs;
    
    /** The type of classifier. */
    private int         mode;
    
    /** The id of the root node. */
    private long        rootID;
    
    /** Handle to the async call so that we can cancel it. */
    private CallHandle  handle;
    
    /**
     * Controls if the specified mode is supported.
     * 
     * @param m The value to control.
     */
    private void checkMode(int m)
    {
        switch (m) {
            case Classifier.CLASSIFY_MODE:
            case Classifier.DECLASSIFY_MODE:   
                return;
            default:
                throw new IllegalArgumentException("Mode not supported.");
        }
    }
    
    /**
     * Creates a new instance. 
     * 
     * @param viewer        The TreeViewer this data loader is for.
     *                      Mustn't be <code>null</code>.
     * @param imageIDs      The id of the images. 
     * @param mode          The type of classifier. One of the following 
     *                      constants:
     *                      {@link Classifier#DECLASSIFY_MODE} or 
     *                      {@link Classifier#CLASSIFY_MODE}.
     * @param rootID    	The id of the root.    
     */
    public ClassifierPathsLoader(Classifier viewer, Set imageIDs, int mode,
                                long rootID)
    {
        super(viewer);
        if (imageIDs == null) 
            throw new IllegalArgumentException("No images.");
        if (imageIDs.size() == 0) 
            throw new IllegalArgumentException("No images.");
        checkMode(mode);
        this.rootID = rootID;
        this.imageIDs = imageIDs;
        this.mode = mode;
    }
    
    /** 
     * Retrieves the CategoryGroup/Category paths containing the image
     * if the mode is {@link Classifier#DECLASSIFY_MODE} or 
     * retrieves the available paths if the mode is 
     * {@link Classifier#CLASSIFY_MODE}.
     * @see ClassifierLoader#load()
     */
    public void load()
    {
        switch (mode) {
            case Classifier.DECLASSIFY_MODE:
                handle = dmView.loadClassificationPaths(imageIDs,
                        DataManagerView.DECLASSIFICATION, 
                        ExperimenterData.class, rootID, this);
                break;
            case Classifier.CLASSIFY_MODE:
               handle = dmView.loadClassificationPaths(imageIDs,
                        DataManagerView.CLASSIFICATION_NME, 
                        ExperimenterData.class, rootID, this);
        } 
    }

    /** 
     * Cancels the data loading. 
     * @see ClassifierLoader#cancel()
     */
    public void cancel() { handle.cancel(); }
    
    /**
     * Feeds the result back to the viewer.
     * @see ClassifierLoader#handleResult(Object)
     */
    public void handleResult(Object result)
    {
        if (viewer.getState() == Classifier.DISCARDED) return; //Async cancel.
        viewer.setClassifications((Set) result);
    }
    
}
