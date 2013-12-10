package com.elasticpath.importexport.importer.importers.impl;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.common.pricing.service.BaseAmountFilter;
import com.elasticpath.domain.pricing.BaseAmount;
import com.elasticpath.domain.pricing.PriceListDescriptor;
import com.elasticpath.domain.pricing.impl.BaseAmountImpl;
import com.elasticpath.importexport.importer.configuration.DependentElementConfiguration;
import com.elasticpath.importexport.importer.configuration.ImporterConfiguration;
import com.elasticpath.importexport.importer.importers.impl.PriceListDescriptorImporterImpl.PriceListDescriptorCollectionsStrategy;
import com.elasticpath.importexport.importer.types.CollectionStrategyType;
import com.elasticpath.importexport.importer.types.DependentElementType;
import com.elasticpath.service.pricing.BaseAmountService;

/**
 * Tests the price list descriptor importer.
 */
public class PriceListDescriptorCollectionStrategyTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	/**
	 * Tests the price list collection strategy with clear collection type.
	 */
	@Test
	public void testPriceListCollectionStrategyClearCollection() {
		final String priceListGuid = "priceListGuid";
		final ImporterConfiguration configuration = createBaseAmountImportConfiguration(CollectionStrategyType.CLEAR_COLLECTION);
		final BaseAmountFilter mockAmountFilter = context.mock(BaseAmountFilter.class);
		final PriceListDescriptor mockPriceListDescriptor = context.mock(PriceListDescriptor.class);
		
		final PriceListDescriptorCollectionsStrategy collectionsStrategy = new PriceListDescriptorCollectionsStrategy(configuration) {
			@Override
			BaseAmountFilter createBaseAmountFilter() {
				return mockAmountFilter;
			}
		};
		
		
		final BaseAmountService mockBaseAmountService = context.mock(BaseAmountService.class);
		collectionsStrategy.setBaseAmountService(mockBaseAmountService);
		context.checking(new Expectations() { {
			final Collection<BaseAmount> result = new ArrayList<BaseAmount>();
			final BaseAmount baseAmount1 = new BaseAmountImpl();
			final BaseAmount baseAmount2 = new BaseAmountImpl();
			
			result.add(baseAmount1);
			result.add(baseAmount2);
			oneOf(mockBaseAmountService).findBaseAmounts(mockAmountFilter); will(returnValue(result));
			oneOf(mockBaseAmountService).delete(baseAmount1);
			oneOf(mockBaseAmountService).delete(baseAmount2);
		
			allowing(mockPriceListDescriptor).getGuid(); will(returnValue(priceListGuid));
			oneOf(mockAmountFilter).setPriceListDescriptorGuid(priceListGuid);
		} });

		assertTrue(collectionsStrategy.isForPersistentObjectsOnly());
		collectionsStrategy.prepareCollections(mockPriceListDescriptor, null);
		
	}
	
	/**
	 * Tests the price list collection strategy with retain collection type.
	 */
	@Test
	public void testPriceListCollectionStrategyRetainCollection() {
		final ImporterConfiguration configuration = createBaseAmountImportConfiguration(CollectionStrategyType.RETAIN_COLLECTION);
		final PriceListDescriptor mockPriceListDescriptor = context.mock(PriceListDescriptor.class);
		final PriceListDescriptorCollectionsStrategy collectionsStrategy = new PriceListDescriptorCollectionsStrategy(configuration);
		
		final BaseAmountService mockBaseAmountService = context.mock(BaseAmountService.class);
		collectionsStrategy.setBaseAmountService(mockBaseAmountService);
		context.checking(new Expectations() { {
			never(mockBaseAmountService).findBaseAmounts(with(any(BaseAmountFilter.class)));
			never(mockBaseAmountService).delete(with(any(BaseAmount.class)));
			allowing(mockPriceListDescriptor).getGuid(); will(returnValue("GUID"));
		} });

		assertTrue(collectionsStrategy.isForPersistentObjectsOnly());
		collectionsStrategy.prepareCollections(mockPriceListDescriptor, null);
		
	}

	private ImporterConfiguration createBaseAmountImportConfiguration(final CollectionStrategyType collectionStrategyType) {
		final ImporterConfiguration configuration = new ImporterConfiguration();
		final Map<DependentElementType, DependentElementConfiguration> dependantMap = 
			new HashMap<DependentElementType, DependentElementConfiguration>();
		final DependentElementConfiguration dependentElementConfiguration = new DependentElementConfiguration();
		dependentElementConfiguration.setDependentElementType(DependentElementType.BASE_AMOUNTS);
		dependentElementConfiguration.setCollectionStrategyType(collectionStrategyType);
		dependantMap.put(dependentElementConfiguration.getDependentElementType(), dependentElementConfiguration);
		configuration.setDependentElementMap(dependantMap);
		return configuration;
	}

}
