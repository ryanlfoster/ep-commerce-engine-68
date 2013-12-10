/*
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.domain.catalog.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Transient;

import org.apache.openjpa.persistence.Dependent;
import org.apache.openjpa.persistence.ElementDependent;
import org.apache.openjpa.persistence.FetchAttribute;
import org.apache.openjpa.persistence.FetchGroup;
import org.apache.openjpa.persistence.FetchGroups;
import org.apache.openjpa.persistence.jdbc.ElementForeignKey;
import org.apache.openjpa.persistence.jdbc.ElementJoinColumn;

import com.elasticpath.domain.catalog.AvailabilityCriteria;
import com.elasticpath.domain.catalog.BundleConstituent;
import com.elasticpath.domain.catalog.ConstituentItem;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.SelectionRule;
import com.elasticpath.persistence.support.FetchGroupConstants;

/**
 * The default implementation of <code>ProductBundle</code>.
 */
@Entity
@DiscriminatorValue("ProductBundleImpl")
@FetchGroups({
	@FetchGroup(name = FetchGroupConstants.BUNDLE_CONSTITUENTS, attributes = {
			@FetchAttribute(name = ProductBundleImpl.FETCH_ATTRIBUTE_NAME, recursionDepth = -1),
			@FetchAttribute(name = ProductBundleImpl.FETCH_CALCULATED),
			@FetchAttribute(name = ProductBundleImpl.FETCH_SELECTION_RULE) }),
	@FetchGroup(name = FetchGroupConstants.PRODUCT_INDEX, attributes = {
			@FetchAttribute(name = ProductBundleImpl.FETCH_ATTRIBUTE_NAME, recursionDepth = -1),
			@FetchAttribute(name = ProductBundleImpl.FETCH_CALCULATED),
			@FetchAttribute(name = ProductBundleImpl.FETCH_SELECTION_RULE) }),
	@FetchGroup(name = FetchGroupConstants.SHOPPING_ITEM_CHILD_ITEMS, attributes = {
			@FetchAttribute(name = ProductBundleImpl.FETCH_ATTRIBUTE_NAME, recursionDepth = -1),
			@FetchAttribute(name = ProductBundleImpl.FETCH_CALCULATED),
			@FetchAttribute(name = ProductBundleImpl.FETCH_SELECTION_RULE) }),
	@FetchGroup(name = FetchGroupConstants.ORDER_DEFAULT, attributes = {
			@FetchAttribute(name = ProductBundleImpl.FETCH_ATTRIBUTE_NAME, recursionDepth = -1),
			@FetchAttribute(name = ProductBundleImpl.FETCH_CALCULATED),
			@FetchAttribute(name = ProductBundleImpl.FETCH_SELECTION_RULE) })
})
public class ProductBundleImpl extends ProductImpl implements ProductBundle {

	private static final long serialVersionUID = 1903132337789196486L;

	/**
     * The fetch group attribute name.
     */
    public static final String FETCH_ATTRIBUTE_NAME = "constituentsInternal";

    /**
     * The calculated field name.
     */
    public static final String FETCH_CALCULATED = "calculated";

    /**
     *The Selection rule field name.
     */
    public static final String FETCH_SELECTION_RULE = "selectionRuleInternal";

	@Override
    public void initialize() {
    	super.initialize();
    	setCalculated(Boolean.FALSE);
    }


	private List<BundleConstituent> constituents = new ArrayList<BundleConstituent>();
	private SelectionRule selectionRule;
	private Boolean calculated;

	/**
	 * Gets the <code>Product</code> constituents associated with this <code>ProductBundle</code>.
	 *
	 * @return the set of bundles
	 */
    @Transient
	public List<BundleConstituent> getConstituents() {
		if (getConstituentsInternal() == null) {
			return Collections.emptyList();
		}
		return Collections.unmodifiableList(getConstituentsInternal());
	}

