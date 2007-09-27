/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */

package com.sitescape.team.domain;
import org.dom4j.Element;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.module.shared.XmlUtils;
import com.sitescape.util.Validator;
/**
 * @hibernate.subclass discriminator-value="V" dynamic-update="true"
 * @author janet
 */
public class VersionAttachment extends FileAttachment {
    private int versionNumber=1;
    private String versionName;
    private FileAttachment parentAttachment;
    
    public VersionAttachment() {
    } 

    public void setOwner(AnyOwner owner) {
    	//this need to be included here since we are overloading.
    	//Hibernate calls the wrong one, it isn't present
    	this.owner = owner;
    } 
    
 	public void setOwner(DefinableEntity entry) {
		//don't set foreign key, so not read in to entry
  		owner = new AnyOwner(entry, false);
  	}

    /**
     * @hibernate.property 
     * @return
     */
    public int getVersionNumber() {
        return versionNumber;
    }
    public void setVersionNumber(int versionNumber) {
    	if (versionNumber == 0) throw new IllegalArgumentException("0 versionNumber");
    	this.versionNumber = versionNumber;
    }

    /**
     * @hibernate.property length="256"
     * @return
     */
    public String getVersionName() {
        return this.versionName;
    }
    public void setVersionName(String versionName) {
    	if (Validator.isNull(versionName)) throw new IllegalArgumentException("null versionName");
    	this.versionName = versionName;
    }
    /**
     * @hibernate.many-to-one class="com.sitescape.team.domain.FileAttachment"
     * @hibernate.column name="parentAttachment" sql-type="char(32)"
     */
    public FileAttachment getParentAttachment() {
        return parentAttachment;
    }
    public void setParentAttachment(FileAttachment parentAttachment) {
        this.parentAttachment = parentAttachment;
    }
    public String toString() {
    	return parentAttachment.toString() + ":" + versionNumber;
    }
	public Element addChangeLog(Element parent) {
		Element element = parent.addElement(ObjectKeys.XTAG_ELEMENT_TYPE_VERSIONATTACHMENT);
		element.addAttribute(ObjectKeys.XTAG_ATTRIBUTE_ID, getId());
		element.addAttribute(ObjectKeys.XTAG_FILE_PARENT, getParentAttachment().getId());

		XmlUtils.addProperty(element, ObjectKeys.XTAG_FILE_VERSION_NUMBER, Long.toString(getVersionNumber()));
		XmlUtils.addProperty(element, ObjectKeys.XTAG_FILE_VERSION_NAME, getVersionName());
		XmlUtils.addProperty(element, ObjectKeys.XTAG_FILE_REPOSITORY, getRepositoryName());
	
		if (creation != null) creation.addChangeLog(element, ObjectKeys.XTAG_ENTITY_CREATION);
		if (modification != null) modification.addChangeLog(element, ObjectKeys.XTAG_ENTITY_MODIFICATION);
		if (!parent.getName().equals(ObjectKeys.XTAG_ELEMENT_TYPE_FILEATTACHMENT)) {
			//add additional information if logged along
			if (!Validator.isNull(getName())) element.addAttribute(ObjectKeys.XTAG_ATTRIBUTE_NAME, getName());
			
			XmlUtils.addProperty(element, ObjectKeys.XTAG_FILE_NAME, getFileItem().getName());
			XmlUtils.addProperty(element, ObjectKeys.XTAG_FILE_LENGTH, Long.toString(getFileItem().getLength()));
			XmlUtils.addProperty(element, ObjectKeys.XTAG_FILE_REPOSITORY, getRepositoryName());
			
		}
		return element;
    	
    }
}
