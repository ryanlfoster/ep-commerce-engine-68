package com.elasticpath.ql.custom.setting;

import java.util.ArrayList;
import java.util.List;

import com.elasticpath.search.searchengine.SearchResultConverter;

/**
 * SQL searcher will find a list of arrays of objects, first element of the array is a setting path, second if context value.
 */
public class SettingSearchResultConverter implements SearchResultConverter <SettingResult, Object[]> {

	@Override
	public List<SettingResult> convert(final List<Object[]> searchResult) {
		List<SettingResult> results = new ArrayList<SettingResult>(searchResult.size());
		for (Object [] pathCtx : searchResult) {
			results.add(new SettingResult((String) pathCtx[0], (String) pathCtx[1]));
		}
		return results;
	}
}
