package com.elasticpath.test.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.Date;
import java.util.Locale;

import org.junit.Test;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.CategoryType;
import com.elasticpath.domain.catalog.LocaleDependantFields;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.impl.CategoryImpl;
import com.elasticpath.domain.catalog.impl.CategoryTypeImpl;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.catalog.impl.ProductLocaleDependantFieldsImpl;
import com.elasticpath.test.integration.DirtiesDatabase;
import com.elasticpath.test.util.Utils;

/**
 * Move <code>ProductImpl</code>'s back and forth through the persistence 
 * layer to check basic behaviour.
 */
public class ProductPersistenceTest extends DbTestCase {
	
	/**
	 * Tests product saving.
	 */
	@DirtiesDatabase
	@Test
	public void testSaveProduct() {
		final Product product = createSimpleProduct();

		final Product savedProduct = saveProduct(product);

		assertSame("Saved product not the same object.", product, savedProduct);
		assertSame("Saved product type not the same object.", product.getProductType(), savedProduct.getProductType());
		assertSame("Saved catalog not the same object.", product.getProductType().getCatalog(), savedProduct.getProductType().getCatalog());
		assertSame("Saved tax code not the same object.", product.getProductType().getTaxCode(), savedProduct.getProductType().getTaxCode());
	}

	/**
	 * Tests saving product operation.
	 */
	@DirtiesDatabase
	@Test
	public void testSaveProductWithMapChange() {
		final Product product = createSimpleProduct();
		ProductLocaleDependantFieldsImpl f = createDependentField();
		product.addOrUpdateLocaleDependantFields(f);

		final Product savedProduct = saveProduct(product);

		assertSame("Saved product not the same object.", product, savedProduct);
		assertSame("Saved product type not the same object.", product.getProductType(), savedProduct.getProductType());
		assertSame("Saved catalog not the same object.", product.getProductType().getCatalog(), savedProduct.getProductType().getCatalog());
		assertSame("Saved tax code not the same object.", product.getProductType().getTaxCode(), savedProduct.getProductType().getTaxCode());
	}
	
	/**
	 * Tests saving product operation.
	 */
	@DirtiesDatabase
	@Test
	public void testSaveProductWithModificationAtLevel2() {
		final Product product = createSimpleProduct();
		product.getProductType().getCatalog().setCode("bbfdfg");

		final Product savedProduct = saveProduct(product);

		assertSame("Saved product not the same object.", product, savedProduct);
		assertSame("Saved product type not the same object.", product.getProductType(), savedProduct.getProductType());
		assertSame("Saved catalog not the same object.", product.getProductType().getCatalog(), savedProduct.getProductType().getCatalog());
		assertSame("Saved tax code not the same object.", product.getProductType().getTaxCode(), savedProduct.getProductType().getTaxCode());
	}

	/**
	 * Tests saving product operation.
	 */
	@DirtiesDatabase
	@Test
	public void testSaveProductWithModificationAtLevel1And2() {
		final Product product = createSimpleProduct();
		product.getProductType().setDescription("bdlklskf");
		product.getProductType().getCatalog().setCode("bbfdfg");

		final Product savedProduct = saveProduct(product);

		assertSame("Saved product not the same object.", product, savedProduct);
		assertSame("Saved product type not the same object.", product.getProductType(), savedProduct.getProductType());
		assertSame("Saved catalog not the same object.", product.getProductType().getCatalog(), savedProduct.getProductType().getCatalog());
		assertSame("Saved tax code not the same object.", product.getProductType().getTaxCode(), savedProduct.getProductType().getTaxCode());
	}

