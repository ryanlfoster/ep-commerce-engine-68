/**
 * 
 */
package com.elasticpath.service.targetedselling;

import java.util.List;

import com.elasticpath.domain.contentspace.DynamicContent;
import com.elasticpath.domain.targetedselling.DynamicContentDelivery;
import com.elasticpath.base.exception.EpServiceException;

/**
 * DynamicContentDeliveryService
 * service for DynamicContentDelivery.
 */
public interface DynamicContentDeliveryService extends TargetedSellingService<DynamicContentDelivery> {	
	
	/**
	 * Find DCD by GUID.
	 * @param guid the guid
	 * @return {@link DynamicContentDelivery}
	 */
	DynamicContentDelivery findByGuid(String guid);
	
	/**
	 * Find by Content Space name.
	 * @param name content space name
	 * @return result list
	 */
	List<DynamicContentDelivery> findByContentSpaceName(String name);
	
	/**
	 * Find by multiply parameters.
	 * @param dynamicContentDeliveryName Dynamic Content Delivery name
	 * @param dynamicContentName Dynamic Content name
	 * @param contentSpaceName Content Space name
	 * @return result list
	 */
	List<DynamicContentDelivery> findBy(String dynamicContentDeliveryName, String dynamicContentName, String contentSpaceName);
	
	/**
	 * Find {@link DynamicContent} objects that have been assigned to a delivery context, by partial name. 
	 * @param string the partial name of the {@code DynamicContent}
	 * @return the {@code DynamicContent} objects matching the given partial name that have been assigned to a delivery context
	 * @throws EpServiceException in case of error
	 */
	List<DynamicContent> findAssignedDynamicContentByPartialName(final String string) throws EpServiceException;
	
	/**
	 * Find {@link DynamicContent} objects that have NOT been assigned to a delivery context, by partial name. 
	 * @param string the partial name of the {@code DynamicContent}
	 * @return the {@code DynamicContent} objects matching the given partial name that have not been assigned to a delivery context.
	 * @throws EpServiceException in case of error
	 */
	List<DynamicContent> findUnAssignedDynamicContentByPartialName(final String string) throws EpServiceException;
	
	/**
	 * Checks whether the given {@link DynamicContent} is used (assigned to a delivery context).
	 * @param dynamicContent the content to check
	 * @return true if it's assigned, false if not
	 * @throws EpServiceException in case of error
	 */
	boolean isDynamicContentAssigned(final DynamicContent dynamicContent) throws EpServiceException;
	
	/**
	 * Get the list dynamic content delivery by given selling context.
	 * @param sellingContextGuid given selling context guid.
	 * @return list dynamic content delivery by given selling context.
	 * @throws EpServiceException in case of error.
	 */
	List<DynamicContentDelivery> findBySellingContextGuid(String sellingContextGuid)  throws EpServiceException;

}
