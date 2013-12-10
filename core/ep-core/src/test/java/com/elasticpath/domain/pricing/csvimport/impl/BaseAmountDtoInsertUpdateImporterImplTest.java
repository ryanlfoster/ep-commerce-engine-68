/**
 * 
 */
package com.elasticpath.domain.pricing.csvimport.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.common.dto.assembler.pricing.BaseAmountDtoAssembler;
import com.elasticpath.common.dto.pricing.BaseAmountDTO;
import com.elasticpath.common.pricing.service.impl.BaseAmountFilterImpl;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.csvimport.ImportValidRow;
import com.elasticpath.csvimport.impl.ImportValidRowImpl;
import com.elasticpath.domain.dataimport.ImportBadRow;
import com.elasticpath.domain.dataimport.impl.ImportBadRowImpl;
import com.elasticpath.domain.dataimport.impl.ImportFaultImpl;
import com.elasticpath.domain.pricing.BaseAmount;
import com.elasticpath.domain.pricing.impl.BaseAmountImpl;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.changeset.ChangeSetService;
import com.elasticpath.service.pricing.BaseAmountService;
import com.elasticpath.service.pricing.impl.BaseAmountFactoryImpl;
import com.elasticpath.service.pricing.impl.BaseAmountValidatorImpl;

/**
 * Tests for the {@link BaseAmountDtoInsertUpdateImporterImpl} class. 
 */
public class BaseAmountDtoInsertUpdateImporterImplTest {

	private static final String GUID = "GUID";
	private final BaseAmountFilterImpl filter = new BaseAmountFilterImpl();
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private BeanFactory mockBeanFactory;
	private BaseAmountService mockBaseAmountService;
	private ChangeSetService mockChangeSetService;
	private BaseAmountDtoAssembler stubAssembler;
	
	
	/**
	 * Test that an EpServiceException is thrown if the PriceListDescriptorGuid is not specified for the import.
	 */
	@Test(expected = EpServiceException.class)
	public void testForEpServiceExceptionWithNoValidRows() {
		BaseAmountDtoInsertUpdateImporterImpl importer = new BaseAmountDtoInsertUpdateImporterImpl();
		importer.importDtos(new ArrayList<ImportValidRow<BaseAmountDTO>>(), null);
	}
	
	/**
	 * First setup.
	 */
	@Before
	public void setUp() {
		mockBeanFactory = createBeanFactory();
		mockBaseAmountService = context.mock(BaseAmountService.class);
		mockChangeSetService = context.mock(ChangeSetService.class);
		
		BaseAmountFactoryImpl baseAmountFactory = new BaseAmountFactoryImpl();
		baseAmountFactory.setValidator(new BaseAmountValidatorImpl());

		stubAssembler = new BaseAmountDtoAssembler();
		stubAssembler.setBaseAmountFactory(baseAmountFactory); 
	}
	
	private BeanFactory createBeanFactory() {
		final BeanFactory beanFactory = context.mock(BeanFactory.class);
		context.checking(new Expectations() { {
			allowing(beanFactory).getBean(ContextIdNames.IMPORT_BAD_ROW); 
				will(onConsecutiveCalls(
						returnValue(new ImportBadRowImpl()), 
						returnValue(new ImportBadRowImpl()), 
						returnValue(new ImportBadRowImpl())));
			allowing(beanFactory).getBean(ContextIdNames.IMPORT_FAULT); 
				will(onConsecutiveCalls(returnValue(new ImportFaultImpl()), returnValue(new ImportFaultImpl()), returnValue(new ImportFaultImpl())));
			allowing(beanFactory).getBean(ContextIdNames.BASE_AMOUNT_FILTER); will(returnValue(filter));
		} });
		return beanFactory;
	}

