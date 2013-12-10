package com.elasticpath.importexport.common.transformers;

import java.util.List;

import com.elasticpath.importexport.common.exception.ConfigurationException;

/**
 * This Interface provides method for transformer chain creation based on <code>TransformerConfiguration</code> objects.
 */
public interface TransformersChainFactory {

	/**
	 * Create the chain of appropriate transformers based on configuration properties.
	 * 
	 * @param transformerConfigurationList the list with the names of transformers to be created
	 * @throws ConfigurationException if there is any configuration problems.
	 * @return the list with transformers chain
	 */
	List<Transformer> createTransformersChain(final List<TransformerConfiguration> transformerConfigurationList) throws ConfigurationException;

}