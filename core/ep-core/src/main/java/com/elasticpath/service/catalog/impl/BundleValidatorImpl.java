package com.elasticpath.service.catalog.impl;

import java.util.List;


import com.elasticpath.domain.catalog.BundleConstituent;
import com.elasticpath.domain.catalog.ConstituentItem;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.service.catalog.BundleValidator;
import com.elasticpath.service.catalog.ProductBundleService;
import com.elasticpath.service.pricing.PaymentScheduleHelper;

/**
 * Validator for bundles.
 */
public class BundleValidatorImpl implements BundleValidator {

	private PaymentScheduleHelper paymentScheduleHelper;
	private ProductBundleService productBundleService;

	@Override
	public boolean doesAssignedBundleContainRecurringCharge(final ProductBundle bundle) {
		if (bundle.isCalculated()) {
			return false;
		}

		return doesBundleContainRecurringCharge(bundle);
	}

	@Override
	public boolean isRecurringChargeItemOnAssignedBundle(final ProductBundle bundle, final ConstituentItem item) {
		if (bundle.isCalculated()) {
			return false;
		}

		return doesConstituentItemContainRecurringCharge(item);
	}

	/**
	 * Checks if a bundle contains recurring charge products or product sku's. This method does not check for cyclical
	 * dependancy. Validation against cyclical dependancy should be done prior to calling this method.
	 *
	 * @param bundle the ProductBundle to check.
	 * @return true if the contains either a Product where at least one of the Products Sku's is a recurring charge,
	 *         or a ProductSku where the that specific ProductSku is a recurring charge.
	 */
	protected boolean doesBundleContainRecurringCharge(final ProductBundle bundle) {
		List<BundleConstituent> constituents = bundle.getConstituents();
		for (BundleConstituent constituent : constituents) {
			ConstituentItem item = constituent.getConstituent();

			// Check each items of the bundle
			if (doesConstituentItemContainRecurringCharge(item)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean areAllBundleConstituentsOfTheSamePricingMechanismType(final ProductBundle bundle) {
		List<BundleConstituent> constituents = bundle.getConstituents();
		for (BundleConstituent bundleConstituent : constituents) {

			if (!isConstituentPricingMechanismValidForThisBundle(bundle, bundleConstituent.getConstituent())) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks if a constituent item contains recurring charge products or product sku's.
	 *
	 * @param item the ConstituentItem to check.
	 * @return true if the constituent item contains either a Product where at least one of the Products Sku's is a recurring charge,
	 *         or a ProductSku where the that specific ProductSku is a recurring charge.
	 */
	protected boolean doesConstituentItemContainRecurringCharge(final ConstituentItem item) {
		if (item.isBundle()) {
			final ProductBundle childBundle = (ProductBundle) item.getProduct();
			if (doesBundleContainRecurringCharge(childBundle)) {
				return true;
			}
		} else if (item.isProduct()) {
			final Product product = item.getProduct();
			if (paymentScheduleHelper.isPaymentScheduleCapable(product)) {
				return true;
			}
		} else if (item.isProductSku()) {
			final ProductSku productSku = item.getProductSku();
			if (paymentScheduleHelper.getPaymentSchedule(productSku) != null) {
				return true;
			}
		}

		return false;
	}


	@Override
	public boolean isConstituentPricingMechanismValidForThisBundle(final ProductBundle bundle, final ConstituentItem item) {
		//at this point pricing mechanism is only associated with bundles
		if (item.isBundle()) {
			final ProductBundle childBundle = (ProductBundle) item.getProduct();
			//we are looking only at the first level, because we assume that the constituent is a valid bundle.
			if (childBundle.isCalculated().booleanValue() != bundle.isCalculated().booleanValue()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean isBundleSelectionRuleValid(final ProductBundle bundle) {
		if (bundle.getSelectionRule() != null
				&& bundle.getSelectionRule().getParameter() > bundle.getConstituents().size()) {
			return false;
		}
		return true;
	}

	@Override
	public boolean isBundleEmpty(final ProductBundle bundle) {
		return bundle.getConstituents().isEmpty();
	}


	@Override
	public Product getCyclicDependency(final ProductBundle bundle) {
		for (BundleConstituent constituent : bundle.getConstituents()) {
			final ConstituentItem constituentItem = constituent.getConstituent();
			if (constituentItem.isBundle()) {
				final Product retrievedConstituent = productBundleService.findByGuid(constituentItem.getProduct().getGuid());
				if (retrievedConstituent != null && ((ProductBundle) retrievedConstituent).hasDescendant(bundle)) {
					return retrievedConstituent;
				}
			}
		}
		return null;
	}

	public void setPaymentScheduleHelper(final PaymentScheduleHelper paymentScheduleHelper) {
		this.paymentScheduleHelper = paymentScheduleHelper;
	}

	public void setProductBundleService(final ProductBundleService productBundleService) {
		this.productBundleService = productBundleService;
	}
}
