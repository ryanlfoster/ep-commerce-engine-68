/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.service.changeset.impl;

import java.util.ArrayList;
import java.util.List;

import com.elasticpath.commons.pagination.Page;
import com.elasticpath.commons.pagination.PaginatorImpl;
import com.elasticpath.domain.changeset.ChangeSetMember;
import com.elasticpath.service.changeset.ChangeSetService;

/**
 * This adapter is responsible for retrieving change set members 
 * and providing the required information for a particular page.
 */
public class ChangeSetMemberPaginator extends PaginatorImpl<ChangeSetMember> {
	
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	private ChangeSetService changeSetService;

	private static List<String> filter = new ArrayList<String>();

	static {
		filter.add("Sku Option Value");
	}
	
	@Override
	protected List<ChangeSetMember> findItems(final Page<ChangeSetMember> page) {
		int startIndex = page.getPageStartIndex() - 1;
		return getChangeSetService().findFilteredMembersByChangeSetGuid(getGroupId(), 
				startIndex, page.getPageSize(), page.getOrderingFields(), getLoadTuner(), filter);
	}

	/**
	 *
	 * @return the change set service
	 */
	protected ChangeSetService getChangeSetService() {
		return changeSetService;
	}

	/**
	 *
	 * @param changeSetService the changeSetService to set
	 */
	public void setChangeSetService(final ChangeSetService changeSetService) {
		this.changeSetService = changeSetService;
	}

	/**
	 *
	 * @return the group ID
	 */
	protected String getGroupId() {
		return super.getObjectId();
	}

	@Override
	public long getTotalItems() {
		return getChangeSetService().getFilteredChangeSetMemberCount(getGroupId(), filter);
	}
	
}
