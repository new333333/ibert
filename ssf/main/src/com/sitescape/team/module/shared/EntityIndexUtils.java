package com.sitescape.team.module.shared;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.CustomAttribute;
import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.Event;
import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.domain.Group;
import com.sitescape.team.domain.Tag;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.WfAcl;
import com.sitescape.team.domain.WorkflowState;
import com.sitescape.team.domain.WorkflowSupport;
import com.sitescape.team.domain.EntityIdentifier.EntityType;
import com.sitescape.team.module.binder.AccessUtils;
import com.sitescape.team.module.workflow.WorkflowUtils;
import com.sitescape.team.search.BasicIndexUtils;
import com.sitescape.team.util.TagUtil;

/**
 * Index the fields common to all Entry types.
 *
 * @author Jong Kim
 */
public class EntityIndexUtils {
    
    // Defines field names
    
    public final static String ENTRY_TYPE_FIELD = "_entryType";
    public final static String ENTRY_TYPE_ENTRY = "entry";
    public final static String ENTRY_TYPE_REPLY = "reply";
    public final static String ENTRY_TYPE_USER = "user";
    public final static String ENTRY_TYPE_GROUP = "group";
    public final static String ENTRY_ANCESTRY = "_entryAncestry";
    public static final String CREATION_DATE_FIELD = "_creationDate";
    public static final String CREATION_DAY_FIELD = "_creationDay";
    public static final String CREATION_YEAR_MONTH_FIELD = "_creationYearMonth";
    public static final String CREATION_YEAR_FIELD = "_creationYear";
    public static final String MODIFICATION_DATE_FIELD = "_modificationDate";
    public static final String MODIFICATION_DAY_FIELD = "_modificationDay";
    public static final String MODIFICATION_YEAR_MONTH_FIELD = "_modificationYearMonth";
    public static final String MODIFICATION_YEAR_FIELD = "_modificationYear";
    public static final String CREATORID_FIELD = "_creatorId";
    public static final String CREATOR_NAME_FIELD = "_creatorName";
    public static final String CREATOR_TITLE_FIELD = "_creatorTitle";
    public static final String SORT_CREATOR_TITLE_FIELD = "_sortCreatorTitle";
    public static final String MODIFICATIONID_FIELD = "_modificationId";
    public static final String MODIFICATION_NAME_FIELD = "_modificationName";
    public static final String MODIFICATION_TITLE_FIELD = "_modificationTitle";
    public static final String DOCID_FIELD = "_docId";
    public static final String COMMAND_DEFINITION_FIELD = "_commandDef";
    public static final String TITLE_FIELD = "title";
    public static final String SORT_TITLE_FIELD = "_sortTitle";
    public static final String TITLE1_FIELD = "_title1";
    public static final String EXTENDED_TITLE_FIELD = "_extendedTitle";
    public static final String NAME_FIELD = "_name";
    public static final String NAME1_FIELD = "_name1";
    public static final String DESC_FIELD = "_desc";
    public static final String EVENT_FIELD = "_event";
    public static final String EVENT_FIELD_START_DATE = "StartDate";
    public static final String EVENT_FIELD_END_DATE = "EndDate";    
    public static final String EVENT_COUNT_FIELD = "_eventCount";
    public static final String EVENT_DATES_FIELD = "_eventDates";
    public static final String EVENT_RECURRENCE_DATES_FIELD = "RecurrenceDates";
    public static final String WORKFLOW_PROCESS_FIELD = "_workflowProcess";
    public static final String WORKFLOW_STATE_FIELD = "_workflowState";
    public static final String WORKFLOW_STATE_CAPTION_FIELD = "_workflowStateCaption";
    public static final String BINDER_ID_FIELD = "_binderId";
    public static final String FILENAME_FIELD = "_fileName";
    public static final String FILE_EXT_FIELD = "_fileExt";
    public static final String FILE_TYPE_FIELD = "_fileType";
    public static final String FILE_ID_FIELD = "_fileID";
    public static final String FILE_UNIQUE_FIELD="_fileNameUnique";
    public static final String RATING_FIELD="_rating";
    public static final String ENTITY_FIELD="_entityType"; 
    public static final String DEFINITION_TYPE_FIELD="_definitionType"; 
 //   public static final String GROUP_SEE_COMMUNITY="groupCommunity";
 //   public static final String GROUP_SEE_ANY="groupAny";
    
    
    // Defines field values
    public static final String READ_ACL_ALL = "all";
        