	/**
	 * Test for valid input.
	 */
	@Test
	public void testImportDtosWithValidInput() {
		
		final BaseAmount baseAmount1 = this.createBaseAmount(BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE);
		
		context.checking(new Expectations() { {  //NOPMD
			allowing(mockBaseAmountService).exists(baseAmount1); will(returnValue(false));
			allowing(mockBaseAmountService).add(baseAmount1);
			allowing(mockChangeSetService).isChangeSetEnabled(); will(returnValue(false));
		} });

		final BaseAmountDTO dto1 = createBaseAmountDTO(BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE);
		
		final ImportValidRow<BaseAmountDTO> row1 = new ImportValidRowImpl<BaseAmountDTO>();
		row1.setDto(dto1);
		
		List<ImportValidRow<BaseAmountDTO>> input = new ArrayList<ImportValidRow<BaseAmountDTO>>();
		input.add(row1);
		
		BaseAmountDtoInsertUpdateImporterImpl importer = new BaseAmountDtoInsertUpdateImporterImpl();
		importer.setBeanFactory(mockBeanFactory);
		importer.setBaseAmountService(mockBaseAmountService);
		importer.setChangeSetService(mockChangeSetService);
		importer.setAssembler(stubAssembler);
		
		Collection<ImportBadRow> importDtos = importer.importDtos(input, GUID);
		Assert.assertEquals(0, importDtos.size());
	}

	/** */
	@Test
	public void testImportDtosWithExistingDTOs() {
		
		final BaseAmount baseAmount1 = this.createBaseAmount(BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE);

		final ArrayList<BaseAmount> listForFind = new ArrayList<BaseAmount>();
		listForFind.add(baseAmount1);
		
		context.checking(new Expectations() { {  //NOPMD
			allowing(mockBaseAmountService).exists(baseAmount1); will(returnValue(true));
			allowing(mockBaseAmountService).findBaseAmounts(filter); will(returnValue(listForFind));
			allowing(mockBaseAmountService).add(baseAmount1); will(returnValue(baseAmount1));
			allowing(mockBaseAmountService).delete(baseAmount1);
			allowing(mockChangeSetService).isChangeSetEnabled(); will(returnValue(false));
		} });

		final BaseAmountDTO dto1 = createBaseAmountDTO(BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE);
		
		final ImportValidRow<BaseAmountDTO> row1 = new ImportValidRowImpl<BaseAmountDTO>();
		row1.setDto(dto1);
		row1.setRowNumber(1);
		
		List<ImportValidRow<BaseAmountDTO>> input = new ArrayList<ImportValidRow<BaseAmountDTO>>();
		input.add(row1);
		
		BaseAmountDtoInsertUpdateImporterImpl importer = new BaseAmountDtoInsertUpdateImporterImpl();
		importer.setBeanFactory(mockBeanFactory);
		importer.setBaseAmountService(mockBaseAmountService);
		importer.setChangeSetService(mockChangeSetService);
		importer.setAssembler(stubAssembler);
		
		Collection<ImportBadRow> importDtos = importer.importDtos(input, GUID);
		Assert.assertEquals(0, importDtos.size()); // TODO check!!!
	}

	/** */
	@Test
	public void testImportDtosWithInvalidListPrice() {
		
		final BaseAmount baseAmount1 = new BaseAmountImpl();
		
		context.checking(new Expectations() { { //NOPMD
			allowing(mockBaseAmountService).exists(baseAmount1); will(returnValue(false));
			allowing(mockBaseAmountService).findBaseAmounts(filter); will(returnValue(baseAmount1));
			allowing(mockBaseAmountService).add(baseAmount1); will(returnValue(baseAmount1));
		} });

		final BaseAmountDTO dto1 = createBaseAmountDTO(BigDecimal.valueOf(-1), BigDecimal.ONE, BigDecimal.ONE);
		
		final ImportValidRow<BaseAmountDTO> row1 = new ImportValidRowImpl<BaseAmountDTO>();
		row1.setDto(dto1);
		row1.setRowNumber(1);
		
		// add rows to list
		List<ImportValidRow<BaseAmountDTO>> input = new ArrayList<ImportValidRow<BaseAmountDTO>>();
		input.add(row1);
		
		BaseAmountDtoInsertUpdateImporterImpl importer = new BaseAmountDtoInsertUpdateImporterImpl();
		importer.setBeanFactory(mockBeanFactory);
		importer.setBaseAmountService(mockBaseAmountService);
		importer.setAssembler(stubAssembler);
		
		List<ImportBadRow> importDtos = importer.importDtos(input, GUID);
		Assert.assertEquals(1, importDtos.size());
		
		ImportBadRow badRow1 = importDtos.get(0);
		Assert.assertEquals(BigDecimal.valueOf(-1).toString(), badRow1.getImportFaults().get(0).getArgs()[0]);
		Assert.assertEquals(1, badRow1.getRowNumber());

	}

