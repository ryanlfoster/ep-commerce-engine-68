package com.elasticpath.importexport.importer.context;

import java.util.Set;
import java.util.TreeSet;

import com.elasticpath.importexport.common.summary.Summary;
import com.elasticpath.importexport.importer.configuration.ImportConfiguration;

/**
 * Container for data shared among Import classes: ImportConfiguration, etc.
 */
public class ImportContext {

	private final ImportConfiguration importConfiguration;
	
	private final Set<String> changedProducts = new TreeSet<String>();
	
	private final Set<String> changedPriceLists = new TreeSet<String>();
	
	private Summary summary;
	
	/**
	 * Constructor initializes import configuration.
	 *
	 * @param importConfiguration constructed earlier
	 */
	public ImportContext(final ImportConfiguration importConfiguration) {
		this.importConfiguration = importConfiguration;
	}
	
	/**
	 * Gets actual import configuration.
	 *
	 * @return import configuration
	 */	
	public ImportConfiguration getImportConfiguration() {
		return importConfiguration;
	}
	
	

	/**
	 * Add changed price list guid to context.
	 * 
	 * @param priceListGuid price list guid to add.
	 */
	public void addChangedPriceLists(final String priceListGuid) {
		changedPriceLists.add(priceListGuid);
	}
	
	/**
	 * Check whether price list was saved or updated during the import or not.
	 * 
	 * @param priceListGuid given price list.
	 * @return true if price list was saved or updated during the import.
	 */
	public boolean isChangedPriceLists(final String priceListGuid) {
		return changedPriceLists.contains(priceListGuid);
	}
	

	/**
	 * Add the code of product updated or saved during the import.
	 * 
	 * @param productCode product code
	 */
	public void addChangedProduct(final String productCode) {
		changedProducts.add(productCode);
	}

	/**
	 * Check whether product was saved or updated during the import or not.
	 * 
	 * @param productCode code of product to verify
	 * @return true if product has been changed during the import of products, false otherwise
	 */
	public boolean isProductChanged(final String productCode) {
		return changedProducts.contains(productCode);
	}
	
	/**
	 * Sets summary object to collect any kind of summary information except messages which are handled by IESummaryAppender.  
	 * @param summary Summary
	 */
	public void setSummary(final Summary summary) {
		this.summary = summary;
	}
	
	/**
	 * Gets summary associated for this export process.
	 * @return Summary
	 */
	public Summary getSummary() {
		return summary;
	}
}
