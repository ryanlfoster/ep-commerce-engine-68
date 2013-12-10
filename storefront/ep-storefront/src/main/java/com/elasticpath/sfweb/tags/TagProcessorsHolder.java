/**
 * 
 */
package com.elasticpath.sfweb.tags;

import java.util.List;

import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.sfweb.servlet.facade.HttpServletRequestFacade;
import com.elasticpath.sfweb.servlet.listeners.BrowsingBehaviorEventListener;

/**
 * TagProcessorsHolder.
 *
 */
public interface TagProcessorsHolder {

	/**
	 * Setter for list tag processors. 
	 * @return the listTagsProcessors
	 */
	List<BrowsingBehaviorEventListener> getListTagsProcessors();

	/**
	 * Setter for list tags processors.
	 * @param listTagsProcessors the listTagsProcessors to set
	 */
	void setListTagsProcessors(List<BrowsingBehaviorEventListener> listTagsProcessors);

	/**
	 * Remove all listeners.
	 */
	void removeAllListeners();
	
	/**
	 * Fire event.
	 * @param session customer session
	 * @param request http request
	 */
	void fireExecute(CustomerSession session, HttpServletRequestFacade request);
}
