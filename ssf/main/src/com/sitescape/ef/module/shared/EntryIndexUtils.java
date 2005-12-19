package com.sitescape.ef.module.shared;

import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import com.sitescape.ef.domain.CustomAttribute;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.Entry;
import com.sitescape.ef.domain.Event;
import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.domain.MultipleWorkflowSupport;
import com.sitescape.ef.domain.WorkflowState;
import com.sitescape.ef.search.BasicIndexUtils;

/**
 *
 * @author Jong Kim
 */
public class EntryIndexUtils {
    
    // Defines field names
    
    public final static String ENTRY_TYPE_FIELD = "_entryType";
    public final static String ENTRY_TYPE_ENTRY = "entry";
    public final static String ENTRY_TYPE_REPLY = "reply";
    public static final String CREATION_DATE_FIELD = "_creationDate";
    public static final String CREATION_DAY_FIELD = "_creationDay";
    public static final String MODIFICATION_DATE_FIELD = "_modificationDate";
    public static final String MODIFICATION_DAY_FIELD = "_modificationDay";
    public static final String CREATORID_FIELD = "_creatorId";
    public static final String MODIFICATIONID_FIELD = "_modificationId";
    public static final String RESERVEDBYID_FIELD = "_reservedById";
    public static final String DOCID_FIELD = "_docId";
    public static final String COMMAND_DEFINITION_FIELD = "_commandDef";
    public static final String TITLE_FIELD = "_title";
    public static final String TITLE1_FIELD = "_title1";
    public static final String DESC_FIELD = "_desc";
    public static final String CUSTOM_ATTRS_FIELD = "_customAttributes";
    public static final String EVENT_FIELD = "_event";
    public static final String EVENT_FIELD_START_DATE = "StartDate";
    public static final String EVENT_FIELD_END_DATE = "EndDate";
    public static final String EVENT_COUNT_FIELD = "_eventCount";
    public static final String WORKFLOW_PROCESS_FIELD = "_workflowProcess";
    public static final String WORKFLOW_STATE_FIELD = "_workflowState";
    
    // Defines field values
    public static final String READ_ACL_ALL = "all";
    
 
    
    public static void addTitle(Document doc, Entry entry) {
        // Add the title field
    	if (entry.getTitle() != null) {
    		String title = entry.getTitle();
    		title = title.trim();
            
            if(title.length() > 0) {
    	        Field allTextField = BasicIndexUtils.allTextField(title);
    	        Field titleField = new Field(EntryIndexUtils.TITLE_FIELD, title, true, true, true); 
    	        Field title1Field = Field.Keyword(EntryIndexUtils.TITLE1_FIELD, title.substring(0, 1));
                doc.add(titleField);
                doc.add(title1Field);
                doc.add(allTextField);
            }
    	}
    }
    
    public static void addEntryType(Document doc, Entry entry) {
        // Add the entry type (entry or reply)
    	if (entry instanceof FolderEntry) {
	        if (((FolderEntry)entry).getTopEntry() == null || ((FolderEntry)entry).getTopEntry() == entry) {
	        	Field entryTypeField = Field.Keyword(EntryIndexUtils.ENTRY_TYPE_FIELD, EntryIndexUtils.ENTRY_TYPE_ENTRY);
	        	doc.add(entryTypeField);
	        } else {
	        	Field entryTypeField = Field.Keyword(EntryIndexUtils.ENTRY_TYPE_FIELD, EntryIndexUtils.ENTRY_TYPE_REPLY);
	        	doc.add(entryTypeField);
	        }
    	} else {
        	Field entryTypeField = Field.Keyword(EntryIndexUtils.ENTRY_TYPE_FIELD, EntryIndexUtils.ENTRY_TYPE_ENTRY);
        	doc.add(entryTypeField);
    	}
    }
    
