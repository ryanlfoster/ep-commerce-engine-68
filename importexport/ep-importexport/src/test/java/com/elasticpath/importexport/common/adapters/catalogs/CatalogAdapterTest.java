package com.elasticpath.importexport.common.adapters.catalogs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.ElasticPath;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.importexport.common.caching.CachingService;
import com.elasticpath.importexport.common.dto.catalogs.CatalogDTO;
import com.elasticpath.importexport.common.dto.catalogs.CatalogType;
import com.elasticpath.importexport.common.exception.runtime.PopulationRollbackException;

/**
 * Verify that CatalogAdapter populates catalog domain object from DTO properly and vice versa.
 * Nested adapters should be tested separately.
 */
public class CatalogAdapterTest {

	private static final String CATALOG_CODE = "CatalogCode";

	private static final String CATALOG_NAME = "CatalogName";

	private static final CatalogType CATALOG_TYPE = CatalogType.master;

	private static final String CATALOG_DEFAULT_LANGUAGE = "en";

	private static final List<String> CATALOG_LANGUAGES = Arrays.asList(CATALOG_DEFAULT_LANGUAGE, "fr");

	private static final Locale CATALOG_DEFAULT_LOCALE = new Locale(CATALOG_DEFAULT_LANGUAGE);

	private static final List<Locale> CATALOG_SUPPORTED_LOCALES = Arrays.asList(CATALOG_DEFAULT_LOCALE, new Locale("fr"));

	private CatalogDTO catalogDto;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private Catalog mockCatalogDomain;

	private CachingService mockCachingService;

	private ElasticPath mockElasticPath;

	private CatalogAdapter catalogAdapter;

	@Before
	public void setUp() throws Exception {
		mockElasticPath = context.mock(ElasticPath.class);

		catalogDto = new CatalogDTO();
		mockCatalogDomain = context.mock(Catalog.class);
		mockCachingService = context.mock(CachingService.class);

		catalogAdapter = new CatalogAdapter();
		catalogAdapter.setElasticPath(mockElasticPath);
		catalogAdapter.setCachingService(mockCachingService);

		context.checking(new Expectations() {
			{
				allowing(mockElasticPath).getBean(ContextIdNames.CATALOG);
				will(returnValue(mockCatalogDomain));
			}
		});
	}

	private void setUpDefaultDto() {
		catalogDto.setCode(CATALOG_CODE);
		catalogDto.setName(CATALOG_NAME);
		catalogDto.setType(CATALOG_TYPE);
		catalogDto.setDefaultLanguage(CATALOG_DEFAULT_LANGUAGE);
		catalogDto.setLanguages(CATALOG_LANGUAGES);
	}

	private void setDefaultDomainExpectationsOnSet() {
		context.checking(new Expectations() {
			{
				oneOf(mockCatalogDomain).setCode(CATALOG_CODE);
				oneOf(mockCatalogDomain).setName(CATALOG_NAME);
				oneOf(mockCatalogDomain).setMaster(CatalogType.isMaster(CATALOG_TYPE));
				oneOf(mockCatalogDomain).setDefaultLocale(CATALOG_DEFAULT_LOCALE);
			}
		});
	}

	private void setDefaultDomainExpectationsOnGet() {
		context.checking(new Expectations() {
			{
				oneOf(mockCatalogDomain).getCode();
				will(returnValue(CATALOG_CODE));
				oneOf(mockCatalogDomain).getName();
				will(returnValue(CATALOG_NAME));
				oneOf(mockCatalogDomain).getDefaultLocale();
				will(returnValue(CATALOG_DEFAULT_LOCALE));
			}
		});
	}

	private void setDefaultDomainExpectationsOnGetForMasterCatalog() {
		setDefaultDomainExpectationsOnGet();
		context.checking(new Expectations() {
			{
				oneOf(mockCatalogDomain).getSupportedLocales();
				will(returnValue(CATALOG_SUPPORTED_LOCALES));
			}
		});
	}