	/**
	 * @return a list of bundle constituents
	 */
	@OneToMany(targetEntity = BundleConstituentImpl.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@ElementJoinColumn(name = "BUNDLE_UID", nullable = false)
	@ElementForeignKey
	@ElementDependent
	@OrderBy("ordering")
	protected List<BundleConstituent> getConstituentsInternal() {
		return this.constituents;
	}

	/**
	 * Sets a list of bundle constituents.
	 * @param constituents a list of constituents to be set
	 */
	protected void setConstituentsInternal(final List<BundleConstituent> constituents) {
		this.constituents = constituents;
	}

	@Override
	public void addConstituent(final BundleConstituent constituent) {
		constituent.setOrdering(getConstituentsInternal().size());
		getConstituentsInternal().add(constituent);
	}

	@Override
	public void removeConstituent(final BundleConstituent constituent) {
		if (getConstituentsInternal().remove(constituent)) {
			resetAllOrderings(getConstituents());
		}
	}

	/**
	 * Reset subsequent constituents' orderings starting from a number.
	 *
	 * @param constituents
	 */
	private void resetAllOrderings(final List<BundleConstituent> constituents) {
		int order = 0;
		for (BundleConstituent constituent : constituents)  {
			constituent.setOrdering(order++);
		}
	}

	@Override
	public void moveConstituentUp(final BundleConstituent constituent) {
		final int index = getConstituentsInternal().indexOf(constituent);
		if (index > 0) {
			swap(getConstituentsInternal().get(index - 1), constituent);
		}
	}

	@Override
	public void moveConstituentDown(final BundleConstituent constituent) {
		final int index = getConstituentsInternal().indexOf(constituent);
		if (index >= 0 && index < getConstituentsInternal().size() - 1) {
			swap(constituent, getConstituentsInternal().get(index + 1));
		}
	}

	/**
	 * Swaps the position of two bundle constituents.
	 *
	 * @param bc1 bundle constituent 1
	 * @param bc2 bundle constituent 2
	 */
	protected void swap(final BundleConstituent bc1, final BundleConstituent bc2) {
		// swaps the ordering
		final int temp = bc1.getOrdering();
		bc1.setOrdering(bc2.getOrdering());
		bc2.setOrdering(temp);

		// swaps the ordering in the list
		getConstituentsInternal().set(bc1.getOrdering(), bc1);
		getConstituentsInternal().set(bc2.getOrdering(), bc2);
	}

	@Override
	public void removeAllConstituents() {
		getConstituentsInternal().clear();
	}

	/**
	 * Returns true if the product should not be displayed (e.g. in its category or as a search result). Checks all constituents, and if any
	 * constituent is hidden, the entire bundle will be hidden.
	 *
	 * @return true if the product should not be displayed
	 */
	@Override
	@Transient
	public boolean isHidden() {
		if (isBundleHidden()) {
			return true;
		}

		for (final BundleConstituent bundleConstituent : getConstituents()) {
			final Product constituent = bundleConstituent.getConstituent().getProduct();
			if (constituent.isHidden()) {
				return true;
			}
		}

		return false;
	}

	@Override
	@Transient
	public boolean isBundleHidden() {
		return super.isHidden();
	}

	/**
	 * Calculates the expected release date for a product.
	 *
	 * @return the expected release {@link Date}. If the product is a bundle, it returns the farthest release date for its constituents including its
	 *         bundle.
	 */
	@Override
	public Date getExpectedReleaseDate() {
		List<Date> validReleaseDate = new ArrayList<Date>();
		populateExpectedReleaseDate(validReleaseDate, this);

		// Sorts all the valid release dates (non-null dates) by ascending order.
		Collections.sort(validReleaseDate);

		if (validReleaseDate.isEmpty()) {
			return null;
		}

		// Returns the farthest release date
		return validReleaseDate.get(validReleaseDate.size() - 1);
	}

	/**
	 * Gets all non-null expected release dates from {@link Product}, including the release date of a {@link ProductBundle}.
	 *
	 * @param releaseDates the container.
	 * @param product the {@link Product}.
	 */
	private void populateExpectedReleaseDate(final List<Date> releaseDates, final Product product) {
		if (product instanceof ProductBundle) {
			ProductBundle bundle = (ProductBundle) product;
			for (BundleConstituent constituent : bundle.getConstituents()) {
				populateExpectedReleaseDate(releaseDates, constituent.getConstituent().getProduct());
			}

			return;
		}

		Date releaseDate = ((ProductImpl) product).getItemExpectedReleaseDate();
		if (releaseDate != null) {
			releaseDates.add(releaseDate);
		}
	}

	/**
	 * Get the end date. After the end date, the product will change to unavailable to customers. The earliest end date of the bundle and all
	 * constituents will be the bundle end date.
	 *
	 * @return the end date, might return null if there is no end date for the product
	 */
	@Override
	@Transient
	public Date getEndDate() {
		Date endDate = super.getEndDate();
		for (final BundleConstituent bundleConstituent : getConstituents()) {
			final ConstituentItem constituent = bundleConstituent.getConstituent();
			Date candidateEndDate = constituent.getEndDate(); // recursive if constituent is a bundle
			if (endDate == null) {
				endDate = candidateEndDate;
			} else if (endDate != null && candidateEndDate != null && candidateEndDate.before(endDate)) {
				endDate = candidateEndDate;
			}
		}

		return endDate;
	}

	@Override
	@Transient
	public Date getBundleEndDate() {
		return super.getEndDate();
	}

	/**
	 * Get the start date that this product will become available to customers. The latest end date of the bundle and all constituents will be the
	 * bundle start date.
	 *
	 * @return the start date
	 */
	@Override
	@Transient
	public Date getStartDate() {
		Date startDate = super.getStartDate();
		for (final BundleConstituent bundleConstituent : getConstituents()) {
			final ConstituentItem constituent = bundleConstituent.getConstituent();
			Date candidateStartDate = constituent.getStartDate();  // recursive if constituent is a bundle
			if (startDate == null) {
				startDate = candidateStartDate;
			} else if (startDate != null && candidateStartDate != null && candidateStartDate.after(startDate)) {
				startDate = candidateStartDate;
			}
		}

		return startDate;
	}

	@Override
	@Transient
	public Date getBundleStartDate() {
		return super.getStartDate();
	}

	@Override
	@Transient
	public int getMinOrderQty() {
		return 1;
	}

	@Override
	public boolean hasDescendant(final Product product) {
		if (equals(product)) {
			return true;
		}

		for (final BundleConstituent constituent : getConstituents()) {
			final Product constituent2 = constituent.getConstituent().getProduct();
			if ((constituent2 instanceof ProductBundle)
					&& ((ProductBundle) constituent2).hasDescendant(product)) {
				return true;
			}

		}
		return false;
	}

	@OneToOne(targetEntity = SelectionRuleImpl.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "bundle")
	@Dependent
	protected SelectionRule getSelectionRuleInternal() {
		return this.selectionRule;
	}

	/**
	 * @param rule the selection rule assigned to this bundle
	 */
	protected void setSelectionRuleInternal(final SelectionRule rule) {
		this.selectionRule = rule;
	}

	@Override
	@Transient
	public SelectionRule getSelectionRule() {
		return getSelectionRuleInternal();
	}

	@Override
	public void setSelectionRule(final SelectionRule rule) {
		if (rule != null) {
			rule.setBundle(this);
		}
		setSelectionRuleInternal(rule);
	}


	/**
	 * Gets the availability criteria - retrieves the availability criteria from constituents and returns the criteria
	 * based on the priority order (AVAILABLE_FOR_PRE_ORDER, AVAILABLE_FOR_BACK_ORDER, AVAILABLE_WHEN_IN_STOCK, ALWAYS_AVAILABLE).
	 *
	 * @return <code>AvailabilityCriteria</code>
	 */
	@Override
	@Transient
	public AvailabilityCriteria getAvailabilityCriteria() {
		AvailabilityCriteriaComparable criteriaComparable = new AvailabilityCriteriaComparable(AvailabilityCriteria.ALWAYS_AVAILABLE);
		for (BundleConstituent constituent : getConstituents()) {
			AvailabilityCriteriaComparable thisCriteriaComparable = new AvailabilityCriteriaComparable(
					constituent.getConstituent().getProduct().getAvailabilityCriteria());
			if (thisCriteriaComparable.getOrdinal() > criteriaComparable.getOrdinal()) {
				criteriaComparable = thisCriteriaComparable;
			}
		}
		return criteriaComparable.getAvailabilityCriteria();
	}


	/**
	 * Classes which allows the AvailabilityCriteria to be compared in the desired priority order.
	 */
	private class AvailabilityCriteriaComparable {
		private static final int PREORDER_ORDINAL = 3;
		private final AvailabilityCriteria availabilityCriteria;

		AvailabilityCriteriaComparable(final AvailabilityCriteria availabilityCriteria) {
			this.availabilityCriteria = availabilityCriteria;
		}

		int getOrdinal() {
			if (availabilityCriteria == AvailabilityCriteria.AVAILABLE_FOR_PRE_ORDER) {
				return PREORDER_ORDINAL;
			} else if (availabilityCriteria == AvailabilityCriteria.AVAILABLE_FOR_BACK_ORDER) {
				return 2;
			} else if (availabilityCriteria == AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK) {
				return 1;
			}
			return 0;
		}

		AvailabilityCriteria getAvailabilityCriteria() {
			return availabilityCriteria;
		}
	}




	@Override
	@Column(name = "CALCULATED")
	public Boolean isCalculated() {
		return calculated;
	}

	@Override
	public void setCalculated(final Boolean calculated) {
		this.calculated = calculated;
	}

}
