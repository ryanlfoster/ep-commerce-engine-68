package com.elasticpath.importexport.common.transformers.impl;

import java.util.ArrayList;
import java.util.List;

import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.common.transformers.Transformer;
import com.elasticpath.importexport.common.transformers.TransformerConfiguration;
import com.elasticpath.importexport.common.transformers.TransformersChainFactory;

/**
 * This class is implementation of <code>TransformersChainFactory</code> interface.
 * <p>
 * It creates chain of transformers based on <code>List</code> of <code>TransformerConfiguration</code> objects
 */
public class TransformersChainFactoryImpl implements TransformersChainFactory {

	@Override
	public List<Transformer> createTransformersChain(final List<TransformerConfiguration> transformerConfigurationList)
			throws ConfigurationException {

		List<Transformer> chainedTransformers = new ArrayList<Transformer>();

		for (TransformerConfiguration configuration : transformerConfigurationList) {
			try {
				Class< ? > clazz = Class.forName(configuration.getClassName());
				chainedTransformers.add((Transformer) clazz.newInstance());
				
			} catch (ClassNotFoundException e) {
				throw new ConfigurationException(e);
			} catch (InstantiationException e) {
				throw new ConfigurationException(e);
			} catch (IllegalAccessException e) {
				throw new ConfigurationException(e);
			}
		}
		return chainedTransformers;
	}
}