    public static void addCreationDate(Document doc, Entry entry) {
        // Add creation-date field
    	if (entry.getCreation() != null) {
    		Date creationDate = entry.getCreation().getDate();
            Field creationDateField = Field.Keyword(CREATION_DATE_FIELD, creationDate);
            doc.add(creationDateField);
            Field creationDayField = Field.Keyword(CREATION_DAY_FIELD, formatDayString(creationDate));
            doc.add(creationDayField);
    	}
        
    }
    
    public static void addModificationDate(Document doc, Entry entry) {
    	// Add modification-date field
    	if (entry.getModification() != null ) {
    		Date modDate = entry.getModification().getDate();
        	Field modificationDateField = Field.Keyword(MODIFICATION_DATE_FIELD, modDate);
        	doc.add(modificationDateField);        
        	Field modificationDayField = Field.Keyword(MODIFICATION_DAY_FIELD, formatDayString(modDate));
            doc.add(modificationDayField);
    	}
    }

    public static void addWorkflow(Document doc, Entry entry) {
    	// Add the workflow fields
   		List workflowStates = entry.getWorkflowStates();
   		if (workflowStates != null) {
   			for (int i = 0; i < workflowStates.size(); i++) {
   				Field workflowStateField = Field.Keyword(WORKFLOW_STATE_FIELD, 
   						((WorkflowState)workflowStates.get(i)).getState());
   				//Index the workflow state
   				doc.add(workflowStateField);
   				Definition def = ((WorkflowState)workflowStates.get(i)).getDefinition();
   				if (def != null) {
	   				Field workflowProcessField = Field.Keyword(WORKFLOW_PROCESS_FIELD, 
	   						def.getId());
	   				//Index the workflow title (which is always the id of the workflow definition)
	   				doc.add(workflowProcessField);
   				}
   			}
   		}
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
				eventName = EVENT_FIELD + count;
				Event ev = (Event) att.getValue();
				// range check to see if this event is in range
		    	evDtStartField = Field.Keyword(eventName+EVENT_FIELD_START_DATE, ev.getDtStart().getTime());
		    	doc.add(evDtStartField);
		    	evDtEndField = Field.Keyword(eventName+EVENT_FIELD_END_DATE, ev.getDtEnd().getTime());
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
        if (entry.getEntryDef() != null) {
        	Field cdefField = Field.Keyword(COMMAND_DEFINITION_FIELD, entry.getEntryDef().getId());
            doc.add(cdefField);
        }
    }
        
    public static void addCreationPrincipalId(Document doc, Entry entry) {
    	//Add the id of the creator (no, not that one...)
        if (entry.getCreation() != null && entry.getCreation().getPrincipal() != null) {
        	Field creationIdField = Field.Keyword(CREATORID_FIELD, entry.getCreation().getPrincipal().getId().toString());
            doc.add(creationIdField);
        }
    }   

    public static void addModificationPrincipalId(Document doc, Entry entry) {
    	//Add the id of the creator (no, not that one...)
        if (entry.getModification() != null && entry.getModification().getPrincipal() != null) {
        	Field modificationIdField = Field.Keyword(MODIFICATIONID_FIELD, entry.getModification().getPrincipal().getId().toString());
        	doc.add(modificationIdField);
        }
    }   

    public static void addReservedByPrincipalId(Document doc, FolderEntry entry) {
    	//Add the id of the reserver
        if (entry.getReservedDoc() != null && entry.getReservedDoc().getPrincipal() != null) {
        	Field reservedByIdField = Field.Keyword(RESERVEDBYID_FIELD, entry.getReservedDoc().getPrincipal().getId().toString());
        	doc.add(reservedByIdField);
        }
    }   

    public static void addDocId(Document doc, Entry entry) {
    	//Add the id of the creator (no, not that one...)
        Field docIdField = Field.Keyword(DOCID_FIELD, entry.getId().toString());
        doc.add(docIdField);
    }

    
    public static String formatDayString(Date date) {
    	DateFormat df = DateFormat.getInstance();
    	SimpleDateFormat sf = (SimpleDateFormat)df;
    	sf.applyPattern("yyyyMMdd");
    	return(df.format(date));
    }
}
