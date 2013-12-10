package com.elasticpath.test.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.common.dto.pricing.PriceListDescriptorDTO;
import com.elasticpath.common.pricing.service.PriceListService;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.BundleConstituent;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.pricing.PriceAdjustment;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.service.catalog.ProductService;
import com.elasticpath.service.pricing.PriceAdjustmentService;
import com.elasticpath.service.pricing.dao.PriceAdjustmentDao;
import com.elasticpath.test.persister.CatalogTestPersister;
import com.elasticpath.test.persister.TaxTestPersister;
import com.elasticpath.test.persister.testscenarios.SimpleStoreScenario;

/**
 * Test the PriceAdjustmentServiceImpl.
 */
public class PriceAdjustmentServiceImplTest extends BasicSpringContextTest {

	@Autowired
	private PriceAdjustmentService priceAdjustmentService;

	@Autowired
	private PriceListService priceListService;

	private CatalogTestPersister catalogPersister;

	private SimpleStoreScenario scenario;

	@Autowired
	private ProductService productService;

	private TaxCode taxCode;


	/**
	 * Get a reference to TestApplicationContext for use within the test. Setup scenarios.
	 * @throws Exception on error
	 */
	@Before
	public void setUp() throws Exception {
		scenario = getTac().useScenario(SimpleStoreScenario.class);
		catalogPersister = getTac().getPersistersFactory().getCatalogTestPersister();
		taxCode = getTac().getPersistersFactory().getTaxTestPersister().getTaxCode(TaxTestPersister.TAX_CODE_GOODS);
	}

	private void createBundles() {
		ProductBundle pb = catalogPersister.createSimpleProductBundle("TYPE", "P1", scenario.getCatalog(), scenario.getCategory(), taxCode);
		pb = (ProductBundle) productService.saveOrUpdate(pb);

		Product pbc = catalogPersister.createSimpleProduct("TYPE", "P1C", scenario.getCatalog(), taxCode, scenario.getCategory());
		pbc = productService.saveOrUpdate(pbc);
		
		Product pbc2 = catalogPersister.createSimpleProduct("TYPE", "P1C2", scenario.getCatalog(), taxCode, scenario.getCategory());
		pbc2 = productService.saveOrUpdate(pbc2);
		
		BundleConstituent constituent = catalogPersister.createSimpleBundleConstituent(pbc, 1);
		BundleConstituent constituent2 = catalogPersister.createSimpleBundleConstituent(pbc2, 1);
		constituent.setGuid("CGUID");
		constituent2.setGuid("C2GUID");
		pb.addConstituent(constituent);
		pb.addConstituent(constituent2);
		productService.saveOrUpdate(pb);
		
		PriceListDescriptorDTO pldDTO = new PriceListDescriptorDTO();
		pldDTO.setGuid("PLGUID");
		pldDTO.setName("PLNAME");
		pldDTO.setCurrencyCode("CAD");
		priceListService.saveOrUpdate(pldDTO);
		
	}
	
	private void createNestedBundles() {
		ProductBundle bundleA = catalogPersister.createSimpleProductBundle("TYPE", "A", scenario.getCatalog(), scenario.getCategory(), taxCode);
		bundleA = (ProductBundle) productService.saveOrUpdate(bundleA);

		ProductBundle bundleB = catalogPersister.createSimpleProductBundle("TYPE", "B", scenario.getCatalog(), scenario.getCategory(), taxCode);
		bundleB = (ProductBundle) productService.saveOrUpdate(bundleB);

		Product prodA1 = catalogPersister.createSimpleProduct("TYPE", "A1", scenario.getCatalog(), taxCode, scenario.getCategory());
		prodA1 = productService.saveOrUpdate(prodA1);
		
		Product prodA2 = catalogPersister.createSimpleProduct("TYPE", "A2", scenario.getCatalog(), taxCode, scenario.getCategory());
		prodA2 = productService.saveOrUpdate(prodA2);
		
		Product prodB1 = catalogPersister.createSimpleProduct("TYPE", "B1", scenario.getCatalog(), taxCode, scenario.getCategory());
		prodB1 = productService.saveOrUpdate(prodB1);
		
		Product prodB2 = catalogPersister.createSimpleProduct("TYPE", "B2", scenario.getCatalog(), taxCode, scenario.getCategory());
		prodB2 = productService.saveOrUpdate(prodB2);

		BundleConstituent constituentA1 = catalogPersister.createSimpleBundleConstituent(prodA1, 1);
		BundleConstituent constituentA2 = catalogPersister.createSimpleBundleConstituent(prodA2, 1);
		BundleConstituent constituentB  = catalogPersister.createSimpleBundleConstituent(bundleB, 1);
		BundleConstituent constituentB1 = catalogPersister.createSimpleBundleConstituent(prodB1, 1);
		BundleConstituent constituentB2 = catalogPersister.createSimpleBundleConstituent(prodB2, 1);

		constituentA1.setGuid("A1GUID");
		constituentA2.setGuid("A2GUID");
		constituentB.setGuid("BGUID");
		constituentB1.setGuid("B1GUID");
		constituentB2.setGuid("B2GUID");
		
		bundleB.addConstituent(constituentB1);
		bundleB.addConstituent(constituentB2);
		//productService.saveOrUpdate(bundleB);
		
		bundleA.addConstituent(constituentA1);
		bundleA.addConstituent(constituentA2);
		bundleA.addConstituent(constituentB);
		productService.saveOrUpdate(bundleA);
		
		PriceListDescriptorDTO pldDTO = new PriceListDescriptorDTO();
		pldDTO.setGuid("PLGUID");
		pldDTO.setName("PLNAME");
		pldDTO.setCurrencyCode("CAD");
		priceListService.saveOrUpdate(pldDTO);
		
	}
	
