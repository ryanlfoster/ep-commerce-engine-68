package com.elasticpath.service.catalog;

import com.elasticpath.domain.catalog.ConstituentItem;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;

/**
 * Validator for bundles.
 */
public interface BundleValidator {

	/**
 	 * Checks if an assigned bundle contains recurring charge products or product sku's. This method does not check for cyclically
	 * dependency. Validation against cyclically dependency should be done prior to calling this method.
	 * @param bundle the ProductBundle to check.
	 * @return true only if the bundle is an Assigned bundle and the bundle contains either a Product where at least one
	 * of the Products Sku's is a recurring charge, or a ProductSku where the that specific ProductSku is a recurring charge. All Calculated bundles
	 * guaranteed to return false.
	 */
	boolean doesAssignedBundleContainRecurringCharge(final ProductBundle bundle);

	/**
	 * Checks if a constituent item can be added to a bundle.
	 * Recurring charge items cannot be added to a bundle with an Assigned Pricing Mechanism.
	 * @param bundle the ProductBundle being created or edited.
	 * @param item the constituent item being added.
	 * @return true if the bundle of an Assigned Pricing Mechanism and the item is a Recurring charge item, false otherwise.
	 */
	boolean isRecurringChargeItemOnAssignedBundle(final ProductBundle bundle, final ConstituentItem item);

	/**
	 * Check that the selection rule on the bundle isn't more than the bundle's number of constituents.
	 * @param bundle the bundle
	 * @return true if SelectionRule is valid, or doesn't apply
	 */
	boolean isBundleSelectionRuleValid(final ProductBundle bundle);

	/**
	 * Checks if the pricing mechanism of the parent is matching the inner constituent.
	 * @param bundle - the bundle that needs to be checked
	 * @param item - the item in the bundle
	 * @return <code>true</code> if the pricing Mechanism of constituent match the one on the parent bundle
	 */
	boolean isConstituentPricingMechanismValidForThisBundle(final ProductBundle bundle, final ConstituentItem item);


	/**
	 * Searches the given product's bundle constituent tree to
	 * find any cyclical references and returns the offending constituent.
	 * @param bundle the root product bundle
	 * @return the constituent that contains a cyclical reference, or null if there
	 * are no cyclical dependencies or if the given product is not a bundle.
	 */
	Product getCyclicDependency(final ProductBundle bundle);
	
	/**
	 * Checks if all bundle constituents have the same pricing mechanism type and the root bundle.
	 * @param bundle the product bundle
	 * @return true if all bundle constituents have the same pricing mechanism type and the root bundle
	 */
	boolean areAllBundleConstituentsOfTheSamePricingMechanismType(final ProductBundle bundle);
	
	
	/**
	 * Checks if is bundle empty.
	 * @param bundle the bundle
	 * @return true, if is bundle empty
	 */
	boolean isBundleEmpty(final ProductBundle bundle);
}
