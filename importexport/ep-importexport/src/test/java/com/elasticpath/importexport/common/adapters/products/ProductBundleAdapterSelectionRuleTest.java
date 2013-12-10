package com.elasticpath.importexport.common.adapters.products;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.domain.catalog.BundleConstituent;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.SelectionRule;
import com.elasticpath.domain.catalog.impl.ProductBundleImpl;
import com.elasticpath.domain.catalog.impl.SelectionRuleImpl;
import com.elasticpath.importexport.common.dto.products.bundles.ProductBundleConstituentDTO;
import com.elasticpath.importexport.common.dto.products.bundles.ProductBundleDTO;
import com.elasticpath.importexport.common.dto.products.bundles.SelectionRuleDTO;
import com.elasticpath.service.catalog.impl.BundleValidatorImpl;

/**
 * The test for ProductBundleAdapter to populate domain product bundle with or without selection rule.
 */

public class ProductBundleAdapterSelectionRuleTest {

	private static final int SELECTION_RULE_PARAMETER_ZERO = 0;
	
	private static final int SELECTION_RULE_PARAMETER_NOT_ZERO = 4;

	private static final String PRODUCT_CODE = "CODE_1";

	private ProductBundleAdapter productBundleAdapterStub;

	/** SetUps the test. */
	@Before
	public void setUp() {
		productBundleAdapterStub = new ProductBundleAdapter() {
			@Override
			BundleConstituent createOrUpdateBundleConstituent(final ProductBundle bundle,	final ProductBundleConstituentDTO constituentDTO) {
				return null;
			}

			@Override
			void checkForCyclicDependencies(final ProductBundle target) {
				//do nothing
			}
			
			@Override 
			void checkForAssignedBundlesWithRecurringCharges(final ProductBundle target) {
				// do nothing
			}
			
			void replaceConstituents(final ProductBundle bundle, final Collection<BundleConstituent> newConstituents) {
				//no-op
			};
			
			@Override
			SelectionRule createSelectionRule(final Integer param) {
				final SelectionRule rule = new SelectionRuleImpl();
				rule.setParameter(param);
				return rule;
			}
		};
		
		productBundleAdapterStub.setBundleValidator(new BundleValidatorImpl());
	}

	/**
	 * Test method for {@link ProductBundleAdapter#populateDomain(ProductBundleDTO, ProductBundle)},
	 * when source's selectionRule is null, and the target's selectionRule is null, then after executing populateDomain(), 
	 * target's selectionRule is null.
	 */
	@Test
	public void testPopulateDomainWithSourceSelectionRuleNullAndTargetSelectionRuleNull() {
		final ProductBundleConstituentDTO productBundleConstituentDTO = new ProductBundleConstituentDTO();

		ProductBundleDTO productBundleDTO = new ProductBundleDTO();
		productBundleDTO.setCode(PRODUCT_CODE);
		productBundleDTO.setConstituents(Arrays.asList(productBundleConstituentDTO));
		productBundleDTO.setSelectionRule(null);
		
		final ProductBundle productBundle = new ProductBundleImpl();
		productBundle.setSelectionRule(null);

		productBundleAdapterStub.populateDomain(productBundleDTO, productBundle);

		assertNull(productBundle.getSelectionRule());
	}
	

	/**
	 * Test method for {@link ProductBundleAdapter#populateDomain(ProductBundleDTO, ProductBundle)},
	 * when source's selectionRule is null, and the target's selectionRule is not null, then after 
	 * executing populateDomain(), target's selectionRule is null.
	 */
	@Test
	public void testPopulateDomainWithSourceSelectionRuleNullAndTargetSelectionRuleNotNull() {
		final ProductBundleConstituentDTO productBundleConstituentDTO = new ProductBundleConstituentDTO();

		ProductBundleDTO productBundleDTO = new ProductBundleDTO();
		productBundleDTO.setCode(PRODUCT_CODE);
		productBundleDTO.setConstituents(Arrays.asList(productBundleConstituentDTO));
		productBundleDTO.setSelectionRule(null);
		
		final ProductBundle productBundle = new ProductBundleImpl();
		SelectionRule rule = new SelectionRuleImpl();
		rule.setParameter(SELECTION_RULE_PARAMETER_ZERO);
		productBundle.setSelectionRule(rule);
		
		productBundleAdapterStub.populateDomain(productBundleDTO, productBundle);

		assertNull(productBundle.getSelectionRule());
	}
	
	/**
	 * Test method for {@link ProductBundleAdapter#populateDomain(ProductBundleDTO, ProductBundle)},
	 * when source's selectionRule is not null, and the target's selectionRule is null, then after 
	 * executing populateDomain(), target's selectionRule has same parameter as the source's selectionRule.
	 */
	@Test
	public void testPopulateDomainWithSourceSelectionRuleNotNullAndTargetSelectionRuleNull() {
		
		final ProductBundleConstituentDTO productBundleConstituentDTO = new ProductBundleConstituentDTO();

		ProductBundleDTO productBundleDTO = new ProductBundleDTO();
		productBundleDTO.setCode(PRODUCT_CODE);
		productBundleDTO.setConstituents(Arrays.asList(productBundleConstituentDTO));
		
		SelectionRuleDTO ruleDTO = new SelectionRuleDTO();
		ruleDTO.setParameter(SELECTION_RULE_PARAMETER_ZERO);
		
		productBundleDTO.setSelectionRule(ruleDTO);
		
		final ProductBundle productBundle = new ProductBundleImpl();
		productBundle.setSelectionRule(null);

		productBundleAdapterStub.populateDomain(productBundleDTO, productBundle);

		assertNotNull(productBundle.getSelectionRule());
		assertEquals(SELECTION_RULE_PARAMETER_ZERO, productBundle.getSelectionRule().getParameter()); 
	}
	
	/**
	 * Test method for {@link ProductBundleAdapter#populateDomain(ProductBundleDTO, ProductBundle)},
	 * when source's selectionRule is not null, and the target's selectionRule is not null, then after 
	 * executing populateDomain(), target's selectionRule has same parameter as the source's selectionRule.
	 */
	@Test
	public void testPopulateDomainWithSourceSelectionRuleNotNullAndTargetSelectionRuleNotNull() {
		final ProductBundleConstituentDTO productBundleConstituentDTO = new ProductBundleConstituentDTO();

		final ProductBundleDTO productBundleDTO = new ProductBundleDTO();
		productBundleDTO.setCode(PRODUCT_CODE);
		productBundleDTO.setConstituents(Arrays.asList(productBundleConstituentDTO));
		SelectionRuleDTO ruleDTO = new SelectionRuleDTO();
		ruleDTO.setParameter(SELECTION_RULE_PARAMETER_ZERO);
		productBundleDTO.setSelectionRule(ruleDTO);
		
		final ProductBundle productBundle = new ProductBundleImpl();
		SelectionRule rule = new SelectionRuleImpl();
		rule.setParameter(SELECTION_RULE_PARAMETER_NOT_ZERO);
		productBundle.setSelectionRule(rule);

		productBundleAdapterStub.populateDomain(productBundleDTO, productBundle);

		assertNotNull(productBundle.getSelectionRule());
		assertEquals(SELECTION_RULE_PARAMETER_ZERO, productBundle.getSelectionRule().getParameter()); 
	}
}
