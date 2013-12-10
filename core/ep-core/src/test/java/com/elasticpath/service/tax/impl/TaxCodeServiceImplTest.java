/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.tax.impl;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.domain.tax.impl.TaxCodeImpl;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.service.tax.TaxCodeService;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/** Test cases for {@link TaxCodeServiceImpl}. */
public class TaxCodeServiceImplTest {
 	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private TaxCodeService taxCodeService;
 	private PersistenceEngine persistenceEngine;
 	private BeanFactory beanFactory;
	private BeanFactoryExpectationsFactory expectationsFactory;
 	private TaxCode taxCode;

	/** Default taxCategory uid. */
	private static final long TAX_CATEGORY_UID = 1000;
	
	/**
	 * Prepares for tests.
	 * 
	 * @throws Exception -- in case of any errors.
	 */
 	@Before
 	public void setUp() {
 		beanFactory = context.mock(BeanFactory.class);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
 		persistenceEngine = context.mock(PersistenceEngine.class);
 		taxCode = context.mock(TaxCode.class);
 
 		context.checking(new Expectations() {
 			{
 				allowing(beanFactory).getBean(ContextIdNames.TAX_CODE);
 				will(returnValue(taxCode));
 				allowing(beanFactory).getBeanImplClass(ContextIdNames.TAX_CODE);
 				will(returnValue(TaxCodeImpl.class));
 			}
 		});
	  		
 		taxCodeService = new TaxCodeServiceImpl();
 		taxCodeService.setPersistenceEngine(persistenceEngine);
  	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	/**
	 * Test behaviour when the persistence engine is not set.
	 */
 	@Test(expected = EpServiceException.class)
 	public void testPersistenceEngineIsNull() {
		taxCodeService.setPersistenceEngine(null);
		taxCodeService.add(new TaxCodeImpl());
	}

	/**
	 * Test method for 'com.elasticpath.service.TaxCodeServiceImpl.getPersistenceEngine()'.
	 */
 	@Test
	public void testGetPersistenceEngine() {
		assertNotNull(taxCodeService.getPersistenceEngine());
	}

	/**
	 * Test method for 'com.elasticpath.service.TaxCodeServiceImpl.add(TaxCode)'.
	 */
	@Test
	public void testAdd() {
		final TaxCode taxCode = new TaxCodeImpl();
		taxCode.setGuid("asd;fj ");
		final List<TaxCode> noDuplicates = new ArrayList<TaxCode>();

		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).save(taxCode);
				oneOf(persistenceEngine).retrieveByNamedQuery(with(any(String.class)), with(any(Object[].class)));
				will(returnValue(noDuplicates));
			}
		});

		taxCodeService.add(taxCode);
	}

	/**
	 * Test method for 'com.elasticpath.service.TaxCodeServiceImpl.update(TaxCode)'.
	 */
	@Test
	public void testUpdate() {
		final String taxguid = "sfsga0b9a 124";
		final TaxCode taxCode = new TaxCodeImpl();
		taxCode.setGuid(taxguid);
		final TaxCode updatedTaxCode = new TaxCodeImpl();
		updatedTaxCode.setGuid(taxguid);
		final String code = "updatedTaxCode";
		final long uidPk = 123456;
		taxCode.setCode(code);
		taxCode.setUidPk(uidPk);

		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).update(taxCode);
				will(returnValue(updatedTaxCode));
			}
		});

		final TaxCode returnedTaxCode = taxCodeService.update(taxCode);
		assertSame(returnedTaxCode, updatedTaxCode);
	}

	/**
	 * Test method for 'com.elasticpath.service.TaxCodeServiceImpl.delete(TaxCode)'.
	 */
	@Test
	public void testDelete() {
		final TaxCode taxCode = new TaxCodeImpl();
		taxCode.setGuid("bba");

		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).delete(taxCode);
			}
		});

		taxCodeService.remove(taxCode);
	}

	/**
	 * Test method for 'com.elasticpath.service.TaxCodeServiceImpl.load(Long)'.
	 */
	@Test
	public void testLoad() {
		final TaxCode taxCode = new TaxCodeImpl();

		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).load(TaxCodeImpl.class, TAX_CATEGORY_UID);
				will(returnValue(taxCode));
			}
		});

		final TaxCode loadedTaxCode = taxCodeService.load(TAX_CATEGORY_UID);
		assertSame(taxCode, loadedTaxCode);
	}

	/**
	 * Test method for 'com.elasticpath.service.TaxCodeServiceImpl.load(Long)'.
	 */
	@Test
	public void testLoadANonExistTaxCode() {
		final long uid = 1234L;
		final TaxCode taxCode = new TaxCodeImpl();

		context.checking(new Expectations() {
			{
				allowing(persistenceEngine).load(TaxCodeImpl.class, uid);
				will(returnValue(taxCode));
			}
		});

		assertSame(taxCode, taxCodeService.load(uid));
	}

	/**
	 * Test method for 'com.elasticpath.service.TaxCodeServiceImpl.getTaxCodesInUse()'.
	 */
	@Test
	public void testGetTaxCodesInUse() {
		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieveByNamedQuery(with(containsString("TAX_CODES_WITH_PRODUCT_TYPE")),
						with(any(Object[].class)));
				will(returnValue(Collections.emptyList()));
				oneOf(persistenceEngine).retrieveByNamedQuery(with(containsString("TAX_CODES_WITH_VALUE")),
						with(any(Object[].class)));
				will(returnValue(Collections.emptyList()));
				oneOf(persistenceEngine).retrieveByNamedQuery(with(containsString("TAX_CODES_WITH_STORE")),
						with(any(Object[].class)));
				will(returnValue(Collections.emptyList()));
				oneOf(persistenceEngine).retrieveByNamedQuery(with(containsString("TAX_CODES_WITH_PRODUCT")),
						with(any(Object[].class)));
				will(returnValue(Collections.emptyList()));
			}
		});
		taxCodeService.getTaxCodesInUse();
	}

	/**
	 * Test method for 'com.elasticpath.service.TaxCodeServiceImpl.load(Long)'.
	 */
	@Test
	public void testLoadANewTaxCode() {
		assertNotNull(taxCodeService.load(0L));
	}

}
