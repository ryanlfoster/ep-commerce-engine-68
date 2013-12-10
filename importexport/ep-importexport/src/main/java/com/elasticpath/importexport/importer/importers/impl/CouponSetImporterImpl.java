package com.elasticpath.importexport.importer.importers.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.elasticpath.domain.rules.Coupon;
import com.elasticpath.domain.rules.CouponConfig;
import com.elasticpath.domain.rules.CouponUsage;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.adapters.promotion.coupon.CouponSet;
import com.elasticpath.importexport.common.adapters.promotion.coupon.CouponSetAdapter;
import com.elasticpath.importexport.common.dto.promotion.coupon.CouponSetDTO;
import com.elasticpath.importexport.common.dto.promotion.coupon.CouponUsageDTO;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.importer.configuration.ImporterConfiguration;
import com.elasticpath.importexport.importer.context.ImportContext;
import com.elasticpath.importexport.importer.importers.CollectionsStrategy;
import com.elasticpath.importexport.importer.importers.SavingStrategy;
import com.elasticpath.importexport.importer.types.CollectionStrategyType;
import com.elasticpath.importexport.importer.types.DependentElementType;
import com.elasticpath.service.rules.CouponConfigService;
import com.elasticpath.service.rules.CouponService;
import com.elasticpath.service.rules.CouponUsageService;

/**
 * Coupon set importer for importing coupon set.
 */
public class CouponSetImporterImpl extends AbstractImporterImpl<CouponSet, CouponSetDTO> {
	/**
	 * Coupon set saving manager.
	 */
	private class CouponSetSavingManager implements SavingManager<CouponSet> {
		@Override
		public void save(final CouponSet persistable) {
			update(persistable);
		}

		@Override
		public CouponSet update(final CouponSet oldCouponSet) {
			CouponSet updatedCouponSet = new CouponSet();

			// coupon config
			CouponConfig updatedConfig = saveOrUpdateCouponConfig(oldCouponSet.getCouponConfig());
			updatedCouponSet.setCouponConfig(updatedConfig);

			// coupon
			List<Coupon> updatedCoupons = new ArrayList<Coupon>();
			for (Coupon oldCoupon : oldCouponSet.getCoupons()) {
				oldCoupon.setCouponConfig(updatedConfig);
				Coupon updatedCoupon = saveOrUpdateCoupon(oldCoupon);
				updatedCoupons.add(updatedCoupon);

			}
			updatedCouponSet.setCoupons(updatedCoupons);

			// coupon usage
			for (Entry<String, Collection<CouponUsage>> entry : oldCouponSet.getUsagesMap().entrySet()) {
				String couponCode = entry.getKey();
				Collection<CouponUsage> addedUsages = saveOrUpdateUsages(couponCode, entry.getValue());
				for (CouponUsage addedUsage : addedUsages) {
					updatedCouponSet.addUsage(couponCode, addedUsage);
				}
			}

			return updatedCouponSet;
		}

		private CouponConfig saveOrUpdateCouponConfig(final CouponConfig config) {
			CouponConfig couponConfig = couponConfigService.findByRuleCode(config.getRuleCode());
			if (couponConfig == null) {
				couponConfig = couponConfigService.add(config);
			} else {
				couponConfig.setDurationDays(config.getDurationDays());
				couponConfig.setGuid(config.getGuid());
				couponConfig.setLimitedDuration(config.isLimitedDuration());
				couponConfig.setUsageType(config.getUsageType());
				couponConfig.setRuleCode(config.getRuleCode());
				couponConfig.setUsageLimit(config.getUsageLimit());
				
				couponConfig = couponConfigService.update(couponConfig);
			}

			return couponConfig;
		}

		private Coupon saveOrUpdateCoupon(final Coupon coupon) {
			Coupon newCoupon = couponService.findByCouponCode(coupon.getCouponCode());
			if (newCoupon == null) {
				newCoupon = couponService.add(coupon);
			} else {
				newCoupon.setCouponCode(coupon.getCouponCode());
				newCoupon.setCouponConfig(coupon.getCouponConfig());
				
				newCoupon = couponService.update(newCoupon);					
			}

			return newCoupon;
		}

		// always adding coupon usages since import/export doesn't support update of coupon usage.
		private Collection<CouponUsage> saveOrUpdateUsages(final String couponCode, final Collection<CouponUsage> usages) {
			List<CouponUsage> updatedUsages = new ArrayList<CouponUsage>();
			for (CouponUsage usage : usages) {
				Coupon coupon = couponService.findByCouponCode(couponCode);
				usage.setCoupon(coupon);
				CouponUsage updatedUsage = saveOrUpdateUsage(usage);
				updatedUsages.add(updatedUsage);
			}

			return updatedUsages;
		}

