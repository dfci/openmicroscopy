/*
 * org.openmicroscopy.shoola.agents.imviewer.util.ProjectionRef 
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
package org.openmicroscopy.shoola.agents.imviewer.util.proj;

import java.util.List;

import pojos.DatasetData;

//Java imports

//Third-party libraries

//Application-internal dependencies

/** 
 * Utility class storing the projection parameters.
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
public class ProjectionRef
{

	/** The first z-section to project. */
	private int               startZ;
	
	/** The last z-section to project. */
	private int               endZ;
	
	/** The stepping used while projecting. Default is <code>1</code>. */
	private int               stepping;
	
	/** The type of projection. */
	private int               type;
	
	/** The collection of datasets where to store the projected image. */
	private List<DatasetData> datasets;
	
	/** The name of the projected image. */
	private String            name;
	
	/** 
	 * Project all channels if <code>true</code>, project the active channels
	 * if <code>false</code>
	 */
	private boolean           allChannels;
	
	/** Creates a new instance.  */
	ProjectionRef()
	{
		setStepping(1);
	}
	
	/**
	 * Creates a new instance.
	 * 
	 * @param startZ 		The first z-section to project.
	 * @param endZ   		The last z-section to project.
	 * @param stepping 	The freqence between each step. 
	 * 						Default is <code>1</code>.
	 * @param type 			The type of projection.
	 */
	ProjectionRef(int startZ, int endZ, int type)
	{
		this(startZ, endZ, 1, type);
	}
	
	/**
	 * Creates a new instance.
	 * 
	 * @param startZ 	The first z-section to project.
	 * @param endZ   	The last z-section to project.
	 * @param frequence The freqence between each step. 
	 * 					Default is <code>1</code>.
	 * @param type 		The type of projection.
	 */
	ProjectionRef(int startZ, int endZ, int frequence, int type)
	{
		setZInterval(startZ, endZ);
		setStepping(frequence);
		setType(type);
	}
	
	/**
	 * Sets the stepping.
	 * 
	 * @param stepping The stepping between each step. 
	 * 					Default is <code>1</code>.
	 */
	void setStepping(int stepping)
	{
		if (stepping < 1) stepping = 1;
		this.stepping = stepping;
	}
	
	/**
	 * Sets the interval to project.
	 * 
	 * @param startZ The first z-section to project.
	 * @param endZ	 The last z-section to project.
	 */
	void setZInterval(int startZ, int endZ)
	{
		if (startZ > endZ) 
			throw new IllegalArgumentException("Interval not valid.");
		this.startZ = startZ;
		this.endZ = endZ;
	}
	
	/**
	 * Sets the type of projection.
	 * 
	 * @param type The type of projection.
	 */
	void setType(int type) { this.type = type; }
	
	/**
	 * Sets the name of the projected image.
	 * 
	 * @param name The value to set.
	 */
	void setImageName(String name) { this.name = name; }
	
	/**
	 * Sets the collection of datasets to add the image to.
	 * 
	 * @param datasets The collection to set.
	 */
	void setDatasets(List<DatasetData> datasets) { this.datasets = datasets; }
	
	/**
	 * Sets to <code>true</code> to project all channels, to <code>false</code>
	 * to projet the active channels.
	 * 
	 * @param allChannels Pass code>true</code> to project all channels,
	 *                    <code>false</code> to projet the active channels.
	 * 					  
	 */
	void setAllChannels(boolean allChannels) { this.allChannels = allChannels; }
	
	/**
	 * Returns the name of the projected image.
	 * 
	 * @return See above.
	 */
	public String getImageName() { return name; }
	
	/**
	 * Returns the collection of datasets to add the image to.
	 * 
	 * @return See above.
	 */
	public List<DatasetData> getDatasets() { return datasets; }
	
	/**
	 * Returns <code>true</code> to project all channels,  <code>false</code>
	 * to projet the active channels.
	 * 
	 * @return See above.
	 */
	public boolean isAllChannels() { return allChannels; }
	
	/**
	 * Returns the first z-section to project.
	 * 
	 * @return See above.
	 */
	public int getStartZ() { return startZ; }
	
	/**
	 * Returns the last z-section to project.
	 * 
	 * @return See above.
	 */
	public int getEndZ() { return endZ; }
	
	/**
	 * Returns the freqence between each step. Default is <code>1</code>.
	 * 
	 * @return See above.
	 */
	public int getStepping() { return stepping; }
	
	/**
	 * Returns the type of projection.
	 * 
	 * @return See above.
	 */
	public int getType() { return type; }
	
}
