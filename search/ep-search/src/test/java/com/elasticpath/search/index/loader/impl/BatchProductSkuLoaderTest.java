package com.elasticpath.search.index.loader.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.search.index.pipeline.IndexingStage;
import com.elasticpath.search.index.pipeline.stats.impl.PipelinePerformanceImpl;
import com.elasticpath.service.catalog.ProductSkuService;

/**
 * Test {@link BatchProductSkuLoader}.
 */
public class BatchProductSkuLoaderTest {
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	
	private final ProductSkuService productSkuService = context.mock(ProductSkuService.class);
	
	@SuppressWarnings("unchecked")
	private final IndexingStage<ProductSku, ?> nextStage = context.mock(IndexingStage.class);
	
	private BatchProductSkuLoader batchProductSkuLoader;
	
	/**
	 * Initialize the services on the {@link BatchProductSkuLoader}.
	 */
	@Before
	public void setUpBulkProductFetcher() {
		batchProductSkuLoader = new BatchProductSkuLoader();
		batchProductSkuLoader.setPipelinePerformance(new PipelinePerformanceImpl());
		batchProductSkuLoader.setSkuService(productSkuService);
	}

	/**
	 * Test that loading a set of product skus works.
	 */
	@Test
	public void testLoadingValidList() {
		
		final List<ProductSku> productSkus = new ArrayList<ProductSku>();
		final ProductSku firstProductSku = context.mock(ProductSku.class, "first");
		final ProductSku secondProductSku = context.mock(ProductSku.class, "second");
		productSkus.add(firstProductSku);
		productSkus.add(secondProductSku);

		final Set<Long> uidsToLoad = createUidsToLoad();

		context.checking(new Expectations() { {
			allowing(productSkuService).findByUids(with(uidsToLoad), with(aNull(FetchGroupLoadTuner.class)));
			will(returnValue(productSkus));

			oneOf(nextStage).send(with(firstProductSku));
			oneOf(nextStage).send(with(secondProductSku));
		} });

		batchProductSkuLoader.setBatch(uidsToLoad);
		batchProductSkuLoader.setNextStage(nextStage);
		batchProductSkuLoader.run();
	}
	
	/**
	 * Test sending an empty set of product sku uids to load returns an empty list.
	 */
	@Test
	public void testLoadingNoSkus() {
		batchProductSkuLoader.setBatch(new HashSet<Long>());
		batchProductSkuLoader.setNextStage(nextStage);
		batchProductSkuLoader.run();
	}
	
	
	/**
	 * Test that trying to load a set of product skus without setting the next stage fails.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testLoadingInvalidNextStage() {
		batchProductSkuLoader.setBatch(createUidsToLoad());
		batchProductSkuLoader.run();
	}

	private Set<Long> createUidsToLoad() {
		final Set<Long> uidsToLoad = new HashSet<Long>();
		uidsToLoad.add(Long.valueOf(1));
		return uidsToLoad;
	}
	
}
