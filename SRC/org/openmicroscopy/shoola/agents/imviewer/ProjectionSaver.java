/*
 * org.openmicroscopy.shoola.agents.imviewer.ProjectionSaver 
 *
 *------------------------------------------------------------------------------
 *  Copyright (C) 2006-2008 University of Dundee. All rights reserved.
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
package org.openmicroscopy.shoola.agents.imviewer;


//Java imports
import java.awt.image.BufferedImage;

//Third-party libraries

//Application-internal dependencies
import org.openmicroscopy.shoola.agents.imviewer.util.proj.ProjectionRef;
import org.openmicroscopy.shoola.agents.imviewer.view.ImViewer;
import org.openmicroscopy.shoola.env.data.events.DSCallAdapter;
import org.openmicroscopy.shoola.env.data.views.CallHandle;
import org.openmicroscopy.shoola.env.log.LogMessage;
import pojos.ImageData;

/** 
 * Creates a projected image for preview or a projection of the whole image.
 * For a preview projected image, the currently selected timepoint and 
 * the active channels are taken into account.
 * For an image's creation, the user selects the active channels or the
 * whole image, the name of the projected image and where to save the 
 * the image.
 *
 * @author  Jean-Marie Burel &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:j.burel@dundee.ac.uk">j.burel@dundee.ac.uk</a>
 * @author Donald MacDonald &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:donald@lifesci.dundee.ac.uk">donald@lifesci.dundee.ac.uk</a>
 * @version 3.0
 * <small>
 * (<b>Internal version:</b> $Revision: $Date: $)
 * </small>
 * @since 3.0-Beta3
 */
public class ProjectionSaver 
	extends DataLoader
{

	/** Indicates to create a preview image. */
	public static final int PREVIEW = 0;
	
	/** Indicates to create a preview image. */
	public static final int PROJECTION = 1;
	
	/** One of the constants defined by this class. */
	private int 		  index;
	
	/** The id of the pixels set. */
	private long          pixelsID;
	
	/** Object containing the projection's parameters. */
	private ProjectionRef ref;
	
	/** Handle to the async call so that we can cancel it. */
    private CallHandle  handle;
    
    /**
     * Creates a new instance.
     * 
     * @param model    Reference to the model. Mustn't be <code>null</code>.
     * @param pixelsID The id of the pixels set.
     * @param ref      Object containing the projection's parameters.
     * @param index    One of the constants defined by this class. 
     */
	public ProjectionSaver(ImViewer model, long pixelsID, ProjectionRef ref, 
			int index)
	{
		super(model);
		if (pixelsID < 0)
			throw new IllegalArgumentException("Pixels Id not valid.");
		if (ref == null)
			throw new IllegalArgumentException("No projection's parameters.");
		this.ref = ref;
		this.pixelsID = pixelsID;
		this.index = index;
	}
	
	/**
     * Creates a preview image or creates a projected image.
     * @see DataLoader#load()
     */
    public void load()
    {
        switch (index) {
			case PREVIEW:
				handle = ivView.renderProjected(pixelsID, ref.getStartZ(), 
						ref.getEndZ(), ref.getStepping(), ref.getType(), this);
				break;
			case PROJECTION:
		}
    }

    /**
     * Cancels the ongoing data retrieval.
     * @see DataLoader#cancel()
     */
    public void cancel() { handle.cancel(); }
    
    /**
     * Notifies the user that an error has occurred and discards the 
     * {@link #viewer}.
     * @see DSCallAdapter#handleException(Throwable)
     */
    public void handleException(Throwable exc) 
    {
        String s = "Data Retrieval Failure: ";
        LogMessage msg = new LogMessage();
        msg.print(s);
        msg.print(exc);
        registry.getLogger().error(this, msg);
        switch (index) {
			case PREVIEW:
				viewer.setRenderProjected(null);
				break;
			case PROJECTION:
				viewer.setRenderProjected(null);
		}
    }
    
    /** 
     * Feeds the result back to the viewer. 
     * @see DataLoader#handleResult(Object)
     */
    public void handleResult(Object result)
    {
        if (viewer.getState() == ImViewer.DISCARDED) return;  //Async cancel.
        switch (index) {
	        case PREVIEW:
	        	viewer.setRenderProjected((BufferedImage) result);
	        	break;
	        case PROJECTION:
	        	viewer.setProjectedImage((ImageData) result);
        }
    }
    
}
