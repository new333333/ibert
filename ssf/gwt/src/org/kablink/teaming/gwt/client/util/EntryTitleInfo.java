/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
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

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Class used to bundle information regarding an entry's title through
 * GWT RPC requests.
 *  
 * @author drfoster
 */
public class EntryTitleInfo implements IsSerializable {
	private boolean		m_descriptionIsHtml;	//
	private boolean		m_file;					//
	private boolean 	m_trash;				//
	private boolean		m_seen;					//
	private EntityId	m_entityId;				//
	private String		m_description;			//
	private String		m_fileDownloadUrl;		//
	private String		m_fileIcon;				//
	private String  	m_title;				//
	
	// The following is only used on the client side to push an Image
	// through to the data table for items.
	private transient Object m_clientItemImage; 

	/**
	 * Constructor method.
	 * 
	 * No parameters as per GWT serialization requirements.
	 */
	public EntryTitleInfo() {
		// Nothing to do.
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public boolean  isDescriptionHtml()  {return m_descriptionIsHtml;}
	public boolean  isFile()             {return m_file;             }
	public boolean  isSeen()             {return m_seen;             }
	public boolean  isTrash()            {return m_trash;            }
	public EntityId getEntityId()        {return m_entityId;         }
	public String   getFileDownloadUrl() {return m_fileDownloadUrl;  }
	public String   getFileIcon()        {return m_fileIcon;         }
	public String   getDescription()     {return m_description;      }
	public String   getTitle()           {return m_title;            }
	public Object   getClientItemImage() {return m_clientItemImage;  }
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setFile(             boolean  file)              {m_file              = file;             }
	public void setSeen(             boolean  seen)              {m_seen              = seen;             }
	public void setTrash(            boolean  trash)             {m_trash             = trash;            }
	public void setEntityId(         EntityId entityId)          {m_entityId          = entityId;         }
	public void setFileDownloadUrl(  String   fileDownloadUrl)   {m_fileDownloadUrl   = fileDownloadUrl;  }
	public void setFileIcon(         String   fileIcon)          {m_fileIcon          = fileIcon;         }
	public void setDescription(      String   description)       {m_description       = description;      }
	public void setDescriptionIsHtml(boolean  descriptionIsHtml) {m_descriptionIsHtml = descriptionIsHtml;}
	public void setTitle(            String   title)             {m_title             = title;            }
	public void setClientItemImage(  Object   clientItemImage)   {m_clientItemImage   = clientItemImage;  }
	
	/**
	 * Constructs an EntryTitleInfo from the parameters.
	 *
	 * @param seen
	 * @param trash
	 * @param title
	 * @param description
	 * @param descriptionIsHtml
	 * 
	 * @return
	 */
	public static EntryTitleInfo construct(EntityId entityId, boolean seen, boolean trash, String title, String description, boolean descriptionIsHtml) {
		EntryTitleInfo reply = new EntryTitleInfo();
		
		reply.setEntityId(         entityId         );
		reply.setSeen(             seen             );
		reply.setTrash(            trash            );
		reply.setTitle(            title            );
		reply.setDescription(      description      );
		reply.setDescriptionIsHtml(descriptionIsHtml);
		
		return reply;
	}
}