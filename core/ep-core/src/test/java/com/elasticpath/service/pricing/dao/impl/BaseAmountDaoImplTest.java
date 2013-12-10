package com.elasticpath.service.pricing.dao.impl;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.common.pricing.service.BaseAmountFilter;
import com.elasticpath.common.pricing.service.impl.BaseAmountFilterImpl;
import com.elasticpath.domain.pricing.BaseAmount;
import com.elasticpath.persistence.api.EpPersistenceException;
import com.elasticpath.persistence.api.PersistenceEngine;

/**
 * Test for BaseAmountDaoImpl.
 */
public class BaseAmountDaoImplTest {
	
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	
	/**
	 * Test the JPQL query builder.
	 * @throws Exception on error
	 */
	@Test
	public void testQueryBuilder() throws Exception {
		final BigDecimal quantityDbl = new BigDecimal("2.43");
		final BigDecimal list = new BigDecimal("9.9");
		final BigDecimal sale = new BigDecimal("3.14");
		BaseAmountFilter filter = new BaseAmountFilterImpl();
		filter.setObjectGuid("GUID");
		BaseAmountDaoImpl.BaseAmountJPQLBuilder builder = new BaseAmountDaoImpl().new BaseAmountJPQLBuilder(filter);
		assertEquals("SELECT baseAmount FROM BaseAmountImpl AS baseAmount WHERE baseAmount.objectGuid = 'GUID'", builder.toString());
		
		BaseAmountFilter filter2 = new BaseAmountFilterImpl();
		filter2.setObjectGuid("GUID2");
		filter2.setObjectType("PROD");
		filter2.setListValue(list);
		filter2.setSaleValue(sale);
		filter2.setPriceListDescriptorGuid("PRICELISTGUID");
		filter2.setQuantity(quantityDbl);
		
		BaseAmountDaoImpl.BaseAmountJPQLBuilder builder2 = new BaseAmountDaoImpl().new BaseAmountJPQLBuilder(filter2);
		assertEquals("SELECT baseAmount FROM BaseAmountImpl AS baseAmount "
				+ "WHERE baseAmount.objectGuid = 'GUID2' "
				+ "AND baseAmount.objectType = 'PROD' "
				+ "AND baseAmount.priceListDescriptorGuid = 'PRICELISTGUID' "
				+ "AND baseAmount.listValueInternal = 9.9 "
				+ "AND baseAmount.saleValueInternal = 3.14 "
				+ "AND baseAmount.quantityInternal = 2.43", builder2.toString());
	}
	
	/**
	 * Test that guid/uidpk checking is done before updating a BaseAmount.
	 */
	@Test
	public void testUpdateGuidChecking() {
		final BaseAmount baseAmount = context.mock(BaseAmount.class);
		final PersistenceEngine engine = context.mock(PersistenceEngine.class);
		BaseAmountDaoImpl dao = new BaseAmountDaoImpl() {
			@Override
			public PersistenceEngine getPersistenceEngine() {
				return engine;
			}
		};
		context.checking(new Expectations() { {
			oneOf(baseAmount).isPersisted(); will(returnValue(true));
			oneOf(baseAmount).getGuid(); will(returnValue("GUID"));
			oneOf(engine).saveOrUpdate(baseAmount); will(returnValue(baseAmount));
		} });
		dao.update(baseAmount);
	}
	
	
	/**
	 * Test that if BaseAmount doesn't have a GUID, exception is thrown.
	 * Service won't be able to update a non existing guid.
	 */
	@Test(expected = EpPersistenceException.class)
	public void testUpdateExceptionNoGuid() {
		final BaseAmount baseAmount = context.mock(BaseAmount.class);
		BaseAmountDaoImpl dao = new BaseAmountDaoImpl() {
			@Override
			public BaseAmount findBaseAmountByGuid(final String guid) {
				return null;
			}
		};
		context.checking(new Expectations() { {
			oneOf(baseAmount).isPersisted(); will(returnValue(true));
			oneOf(baseAmount).getGuid(); will(returnValue(null));
		} });
		dao.update(baseAmount);
	}
	
	/**
	 * Test that if BaseAmount doesn't have a UIDPK, exception is thrown.
	 * Service won't be able to update a non persisted object.
	 */
	@Test(expected = EpPersistenceException.class)
	public void testUpdateExceptionNoUid() {
		final BaseAmount baseAmount = context.mock(BaseAmount.class);
		BaseAmountDaoImpl dao = new BaseAmountDaoImpl() {
			@Override
			public BaseAmount findBaseAmountByGuid(final String guid) {
				return null;
			}
		};
		context.checking(new Expectations() { {
			oneOf(baseAmount).isPersisted(); will(returnValue(false));
		} });
		dao.update(baseAmount);
	}
}
