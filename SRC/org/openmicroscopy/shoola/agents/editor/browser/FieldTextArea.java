 /*
 * org.openmicroscopy.shoola.agents.editor.browser.FieldTextArea 
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
package org.openmicroscopy.shoola.agents.editor.browser;

//Java imports

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.IllegalComponentStateException;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTML.Tag;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

//Third-party libraries

//Application-internal dependencies

import org.openmicroscopy.shoola.agents.editor.IconManager;
import org.openmicroscopy.shoola.agents.editor.browser.paramUIs.ITreeEditComp;
import org.openmicroscopy.shoola.agents.editor.browser.paramUIs.ParamEditorDialog;
import org.openmicroscopy.shoola.agents.editor.browser.paramUIs.TextBoxEditor;
import org.openmicroscopy.shoola.agents.editor.browser.paramUIs.editTemplate.FieldContentEditor;
import org.openmicroscopy.shoola.agents.editor.model.DataReference;
import org.openmicroscopy.shoola.agents.editor.model.Field;
import org.openmicroscopy.shoola.agents.editor.model.IAttributes;
import org.openmicroscopy.shoola.agents.editor.model.IField;
import org.openmicroscopy.shoola.agents.editor.model.IFieldContent;
import org.openmicroscopy.shoola.agents.editor.model.TextBoxStep;
import org.openmicroscopy.shoola.agents.editor.model.TextContent;
import org.openmicroscopy.shoola.agents.editor.model.TreeModelMethods;
import org.openmicroscopy.shoola.agents.editor.model.params.AbstractParam;
import org.openmicroscopy.shoola.agents.editor.model.params.FieldParamsFactory;
import org.openmicroscopy.shoola.agents.editor.model.params.IParam;
import org.openmicroscopy.shoola.agents.editor.model.params.TextParam;
import org.openmicroscopy.shoola.agents.editor.uiComponents.CustomButton;
import org.openmicroscopy.shoola.agents.editor.uiComponents.ImagePreview;

/** 
 * This Text Area is represents a Field/Step (or a node) of the data model tree,
 * when it is displayed in the "Text Document" view (rather than a JTree view).
 * The text is displayed using a {@link HtmlContentEditor}, but the editing
 * is managed by this class. 
 *
 * @author  William Moore &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:will@lifesci.dundee.ac.uk">will@lifesci.dundee.ac.uk</a>
 * @version 3.0
 * <small>
 * (<b>Internal version:</b> $Revision: $Date: $)
 * </small>
 * @since OME3.0
 */
