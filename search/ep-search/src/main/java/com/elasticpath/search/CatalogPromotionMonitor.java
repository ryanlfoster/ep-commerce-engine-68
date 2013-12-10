package com.elasticpath.search;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.elasticpath.commons.exception.EpDateBindException;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.rules.RuleScenarios;
import com.elasticpath.domain.search.UpdateType;
import com.elasticpath.persistence.PropertiesDao;
import com.elasticpath.service.rules.RuleService;
import com.elasticpath.service.search.CatalogPromoQueryComposerHelper;
import com.elasticpath.service.search.IndexNotificationService;
import com.elasticpath.service.search.query.FilteredSearchCriteria;

/**
 * Monitor's catalog promotions for changes and updates indexes as appropriate. Only the product
 * index is modified.
 * <p>
 * <i>NOTE</i>: This only monitors promotions that have been automatically started or ended and
 * not those that have been edited (or initially saved) to be started/expired.
 * </p>
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
public class CatalogPromotionMonitor {

	private static final String PROPERTIES_FILENAME = "catalogPromo";

	private static final String LAST_CHECK_DATE = "lastCheck";
	
	/** Number of criterias created for a rule with no conditions. */
	private static final int CRITERIAS_PER_EMPTY_RULE = 3;

	private PropertiesDao propertiesDao;

	private RuleService ruleService;

	private IndexNotificationService indexNotificationService;

	private Properties properties;

	private final DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
	
	private CatalogPromoQueryComposerHelper catalogPromoQueryComposerHelper;
	
	private int maxClauseCount;

	private static final Logger LOG = Logger.getLogger(CatalogPromotionMonitor.class);
	
	/**
	 * Checks for catalog promotion changes and notifies SOLR of these changes.
	 */
	public void checkCatalogPromotionChanges() {
		final long startTime = System.currentTimeMillis();
		LOG.info("Start check for catalog promotion changes quartz job at: " + new Date(startTime));
		
		Collection<Long> changedPromos = ruleService.findChangedPromoUids(getLastCheckDate(),
				RuleScenarios.CATALOG_BROWSE_SCENARIO);
		if (!changedPromos.isEmpty()) {
			batchNotifyPromoChanged(ruleService.findByUids(changedPromos));
		}
		setLastCheckDate();
		
		LOG.info("Check for catalog promotion changes quartz job completed in (ms): " + (System.currentTimeMillis() - startTime));
	}

	/**
	 * Notifies that the given collection of promotion rules have changed.
	 * 
	 * @param promoRules the collection of promotion rules that have changed
	 */
	protected void notifyPromoChanged(final Collection<Rule> promoRules) {
		FilteredSearchCriteria< ? > searchCriteria = catalogPromoQueryComposerHelper.constructSearchCriteria(promoRules);
		if (!searchCriteria.isEmpty()) {
			indexNotificationService.addViaQuery(UpdateType.UPDATE, searchCriteria, false);
		}
	}
	
	/**
	 * Notifies that the given collection of promotion rules have changed. This will break
	 * the notifications into chunks that have a search criteria size of less than the given
	 * limit.
	 * 
	 * @param promoRules the collection of promotion rules that have changed
	 */
	protected void batchNotifyPromoChanged(final Collection<Rule> promoRules) {

		int criteriaSize = 0;
		Collection<Rule> ruleSubset = new HashSet<Rule>();
		for (Rule rule : promoRules) {
			int ruleCriteriaSize = rule.getConditions().size() + CRITERIAS_PER_EMPTY_RULE;
			if (criteriaSize + ruleCriteriaSize <= getMaxClauseCount()) {
				criteriaSize += ruleCriteriaSize;
			} else {
				notifyPromoChanged(ruleSubset);
				ruleSubset.clear();
				criteriaSize = ruleCriteriaSize;
			}
			ruleSubset.add(rule);
		}
		if (!ruleSubset.isEmpty()) {
			notifyPromoChanged(ruleSubset);
		}
		
	}

	private Date getLastCheckDate() {
		Date date = null;
		final String dateString = getProperties().getProperty(LAST_CHECK_DATE);
		if (dateString != null && dateString.length() > 0) {
			synchronized (dateFormatter) {
				try {
					date = dateFormatter.parse(dateString);
				} catch (ParseException e) {
					throw new EpDateBindException(dateString
							+ " is a bad date string. The required format is: yyyy-MM-dd'T'HH:mm:ss'Z'.", e);
				}
			}
		}
		return date;
	}

	private void setLastCheckDate() {
		synchronized (dateFormatter) {
			getProperties().setProperty(LAST_CHECK_DATE, dateFormatter.format(new Date()));
		}
		propertiesDao.storePropertiesFile(getProperties(), PROPERTIES_FILENAME);
	}

	private Properties getProperties() {
		if (properties == null) {
			properties = propertiesDao.getPropertiesFile(PROPERTIES_FILENAME);
		}
		return properties;
	}

	/**
	 * Sets the {@link PropertiesDao} instance to use.
	 * 
	 * @param propertiesDao the {@link PropertiesDao} instance to use
	 */
	public void setPropertiesDao(final PropertiesDao propertiesDao) {
		this.propertiesDao = propertiesDao;
	}

	/**
	 * Sets the {@link RuleService} instance to use.
	 * 
	 * @param ruleService the {@link RuleService} instance to use
	 */
	public void setRuleService(final RuleService ruleService) {
		this.ruleService = ruleService;
	}

	/**
	 * Sets the {@link IndexNotificationService} instance to use.
	 * 
	 * @param indexNotificationService the {@link IndexNotificationService} instance to use
	 */
	public void setIndexNotificationService(final IndexNotificationService indexNotificationService) {
		this.indexNotificationService = indexNotificationService;
	}

	/**
	 * Sets the {@link CatalogPromoQueryComposerHelper} instance to use.
	 * 
	 * @param catalogPromoQueryComposerHelper the {@link CatalogPromoQueryComposerHelper} instance
	 *            to use
	 */
	public void setCatalogPromoQueryComposerHelper(final CatalogPromoQueryComposerHelper catalogPromoQueryComposerHelper) {
		this.catalogPromoQueryComposerHelper = catalogPromoQueryComposerHelper;
	}

	/**
	 *
	 * @param maxClauseCount the maxClauseCount to set
	 */
	public void setMaxClauseCount(final int maxClauseCount) {
		this.maxClauseCount = maxClauseCount;
	}

	/**
	 *
	 * @return the maxClauseCount
	 */
	public int getMaxClauseCount() {
		return maxClauseCount;
	}
}
