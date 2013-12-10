package com.elasticpath.domain.dataimport.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.jmock.Expectations;
import org.junit.Test;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.exception.EpInvalidGuidBindException;
import com.elasticpath.commons.exception.EpUnsupportedOperationException;
import com.elasticpath.commons.util.Utility;
import com.elasticpath.commons.util.impl.UtilityImpl;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.impl.CatalogImpl;
import com.elasticpath.domain.catalog.impl.CategoryImpl;
import com.elasticpath.domain.catalog.impl.PriceTierImpl;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.dataimport.CatalogImportField;
import com.elasticpath.domain.dataimport.ImportField;
import com.elasticpath.domain.misc.impl.RandomGuidImpl;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.service.dataimport.ImportGuidHelper;
import com.elasticpath.test.jmock.AbstractEPTestCase;

/**
 * Test <code>ImportDataTypeProductPriceImpl</code>.
 */
public class ImportDataTypeProductCategoryAssociationImplTest extends AbstractEPTestCase {

	private ImportDataTypeProductCategoryAssociationImpl productCategoryAssociationImportType;

	private ImportGuidHelper mockImportGuidHelper;

	private ImportGuidHelper importGuidHelper;

	private ProductCategoryImportBean productCategory;

	private static final String CATEGORY_GUID = "category Guid";

	private Catalog masterCatalog;

	private final Utility utility = new UtilityImpl() {
		private static final long serialVersionUID = -758690347387824231L;

		/**
		 * Override for test.
		 * @return pattern
		 */
		@Override
		protected String getDefaultDateFormatPattern() {
			return "EEE MMM dd HH:mm:ss z yyyy";
		}
	};



	/**
	 * Prepare for tests.
	 *
	 * @throws Exception in case of error happens.
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();

		this.productCategoryAssociationImportType = new ImportDataTypeProductCategoryAssociationImpl();

		stubGetBean(ContextIdNames.PRICE_TIER, PriceTierImpl.class);
		stubGetBean(ContextIdNames.UTILITY, utility);

		this.productCategory = new ProductCategoryImportBean();


		// Setup ImportService.
		setupImportService();

		productCategoryAssociationImportType.init(null);
	}

	private void setupImportService() {
		this.mockImportGuidHelper = context.mock(ImportGuidHelper.class);
		this.importGuidHelper = this.mockImportGuidHelper;

	}

	/**
	 * Test method init().
	 */
	@Test
	public void testInitError() {
		this.productCategoryAssociationImportType.init(null);

		try {
			this.productCategoryAssociationImportType.init(new Object());
			fail("EpDomainException expected.");
		} catch (final EpDomainException e) {
			// succeed
			assertNotNull(e);
		}
	}

	/**
	 * Test method getRequiredImportFields.
	 */
	@Test
	public void testGetRequiredImportFields() {
		List<ImportField> requiredImportFields = this.productCategoryAssociationImportType.getRequiredImportFields();
		for (final ImportField importField : requiredImportFields) {
			assertTrue(importField.isRequired());
		}
	}

	/**
	 * Test method getOptionalImportFields.
	 */
	@Test
	public void testGetOptionalImportFields() {
		List<ImportField> requiredImportFields = this.productCategoryAssociationImportType.getOptionalImportFields();
		for (final ImportField importField : requiredImportFields) {
			assertFalse(importField.isRequired());
		}
	}

	/**
	 * Test method getImportField.
	 */
	@Test
	public void testGetImportFieldOfProductGuid() {
		ImportField importField = this.productCategoryAssociationImportType.getImportField("productCode");
		assertTrue(importField.isRequired());

		// No error if the given guid is a valid one.
		context.checking(new Expectations() {
			{
				allowing(mockImportGuidHelper).isProductGuidExist("validGuid");
				will(returnValue(true));
			}
		});
		importField.setStringValue(productCategory, "validGuid", importGuidHelper);

		// Exception expected if the given guid is an invalid one.
		context.checking(new Expectations() {
			{
				allowing(mockImportGuidHelper).isProductGuidExist("invalidGuid");
				will(returnValue(false));
			}
		});
		try {
			importField.setStringValue(productCategory, "invalidGuid", importGuidHelper);
			fail("EpInvalidGuidBindException expected.");
		} catch (EpInvalidGuidBindException e) {
			// succeed!
			assertNotNull(e);
		}

		// The getStringValue() method is not supportted.
		try {
			importField.getStringValue(productCategory);
			fail("EpUnsupportedOperationException expected!");
		} catch (EpUnsupportedOperationException e) {
			// succeed!
			assertNotNull(e);
		}
	}

