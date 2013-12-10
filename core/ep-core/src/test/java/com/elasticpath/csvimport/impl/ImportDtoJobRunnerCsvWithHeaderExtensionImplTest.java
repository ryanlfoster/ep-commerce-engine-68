/**
 *
 */
package com.elasticpath.csvimport.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tools.ant.filters.StringInputStream;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.elasticpath.common.dto.assembler.pricing.BaseAmountDtoAssembler;
import com.elasticpath.common.dto.assembler.pricing.BaseAmountDtoAssemblerForCsvImport;
import com.elasticpath.common.dto.pricing.BaseAmountDTO;
import com.elasticpath.common.dto.pricing.PriceListDescriptorDTO;
import com.elasticpath.common.pricing.service.BaseAmountFilter;
import com.elasticpath.common.pricing.service.impl.BaseAmountFilterImpl;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.csvimport.CsvReaderConfiguration;
import com.elasticpath.csvimport.DtoImportDataType;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.cmuser.impl.CmUserImpl;
import com.elasticpath.domain.dataimport.ImportAction;
import com.elasticpath.domain.dataimport.ImportJob;
import com.elasticpath.domain.dataimport.ImportNotification;
import com.elasticpath.domain.dataimport.impl.AbstractImportTypeImpl;
import com.elasticpath.domain.dataimport.impl.ImportBadRowImpl;
import com.elasticpath.domain.dataimport.impl.ImportFaultImpl;
import com.elasticpath.domain.dataimport.impl.ImportJobRequestImpl;
import com.elasticpath.domain.dataimport.impl.ImportJobStatusImpl;
import com.elasticpath.domain.misc.RandomGuid;
import com.elasticpath.domain.misc.impl.RandomGuidImpl;
import com.elasticpath.domain.pricing.BaseAmount;
import com.elasticpath.domain.pricing.PriceListDescriptor;
import com.elasticpath.domain.pricing.csvimport.impl.BaseAmountDtoInsertUpdateImporterWithHeaderExtensionImpl;
import com.elasticpath.domain.pricing.csvimport.impl.CsvImportFieldBaseAmountDtoMapperImpl;
import com.elasticpath.domain.pricing.csvimport.impl.ImportDataTypeBaseAmountImpl;
import com.elasticpath.domain.pricing.csvimport.impl.ImportDataTypeHeaderForBaseAmountImpl;
import com.elasticpath.persistence.CsvFileReader;
import com.elasticpath.persistence.impl.CsvFileReaderImpl;
import com.elasticpath.service.changeset.ChangeSetService;
import com.elasticpath.service.dataimport.ImportService;
import com.elasticpath.service.dataimport.dao.ImportJobStatusDao;
import com.elasticpath.service.dataimport.dao.ImportNotificationDao;
import com.elasticpath.service.dataimport.impl.ImportJobStatusHandlerImpl;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.pricing.BaseAmountService;
import com.elasticpath.service.pricing.PriceListDescriptorService;
import com.elasticpath.service.pricing.impl.BaseAmountFactoryImpl;
import com.elasticpath.service.pricing.impl.BaseAmountServiceImpl;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * A test for ImportDtoJobRunnerCsvWithHeaderExtensionImpl.
 *
 */
@SuppressWarnings({ "PMD.TooManyFields", "PMD.ExcessiveImports" })
public class ImportDtoJobRunnerCsvWithHeaderExtensionImplTest {

	private static final int ITEMS_6 = 6;
	private static final int ITEMS_8 = 8;

	private static final Integer INTEGER_7 = Integer.valueOf(7);
	private static final Integer INTEGER_6 = Integer.valueOf(6);
	private static final Integer INTEGER_5 = Integer.valueOf(5);
	private static final Integer INTEGER_3 = Integer.valueOf(3);
	private static final Integer INETEGER_2 = Integer.valueOf(2);

	private final String csvFile =
		"productName,productCode,skuCode,skuConfiguration,Qty,listPrice_CanadianPriceList_CAD,salePrice_CanadianPriceList_CAD"
		+ "\nCanon Camera 1,prod1,,,1,150.99,"
		+ "\nCanon Camera 1,prod1,SKU1,red,1,129.99,"
		+ "\nCanon Camera 1,prod1,SKU2,blue,1,139.99,"
		+ "\nCanon Camera 1,prod1,SKU3,green,1,139.99,"
		+ "\nRoad Pouch 1,prod2,,,1,24.99,"
		+ "\nRoad Pouch 1,prod2,SKUroad1,\"red,large\",1,25.99,"
		+ "\nRoad Pouch 1,prod2,SKUroad2,\"red, medium\",1,23.99,"
		+ "\nRoad Pouch 1,prod2,SKUroad3,\"yellow, medium\",1,23.99,"
		+ "\n";

