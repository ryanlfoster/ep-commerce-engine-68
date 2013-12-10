package com.elasticpath.importexport.util.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import com.elasticpath.importexport.util.ApplicationPropertiesHelper;

/**
 * ApplicationPropertiesHelper Implementation.
 */
public class ApplicationPropertiesHelperImpl implements ApplicationPropertiesHelper {

	private Properties applicationProperties;

	@Override
	public Map<String, String> getPropertiesWithNameStartsWith(final String prefix) {
		final Map<String, String> results = new HashMap<String, String>();
		for (Entry<Object, Object> entry : getApplicationProperties().entrySet()) {
			final String key = (String) entry.getKey();
			if (key.startsWith(prefix)) {
				results.put(key, (String) entry.getValue());
			}
		}

		return results;
	}

	protected Properties getApplicationProperties() {
		return applicationProperties;
	}

	public void setApplicationProperties(final Properties applicationProperties) {
		this.applicationProperties = applicationProperties;
	}
}
