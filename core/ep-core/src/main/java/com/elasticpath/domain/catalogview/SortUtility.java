package com.elasticpath.domain.catalogview;

import com.elasticpath.commons.exception.EpBindException;
import com.elasticpath.service.search.query.SortBy;
import com.elasticpath.service.search.query.SortOrder;
import com.elasticpath.service.search.query.StandardSortBy;

/**
 * Provides utility methods for sorting in the SF.
 */
public final class SortUtility {
	
	/**
	 * The delimiter used to delimit the sorting type and sorting order.
	 */
	private static final char SORTING_TYPE_ORDER_DELIMITER = '-';
	
	private SortUtility() { }
	
	/**
	 * Constructs a sorting string identifier from the sorting type and order.
	 *
	 * @param sortType the sort type
	 * @param sortOrder the sort order
	 * @return a sorting identifier string
	 */
	public static String constructSortTypeOrderString(final SortBy sortType, final SortOrder sortOrder) {
		return sortType.getSortString() + SORTING_TYPE_ORDER_DELIMITER + sortOrder.getSortString();
	}
	
	/**
	 * Extracts the {@link SortBy} from the sorter identifier string.
	 *
	 * @param sorterIdStr the identifier string
	 * @return a {@link SortBy} from the identifier string
	 * @throws EpBindException if the sort order cannot be determined
	 */
	public static SortBy extractSortType(final String sorterIdStr) throws EpBindException {
		final String sortBy = sorterIdStr.substring(0, sorterIdStr.lastIndexOf(SORTING_TYPE_ORDER_DELIMITER));
		final SortBy sortByType = StandardSortBy.valueOfUsingSortName(sortBy);
		
		if (sortByType == null) {
			throw new EpBindException(String.format("Unable to convert <%1$s> into a sort type, obtained from <%2$s>", sortBy,
					sorterIdStr));
		}
		return sortByType;
	}
	
	/**
	 * Extracts the {@link SortOrder} from the sorter identifier string.
	 *
	 * @param sorterIdStr the identifier string
	 * @return a {@link SortOrder} from the identifier string
	 * @throws EpBindException if the sort order cannot be determined
	 */
	public static SortOrder extractSortOrder(final String sorterIdStr) throws EpBindException {
		final String sortOrder = sorterIdStr.substring(sorterIdStr.lastIndexOf(SORTING_TYPE_ORDER_DELIMITER) + 1);
		final SortOrder sortOrderType = SortOrder.parseSortString(sortOrder);
		
		if (sortOrderType == null) {
			throw new EpBindException(String.format("Unable to convert <%1$s> into a sort order, obtained from <%2$s>",
					sortOrder, sorterIdStr));
		}
		return sortOrderType;
	}
}
