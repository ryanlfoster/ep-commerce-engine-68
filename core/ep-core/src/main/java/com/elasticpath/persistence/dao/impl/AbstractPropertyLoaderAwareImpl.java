package com.elasticpath.persistence.dao.impl;

import java.util.Properties;

import com.elasticpath.domain.impl.AbstractEpDomainImpl;
import com.elasticpath.persistence.dao.PropertyLoaderAware;

/**
 * Implementation helper for {@link PropertyLoaderAware}.
 */
public abstract class AbstractPropertyLoaderAwareImpl extends AbstractEpDomainImpl implements PropertyLoaderAware {

	private static final long serialVersionUID = 1L;

	private Properties properties;

	@Override
	public void setInitializingProperties(final Properties properties) {
		this.properties = properties;
	}

	@Override
	public String getProperty(final String name) {
		return properties.getProperty(name);
	}

	protected Properties getProperties() {
		return properties;
	}
}
