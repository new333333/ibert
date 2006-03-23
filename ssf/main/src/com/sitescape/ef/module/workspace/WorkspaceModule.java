package com.sitescape.ef.module.workspace;

import java.util.Collection;

import com.sitescape.ef.domain.NoWorkspaceByTheIdException;
import com.sitescape.ef.domain.Workspace;
import com.sitescape.ef.security.AccessControlException;
import org.dom4j.Document;
import com.sitescape.ef.module.shared.DomTreeBuilder;
/**
 * @author Jong Kim
 *
 */
public interface WorkspaceModule {
    /**
     * 
     * @param zoneName
     * @param workspaceName If <code>null</code>, default workspace is assumed.
     * @return
     * @throws NoWorkspaceByTheNameException
     */
	public Workspace getWorkspace() throws NoWorkspaceByTheIdException, AccessControlException;
	public Workspace getWorkspace(Long workspaceId)	throws NoWorkspaceByTheIdException, AccessControlException;
  	/**
  	 * Return list of child binders, that have been verified for read access
  	 * and sorted by title
  	 * @param id
  	 * @return
  	 * @throws AccessControlException
  	 */
	public Collection getWorkspaceTree(Long id) throws AccessControlException; 
  	public Document getDomWorkspaceTree(DomTreeBuilder domTreeHelper) throws AccessControlException;
    public Document getDomWorkspaceTree(Long id, DomTreeBuilder domTreeHelper, boolean recurse) throws AccessControlException;
}
