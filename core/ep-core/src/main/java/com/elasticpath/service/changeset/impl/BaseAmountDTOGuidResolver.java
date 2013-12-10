/**
 * 
 */
package com.elasticpath.service.changeset.impl;

import com.elasticpath.common.dto.pricing.BaseAmountDTO;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.misc.RandomGuid;
import com.elasticpath.service.changeset.ObjectGuidResolver;

/**
 * PriceListDescriptorDTO guid resolver class.
 *
 */
public class BaseAmountDTOGuidResolver implements ObjectGuidResolver {
	private BeanFactory beanFactory;
	
	@Override
	public String resolveGuid(final Object object) {
		BaseAmountDTO dto = (BaseAmountDTO) object;
		if (dto.getGuid() == null) {
			return getBeanFactory().<RandomGuid> getBean(ContextIdNames.RANDOM_GUID).toString();
		}
		return dto.getGuid();
	}

	@Override
	public boolean isSupportedObject(final Object object) {
		return object instanceof BaseAmountDTO;
	}

	/**
	 * @return the bean factory
	 */
	public BeanFactory getBeanFactory() {
		return beanFactory;
	}

	/**
	 * @param beanFactory to use
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}
}
