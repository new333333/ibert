/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.gwt.client.util;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * Class used to communicate information about contents of a task
 * folder between the client (i.e., the TaskListing) and the server
 * (i.e., GwtRpcServiceImpl.getTaskListItem().)
 * 
 * @author drfoster@novell.com
 *
 */
public class TaskListItem implements IsSerializable {
	private List<TaskListItem>	m_subtasks = new ArrayList<TaskListItem>();
	private TaskInfo			m_task     = new TaskInfo();
	
	/*
	 * Inner class used to communicate information about an individual
	 * task.
	 * 
	 * The information contained in this object is constructed using
	 * the information returned from a search for the FolderEntry's to
	 * display from a task folder.  It's basically the same information
	 * displayed by task_folder_list.jsp.
	 */
	public static class TaskInfo implements IsSerializable {
		private boolean		m_overdue;	
		private boolean		m_seen;	
		private List<Long>	m_assignments      = new ArrayList<Long>();
		private List<Long>	m_assignmentGroups = new ArrayList<Long>();
		private List<Long>	m_assignmentTeams  = new ArrayList<Long>();
		private Long		m_binderId;	
		private Long		m_taskId;	
		private String		m_completed  = "";	
		private String		m_entityType = "";
		private String		m_priority   = "";
		private String		m_title      = "";
		private String		m_status     = "";
		
		/**
		 * Constructor method.
		 * 
		 * No parameters as per GWT serialization requirements.
		 */
		public TaskInfo() {
			// Nothing to do.
		}
		
		/**
		 * Get'er methods.
		 * 
		 * @return
		 */
		public boolean    getOverdue()          {return m_overdue;         }
		public boolean    getSeen()             {return m_seen;            }
		public List<Long> getAssignments()      {return m_assignments;     }
		public List<Long> getAssignmentGroups() {return m_assignmentGroups;}
		public List<Long> getAssignmentTeams()  {return m_assignmentTeams; }
		public Long       getBinderId()         {return m_binderId;        }
		public Long       getTaskId()           {return m_taskId;          }
		public String     getCompleted()        {return m_completed;       }
		public String     getEntityType()       {return m_entityType;      }
		public String     getPriority()         {return m_priority;        }
		public String     getTitle()            {return m_title;           }
		public String     getStatus()           {return m_status;          }
		
		/**
		 * Set'er methods.
		 * 
		 * @return
		 */
		public void setOverdue(         boolean    overdue)          {m_overdue          = overdue;         }
		public void setSeen(            boolean    seen)             {m_seen             = seen;            }
		public void setAssignments(     List<Long> assignments)      {m_assignments      = assignments;     }
		public void setAssignmentGroups(List<Long> assignmentGroups) {m_assignmentGroups = assignmentGroups;}
		public void setAssignmentTeams( List<Long> assignmentTeams)  {m_assignmentTeams  = assignmentTeams; }
		public void setBinderId(        Long       binderId)         {m_binderId         = binderId;        }
		public void setTaskId(          Long       taskId)           {m_taskId           = taskId;          }
		public void setCompleted(       String     completed)        {m_completed        = completed;       }
		public void setEntityType(      String     entityType)       {m_entityType       = entityType;      }
		public void setPriority(        String     priority)         {m_priority         = priority;        }
		public void setTitle(           String     title)            {m_title            = title;           }
		public void setStatus(          String     status)           {m_status           = status;          }
	}
	
	/**
	 * Constructor method.
	 * 
	 * No parameters as per GWT serialization requirements.
	 */
	public TaskListItem() {
		// Nothing to do.
	}

	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public List<TaskListItem> getSubtasks() {return m_subtasks;}
	public TaskInfo           getTask()     {return m_task;    }
	
	/**
	 * Set'er methods.
	 * 
	 * @return
	 */
	public void setSubtasks(List<TaskListItem> subtasks) {m_subtasks = subtasks;}
	public void setTask(    TaskInfo           task)     {m_task     = task;    }
}