    public static void addTitle(Document doc, DefinableEntity entry) {
        // Add the title field
    	if (entry.getTitle() != null) {
    		String title = entry.getTitle();
    		title = title.trim();
            
            if(title.length() > 0) {
    	        Field allTextField = BasicIndexUtils.allTextField(title);
    	        Field titleField = new Field(EntityIndexUtils.TITLE_FIELD, title, Field.Store.YES, Field.Index.TOKENIZED);
    	        Field sortTitleField = new Field(EntityIndexUtils.SORT_TITLE_FIELD, title.toLowerCase(), Field.Store.YES, Field.Index.UN_TOKENIZED);
    	        Field title1Field = new Field(EntityIndexUtils.TITLE1_FIELD, title.substring(0, 1), Field.Store.YES, Field.Index.UN_TOKENIZED);
    	        doc.add(titleField);
    	        doc.add(sortTitleField);
                doc.add(title1Field);
                doc.add(allTextField);
                if ((entry.getEntityType().equals(EntityType.folder) || entry.getEntityType().equals(EntityType.workspace)) &&
                		entry.getParentBinder() != null) {
                	String extendedTitle = title + " (" + entry.getParentBinder().getTitle() + ")";
                	//Special case: user workspaces don't show parent folder
                	if (entry.getParentBinder().getEntityType().equals(EntityType.profiles)) extendedTitle = title;
        	        Field extendedTitleField = new Field(EntityIndexUtils.EXTENDED_TITLE_FIELD, extendedTitle, Field.Store.YES, Field.Index.TOKENIZED);
        	        doc.add(extendedTitleField);
                }
            }
    	}
    }
    public static void addRating(Document doc, DefinableEntity entry) {
    	//rating may not exist or not be supported
    	try {
        	Field rateField = new Field(RATING_FIELD, entry.getAverageRating().getAverage().toString(), Field.Store.NO, Field.Index.UN_TOKENIZED);
        	doc.add(rateField);
        } catch (Exception ex) {};
   	
    }
    public static void addEntityType(Document doc, DefinableEntity entry) {
      	Field eField = new Field(ENTITY_FIELD, entry.getEntityType().name(), Field.Store.YES, Field.Index.UN_TOKENIZED);
       	doc.add(eField);
    }
    public static void addDefinitionType(Document doc, DefinableEntity entry) {
    	Integer definitionType = entry.getDefinitionType();
    	if (definitionType == null) definitionType = 0;
      	Field eField = new Field(DEFINITION_TYPE_FIELD, definitionType.toString(), Field.Store.YES, Field.Index.UN_TOKENIZED);
       	doc.add(eField);
    }
    public static void addEntryType(Document doc, DefinableEntity entry) {
        // Add the entry type (entry or reply)
    	if (entry instanceof FolderEntry) {
	        if (((FolderEntry)entry).getTopEntry() == null || ((FolderEntry)entry).getTopEntry() == entry) {
	        	Field entryTypeField = new Field(EntityIndexUtils.ENTRY_TYPE_FIELD, EntityIndexUtils.ENTRY_TYPE_ENTRY, Field.Store.YES, Field.Index.UN_TOKENIZED);
	        	doc.add(entryTypeField);
	        } else {
	        	Field entryTypeField = new Field(EntityIndexUtils.ENTRY_TYPE_FIELD, EntityIndexUtils.ENTRY_TYPE_REPLY, Field.Store.YES, Field.Index.UN_TOKENIZED);
	        	doc.add(entryTypeField);
	        }
    	} else if (entry instanceof User) {
        	Field entryTypeField = new Field(EntityIndexUtils.ENTRY_TYPE_FIELD, EntityIndexUtils.ENTRY_TYPE_USER, Field.Store.YES, Field.Index.UN_TOKENIZED);
        	doc.add(entryTypeField);
    	} else if (entry instanceof Group) {
    		Field entryTypeField = new Field(EntityIndexUtils.ENTRY_TYPE_FIELD, EntityIndexUtils.ENTRY_TYPE_GROUP, Field.Store.YES, Field.Index.UN_TOKENIZED);
    		doc.add(entryTypeField);
    	} 
   }
    
