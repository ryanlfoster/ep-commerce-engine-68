/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.commons.pagination;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;

import com.elasticpath.domain.impl.AbstractEpDomainImpl;
import com.elasticpath.persistence.api.LoadTuner;

/**
 * Base paginator implementation. Requires a PaginatorLocator to be set to allow an interface to
 * the database or other source.
 *
 * @param <T> the class this paginator works with
 */
public class PaginatorImpl<T> extends AbstractEpDomainImpl implements Paginator<T> {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	private int pageSize;

	private PageImpl<T> currentPage;

	private DirectedSortingField[] sortingFields;

	private String objectId;

	private LoadTuner loadTuner;

	private PaginatorLocator<T> paginatorLocator;

	private long totalItems = 0;

	/**
	 * @return the objectId
	 */
	public String getObjectId() {
		return objectId;
	}

	/**
	 * @param paginationConfig the pagination config
	 */
	public void init(final PaginationConfig paginationConfig) {
		if (paginationConfig == null) {
			throw new IllegalArgumentException("Pagination config cannot be null.");
		}
		if (paginationConfig.getPageSize() < 1) {
			throw new IllegalArgumentException("Page size should be greater or equal to one.");
		}
		if (paginationConfig.getObjectId() == null) {
			throw new IllegalArgumentException("Object ID cannot be null.");
		}
		if (ArrayUtils.isEmpty(paginationConfig.getSortingFields())) {
			throw new IllegalArgumentException("At least one sorting field is required.");
		}

		this.pageSize = paginationConfig.getPageSize();
		this.sortingFields = paginationConfig.getSortingFields();
		this.objectId = paginationConfig.getObjectId();
		this.loadTuner = paginationConfig.getLoadTuner();
	}

	/**
	 *
	 * @return the loadTuner the load tuner
	 */
	public LoadTuner getLoadTuner() {
		return loadTuner;
	}

	/**
	 * Gets the page with the given page number and page size, ordered by the ordering field. If the page number is not available than the last
	 * available page will be returned (e.g. if only 2 pages are available and page number 5 is requested, page number 2 will be returned).
	 *
	 * @param page the page
	 * @return a new instance of the page
	 */
	protected Page<T> getPage(final PageImpl<T> page) {

		page.limitToLastPage(getTotalPages());

		List<T> items = Collections.emptyList();
		if (getTotalItems() > 0) {
			items = findItems(page);
		}
		if (items == null) {
			items = Collections.emptyList();
		}
		currentPage = createNewPage(page, items);

		this.totalItems = 0;

		return currentPage;
	}

	/**
	 * @return the total available pages for type T
	 */
	public int getTotalPages() {
		double pages = Double.valueOf(getTotalItems()) / Double.valueOf(getPageSize());
		return Math.max(1, (int) Math.ceil(pages));
	}

	/**
	 * Creates a new page with the given parameters.
	 *
	 * @param page the page
	 * @param items the total items
	 * @return a new page
	 */
	protected PageImpl<T> createNewPage(final PageImpl<T> page, final List<T> items) {
		return new PageImpl<T>(items, this, page);
	}

	/**
	 * @return the current page size
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * @return the first page
	 */
	public Page<T> first() {
		return getPage(new PageImpl<T>(this, 1, pageSize, sortingFields));
	}

	/**
	 * @return the last page
	 */
	public Page<T> last() {
		return getPage(new PageImpl<T>(this, getTotalPages(), pageSize, sortingFields));
	}

	@Override
	public Page<T> getPage(final int pageNumber) {
		int page = pageNumber;
		if (page < 1) {
			page = 1;
		}
		if (page > getTotalPages()) {
			page = getTotalPages();
		}
		return getPage(new PageImpl<T>(this, page, pageSize, sortingFields));
	}

	/**
	 * @return the next page
	 */
	public Page<T> next() {
		return getPage(((PageImpl<T>) getCurrentPage()).next());
	}

	/**
	 * @return the previous page
	 */
	public Page<T> previous() {
		return getPage(((PageImpl<T>) getCurrentPage()).previous());
	}

	/**
	 * @return the current page
	 */
	public Page<T> getCurrentPage() {
		if (currentPage == null) {
			first();
		}
		return currentPage;
	}

	/**
	 * Get the sorting fields.
	 *
	 * @return the sorting fields
	 */
	public DirectedSortingField[] getSortingFields() {
		return this.sortingFields.clone();
	}

	/**
	 * Sets the sorting fields to allow different sorting.
	 * @param sortingFields the new fields
	 */
	public void setSortingFields(final DirectedSortingField... sortingFields) {
		this.sortingFields = sortingFields;
	}

