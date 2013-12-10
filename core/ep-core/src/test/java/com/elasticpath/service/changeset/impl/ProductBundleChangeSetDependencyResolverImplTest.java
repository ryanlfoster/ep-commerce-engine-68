package com.elasticpath.service.changeset.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.domain.catalog.BundleConstituent;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.impl.BundleConstituentImpl;
import com.elasticpath.domain.catalog.impl.ProductBundleImpl;
import com.elasticpath.domain.catalog.impl.ProductConstituentImpl;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * The unit test class for product bundle change set dependency resolver.
 */
public class ProductBundleChangeSetDependencyResolverImplTest {

	private final ProductBundleChangeSetDependencyResolverImpl resolver = new ProductBundleChangeSetDependencyResolverImpl();

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private BeanFactory beanFactory;
	private BeanFactoryExpectationsFactory expectationsFactory;

	/**
	 * Set up required before each test.
	 */
	@Before
	public void setUp() {
	    beanFactory = context.mock(BeanFactory.class);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
		expectationsFactory.allowingBeanFactoryGetBean("productConstituent", ProductConstituentImpl.class);
	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	/**
	 * Test get change set dependency for product bundle.
	 */
	@Test
	public void testGetChangeSetDependency() {
		Object obj  = new Object();
		Set< ? > dependencies = resolver.getChangeSetDependency(obj);
		assertTrue("Non-ProductBundle object should not be processed", dependencies.isEmpty());

		ProductBundle productBundle = new ProductBundleImpl();
		BundleConstituent constituent = new BundleConstituentImpl();
		Product product = new ProductImpl();
		constituent.setConstituent(product);
		productBundle.addConstituent(constituent);
		dependencies = resolver.getChangeSetDependency(productBundle);
		assertEquals("the product is not found in the dependency list of product bundle", product, dependencies.iterator().next());
	}

}