    public static void addCreationDate(Document doc, DefinableEntity entry) {
        // Add creation-date field
    	if (entry.getCreation() != null) {
    		Date creationDate = entry.getCreation().getDate();
            Field creationDateField = new Field(CREATION_DATE_FIELD, DateTools.dateToString(creationDate,DateTools.Resolution.SECOND), Field.Store.YES, Field.Index.UN_TOKENIZED);
            doc.add(creationDateField);
            // index the YYYYMMDD string
            String dayString = formatDayString(creationDate);
            Field creationDayField = new Field(CREATION_DAY_FIELD, dayString, Field.Store.YES, Field.Index.UN_TOKENIZED);
            doc.add(creationDayField);
            // index the YYYYMM string
            String yearMonthString = dayString.substring(0,6);
            Field creationYearMonthField = new Field(CREATION_YEAR_MONTH_FIELD, yearMonthString, Field.Store.YES, Field.Index.UN_TOKENIZED);
            doc.add(creationYearMonthField);
            // index the YYYY string
            String yearString = dayString.substring(0,4);
            Field creationYearField = new Field(CREATION_YEAR_FIELD, yearString, Field.Store.YES, Field.Index.UN_TOKENIZED);
            doc.add(creationYearField);
    	}
        
    }
    
    public static void addModificationDate(Document doc, DefinableEntity entry) {
    	// Add modification-date field
    	if (entry.getModification() != null ) {
    		Date modDate = entry.getModification().getDate();
        	Field modificationDateField = new Field(MODIFICATION_DATE_FIELD, DateTools.dateToString(modDate,DateTools.Resolution.SECOND), Field.Store.YES, Field.Index.UN_TOKENIZED);
        	doc.add(modificationDateField);        
            // index the YYYYMMDD string
            String dayString = formatDayString(modDate);
            Field modificationDayField = new Field(MODIFICATION_DAY_FIELD, dayString, Field.Store.YES, Field.Index.UN_TOKENIZED);
            doc.add(modificationDayField);
            // index the YYYYMM string
            String yearMonthString = dayString.substring(0,6);
            Field modificationYearMonthField = new Field(MODIFICATION_YEAR_MONTH_FIELD, yearMonthString, Field.Store.YES, Field.Index.UN_TOKENIZED);
            doc.add(modificationYearMonthField);
            // index the YYYY string
            String yearString = dayString.substring(0,4);
            Field modificationYearField = new Field(MODIFICATION_YEAR_FIELD, yearString, Field.Store.YES, Field.Index.UN_TOKENIZED);
            doc.add(modificationYearField);   	}
    }

