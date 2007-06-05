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
package com.sitescape.team.module.definition.notify;

import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;

import com.sitescape.team.domain.CustomAttribute;

public class NotifyBuilderSurvay extends AbstractNotifyBuilder {

    protected boolean build(Element element, Notify notifyDef, CustomAttribute attribute, Map args) {
     	Object obj = attribute.getValue();
    	if (obj instanceof Document) {
    		Document document = (Document)obj;
    		// TODO put survay content here?
    		element.setText("SURVAY!!!"+document.asXML());
    	} else if (obj != null) {
    		element.setText(obj.toString());
    	}
    	return true;
   }
}
