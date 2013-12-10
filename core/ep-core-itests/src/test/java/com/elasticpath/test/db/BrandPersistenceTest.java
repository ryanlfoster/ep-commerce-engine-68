package com.elasticpath.test.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;

import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.impl.BrandImpl;
import com.elasticpath.domain.catalog.impl.CatalogImpl;
import com.elasticpath.service.catalog.BrandService;
import com.elasticpath.test.integration.DirtiesDatabase;
import com.elasticpath.test.util.Utils;

/**
 * Test that the brand domain object is persisted correctly. 
 * This class will call most methods on JpaPersistenceEngine to make sure we put it through its paces.
 */
public class BrandPersistenceTest extends DbTestCase {

	private static final String GUID = "guid";
	private static final String CODE = "code";
	
	/**
	 * Tests brand saving operation.
	 */
	@Ignore
	@DirtiesDatabase
	@Test
	public void testSaveBrand() {
		final Brand brand = createSimpleBrand();

		final Brand savedBrand = getTxTemplate().execute(new TransactionCallback<Brand>() {
			@Override
			public Brand doInTransaction(final TransactionStatus arg0) {
				getPersistenceEngine().save(brand);
				return brand;
			}
		});

		assertSame("Saved brand not the same object.", brand, savedBrand);
		assertSame("Saved catalog not the same object.", brand.getCatalog(), savedBrand.getCatalog());
	}
	
	/**
	 * Actually check retrieving a brand gives us the data we stored into it.
	 */
	@Ignore
	@DirtiesDatabase
	@Test
	public void testRetrieveBrand() {

		final String guid = Utils.uniqueCode(GUID);
		final String code = Utils.uniqueCode(CODE);
		final Catalog catalog = createPersistedCatalog();
		final String imageUrl = "brandImageUrl";

		final Brand brand = new BrandImpl();

		brand.setGuid(guid);
		brand.setCode(code);
		brand.setCatalog(catalog);
		brand.setImageUrl(imageUrl);

		getTxTemplate().execute(new TransactionCallback<Brand>() {
			@Override
			public Brand doInTransaction(final TransactionStatus arg0) {
				getPersistenceEngine().save(brand);
				return brand;
			}
		});
		assertTrue(brand.getUidPk() != 0);
		Brand retrievedBrand = getTxTemplate().execute(new TransactionCallback<Brand>() {
			@Override
			public Brand doInTransaction(final TransactionStatus arg0) {
				return getPersistenceEngine().get(BrandImpl.class, brand.getUidPk());
			}
		});
		BrandService service = getBeanFactory().getBean("brandService");
		Brand newBrand = new BrandImpl();
		newBrand.setGuid("newGuid");
		newBrand.setCode("newCode");
		newBrand.setCatalog(catalog);
		service.add(newBrand);
		
		assertTrue(newBrand.getUidPk() != 0);
		assertEquals(newBrand.getUidPk(), service.findAllBrandsFromCatalog(catalog.getUidPk()).get(1).getUidPk());
		assertEquals(newBrand.getUidPk(), service.list().get(1).getUidPk());
		assertNotNull(service.get(newBrand.getUidPk()));
				
				
				
		assertTrue(brand.getUidPk() != 0);
		assertEquals(brand.getUidPk(), service.findAllBrandsFromCatalog(catalog.getUidPk()).get(0).getUidPk());
		assertEquals(brand.getUidPk(), service.list().get(0).getUidPk());
		assertNotNull(service.get(brand.getUidPk()));

//		Brand retrievedBrand = getPersistenceEngine().get(BrandImpl.class, brand.getUidPk());
		assertNotNull("Brand not retrieved - null", retrievedBrand);
		assertEquals(catalog.getUidPk(), retrievedBrand.getCatalog().getUidPk());
		assertEquals(code, retrievedBrand.getCode()); // NOTE: code and guid are synonyms
		assertEquals(code, retrievedBrand.getGuid()); // last in wins
		assertEquals(imageUrl, retrievedBrand.getImageUrl());
	}

	/**
	 * Tests brand merging.
	 */
	@Ignore
	@DirtiesDatabase
	@Test
	public void testMergeBrand() {
		final Brand brand = createSimpleBrand();

		getTxTemplate().execute(new TransactionCallback<Brand>() {
			@Override
			public Brand doInTransaction(final TransactionStatus arg0) {
				getPersistenceEngine().save(brand);
				return brand;
			}
		});

		final Brand retrievedBrand = getPersistenceEngine().get(BrandImpl.class, brand.getUidPk());

		final Brand mergedBrand = getTxTemplate().execute(new TransactionCallback<Brand>() {
			@Override
			public Brand doInTransaction(final TransactionStatus arg0) {
				return getPersistenceEngine().merge(retrievedBrand);
			}
		});

		assertNotSame("Merged brand is the same object.", retrievedBrand, mergedBrand);
		assertNotSame("Merged catalog is not the same object.", retrievedBrand.getCatalog(), mergedBrand.getCatalog());
	}

