/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.test.integration;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeType;
import com.elasticpath.domain.attribute.AttributeUsage;
import com.elasticpath.domain.attribute.impl.AttributeImpl;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.service.attribute.AttributeService;
import com.elasticpath.service.catalog.ProductService;
import com.elasticpath.service.customer.CustomerService;
import com.elasticpath.test.db.DbTestCase;
import com.elasticpath.test.util.Utils;

/**
 * Tests the attribute service methods.
 */
public class AttributeServiceImplTest extends DbTestCase {

	private static final String ATTR_KEY = "attr_key";

	private static final String ATTR_KEY2 = "attr_key2";

	@Autowired
	private AttributeService service;

	/**
	 * Tests if keyExists() works properly.
	 */
	@DirtiesDatabase
	@Test
	public void testAttributeExists() {
		Attribute attribute = newAttribute(ATTR_KEY, AttributeUsage.PRODUCT, scenario.getCatalog(), false);
		service.add(attribute);

		assertTrue(service.keyExists(ATTR_KEY));

		service.remove(attribute);

		assertFalse(service.keyExists(ATTR_KEY));
	}

	/**
	 * Tests whether isInUse() works as expected.
	 */
	@DirtiesDatabase
	@Test
	public void testIsAttributeInUseForProductAttributes() {
		Attribute attribute = newAttribute(ATTR_KEY, AttributeUsage.PRODUCT, scenario.getCatalog(), false);
		service.add(attribute);
		
		Attribute attribute2 = newAttribute(ATTR_KEY2, AttributeUsage.PRODUCT, scenario.getCatalog(), false);
		service.add(attribute2);
		
		final Product product = getTac().getPersistersFactory().getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());
		product.getAttributeValueGroup().setAttributeValue(attribute, Locale.US, "value");
		// product.getAttributeValueGroup().setAttributeValue(attribute2, Locale.US, "value2");
		product.setAttributeValueMap(product.getAttributeValueGroup().getAttributeValueMap());

		ProductService prodService = getBeanFactory().getBean(ContextIdNames.PRODUCT_SERVICE);
		prodService.saveOrUpdate(product);

		assertTrue(attribute.isPersisted());
		assertTrue(attribute2.isPersisted());

		assertTrue(service.isInUse(attribute.getUidPk()));
		assertFalse(service.isInUse(attribute2.getUidPk()));

	}

	/**
	 * Tests whether isInUse() works as expected.
	 */
	@DirtiesDatabase
	@Test
	public void testIsAttributeInUseForCustomerAttributes() {
		Attribute attribute = newAttribute(ATTR_KEY, AttributeUsage.CUSTOMERPROFILE, null, true);
		service.add(attribute);
		
		Attribute attribute2 = newAttribute(ATTR_KEY2, AttributeUsage.CUSTOMERPROFILE, null, true);
		service.add(attribute2);
		
		
		final Customer customer = persisterFactory.getStoreTestPersister().createDefaultCustomer(scenario.getStore());
		
		customer.getCustomerProfile().setProfileValue(attribute.getKey(), "value");
		customer.setProfileValueMap(customer.getCustomerProfile().getProfileValueMap());
		
		CustomerService customerService = getBeanFactory().getBean(ContextIdNames.CUSTOMER_SERVICE);
		customerService.update(customer);

		assertTrue(attribute.isPersisted());
		assertTrue(attribute2.isPersisted());

		assertTrue(service.isInUse(attribute.getUidPk()));
		assertFalse(service.isInUse(attribute2.getUidPk()));

	}

	/**
	 * Test scenario.:
	 * 
	 * 1. Create one global attribute of usage type CUSTOMER_PROFILE
	 * 2. Create a catalog attribute
	 * 3. Retrieve by catalog and usage type only CATALOG related attributes
	 * 4. Verify the result
	 */
	@DirtiesDatabase
	@Test
	public void testFindAllCatalogAndGlobalAttributes() {
		Catalog catalog = scenario.getCatalog();
		
		Attribute attribute1 = newAttribute("key1", AttributeUsage.CUSTOMERPROFILE, null, true);
		service.add(attribute1);

		Attribute attribute2 = newAttribute("key2", AttributeUsage.CATEGORY, catalog, false);
		service.add(attribute2);

		Collection<Attribute> result = service.findAllCatalogAndGlobalAttributesByType(catalog.getUidPk(), Arrays.asList(AttributeUsage.CATEGORY));

		assertTrue(result.contains(attribute2));
	}
	
	/**
	 */
	private Attribute newAttribute(final String key, final int usageType, final Catalog catalog, final boolean global) {
		final Attribute attr = new AttributeImpl();

		attr.setAttributeType(AttributeType.SHORT_TEXT);
		attr.setAttributeUsageId(usageType);
		attr.setCatalog(catalog);
		attr.setKey(key);
		attr.setGlobal(global);
		attr.setLocaleDependant(false);
		attr.setName(Utils.uniqueCode("Attr Name"));
		
		return attr;
	}

//	private List<Long> testQuery(final long uid) {
//		
//		PersistenceSession session = service.getPersistenceEngine().getSessionFactory().createPersistenceSession();
//		Query q = session.createQuery(
//				"SELECT sa.uidPk, ca.uidPk, pa.uidPk "
//				+ "FROM SkuAttributeValueImpl sav, CategoryAttributeValueImpl cav, ProductAttributeValueImpl pav "
//				+ " LEFT OUTER JOIN sav.attribute sa"
//				+ " LEFT OUTER JOIN cav.attribute ca"
//				+ " LEFT OUTER JOIN pav.attribute pa"
//				+ " WHERE (sa.uidPk = ?1) OR (ca.uidPk=?1) OR (pa.uidPk=?1)");
//
//		// q = session.createQuery("SELECT pav.uidPk FROM ProductAttributeValueImpl pav "
//		// + " LEFT JOIN pav.attribute pa"
//		// + " WHERE pa.uidPk=?1");
//
//		q.setParameter(1, uid);
//
//		return q.list();
//	}

}
