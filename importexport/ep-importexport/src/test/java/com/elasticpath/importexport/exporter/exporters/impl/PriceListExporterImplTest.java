package com.elasticpath.importexport.exporter.exporters.impl;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.common.dto.assembler.pricing.PriceListDescriptorDtoAssembler;
import com.elasticpath.common.dto.pricing.PriceListDescriptorDTO;
import com.elasticpath.common.pricing.service.BaseAmountFilter;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.ElasticPath;
import com.elasticpath.domain.pricing.BaseAmount;
import com.elasticpath.domain.pricing.PriceAdjustment;
import com.elasticpath.domain.pricing.PriceListDescriptor;
import com.elasticpath.domain.pricing.impl.PriceListDescriptorImpl;
import com.elasticpath.importexport.common.adapters.DtoAssemblerDelegatingAdapter;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.exporter.configuration.search.SearchConfiguration;
import com.elasticpath.importexport.exporter.context.DependencyRegistry;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.importexport.exporter.search.ImportExportSearcher;
import com.elasticpath.ql.parser.EPQueryType;
import com.elasticpath.service.pricing.BaseAmountService;
import com.elasticpath.service.pricing.PriceAdjustmentService;
import com.elasticpath.service.pricing.PriceListDescriptorService;

/**
 * Test Case for PriceListExporterImpl.
 */
public class PriceListExporterImplTest {

	// CHECKSTYLE:OFF
	private static final DtoAssemblerDelegatingAdapter<PriceListDescriptor, PriceListDescriptorDTO> PRICE_LIST_DESCRIPTOR_ADAPTER = new DtoAssemblerDelegatingAdapter<PriceListDescriptor, PriceListDescriptorDTO>();
	// CHECKSTYLE:ON

	private static final PriceListDescriptor PRICE_LIST_DESCRIPTOR = new PriceListDescriptorImpl();

	private static final String ID_GUID = "H11";

	private static final List<String> IDS_LIST = Arrays.asList(ID_GUID);

	private static final Set<String> IDS_SET = new HashSet<String>(IDS_LIST);

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private final PriceListExporterImpl priceListExporter = new PriceListExporterImpl();

	private final ImportExportSearcher importExportSearcher = context.mock(ImportExportSearcher.class);

	private final PriceListDescriptorService priceListDescriptorService = context.mock(PriceListDescriptorService.class);

	private final BaseAmountService baseAmountService = context.mock(BaseAmountService.class);

	private final PriceAdjustmentService priceAdjustmentService = context.mock(PriceAdjustmentService.class);

	private final BaseAmountFilter baseAmountFilter = context.mock(BaseAmountFilter.class);

	private final BaseAmount baseAmount = context.mock(BaseAmount.class);

	private final ElasticPath elasticpath = context.mock(ElasticPath.class);

	/**
	 * SetUp.
	 * 
	 * @throws Exception if any error
	 */
	@Before
	public void setUp() throws Exception {

		priceListExporter.setImportExportSearcher(importExportSearcher);
		priceListExporter.setPriceListDescriptorService(priceListDescriptorService);
		priceListExporter.setPriceListDescriptorAdapter(PRICE_LIST_DESCRIPTOR_ADAPTER);
		PRICE_LIST_DESCRIPTOR_ADAPTER.setAssembler(new PriceListDescriptorDtoAssembler());
		priceListExporter.setBaseAmountService(baseAmountService);
		priceListExporter.setElasticPath(elasticpath);
	}

	/**
	 * Tests getListExportableIDs method, also initialize and initializeExporter.
	 * 
	 * @throws ConfigurationException if initialization fail
	 */
	@Test
	public void testGetListExportableIDs() throws ConfigurationException {
		final ExportContext exportContext = new ExportContext(null, new SearchConfiguration());
		context.checking(new Expectations() {
			{
				oneOf(importExportSearcher).searchGuids(exportContext.getSearchConfiguration(), EPQueryType.PRICELIST);
				will(returnValue(IDS_LIST));
			}
		});

		priceListExporter.initialize(exportContext);
		assertEquals(IDS_LIST, priceListExporter.getListExportableIDs());
	}

	/**
	 * Tests findByIDs.
	 */
	@Test
	public void testFindByIDsListOfString() {
		context.checking(new Expectations() {
			{
				oneOf(priceListDescriptorService).findByGuid(ID_GUID);
				will(returnValue(PRICE_LIST_DESCRIPTOR));
			}
		});

		assertEquals(Arrays.asList(PRICE_LIST_DESCRIPTOR), priceListExporter.findByIDs(IDS_LIST));
	}

	/**
	 * Test getDomainAdapter.
	 */
	@Test
	public void testGetDomainAdapter() {
		assertEquals(PRICE_LIST_DESCRIPTOR_ADAPTER, priceListExporter.getDomainAdapter());
	}

	/**
	 * Tests getDtoClass.
	 */
	@Test
	public void testGetDtoClass() {
		assertEquals(PriceListDescriptorDTO.class, priceListExporter.getDtoClass());
	}

	/**
	 * Tests getDependentClasses.
	 */
	@Test
	public void testGetDependentClasses() {
		Class<?>[] dependentClasses = priceListExporter.getDependentClasses();
		assertEquals(2, dependentClasses.length);
	}

	/**
	 * Tests getJobType.
	 */
	@Test
	public void testGetJobType() {
		assertEquals(JobType.PRICELISTDESCRIPTOR, priceListExporter.getJobType());
	}

	/**
	 * Tests addDependencies.
	 */
	@Test
	public void testAddDependenciesListOfPriceListDescriptorDependencyRegistry() {
		context.checking(new Expectations() {
			{
				oneOf(elasticpath).getBean(ContextIdNames.BASE_AMOUNT_FILTER);
				will(returnValue(baseAmountFilter));
				oneOf(baseAmountFilter).setPriceListDescriptorGuid(ID_GUID);
				oneOf(baseAmountService).findBaseAmounts(baseAmountFilter);
				will(returnValue(Arrays.asList(baseAmount)));
				allowing(priceAdjustmentService).findByPriceList(with(any(String.class)));
				will(returnValue(Collections.emptyList()));
				oneOf(baseAmount).getGuid();
				will(returnValue(ID_GUID));
			}
		});

		PRICE_LIST_DESCRIPTOR.setGuid(ID_GUID);

		final DependencyRegistry dependencyRegistry = new DependencyRegistry(
				Arrays.asList(new Class<?>[] { BaseAmount.class, PriceAdjustment.class }));
		priceListExporter.addDependencies(Arrays.asList(PRICE_LIST_DESCRIPTOR), dependencyRegistry);

		assertEquals(IDS_SET, dependencyRegistry.getDependentGuids(BaseAmount.class));
	}
}
