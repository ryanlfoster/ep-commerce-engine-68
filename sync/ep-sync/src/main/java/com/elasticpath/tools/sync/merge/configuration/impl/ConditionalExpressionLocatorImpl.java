/**
 * 
 */
package com.elasticpath.tools.sync.merge.configuration.impl;

import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.tags.domain.ConditionalExpression;
import com.elasticpath.tags.service.TagConditionService;
import com.elasticpath.tools.sync.exception.SyncToolConfigurationException;

/**
 * The ConditionalExpression locator.
 *
 */
public class ConditionalExpressionLocatorImpl extends AbstractEntityLocator {

	private TagConditionService tagConditionService;
	
	/* (non-Javadoc)
	 * @see com.elasticpath.tools.sync.merge.configuration.EntityLocator#isResponsibleFor(java.lang.Class)
	 */
	@Override
	public boolean isResponsibleFor(final Class< ? > clazz) {
		return ConditionalExpression.class.isAssignableFrom(clazz);
	}

	/* (non-Javadoc)
	 * @see com.elasticpath.tools.sync.merge.configuration.EntityLocator#locatePersistence(java.lang.String, java.lang.Class)
	 */
	@Override
	public Persistable locatePersistence(final String guid, final Class< ? > clazz) throws SyncToolConfigurationException {
		return this.tagConditionService.findByGuid(guid);
	}

	/**
	 * @param tagConditionService the tagConditionService to set
	 */
	public void setTagConditionService(final TagConditionService tagConditionService) {
		this.tagConditionService = tagConditionService;
	}
}
