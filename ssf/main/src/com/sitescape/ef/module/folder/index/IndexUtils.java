package com.sitescape.ef.module.folder.index;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.portlet.PortletSession;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import com.sitescape.ef.domain.AclControlledEntry;
import com.sitescape.ef.domain.CustomAttribute;
import com.sitescape.ef.domain.Entry;
import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.domain.Event;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.security.acl.AccessType;
import com.sitescape.ef.security.acl.AclManager;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.util.WebHelper;

/**
 *
 * @author Jong Kim
 */
public class IndexUtils {
    
    // Defines field names
    
    public static final String ALL_TEXT_FIELD = "_allText";
    public static final String CREATION_DATE_FIELD = "_creationDate";
    public static final String MODIFICATION_DATE_FIELD = "_modificationDate";
    public static final String CREATORID_FIELD = "_creatorId";
    public static final String MODIFICATIONID_FIELD = "_modificationId";
    public static final String READ_ACL_FIELD = "_readAcl";
    public static final String DOCID_FIELD = "_docId";
    public static final String DOCNUMBER_FIELD = "_docNum";
    public static final String COMMAND_DEFINITION_FIELD = "_commandDef";
    public static final String TITLE_FIELD = "_title";
    public static final String TITLE1_FIELD = "_title1";
    public static final String DESC_FIELD = "_desc";
    public static final String FOLDERID_FIELD = "_folderId";
    public static final String CUSTOMATTRS_FIELD = "_customAttributes";
    public static final String EVENT_COUNT_FIELD = "_eventCount";
    
    // Defines field values
    public static final String READ_ACL_ALL = "all";
    
    public static void addAllText(Document doc, String text) {
        doc.add(allTextField(text));
    }
    
    public static Field allTextField(String text) {
        return new Field(ALL_TEXT_FIELD, text, false, true, true);
    }
    
    public static void addCreationDate(Document doc, Entry entry) {
        // Add creation-date field
        Field creationDateField = Field.Keyword(CREATION_DATE_FIELD, entry.getCreation().getDate());
        doc.add(creationDateField);
    }
    
    public static void addModificationDate(Document doc, Entry entry) {
    	// Add modification-date field
    	Field modificationDateField = Field.Keyword(MODIFICATION_DATE_FIELD, entry.getModification().getDate());
    	doc.add(modificationDateField);
    }

    public static void addEvents(Document doc, Entry entry) {
    	int count = 0;
    	String eventName;
    	Field evDtStartField = null;
    	Field evDtEndField = null;
		Map customAttrs = entry.getCustomAttributes();
		Set keyset = customAttrs.keySet();
		Iterator attIt = keyset.iterator();
		// look through the custom attrs of this entry for any of type EVENT
		while (attIt.hasNext()) {
			CustomAttribute att = (CustomAttribute) customAttrs.get(attIt.next());
			if (att.getValueType() == CustomAttribute.EVENT) {
				// set the event name to event + count
				eventName = "_event" + count;
				Event ev = (Event) att.getValue();
				// range check to see if this event is in range
		    	evDtStartField = Field.Keyword(eventName+"StartDate", ev.getDtStart().getTime());
		    	doc.add(evDtStartField);
		    	evDtEndField = Field.Keyword(eventName+"EndDate", ev.getDtEnd().getTime());
		    	doc.add(evDtEndField);
		    	//SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				//String dateKey = sdf.format(ev.getDtStart().getTime());
		    	//doc.add(evDtStartField);
		    	count++;
			}
		}    	
		// Add event count field
    	Field eventCountField = Field.Keyword(EVENT_COUNT_FIELD, Integer.toString(count));
    	doc.add(eventCountField);
    }
    
    public static void addCommandDefinition(Document doc, Entry entry) {
        Field cdefField = Field.Keyword(COMMAND_DEFINITION_FIELD, entry.getEntryDef().getId());
        doc.add(cdefField);
    }
        
    public static void addCreationPrincipleId(Document doc, Entry entry) {
    	//Add the id of the creator (no, not that one...)
        Field creationIdField = Field.Keyword(CREATORID_FIELD, entry.getCreation().getPrincipal().getStringId());
        doc.add(creationIdField);
    }   

    public static void addModificationPrincipleId(Document doc, Entry entry) {
    	//Add the id of the creator (no, not that one...)
        Field modificationIdField = Field.Keyword(MODIFICATIONID_FIELD, entry.getModification().getPrincipal().getStringId());
        doc.add(modificationIdField);
    }   

    public static void addDocId(Document doc, Entry entry) {
    	//Add the id of the creator (no, not that one...)
        Field docIdField = Field.Keyword(DOCID_FIELD, entry.getStringId());
        doc.add(docIdField);
    }

    public static void addDocNumber(Document doc, FolderEntry entry) {
    	//Add the id of the creator (no, not that one...)
        Field docNumField = Field.Keyword(DOCNUMBER_FIELD, entry.getDocNumber());
        doc.add(docNumField);
    }    

    public static void addFolderId(Document doc, Folder folder) {
    	//Add the folder id to the document in the index
        Field folderField = Field.Keyword(FOLDERID_FIELD, folder.getId().toString());
        doc.add(folderField);
    }   
    
    public static void addReadAcls(Document doc, Folder folder, Entry entry, AclManager aclManager) {
        // Add ACL field. We only need to index ACLs for read access. 
        Field racField;
        if(entry instanceof AclControlledEntry) {
	        StringBuffer pIds = new StringBuffer();
	        Set readMemberIds = aclManager.getMembers(folder, (AclControlledEntry) entry, AccessType.READ);
	        for(Iterator i = readMemberIds.iterator(); i.hasNext();) {
	            pIds.append(i.next()).append(" ");
	        }
	        // I'm not sure if putting together a long string value is more
	        // efficient than processing multiple short strings... We will see.
	        if (pIds.length() != 0)
	          racField = new Field(READ_ACL_FIELD, pIds.toString(), true, true, true);
	        else
	          racField = new Field(READ_ACL_FIELD, READ_ACL_ALL, true, true, true);
        }
        else {
            racField = new Field(READ_ACL_FIELD, READ_ACL_ALL, true, true, true);
        }
        
        doc.add(racField);
    }
}