	/**
	 * Test method getImportField.
	 */
	@Test
	public void testGetImportFieldOfCategoryGuid() {
		final CatalogImportField importField = (CatalogImportField) this.productCategoryAssociationImportType.
			getImportField("categoryCode");
		importField.setCatalog(new CatalogImpl());

		final Category category = getCategory();
		category.setGuid(CATEGORY_GUID);
		context.checking(new Expectations() {
			{
				allowing(mockImportGuidHelper).findCategoryByGuidAndCatalogGuid(
						with(category.getGuid()), with(aNull(String.class)), with(any(boolean.class)), with(any(boolean.class)));
				will(returnValue(category));
			}
		});

		importField.setStringValue(productCategory, CATEGORY_GUID, importGuidHelper);
		assertEquals(CATEGORY_GUID, importField.getStringValue(productCategory));
	}

	/**
	 * Test method getImportField.
	 */
	@Test
	public void testGetImportFieldOfFeaturedProductOrder() {
		final ImportField importField = this.productCategoryAssociationImportType.getImportField(
				ImportDataTypeProductCategoryAssociationImpl.PREFIX_OF_FIELD_NAME + "featuredProductOrder");

		final String newFeaturedProductOrder = "2";
		importField.setStringValue(productCategory, newFeaturedProductOrder, importGuidHelper);
		assertEquals(String.valueOf(Integer.parseInt(newFeaturedProductOrder)),
				importField.getStringValue(productCategory));
	}


	/**
	 * Test method createValueObject().
	 */
	@Test
	public void testCreateValueObject() {
		final Persistable valueObject = this.productCategoryAssociationImportType.createValueObject();
		assertNotNull(valueObject);
		assertTrue(valueObject instanceof ProductCategoryImportBean);
	}

	/**
	 * Test method saveOrUpdateValueObject().
	 */
	@Test
	public void testSaveOrUpdateValueObject() {
		final Product productImpl = new ProductImpl();
		productImpl.initialize();
		productImpl.setGuid("productGuid");
		assertNotNull(productImpl.getCategories());
		assertEquals(0, productImpl.getCategories().size());

		final CatalogImportField importField = (CatalogImportField) this.productCategoryAssociationImportType.
			getImportField("categoryCode");
		importField.setCatalog(new CatalogImpl());

		// create a category
		final String categoryGuid = "category Guid";
		final Category category = getCategory();
		category.setGuid(categoryGuid);
		context.checking(new Expectations() {
			{
				allowing(mockImportGuidHelper).findCategoryByGuidAndCatalogGuid(
						with(category.getGuid()), with(aNull(String.class)), with(any(boolean.class)), with(any(boolean.class)));
				will(returnValue(category));
			}
		});
		importField.setStringValue(productCategory, categoryGuid, importGuidHelper);

		this.productCategoryAssociationImportType.saveOrUpdate(productImpl, productCategory);
		assertNotNull(productImpl.getCategories());
		assertEquals(1, productImpl.getCategories().size());
		assertTrue(productImpl.getCategories().contains(productCategory.getCategory()));

		// save again
		this.productCategoryAssociationImportType.saveOrUpdate(productImpl, productCategory);
		assertEquals(1, productImpl.getCategories().size());
		assertTrue(productImpl.getCategories().contains(productCategory.getCategory()));

		// create another category
		final String anotherCategoryGuid = "category Guid 2";
		final Category anotherCategory = getCategory();
		anotherCategory.setGuid(anotherCategoryGuid);
		context.checking(new Expectations() {
			{
				allowing(mockImportGuidHelper).findCategoryByGuidAndCatalogGuid(
						with(anotherCategory.getGuid()), with(aNull(String.class)), with(any(boolean.class)), with(any(boolean.class)));
				will(returnValue(anotherCategory));
			}
		});

		final ProductCategoryImportBean anotherProductCategory =
				(ProductCategoryImportBean) this.productCategoryAssociationImportType.createValueObject();
		importField.setStringValue(anotherProductCategory, anotherCategoryGuid, importGuidHelper);

		// save another category
		this.productCategoryAssociationImportType.saveOrUpdate(productImpl, anotherProductCategory);
		assertEquals(2, productImpl.getCategories().size());
		assertTrue(productImpl.getCategories().contains(productCategory.getCategory()));
		assertTrue(productImpl.getCategories().contains(anotherProductCategory.getCategory()));
	}

