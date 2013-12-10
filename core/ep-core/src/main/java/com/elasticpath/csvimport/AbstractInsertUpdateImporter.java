package com.elasticpath.csvimport;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.dataimport.ImportBadRow;
import com.elasticpath.domain.dataimport.ImportFault;

/**
 * 
 * The abstract class for insert update importer.
 *
 */
public class AbstractInsertUpdateImporter {
	

	private BeanFactory beanFactory;
	
	/**
	 * Create import fault object.
	 * @param code message code
	 * @param parameter message parameter
	 * @return ImportFault object
	 */
	protected ImportFault createImportFault(final String code, final Object parameter) {
		final ImportFault fault = getBeanFactory().getBean(ContextIdNames.IMPORT_FAULT);
		fault.setLevel(ImportFault.ERROR);
		fault.setCode(code);
		fault.setArgs(new Object[] { parameter });
		return fault;
	}

	/**
	 * Creates an ImportBadRow record.
	 * @param row the raw row string
	 * @param rowNumber the absolute row position within the original input stream
	 * @param fault {@link ImportFault}
	 * @return the bad row record
	 */
	protected ImportBadRow createImportBadRow(final String row, final int rowNumber, final ImportFault fault) {
		final ImportBadRow badRow = getBeanFactory().getBean(ContextIdNames.IMPORT_BAD_ROW);
		badRow.setRowNumber(rowNumber); //absolute position, not relative
		badRow.setRow(row);
		badRow.addImportFault(fault);
		return badRow;
	}

	/**
	 * @return the beanFactory
	 */
	public BeanFactory getBeanFactory() {
		return beanFactory;
	}

	/**
	 * @param beanFactory the beanFactory to set
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

}