	private final String csvFileFailed =
		"productName,productCode,skuCode,skuConfiguration,Qty,listPrice_CanadianPriceList_CAD,salePrice_CanadianPriceList_CAD"
		+ "\nCanon Camera 1,prod1,,,1,150.99,"
		+ "\nCanon Camera 1,prod1,SKU1,red,1,129.99,"
		+ "\nCanon Camera 1,prod1,SKU2,blue,1,-2,"
		+ "\nCanon Camera 1,prod1,SKU3,green,1,139.99,"
		+ "\nRoad Pouch 1,prod2,,,1,24.99,"
		+ "\nRoad Pouch 1,prod2,SKUroad1,\"red,large\",1,25.99,"
		+ "\nRoad Pouch 1,prod2,SKUroad2,\"red, medium\",1,23.99,"
		+ "\nRoad Pouch 1,prod2,SKUroad3,\"yellow, medium\",1,23.99,-1"
		+ "\n";


	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private BeanFactory beanFactory;
	private BeanFactoryExpectationsFactory expectationsFactory;

	private final PriceListDescriptorService priceListDescriptorService = context.mock(PriceListDescriptorService.class);

	private final ImportDtoJobRunnerCsvWithHeaderExtensionImpl baseAmountJobRunner =
		new ImportDtoJobRunnerCsvWithHeaderExtensionImpl() {
		@Override
		protected InputStream createInputStream() {
			return new StringInputStream(csvFile);
		}
	};
	private final ImportDtoJobRunnerCsvWithHeaderExtensionImpl baseAmountJobRunnerForFailedResults =
		new ImportDtoJobRunnerCsvWithHeaderExtensionImpl() {
		@Override
		protected InputStream createInputStream() {
			return new StringInputStream(csvFileFailed);
		}
	};

	private final BaseAmountService baseAmountService =
		new BaseAmountServiceImpl() {
			@Override
			public boolean exists(final BaseAmount baseAmount) {
				return false;
			}
			@Override
			public BaseAmount add(final BaseAmount baseAmount) {
				baseAmountsList.add(baseAmount);
				return baseAmount;
			}
			@Override
			public Collection<BaseAmount> findBaseAmounts(final BaseAmountFilter filter) {
				return Collections.emptyList();
			}
		};

	private final Validator validator = context.mock(Validator.class);

	private final List<BaseAmount> baseAmountsList = new ArrayList<BaseAmount>();
	private final ImportJobRequestImpl importJobRequest = new ImportJobRequestImpl("FirstId");

	private int guidCounter;

	private final ImportJobStatusDao importJobStatusDao = context.mock(ImportJobStatusDao.class);
	private final ImportNotificationDao importNotificationDao = context.mock(ImportNotificationDao.class);
	private final TimeService timeService = context.mock(TimeService.class);

	private final ImportJob importJob = context.mock(ImportJob.class);

	private Map<String, Integer> mappings;

	private final PriceListDescriptor priceListDescriptor = context.mock(PriceListDescriptor.class);;

	private DtoImportDataType<BaseAmountDTO> dtoImportDataType;
	private DtoImportDataType<PriceListDescriptorDTO> dtoImportDataTypeForHeader;

	private RandomGuid randomGuid;
	private ImportJobStatusImpl importJobStatus;

	private CsvReaderConfiguration csvReaderConfiguration;
	private CsvFileReader csvFileReader;
	private final ChangeSetService changeSetService = context.mock(ChangeSetService.class);


	/**
	 * Setup initial setting for the objects.
	 */
	@Before
	public void setUp() {

	    beanFactory = context.mock(BeanFactory.class);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);

		guidCounter = 0;


		randomGuid = new RandomGuidImpl() {
			private static final long serialVersionUID = -6107620621955626563L;

			@Override
			public String toString() {
				return "GUID_" + guidCounter++;
			}
		};

		CmUser cmUser = new CmUserImpl();


		dtoImportDataType = new ImportDataTypeBaseAmountImpl();
		dtoImportDataType.init(null);