	/**
	 * Test method clearValueObject().
	 * All
	 */
	@Test
	public void testClearValueObjects() {
		final Product productImpl = new ProductImpl();
		productImpl.initialize();
		productImpl.setGuid("productGuid");
		assertNotNull(productImpl.getCategories());
		assertEquals(0, productImpl.getCategories().size());

		final CatalogImportField importField = (CatalogImportField) this.productCategoryAssociationImportType.
			getImportField("categoryCode");
		importField.setCatalog(new CatalogImpl());

		// create a category
		final String categoryGuid = "category Guid";
		final Category category = getCategory();
		category.setGuid(categoryGuid);
		context.checking(new Expectations() {
			{
				allowing(mockImportGuidHelper).findCategoryByGuidAndCatalogGuid(
						with(category.getGuid()), with(aNull(String.class)), with(any(boolean.class)), with(any(boolean.class)));
				will(returnValue(category));
			}
		});
		importField.setStringValue(productCategory, categoryGuid, importGuidHelper);

		this.productCategoryAssociationImportType.saveOrUpdate(productImpl, productCategory);
		assertNotNull(productImpl.getCategories());
		assertEquals(1, productImpl.getCategories().size());
		assertTrue(productImpl.getCategories().contains(productCategory.getCategory()));

		// create another category
		final String anotherCategoryGuid = "category Guid 2";
		final Category anotherCategory = getCategory();
		anotherCategory.setGuid(anotherCategoryGuid);
		context.checking(new Expectations() {
			{
				allowing(mockImportGuidHelper).findCategoryByGuidAndCatalogGuid(
						with(anotherCategory.getGuid()), with(aNull(String.class)), with(any(boolean.class)), with(any(boolean.class)));
				will(returnValue(anotherCategory));
			}
		});

		final ProductCategoryImportBean anotherProductCategory =
				(ProductCategoryImportBean) this.productCategoryAssociationImportType.createValueObject();
		importField.setStringValue(anotherProductCategory, anotherCategoryGuid, importGuidHelper);

		// save another category
		this.productCategoryAssociationImportType.saveOrUpdate(productImpl, anotherProductCategory);

		this.productCategoryAssociationImportType.clearValueObjects(productImpl);
		assertNotNull(productImpl.getCategories());
		assertEquals(1, productImpl.getCategories().size());
		assertNotNull("Default category should always be kept", productImpl.getDefaultCategory(anotherCategory.getCatalog()));
	}

	/**
	 * @return a new <code>Category</code> instance.
	 */
	protected Category getCategory() {
		final Category category = new CategoryImpl();
		category.initialize();
		category.setCode((new RandomGuidImpl()).toString());
		category.setCatalog(getCatalog());

		return category;
	}

	/**
	 * @return the master catalog singleton
	 */
	protected Catalog getCatalog() {
		if (masterCatalog == null) {
			masterCatalog = new CatalogImpl();
			masterCatalog.setMaster(true);
			masterCatalog.setCode("Irrelevant catalog code");
		}
		return masterCatalog;
	}
}
