package com.elasticpath.tags.service;

import java.util.List;

import com.elasticpath.tags.domain.TagOperator;

/**
 * Service interface for TagOperator domain object.
 */
public interface TagOperatorService extends GenericService<TagOperator> {

	/**
	 * get all tag definitions.
	 * @return a list of tag operator.
	 */
	List<TagOperator> getTagOperators();

}