public class FieldTextArea 
	extends JPanel
	implements FocusListener,
	PropertyChangeListener,
	ActionListener,
	KeyListener
{
	/**  The text area that displays and allows users to edit the text */
	protected HtmlContentEditor		contentEditor;
	
	/**  The text area that displays and allows users to edit the name */
	protected HtmlContentEditor		nameEditor;
	
	
	private static String 			addParamToolTip = 
									"Add a parameter to this step";
	
	/** The field that is represented by this UI component */
	private IField 					field;

	/** 
	 * The JTree that is coordinates selection of fields etc.
	 * When undoable edits are committed to the undo/redo queue, they include
	 * a reference to this JTree, so that they can select the correct node
	 * when undo is performed. 
	 */
	private JTree	 				navTree;
	
	/** 
	 * The treeNode that this Text Area represents. 
	 * When undoable edits are committed to the undo/redo queue, they include
	 * a reference to this node, so that they can select the correct node
	 * when undo is performed. 
	 */
	private DefaultMutableTreeNode 	treeNode;
	
	/**
	 * Controller for editing actions etc. 
	 */
	protected BrowserControl 		controller;
	
	/**
	 * A dialog for displaying a pop-up edit of a parameter.
	 * This reference allows the dialog to be hidden when this field 
	 * is de-selected. 
	 */
	private JDialog					paramEditDialog;
	

	/** A border to illustrate when this field is selected */
	protected Border 				selectedBorder;
	
	/** A border to illustrate when this field is unselected */
	private Border 					unselectedBorder;
	
	/** Button to add a parameter into this field. */
	private JButton 				addParamButton;

	/** Icon for the add-parameter button */
	private Icon 					addParamIcon;
	
	/** Button to indicate field has steps (no function yet). */
	JButton 						notesButton;
	
	/** Icon for the add-parameter button, to "hide" button without changing
	 * it's size */
	private Icon 					blankIcon;
	
	/**
	 * This is only used if the Step that this UI displays is a 'Comment' step.
	 * This text area is displayed below the main description text editor. 
	 */
	private JTextArea				commentTextBox;
	
	/**
	 * A panel to display thumbnails for any data-refs that are links to images. 
	 */
	private JPanel 					imagePanel;
	
	/**
	 * The HTML tag id to use for displaying the Field Name.
	 * This needs to be all lower case, since the styleSheet text in the
	 * HTML header automatically is converted to lower case, but the 
	 * id names in the HTML body is not, so they won't match if one includes
	 * capitals!
	 */
	public static final String			NAME_ID = "fieldname";

	/**
	 * The HTML tag name to use for displaying the Field Text contents
	 */
	public static final Tag			TEXT_TAG = Tag.SPAN;

	/**
	 * The HTML tag name to use for displaying the Field Parameters
	 */
	public static final Tag			PARAM_TAG = Tag.A;

	/**
	 * The HTML tag name to use for displaying the Field Name.
	 */
	public static final Tag			NAME_TAG = Tag.SPAN;
	
	/**
	 * A useful bit of HTML for adding space between tags etc. 
	 */
	public static final String 		TEXT_SPACER = "<"+ TEXT_TAG +
													"> </"+ TEXT_TAG + ">";
	
	/**
	 * Initialises UI components. 
	 */
	private void initialise()
	{
		// make a content Editor and set a document filter
		contentEditor = new HtmlContentEditor();
		contentEditor.addFocusListener(this);
		contentEditor.addKeyListener(this);
		contentEditor.addMouseListener(new ParamMouseListener());
		Document d = contentEditor.getDocument();
		AbstractDocument doc;
		if (d instanceof AbstractDocument) {
            doc = (AbstractDocument)d;
            if (doc instanceof StyledDocument)
            doc.setDocumentFilter(new TextAreaFilter((StyledDocument)doc, Tag.A));
        }
		
		nameEditor = new HtmlContentEditor();
		nameEditor.addFocusListener(this);
		nameEditor.addKeyListener(this);
		d = nameEditor.getDocument();
		if (d instanceof AbstractDocument) {
            doc = (AbstractDocument)d;
            if (doc instanceof StyledDocument)
            doc.setDocumentFilter(new TextAreaNameFilter());
        }
		
		int indent = 5 + treeNode.getLevel() * 14;	// indent according to hierarchy
		Border emptyBorder = new EmptyBorder(7,indent,7,7);
		Border lb = BorderFactory.createLineBorder(org.openmicroscopy.shoola.util.ui.UIUtilities.LIGHT_GREY);
		selectedBorder = BorderFactory.createCompoundBorder(lb, emptyBorder);
		lb = BorderFactory.createLineBorder(Color.white);
		unselectedBorder = BorderFactory.createCompoundBorder(lb, emptyBorder);
		
		IconManager iM = IconManager.getInstance();
		addParamIcon = iM.getIcon(IconManager.ADD_NUMBER);
		blankIcon = iM.getIcon(IconManager.SPACER);
		
		// button for adding paramters. 
		addParamButton = new CustomButton(addParamIcon);
		addParamButton.setToolTipText(addParamToolTip);
		addParamButton.addActionListener(this);
		
		// merely indicates that this step has notes. No function yet. 
		Icon notesIcon = iM.getIcon(IconManager.STEP_NOTE_ICON);
		notesButton = new CustomButton(notesIcon);
		notesButton.setVisible(false); 		// unless step has notes...
	}
	
	/**
	 * Builds and configures the UI
	 */
	private void buildUI()
	{
		setBackground(null);
		setLayout(new BorderLayout());
		
		Box titleToolBarBox = Box.createHorizontalBox();
		titleToolBarBox.add(nameEditor);
		titleToolBarBox.add(Box.createHorizontalGlue());
		refreshNotesVisibility();
		titleToolBarBox.add(notesButton);
		
		add(titleToolBarBox, BorderLayout.NORTH);
		add(contentEditor, BorderLayout.CENTER);

		
		if (field instanceof TextBoxStep) {
			addParamButton.setVisible(false);
			IParam textBoxParam = ((TextBoxStep)field).getTextBoxParam();
			TextBoxEditor tbe = new TextBoxEditor(textBoxParam);
			commentTextBox = tbe.getTextBox();
			tbe.addPropertyChangeListener(ITreeEditComp.VALUE_CHANGED_PROPERTY, this);
			add(tbe, BorderLayout.SOUTH);
		} else {
			// panel to display any data-ref images.
			// ** NB: Shouldn't need to display dataRefs in TextBoxStep **
			imagePanel = new JPanel(new FlowLayout());
			imagePanel.setBackground(null);
			add(imagePanel, BorderLayout.SOUTH);
		}

		JPanel buttonContainer = new JPanel(new BorderLayout());
		buttonContainer.setBackground(null);
		buttonContainer.add(addParamButton, BorderLayout.NORTH);
		add(buttonContainer, BorderLayout.EAST);
		
		refreshText();
		setSelected(false);
	}

	
	/**
     * Converts the current text of the editor into a list of
     * {@link IFieldContent}, as in the model. 
     * 
     * @return
     */
    private List<IFieldContent> getNewContent() 
    {
    	return getNewContent(null);
    }
    
    /**
     * Converts the current text of the editor into a list of
     * {@link IFieldContent}, including a new parameter object, which will be
     * added wherever a parameter tag has id="new"
     * 
     * <code>newParam</code> can be null if not adding a new parameter. 
     * 
     * @return		a list of {@link IFieldContent} to represent the content
     */
    private List<IFieldContent> getNewContent(IFieldContent newParam) 
    {
    	int start = 0;
    	int end = contentEditor.getDocument().getLength();
    	
    	return getNewContent(newParam, start, end);
    }
	
	/**
     * Converts the current text of the editor into a list of
     * {@link IFieldContent}, including a new parameter object, which will be
     * added wherever a parameter tag has id="new"
     * 
     * 
     * <code>newParam</code> can be null if not adding a new parameter. 
     * 
     * @param	
     * @return		a list of {@link IFieldContent} to represent the content
     */
    private List<IFieldContent> getNewContent(IFieldContent newParam, 
    		int start, int end) 
    {
    	List<IFieldContent> contentList = new ArrayList<IFieldContent>();
    	
    	Document d = contentEditor.getDocument();
    	
    	int lastChar = start;	// keep track of the last processed character
    	int docEnd = end;	
    	
    	// Get the parameter tokens, iterate through them...
    	List<TextToken> tags = contentEditor.getElementsByTag
    											(FieldTextArea.PARAM_TAG);
    	
    	String description;
    	int id;
    	int tagStart;
    	
    	try {
    		for (TextToken param : tags) {
    			tagStart = param.getStart();
    			// check whether start of tag is after the end of the text you want
    			// or before the start of the text you want. 
    			// if so, simply ignore
    			if (tagStart >= docEnd) {
    				continue;
    			}
    			if (tagStart < lastChar) {
    				// if split is between tagStart and tagEnd, move lastChar to end
    				if (param.getEnd() > lastChar)
    					lastChar = param.getEnd();
    				continue;
    			}
    			// is there any text between start of this tag and end of last?
    			description = d.getText(lastChar, tagStart-lastChar);
				
				if (description.trim().length() > 0) {
					// if so, add a new text content object to the list
					// without trimming. 
					contentList.add(new TextContent(description));
				}
				String tagId = param.getId();
				// if the text references a "new" parameter, use newParam
				if (("new".equals(tagId)) && (newParam != null)) {
					contentList.add(newParam);
				}
				else {
				try {
					// now process the parameter. This should exist in the
					// field already, since text editing can only edit text 
					// content, or DELETE parameters.
					id = Integer.parseInt(param.getId());
					// Simply copy a reference into the new list
					if (id < field.getContentCount())
						contentList.add(field.getContentAt(id));
				} catch (NumberFormatException ex) {
					// ignore elements that do not have a valid (integer) id
				}
				}
				
				lastChar = param.getEnd();	// update the end of last element
			}
    		// any text left over?
    		int remainingChars = docEnd-lastChar+1;
    		if (remainingChars > 0) {
    			description = d.getText(lastChar, remainingChars);
    			if (description.trim().length() > 0) {
    				// if so, add another text content to the list
    				contentList.add(new TextContent(description));
    			}
			}
    		
    	} catch (BadLocationException e) {
			e.printStackTrace();
		}
    	
    	return contentList;
    }
    
	
	/**
	 * Shows the pop-up dialog for editing a parameter. 
	 * The dialog adds this class as a {@link PropertyChangeListener} to 
	 * the UI component for editing the UI, so that when the user edits,
	 * the {@link ITreeEditComp#VALUE_CHANGED_PROPERTY} property
	 * the data is saved to the model, via controller for undo/ redo etc.
	 * 
	 * @param index		The index of the Parameter in the {@link #field}
	 * @param point		The screen location to show the dialog
	 */
	protected void showParamDialog(int index, Point point) 
	{
		if ((paramEditDialog != null) && (paramEditDialog.isVisible())) {
			paramEditDialog.setVisible(false);
			paramEditDialog.dispose();
		}
			
		IFieldContent content = field.getContentAt(index);
		if (content instanceof IParam) {
			IParam param = (IParam)content;
			paramEditDialog = new ParamEditorDialog(param, point, this);
			paramEditDialog.setVisible(true);
		}
	}
	
	/**
	 * Returns true if the {@link #treeNode} is selected in the {@link #navTree}.
	 * 
	 * @return		see above.
	 */
	private boolean isFieldSelected() 
	{
		if (treeNode == null) return false;
		if (navTree == null) return false;
		
		TreePath path = new TreePath(treeNode.getPath());
		return navTree.isPathSelected(path);
	}
	
	/**
	 * Updates the visibility of the notes icon(button), showing only if
	 * the field has >0 notes. 
	 */
	private void refreshNotesVisibility() 
	{
		notesButton.setVisible(field.getNoteCount() > 0);
	}
	
	/**
	 * Refreshes the data-refs, displaying a thumbnail for any that are images.
	 */
	private void refreshDataRefs() 
	{
		if (imagePanel == null)		return;		// won't exist for textBoxStep
		imagePanel.removeAll();
		
		int drCount = field.getDataRefCount();
		String r;
		for (int i=0; i< drCount; i++) {
			r = field.getDataRefAt(i).getAttribute(DataReference.REFERENCE);
			if (DataReference.showImage(r)) {
				imagePanel.add(new ImagePreview(r));
			}
		}
	}
	
	private String getNameHtml()
	{
		String html = "";
		
		// html for the field name
		String name = getFieldName();
		if (name != null) {
			html = "<"+ FieldTextArea.NAME_TAG  +" " + HTML.Attribute.ID +
				"='"+ FieldTextArea.NAME_ID +"'>" 
				+ name.trim() + "</"+ FieldTextArea.NAME_TAG +"><br>";
		}
		
		return html;
	}

	/**
	 * Method for generating the html representation of the <code>field</code>.
	 * 
	 * @param field
	 * @return
	 */
	private String getContentHtml()
	{
		String html = "";
		
		String contentText;
		String contentString;
		IFieldContent content;
		
		// if no content, put a place-holder so users can enter text
		if (field.getContentCount() == 0) {
			contentText = "<"+ FieldTextArea.TEXT_TAG +" " +
			HTML.Attribute.ID + 
			"='0'> </"+ FieldTextArea.TEXT_TAG + ">";
			return html + contentText;
		}
		
		// flag used to insert space between parameter objects, so user can
		// start typing and insert text between parameters. 
		boolean includeSpacer = false;	
		
		// html for the field contents. 
		for (int i=0; i<field.getContentCount(); i++) {
			content = field.getContentAt(i);
			if (content instanceof IParam) {
				contentString = content.toString();
				if (contentString.length() == 0)
					contentString = "param";
				
				contentString = "[" + contentString + "]";
				// id attribute allows parameters to be linked to model
				// eg for editing parameters. 
				contentText = (includeSpacer ? TEXT_SPACER : "") + // space before param
						"<"+ FieldTextArea.PARAM_TAG + " href='#' " +
				 		HTML.Attribute.ID + "='" + i + "'>" + 
				 		contentString + 
				 		"</"+ FieldTextArea.PARAM_TAG + ">";
				includeSpacer = true;
			} else {
				// don't need this ID attribute, but might be useful in future?
				contentText = "<"+ FieldTextArea.TEXT_TAG +" " +
				HTML.Attribute.ID + "='" + i + "'>" + 
				content.toString() 
				+ "</"+ FieldTextArea.TEXT_TAG + ">";
				if (content.toString().length() > 0)
					includeSpacer = false;		// don't need a space after text
			}
			// add each component. 
			html = html + contentText;
		}
		return html;
	}
	
	/**
     * Saves the current textual content back to the model, if the 
     * text has been edited.
     */
    protected void saveContent() 
    {
    	if (contentEditor.hasDataToSave()) {
    		
    		// convert the current text of this editor into a list of
    		// content, in the same format as the data model...
			List<IFieldContent> newContent = getNewContent();
			
			// replace the old content of the field with new content
			saveContent(field, newContent, navTree, treeNode);
			
			// reset this flag
			contentEditor.dataSaved();
		}
    }
    
    /**
     * Saves the current field name back to the model, if the 
     * name has been edited.
     */
    protected void saveName() 
    {
    	String oldName = getFieldName();
    	String newName = getEditedName();
    	if (! oldName.equals(newName)) {
			
			saveName(field, newName, navTree, treeNode);
			
			// reset this flag, even though not read
			nameEditor.dataSaved();
		}
    }
    
   /**
    * Saves the content via the controller, which handles adding to undo/redo.
    * This method may be overridden by subclasses to change the saving 
    * behavior of this class.
    * 
    * @param fld		The field to add a new parameter to.
	* @param content 		The new content, as a list.
	* @param tree			The JTree to refresh with undo/redo
	* @param node		The node to highlight / refresh with undo/redo. 
    */
    protected void saveContent(IField fld,
			List<IFieldContent> content, JTree tree, TreeNode node) {
    	
    	// replace the old content of the field with new content
		controller.editFieldContent(fld, content, tree, node);
    }
    
    /**
     * Saves the name via the controller, which handles adding to undo/redo.
     * 
     * @param fld		The field to add a new parameter to.
 	* @param name 		The new name of the field
 	* @param tree			The JTree to refresh with undo/redo
 	* @param node		The node to highlight / refresh with undo/redo. 
     */
     protected void saveName(IField fld, String name, JTree tree, TreeNode node) {
     	
 		controller.editAttribute(field, Field.FIELD_NAME, 
 				name, "Edit Name", tree, node);
     }
    
    /**
     * Splits the field/step into two, creating a new field with the content
     * after the <code>splitChar</code>, adding it after this field as a 
     * sibling in the tree. 
     * 
     * @param splitChar		The character in the document of {@link #contentEditor}
     */
    private void splitField(int splitChar) {
    	
    	// can't split root field
    	if (treeNode.isRoot()) return;
    	
    	String fieldName = getEditedName();
		
		int start = 0;
    	int end = contentEditor.getDocument().getLength();
    	
    	List<IFieldContent> content1 = getNewContent(null, start, splitChar-1);
    	List<IFieldContent> content2 = getNewContent(null, splitChar, end);
    	
    	controller.splitField(field, fieldName, content1, content2, navTree, treeNode);
    	
    	// reset this flag
		contentEditor.dataSaved();
    }
    
 
    
    /**
	 * Inserts a new parameter, of the type defined by <code>paramType</code>
	 * into the text content at the current selection or caret position. 
	 * The currently highlighted text will become the value of the parameter. 
	 */
	private void insertParam(String paramType) 
	{
		// get the currently selected text and selection positions
		String selectedText = contentEditor.getSelectedText();
		int start = contentEditor.getSelectionStart();
		int end = contentEditor.getSelectionEnd();
		
		Document d = contentEditor.getDocument();
		
		// edit the document text, inserting tags around the currently-selected
		// text (or inserting the tag into the caret position, if no text
		// is selected). 
		MutableAttributeSet aAttributes = new SimpleAttributeSet();
		aAttributes.addAttribute(HTML.Attribute.ID, "new");
		
		MutableAttributeSet tagAttributes = new SimpleAttributeSet();
		tagAttributes.addAttribute(FieldTextArea.PARAM_TAG, aAttributes);
		
		if ((selectedText == null) || (selectedText.length() == 0)) {
			// need to insert something!
			selectedText = AbstractParam.DEFAULT_PARAM_NAME;	
		}
		
		try {
			// replace the selected text with the parameter 
			d.remove(start, end - start);
			
			// if textEditor doesn't have focus, char = 0. 
			if (start == 0) start++;	// to avoid adding into header! 
			
			d.insertString(start, selectedText, tagAttributes);
		} catch (BadLocationException e1) {
			e1.printStackTrace();
		}
		
		// create a new parameter
		IParam param = FieldParamsFactory.getFieldParam(paramType);
		param.setAttribute(TextParam.PARAM_NAME, selectedText);
		
		// and use it to build a new content list. This method links the 
		// newly inserted tag (id=new) with the parameter argument
		List<IFieldContent> newContent = getNewContent(param);
		
		// get the new name, just in case it changed...
		String fieldName = getEditedName();
		
		// replace the old content of the field with new content
		controller.editFieldContent(field, fieldName, 
				newContent, navTree, treeNode);
		
		// reset this flag
		contentEditor.dataSaved();
	}
	
	/**
	 * Handy method for getting the edited name of the field/step.
	 * If the current name is the same as the default text (eg Step 1) then
	 * this method will return null, since the name has not been edited and
	 * should not be saved. 
	 * 
	 * @param currentText
	 * @return
	 */
	private String getEditedName()
	{
		Document d = nameEditor.getDocument();
		String name = "";
		try {
			name = d.getText(0, d.getLength());
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return name.trim();
	}

	/**
     * Changes the appearance of this Field to indicate that the corresponding
     * node is selected in the JTree.
     * Called by {@link #refreshSelection()}
     * 
     * @param selected		If true, border is painted etc. 
     */
    private void setSelected(boolean selected)
	{
		setBorder(selected ? selectedBorder : unselectedBorder);
		// don't want to hide the button, or the text will expand to fill! 
		addParamButton.setIcon(selected ? addParamIcon : blankIcon);
		
		// don't allow addition of parameters to root. 
		if (selected) {
			if(treeNode.isRoot()) {
				selected = false;
			}
		}
		addParamButton.setEnabled(selected);
	}
    
    /**
     * A {@link MouseListener} added to the editor component. 
     * Used to launch the parameter edit dialog if the user clicks on a 
     * parameter element. 
     * Also, saves any changes to the model.
     * 
     * @author will
     *
     */
    private class ParamMouseListener extends MouseAdapter
    {
    	public void mouseClicked(MouseEvent e) 
    	{
    		Point mouseLoc = e.getPoint();
    		int c = contentEditor.viewToModel(mouseLoc);
    		
    		// if any changes have been made to the document, save changes.
    		saveContent();
    		
    		// if click is on a parameter...
    		if (contentEditor.isOffsetWithinTag(c, FieldTextArea.PARAM_TAG)) {
    			// disable add-param button
    			addParamButton.setEnabled(false);
    			
    			// show edit dialog
    			String id = contentEditor.getElementId(c);
    			if ((id != null) && (isShowing())) { 
    				Point paneLoc = null;
    				try { 
    					paneLoc = getLocationOnScreen();
    					// give the exact location of the mouse
    					if (mouseLoc != null)
    						paneLoc.translate((int)mouseLoc.getX(), 
    											(int)mouseLoc.getY());
    				} catch (IllegalComponentStateException ex){
    					ex.printStackTrace();
    				}
    				showParamDialog(Integer.parseInt(id), paneLoc);
    			}
    		}
    		else {
    			// enable add-param button, unless root node
    			addParamButton.setEnabled(! treeNode.isRoot());
    		}
    	}
    }
    
	/**
	 * Creates an instance of this class. 
	 * Calls {@link #initialise()}, then {@link #buildUI()}.
	 * 
	 * @param field			The field used to build this UI
	 * @param tree			For undo/redo selection see {@link #navTree}
	 * @param treeNode		For undo/redo selection see {@link #treeNode}
	 * @param controller	For managing edits
	 */
	public FieldTextArea(IField field, JTree tree, DefaultMutableTreeNode treeNode,
			BrowserControl controller) 
	{
		this.field = field;
		this.navTree = tree;
		this.treeNode = treeNode;
		this.controller = controller;
		
		initialise();
		buildUI();
	}
	
	/**
	 * Gets the field name for display. 
	 * This method may be overridden by subclasses if they do not want to
	 * display the field name, E.g. {@link FieldContentEditor}.
	 * 
	 * @return		The name of the field. 
	 */
	protected String getFieldName()
	{
		String name = field.getAttribute(Field.FIELD_NAME);
		if ((name == null) || (name.length() == 0)) {
			name = TreeModelMethods.getNodeName(treeNode);
		} 
		return name;
	}
	
	/**
	 * Refreshes the text of this UI, based on the {@link #field}.
	 * Sets the HTML to include CSS style header. 
	 * This method is called by the parent UI when the field has been 
	 * edited.
	 */
	public void refreshText() {
		
		// set name html
		String content = getNameHtml();
		String html = "<html><head> <style type='text/css'> \n" +
			FieldTextArea.NAME_TAG + "#" + FieldTextArea.NAME_ID +
			" {font-weight: 500; font-family: arial} \n" +
			"</style> </head> " +
			"<body> " + content + " </body> </html>";
		nameEditor.setText(html);
		
		// set content html
		content = getContentHtml();
		html = "<html><body> " + content + " </body> </html>";
		contentEditor.setText(html);
		
		contentEditor.dataSaved();
		
		// if this is a 'Comment Step' it will have a text box that needs update
		if (field instanceof TextBoxStep) {
			IParam textBoxParam = ((TextBoxStep)field).getTextBoxParam();
			String newText = textBoxParam.getAttribute(TextParam.PARAM_VALUE);
			if (commentTextBox != null)
				commentTextBox.setText(newText);
		}
		
		refreshNotesVisibility(); 
		refreshDataRefs();
	}

	/**
	 * Updates the border to correspond to the selection state and
	 * if the field is not selected, the {@link #paramEditDialog} is disposed. 
	 * 
	 * This is called by the parent UI when it receives tree-selection-changed
	 * events. 
	 */
	public void refreshSelection()
	{
		boolean selected = isFieldSelected();
		setSelected(selected);
		
		// if this step is not selected, close any paramEditDialog open
		if ((! selected) && (paramEditDialog != null)) {
			paramEditDialog.dispose();
		}
		// add or removal of experiment-info will trigger refresh selection
		refreshNotesVisibility(); 
	}
	
	/**
	 * Implemented as specified by the {@link FocusListener} interface.
	 * Sets a selected border, and attempts to select the corresponding
	 * path in the navigation {@link JTree}
	 * @see FocusListener#focusGained(FocusEvent)
	 */
	public void focusGained(FocusEvent e) {
		
		if (treeNode == null) return;
		TreePath path = new TreePath(treeNode.getPath());
		if (navTree != null) {
			TreePath currentPath = navTree.getSelectionPath();
			if (!path.equals(currentPath)) {
				navTree.setSelectionPath(path);
			}
		}
	}

	/**
	 * Implemented as specified by the {@link FocusListener} interface.
	 * if any changes have been made to the document, save changes.
	 * 
	 * @see FocusListener#focusLost(FocusEvent)
	 */
	public void focusLost(FocusEvent e) {
		Object source = e.getSource();
		if (source instanceof HtmlContentEditor) {
			if (source.equals(contentEditor)) {
				saveContent();
			} else if (source.equals(nameEditor)) {
				saveName();
			}
		}
	}
	
	
	/**
	 * Implemented as specified by the {@link PropertyChangeListener} interface.
	 * Listens for changes to the dialog for editing parameters.
	 * Delegates editing to the {@link #controller}. 
	 * 
	 * @see PropertyChangeListener#propertyChange(PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		
		String propName = evt.getPropertyName();
		
		if (ITreeEditComp.VALUE_CHANGED_PROPERTY.equals(propName)) {
			
			if (evt.getSource() instanceof ITreeEditComp) {
				
				/* Need controller to pass on the edit  */
				if (controller == null) return;
				
				ITreeEditComp src = (ITreeEditComp)evt.getSource();
				IAttributes param = src.getParameter();
				String attrName = src.getAttributeName();
				String displayName = src.getEditDisplayName();
				
				String newValue;
				Object newVal = evt.getNewValue();
				if ((newVal instanceof String) || (newVal == null)){
					newValue = (newVal == null ? null : newVal.toString());
				 	 controller.editAttribute(param, attrName, newValue, 
				 			displayName, navTree, treeNode);
				}
				
				else if (newVal instanceof HashMap) {
					HashMap newVals = (HashMap)newVal;
					controller.editAttributes(param, displayName, newVals, 
							navTree, treeNode);
				}
			}
		}
	}

	/**
	 * Implemented as specified by the {@link ActionListener} interface.
	 * Handles the "Add Parameter" button. 
	 * 
	 * @see ActionListener#actionPerformed(ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) 
	{
		if (e.getSource().equals(addParamButton)) {
			insertParam(TextParam.TEXT_LINE_PARAM);
		}
	}

	/**
	 * Required by the {@link KeyListener} interface, but this is a null
	 * implementation. 
	 * @see	KeyListener#keyPressed(KeyEvent)
	 */
	public void keyPressed(KeyEvent e) {}

	/**
	 * Implemented as specified by the {@link KeyListener} interface.
	 * Handles "Enter" key from the {@link #contentEditor} and calls 
	 * {@link #splitField(int)} to split this field into two. 
	 * 
	 * @see	KeyListener#keyReleased(KeyEvent)
	 */
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			if (e.getSource().equals(contentEditor)) {
				int splitChar = contentEditor.getCaretPosition();
				splitField(splitChar);
			} else if (e.getSource().equals(nameEditor)) {
				contentEditor.setCaretPosition(1);
				contentEditor.requestFocusInWindow();
			}
		}
	}

	/**
	 * Required by the {@link KeyListener} interface, but this is a null
	 * implementation. 
	 * @see	KeyListener#keyTyped(KeyEvent)
	 */
	public void keyTyped(KeyEvent e) {}
}