	/**
	 * Finds elements with the specified criteria.
	 *
	 * @param unpopulatedPage the page to be returned
	 * @return the elements found for the specified criteria. Must not return null.
	 */
	protected List<T> findItems(final Page<T> unpopulatedPage) {
		return paginatorLocator.findItems(unpopulatedPage, getObjectId());
	}

	/**
	 *
	 * @return the total items of type T
	 */
	public long getTotalItems() {
		if (this.totalItems == 0) {
			this.totalItems = paginatorLocator.getTotalItems(getObjectId());
		}
		return this.totalItems;
	}

	/**
	 * @param paginatorLocator the paginatorLocator to set.
	 */
	public void setPaginatorLocator(final PaginatorLocator<T> paginatorLocator) {
		this.paginatorLocator = paginatorLocator;
	}

	/**
	 * @return the paginator locator.
	 */
	public PaginatorLocator<T> getPaginatorLocator() {
		return paginatorLocator;
	}

	@Override
	public void refreshCurrentPage() {
		getPage((PageImpl<T>) getCurrentPage());
	}

	/**
	 * An abstract implementation of a page involving a pagination adapter.
	 *
	 * @param <T> the class to be used for this page
	 */
	@SuppressWarnings("PMD.ImmutableField")
	public static class PageImpl<T> implements Page<T>, Serializable {

		/**
		 * Serial version id.
		 */
		private static final long serialVersionUID = 5000000001L;

		private List<T> items = new ArrayList<T>();

		private final int pageSize;

		private final DirectedSortingField[] orderingFields;

		private int pageNumber;

		private final Paginator<T> paginator;

		/**
		 * @param items the items for this page
		 * @param paginator the
		 * @param page the page
		 */
		public PageImpl(final List<T> items, final Paginator<T> paginator, final Page<T> page) {
			this.items = items;
			this.paginator = paginator;
			this.pageNumber = page.getPageNumber();
			this.pageSize = page.getPageSize();
			this.orderingFields = page.getOrderingFields();
		}

		/**
		 * @param paginator the paginator
		 * @param pageNumber the page number
		 * @param pageSize the page size
		 * @param orderingFields the ordering fields
		 */
		public PageImpl(final Paginator<T> paginator, final int pageNumber, final int pageSize, final DirectedSortingField[] orderingFields) {
			this.paginator = paginator;
			this.pageNumber = pageNumber;
			this.pageSize = pageSize;
			this.orderingFields = (DirectedSortingField[]) ArrayUtils.clone(orderingFields);
		}

		/**
		 * @return the items belonging to this page
		 */
		public List<T> getItems() {
			return items;
		}

		/**
		 * @return the page ending index
		 */
		public int getPageEndIndex() {
			if (getPageStartIndex() > 0) {
				return getPageStartIndex() - 1 + Math.min(getPageSize(), getItems().size());
			}
			return 0;
		}

		/**
		 * @return the page size of this page
		 */
		public int getPageSize() {
			return pageSize;
		}

		/**
		 * @return the page number of this page
		 */
		public int getPageNumber() {
			return pageNumber;
		}

		/**
		 * @return the starting index of this page
		 */
		public int getPageStartIndex() {
			if (getPageNumber() == 1 && paginator.getTotalItems() == 0) {
				return 0;
			}
			if (getPageNumber() > 0) {
				return (getPageNumber() - 1) * getPageSize() + 1;
			}
			return 0;
		}

		/**
		 * @return the total number of items
		 */
		public long getTotalItems() {
			return paginator.getTotalItems();
		}

		/**
		 * @return the total number of pages available
		 */
		public int getTotalPages() {
			return paginator.getTotalPages();
		}

		/**
		 * @return the field the data is ordered by
		 */
		public DirectedSortingField[] getOrderingFields() {
			return (DirectedSortingField[]) ArrayUtils.clone(orderingFields);
		}

		/**
		 * @return the first of the available pages
		 */
		public Page<T> first() {
			return new PageImpl<T>(paginator, 1, getPageSize(), getOrderingFields());
		}

		/**
		 * @return the last of the available pages
		 */
		public Page<T> last() {
			return new PageImpl<T>(paginator, getTotalPages(), getPageSize(), getOrderingFields());
		}

		/**
		 * @return the next page
		 */
		public PageImpl<T> next() {
			return new PageImpl<T>(paginator, getPageNumber() + 1, getPageSize(), getOrderingFields());
		}

		/**
		 * @return the previous page
		 */
		public PageImpl<T> previous() {
			int pageNumber = getPageNumber() - 1;
			if (pageNumber < 1) {
				pageNumber = 1;
			}
			return new PageImpl<T>(paginator, pageNumber, getPageSize(), getOrderingFields());
		}

		/**
		 * @param lastPageNumber the last available page number
		 */
		public void limitToLastPage(final int lastPageNumber) {
			pageNumber = Math.min(pageNumber, lastPageNumber);
		}
	}

}