	/**
	 * test we can get a list of price adjustments by supplying a list of bundle constituents.
	 */
	@DirtiesDatabase
	@Test
	public void testRetrieveByBundleConstituentList() {
		createBundles();
		createPA("P1", "PA1", "PLGUID", "CGUID");
		createPA("P1", "PA2", "PLGUID", "C2GUID");
		
		
		PriceAdjustmentDao dao = getBeanFactory().getBean("priceAdjustmentDao");
		Collection<String> bcList = new ArrayList<String>();
		bcList.add("CGUID");
		bcList.add("C2GUID");
		Collection<PriceAdjustment> assignments = dao.findByPriceListBundleConstituents("PLGUID", bcList);
		assertEquals(2, assignments.size());
	}

	
	/**
	 * Ensure we can get all price adjustments by price list guid.
	 */
	@DirtiesDatabase
	@Test
	public void testRetrieveAdjustmentsForPriceList() {
		createBundles();
		PriceAdjustment createPA = createPA("P1", "PA1", "PLGUID", "CGUID");
		PriceAdjustment createPA2 = createPA("P1", "PA2", "PLGUID", "C2GUID");
		List<PriceAdjustment> adjustments = priceAdjustmentService.findByPriceList("PLGUID");
		assertNotNull(adjustments);
		assertEquals(2, adjustments.size());
		
		assertTrue("PA1 should be found", adjustments.contains(createPA));
		assertTrue("PA2 should be found", adjustments.contains(createPA2));
	} 
	
	/**
	 * Test that looking up price adjustments works for regular and nested product bundles. 
	 */
	@DirtiesDatabase
	@Test
	public void testFindByPriceListAndBundleAsMap() {
		
		createNestedBundles();
		
		PriceAdjustment paA1 = createPA("A", "PAA1", "PLGUID", "A1GUID");
		PriceAdjustment paB  = createPA("A", "PAB", "PLGUID", "BGUID");
		PriceAdjustment paB1 = createPA("B", "PAB1", "PLGUID", "B1GUID");
		PriceAdjustment paB2 = createPA("B", "PAB2", "PLGUID", "B2GUID");
		
		assertEquals("Should return an empty map when searching an invalid price list.",
				Collections.emptyMap(),
				priceAdjustmentService.findByPriceListAndBundleAsMap("INVALID_PL_GUID", (ProductBundle) productService.findByGuid("B")));
		
		
		Map<String, PriceAdjustment> expectedPriceAdjustments = new HashMap<String, PriceAdjustment>();
		expectedPriceAdjustments.put("B1GUID", paB1);
		expectedPriceAdjustments.put("B2GUID", paB2);
		
		assertEquals("Should return a map with two price adjustments for bundle B.",
				expectedPriceAdjustments, priceAdjustmentService.findByPriceListAndBundleAsMap("PLGUID",
				(ProductBundle) productService.findByGuid("B")));
		
		expectedPriceAdjustments.put("A1GUID", paA1);
		expectedPriceAdjustments.put("BGUID", paB);
		
		assertEquals("Should return a map with four price adjustments for bundle A: adjustments of bundle B and its constituents must be included.",
				expectedPriceAdjustments, priceAdjustmentService.findByPriceListAndBundleAsMap("PLGUID",
				(ProductBundle) productService.findByGuid("A")));
	}
	
	/**
	 * Test we can get all price adjustments on a product bundle.
	 */
	@DirtiesDatabase
	@Test
	public void testRetrievePAforBundle() {
		createBundles();
		PriceAdjustment createPA = createPA("P1", "PA1", "PLGUID", "CGUID");
		PriceAdjustment createPA2 = createPA("P1", "PA2", "PLGUID", "C2GUID");
		
		ProductBundle bundle = (ProductBundle) productService.findByGuid("P1");
		Collection<PriceAdjustment> findAllAdjustmentsOnBundle = priceAdjustmentService.findAllAdjustmentsOnBundle("PLGUID", bundle);
		assertNotNull(findAllAdjustmentsOnBundle);
		assertEquals(2, findAllAdjustmentsOnBundle.size());
		assertEquals(createPA.getGuid(), ((PriceAdjustment) findAllAdjustmentsOnBundle.toArray()[0]).getGuid());
		assertEquals(createPA2.getGuid(), ((PriceAdjustment) findAllAdjustmentsOnBundle.toArray()[1]).getGuid());
	}

	private PriceAdjustment createPA(final String bundleGuid, final String paGuid, final String plGuid, final String cGuid) {
		PriceAdjustment pa = getBeanFactory().getBean(ContextIdNames.PRICE_ADJUSTMENT);
		pa.setPriceListGuid(plGuid);
		pa.setAdjustmentAmount(BigDecimal.TEN);
		pa.setGuid(paGuid);
		ProductBundle bundle = (ProductBundle) productService.findByGuid(bundleGuid);
		for (BundleConstituent constituent : bundle.getConstituents()) {
			if (cGuid.equals(constituent.getGuid())) {
				constituent.addPriceAdjustment(pa);
				break;
			}
		}
		bundle = (ProductBundle) productService.saveOrUpdate(bundle);
		
		for (BundleConstituent constituent : bundle.getConstituents()) {
			if (cGuid.equals(constituent.getGuid())) {
				constituent.addPriceAdjustment(pa);
				for (PriceAdjustment adj : constituent.getPriceAdjustments()) {
					if (plGuid.equals(adj.getPriceListGuid())) {
						return adj;
					}
				}
			}
		}
		
		return null;
	}
	
}
