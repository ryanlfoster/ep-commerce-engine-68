package com.elasticpath.domain.misc;

import java.util.Map;

import com.elasticpath.domain.EpDomain;

/**
 * Interface for transient property based classes.
 * 
 * @deprecated use {@link com.elasticpath.persistence.dao.PropertyLoaderAware}
 */
@Deprecated
public interface PropertyBased extends EpDomain {

	/**
	 * Set the properties files that are used as a source of data.
	 *
	 * @param propertiesMap the map containing properties.
	 */
	void setPropertiesMap(Map<String, ? extends Map<Object, Object>> propertiesMap);

}
