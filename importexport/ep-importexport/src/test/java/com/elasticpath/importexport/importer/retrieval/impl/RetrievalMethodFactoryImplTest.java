package com.elasticpath.importexport.importer.retrieval.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.common.types.TransportType;
import com.elasticpath.importexport.importer.configuration.RetrievalConfiguration;
import com.elasticpath.importexport.importer.retrieval.RetrievalMethod;

/**
 * RetrievalMethodFactoryImplTest. 
 */
public class RetrievalMethodFactoryImplTest {

	private final RetrievalMethodFactoryImpl retrievalMethodFactory = new RetrievalMethodFactoryImpl();
	
	/**
	 * Dummy Retrieval Method.
	 */
	private static class DummyRetrievalMethod extends AbstractRetrievalMethodImpl {
		public File retrieve() {
			return null;
		}
	}
	
	/**
	 * testCreateRetrievalMethod.
	 * 
	 * @throws ConfigurationException if factory can't create.
	 */
	@Test
	public void testCreateRetrievalMethod() throws ConfigurationException {
		Map<TransportType, AbstractRetrievalMethodImpl> retrievalMethods = new HashMap<TransportType, AbstractRetrievalMethodImpl>();
		retrievalMethods.put(TransportType.FILE, new DummyRetrievalMethod());
		retrievalMethodFactory.setRetrievalMethods(retrievalMethods);
		assertEquals(retrievalMethods, retrievalMethodFactory.getRetrievalMethods());
		
		RetrievalConfiguration retrievalConfiguration = new RetrievalConfiguration();
		retrievalConfiguration.setMethod(TransportType.FILE);
		retrievalConfiguration.setSource("source");
		
		RetrievalMethod retrievalMethod = retrievalMethodFactory.createRetrievalMethod(retrievalConfiguration);
		
		assertNotNull(retrievalMethod);
		assertTrue(retrievalMethod instanceof DummyRetrievalMethod);
	}
	
	/**
	 * Tests Exception Throwing.
	 */
	@Test(expected = ConfigurationException.class)
	public void testCreateRetrievalMethodThrowsExeption() throws Exception {
		retrievalMethodFactory.setRetrievalMethods(new HashMap<TransportType, AbstractRetrievalMethodImpl>());
		
		RetrievalConfiguration retrievalConfiguration = new RetrievalConfiguration();
		retrievalConfiguration.setMethod(TransportType.FILE);
		retrievalConfiguration.setSource("source");
		
		
		retrievalMethodFactory.createRetrievalMethod(retrievalConfiguration);
	}
}
