package com.elasticpath.test.db;

import static org.junit.Assert.fail;

import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.persistence.EntityManager;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.CategoryType;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.catalog.impl.CatalogImpl;
import com.elasticpath.domain.catalog.impl.CategoryImpl;
import com.elasticpath.domain.catalog.impl.CategoryTypeImpl;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.catalog.impl.ProductLocaleDependantFieldsImpl;
import com.elasticpath.domain.catalog.impl.ProductTypeImpl;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.StoreType;
import com.elasticpath.domain.store.impl.StoreImpl;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.persistence.openjpa.impl.JpaSessionImpl;
import com.elasticpath.test.integration.BasicSpringContextTest;
import com.elasticpath.test.persister.TestDataPersisterFactory;
import com.elasticpath.test.persister.testscenarios.SimpleStoreScenario;
import com.elasticpath.test.util.Utils;

/**
 * DB tests specific class. Derivatives doesn't make use of service methods call, but in contrast use persistent engine directly.
 */
/* @Ignore - no longer necessary. Surefire only picks up *Test.java so this test will never be picked up */
@SuppressWarnings({ "PMD.TestClassWithoutTestCases", "PMD.AbstractClassWithoutAbstractMethod", "PMD.AbstractNaming" })
public abstract class DbTestCase extends BasicSpringContextTest {

	private static final Logger LOG = Logger.getLogger(DbTestCase.class);

	/** Store scenario. */
	protected SimpleStoreScenario scenario;

	/** Data persister Factory. */
	@Autowired
	protected TestDataPersisterFactory persisterFactory;

	@Autowired
	private PersistenceEngine persistenceEngine;

	@Rule
	public TestName name = new TestName();

	/**
	 * Get a reference to TestApplicationContext for use within the test. Setup scenarios.
	 *
	 * @throws Exception in case of any errors.
	 */
	@Before
	public void setUpDb() throws Exception {
		LOG.trace(String.format("%s setting up db...", name.getMethodName()));

		List<StoreImpl> stores = persistenceEngine.retrieve("SELECT s FROM StoreImpl s ");
		if (!stores.isEmpty()) {
			fail(String.format("Found [%s] stores before the test started.  Bailing out: [%s]", stores.size(), StringUtils.join(stores, ",")));
		}

		setScenario(getTac().useScenario(SimpleStoreScenario.class));
	}

	/**
	 * Call to do something with the persistence engine or test setup/verification.
	 *
	 * @param callBack call back.
	 * @return result object.
	 */
	protected <T> T doInTransaction(final TransactionCallback<T> callBack) {
		return getTxTemplate().execute(callBack);
	}

	/**
	 * Creates a new product type, tax code and then returns a new product attached to those new objects.
	 *
	 * @return created product.
	 */
	protected Product createSimpleProduct() {
		return createSimpleProduct(createPersistedCatalog("Svsdvsdv"), createPersistedTaxCode());
	}

	/**
	 * Creates a catalog using default values.
	 *
	 * @return The newly persisted catalog.
	 */
	public Catalog createCatalog() {
		return createCatalog("Test Catalog", Locale.getDefault(), Currency.getInstance(Locale.CANADA));
	}

	/**
	 * Creates a catalog using the given values.
	 *
	 * @param name The catalog name.
	 * @param defaultLocale The catalog locale.
	 * @param defaultCurrency The catalog default currency.
	 * @return The newly persisted catalog.
	 */
	public Catalog createCatalog(final String name, final Locale defaultLocale, final Currency defaultCurrency) {
		final Catalog catalog = new CatalogImpl();
		catalog.setCode(Utils.uniqueCode("catalog"));
		catalog.setDefaultLocale(defaultLocale);
		catalog.setName(name);

		getTxTemplate().execute(new TransactionCallback<Catalog>() {
			@Override
			public Catalog doInTransaction(final TransactionStatus arg0) {
				getPersistenceEngine().save(catalog);
				return catalog;
			}
		});

		return catalog;
	}

