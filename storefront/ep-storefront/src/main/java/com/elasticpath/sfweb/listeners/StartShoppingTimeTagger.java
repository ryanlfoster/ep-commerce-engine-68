package com.elasticpath.sfweb.listeners;

import java.util.Calendar;

import org.apache.log4j.Logger;

import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.sfweb.servlet.facade.HttpServletRequestFacade;
import com.elasticpath.sfweb.servlet.listeners.CustomerLoginEventListener;
import com.elasticpath.sfweb.servlet.listeners.NewHttpSessionEventListener;
import com.elasticpath.tags.Tag;
import com.elasticpath.tags.TagSet;

/**
 * Adds the SHOPPING_START_TIME_TAG tag to a customer's tag cloud.
 */
public class StartShoppingTimeTagger implements NewHttpSessionEventListener, CustomerLoginEventListener {
	
	private static final String SHOPPING_START_TIME = "SHOPPING_START_TIME";
	private static final Logger LOG = Logger.getLogger(StartShoppingTimeTagger.class);

	/**
	 * Apply the start shopping tag to the given session.
	 * 
	 * @param session instance of CustomerSession
	 * @param request the originating HttpServletRequest
	 */
	public void execute(final CustomerSession session, final HttpServletRequestFacade request) {
		TagSet tagSet = session.getCustomerTagSet();
		addStartShoppingTimeTag(tagSet);
	}
	
	/**
	 * Adds the CreateSessionStartTime tag with the given startTime to the given tagMap.
	 * @param tagSet the tag set to which the tag should be added
	 */
	void addStartShoppingTimeTag(final TagSet tagSet) {
		long currentTimeMilliseconds = Calendar.getInstance().getTimeInMillis();
		tagSet.addTag(SHOPPING_START_TIME, new Tag(currentTimeMilliseconds));
		if (LOG.isDebugEnabled()) {
			LOG.debug("Updated customer tag cloud with start shopping time in ms: " + tagSet);
		}
	}
}