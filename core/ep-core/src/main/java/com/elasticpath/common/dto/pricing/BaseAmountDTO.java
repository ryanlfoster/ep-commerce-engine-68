package com.elasticpath.common.dto.pricing;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.common.dto.Dto;

/**
 * The data transfer object for the <code>BaseAmount</code>.
 */
@XmlRootElement(name = BaseAmountDTO.ROOT_ELEMENT)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { })
public class BaseAmountDTO implements Dto {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 20090928L;

	/** The name of root element in XML representation. */
	public static final String ROOT_ELEMENT = "base_amount";

	@XmlElement(name = "guid", required = true)
	private String guid;

	@XmlElement(name = "object_guid", required = true)
	private String objectGuid;

	@XmlElement(name = "object_type", required = true)
	private String objectType;

	@XmlElement(name = "quantity", required = true)
	private BigDecimal quantity;

	@XmlElement(name = "list_price")
	private BigDecimal listValue;

	@XmlElement(name = "sale_price")
	private BigDecimal saleValue;

	@XmlElement(name = "price_list_guid", required = true)
	private String priceListDescriptorGuid;

	private String productCode;

	private String productName;

	private String skuCode;

	private String skuConfiguration;

	private boolean multiSku;

	private String paymentScheduleName;

	/**
	 * Empty constructor.
	 */
	public BaseAmountDTO() {
		super();
	}

	/**
	 * A constructor with BaseAmountDTO in parameters. Allow to have a copy of source object.
	 * 
	 * @param baseAmountDTO source object
	 */
	public BaseAmountDTO(final BaseAmountDTO baseAmountDTO) {
		super();
		this.guid = baseAmountDTO.getGuid();
		this.objectGuid = baseAmountDTO.getObjectGuid();
		this.objectType = baseAmountDTO.getObjectType();
		this.priceListDescriptorGuid = baseAmountDTO.getPriceListDescriptorGuid();
		this.quantity = baseAmountDTO.getQuantity();
		this.multiSku = baseAmountDTO.isMultiSku();
		this.listValue = baseAmountDTO.getListValue();
		this.saleValue = baseAmountDTO.getSaleValue();
		this.productCode = baseAmountDTO.getProductCode();
		this.productName = baseAmountDTO.getProductName();
		this.skuCode = baseAmountDTO.getSkuCode();
		this.skuConfiguration = baseAmountDTO.getSkuConfiguration();
		this.paymentScheduleName = baseAmountDTO.getPaymentScheduleName();
	}

	/**
	 * Gets the GUID of the base amount.
	 * 
	 * @return the GUID of the BaseAmount
	 */
	public String getGuid() {
		return this.guid;
	}

	/**
	 * Set the GUID of the Base Amount.
	 * 
	 * @param guid GUID of the base amount
	 */
	public void setGuid(final String guid) {
		this.guid = guid;
	}

	/**
	 * Gets the object GUID of the base amount.
	 * 
	 * @return the object GUID of the Product/SKU for this base amount.
	 */
	public String getObjectGuid() {
		return this.objectGuid;
	}

	/**
	 * Set the object GUID of the Product/SKU for this base amount.
	 * 
	 * @param objectGuid object GUID of the Product/SKU for this base amount
	 */
	public void setObjectGuid(final String objectGuid) {
		this.objectGuid = objectGuid;
	}

	/**
	 * Get the object type for this base amount.
	 * 
	 * @return Type Product or SKU
	 */
	public String getObjectType() {
		return this.objectType;
	}

	/**
	 * Set the object type of the Product/SKU.
	 * 
	 * @param objectType object type of the Product/SKU
	 */
	public void setObjectType(final String objectType) {
		this.objectType = objectType;
	}

	/**
	 * Gets the quantity threshold at which this particular List Price and Sale Price take affect for a particular Product or SKU when in a Price
	 * List.
	 * 
	 * @return the quantity of the base amount
	 */
	public BigDecimal getQuantity() {
		return this.quantity;
	}

	/**
	 * Set the quantity of the Product/SKU.
	 * 
	 * @param quantity quantity of the Product/SKU
	 */
	public void setQuantity(final BigDecimal quantity) {
		this.quantity = quantity;
	}

	/**
	 * Gets the list value.
	 * 
	 * @return the list value
	 */
	public BigDecimal getListValue() {
		return this.listValue;
	}

	/**
	 * Set the list value of the Product/SKU.
	 * 
	 * @param listValue list value of the Product/SKU
	 */
	public void setListValue(final BigDecimal listValue) {
		this.listValue = listValue;
	}