	/**
	 * Creates a simple store.
	 *
	 * @return The store created using default values.
	 */
	public Store createStore() {
		String country = "Canada";
		String name = "Store 1";
		StoreType storeType = StoreType.B2B;
		String subCountry = "Canada";
		String url = "http://www.some-where-out-there.com";
		TimeZone timeZone = TimeZone.getDefault();
		String code = "store";
		String emailSenderName = "some name";
		String emailSenderAddress = "some@name.com";
		String storeAdminEmailAddress = "admin@test.com";
		Catalog catalog = createCatalog();
		Locale defaultLocale = Locale.getDefault();
		Currency defaultCurrency = Currency.getInstance(Locale.CANADA);

		return createStore(country, name, storeType, subCountry, url, timeZone, code,
				emailSenderName, emailSenderAddress, storeAdminEmailAddress, catalog, defaultLocale, defaultCurrency);
	}

	/**
	 * Creates simple store.
	 *
	 * @param country The store country.
	 * @param name The store name.
	 * @param storeType The StoreType value.
	 * @param subCountry The province or state.
	 * @param url The store URL.
	 * @param timeZone The store time zone.
	 * @param code The unique store code.
	 * @param emailSenderName The email sender name.
	 * @param emailSenderAddress The email sender address.
	 * @param storeAdminEmailAddress The admin email address.
	 * @param catalog The store default catalog.
	 * @param defaultLocale The default locale.
	 * @param defaultCurrency The default currency of the store.
	 * @return The newly persisted store.
	 */
	public Store createStore(final String country, final String name, final StoreType storeType,
			final String subCountry, final String url, final TimeZone timeZone, final String code, final String emailSenderName, final String emailSenderAddress, final String storeAdminEmailAddress,
			final Catalog catalog, final Locale defaultLocale, final Currency defaultCurrency) {

		final Store store = getBeanFactory().getBean(ContextIdNames.STORE);
		store.setCountry(country);
		store.setName(name);
		store.setStoreType(storeType);
		store.setSubCountry(subCountry);
		store.setUrl(url);
		store.setTimeZone(timeZone);
		store.setCode(Utils.uniqueCode(code));
		store.setEmailSenderName(emailSenderName);
		store.setEmailSenderAddress(emailSenderAddress);
		store.setStoreAdminEmailAddress(storeAdminEmailAddress);
		store.setCatalog(catalog);
		store.setDefaultLocale(defaultLocale);
		store.setDefaultCurrency(defaultCurrency);

		return store;
	}

	/**
	 * Creates a new category.
	 *
	 * @return The newly created category.
	 */
	public Category createCategory(final Catalog catalog, final CategoryType categoryType) {
		final Category category = new CategoryImpl();
		category.initialize();
		category.setStartDate(new Date());
		category.setCategoryType(categoryType);
		category.setCode(Utils.uniqueCode("cat_code"));
		category.setCatalog(catalog);

		doInTransaction(new TransactionCallback<Category>() {

			@Override
			public Category doInTransaction(final TransactionStatus arg0) {
				getPersistenceEngine().save(category);
				return null;
			}

		});

		return category;
	}	
	
	/**
	 * Creates a new category type.
	 *
	 * @return The new category type.
	 */
	public CategoryType createCategoryType(final Catalog catalog) {
		final CategoryType catType = new CategoryTypeImpl();

		catType.setCatalog(catalog);
		catType.setName(Utils.uniqueCode("cat_name"));
		catType.setTemplate("templ");
		catType.setGuid(Utils.uniqueCode("guid"));

		doInTransaction(new TransactionCallback<CategoryType>() {
			@Override
			public CategoryType doInTransaction(final TransactionStatus arg0) {
				EntityManager entityManager = ((JpaSessionImpl) getPersistenceEngine().getPersistenceSession()).getEntityManager();
				if (!entityManager.contains(catType.getCatalog())) {
					catType.setCatalog(entityManager.find(CatalogImpl.class, catType.getCatalog().getUidPk()));
				}
				getPersistenceEngine().save(catType);
				return null;
			}
		});

		return catType;
	}