		dtoImportDataTypeForHeader = new ImportDataTypeHeaderForBaseAmountImpl();
		dtoImportDataTypeForHeader.init(null);

		importJobStatus = new ImportJobStatusImpl();
		final CsvImportFieldBaseAmountDtoMapperImpl mapper = new CsvImportFieldBaseAmountDtoMapperImpl();
		final CsvImportFieldObjectMapperImpl<PriceListDescriptorDTO> mapperForHeader = new CsvImportFieldObjectMapperImpl<PriceListDescriptorDTO>();
		final DtoCsvLineReaderWithHeaderExtensionImpl<BaseAmountDTO, PriceListDescriptorDTO> dtoCsvLineReader =
			new DtoCsvLineReaderWithHeaderExtensionImpl<BaseAmountDTO, PriceListDescriptorDTO>();

		final ImportJobStatusHandlerImpl importJobStatusHandler = new ImportJobStatusHandlerImpl();

		final BaseAmountDtoInsertUpdateImporterWithHeaderExtensionImpl insertUpdateDtoImporter =
			new BaseAmountDtoInsertUpdateImporterWithHeaderExtensionImpl();
		insertUpdateDtoImporter.setChangeSetService(changeSetService);

		final BaseAmountDtoAssembler assembler = new BaseAmountDtoAssemblerForCsvImport();

		final BaseAmountFactoryImpl baseAmountFactory = new BaseAmountFactoryImpl();
		csvReaderConfiguration = new CsvReaderConfigurationImpl();
		csvFileReader = new CsvFileReaderImpl();

		initMappings();
		initExpectations();


		importJobStatus.setImportJob(importJob);
		importJobStatus.setStartedBy(cmUser);
		importJobRequest.setImportJob(importJob);
		importJobRequest.setInitiator(cmUser);

		importJobStatusHandler.setImportJobStatusDao(importJobStatusDao);
		importJobStatusHandler.setImportNotificationDao(importNotificationDao);
		importJobStatusHandler.setTimeService(timeService);

		mapper.setBeanFactory(beanFactory);
		mapper.setDtoImportDataType(dtoImportDataType);

		mapperForHeader.setBeanFactory(beanFactory);
		mapperForHeader.setDtoImportDataType(dtoImportDataTypeForHeader);

		dtoCsvLineReader.setBeanFactory(beanFactory);
		dtoCsvLineReader.setMapper(mapper);
		dtoCsvLineReader.setMapperForHeader(mapperForHeader);

		baseAmountFactory.setValidator(validator);

		assembler.setBaseAmountFactory(baseAmountFactory);

		insertUpdateDtoImporter.setAssembler(assembler);
		insertUpdateDtoImporter.setBaseAmountService(baseAmountService);
		insertUpdateDtoImporter.setBeanFactory(beanFactory);

		final ImportService importService = null;
		baseAmountJobRunner.setPriceListDescriptorService(priceListDescriptorService);
		baseAmountJobRunner.setBeanFactory(beanFactory);
		baseAmountJobRunner.setDtoCsvLineReader(dtoCsvLineReader);
		baseAmountJobRunner.setImportService(importService);
		baseAmountJobRunner.setImportJobStatusHandler(importJobStatusHandler);
		baseAmountJobRunner.setInsertUpdateDtoImporter(insertUpdateDtoImporter);
		baseAmountJobRunner.setChangeSetService(changeSetService);
		baseAmountJobRunner.setPersistenceListenerMetadataMap(new HashMap<String, Object>());

