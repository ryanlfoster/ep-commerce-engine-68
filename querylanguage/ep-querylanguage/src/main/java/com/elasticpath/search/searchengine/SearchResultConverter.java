/**
 * 
 */
package com.elasticpath.search.searchengine;

import java.util.List;

/**
 * Native searcher may return some data which is subject for subsequent conversion to some other application format. 
 * Particular <code>SearchResultConverter</code> implementation which is option may provide such conversion.
 * 
 * @param <T> target object to convert to
 * @param <S> source object as returned by a searcher
 */
public interface SearchResultConverter<T, S> {

	/**
	 * Provides search results conversion.
	 * 
	 * @param searchResult data returned by a search engine 
	 * @return converted data
	 */
	List<T> convert(List<S> searchResult);
}