	/**
	 * Creates a new product.
	 *
	 * @param destCatalog catalog.
	 * @param taxCode tax code.
	 * @return created product.
	 */
	protected Product createSimpleProduct(final Catalog destCatalog, final TaxCode taxCode) {
		final Product product = new ProductImpl();
		product.initialize();
		product.setLastModifiedDate(new Date());
		product.setStartDate(new Date());
		product.setCode(Utils.uniqueCode("product"));

		ProductLocaleDependantFieldsImpl f = createDependentField();
		f.setLocale(Locale.ENGLISH);
		product.addOrUpdateLocaleDependantFields(f);

		ProductType productType = new ProductTypeImpl();
		productType.setName(Utils.uniqueCode("productName"));
		productType.initialize();
		productType.setCatalog(destCatalog);
		productType.setTemplate("template1");
		productType.setTaxCode(taxCode);

		productType = persist(productType);

		productType = getPersistenceEngine().load(ProductTypeImpl.class, productType.getUidPk());
		product.setProductType(productType);

		return product;
	}

	private ProductType persist(final ProductType productType) {
		// We don't cascade persist onto product types, so make sure it exists.
		return getTxTemplate().execute(new TransactionCallback<ProductType>() {
			@Override
			public ProductType doInTransaction(final TransactionStatus arg0) {
				return getPersistenceEngine().saveOrUpdate(productType);
			}
		});
	}

	/**
	 * Creates tax code.
	 *
	 * @return tax code.
	 */
	protected TaxCode createPersistedTaxCode() {
		return createPersistedTaxCode(Utils.uniqueCode("tax"));
	}

	/**
	 * Creates and persists tax code.
	 *
	 * @param code code as String.
	 * @return persisted tax code.
	 */
	protected TaxCode createPersistedTaxCode(final String code) {
		final TaxCode taxCode = new com.elasticpath.domain.tax.impl.TaxCodeImpl();
		taxCode.initialize();
		taxCode.setCode(code);

		// We don't cascade persist from product onto tax codes, so make sure it exists
		getTxTemplate().execute(new TransactionCallback<TaxCode>() {
			@Override
			public TaxCode doInTransaction(final TransactionStatus arg0) {
				getPersistenceEngine().save(taxCode);
				return null;
			}
		});

		return taxCode;
	}

	/**
	 * Creates and persists catalog.
	 *
	 * @param code catalog code as String.
	 * @return persisted catalog.
	 */
	protected Catalog createPersistedCatalog(final String code) {
		final Catalog catalog = new CatalogImpl();
		catalog.setMaster(true);
		catalog.setCode(Utils.uniqueCode("catalog"));
		catalog.addSupportedLocale(Locale.JAPANESE);
		catalog.setDefaultLocale(Locale.getDefault());
//		catalog.setDefaultCurrency(Currency.getInstance(Locale.CANADA));
		catalog.setName(catalog.getCode());

		// We don't cascade persist from product onto catalog, so make sure it exists
		getTxTemplate().setPropagationBehaviorName(DefaultTransactionDefinition.PREFIX_PROPAGATION + Propagation.REQUIRES_NEW.name());
		getTxTemplate().execute(new TransactionCallback<Catalog>() {
			@Override
			public Catalog doInTransaction(final TransactionStatus arg0) {
				getPersistenceEngine().save(catalog);
				return catalog;
			}
		});

		return catalog;
	}

	/**
	 * Creates product locale dependent fields.
	 *
	 * @return product locale dependent fields.
	 */
	protected ProductLocaleDependantFieldsImpl createDependentField() {
		ProductLocaleDependantFieldsImpl field = new ProductLocaleDependantFieldsImpl();
		field.setLocale(Locale.US);
		return field;
	}

	/**
	 * @return the txTemplate
	 */
	protected TransactionTemplate getTxTemplate() {
		return getTac().getTxTemplate();
	}

	/**
	 * @param scenario the scenario to set
	 */
	protected void setScenario(final SimpleStoreScenario scenario) {
		this.scenario = scenario;
	}

	/**
	 * @return the scenario
	 */
	protected SimpleStoreScenario getScenario() {
		return scenario;
	}

	/**
	 * @return the persisterFactory
	 */
	protected TestDataPersisterFactory getPersisterFactory() {
		return getTac().getPersistersFactory();
	}

	/**
	 * @return the persistenceEngine
	 */
	protected PersistenceEngine getPersistenceEngine() {
		return persistenceEngine;
	}
}
