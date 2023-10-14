package com.databuck.bean;

public class Domain implements java.io.Serializable {
	static final long serialVersionUID = 8999999L;
	// data member
		private int domainId;
		private String domainName;
		
		
		public int getDomainId() {
			return domainId;
		}
		public void setDomainId(int domainId) {
			this.domainId = domainId;
		}
		public String getDomainName() {
			return domainName;
		}
		public void setDomainName(String domainName) {
			this.domainName = domainName;
		}
		
		
		@Override
		public String toString() {
			return "Domain [domainId=" + domainId + ", domainName=" + domainName + "]";
		}
		
	
}