    public static void addWorkflow(Document doc, DefinableEntity entry) {
    	// Add the workflow fields
    	if (entry instanceof WorkflowSupport) {
    		WorkflowSupport wEntry = (WorkflowSupport)entry;
    		Set workflowStates = wEntry.getWorkflowStates();
    		if (workflowStates != null) {
    			for (Iterator iter=workflowStates.iterator(); iter.hasNext();) {
    				WorkflowState ws = (WorkflowState)iter.next();
    				Field workflowStateField = new Field(WORKFLOW_STATE_FIELD, 
   						ws.getState(), Field.Store.YES, Field.Index.UN_TOKENIZED);
    				Field workflowStateCaptionField = new Field(WORKFLOW_STATE_CAPTION_FIELD, 
   						WorkflowUtils.getStateCaption(ws.getDefinition(), ws.getState()), Field.Store.YES, Field.Index.UN_TOKENIZED);
    				//Index the workflow state
    				doc.add(workflowStateField);
    				doc.add(workflowStateCaptionField);
   				
    				Definition def = ws.getDefinition();
    				if (def != null) {
    					Field workflowProcessField = new Field(WORKFLOW_PROCESS_FIELD, 
    							def.getId(), Field.Store.NO, Field.Index.UN_TOKENIZED);
    					//	Index the workflow title (which is always the id of the workflow definition)
    					doc.add(workflowProcessField);
    				}
   				}
   			}
   		}
     }

	/**
	 * Events are index by their customAttribute name.  We need to get
	 * the events associated with an entry from the search results.
	 * This is needed for the calendar view.
	 * To do this, we create another mapping of events here.  This mapping
	 * maps a well known name to the real attribute name.
	 * @param doc
	 * @param entry
	 */
    public static void addEvents(Document doc, DefinableEntity entry) {
    	int count = 0;
    	Field eventName;
		Map customAttrs = entry.getCustomAttributes();
		Set keyset = customAttrs.keySet();
		Iterator attIt = keyset.iterator();
		// look through the custom attrs of this entry for any of type EVENT

		Set entryEventsDates = new HashSet();

		while (attIt.hasNext()) {
			CustomAttribute att = (CustomAttribute) customAttrs.get(attIt.next());
			if (att.getValueType() == CustomAttribute.EVENT) {
				// set the event name to event + count
				Event event = (Event)att.getValue();
				entryEventsDates.addAll(event.getAllEventDays());
								
				if (att.getValue() != null) {
					eventName = new Field(EVENT_FIELD + count, att.getName(), Field.Store.YES, Field.Index.UN_TOKENIZED);
					doc.add(eventName);
					count++;
				}
				doc.add(getRecurrenceDatesField(event));
			}
		}
		
		doc.add(getEntryEventDaysField(entryEventsDates));
		
		// Add event count field
    	Field eventCountField = new Field(EVENT_COUNT_FIELD, Integer.toString(count), Field.Store.YES, Field.Index.UN_TOKENIZED);
    	doc.add(eventCountField);
    }
    
    
	private static Field getEntryEventDaysField(Set dates) {
		StringBuilder sb = new StringBuilder();
		Iterator datesIt = dates.iterator();
		while (datesIt.hasNext()) {
			sb.append(DateTools.dateToString(((Calendar)datesIt.next()).getTime(), DateTools.Resolution.DAY));
			sb.append(" ");
		}

		if (sb.length() > 0)
			sb.deleteCharAt(sb.length() - 1);
		
		return new Field(EVENT_DATES_FIELD, sb.toString(), Field.Store.YES, Field.Index.TOKENIZED);
	}
	
	private static Field getRecurrenceDatesField(Event event) {
		StringBuilder sb = new StringBuilder();
		Iterator it = event.getAllRecurrenceDates().iterator();
		while (it.hasNext()) {
			Calendar[] eventDates = (Calendar[]) it.next();
			sb.append(DateTools.dateToString(eventDates[0].getTime(), DateTools.Resolution.SECOND));
			sb.append(" ");
			sb.append(DateTools.dateToString(eventDates[1].getTime(), DateTools.Resolution.SECOND));
			sb.append(",");
		}
		if (sb.length() > 0)
			sb.deleteCharAt(sb.length() - 1);

		return new Field(event.getName() + BasicIndexUtils.DELIMITER + EntityIndexUtils.EVENT_RECURRENCE_DATES_FIELD, sb.toString(), Field.Store.YES, Field.Index.UN_TOKENIZED);
	}
	
