package com.elasticpath.ql.custom.customer;

import com.elasticpath.domain.attribute.AttributeUsage;
import com.elasticpath.ql.parser.AbstractEpQLCustomConfiguration;

/**
 * Holds mapping between EqQL fields and field descriptors for Customer Profile Attributes.
 */
public class CustomerProfileAttributeConfiguration extends AbstractEpQLCustomConfiguration {
	
	@Override
	public void initialize() {
		setQueryPrefix("SELECT a.key FROM AttributeImpl a ");
		setQueryPostfix(" WHERE a.attributeUsageIdInternal = " + AttributeUsage.CUSTOMERPROFILE + " ORDER BY a.key ASC");
	}
}
