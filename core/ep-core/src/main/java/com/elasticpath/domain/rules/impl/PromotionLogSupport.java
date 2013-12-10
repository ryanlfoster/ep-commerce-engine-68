package com.elasticpath.domain.rules.impl;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.elasticpath.commons.util.extenum.AbstractExtensibleEnum;
/**
 * Provides Promotions-centric logging capabilities (as opposed to class-centric logging).
 */

public class PromotionLogSupport extends AbstractExtensibleEnum<PromotionLogSupport> {
	private static final long serialVersionUID = 1L;
	private static final String LOG_ROOT = "com.elasticpath.promotions";
	
	@SuppressWarnings("PMD.LoggerIsNotStaticFinal")
	private final Logger logger;

	/**
	 * Applied RuleCode specific logging support.
	 */
	public static final PromotionLogSupport RULECODE = new PromotionLogSupport(0, "RULECODE", "rulecode");
	// Add other log aspects here.
	
	/**
	 * Initialises logger with ordinal, name, and subcategory.
	 * 
	 * @param ordinal the ordinal
	 * @param name the name
	 * @param subCategory the subcategory for logging
	 */
	public PromotionLogSupport(final int ordinal, final String name, final String subCategory) {
		super(ordinal, name, PromotionLogSupport.class);
		logger = Logger.getLogger(LOG_ROOT + "." + subCategory);
	}

	@Override
	protected Class<PromotionLogSupport> getEnumType() {
		return PromotionLogSupport.class;
	}

	/**
	 * logs message if level is enabled.
	 * 
	 * @param level the logging level
	 * @param message the message to be logged
	 */
	public void log(final Level level, final String message) {
		if (logger.isEnabledFor(level)) {
			logger.log(level, message);
		}	
	}
	/**
	 * @return logger name
	 */
	public String getLoggerName() {
		return logger.getName();
	}

}