	public static void addAttachedFileIds(Document doc, DefinableEntity entry) {
		List atts = entry.getFileAttachments();
        for (int j = 0; j < atts.size(); j++) {
        	FileAttachment fa = (FileAttachment)atts.get(j);
        	addFileAttachmentUid(doc, fa);
        }
    }
        
    public static void addCommandDefinition(Document doc, DefinableEntity entry) {
        if (entry.getEntryDef() != null) {
        	Field cdefField = new Field(COMMAND_DEFINITION_FIELD, entry.getEntryDef().getId(), Field.Store.YES, Field.Index.UN_TOKENIZED);
            doc.add(cdefField);
        }
    }
        
    public static void addCreationPrincipalId(Document doc, DefinableEntity entry) {
    	//Add the id of the creator (no, not that one...)
        if (entry.getCreation() != null && entry.getCreation().getPrincipal() != null) {
        	Field creationIdField = new Field(CREATORID_FIELD, entry.getCreation().getPrincipal().getId().toString(), Field.Store.YES, Field.Index.UN_TOKENIZED);
            doc.add(creationIdField);
            Field creationNameField = new Field(CREATOR_NAME_FIELD, entry.getCreation().getPrincipal().getName().toString(), Field.Store.YES, Field.Index.UN_TOKENIZED);
            doc.add(creationNameField);
            Field creationTitleField = new Field(CREATOR_TITLE_FIELD, entry.getCreation().getPrincipal().getTitle().toString(), Field.Store.YES, Field.Index.TOKENIZED);
            doc.add(creationTitleField);
            Field creationSortTitleField = new Field(SORT_CREATOR_TITLE_FIELD, entry.getCreation().getPrincipal().getTitle().toString().toLowerCase(), Field.Store.YES, Field.Index.UN_TOKENIZED);
            doc.add(creationSortTitleField);
        }
    }   

    public static void addModificationPrincipalId(Document doc, DefinableEntity entry) {
    	//Add the id of the creator (no, not that one...)
        if (entry.getModification() != null && entry.getModification().getPrincipal() != null) {
        	Field modificationIdField = new Field(MODIFICATIONID_FIELD, entry.getModification().getPrincipal().getId().toString(), Field.Store.YES, Field.Index.UN_TOKENIZED);
        	doc.add(modificationIdField);
        	Field modificationNameField = new Field(MODIFICATION_NAME_FIELD, entry.getModification().getPrincipal().getName().toString(), Field.Store.YES, Field.Index.UN_TOKENIZED);
        	doc.add(modificationNameField);
        	Field modificationTitleField = new Field(MODIFICATION_TITLE_FIELD, entry.getModification().getPrincipal().getTitle().toString(), Field.Store.YES, Field.Index.UN_TOKENIZED);
        	doc.add(modificationTitleField);
        }
    }   

     public static void addDocId(Document doc, DefinableEntity entry) {
    	//Add the id of the creator (no, not that one...)
        Field docIdField = new Field(DOCID_FIELD, entry.getId().toString(), Field.Store.YES, Field.Index.UN_TOKENIZED);
        doc.add(docIdField);
    }

    public static void addBinder(Document doc, Binder binder) {
    	Field binderIdField;
    	if (binder != null)
    		binderIdField = new Field(BINDER_ID_FIELD, binder.getId().toString(), Field.Store.YES, Field.Index.UN_TOKENIZED);
    	else
    		binderIdField = new Field(BINDER_ID_FIELD, "", Field.Store.YES, Field.Index.UN_TOKENIZED);
    		
       	doc.add(binderIdField);
    }   
    
    public static String formatDayString(Date date) {
    	DateFormat df = DateFormat.getInstance();
    	SimpleDateFormat sf = (SimpleDateFormat)df;
    	sf.applyPattern("yyyyMMdd");
    	return(df.format(date));
    }
    public static void addReadAccess(Document doc, Binder binder) {
    	//set entryAcl to all
		Field entryAclField = new Field(BasicIndexUtils.ENTRY_ACL_FIELD, BasicIndexUtils.READ_ACL_ALL, Field.Store.NO, Field.Index.TOKENIZED);
		doc.add(entryAclField);
		//get real binder access
		doc.add(getBinderAccess(binder));
    }
    