	/**
	 * Tests retrieving product operation.
	 */
	@DirtiesDatabase
	@Test
	public void testRetrieveProductWithModificationAtLevel2() {
		final Product product = createSimpleProduct();
		product.getProductType().getCatalog().setMaster(true);

		final Product savedProduct = saveProduct(product);

		assertNotNull("Product not retrieved - null", getPersistenceEngine().get(ProductImpl.class, savedProduct.getUidPk()));
		assertNotNull("Product type not retrieved - null", getPersistenceEngine().get(ProductImpl.class, savedProduct.getUidPk()).getProductType());
		assertNotNull("Catalog not retrieved - null", getPersistenceEngine().get(ProductImpl.class, savedProduct.getUidPk()).getProductType()
				.getCatalog());
		assertNotNull("Tax code not retrieved - null", getPersistenceEngine().get(ProductImpl.class, savedProduct.getUidPk()).getProductType()
				.getTaxCode());
	}

	/**
	 * Tests retrieving product operation.
	 */
	@DirtiesDatabase
	@Test
	public void testRetrieveProductWithModificationAtLevel1And2() {
		final Product product = createSimpleProduct();
		product.getProductType().setName("9j90jf0j340fj");
		product.getProductType().getCatalog().setDefaultLocale(Locale.FRANCE);

		final Product savedProduct = saveProduct(product);

		assertNotNull("Product not retrieved - null", getPersistenceEngine().get(ProductImpl.class, savedProduct.getUidPk()));
		assertNotNull("Product type not retrieved - null", getPersistenceEngine().get(ProductImpl.class, savedProduct.getUidPk()).getProductType());
		assertNotNull("Catalog not retrieved - null", getPersistenceEngine().get(ProductImpl.class, savedProduct.getUidPk()).getProductType()
				.getCatalog());
		assertNotNull("Tax code not retrieved - null", getPersistenceEngine().get(ProductImpl.class, savedProduct.getUidPk()).getProductType()
				.getTaxCode());
	}

	/**
	 * Tests retrieving product operation.
	 */
	@DirtiesDatabase
	@Test
	public void testRetrieveProduct() {
		final Product product = createSimpleProduct();

		final Product savedProduct = saveProduct(product);

		assertNotNull("Product not retrieved - null", getPersistenceEngine().get(ProductImpl.class, savedProduct.getUidPk()));
		assertNotNull("Product type not retrieved - null", getPersistenceEngine().get(ProductImpl.class, savedProduct.getUidPk()).getProductType());
		assertNotNull("Catalog not retrieved - null", getPersistenceEngine().get(ProductImpl.class, savedProduct.getUidPk()).getProductType()
				.getCatalog());
		assertNotNull("Tax code not retrieved - null", getPersistenceEngine().get(ProductImpl.class, savedProduct.getUidPk()).getProductType()
				.getTaxCode());
	}

	/**
	 * Tests retrieving product operation.
	 */
	@DirtiesDatabase
	@Test
	public void testRetrieveProductWithMapChange() {
		final Product product = createSimpleProduct();
		LocaleDependantFields ldf = product.getLocaleDependantFieldsWithoutFallBack(Locale.GERMAN);
		product.addOrUpdateLocaleDependantFields(ldf);

		final Product savedProduct = saveProduct(product);

		assertNotNull("Product not retrieved - null", getPersistenceEngine().get(ProductImpl.class, savedProduct.getUidPk()));
		assertNotNull("Product type not retrieved - null", getPersistenceEngine().get(ProductImpl.class, savedProduct.getUidPk()).getProductType());
		assertNotNull("Catalog not retrieved - null", getPersistenceEngine().get(ProductImpl.class, savedProduct.getUidPk()).getProductType()
				.getCatalog());
		assertNotNull("Tax code not retrieved - null", getPersistenceEngine().get(ProductImpl.class, savedProduct.getUidPk()).getProductType()
				.getTaxCode());
	}

	/**
	 * Tests merging product operation.
	 */
	@DirtiesDatabase
	@Test
	public void testMergeProduct() {
		final Product product = createSimpleProduct();

		final Product savedProduct = saveProduct(product);

		final Product retrievedProduct = getProduct(savedProduct);

		final Product mergedProduct = mergeProduct(retrievedProduct);

		assertNotSame("Merged product is the same object.", retrievedProduct, mergedProduct);
		assertNotSame("Merged product type not the same object.", product.getProductType(), mergedProduct.getProductType());
		assertNotSame("Merged catalog not the same object.", product.getProductType().getCatalog(), mergedProduct.getProductType().getCatalog());
		assertNotSame("Merged tax code not the same object.", product.getProductType().getTaxCode(), mergedProduct.getProductType().getTaxCode());
	}

