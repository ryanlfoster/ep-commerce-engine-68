package com.elasticpath.domain.targetedselling;

import java.util.Set;

import com.elasticpath.domain.contentspace.ContentSpace;
import com.elasticpath.domain.contentspace.DynamicContent;
import com.elasticpath.domain.sellingcontext.SellingContext;
import com.elasticpath.persistence.api.Entity;

/**
 * <p>Defines all of the {@link ContentSpace}s within which a particular piece of 
 * {@link DynamicContent} could appear.
 * Also, defines SellingContext's guid in which the DynamicContent may appear.
 * In the case where more than one {@code DynamicContentDelivery}'s selling context applies
 * to a given {@link ContentSpace}, the priority of the {@code DynamicContentDelivery} 
 * can be evaluated to select a single winner.</p> 
 */
public interface DynamicContentDelivery extends Entity {
	
	/**
	 * Get the delivery name.
	 * @return delivery name
	 */
	String getName();
	
	/**
	 * Set the delivery name.
	 * @param name delivery name
	 */
	void setName(final String name);
	
	/**
	 * Get the delivery description.
	 * @return delivery description
	 */
	String getDescription();
	
	/**
	 * Set the delivery description.
	 * @param description of the delivery
	 */
	void setDescription(final String description);	

	/**
	 * Get the dynamic contents.
	 * @return Get Dynamic Content for this delivery
	 */
	DynamicContent getDynamicContent();

	/**
	 * Set the dynamic content for this delivery.
	 * @param dynamicContent  a set of dynamic content 
	 */
	void setDynamicContent(final DynamicContent dynamicContent);
	
	
	/**
	 * Get the priority of delivery. 
	 * @return priority
	 */
	int getPriority();
	
	/**
	 * Set the priority of delivery.
	 * @param priority of delivery.
	 */
	void setPriority(int priority);
	
	/**
	 * Get the set of assigned targets.
	 * @return Set of assigned targets
	 */
	Set<ContentSpace> getContentspaces();

	/**
	 * Set assigned contenspaces.
	 * @param contentspaces Set of assigned contenspaces
	 */
	void setContentspaces(final Set<ContentSpace> contentspaces);

	/**
	 * Gets the selling context.
	 * 
	 * @return Selling context for this DCA.
	 */	
	SellingContext getSellingContext();
	
	/**
	 * Set selling context for this DCA which will provide conditions for associated with specific tag dictionary.
	 *
	 * @param sellingContext the selling context for this DCA to set
	 */
	void setSellingContext(SellingContext sellingContext);

	/**
	 * Gets the selling context GUID.
	 * 
	 * @return the selling context GUID or null if none has been set
	 */
	String getSellingContextGuid();

}