    private static Field getBinderAccess(Binder binder) {
		Set binderIds = AccessUtils.getReadAccessIds(binder);
		StringBuffer bIds = new StringBuffer();
		if (!binderIds.isEmpty()) {
			for (Iterator i = binderIds.iterator(); i.hasNext();) {
				bIds.append(i.next()).append(" ");
			}
		} else {
			bIds.append(BasicIndexUtils.READ_ACL_ALL);
		}   		
		return new Field(BasicIndexUtils.FOLDER_ACL_FIELD, bIds.toString(), Field.Store.NO, Field.Index.TOKENIZED);

    }
    public static void addReadAccess(Document doc, Binder binder, DefinableEntity entry) {
		// Add ACL field. We only need to index ACLs for read access.
    	if (entry instanceof WorkflowSupport) {
    		WorkflowSupport wEntry = (WorkflowSupport)entry;
    		//get principals given read access 
         	Set ids = wEntry.getStateMembers(WfAcl.AccessType.read);
		// I'm not sure if putting together a long string value is more
		// efficient than processing multiple short strings... We will see.
		StringBuffer pIds = new StringBuffer();
		for (Iterator i = ids.iterator(); i.hasNext();) {
			pIds.append(i.next()).append(" ");
		}
       		if (ids.isEmpty() || wEntry.isWorkAreaAccess(WfAcl.AccessType.read)) {
       			//add all => folder check
       			pIds.append(BasicIndexUtils.READ_ACL_ALL);      			
       		}
		// Add the Entry_ACL field
		Field entryAclField = new Field(BasicIndexUtils.ENTRY_ACL_FIELD, pIds.toString(), Field.Store.NO, Field.Index.TOKENIZED);
		doc.add(entryAclField);
    		//add binder access
    		doc.add(getBinderAccess(binder));

    	} else addReadAccess(doc, binder);
			}

    public static void addTags(Document doc, DefinableEntity entry, List allTags) {
    	List pubTags = new ArrayList<Tag>();
    	List privTags = new ArrayList<Tag>();
    	String indexableTags = "";
    	String aclTags = "";
    	   	
    	for (Iterator iter=allTags.iterator(); iter.hasNext();) {
    		Tag thisTag = (Tag)iter.next();
    		if (thisTag.isPublic())
    			pubTags.add(thisTag);
    		else
    			privTags.add(thisTag);
    	}
    	pubTags = TagUtil.uniqueTags(pubTags);
    	privTags = TagUtil.uniqueTags(privTags);
    	
    	// index all the public tags (allTags field and tag_acl field)
		for (Iterator iter=pubTags.iterator(); iter.hasNext();) {
			Tag thisTag = (Tag)iter.next();
			indexableTags += " " + thisTag.getName();
			aclTags += " " + BasicIndexUtils.buildAclTag(thisTag.getName(), BasicIndexUtils.READ_ACL_ALL);
		}
	
		// now index the private tags (just the tag_acl field)
		for (Iterator iter=privTags.iterator(); iter.hasNext();) {
			Tag thisTag = (Tag)iter.next();
			aclTags += " " + BasicIndexUtils.buildAclTag(thisTag.getName(), thisTag.getOwnerIdentifier().getEntityId().toString());
		}
    
    	Field tagField = new Field(BasicIndexUtils.TAG_FIELD, indexableTags, Field.Store.YES, Field.Index.TOKENIZED);
    	doc.add(tagField);
    	
    	tagField = BasicIndexUtils.allTextField(indexableTags);
    	doc.add(tagField);
    	
    	tagField = new Field(BasicIndexUtils.ACL_TAG_FIELD, aclTags, Field.Store.YES, Field.Index.TOKENIZED);
    	doc.add(tagField);
    }
	