	/**
	 * Tests merging product operation.
	 */
	@DirtiesDatabase
	@Test
	public void testMergeProductwithDateChange() {
		final Product product = createSimpleProduct();
		Date date = new Date();
		product.setEndDate(date);

		final Product savedProduct = saveProduct(product);

		final Product retrievedProduct = getProduct(savedProduct);

		final Product mergedProduct = mergeProduct(retrievedProduct);

		assertNotSame("Merged product is the same object.", retrievedProduct, mergedProduct);
		assertNotSame("Merged product type not the same object.", product.getProductType(), mergedProduct.getProductType());
		assertNotSame("Merged catalog not the same object.", product.getProductType().getCatalog(), mergedProduct.getProductType().getCatalog());
		assertNotSame("Merged tax code not the same object.", product.getProductType().getTaxCode(), mergedProduct.getProductType().getTaxCode());
		assertNotNull(mergedProduct.getEndDate());

		final Product retrievedProduct2 = getProduct(product);
		retrievedProduct2.setEndDate(null);

		final Product mergedProduct2 = mergeProduct(retrievedProduct2);
		assertNull(mergedProduct2.getEndDate());
	}

	/**
	 * Tests merging product operation.
	 */
	@DirtiesDatabase
	@Test
	public void testMergeProductWithMapChange() {
		final Product product = createSimpleProduct();

		final Product savedProduct = saveProduct(product);

		final Product retrievedProduct = getProduct(savedProduct);
		ProductLocaleDependantFieldsImpl f = createDependentField();
		product.addOrUpdateLocaleDependantFields(f);

		final Product mergedProduct = mergeProduct(retrievedProduct);

		assertNotSame("Merged product is the same object.", retrievedProduct, mergedProduct);
		assertNotSame("Merged product type not the same object.", product.getProductType(), mergedProduct.getProductType());
		assertNotSame("Merged catalog not the same object.", product.getProductType().getCatalog(), mergedProduct.getProductType().getCatalog());
		assertNotSame("Merged tax code not the same object.", product.getProductType().getTaxCode(), mergedProduct.getProductType().getTaxCode());
	}

	/**
	 * Tests merging product operation.
	 */
	@DirtiesDatabase
	@Test
	public void testMergeProductWithModificationAtLevel2() {
		final Product product = createSimpleProduct();

		final Product savedProduct = saveProduct(product);

		final Product retrievedProduct = getProduct(savedProduct);
		product.getProductType().setName("sdfsdf");

		final Product mergedProduct = mergeProduct(retrievedProduct);

		assertNotSame("Merged product is the same object.", retrievedProduct, mergedProduct);
		assertNotSame("Merged product type not the same object.", product.getProductType(), mergedProduct.getProductType());
		assertNotSame("Merged catalog not the same object.", product.getProductType().getCatalog(), mergedProduct.getProductType().getCatalog());
		assertNotSame("Merged tax code not the same object.", product.getProductType().getTaxCode(), mergedProduct.getProductType().getTaxCode());
	}

	/**
	 * Tests merging product operation.
	 */
	@DirtiesDatabase
	@Test
	public void testMergeProductWithModificationAtLevel1And2() {
		final Product product = createSimpleProduct();

		final Product savedProduct = saveProduct(product);

		final Product retrievedProduct = getProduct(savedProduct);
		product.getProductType().setName("sdsdfsdffsdf");
		product.getProductType().getTaxCode().setCode("sdlvsdklfjslkfjwef");

		final Product mergedProduct = mergeProduct(retrievedProduct);

		assertNotSame("Merged product is the same object.", retrievedProduct, mergedProduct);
		assertNotSame("Merged product type not the same object.", product.getProductType(), mergedProduct.getProductType());
		assertNotSame("Merged catalog not the same object.", product.getProductType().getCatalog(), mergedProduct.getProductType().getCatalog());
		assertNotSame("Merged tax code not the same object.", product.getProductType().getTaxCode(), mergedProduct.getProductType().getTaxCode());
	}