	/** */
	@Test
	public void testImportDtosWithInvalidSalePrice() {
		
		final BaseAmount baseAmount1 = new BaseAmountImpl();
		
		context.checking(new Expectations() { { //NOPMD
			allowing(mockBaseAmountService).exists(baseAmount1); will(returnValue(false));
			allowing(mockBaseAmountService).findBaseAmounts(filter); will(returnValue(baseAmount1));
			allowing(mockBaseAmountService).add(baseAmount1); will(returnValue(baseAmount1));
		} });

		// ROW2
		final BaseAmountDTO dto2 = createBaseAmountDTO(BigDecimal.ONE, BigDecimal.valueOf(-1), BigDecimal.ONE);
		final ImportValidRow<BaseAmountDTO> row2 = new ImportValidRowImpl<BaseAmountDTO>();
		row2.setDto(dto2);
		row2.setRowNumber(2);

		// add rows to list
		List<ImportValidRow<BaseAmountDTO>> input = new ArrayList<ImportValidRow<BaseAmountDTO>>();
		input.add(row2);
		
		BaseAmountDtoInsertUpdateImporterImpl importer = new BaseAmountDtoInsertUpdateImporterImpl();
		importer.setBeanFactory(mockBeanFactory);
		importer.setBaseAmountService(mockBaseAmountService);
		importer.setAssembler(stubAssembler);
		
		List<ImportBadRow> importDtos = importer.importDtos(input, GUID);
		Assert.assertEquals(1, importDtos.size());
		
		ImportBadRow badRow2 = importDtos.get(0);
		Assert.assertEquals(BigDecimal.valueOf(-1).toString(), badRow2.getImportFaults().get(0).getArgs()[0]);
		Assert.assertEquals(2, badRow2.getRowNumber());

	}

	/** */
	@Test
	public void testImportDtosWithInvalidQuantity() {
		
		final BaseAmount baseAmount1 = new BaseAmountImpl();
		
		context.checking(new Expectations() { { //NOPMD
			allowing(mockBaseAmountService).exists(baseAmount1); will(returnValue(false));
			allowing(mockBaseAmountService).findBaseAmounts(filter); will(returnValue(baseAmount1));
			allowing(mockBaseAmountService).add(baseAmount1); will(returnValue(baseAmount1));
		} });

		// ROW
		final BaseAmountDTO dto3 = createBaseAmountDTO(BigDecimal.ONE, BigDecimal.ONE, BigDecimal.valueOf(-1));
		final ImportValidRow<BaseAmountDTO> row3 = new ImportValidRowImpl<BaseAmountDTO>();
		row3.setDto(dto3);
		row3.setRowNumber(1);

		// add rows to list
		List<ImportValidRow<BaseAmountDTO>> input = new ArrayList<ImportValidRow<BaseAmountDTO>>();
		input.add(row3);
		
		BaseAmountDtoInsertUpdateImporterImpl importer = new BaseAmountDtoInsertUpdateImporterImpl();
		importer.setBeanFactory(mockBeanFactory);
		importer.setBaseAmountService(mockBaseAmountService);
		importer.setAssembler(stubAssembler);
		
		List<ImportBadRow> importDtos = importer.importDtos(input, GUID);
		Assert.assertEquals(1, importDtos.size());
		
		ImportBadRow badRow3 = importDtos.get(0);
		Assert.assertEquals(BigDecimal.valueOf(-1).toString(), badRow3.getImportFaults().get(0).getArgs()[0]);
		Assert.assertEquals(1, badRow3.getRowNumber());
	}