		baseAmountJobRunnerForFailedResults.setPriceListDescriptorService(priceListDescriptorService);
		baseAmountJobRunnerForFailedResults.setBeanFactory(beanFactory);
		baseAmountJobRunnerForFailedResults.setDtoCsvLineReader(dtoCsvLineReader);
		baseAmountJobRunnerForFailedResults.setImportService(importService);
		baseAmountJobRunnerForFailedResults.setImportJobStatusHandler(importJobStatusHandler);
		baseAmountJobRunnerForFailedResults.setInsertUpdateDtoImporter(insertUpdateDtoImporter);
		baseAmountJobRunnerForFailedResults.setChangeSetService(changeSetService);
		baseAmountJobRunnerForFailedResults.setPersistenceListenerMetadataMap(new HashMap<String, Object>());
	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	/**
	 *
	 */
	private void initExpectations() {
		context.checking(new Expectations() {
		{
			allowing(timeService).getCurrentTime(); will(returnValue(new Date()));
			allowing(importJobStatusDao).findByProcessId("SomeID"); will(returnValue(importJobStatus));
			allowing(importJobStatusDao).saveOrUpdate(importJobStatus); will(returnValue(importJobStatus));

			allowing(importJob).getName(); will(returnValue("Import Job Name"));
			allowing(importJob).getCsvFileColDelimeter(); will(returnValue(','));
			allowing(importJob).getCsvFileTextQualifier(); will(returnValue('"'));
			allowing(importJob).getMappings(); will(returnValue(mappings));
			allowing(importJob).getImportType(); will(returnValue(AbstractImportTypeImpl.INSERT_UPDATE_TYPE));
			allowing(importJob).getMaxAllowErrors(); will(returnValue(Integer.MAX_VALUE));

			allowing(beanFactory).getBean(ContextIdNames.IMPORT_DATA_TYPE_BASEAMOUNT); will(returnValue(dtoImportDataType));
			allowing(beanFactory).getBean(ContextIdNames.CSV_FILE_READER); will(returnValue(csvFileReader));
			allowing(beanFactory).getBean(ContextIdNames.CSV_READER_CONFIGURATION); will(returnValue(csvReaderConfiguration));
			allowing(beanFactory).getBean(ContextIdNames.RANDOM_GUID); will(returnValue(randomGuid));

			oneOf(beanFactory).getBean(ContextIdNames.CSV_READ_RESULT); will(returnValue(new CsvReadResultImpl<Object>()));
			oneOf(beanFactory).getBean(ContextIdNames.CSV_READ_RESULT); will(returnValue(new CsvReadResultImpl<Object>()));

			expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.IMPORT_VALID_ROW, ImportValidRowImpl.class);

			allowing(beanFactory).getBean(ContextIdNames.BASE_AMOUNT_FILTER); will(returnValue(new BaseAmountFilterImpl()));

			allowing(beanFactory).getBean(ContextIdNames.IMPORT_FAULT); will(returnValue(new ImportFaultImpl()));

			allowing(beanFactory).getBean(ContextIdNames.IMPORT_BAD_ROW); will(returnValue(new ImportBadRowImpl()));

			allowing(beanFactory).getBean(ContextIdNames.RANDOM_GUID); will(returnValue(randomGuid));

			allowing(priceListDescriptor).getGuid(); will(returnValue("PRICE_LIST_DESCRIPTOR_GUID1"));

			allowing(priceListDescriptorService).findByName("CanadianPriceList"); will(returnValue(priceListDescriptor));

			allowing(importNotificationDao).findByProcessId(with(any(String.class)), with(any(ImportAction.class)));
					will(returnValue(new ArrayList<ImportNotification>()));

			allowing(validator).validate(with(any(Object.class)), with(any(Errors.class)));
		} });
	}

	/**
	 * Init mappings for the import job.
	 */
	private void initMappings() {
		mappings = new HashMap<String, Integer>();
		mappings.put("productCode", INETEGER_2);
		mappings.put("skuCode", INTEGER_3);
		mappings.put("quantity", INTEGER_5);
		mappings.put("listPrice", INTEGER_6);
		mappings.put("salePrice", INTEGER_7);
	}

	/**
	 * Test the job to run and import base amounts.
	 */
	@Test
	public void testRunWithValidValues() {
		String importJobProcessId = "SomeID";
		baseAmountJobRunner.init(importJobRequest, importJobProcessId);

		context.checking(new Expectations() { {
			allowing(changeSetService).isChangeSetEnabled();
			will(returnValue(false));
		} });
		baseAmountJobRunner.run();
		Assert.assertEquals("Here we expect a number of imported BaseAmounts", ITEMS_8, baseAmountsList.size());
	}

	/**
	 * Test the job to run and import base amounts.
	 */
	@Test
	public void testRunWithFailedValues() {
		String importJobProcessId = "SomeID";
		context.checking(new Expectations() { {
			allowing(changeSetService).isChangeSetEnabled();
			will(returnValue(false));
		} });

		baseAmountJobRunnerForFailedResults.init(importJobRequest, importJobProcessId);
		baseAmountJobRunnerForFailedResults.run();
		Assert.assertEquals("Here we expect a number of imported BaseAmounts", ITEMS_6, baseAmountsList.size());
	}
}