	/**
	 * Tests merging product operation.
	 */
	@DirtiesDatabase
	@Test
	public void testMergeProductWithPersistedObjectChange() {
		final Product product = createSimpleProduct();

		final Product savedProduct = saveProduct(product);

		final Product retrievedProduct = getProduct(savedProduct);
		product.getProductType().setCatalog(createPersistedCatalog("sdfsdfkljsefoij"));

		final Product mergedProduct = mergeProduct(retrievedProduct);

		assertNotSame("Merged product is the same object.", retrievedProduct, mergedProduct);
		assertNotSame("Merged product type not the same object.", product.getProductType(), mergedProduct.getProductType());
		assertNotSame("Merged catalog not the same object.", product.getProductType().getCatalog(), mergedProduct.getProductType().getCatalog());
		assertNotSame("Merged tax code not the same object.", product.getProductType().getTaxCode(), mergedProduct.getProductType().getTaxCode());
	}
	
	/**
	 * Test setting category operation.
	 */
	@DirtiesDatabase
	@Test
	public void testSetCategories() {
		final Product product = createSimpleProduct();

		product.setCategoryAsDefault(createCategory(product.getProductType().getCatalog()));

		final Product savedProduct = saveProduct(product);

		assertEquals(1, savedProduct.getCategories().size());

		final Product retrievedProduct = getProduct(savedProduct);
		product.getProductType().setCatalog(createPersistedCatalog("sdfsdfkljsefoij"));

		assertEquals(1, retrievedProduct.getCategories().size());
	}

	private Category createCategory(final Catalog catalog) {
		final Category category = new CategoryImpl();
		category.initialize();
		category.setStartDate(new Date());
		category.setCategoryType(createCategoryType(catalog));
		category.setCode(Utils.uniqueCode("cat_code"));
		category.setCatalog(catalog);

		saveCategory(catalog, category);
		return category;
	}

	@Override
	public CategoryType createCategoryType(final Catalog catalog) {
		final CategoryType catType = new CategoryTypeImpl();

		catType.setCatalog(catalog);
		catType.setName(Utils.uniqueCode("cat_name"));
		catType.setTemplate("templ");
		catType.setGuid(Utils.uniqueCode("guid"));

		saveCategoryType(catalog, catType);
		return catType;
	}

	private void saveCategory(final Catalog catalog, final Category category) {
		doInTransaction(new TransactionCallback<Category>() {
			public Category doInTransaction(final TransactionStatus status) {
				getPersistenceEngine().save(category);
				return category;
			}
		});
	}

	private void saveCategoryType(final Catalog catalog,
			final CategoryType catType) {
		doInTransaction(new TransactionCallback<CategoryType>() {
			public CategoryType doInTransaction(final TransactionStatus status) {
				getPersistenceEngine().save(catType);
				return catType;
			}
		});
			}

	private Product saveProduct(final Product product) {
		final Product savedProduct = getTxTemplate().execute(new TransactionCallback<Product>() {
			public Product doInTransaction(final TransactionStatus status) {
				getPersistenceEngine().save(product);
				return product;
			}
		});
		return savedProduct;
	}

	private Product getProduct(final Product product) {
		final Product retrievedProduct = getTxTemplate().execute(new TransactionCallback<Product>() {
			public Product doInTransaction(final TransactionStatus status) {
				return getPersistenceEngine().get(ProductImpl.class, product.getUidPk());
			}
		});
		return retrievedProduct;
	}

	private Product mergeProduct(final Product retrievedProduct) {
		final Product mergedProduct = getTxTemplate().execute(new TransactionCallback<Product>() {
			public Product doInTransaction(final TransactionStatus status) {
				return getPersistenceEngine().merge(retrievedProduct);
			}
		});
		return mergedProduct;
	}
}
