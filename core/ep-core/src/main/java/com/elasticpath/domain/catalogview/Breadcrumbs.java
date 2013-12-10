package com.elasticpath.domain.catalogview;

import java.util.List;

import com.elasticpath.domain.catalogview.CatalogViewRequest.Breadcrumb;

/**
 * A list of navigational breadcrumbs to a specific catalog page, the result of 
 * viewing the catalog using a set of filters.
 * 
 * Each crumb provides:
 * <ul>
 *   <li>it's display name</li>
 *   <li>a url to itself (to cut out any crumbs that follows it</li>
 *   <li>a url for the full the full set of filters without that filter.</li>
 * </ul>
 */
public interface Breadcrumbs {

	
	/**
	 * Return all the breadcrumb objects in the order they should be displayed.
	 * 
	 * @return the breadcrumbs in the order they should be displayed.
	 */
	List<Breadcrumb> asList();
}