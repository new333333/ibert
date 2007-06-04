/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.module.definition;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.dom4j.Document;
import org.dom4j.Element;

import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.DefinitionInvalidException;
import com.sitescape.team.domain.Entry;
import com.sitescape.team.module.shared.InputDataAccessor;

/**
 * @author hurley
 *
 */
public interface DefinitionModule {

	public String addDefinition(Document doc, boolean replace);
	public Definition addDefinition(String name, String title, int type, InputDataAccessor inputData);
	/**
	 * Adds an item to an item in a definition tree.
	 *
	 * @param This call takes 4 parameters: def, itemId, itemNameToAdd, formData<br>
	 *        def - contains the definition that is being modified<br>
	 *        itemId - the id of the item being added to<br>
	 *        itemNameToAdd - the name of the item to be added<br>
	 *        formData - a Map of the values to e set in the newly added item
	 *                   The Map should contain each property value indexed by the 
	 *                     property name prefixed by "propertyId_".
	 * 
	 * @return the next element in the iteration.
	 * @exception NoSuchElementException iteration has no more elements.
	 */
	public Element addItem(String defId, String itemId, String itemName, InputDataAccessor inputData) throws DefinitionInvalidException;
	public Definition addDefaultDefinition(int type);
	public void deleteDefinition(String id);
	public void deleteItem(String defId, String itemId) throws DefinitionInvalidException;

	public Definition getDefinition(String id);
	public List<Definition> getDefinitions();
	public List<Definition> getDefinitions(int type);
	public Document getDefinitionConfig();
	/**
	 * Routine to process the input data and return a map of only the entry data
	 * 
	 * @param def
	 * @param inputData
	 * @return
	 */
	public Map getEntryData(Document def, InputDataAccessor inputData, Map fileItems);
	public Map getEntryDefinitionElements(String id);
	public Map getWorkflowDefinitionStates(String id);

	public void modifyDefinitionName(String id, String name, String caption);
	public void modifyDefinitionAttribute(String id, String key, String value);
	public void modifyDefinitionProperties(String id, InputDataAccessor inputData);
	public void modifyItem(String defId, String itemId, InputDataAccessor inputData) throws DefinitionInvalidException;
	public void modifyItemLocation(String defId, String sourceItemId, String targetItemId, String position) throws DefinitionInvalidException;
	public Definition setDefaultBinderDefinition(Binder binder);
	public Definition setDefaultEntryDefinition(Entry entry);
	public void setDefinitionLayout(String id, InputDataAccessor inputData);

  	public boolean testAccess(int type, String operation);
	
  	public void walkDefinition(DefinableEntity entry, DefinitionModule.DefinitionVisitor visitor);
	
  	interface DefinitionVisitor
  	{
  		abstract public void visit(Element entryElement, Element flagElement, Map args);
  		abstract public String getFlagElementName();
  	}
}
