package com.elasticpath.test.integration.importjobs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.Collection;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Test;

import com.elasticpath.common.pricing.service.BaseAmountFilter;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.dataimport.ImportBadRow;
import com.elasticpath.domain.dataimport.ImportJob;
import com.elasticpath.domain.dataimport.impl.AbstractImportTypeImpl;
import com.elasticpath.domain.pricing.BaseAmount;
import com.elasticpath.domain.pricing.PriceListDescriptor;
import com.elasticpath.service.pricing.BaseAmountService;
import com.elasticpath.service.pricing.PriceListDescriptorService;
import com.elasticpath.test.integration.DirtiesDatabase;
import com.elasticpath.test.util.Utils;

/**
 * Test import job for Base Amounts.
 */
public class ImportBaseAmountTest extends ImportJobTestCase {

	private void assertBaseAmountsInserted() {
		BaseAmountService baseAmountService = getBeanFactory().getBean(ContextIdNames.BASE_AMOUNT_SERVICE);
		BaseAmountFilter filter = getBeanFactory().getBean(ContextIdNames.BASE_AMOUNT_FILTER);
		Collection<BaseAmount> baseAmounts = baseAmountService.findBaseAmounts(filter);
		assertFalse(baseAmounts.isEmpty());
	}

	/**
	 * Test import BaseAmount insert.
	 *
	 * @throws Exception if any exception is thrown
	 */
	@DirtiesDatabase
	@Test
	public void testImportBaseAmountInsert() throws Exception {
		PriceListDescriptor priceList = createPriceListForImport();

		List<ImportBadRow> badRows = executeImportJob(createInsertBaseAmountImportJob(priceList.getGuid()));
		assertEquals(0, badRows.size());
		assertBaseAmountsInserted();
	}
	
	/**
	 * Creates a PriceList into which BaseAmounts can be imported.
	 * @return the created PLD
	 */
	PriceListDescriptor createPriceListForImport() {
		String name = "PRICE LIST";
		PriceListDescriptor descriptor = getBeanFactory().getBean(ContextIdNames.PRICE_LIST_DESCRIPTOR);
		String guid = descriptor.getGuid();
		assertNotNull(guid);
		descriptor.setCurrencyCode(Currency.getInstance(Locale.CANADA).getCurrencyCode());
		descriptor.setName(name);
		PriceListDescriptor createdDescriptor = getPriceListDescriptorService().add(descriptor);
		assertNotNull("The created PriceList into which BaseAmounts will be inserted cannot be null.", createdDescriptor);
		return createdDescriptor;
	}
	
	/**
	 * @return the PriceListDescriptor Service
	 */
	PriceListDescriptorService getPriceListDescriptorService() {
		return getBeanFactory().getBean(ContextIdNames.PRICE_LIST_DESCRIPTOR_SERVICE);
	}
	
	/**
	 * @param priceListDescriptorGuid the GUID of the price list descriptor
	 * @return the created import job
	 */
	protected ImportJob createInsertBaseAmountImportJob(final String priceListDescriptorGuid) {
		Map<String, Integer> mappings = new HashMap<String, Integer>();
		final int column3 = 3;
		final int column4 = 4;
		final int column5 = 5;
		final int column6 = 6;
		
		mappings.put("type", 1);
		mappings.put("productCode", 2);
		mappings.put("skuCode", column3);
		mappings.put("quantity", column4);
		mappings.put("listPrice", column5);
		mappings.put("salePrice", column6);

		ImportJob importJob = createSimpleImportJob(scenario.getCatalog(), Utils.uniqueCode("Insert Base Amounts"),
				"baseamount.csv", AbstractImportTypeImpl.INSERT_UPDATE_TYPE, "Base Amount", mappings);
		importJob.setDependentPriceListGuid(priceListDescriptorGuid);
		return importService.saveOrUpdateImportJob(importJob);
	}

}
