package com.elasticpath.domain.catalogview;

import java.util.List;
import java.util.Set;

import com.elasticpath.common.dto.SkuInventoryDetails;
import com.elasticpath.domain.catalog.Availability;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductAssociation;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.service.catalogview.impl.InventoryMessage;

/**
 * A storefront focused extension to Product.
 */
public interface StoreProduct extends Product {

	/**
	 * Returns <code>true</code> if the product is available for purchase.
	 *
	 * @return <code>true</code> if the product is available for purchase, <code>false</code>
	 *         otherwise
	 */
	boolean isAvailable();

	/**
	 * Returns <code>true</code> if the product can be displayed for this store.
	 * Checks whether the product is not hidden, current date is within the product's date range
	 * and that the product has at least one SKU in stock or is out of stock but should be
	 * visible.
	 *
	 * @return <code>true</code> if the product is available for purchase, <code>false</code>
	 *         otherwise
	 */
	boolean isDisplayable();

	/**
	 * Verifies that a product could be purchased in the current store the product exists in.
	 *
	 * @return true if the product is available to be purchased
	 */
	boolean isPurchasable();

	/**
	 * Gets the message availability code for a sku belonging to this product.
	 *
	 * @param skuUid the product sku UIDPK
	 * @return {@link InventoryMessage}
	 */
	InventoryMessage getMessageCode(long skuUid);

	/**
	 * Gets the inventory availability message code for the specified SKU.
	 *
	 * @param productSku the product SKU
	 * @return {@link InventoryMessage}
	 */
	InventoryMessage getMessageCode(ProductSku productSku);

	/**
	 * Gets the availability of the SKU with the specified sku code.
	 *
	 * @param skuCode the SKU code
	 * @return true if available
	 */
	boolean isSkuAvailable(String skuCode);

	/**
	 * Get the <code>ProductAssociation</code>s for merchandising this product.
	 *
	 * @return a set of <code>ProductAssociation</code>s
	 */
	Set<ProductAssociation> getProductAssociations();

	/**
	 * Get a sorted list of <code>ProductAssociation</code>s for merchandising this product.
	 *
	 * @return a sorted list of <code>ProductAssociation</code>s
	 */
	List<ProductAssociation> getSortedProductAssociations();

	/**
	 * Returns a set of <code>ProductAssociation</code>s with the given association type. Only returns associations where the date range is valid
	 * for the current date.
	 *
	 * @param associationType the type of the association. The association type is a constant value defined on the <code>ProductAssociation</code>
	 *            interface.
	 * @return a set of all defined associations of the specified type
	 */
	Set<ProductAssociation> getAssociationsByType(final int associationType);

	/**
	 * Returns a set of <code>ProductAssociation</code>s with the given association type.
	 *
	 * @param associationType the type of the association. The association type is a constant value defined on the <code>ProductAssociation</code>
	 *            interface.
	 * @param includeAll Set to true to return associations that are not in their valid date range
	 * @return a set of all defined associations of the specified type
	 */
	Set<ProductAssociation> getAssociationsByType(final int associationType, final boolean includeAll);

	/**
	 * @deprecated Use {@link #getAssociationsByType(int, Set)}
	 *
	 * @param associationType the type of the association. The association type is a constant value defined on the <code>ProductAssociation</code>
	 *            interface.
	 * @param filterTargetProducts Excludes any provided target products from list of returned associations.
	 * @return a set of all defined associations of the specified type
	 */
	@Deprecated
	Set<ProductAssociation> getAssociationsByType(final int associationType, final List<Product> filterTargetProducts);

	/**
	 * Returns a set of <code>ProductAssociation</code>s with the given association type.
	 *
	 * @param associationType the type of the association. The association type is a constant value defined on the <code>ProductAssociation</code>
	 *            interface.
	 * @param filterTargetProducts Excludes any provided target products from list of returned associations.
	 * @return a set of all defined associations of the specified type
	 */
	Set<ProductAssociation> getAssociationsByType(final int associationType, final Set<Product> filterTargetProducts);

	/**
	 * Return the product association with the specified UID.
	 *
	 * @param associationUid the UID of the <code>ProductAssociation</code> to be returned.
	 * @return the corresponding <code>ProductAssociation</code> or null if no matching association is found.
	 */
	ProductAssociation getAssociationById(final long associationUid);

	/**
	 * Sets the StoreProduct's set of associations.
	 * @param productAssociations the product associations to set.
	 */
	void setProductAssociations(final Set<ProductAssociation> productAssociations);

	/**
	 *
	 * @return wrapped product.
	 */
	Product getWrappedProduct();

	/**
	 *
	 * @param skuCode The skuCode to check.
	 * @return The inventory details for {@code skuCode}.
	 */
	SkuInventoryDetails getInventoryDetails(final String skuCode);
	
	/**
	 * Gets the availability.
	 *
	 * @return the availability
	 */
	Availability getAvailability();

}
