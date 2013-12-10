/**
 * 
 */
package com.elasticpath.sfweb.tags.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.sfweb.servlet.facade.HttpServletRequestFacade;
import com.elasticpath.sfweb.servlet.listeners.BrowsingBehaviorEventListener;
import com.elasticpath.sfweb.tags.TagProcessorsHolder;

/**
 * TagProcessorsHolderImpl.
 *
 */
public class TagProcessorsHolderImpl implements TagProcessorsHolder {

	private static final Logger LOG = Logger.getLogger(TagProcessorsHolderImpl.class);
	
	private List<BrowsingBehaviorEventListener> listTagsProcessors = new ArrayList<BrowsingBehaviorEventListener>();

	@Override
	public List<BrowsingBehaviorEventListener> getListTagsProcessors() {
		return listTagsProcessors;
	}

	@Override
	public void setListTagsProcessors(final List<BrowsingBehaviorEventListener> listTagsProcessors) {
		this.listTagsProcessors = listTagsProcessors;
	} 
	
	@Override
	public void removeAllListeners() {
		this.listTagsProcessors.clear();
	}

	@Override
	public void fireExecute(final CustomerSession session, final HttpServletRequestFacade request) {
		if (session == null) {
			LOG.debug("The session is null. ignoring the listeners...");
			return;
		}
		for (final BrowsingBehaviorEventListener listener : this.listTagsProcessors) {
			try {
				listener.execute(session, request);
			} catch (Exception e) {
				LOG.error("Failed to invoke listener.", e);
			}
		}
	}
}
