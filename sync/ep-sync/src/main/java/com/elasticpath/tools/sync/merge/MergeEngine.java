package com.elasticpath.tools.sync.merge;

import java.util.Comparator;
import java.util.Map;

import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.tools.sync.exception.SyncToolConfigurationException;
import com.elasticpath.tools.sync.merge.configuration.EntityFilter;
import com.elasticpath.tools.sync.merge.configuration.EntityLocator;
import com.elasticpath.tools.sync.merge.configuration.GuidLocator;
import com.elasticpath.tools.sync.merge.configuration.MergeBoundarySpecification;
import com.elasticpath.tools.sync.utils.SyncUtils;

/**
 * Merge Engine provides merge of two Ep entities.
 */
public interface MergeEngine {

	/**
	 * Merge source on target.
	 * 
	 * @param source source object
	 * @param target target object
	 * @throws SyncToolConfigurationException {@link SyncToolConfigurationException}
	 */
	void processMerge(final Persistable source, final Persistable target) throws SyncToolConfigurationException;

	/**
	 * @param mergeBoundarySpec the mergeBoundarySpec to set
	 */
	void setMergeBoundarySpecification(final MergeBoundarySpecification mergeBoundarySpec);

	/**
	 * @param entityLocator the entityLocator to set
	 */
	void setEntityLocator(final EntityLocator entityLocator);

	/**
	 * @param guidLocator the guidLocator to set
	 */
	void setGuidLocator(final GuidLocator guidLocator);

	/**
	 * @param beanCreator the beanCreator to set
	 */
	void setBeanCreator(final BeanCreator beanCreator);

	/**
	 * @param jpaPersistentStateLocator the jpaPersistentStateLocator to set
	 */
	void setJpaPersistentStateLocator(final PersistentStateLocator jpaPersistentStateLocator);

	/**
	 * @param syncUtils the syncUtils to set
	 */
	void setSyncUtils(final SyncUtils syncUtils);

	/**
	 * @param guidComparator the guidComparator to set
	 */
	void setGuidComparator(final Comparator<Persistable> guidComparator);

	/**
	 * @param mergeFilters merge filters mapping from String rep of class name to filter
	 */
	void setMergeFilters(final Map<String, EntityFilter> mergeFilters);
}