		private CouponUsage saveOrUpdateUsage(final CouponUsage usage) {
			CouponUsage couponUsage = couponUsageService.findByCouponCodeAndEmail(usage.getCoupon().getCouponCode(), usage.getCustomerEmailAddress());
			if (couponUsage == null) {
				couponUsage = couponUsageService.add(usage);
			} else {
				couponUsage.setActiveInCart(usage.isActiveInCart());
				couponUsage.setCoupon(usage.getCoupon());
				couponUsage.setCustomerEmailAddress(usage.getCustomerEmailAddress());
				couponUsage.setUseCount(usage.getUseCount());
				
				couponUsage = couponUsageService.update(couponUsage);
			}

			return couponUsage;
		}
	}

	private CouponConfigService couponConfigService;

	private CouponService couponService;

	private CouponSetAdapter couponSetAdapter;

	private CouponUsageService couponUsageService;

	/**
	 * @param couponUsageService the couponUsageService to set
	 */
	public void setCouponUsageService(final CouponUsageService couponUsageService) {
		this.couponUsageService = couponUsageService;
	}

	@Override
	protected CouponSet findPersistentObject(final CouponSetDTO dto) {
		CouponSet couponSet = new CouponSet();

		CouponConfig couponConfig = couponConfigService.findByRuleCode(dto.getCouponConfigDTO().getRuleCode());
		if (couponConfig == null) {
			return null;
		}
		couponSet.setCouponConfig(couponConfig);

		for (String couponCode : dto.getCouponCodes()) {
			Coupon coupon = couponService.findByCouponCode(couponCode);
			if (coupon != null) {
				couponSet.getCoupons().add(coupon);
			}
		}

		for (CouponUsageDTO usageDTO : dto.getCouponUsageDTO()) {
			List<CouponUsage> usages = couponUsageService.findByCode(usageDTO.getCouponCode());
			for (CouponUsage usage : usages) {
				couponSet.addUsage(usage.getCoupon().getCouponCode(), usage);
			}

		}

		return couponSet;
	}

	@Override
	protected DomainAdapter<CouponSet, CouponSetDTO > getDomainAdapter() {
		return couponSetAdapter;
	}

	@Override
	protected String getDtoGuid(final CouponSetDTO dto) {
		return null;
	}

	@Override
	public String getImportedObjectName() {
		return CouponSetDTO.ROOT_ELEMENT;
	}

	@Override
	public void initialize(final ImportContext context, final SavingStrategy<CouponSet, CouponSetDTO> savingStrategy) {
		super.initialize(context, savingStrategy);
		getSavingStrategy().setSavingManager(new CouponSetSavingManager());
	}

	/**
	 * Setter for {@link CouponConfigService}.
	 * 
	 * @param couponConfigService {@link CouponConfigService}.
	 */
	public void setCouponConfigService(final CouponConfigService couponConfigService) {
		this.couponConfigService = couponConfigService;
	}

	/**
	 * Setter for {@link CouponService}.
	 * 
	 * @param couponService {@link CouponService}.
	 */
	public void setCouponService(final CouponService couponService) {
		this.couponService = couponService;
	}

	/**
	 * Setter for {@link CouponSetAdapter}.
	 * 
	 * @param couponSetAdapter {@link CouponSetAdapter}.
	 */
	public void setCouponSetAdapter(final CouponSetAdapter couponSetAdapter) {
		this.couponSetAdapter = couponSetAdapter;
	}

	@Override
	protected void setImportStatus(final CouponSetDTO object) {
		getStatusHolder().setImportStatus("(for coupon set " + object.getCouponConfigDTO().getCouponConfigCode() + ")");
	}

	@Override
	protected CollectionsStrategy<CouponSet, CouponSetDTO> getCollectionsStrategy() {
		return new CouponSetCollectionsStrategy(getContext().getImportConfiguration().getImporterConfiguration(JobType.COUPONSET));
	}

	@Override
	public Class<? extends CouponSetDTO> getDtoClass() {
		return CouponSetDTO.class;
	}

	/**
	 * Coupon set collection strategy.
	 */
	private class CouponSetCollectionsStrategy implements CollectionsStrategy<CouponSet, CouponSetDTO> {
		private final ImporterConfiguration importerConfiguration;

		public CouponSetCollectionsStrategy(final ImporterConfiguration importerConfiguration) {
			this.importerConfiguration = importerConfiguration;
		}

		@Override
		public boolean isForPersistentObjectsOnly() {
			return false;
		}

		@Override
		public void prepareCollections(final CouponSet domainObject, final CouponSetDTO dto) {
			if (importerConfiguration.getCollectionStrategyType(DependentElementType.COUPONSET).equals(CollectionStrategyType.CLEAR_COLLECTION)) {
				CouponConfig couponConfig = domainObject.getCouponConfig();
				if (couponConfig != null) {
					couponService.deleteCouponsByCouponConfigGuid(couponConfig.getGuid());
				}

				domainObject.setCoupons(new ArrayList<Coupon>());
				domainObject.setUsagesMap(new HashMap<String, Collection<CouponUsage>>());
			}
		}
	}
}
