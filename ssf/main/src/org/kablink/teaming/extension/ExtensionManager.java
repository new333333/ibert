package org.kablink.teaming.extension;

import java.util.List;
import java.util.Map;

import org.kablink.teaming.domain.ExtensionInfo;

public interface ExtensionManager {
	
	public void addExtension(String name, Long zoneId, Map updates);
	public List<ExtensionInfo> getExtensions();
	public void modifyExtension(String id, Map updates);
	public void removeExtensions(String id);
	public void deploy();
	
//	public ExtensionDeployer getExtDeployer();
//	public void setExtDeployer(ExtensionDeployer extDeployer);
}