	/**
	 * Gets the sale value of the base amount.
	 * 
	 * @return the sale value.
	 */
	public BigDecimal getSaleValue() {
		return this.saleValue;
	}

	/**
	 * Set the sale value of the Product/SKU.
	 * 
	 * @param saleValue sale value of the Product/SKU
	 */
	public void setSaleValue(final BigDecimal saleValue) {
		this.saleValue = saleValue;
	}

	/**
	 * Gets the price list descriptor GUID of the base amount.
	 * 
	 * @return the associated <code>PriceListDescriptor</code> GUID
	 */
	public String getPriceListDescriptorGuid() {
		return this.priceListDescriptorGuid;
	}

	/**
	 * Set the associated <code>PriceListDescriptor</code> GUID.
	 * 
	 * @param priceListDescriptorGuid GUID of the price list descriptor
	 */
	public void setPriceListDescriptorGuid(final String priceListDescriptorGuid) {
		this.priceListDescriptorGuid = priceListDescriptorGuid;
	}

	/**
	 * @return product code.
	 */
	public String getProductCode() {
		return productCode;
	}

	/**
	 * Set the product code.
	 * 
	 * @param productCode to sset.
	 */
	public void setProductCode(final String productCode) {
		this.productCode = productCode;
	}

	/**
	 * @return product code
	 */
	public String getProductName() {
		return productName;
	}

	/**
	 * Set product name.
	 * 
	 * @param productName to set
	 */
	public void setProductName(final String productName) {
		this.productName = productName;
	}

	/**
	 * @return sku configuration
	 */
	public String getSkuConfiguration() {
		return skuConfiguration;
	}

	/**
	 * Set the sku configuration.
	 * 
	 * @param skuConfiguration to set.
	 */
	public void setSkuConfiguration(final String skuConfiguration) {
		this.skuConfiguration = skuConfiguration;
	}

	/**
	 * @return sku code
	 */
	public String getSkuCode() {
		return skuCode;
	}

	/**
	 * Set the sku code.
	 * 
	 * @param skuCode to set
	 */
	public void setSkuCode(final String skuCode) {
		this.skuCode = skuCode;
	}

	/**
	 * @return true if product has multiple sku
	 */
	public boolean isMultiSku() {
		return multiSku;
	}

	/**
	 * Set multi sku flag.
	 * 
	 * @param multiSku flag
	 */
	public void setMultiSku(final boolean multiSku) {
		this.multiSku = multiSku;
	}

	/**
	 * Get the associated {@link com.elasticpath.domain.subscriptions.PaymentSchedule}'s name.
	 * 
	 * @return name of the associated paymentSchedule
	 */
	public String getPaymentScheduleName() {
		return paymentScheduleName;
	}

	/**
	 * Set the associated {@link com.elasticpath.domain.subscriptions.PaymentSchedule}'s name .
	 * 
	 * @param paymentScheduleName associated paymentSchedule's name to set
	 */
	public void setPaymentScheduleName(final String paymentScheduleName) {
		this.paymentScheduleName = paymentScheduleName;
	}

	/**
	 * Verify the equality of the object.
	 * 
	 * @param obj a <code>BaseAmountDTO</code>
	 * @return boolean equals or not
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof BaseAmountDTO)) {
			return false;
		}
		BaseAmountDTO baseAmountDto = (BaseAmountDTO) obj;

		boolean isEqual = StringUtils.equals(getObjectGuid(), baseAmountDto.getObjectGuid())
				&& StringUtils.equals(getObjectType(), baseAmountDto.getObjectType()) && this.getQuantity().equals(baseAmountDto.getQuantity())
				&& StringUtils.equalsIgnoreCase(this.getGuid(), baseAmountDto.getGuid())
				&& priceEquals(this.getListValue(), baseAmountDto.getListValue()) && priceEquals(this.getSaleValue(), baseAmountDto.getSaleValue());

		return isEqual;
	}

	private boolean priceEquals(final BigDecimal thisPrice, final BigDecimal otherPrice) {
		if (thisPrice == null) {
			return otherPrice == null;
		}
		if (otherPrice == null) {
			return false;
		}
		return thisPrice.compareTo(otherPrice) == 0;
	}

	/**
	 * Returns hashCode.
	 * 
	 * @return int hash code
	 */
	@Override
	public int hashCode() {
		if (this.guid != null) {
			return this.guid.hashCode();
		}

		return 0;
	}
}