	private void setDefaultDomainExpectationsOnAdd() {
		context.checking(new Expectations() {
			{
				oneOf(mockCatalogDomain).addSupportedLocale(CATALOG_SUPPORTED_LOCALES.get(0));
				oneOf(mockCatalogDomain).addSupportedLocale(CATALOG_SUPPORTED_LOCALES.get(1));
			}
		});
	}

	/**
	 * Check that all required fields for domain object are being set during domain population.
	 */
	@Test
	public void testPopulateDomain() {
		setUpDefaultDto();
		setDefaultDomainExpectationsOnSet();
		setDefaultDomainExpectationsOnAdd();

		catalogAdapter.populateDomain(catalogDto, mockCatalogDomain);
	}

	/**
	 * Check that populateDomain Throws PopulateRuntimeException.
	 */
	@Test(expected = PopulationRollbackException.class)
	public void testPopulateDomainThrowsOnWrongLocale() {
		setUpDefaultDto();

		context.checking(new Expectations() {
			{
				allowing(mockCatalogDomain).setCode(CATALOG_CODE);
				allowing(mockCatalogDomain).setName(CATALOG_NAME);
				allowing(mockCatalogDomain).setMaster(CatalogType.isMaster(CATALOG_TYPE));
			}
		});

		catalogDto.setDefaultLanguage("zu");

		catalogAdapter.populateDomain(catalogDto, mockCatalogDomain);
	}

	/**
	 * Check that populateDomain adds Error to ErrorCollector.
	 */
	@Test
	public void testPopulateDomainAddsErrorOnLocale() {
		setUpDefaultDto();

		setDefaultDomainExpectationsOnSet();
		setDefaultDomainExpectationsOnAdd();


		List<String> languages = new ArrayList<String>(CATALOG_LANGUAGES);
		languages.add("zu");

		catalogDto.setLanguages(languages);

		catalogAdapter.populateDomain(catalogDto, mockCatalogDomain);
	}

	/**
	 * Check that all required fields for Master Catalog DTO object are being set during DTO population.
	 */
	@Test
	public void testPopulateMasterCatalogDTO() {
		context.checking(new Expectations() {
			{
				allowing(mockCatalogDomain).isMaster();
				will(returnValue(true));
			}
		});
		setDefaultDomainExpectationsOnGetForMasterCatalog();

		CatalogDTO catalogDto = catalogAdapter.createDtoObject();

		catalogAdapter.populateDTO(mockCatalogDomain, catalogDto);

		assertEquals(CATALOG_CODE, catalogDto.getCode());
		assertEquals(CATALOG_NAME, catalogDto.getName());
		assertEquals(CATALOG_TYPE, catalogDto.getType());
		assertEquals(CATALOG_DEFAULT_LANGUAGE, catalogDto.getDefaultLanguage());
		assertEquals(CATALOG_LANGUAGES, catalogDto.getLanguages());
	}

	/**
	 * Check that all required fields for Virtual Catalog DTO object are being set during DTO population.
	 */
	@Test
	public void testPopulateVirtualCatalogDTO() {
		context.checking(new Expectations() {
			{
				allowing(mockCatalogDomain).isMaster();
				will(returnValue(false));
			}
		});
		setDefaultDomainExpectationsOnGet();

		CatalogDTO catalogDto = catalogAdapter.createDtoObject();

		catalogAdapter.populateDTO(mockCatalogDomain, catalogDto);

		assertEquals(CATALOG_CODE, catalogDto.getCode());
		assertEquals(CATALOG_NAME, catalogDto.getName());
		assertEquals(CatalogType.virtual, catalogDto.getType());
		assertEquals(CATALOG_DEFAULT_LANGUAGE, catalogDto.getDefaultLanguage());
	}

	/**
	 * Check that CreateDtoObject works.
	 */
	@Test
	public void testCreateDtoObject() {
		assertNotNull(catalogAdapter.createDtoObject());
	}

	/**
	 * Check that createDomainObject works.
	 */
	@Test
	public void testCreateDomainObject() {
		assertNotNull(catalogAdapter.createDomainObject());
	}
}