    public static void addFileAttachmentName(Document doc,String filename) {
      	Field fileNameField = new Field(FILENAME_FIELD, filename, Field.Store.YES, Field.Index.UN_TOKENIZED);
       	doc.add(fileNameField);
       	fileNameField = BasicIndexUtils.allTextField(filename);
    	doc.add(fileNameField);
    }
    
    public static void addFileExtension(Document doc,String fileName) {
      	Field fileExtField = new Field(FILE_EXT_FIELD, getFileExtension(fileName), Field.Store.YES, Field.Index.UN_TOKENIZED);
       	doc.add(fileExtField);   	
    }

    public static void addFileType(Document doc, File textfile) {
    	org.dom4j.Document document = null;
       	if ((textfile == null) || textfile.length() <= 0) return;
    	// open the file with an xml reader
		SAXReader reader = new SAXReader();
		try {
			document = reader.read(textfile);
			//textfile.delete();
			if (document == null) return;
		} catch (Exception e) {e.toString();}
        //} catch (Exception e) {logger.info("Error with converted file: " + e.toString());}
		// <document type="Microsoft Word 2002">
		Element x = (Element)document.selectSingleNode("/searchml");
		Element y = (Element)x.selectSingleNode("document");
		List nodes = document.selectNodes("/searchml");
		
      	
		Field fileTypeField = new Field(FILE_TYPE_FIELD, x.getText(), Field.Store.YES, Field.Index.UN_TOKENIZED);
       	doc.add(fileTypeField);   	
	
		
		return;    
    }
    
    public static String getFileExtension(String fileName) {
        int extensionStart = fileName.lastIndexOf('.');
        String extension = "";

        if (extensionStart >= 0) {
            extension = fileName.substring(extensionStart + 1);
        }

        return extension;
    }
    
    public static void addFileAttachmentUid(Document doc, FileAttachment fa) {
    	doc.removeFields(FILE_ID_FIELD);
    	Field fileIDField = new Field(FILE_ID_FIELD, fa.getId(), Field.Store.YES, Field.Index.UN_TOKENIZED);
    	doc.add(fileIDField); 
    }
    
    // in the _allText field for this attachment, just add the contents of
    // the file attachment, it's name, and it's creator/modifier
    public static Document addFileAttachmentAllText(Document doc) {
       	doc.removeFields(BasicIndexUtils.ALL_TEXT_FIELD);
       	String text = doc.getField(BasicIndexUtils.TEMP_FILE_CONTENTS_FIELD).stringValue();
       	doc.removeFields(BasicIndexUtils.TEMP_FILE_CONTENTS_FIELD);
       	text += " " + doc.getField(EntityIndexUtils.FILENAME_FIELD).stringValue();
       	text += " " + doc.getField(EntityIndexUtils.MODIFICATION_NAME_FIELD).stringValue();
       	text += " " + doc.getField(EntityIndexUtils.CREATOR_NAME_FIELD).stringValue();
       	Field allText = new Field(BasicIndexUtils.ALL_TEXT_FIELD, text, Field.Store.NO, Field.Index.TOKENIZED);
       	doc.add(allText);
       	return doc;
    }
    public static void addFileUnique(Document doc, boolean unique) {
       	Field uniqueField = new Field(FILE_UNIQUE_FIELD, Boolean.toString(unique), Field.Store.YES, Field.Index.UN_TOKENIZED);
    	doc.add(uniqueField);     	
    }

    public static void addAncestry(Document doc, DefinableEntity entry) {
    	
    	Binder parentBinder = entry.getParentBinder();
    	
    	while (parentBinder != null) {	
    		Field ancestry = new Field(ENTRY_ANCESTRY, parentBinder.getId().toString(), Field.Store.YES, Field.Index.UN_TOKENIZED);
    		doc.add(ancestry);
    		parentBinder = ((Binder)parentBinder).getParentBinder();
    	}
    }
}
