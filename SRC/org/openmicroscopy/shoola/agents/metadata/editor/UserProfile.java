/*
 * org.openmicroscopy.shoola.agents.metadata.editor.UserProfile 
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
package org.openmicroscopy.shoola.agents.metadata.editor;



//Java imports
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;


//Third-party libraries

//Application-internal dependencies
import org.openmicroscopy.shoola.agents.metadata.MetadataViewerAgent;
import org.openmicroscopy.shoola.agents.util.EditorUtil;
import org.openmicroscopy.shoola.agents.util.ui.GroupsRenderer;
import org.openmicroscopy.shoola.env.LookupNames;
import org.openmicroscopy.shoola.env.config.Registry;
import org.openmicroscopy.shoola.env.ui.UserNotifier;
import org.openmicroscopy.shoola.util.ui.UIUtilities;
import pojos.ExperimenterData;
import pojos.GroupData;

/** 
 * Component displaying the user details.
 *
 * @author  Jean-Marie Burel &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:j.burel@dundee.ac.uk">j.burel@dundee.ac.uk</a>
 * @author Donald MacDonald &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:donald@lifesci.dundee.ac.uk">donald@lifesci.dundee.ac.uk</a>
 * @version 3.0
 * <small>
 * (<b>Internal version:</b> $Revision: $Date: $)
 * </small>
 * @since OME3.0
 */
