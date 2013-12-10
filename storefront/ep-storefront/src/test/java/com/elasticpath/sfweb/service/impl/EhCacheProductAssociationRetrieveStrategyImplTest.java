package com.elasticpath.sfweb.service.impl;

import java.util.HashSet;
import java.util.Set;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.catalog.ProductAssociation;
import com.elasticpath.service.catalog.ProductAssociationService;
import com.elasticpath.service.search.query.ProductAssociationSearchCriteria;

/**
 * The junit test class for EhCacheProductAssociationRetrieveStrategyImpl.
 */

public class EhCacheProductAssociationRetrieveStrategyImplTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private final EhCacheProductAssociationRetrieveStrategyImpl strategy = new EhCacheProductAssociationRetrieveStrategyImpl();
	private final Ehcache cache = context.mock(Ehcache.class);
	private final ProductAssociationService productAssociationService = context.mock(ProductAssociationService.class);
	
	/**
	 * The setup method.
	 */
	@Before
	public void setUp() {
		strategy.setCache(cache);
		strategy.setProductAssociationService(productAssociationService);
	}
	
	/**
	 * Test get associations not cached.
	 */
	@Test
	public void testGetAssociationsNotCached() {
		final ProductAssociationSearchCriteria criteria = new ProductAssociationSearchCriteria();
		
		final Set<ProductAssociation> productAssociations = new HashSet<ProductAssociation>();
		
		final Element element = new Element(criteria, productAssociations);
 		
		context.checking(new Expectations() {
			{
				oneOf(cache).get(criteria);
				will(returnValue(null));
				
				oneOf(productAssociationService).getAssociations(criteria);
				will(returnValue(productAssociations));
				
				oneOf(cache).put(element);
			}
		});
		
		strategy.getAssociations(criteria);
		
	}
	
	/**
	 * Test get associations cached.
	 */
	@Test
	public void testGetAssociationsCached() {
		final ProductAssociationSearchCriteria criteria = new ProductAssociationSearchCriteria();
		
		final Set<ProductAssociation> productAssociations = new HashSet<ProductAssociation>();
		
		final Element element = new Element(criteria, productAssociations);
 		
		context.checking(new Expectations() {
			{
				oneOf(cache).get(criteria);
				will(returnValue(element));
			}
		});
		
		strategy.getAssociations(criteria);
		
	}
	
	/**
	 * Test get associations not cached.
	 */
	@Test
	public void testGetAssociationsCachedButExpired() {
		final ProductAssociationSearchCriteria criteria = new ProductAssociationSearchCriteria();
		
		final Set<ProductAssociation> productAssociations = new HashSet<ProductAssociation>();
		
		final Element element = new Element(criteria, productAssociations) {
			private static final long serialVersionUID = -6678306468591077238L;

			public boolean isExpired() {
				return true;
			}
		};
 		
		context.checking(new Expectations() {
			{
				oneOf(cache).get(criteria);
				will(returnValue(element));
				
				oneOf(productAssociationService).getAssociations(criteria);
				will(returnValue(productAssociations));
				
				oneOf(cache).put(element);
			}
		});
		
		strategy.getAssociations(criteria);
		
	}

}
