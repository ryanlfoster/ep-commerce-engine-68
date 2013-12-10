package com.elasticpath.search.index.loader.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.store.Store;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.search.index.pipeline.IndexingStage;
import com.elasticpath.search.index.pipeline.stats.PipelinePerformance;
import com.elasticpath.service.catalogview.IndexProduct;
import com.elasticpath.service.catalogview.StoreProductService;
import com.elasticpath.service.store.StoreService;

/**
 * Test {@link BatchProductLoader}.
 */
public class BatchProductLoaderTest {
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private final StoreProductService storeProductService = context.mock(StoreProductService.class);

	private final StoreService storeService = context.mock(StoreService.class);

	private BatchProductLoader batchProductLoader;

	@SuppressWarnings("unchecked")
	private final IndexingStage<IndexProduct, ?> nextStage = context.mock(IndexingStage.class);

	private final PipelinePerformance performance = context.mock(PipelinePerformance.class);

	/**
	 * Initialize the services on the {@link BatchProductLoader}.
	 */
	@Before
	public void setUpBulkProductFetcher() {
		batchProductLoader = new BatchProductLoader();
		batchProductLoader.setStoreService(storeService);
		batchProductLoader.setStoreProductService(storeProductService);
		batchProductLoader.setPipelinePerformance(performance);
	}

	/**
	 * Test that loading a set of products works.
	 */
	@Test
	public void testLoadingValidList() {

		final IndexProduct firstIndexProduct = context.mock(IndexProduct.class, "first");
		final IndexProduct secondIndexProduct = context.mock(IndexProduct.class, "second");
		final Set<IndexProduct> indexProducts = new HashSet<IndexProduct>();
		indexProducts.add(firstIndexProduct);
		indexProducts.add(secondIndexProduct);

		final Set<Long> uidsToLoad = createUidsToLoad();

		context.checking(new Expectations() {
			{
				final List<Store> stores = Collections.emptyList();

				allowing(storeProductService).getIndexProducts(
						with(uidsToLoad), with(stores), with(aNull(FetchGroupLoadTuner.class)));
				will(returnValue(indexProducts));

				allowing(storeService).findAllCompleteStores(with(aNull(FetchGroupLoadTuner.class)));
				will(returnValue(stores));

				exactly(1).of(nextStage).send(with(firstIndexProduct));
				exactly(1).of(nextStage).send(with(secondIndexProduct));

				allowing(performance).addCount(with(any(String.class)), with(any(Long.class)));
				allowing(performance).addValue(with(any(String.class)), with(any(double.class)));
			}
		});

		batchProductLoader.setBatch(uidsToLoad);
		batchProductLoader.setNextStage(nextStage);
		batchProductLoader.run();
	}

	/**
	 * Test that trying to load a set of products without setting the next stage fails.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testLoadingInvalidNextStage() {
		batchProductLoader.setBatch(createUidsToLoad());
		batchProductLoader.run();
	}

	/**
	 * Returns a batch of 1 long with the value of one.
	 * 
	 * @return a very lonely batch, if the object doesn't get garbage collected eventually it may need to seek therapy.
	 */
	private Set<Long> createUidsToLoad() {
		final Set<Long> uidsToLoad = new HashSet<Long>();
		uidsToLoad.add(Long.valueOf(1));
		return uidsToLoad;
	}
}
