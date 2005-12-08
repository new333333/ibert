package com.sitescape.ef.domain;
import java.io.Serializable;



/**
 * @author Janet McCann
 *
 */
public class UserPropertiesPK implements Serializable {
	private final static long serialVersionUID=1;
	private Long folderId, principalId;
	public UserPropertiesPK() {
		
	}
	public UserPropertiesPK(Long principalId) {
		this.principalId = principalId;
		folderId = new Long(0);
	}
	public UserPropertiesPK(Long principalId, Long folderId) {
		this.principalId = principalId;
		this.folderId = folderId;
	}
	/**
 	 * @hibernate.key-property position="1"
 	 */
	public Long getPrincipalId() {
		return principalId;
	}
	public void setPrincipalId(Long principalId) {
		this.principalId = principalId;
	}
	/**
 	 * @hibernate.key-property position="2"
 	 */
	public Long getFolderId() {
		return folderId;
	}
	public void setFolderId(Long folderId) {
		this.folderId = folderId;
	}
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof UserPropertiesPK) {
			UserPropertiesPK pk = (UserPropertiesPK) obj;
			if (pk.getPrincipalId().equals(principalId) && 
					pk.getFolderId().equals(folderId)) return true;
		}
		return false;
	}
	public int hashCode() {
		return 31*folderId.hashCode() + principalId.hashCode();
	}
}
