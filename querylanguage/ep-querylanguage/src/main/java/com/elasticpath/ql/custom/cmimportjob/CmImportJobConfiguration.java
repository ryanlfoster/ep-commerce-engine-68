package com.elasticpath.ql.custom.cmimportjob;

import com.elasticpath.ql.parser.AbstractEpQLCustomConfiguration;

/**
 * Holds mapping between EPQL fields and field descriptors for Import Jobs.
 */
public class CmImportJobConfiguration extends AbstractEpQLCustomConfiguration {

	@Override
	public void initialize() {
		setQueryPrefix("SELECT im.guid FROM ImportJobImpl im");
		setQueryPostfix("");
	}
	
}