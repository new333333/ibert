package com.sitescape.ef.domain;
import java.io.Serializable;



/**
 * @author Janet McCann
 *
 */
public class UserPropertiesPK implements Serializable {
	private final static long serialVersionUID=1;
	private Long binderId,principalId;
	public UserPropertiesPK() {
		
	}
	public UserPropertiesPK(Long principalId) {
		this.principalId = principalId;
		binderId = new Long(-1);
	}
	public UserPropertiesPK(Long principalId, Long binderId) {
		this.principalId = principalId;
		this.binderId = binderId;
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
	public Long getBinderId() {
		return binderId;
	}
	public void setBinderId(Long binderId) {
		this.binderId = binderId;
	}
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof UserPropertiesPK) {
			UserPropertiesPK pk = (UserPropertiesPK) obj;
			if (pk.getPrincipalId().equals(principalId) && 
					pk.getBinderId().equals(binderId)) return true;
		}
		return false;
	}
	public int hashCode() {
		return 31*binderId.hashCode() + principalId.hashCode();
	}
}