	/** */
	@Test
	public void testImportDtosWithMultipleWrongRows() {

		final BaseAmount baseAmount1 = new BaseAmountImpl();
		
		context.checking(new Expectations() { { //NOPMD
			allowing(mockBaseAmountService).exists(baseAmount1); will(returnValue(false));
			allowing(mockBaseAmountService).findBaseAmounts(filter); will(returnValue(baseAmount1));
			allowing(mockBaseAmountService).add(baseAmount1); will(returnValue(baseAmount1));
		} });

		// ROW1
		final BaseAmountDTO dtoWithInvalidListPrice = createBaseAmountDTO(BigDecimal.valueOf(-1), BigDecimal.ONE, BigDecimal.ONE);
		final ImportValidRow<BaseAmountDTO> row1 = new ImportValidRowImpl<BaseAmountDTO>();
		row1.setDto(dtoWithInvalidListPrice);
		row1.setRowNumber(1);
		
		// ROW2
		final BaseAmountDTO dtoWithInvalidSalePrice = createBaseAmountDTO(BigDecimal.ONE, BigDecimal.valueOf(-1), BigDecimal.ONE);
		final ImportValidRow<BaseAmountDTO> row2 = new ImportValidRowImpl<BaseAmountDTO>();
		row2.setDto(dtoWithInvalidSalePrice);
		row2.setRowNumber(2);

		// add rows to list
		List<ImportValidRow<BaseAmountDTO>> input = new ArrayList<ImportValidRow<BaseAmountDTO>>();
		input.add(row1);
		input.add(row2);
		
		BaseAmountDtoInsertUpdateImporterImpl importer = new BaseAmountDtoInsertUpdateImporterImpl();
		importer.setBeanFactory(mockBeanFactory);
		importer.setBaseAmountService(mockBaseAmountService);
		importer.setAssembler(stubAssembler);
		
		List<ImportBadRow> importDtos = importer.importDtos(input, GUID);
		Assert.assertEquals(2, importDtos.size());
		
		ImportBadRow badRow1 = importDtos.get(0);
		Assert.assertEquals(1, badRow1.getRowNumber());
		Assert.assertEquals(BigDecimal.valueOf(-1).toString(), badRow1.getImportFaults().get(0).getArgs()[0]);

		ImportBadRow badRow2 = importDtos.get(1);
		Assert.assertEquals(2, badRow2.getRowNumber());
		Assert.assertEquals(BigDecimal.valueOf(-1).toString(), badRow2.getImportFaults().get(0).getArgs()[0]);
	}

	private BaseAmountDTO createBaseAmountDTO(final BigDecimal listPrice, final BigDecimal salePrice, final BigDecimal quantity) {
		final BaseAmountDTO dto1 = new BaseAmountDTO();
		
		dto1.setListValue(listPrice);
		dto1.setSaleValue(salePrice);
		dto1.setQuantity(quantity);
		dto1.setGuid(GUID);
		dto1.setObjectGuid("OBJECT_GUID");
		dto1.setObjectType("PRODUCT");
		
		return dto1;
	}

	private BaseAmount createBaseAmount(final BigDecimal listPrice, final BigDecimal salePrice, final BigDecimal quantity) {
		final BaseAmountImpl baseAmount = new BaseAmountImpl();
		
		baseAmount.setListValue(listPrice);
		baseAmount.setSaleValue(salePrice);
		baseAmount.setQuantity(quantity);
		baseAmount.setGuid(GUID);
		baseAmount.setObjectGuid("OBJECT_GUID");
		baseAmount.setObjectType("SKU");
		
		return baseAmount;
	}
}
