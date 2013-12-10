package com.elasticpath.sfweb.servlet.listeners;


import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.sfweb.servlet.facade.HttpServletRequestFacade;


/**
 * Interface for listeners of customer session events during all session.
 */
public interface HttpSessionEventListener {
	
	/**
	 * Execute action based on information in the customer session and request.
	 * @param session instance of {@link CustomerSession} that started.
	 * @param request the originating HttpServletRequest. 
	 */
	void execute(CustomerSession session, HttpServletRequestFacade request);
	

}
