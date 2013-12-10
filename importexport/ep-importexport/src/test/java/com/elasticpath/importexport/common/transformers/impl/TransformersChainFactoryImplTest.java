package com.elasticpath.importexport.common.transformers.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.common.transformers.Transformer;
import com.elasticpath.importexport.common.transformers.TransformerConfiguration;
import com.elasticpath.importexport.common.transformers.TransformersChainFactory;

/**
 * Test for TransformersChainFactoryImpl.
 */
public class TransformersChainFactoryImplTest {

	/**
	 * The DummyTransformer.
	 */
	public static class DummyTransformer extends AbstractTransformerImpl {
		@Override
		protected void processTransformation(final InputStream input, final OutputStream output) {
			// empty, Do nothing
		}
	}

	/**
	 * IllegalAccessExceptionTransformer.
	 */	
	public static final class IllegalAccessExceptionTransformer extends DummyTransformer {
		private IllegalAccessExceptionTransformer() {
			// empty, No Default Constructor.
		}
	}

	/**
	 * InstantiationExceptionTransformer.
	 */
	public abstract static class AbstractInstantiationExceptionTransformer implements Transformer {
		// Empty Abstract Class
	}

	/*
	 * Creates TransformerConfigurationList With only one item in it. @param clazz - the class for TransformerConfiguration
	 */
	private List<TransformerConfiguration> createTransformerConfigurationList(final String className) {
		List<TransformerConfiguration> transformerConfigurationList = new ArrayList<TransformerConfiguration>();
		TransformerConfiguration transformerConfiguration = new TransformerConfiguration();
		transformerConfiguration.setClassName(className);
		transformerConfigurationList.add(transformerConfiguration);
		return transformerConfigurationList;
	}

	/**
	 * Verify that transformers chain factory creates the chain of transformers properly.
	 * 
	 * @throws ConfigurationException
	 */
	@Test
	public void testCreateTransformersChain() {
		TransformersChainFactory transformersChainFactory = new TransformersChainFactoryImpl();

		List<Transformer> transformerList = null;
		try {
			transformerList = transformersChainFactory.createTransformersChain(
					createTransformerConfigurationList(DummyTransformer.class.getName()));
			
		} catch (ConfigurationException e) {
			fail("ConfigurationException must not be thrown");
		}

		assertEquals(1, transformerList.size());

		Transformer transformer = transformerList.get(0);
		assertTrue(transformer instanceof DummyTransformer);

		transformer.transform(null); // Dummy Transformer can do nothing with nothing.
	}

	/**
	 * Tests CreateTransformersChain and Throws ConfigurationException Cause ClassNotFoundException.
	 */
	@Test
	public void testCreateTransformersChainThrowsConfigurationExceptionCauseClassNotFoundException() {
		TransformersChainFactory transformersChainFactory = new TransformersChainFactoryImpl();
		
		try {
			transformersChainFactory.createTransformersChain(
					createTransformerConfigurationList("MyDummyUnknownClass"));
			
			fail("ConfigurationException must be thrown cause ClassNotFoundException");
		} catch (ConfigurationException e) {
			assertNotNull(e);
			assertTrue(e.getCause() instanceof ClassNotFoundException);
		}
	}

	/**
	 * Tests Create TransformersChain and Throws Exceptions Cause IllegalAccessException.
	 */
	@Test
	public void testCreateTransformersChainThrowsExceptionsCauseIllegalAccessException() {
		TransformersChainFactory transformersChainFactory = new TransformersChainFactoryImpl();

		try {
			transformersChainFactory.createTransformersChain(
					createTransformerConfigurationList(IllegalAccessExceptionTransformer.class.getName()));
			
			fail("ConfigurationException must be thrown cause of IllegalAccessException");
		} catch (ConfigurationException e) {
			assertNotNull(e);
			assertTrue(e.getCause() instanceof IllegalAccessException);
		}
	}
	
	/**
	 * Tests CreateTransformersChain and Throws Exceptions Cause InstantiationException.
	 */
	@Test
	public void testCreateTransformersChainThrowsExceptionsCauseInstantiationException() {
		TransformersChainFactory transformersChainFactory = new TransformersChainFactoryImpl();
		
		try {
			transformersChainFactory.createTransformersChain(
					createTransformerConfigurationList(AbstractInstantiationExceptionTransformer.class.getName()));
			
			fail("ConfigurationException must be thrown cause of InstantiationException");
		} catch (ConfigurationException e) {
			assertNotNull(e);
			assertTrue(e.getCause() instanceof InstantiationException);
		}
	}
}
