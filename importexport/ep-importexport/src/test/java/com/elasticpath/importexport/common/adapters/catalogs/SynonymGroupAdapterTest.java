package com.elasticpath.importexport.common.adapters.catalogs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.ElasticPath;
import com.elasticpath.domain.search.Synonym;
import com.elasticpath.domain.search.SynonymGroup;
import com.elasticpath.domain.search.impl.SynonymGroupImpl;
import com.elasticpath.importexport.common.caching.CachingService;
import com.elasticpath.importexport.common.dto.catalogs.SynonymGroupDTO;

/**
 * Verify that SynonymGroupAdapterTest (from Catalogs) populates catalog domain object from DTO properly and vice versa. 
 * Nested adapters should be tested separately.
 */
public class SynonymGroupAdapterTest {
	
	private static final String LOCALE_EN = "en";

	private static final String CONCEPT_TERM = "concept_term";
	
	private static final String SYNONYM1 = "synonym1";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private ElasticPath mockElasticPath;
	
	private CachingService mockCachingService;

	private SynonymGroupAdapter synonymGroupAdapter;
		
	/**
	 * Setup test.
	 */
	@Before
	public void setUp() throws Exception {
		
		mockElasticPath = context.mock(ElasticPath.class);
		mockCachingService = context.mock(CachingService.class);
		
		synonymGroupAdapter = new SynonymGroupAdapter();
		synonymGroupAdapter.setElasticPath(mockElasticPath);
		synonymGroupAdapter.setCachingService(mockCachingService);

		context.checking(new Expectations() {
			{
				allowing(mockElasticPath).getBean(ContextIdNames.SYNONYM_GROUP);
				will(returnValue(new SynonymGroupImpl()));
			}
		});
	}

	/**
	 * Check that all required fields for Dto object are being set during domain population.
	 */
	@Test
	public void testPopulateDTO() {
		final SynonymGroup mockDomain = context.mock(SynonymGroup.class);
		final Synonym mockSynonym = context.mock(Synonym.class);
		
		final Set<Synonym> synonymSet = new HashSet<Synonym>();
		synonymSet.add(mockSynonym);

		context.checking(new Expectations() {
			{
				oneOf(mockDomain).getConceptTerm();
				will(returnValue(CONCEPT_TERM));
				oneOf(mockDomain).getSynonyms();
				will(returnValue(synonymSet));
				oneOf(mockSynonym).getSynonym();
				will(returnValue(SYNONYM1));
				oneOf(mockDomain).getLocale();
				will(returnValue(new Locale(LOCALE_EN)));
			}
		});

		SynonymGroupDTO dto = synonymGroupAdapter.createDtoObject();
		synonymGroupAdapter.populateDTO(mockDomain, dto);

		assertEquals(CONCEPT_TERM, dto.getConceptTerm());
		assertEquals(1, dto.getSynonyms().size());
		assertEquals(SYNONYM1, dto.getSynonyms().get(0));
	}

	/**
	 * Check that all required fields for domain object are being set during domain population.
	 */
	@Test
	public void testPopulateDomain() {
		SynonymGroupDTO dto = synonymGroupAdapter.createDtoObject();
		dto.setConceptTerm(CONCEPT_TERM);
		dto.setSynonyms(Arrays.asList(SYNONYM1));
		dto.setLocale(LOCALE_EN);
		
		final SynonymGroup mockDomain = context.mock(SynonymGroup.class);
		final Synonym mockSynonym = context.mock(Synonym.class);

		context.checking(new Expectations() {
			{
				oneOf(mockDomain).setConceptTerm(CONCEPT_TERM);
				oneOf(mockDomain).setLocale(new Locale(LOCALE_EN));

				oneOf(mockElasticPath).getBean(ContextIdNames.SYNONYM);
				will(returnValue(mockSynonym));
				oneOf(mockSynonym).setSynonym(SYNONYM1);

				oneOf(mockDomain).setSynonyms(Arrays.asList(mockSynonym));
			}
		});

		synonymGroupAdapter.populateDomain(dto, mockDomain);
	}

	/**
	 * Check that CreateDtoObject works. 
	 */
	@Test
	public void testCreateDomainObject() {
		assertNotNull(synonymGroupAdapter.createDomainObject());
	}

	/**
	 * Check that createDomainObject works.
	 */
	@Test
	public void testCreateDtoObject() {
		assertNotNull(synonymGroupAdapter.createDtoObject());
	}
}
