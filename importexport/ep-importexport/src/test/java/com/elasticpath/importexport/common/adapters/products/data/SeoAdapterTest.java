package com.elasticpath.importexport.common.adapters.products.data;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


import com.elasticpath.common.dto.DisplayValue;
import com.elasticpath.domain.catalog.LocaleDependantFields;
import com.elasticpath.domain.catalog.ObjectWithLocaleDependantFields;
import com.elasticpath.importexport.common.dto.products.SeoDTO;
import com.elasticpath.importexport.common.exception.runtime.PopulationRuntimeException;

/**
 * Verify that SeoAdapter populates category domain object from DTO properly and vice versa.
 * <br>Nested adapters should be tested separately.
 */
public class SeoAdapterTest {

	private static final String ZUZU_TITLE = "zuzuTitle";

	private static final String LANGUAGE_ZUZU = "zuzu";

	private static final String LANGUAGE_EN = "en";

	private static final String SEO_TITLE = "seoTitle";

	private static final String SEO_KEY_WORDS = "seoKeyWords";

	private static final String SEO_DESCRIPTION = "seoDescription";

	private static final String SEO_URL = "seoURL";

	private static final Locale LOCALE_EN = new Locale(LANGUAGE_EN);

	private static final Collection<Locale> SUPPORTED_LOCALES = Arrays.asList(LOCALE_EN);

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private ObjectWithLocaleDependantFields mockObjectWithLocaleDependantFields;

	private LocaleDependantFields mockLocaleDependantFields;

	private SeoAdapter seoAdapter;

	@Before
	public void setUp() throws Exception {
		mockObjectWithLocaleDependantFields = context.mock(ObjectWithLocaleDependantFields.class);
		mockLocaleDependantFields = context.mock(LocaleDependantFields.class);

		seoAdapter = new SeoAdapter();
	}

	/**
	 * Test that AbstractSeoPopulateDomainStrategy.populateDomain works properly.
	 */
	@Test(expected = PopulationRuntimeException.class)
	public void testPopulateDomainOnAbstractSeoPopulateDomainStrategy() {
		SeoAdapter.AbstractSeoPopulateDomainStrategy testStrategy = seoAdapter.new AbstractSeoPopulateDomainStrategy() {
			@Override
			protected List<DisplayValue> getList(final SeoDTO seoDTO) {
				return seoDTO.getTitleList();
			}

			@Override
			protected void setValue(final LocaleDependantFields fields, final String value) {
				fields.setTitle(value);
			}
		};

		context.checking(new Expectations() {
			{
				oneOf(mockObjectWithLocaleDependantFields).getLocaleDependantFieldsWithoutFallBack(LOCALE_EN);
				will(returnValue(mockLocaleDependantFields));

				oneOf(mockObjectWithLocaleDependantFields).addOrUpdateLocaleDependantFields(mockLocaleDependantFields);

				oneOf(mockLocaleDependantFields).setTitle(SEO_TITLE);
			}
		});

		final SeoDTO seoDTO = new SeoDTO();
		seoDTO.setTitleList(Arrays.asList(new DisplayValue(LANGUAGE_EN, SEO_TITLE), new DisplayValue(LANGUAGE_ZUZU, ZUZU_TITLE)));

		testStrategy.populateDomain(seoDTO, mockObjectWithLocaleDependantFields);
	}

	/**
	 * Tests populateDomain.
	 */
	@Test
	public void testPopulateDomain() {
		context.checking(new Expectations() {
			{
				atLeast(1).of(mockObjectWithLocaleDependantFields).getLocaleDependantFieldsWithoutFallBack(LOCALE_EN);
				will(returnValue(mockLocaleDependantFields));

				atLeast(1).of(mockObjectWithLocaleDependantFields).addOrUpdateLocaleDependantFields(mockLocaleDependantFields);

				oneOf(mockLocaleDependantFields).setUrl(SEO_URL);
				oneOf(mockLocaleDependantFields).setTitle(SEO_TITLE);
				oneOf(mockLocaleDependantFields).setKeyWords(SEO_KEY_WORDS);
				oneOf(mockLocaleDependantFields).setDescription(SEO_DESCRIPTION);
			}
		});

		final SeoDTO seoDTO = new SeoDTO();
		seoDTO.setTitleList(Arrays.asList(new DisplayValue(LANGUAGE_EN, SEO_TITLE)));
		seoDTO.setKeywordsList(Arrays.asList(new DisplayValue(LANGUAGE_EN, SEO_KEY_WORDS)));
		seoDTO.setUrlList(Arrays.asList(new DisplayValue(LANGUAGE_EN, SEO_URL)));
		seoDTO.setDescriptionList(Arrays.asList(new DisplayValue(LANGUAGE_EN, SEO_DESCRIPTION)));

		seoAdapter.populateDomain(seoDTO, mockObjectWithLocaleDependantFields);
	}

	/**
	 * Tests populateDTO.
	 */
	@Test
	public void testPopulateDTO() {
		seoAdapter.setSupportedLocales(SUPPORTED_LOCALES);

		context.checking(new Expectations() {
			{
				oneOf(mockObjectWithLocaleDependantFields).getLocaleDependantFieldsWithoutFallBack(LOCALE_EN);
				will(returnValue(mockLocaleDependantFields));

				oneOf(mockLocaleDependantFields).getUrl();
				will(returnValue(SEO_URL));
				oneOf(mockLocaleDependantFields).getTitle();
				will(returnValue(SEO_TITLE));
				oneOf(mockLocaleDependantFields).getKeyWords();
				will(returnValue(SEO_KEY_WORDS));
				oneOf(mockLocaleDependantFields).getDescription();
				will(returnValue(SEO_DESCRIPTION));
			}
		});

		final SeoDTO seoDTO = new SeoDTO();

		seoAdapter.populateDTO(mockObjectWithLocaleDependantFields, seoDTO);

		assertDisplayValueList(seoDTO.getUrlList(), LANGUAGE_EN, SEO_URL);
		assertDisplayValueList(seoDTO.getTitleList(), LANGUAGE_EN, SEO_TITLE);
		assertDisplayValueList(seoDTO.getKeywordsList(), LANGUAGE_EN, SEO_KEY_WORDS);
		assertDisplayValueList(seoDTO.getDescriptionList(), LANGUAGE_EN, SEO_DESCRIPTION);
	}

	private void assertDisplayValueList(final List<DisplayValue> displayValueList, final String language, final String value) {
		assertEquals(1, displayValueList.size());
		assertEquals(language, displayValueList.get(0).getLanguage());
		assertEquals(value, displayValueList.get(0).getValue());
	}

	/**
	 * Tests populateDTO On Null SupportedLocales.
	 */
	@Test(expected = PopulationRuntimeException.class)
	public void testPopulateDTOOnNullSupportedLocales() {
		seoAdapter.setSupportedLocales(null);

		ObjectWithLocaleDependantFields ldfObject = mockObjectWithLocaleDependantFields;

		seoAdapter.populateDTO(ldfObject, new SeoDTO());
	}
}
