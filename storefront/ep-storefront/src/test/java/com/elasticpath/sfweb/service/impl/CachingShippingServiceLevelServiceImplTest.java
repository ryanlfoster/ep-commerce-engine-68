package com.elasticpath.sfweb.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.shipping.ShippingServiceLevel;
import com.elasticpath.domain.shipping.impl.ShippingServiceLevelImpl;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.sfweb.service.EntityCache;
import com.elasticpath.sfweb.test.BeanFactoryExpectationsFactory;

public class CachingShippingServiceLevelServiceImplTest {

	public static final String UIDPK = "uidPk";
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private CachingShippingServiceLevelServiceImpl service;
	private final PersistenceEngine persistenceEngine = context.mock(PersistenceEngine.class);
	@SuppressWarnings("unchecked")
	private final EntityCache<ShippingServiceLevel> entityCache = context.mock(EntityCache.class);
	private ShippingServiceLevelImpl ssl;
	private ShippingServiceLevelImpl ssl2;

	@Before
	public void setUp() throws Exception {
		BeanFactory beanFactory = context.mock(BeanFactory.class);
		BeanFactoryExpectationsFactory bfef = new BeanFactoryExpectationsFactory(context, beanFactory);
		bfef.allowingBeanFactoryGetBean(ContextIdNames.SHIPPING_SERVICE_LEVEL, ShippingServiceLevelImpl.class);

		ssl = new ShippingServiceLevelImpl();
		ssl.setUidPk(1L);
		ssl.setCode("ssl-code");
		ssl.setGuid("ssl-guid");

		ssl2 = new ShippingServiceLevelImpl();
		ssl2.setUidPk(2L);
		ssl2.setCode("ssl-code-2");
		ssl2.setGuid("ssl-guid-2");

		service = new CachingShippingServiceLevelServiceImpl();
		service.setPersistenceEngine(persistenceEngine);
		service.setEntityCache(entityCache);
	}

	@Test
	public void testGetByUidPkCacheMissesAddToCache() {
		// Expectations
		context.checking(new Expectations() { {
			allowing(entityCache).get(UIDPK, ssl.getUidPk()); will(returnValue(null));
			oneOf(persistenceEngine).get(ShippingServiceLevelImpl.class, ssl.getUidPk()); will(returnValue(ssl));
			oneOf(entityCache).put(ssl); will(returnValue(ssl));
		} });

		// When
		ShippingServiceLevel found = service.get(ssl.getUidPk());

		// Then
		assertSame("Should return value from persistence", ssl, found);  // NOPMD
	}

	@Test
	public void testGetByUidPkCacheAndPersistenceMissesReturnNull() {
		// Expectations
		context.checking(new Expectations() { {
			allowing(entityCache).get(UIDPK, ssl.getUidPk()); will(returnValue(null));
			oneOf(persistenceEngine).get(ShippingServiceLevelImpl.class, ssl.getUidPk()); will(returnValue(null));
		} });

		// When
		ShippingServiceLevel found = service.get(ssl.getUidPk());

		// Then
		assertNull("Nothing should have been found", found);
	}

	@Test
	public void testGetByUidPkCacheHit() {
		// Expectations
		context.checking(new Expectations() { {
			allowing(entityCache).get(UIDPK, ssl.getUidPk()); will(returnValue(ssl));
		} });

		// When
		ShippingServiceLevel found = service.get(ssl.getUidPk());

		// Then
		assertSame("Should return value from persistence", ssl, found);  // NOPMD
	}

	@Test
	public void testFindByCodeCacheMissesAddToCache() {
		// Expectations
		context.checking(new Expectations() { {
			allowing(entityCache).get("code", ssl.getCode()); will(returnValue(null));
			oneOf(persistenceEngine).retrieveByNamedQuery(with(any(String.class)), with(any(Object[].class)));
				will(returnValue(Collections.singletonList(ssl)));
			oneOf(entityCache).put(ssl); will(returnValue(ssl));
		} });

		// When
		ShippingServiceLevel found = service.findByCode(ssl.getCode());

		// Then
		assertSame("Should return value from persistence", ssl, found);  // NOPMD
	}

	@Test
	public void testFindByCodeAndPersistenceMissesReturnNull() {
		// Expectations
		context.checking(new Expectations() { {
			allowing(entityCache).get("code", ssl.getCode()); will(returnValue(null));
			oneOf(persistenceEngine).retrieveByNamedQuery(with(any(String.class)), with(any(Object[].class)));
				will(returnValue(Collections.emptyList()));
		} });

		// When
		ShippingServiceLevel found = service.findByCode(ssl.getCode());

		// Then
		assertNull("Nothing should have been found", found);
	}

	@Test
	public void testFindByCodeCacheHit() {
		// Expectations
		context.checking(new Expectations() { {
			allowing(entityCache).get("code", ssl.getCode()); will(returnValue(ssl));
		} });

		// When
		ShippingServiceLevel found = service.findByCode(ssl.getCode());

		// Then
		assertSame("Should return value from persistence", ssl, found);  // NOPMD
	}

	@Test
	public void testFindByGuidCacheMissesAddToCache() {
		// Expectations
		context.checking(new Expectations() { {
			allowing(entityCache).get("guid", ssl.getGuid()); will(returnValue(null));
			oneOf(persistenceEngine).retrieveByNamedQuery(with(any(String.class)), with(any(Object[].class)));
				will(returnValue(Collections.singletonList(ssl)));
			oneOf(entityCache).put(ssl); will(returnValue(ssl));
		} });

		// When
		ShippingServiceLevel found = service.findByGuid(ssl.getGuid());

		// Then
		assertSame("Should return value from persistence", ssl, found);  // NOPMD
	}

	@Test
	public void testFindByGuidAndPersistenceMissesReturnNull() {
		// Expectations
		context.checking(new Expectations() { {
			allowing(entityCache).get("guid", ssl.getGuid()); will(returnValue(null));
			oneOf(persistenceEngine).retrieveByNamedQuery(with(any(String.class)), with(any(Object[].class)));
				will(returnValue(Collections.emptyList()));
		} });

		// When
		ShippingServiceLevel found = service.findByGuid(ssl.getGuid());

		// Then
		assertNull("Nothing should have been found", found);
	}

	@Test
	public void testFindByGuidCacheHit() {
		// Expectations
		context.checking(new Expectations() { {
			allowing(entityCache).get("guid", ssl.getGuid()); will(returnValue(ssl));
		} });

		// When
		ShippingServiceLevel found = service.findByGuid(ssl.getGuid());

		// Then
		assertSame("Should return value from persistence", ssl, found);  // NOPMD
	}

	@Test
	public void testFindByStoreAndState() {
		// Expectations
		context.checking(new Expectations() { {
			oneOf(persistenceEngine).retrieveByNamedQuery(with(any(String.class)), with(any(Object[].class)));
				will(returnValue(Arrays.asList(ssl.getUidPk(), ssl2.getUidPk())));

			oneOf(entityCache).get("uidPk", ssl.getUidPk()); will(returnValue(ssl));
			oneOf(entityCache).get("uidPk", ssl2.getUidPk()); will(returnValue(ssl2));
		} });

		// When
		List<ShippingServiceLevel> found = service.findByStoreAndState("store", true);

		// Then
		assertEquals("Should return shipping service levels from cache using the ids retrieved from the query",
				Arrays.asList(ssl, ssl2), found);
	}
}
