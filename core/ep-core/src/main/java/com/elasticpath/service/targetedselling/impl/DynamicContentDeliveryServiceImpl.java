/**
 * Replace for campaign service.
 */
package com.elasticpath.service.targetedselling.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.elasticpath.domain.contentspace.DynamicContent;
import com.elasticpath.domain.targetedselling.DynamicContentDelivery;
import com.elasticpath.persistence.api.EpPersistenceException;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.targetedselling.DynamicContentDeliveryService;

/**
 * DynamicContentDeliveryServiceImpl service for querying a DynamicContentDelivery.
 */
public class DynamicContentDeliveryServiceImpl extends AbstractTargetedSellingServiceImpl<DynamicContentDelivery>
	implements DynamicContentDeliveryService {

	private static final String QUERY_FIND_BY_NAME = "DYNAMIC_CONTENT_DELIVERY_FIND_BY_NAME";

	private static final String QUERY_FIND_BY_NAME_LIKE = "DYNAMIC_CONTENT_DELIVERY_FIND_BY_NAME_LIKE";

	private static final String QUERY_FIND_ALL = "DYNAMIC_CONTENT_DELIVERY_FIND_ALL";

	private static final String QUERY_FIND_BY_CONTENT_SPACE = "DYNAMIC_CONTENT_DELIVERY_FIND_BY_CONTENT_SPACE";

	private static final String QUERY_FIND_BY = "DYNAMIC_CONTENT_DELIVERY_FIND_BY";

	private static final String QUERY_FIND_DCD_BY_SELLING_CONTEXT_GUID = "DYNAMIC_CONTENT_DELIVERY_FIND_BY_SELLING_CONTEXT_GUID";

	@Override
	protected String getQueryFindByName() {
		return QUERY_FIND_BY_NAME;
	}

	@Override
	protected String getQueryFindByNameLike() {
		return QUERY_FIND_BY_NAME_LIKE;
	}

	@Override
	protected String getFindAllQueryName() {
		return QUERY_FIND_ALL;
	}

	@Override
	public List<DynamicContentDelivery> findByContentSpaceName(final String name) {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery(QUERY_FIND_BY_CONTENT_SPACE, escapeSpecialCharacters(name));
	}

	@Override
	public DynamicContentDelivery findByGuid(final String guid) {
		sanityCheck();
		List<DynamicContentDelivery> result = getPersistenceEngine().retrieveByNamedQuery("DYNAMIC_CONTENT_DELIVERY_FIND_BY_GUID",
				escapeSpecialCharacters(guid));
		if (result.isEmpty()) {
			return null;
		}
		return result.get(0);
	}

	@Override
	public List<DynamicContentDelivery> findBy(final String dynamicContentDeliveryName,
			final String dynamicContentName,
			final String contentSpaceName) {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery(QUERY_FIND_BY,
				"%" + escapeSpecialCharacters(dynamicContentDeliveryName) + "%",
				"%" + escapeSpecialCharacters(dynamicContentName) + "%",
				"%" + escapeSpecialCharacters(contentSpaceName) + "%");
	}

	/**
	 * Find {@link DynamicContent} objects that have been assigned to a delivery context, by partial name.
	 *
	 * @param string the partial name of the {@code DynamicContent}
	 * @return the {@code DynamicContent} objects matching the given partial name that have been assigned to a delivery context
	 */
	public List<DynamicContent> findAssignedDynamicContentByPartialName(final String string) {
		List<DynamicContent> results = new ArrayList<DynamicContent>();
		sanityCheck();
		try {
			results = getPersistenceEngine().retrieveByNamedQuery("DYNAMIC_CONTENT_FIND_BY_NAME_LIKE_ASSIGNED",
					"%" + escapeSpecialCharacters(string) + "%");
		} catch (EpPersistenceException ex) {
			throw new EpServiceException("Unable to find assigned dynamic content.", ex);
		}
		return results;
	}

	/**
	 * Find {@link DynamicContent} objects that have NOT been assigned to a delivery context, by partial name.
	 *
	 * @param string the partial name of the {@code DynamicContent}
	 * @return the {@code DynamicContent} objects matching the given partial name that have not been assigned to a delivery context.
	 */
	public List<DynamicContent> findUnAssignedDynamicContentByPartialName(final String string) {
		List<DynamicContent> results = new ArrayList<DynamicContent>();
		sanityCheck();
		try {
			List<Long> assignedDynamicContentUids = getAssignedDynamicContentUids(findAssignedDynamicContentByPartialName(""));
			results = getPersistenceEngine().retrieveByNamedQueryWithList("DYNAMIC_CONTENT_FIND_BY_NAME_LIKE_NOTASSIGNED",
					"list",
					assignedDynamicContentUids,
					"%" + escapeSpecialCharacters(string) + "%");
		} catch (EpPersistenceException ex) {
			throw new EpServiceException("Unable to find unassigned dynamic content.", ex);
		}
		return results;
	}

	/**
	 * Get the list of uidpk from dynamic content collection.
	 *
	 * @param collection of Dynamic Content.
	 * @return list of uidpk.
	 */
	private List<Long> getAssignedDynamicContentUids(final Collection<DynamicContent> collection) {
		ArrayList<Long> assignedDynamicContentUids = new ArrayList<Long>(collection.size());
		for (DynamicContent dynamicContent : collection) {
			assignedDynamicContentUids.add(dynamicContent.getUidPk());
		}
		return assignedDynamicContentUids;
	}

	/**
	 * Checks whether the given {@link DynamicContent} is used (assigned to a delivery context).
	 *
	 * @param dynamicContent the content to check
	 * @return true if it's assigned, false if not
	 * @throws EpServiceException in case of error
	 */
	public boolean isDynamicContentAssigned(final DynamicContent dynamicContent) throws EpServiceException {
		sanityCheck();
		if (dynamicContent == null) {
			throw new EpServiceException("Cannot check DynamicContent which is null.");
		}

		if (dynamicContent.isPersisted()) {
			final List<Long> results = getPersistenceEngine().retrieveByNamedQuery("DYNAMIC_CONTENT_IN_USE", dynamicContent.getUidPk());
			final boolean isInUse;
			if ((results == null) || (results.isEmpty())) {
				isInUse = false;
			} else {
				isInUse = true;
			}
			return isInUse;
		}

		return false;
	}

	/**
	 * Get the list dynamic content delivery by given selling context.
	 *
	 * @param sellingContextGuid given selling context guid.
	 * @return list dynamic content delivery by given selling context.
	 * @throws EpServiceException in case of error
	 */
	public List<DynamicContentDelivery> findBySellingContextGuid(final String sellingContextGuid) throws EpServiceException {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery(QUERY_FIND_DCD_BY_SELLING_CONTEXT_GUID, sellingContextGuid);
	}
}
