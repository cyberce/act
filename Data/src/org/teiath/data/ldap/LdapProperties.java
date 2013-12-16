package org.teiath.data.ldap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("ldapProperties")
public class LdapProperties {

	@Value("${ldap.enabled}")
	private Boolean ldapEnabled;

	public LdapProperties() {
	}

	public Boolean isLdapEnabled() {
		return ldapEnabled;
	}

	public void setLdapEnabled(Boolean ldapEnabled) {
		this.ldapEnabled = ldapEnabled;
	}
}