class UserProfile 	
	extends JPanel
	implements ActionListener, ChangeListener, DocumentListener
{
    
	/** Text of the label in front of the new password area. */
	private static final String		PASSWORD_OLD = "Old password";
	
	/** Text of the label in front of the new password area. */
	private static final String		PASSWORD_NEW = "New password";
	
	/** Text of the label in front of the confirmation password area. */
	private static final String		PASSWORD_CONFIRMATION = "Confirm password";
	
	/** The title of the dialog displayed if a problem occurs. */
	private static final String		PASSWORD_CHANGE_TITLE = "Change Password";
	
    /** The items that can be edited. */
    private Map<String, JTextField>	items;
    
    /** UI component displaying the groups, the user is a member of. */
    private JComboBox				groups;

    /** Password field to enter the new password. */
    private JPasswordField			passwordNew;
    
    /** Password field to confirm the new password. */
    private JPasswordField			passwordConfirm;
    
    /** Hosts the old password. */
    private JPasswordField			oldPassword;

    /** Modify password. */
    private JButton					passwordButton;
    
    /** Box to make the selected user an administrator. */
    private JCheckBox				adminBox;
    
    /** Box to make the user active or not. */
    private JCheckBox				activeBox;
    
    /** Box to make the selected user the owner of the group is a member of. */
    private JCheckBox				ownerBox;
    
	/** Reference to the Model. */
    private EditorModel				model;
    
    /** The original index. */
    private int						originalIndex;
    
    /** The currently selected index. */
    private int						selectedIndex;
    
    /** The user's details. */
    private Map						details;
    
    /** The groups the user is a member of. */
    private GroupData[] 			groupData;

    /** Flag indicating that the selected user is an owner of the group. */
    private boolean					groupOwner;
    
    /** Indicates that the user is an administrator. */
    private boolean					admin;
    
    /** Indicates that the user is active or not. */
    private boolean					active;
 
    /** Modifies the existing password. */
    private void changePassword()
    {
    	UserNotifier un;
    	StringBuffer buf = new StringBuffer();
        buf.append(passwordNew.getPassword());
        String newPass = buf.toString();
        
        String pass = buf.toString();
        buf = new StringBuffer();
        buf.append(passwordConfirm.getPassword());
        String confirm = buf.toString();

        buf = new StringBuffer();
        buf.append(oldPassword.getPassword());
        String old = buf.toString();
        if (old == null || old.trim().length() == 0) {
        	un = MetadataViewerAgent.getRegistry().getUserNotifier();
        	un.notifyInfo(PASSWORD_CHANGE_TITLE, 
        				"Please specify your old password.");
        	oldPassword.requestFocus();
        	return;
        }
        if (newPass == null || newPass.length() == 0) {
        	un = MetadataViewerAgent.getRegistry().getUserNotifier();
        	un.notifyInfo(PASSWORD_CHANGE_TITLE, 
        			"Please enter your new password.");
        	passwordNew.requestFocus();
        	return;
        }

        if (pass == null || confirm == null || confirm.length() == 0 ||
        	!pass.equals(confirm)) {
        	un = MetadataViewerAgent.getRegistry().getUserNotifier();
            un.notifyInfo(PASSWORD_CHANGE_TITLE, 
            			"The passwords entered do not match. " +
            			"Please try again.");
            passwordNew.setText("");
            passwordConfirm.setText("");
            passwordNew.requestFocus();
            return;
        }
        model.changePassword(old, confirm);
    }

    /** Initializes the components composing this display. */
    private void initComponents()
    {
    	adminBox = new JCheckBox();
    	adminBox.setVisible(false);
    	ownerBox = new JCheckBox();
    	activeBox = new JCheckBox();
    	activeBox.setVisible(false);
    	passwordButton =  new JButton("Change password");
    	passwordButton.setBackground(UIUtilities.BACKGROUND_COLOR);
    	passwordButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {  
            	changePassword(); 
            }
        });
    	passwordNew = new JPasswordField();
    	passwordNew.setBackground(UIUtilities.BACKGROUND_COLOR);
    	passwordConfirm = new JPasswordField();
    	passwordConfirm.setBackground(UIUtilities.BACKGROUND_COLOR);
    	oldPassword = new JPasswordField();
    	oldPassword.setBackground(UIUtilities.BACKGROUND_COLOR);
    	items = new HashMap<String, JTextField>();
    	ExperimenterData user = (ExperimenterData) model.getRefObject();
    	List userGroups = user.getGroups();
    	GroupData defaultGroup = user.getDefaultGroup();
		long groupID = defaultGroup.getId();
		//Build the array for box.
		Iterator i = userGroups.iterator();
		//Remove not visible group
		GroupData g;
		
		List<GroupData> validGroups = new ArrayList<GroupData>();
		while (i.hasNext()) {
			g = (GroupData) i.next();
			if (model.isValidGroup(g))
				validGroups.add(g);
		}
		groupData = new GroupData[validGroups.size()];
		groupOwner = false;
		admin = false;
		active = false;
		int selectedIndex = 0;
		int index = 0;
		i = validGroups.iterator();
		boolean owner = false;
		while (i.hasNext()) {
			g = (GroupData) i.next();
			groupData[index] = g;
			if (g.getId() == groupID) {
				owner = setGroupOwner(g);
				originalIndex = index;
			}
			index++;
		}
		selectedIndex = originalIndex;
		//sort by name
		groups = EditorUtil.createComboBox(groupData, 0);
		groups.setEnabled(false);
		groups.setRenderer(new GroupsRenderer());
		if (groupData.length != 0)
			groups.setSelectedIndex(selectedIndex);
		
		if (MetadataViewerAgent.isAdministrator()) {
			owner = true;
			adminBox.setVisible(true);
			activeBox.setVisible(true);
			activeBox.setSelected(true);
			adminBox.addChangeListener(this);
			activeBox.addChangeListener(this);
			active = true;
			admin = false;
		}
		ownerBox.setEnabled(owner);
		ownerBox.addChangeListener(this);
		/*
		if (isOwner) {
			groups.addActionListener(this);
			groups.setEnabled(true);
		} else groups.setEnabled(false);
		*/
    }
    
    /**
     * Selects or not the {@link #ownerBox} if the selected user 
     * is an owner. Returns <code>true</code> if the currently logged in 
     * user is an owner of the group.
     * 
     * @param group The group to handle.
     */
    private boolean setGroupOwner(GroupData group)
    {
    	ExperimenterData ref = (ExperimenterData) model.getRefObject();
    	long userID = MetadataViewerAgent.getUserDetails().getId();
    	Set leaders = group.getLeaders();
    	ExperimenterData exp;
    	boolean isOwner = false;
    	if (leaders != null) {
    		Iterator i = leaders.iterator();
        	while (i.hasNext()) {
    			exp = (ExperimenterData) i.next();
    			if (exp.getId() == ref.getId()) {
    				groupOwner = true;
    				ownerBox.setSelected(true);
    			}
    			if (exp.getId() == userID)
    				isOwner = true;
    		}
    	}
    	return isOwner;
    }
    
    /**
     * Builds the panel hosting the user's details.
     * 
     * @return See above.
     */
    private JPanel buildContentPanel()
    {
    	ExperimenterData user = (ExperimenterData) model.getRefObject();
    	boolean editable = model.isUserOwner(user);
    	details = EditorUtil.convertExperimenter(user);
        JPanel content = new JPanel();
        content.setBorder(
				BorderFactory.createTitledBorder("Profile"));
    	content.setBackground(UIUtilities.BACKGROUND_COLOR);
    	Entry entry;
    	Iterator i = details.entrySet().iterator();
        JLabel label;
        JTextField area;
        String key, value;
        content.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(0, 2, 2, 0);
		//Add log in name but cannot edit.
		c.gridx = 0;
        c.gridy++;
        label = UIUtilities.setTextFont(EditorUtil.DISPLAY_NAME);
        c.gridwidth = GridBagConstraints.RELATIVE; //next-to-last
        c.fill = GridBagConstraints.NONE;      //reset to default
        c.weightx = 0.0;  
        content.add(label, c);
        c.gridx++;
        content.add(Box.createHorizontalStrut(5), c); 
        c.gridx++;
        c.gridwidth = GridBagConstraints.REMAINDER;     //end row
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        area = new JTextField(user.getUserName());
        area.setEnabled(false);
        area.setEditable(false);
        content.add(area, c);  
		
		
        while (i.hasNext()) {
            ++c.gridy;
            c.gridx = 0;
            entry = (Entry) i.next();
            key = (String) entry.getKey();
            value = (String) entry.getValue();
            label = UIUtilities.setTextFont(key);
            area = new JTextField(value);
            area.setBackground(UIUtilities.BACKGROUND_COLOR);
            if (editable) {
            	area.setEditable(editable);
            	area.getDocument().addDocumentListener(this);
            }
            items.put(key, area);
            label.setBackground(UIUtilities.BACKGROUND_COLOR);
            c.gridwidth = GridBagConstraints.RELATIVE; //next-to-last
            c.fill = GridBagConstraints.NONE;      //reset to default
            c.weightx = 0.0;  
            content.add(label, c);
            label.setLabelFor(area);
            c.gridx++;
            content.add(Box.createHorizontalStrut(5), c); 
            c.gridx++;
            c.gridwidth = GridBagConstraints.REMAINDER;     //end row
            c.fill = GridBagConstraints.HORIZONTAL;
            c.weightx = 1.0;
            content.add(area, c);  
        }
        c.gridx = 0;
        c.gridy++;
        label = UIUtilities.setTextFont(EditorUtil.DEFAULT_GROUP);
        c.gridwidth = GridBagConstraints.RELATIVE; //next-to-last
        c.fill = GridBagConstraints.NONE;      //reset to default
        c.weightx = 0.0;  
        content.add(label, c);
        c.gridx++;
        content.add(Box.createHorizontalStrut(5), c); 
        c.gridx++;
        c.gridwidth = GridBagConstraints.REMAINDER;     //end row
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        content.add(groups, c); 
        
        c.gridx = 0;
        c.gridy++;
        label = UIUtilities.setTextFont(EditorUtil.GROUP_OWNER);
        c.gridwidth = GridBagConstraints.RELATIVE; //next-to-last
        c.fill = GridBagConstraints.NONE;      //reset to default
        c.weightx = 0.0;  
        content.add(label, c);
        c.gridx++;
        content.add(Box.createHorizontalStrut(5), c); 
        c.gridx++;
        c.gridwidth = GridBagConstraints.REMAINDER;     //end row
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        content.add(ownerBox, c); 
        if (activeBox.isVisible()) {
        	c.gridx = 0;
            c.gridy++;
            label = UIUtilities.setTextFont(EditorUtil.ACTIVE);
            c.gridwidth = GridBagConstraints.RELATIVE; //next-to-last
            c.fill = GridBagConstraints.NONE;      //reset to default
            c.weightx = 0.0;  
            content.add(label, c);
            c.gridx++;
            content.add(Box.createHorizontalStrut(5), c); 
            c.gridx++;
            c.gridwidth = GridBagConstraints.REMAINDER;     //end row
            c.fill = GridBagConstraints.HORIZONTAL;
            c.weightx = 1.0;
            content.add(activeBox, c);  
        }
        if (adminBox.isVisible()) {
        	c.gridx = 0;
            c.gridy++;
            label = UIUtilities.setTextFont(EditorUtil.ADMINISTRATOR);
            c.gridwidth = GridBagConstraints.RELATIVE; //next-to-last
            c.fill = GridBagConstraints.NONE;      //reset to default
            c.weightx = 0.0;  
            content.add(label, c);
            c.gridx++;
            content.add(Box.createHorizontalStrut(5), c); 
            c.gridx++;
            c.gridwidth = GridBagConstraints.REMAINDER;     //end row
            c.fill = GridBagConstraints.HORIZONTAL;
            c.weightx = 1.0;
            content.add(adminBox, c);  
        }
        c.gridx = 0;
        c.gridy++;
        content.add(Box.createHorizontalStrut(10), c); 
        c.gridy++;
        label = UIUtilities.setTextFont(EditorUtil.MANDATORY_DESCRIPTION,
        		Font.ITALIC);
        c.weightx = 0.0;  
        content.add(label, c);
        return content;
    }
    
    /** 
     * Builds the UI component hosting the UI component used to modify 
     * the password.
     * 
     * @return See above.
     */
    private JPanel buildPasswordPanel()
    {
    	JPanel content = new JPanel();
    	content.setBackground(UIUtilities.BACKGROUND_COLOR);
    	Registry reg = MetadataViewerAgent.getRegistry();
    	String ldap = (String) reg.lookup(LookupNames.USER_AUTHENTICATION);
    	if (ldap != null && ldap.length() > 0) {
    		content.setBorder(
    				BorderFactory.createTitledBorder("LDAP Authentication"));
    		content.setLayout(new FlowLayout(FlowLayout.LEFT));
    		content.add(new JLabel(ldap));
    		return content;
    	}
    	content.setBorder(
				BorderFactory.createTitledBorder("Change Password"));
    	
		content.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(0, 2, 2, 0);
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = GridBagConstraints.RELATIVE; //next-to-last
		c.fill = GridBagConstraints.NONE;      //reset to default
		c.weightx = 0.0;  
    	content.add(UIUtilities.setTextFont(PASSWORD_OLD), c);
    	c.gridx++;
    	c.gridwidth = GridBagConstraints.REMAINDER;     //end row
    	c.fill = GridBagConstraints.HORIZONTAL;
    	c.weightx = 1.0;
    	content.add(oldPassword, c);
    	c.gridy++;
    	c.gridx = 0;
    	c.gridwidth = GridBagConstraints.RELATIVE; //next-to-last
		c.fill = GridBagConstraints.NONE;      //reset to default
		c.weightx = 0.0;  
    	content.add(UIUtilities.setTextFont(PASSWORD_NEW), c);
    	c.gridx++;
    	c.gridwidth = GridBagConstraints.REMAINDER;     //end row
    	c.fill = GridBagConstraints.HORIZONTAL;
    	c.weightx = 1.0;
    	content.add(passwordNew, c);
    	c.gridy++;
    	c.gridx = 0;
    	c.gridwidth = GridBagConstraints.RELATIVE; //next-to-last
		c.fill = GridBagConstraints.NONE;      //reset to default
		c.weightx = 0.0;  
    	content.add(UIUtilities.setTextFont(PASSWORD_CONFIRMATION), c);
    	c.gridx++;
    	c.gridwidth = GridBagConstraints.REMAINDER;     //end row
    	c.fill = GridBagConstraints.HORIZONTAL;
    	c.weightx = 1.0;
    	content.add(passwordConfirm, c);
    	c.gridy++;
    	c.gridx = 0;
    	JPanel p = new JPanel();
    	p.setBackground(UIUtilities.BACKGROUND_COLOR);
    	p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
    	p.add(content);
    	JPanel buttonPanel = UIUtilities.buildComponentPanel(passwordButton);
    	buttonPanel.setBackground(UIUtilities.BACKGROUND_COLOR);
    	p.add(buttonPanel);
    	return p;
    }

    /** Message displayed when one of the required fields is left blank. */
    private void showRequiredField()
    {
    	UserNotifier un = MetadataViewerAgent.getRegistry().getUserNotifier();
        un.notifyInfo("Edit Profile", "The required fields cannot be left " +
        		"blank.");
        return;
    }
    
    /**
     * Creates a new instance.
     * 
     * @param model	Reference to the model. Mustn't be <code>null</code>. 
     * @param view 	Reference to the control. Mustn't be <code>null</code>.                     
     */
	UserProfile(EditorModel model)
	{
		if (model == null)
			throw new IllegalArgumentException("No model.");
		this.model = model;
		setBackground(UIUtilities.BACKGROUND_COLOR);
	}
 
	/** Builds and lays out the UI. */
    void buildGUI()
    {
    	removeAll();
    	initComponents();
    	setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
    	c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(0, 2, 2, 0);
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = GridBagConstraints.RELATIVE; //next-to-last
		c.weightx = 1.0;  
    	add(buildContentPanel(), c);
    	if (model.isUserOwner(model.getRefObject())) {
    		c.gridy++;
    		add(Box.createVerticalStrut(5), c); 
    		c.gridy++;
    		add(buildPasswordPanel(), c);
    	}
    }
    
	/** Clears the password fields. */
	void passwordChanged()
	{
		oldPassword.setText("");
		passwordNew.setText("");
        passwordConfirm.setText("");
	}

	/**
	 * Returns <code>true</code> if data to save, <code>false</code>
	 * otherwise.
	 * 
	 * @return See above.
	 */
	boolean hasDataToSave()
	{
		if (selectedIndex != originalIndex) return true;
		if (details == null) return false;
		Entry entry;
		Iterator i = details.entrySet().iterator();
		String key;
		String value;
		JTextField field;
		String v;
		while (i.hasNext()) {
			entry = (Entry) i.next();
			key = (String) entry.getKey();
			field = items.get(key);
			v = field.getText();
			if (v != null) {
				v = v.trim();
				value = (String) entry.getValue();
				if (value != null && !v.equals(value))
					return true;
			}
		}
		Boolean b = ownerBox.isSelected();
		if (b.compareTo(groupOwner) != 0) return true;
		if (adminBox.isVisible()) {
			b = adminBox.isSelected();
			if (b.compareTo(admin) != 0) return true;
		}
		if (activeBox.isVisible()) {
			b = activeBox.isSelected();
			if (b.compareTo(active) != 0) return true;
		}
		return false;
	}

	/**
	 * Returns the experimenter to save.
	 * 
	 * @return See above.
	 */
	ExperimenterData getExperimenterToSave()
	{
		ExperimenterData original = (ExperimenterData) model.getRefObject();
    	//Required fields first
    	JTextField f = items.get(EditorUtil.LAST_NAME);
    	String v = f.getText();
    	if (v == null || v.trim().length() == 0) showRequiredField();
    	original.setLastName(v);
    	f = items.get(EditorUtil.EMAIL);
    	v = f.getText();
    	if (v == null || v.trim().length() == 0) showRequiredField();
    	original.setEmail(v);
    	f = items.get(EditorUtil.INSTITUTION);
    	v = f.getText();
    	if (v == null) v = "";
    	original.setInstitution(v.trim());
    	f = items.get(EditorUtil.FIRST_NAME);
    	v = f.getText();
    	if (v == null) v = "";
    	original.setFirstName(v.trim());
    	
    	//set the groups
    	if (selectedIndex != originalIndex) {
    		GroupData g = null;
    		if (selectedIndex < groupData.length)
    			g = groupData[selectedIndex];
    		ExperimenterData user = (ExperimenterData) model.getRefObject();
    		List userGroups = user.getGroups();
    		List<GroupData> newGroups = new ArrayList<GroupData>();
    		if (g != null) newGroups.add(g);
    		Iterator i = userGroups.iterator();
    		long id = -1;
    		if (g != null) id = g.getId();
    		GroupData group;
    		while (i.hasNext()) {
				group = (GroupData) i.next();
				if (group.getId() != id)
					newGroups.add(group);
			}
    		//Need to see what to do b/c no ExperimenterGroupMap
    		original.setGroups(newGroups);
    	}
		return original;//newOne;
	}
	
	/** 
	 * Fires a property change event when a index is selected.
	 * @see ActionListener#actionPerformed(ActionEvent)
	 */
	public void actionPerformed(ActionEvent e)
	{
		selectedIndex = groups.getSelectedIndex();
		buildGUI();
		firePropertyChange(EditorControl.SAVE_PROPERTY, Boolean.valueOf(false), 
				Boolean.valueOf(true));
	}
	
	/**
	 * Fires property indicating that some text has been entered.
	 * @see DocumentListener#insertUpdate(DocumentEvent)
	 */
	public void insertUpdate(DocumentEvent e)
	{
		firePropertyChange(EditorControl.SAVE_PROPERTY, Boolean.valueOf(false), 
				Boolean.valueOf(true));
	}

	/**
	 * Fires property indicating that some text has been entered.
	 * @see DocumentListener#removeUpdate(DocumentEvent)
	 */
	public void removeUpdate(DocumentEvent e)
	{
		firePropertyChange(EditorControl.SAVE_PROPERTY, Boolean.valueOf(false), 
				Boolean.valueOf(true));
	}
	
	/**
	 * Fires property indicating that some values have changed.
	 * @see ChangeListener#stateChanged(ChangeEvent)
	 */
	public void stateChanged(ChangeEvent e)
	{
		firePropertyChange(EditorControl.SAVE_PROPERTY, Boolean.valueOf(false), 
				Boolean.valueOf(true));
	}
	
	/**
	 * Required by the {@link DocumentListener} I/F but no-op implementation
	 * in our case.
	 * @see DocumentListener#changedUpdate(DocumentEvent)
	 */
	public void changedUpdate(DocumentEvent e) {}
	
}