	/**
	 * Test that updating a brand works like merging - they have identical code at the moment.
	 */
	@Ignore
	@DirtiesDatabase
	@Test
	public void testUpdateBrand() {
		final Brand brand = createSimpleBrand();

		getTxTemplate().execute(new TransactionCallback<Brand>() {
			@Override
			public Brand doInTransaction(final TransactionStatus arg0) {
				getPersistenceEngine().save(brand);
				return brand;
			}
		});

		final Brand retrievedBrand = getPersistenceEngine().get(BrandImpl.class, brand.getUidPk());

		final Brand mergedBrand = getTxTemplate().execute(new TransactionCallback<Brand>() {
			@Override
			public Brand doInTransaction(final TransactionStatus arg0) {
				return getPersistenceEngine().update(retrievedBrand);
			}
		});

		assertNotSame("Updated brand is the same object.", retrievedBrand, mergedBrand);
		assertNotSame("Updated catalog is not the same object.", retrievedBrand.getCatalog(), mergedBrand.getCatalog());
	}

	/**
	 * Test that deleting a brand works..
	 */
	@Ignore
	@DirtiesDatabase
	@Test
	public void testDeleteBrand() {
		final Brand brand = createSimpleBrand();

		getTxTemplate().execute(new TransactionCallback<Brand>() {
			@Override
			public Brand doInTransaction(final TransactionStatus arg0) {
				getPersistenceEngine().save(brand);
				return brand;
			}
		});

		final Brand retrievedBrand = getPersistenceEngine().get(BrandImpl.class, brand.getUidPk());
		assertNotNull(retrievedBrand);

		getTxTemplate().execute(new TransactionCallbackWithoutResult() {
			@Override
			protected void doInTransactionWithoutResult(final TransactionStatus arg0) {
				getPersistenceEngine().delete(retrievedBrand);
			}
		});

		final Brand retrievedDeletedBrand = getPersistenceEngine().get(BrandImpl.class, brand.getUidPk());
		assertNull("Deleted brand found in database", retrievedDeletedBrand);
	}

	/**
	 * Tests retriveing operation.
	 */
	@Ignore
	@DirtiesDatabase
	@Test
	public void testRetrieveByNamedQueryWithList() {
		getTxTemplate().execute(new TransactionCallback<Brand>() {
			@Override
			public Brand doInTransaction(final TransactionStatus arg0) {
				final int three = 3;
				List<Brand> brands = getPersistenceEngine().retrieveByNamedQueryWithList("BRAND_IN_USE_LIST", "list", Arrays.asList(1, 2, three));
				assertNotNull(brands);
				assertEquals(0, brands.size());
				return null;
			}
		});
	}

	/**
	 * Make sure that saveOrMerge works as expected.
	 */
	@Ignore
	@DirtiesDatabase
	@Test
	public void testSaveOrMergeDoingSaveThenMerge() {

		final Brand brand = createSimpleBrand();
		assertFalse("Precondition failed", brand.isPersisted());

		final Brand returnedBrand = getTxTemplate().execute(new TransactionCallback<Brand>() {
			@Override
			public Brand doInTransaction(final TransactionStatus arg0) {
				getPersistenceEngine().saveOrMerge(brand);
				return brand;
			}
		});

		assertSame("When saving the object we passed in should be returned.", brand, returnedBrand);
		assertTrue(brand.isPersisted());

		// Dirty the object
		String code = Utils.uniqueCode(CODE);
		brand.setCode(code);

		final Brand mergeReturnedBrand = getTxTemplate().execute(new TransactionCallback<Brand>() {
			@Override
			public Brand doInTransaction(final TransactionStatus arg0) {
				getPersistenceEngine().saveOrMerge(brand);
				return brand;
			}
		});

		// OpenJPA seems to return the same object here.
		assertEquals(code, mergeReturnedBrand.getCode());
		assertEquals(code, brand.getCode());
	}

	/**
	 * Make sure that saveOrUpdate work the same as saveOrMerge, one calls the other at the moment.
	 */
	@Ignore
	@DirtiesDatabase
	@Test
	public void testSaveOrUpdateDoingSaveThenMerge() {

		final Brand brand = createSimpleBrand();
		assertFalse("Precondition failed", brand.isPersisted());

		final Brand returnedBrand = getTxTemplate().execute(new TransactionCallback<Brand>() {
			@Override
			public Brand doInTransaction(final TransactionStatus arg0) {
				getPersistenceEngine().saveOrUpdate(brand);
				return brand;
			}
		});

		assertSame("When saving the object we passed in should be returned.", brand, returnedBrand);
		assertTrue(brand.isPersisted());

		// Dirty the object
		String code = Utils.uniqueCode(CODE);
		brand.setCode(code);

		final Brand mergeReturnedBrand = getTxTemplate().execute(new TransactionCallback<Brand>() {
			@Override
			public Brand doInTransaction(final TransactionStatus arg0) {
				getPersistenceEngine().saveOrUpdate(brand);
				return brand;
			}
		});

		// OpenJPA seems to return the same object here.
		assertEquals(code, mergeReturnedBrand.getCode());
		assertEquals(code, brand.getCode());
	}

	private Brand createSimpleBrand() {

		final Brand brand = new BrandImpl();
		brand.initialize();
		brand.setCode(Utils.uniqueCode(CODE));
		brand.setCatalog(createPersistedCatalog());
		return brand;
	}

	private Catalog createPersistedCatalog() {
		final Catalog catalog = new CatalogImpl();
		catalog.setCode(Utils.uniqueCode("catalog"));
		catalog.setDefaultLocale(Locale.getDefault());
//		catalog.setDefaultCurrency(Currency.getInstance(Locale.CANADA));

		getTxTemplate().execute(new TransactionCallback<Catalog>() {
			@Override
			public Catalog doInTransaction(final TransactionStatus arg0) {
				getPersistenceEngine().save(catalog);
				return catalog;
			}
		});

		return catalog;
	}

}
