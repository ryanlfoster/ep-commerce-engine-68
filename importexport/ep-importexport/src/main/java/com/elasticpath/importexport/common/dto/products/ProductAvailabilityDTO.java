package com.elasticpath.importexport.common.dto.products;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import com.elasticpath.common.dto.Dto;
import com.elasticpath.domain.catalog.AvailabilityCriteria;

/**
 * The implementation of the <code>Dto</code> interface that contains data of product availability object.
 * <p>
 * This implementation designed for JAXB to working with xml representation of data
 */
@XmlAccessorType(XmlAccessType.NONE)
public class ProductAvailabilityDTO implements Dto {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@XmlElement(name = "storevisible", required = true)
	private boolean storeVisible;

	@XmlElement(name = "notsoldseperately")
	private Boolean notSoldSeparately = Boolean.FALSE;

	@XmlElement(name = "enabledate", required = true)
	private Date startDate;

	@XmlElement(name = "disabledate")
	private Date endDate;

	@XmlElement(name = "minorderqty", required = true)
	private int minOrderQty;

	@XmlElement(name = "availabilityrule", required = true)
	private AvailabilityCriteria availabilityCriteria;

	@XmlElement(name = "orderlimit", required = true)
	private int preOrBackOrderLimit;

	@XmlElement(name = "expectedreleasedate")
	private Date expectedReleaseDate;

	/**
	 * Returns true if the product should be displayed.
	 * 
	 * @return true if the product should be displayed
	 */
	public boolean isStoreVisible() {
		return storeVisible;
	}

	/**
	 * Sets to true if the product should be displayed.
	 * 
	 * @param storevisible true if the product should be displayed
	 */
	public void setStorevisible(final boolean storevisible) {
		this.storeVisible = storevisible;
	}

	/**
	 * Gets the start date that this product will become available to customers.
	 * 
	 * @return the start date
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * Sets the start date that this product will become valid.
	 * 
	 * @param startDate the start date
	 */
	public void setStartDate(final Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * Gets the end date. After the end date, the product will change to unavailable to customers.
	 * 
	 * @return the end date
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * Sets the end date. Precondition: endDate is after the start date
	 * 
	 * @param endDate the end date
	 */
	public void setEndDate(final Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * Gets the minimum order quantity of the product.
	 * 
	 * @return the minimum order quantity of the product.
	 */
	public int getMinOrderQty() {
		return minOrderQty;
	}

	/**
	 * Sets the <code>MinOrderQty</code> associated with this <code>Product</code>.
	 * 
	 * @param minOrderQty - the minimum order quantity of the product.
	 */
	public void setMinOrderQty(final int minOrderQty) {
		this.minOrderQty = minOrderQty;
	}

	/**
	 * Gets the availability criteria.
	 * 
	 * @return <code>AvailabilityCriteria</code>
	 */
	public AvailabilityCriteria getAvailabilityCriteria() {
		return availabilityCriteria;
	}

	/**
	 * Sets the availability criteria.
	 * 
	 * @param availabilityCriteria <code>AvailabilityCriteria</code>
	 */
	public void setAvailabilityCriteria(final AvailabilityCriteria availabilityCriteria) {
		this.availabilityCriteria = availabilityCriteria;
	}

	/**
	 * Gets the pre or back order limit.
	 * 
	 * @return order limit
	 */
	public int getPreOrBackOrderLimit() {
		return preOrBackOrderLimit;
	}

	/**
	 * Sets the pre or back order limit.
	 * 
	 * @param preOrBackOrderLimit the order limit
	 */
	public void setPreOrBackOrderLimit(final int preOrBackOrderLimit) {
		this.preOrBackOrderLimit = preOrBackOrderLimit;
	}

	/**
	 * Gets the expected release date.
	 * 
	 * @return <code>Date</code>
	 */
	public Date getExpectedReleaseDate() {
		return expectedReleaseDate;
	}

	/**
	 * Sets the expected release date for a product available on pre order.
	 * 
	 * @param expectedReleaseDate the release date
	 */
	public void setExpectedReleaseDate(final Date expectedReleaseDate) {
		this.expectedReleaseDate = expectedReleaseDate;
	}

	/**
	 * @return true if product can not be sold separately (outside of bundle)
	 */
	public boolean isNotSoldSeparately() {
		return notSoldSeparately;
	}
	
	/**
	 * @param notSoldSeparately true if product is not to be sold outside of bundles
	 */
	public void setNotSoldSeparately(final boolean notSoldSeparately) {
		this.notSoldSeparately = notSoldSeparately;
	}

}
