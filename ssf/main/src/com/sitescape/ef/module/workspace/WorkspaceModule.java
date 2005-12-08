package com.sitescape.ef.module.workspace;

import com.sitescape.ef.domain.NoWorkspaceByTheIdException;
import com.sitescape.ef.domain.Workspace;
import com.sitescape.ef.security.AccessControlException;
import org.dom4j.Document;
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
    public Document getDomWorkspaceTree() throws AccessControlException;
    public Document getDomWorkspaceTree(Long id) throws AccessControlException;
}
