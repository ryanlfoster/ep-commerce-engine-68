package com.elasticpath.domain.shipping.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Currency;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.apache.openjpa.persistence.DataCache;
import org.apache.openjpa.persistence.ElementDependent;
import org.apache.openjpa.persistence.jdbc.ElementForeignKey;
import org.apache.openjpa.persistence.jdbc.ElementJoinColumn;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.exception.SCCMCurrencyMissingException;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.impl.AbstractLegacyPersistenceImpl;
import com.elasticpath.domain.shipping.ShippingCostCalculationMethod;
import com.elasticpath.domain.shipping.ShippingCostCalculationParameter;
import com.elasticpath.domain.shoppingcart.ShoppingItem;

/**
 * A shippingCostCalculationMethod represents a method to be used for shipping cost calculation. It is a component of a shipping service level.
 */
@Entity
@Table(name = AbstractShippingCostCalculationMethodImpl.TABLE_NAME)
@DiscriminatorColumn(name = "TYPE", discriminatorType = DiscriminatorType.STRING)
@DataCache(enabled = false)
public abstract class AbstractShippingCostCalculationMethodImpl extends AbstractLegacyPersistenceImpl implements ShippingCostCalculationMethod {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TSHIPPINGCOSTCALCULATIONMETHOD";

	private String type;

	private Set<ShippingCostCalculationParameter> parameters;

	private long uidPk;

	/**
	 * Must be implemented by subclasses to return their type. (e.g. fixedBaseAndOrderTotalPercentageMethod. Should match bean name)
	 * 
	 * @return the kind of the action subclass.
	 */
	@Transient
	protected abstract String getMethodType();

	/**
	 * Get the type of shippingCostCalculationMethod, i.e. fixBase, fixBaseAndPercentageOfOrderTotal and etc.
	 * 
	 * @return the shippingCostCalculationMethod type.
	 */
	@Transient
	public String getType() {
		if (this.type == null) {
			this.type = getMethodType();
		} else if (!this.type.equals(this.getMethodType())) {
			throw new EpDomainException("Invalid shipping cost calculation type");
		}
		return this.type;
	}

	/**
	 * Set the type of shippingCostCalculationMethod.
	 * 
	 * @param type the type of shippingCostCalculationMethod.
	 */
	public void setType(final String type) {
		this.type = type;
	}

	/**
	 * Get the parameters associated with this shippingCostCalculationMethod.
	 * 
	 * @return the parameters
	 */
	@OneToMany(targetEntity = ShippingCostCalculationParameterImpl.class, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH,
			CascadeType.REMOVE }, fetch = FetchType.EAGER)
	@ElementJoinColumn(name = "SCCM_UID")
	@ElementDependent
	@ElementForeignKey(name = "TSHIPPINGCOSTCALCULATIONPARAM_IBFK_1")
	public Set<ShippingCostCalculationParameter> getParameters() {
		return this.parameters;
	}

	/**
	 * Set the parameters of this shippingCostCalculationMethod.
	 * 
	 * @param shippingCostCalculationParameters a set of <code>ShippingCostCalculationParamater</code> objects
	 */
	public void setParameters(final Set<ShippingCostCalculationParameter> shippingCostCalculationParameters) {
		this.parameters = shippingCostCalculationParameters;
	}

	@Override
	@Transient
	public boolean hasParameter(final String key, final Currency... currency) {
		if (currency.length > 1 || key == null) {
			throw new IllegalArgumentException("Illegal argument: key should not be null and only one currency can be matched.");
		}
		
		if (getParameters() != null) {
			for (ShippingCostCalculationParameter currParam : getParameters()) {
				if (currParam.getKey().equals(key) 
					&& ((!currParam.isCurrencyAware() && currency.length == 0) 
							|| (currParam.isCurrencyAware() && currency.length == 1 && currency[0].equals(currParam.getCurrency())))) {
					return true;
				}
			}
		}
		return false;
	}
	
	
	/**
	 * Returns the value of a parameter with the specified key if the parameter is not currencyAware.
	 * 
	 * @param key The key of the parameter to be returned
	 * @return the value of the parameter with the specified key or "" if no matching parameter was found.
	 */
	protected String getParamValue(final String key) {
		String value = "";
		if (getParameters() != null) {
			for (ShippingCostCalculationParameter currParam : getParameters()) {
				if (currParam.getKey().equals(key) && !currParam.isCurrencyAware()) {
					value = currParam.getValue();
					break;
				}
			}
		}
		return value;
	}

	 /**
	 * Returns the value of a parameter with the specified key for the specified currency. 
	 * 
	 * @param key The key of the parameter to be returned
	 * @param currency The currency, parameter should match to.
	 * @return the value of the parameter with the specified key and currency.
	 * 
	 * @throws SCCMCurrencyMissingException if matching param value for the passed currency is not found or if currency is null.
	 */
	protected String getParamValue(final String key, final Currency currency) 
		throws SCCMCurrencyMissingException {
		if (currency == null) {
			throw new SCCMCurrencyMissingException("Currency was null for key: " + key);			
		}

		String value = null;
		if (this.parameters != null) {
			for (ShippingCostCalculationParameter currParam : this.parameters) {
				// if currency parameter provided - then take it into consideration when getting appropriate parameter.
				if (currParam.isCurrencyAware() && currParam.getKey().equals(key) 
											&& currParam.getCurrency() != null && currency.equals(currParam.getCurrency())) {
					value = currParam.getValue();
					break;
				}
			}
		}
		
		if (StringUtils.isEmpty(value)) {
			throw new SCCMCurrencyMissingException("No value for " + key + " matching currency: "
						+ currency.getCurrencyCode());
		}

		return value;
	}

	@Override
	public List<ShippingCostCalculationParameter> getDefaultParameters(final List<Currency> currencyList) {
		List<ShippingCostCalculationParameter> paramsSet = new ArrayList<ShippingCostCalculationParameter>();
		for (String paramKey : getParameterKeys()) {
			ShippingCostCalculationParameter parameter = getShippingCostCalculationParameterBean();
			parameter.setKey(paramKey);
			/** now we have a parameter, currency-awareness flag was already set. */

			/** if this parameter is currency aware, then clone it for each currency. */
			if (parameter.isCurrencyAware()) {
				ShippingCostCalculationParameter parameterToSet = parameter;
				for (Currency currency : currencyList) {
					parameterToSet.setCurrency(currency);
					paramsSet.add(parameterToSet);
					parameterToSet = getShippingCostCalculationParameterBean();
					parameterToSet.setKey(paramKey);
				}
			} else {
				/** otherwise-set non-currency aware parameter. */
				paramsSet.add(parameter);
			}
		}
		return paramsSet;
	}
	
	/**
	 * @return a new {@code ShippingCostCalculationParameter} bean.
	 */
	@Transient
	ShippingCostCalculationParameter getShippingCostCalculationParameterBean() {
		return getBean(ContextIdNames.SHIPPING_COST_CALCULATION_PARAMETER);
	}

	/**
	 * Gets the unique identifier for this domain model object.
	 * 
	 * @return the unique identifier.
	 */
	@Id
	@Column(name = "UIDPK")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
	@TableGenerator(name = TABLE_NAME, table = "JPA_GENERATED_KEYS", pkColumnName = "ID", valueColumnName = "LAST_VALUE", pkColumnValue = TABLE_NAME)
	public long getUidPk() {
		return this.uidPk;
	}

	/**
	 * Sets the unique identifier for this domain model object.
	 * 
	 * @param uidPk the new unique identifier.
	 */
	public void setUidPk(final long uidPk) {
		this.uidPk = uidPk;
	}
	
	/**
	 * Adds up the price of all the line items that are shippable.
	 * 
	 * @param shoppingItems the items to calculate the subtotal of
	 * @return the subtotal amount of the shippable items
	 */
	protected BigDecimal getShippableItemsSubtotal(final Collection< ? extends ShoppingItem> shoppingItems) {
		BigDecimal subtotal = BigDecimal.ZERO.setScale(2);
		for (ShoppingItem shoppingItem : shoppingItems) {
			if (shoppingItem.getProductSku().isShippable()) {
				BigDecimal cartAmount = shoppingItem.getTotal().getAmount();
				subtotal = subtotal.add(cartAmount);
			}
		}
		return subtotal;
	}

	/**
	 * Calculate the total weight of the items.
	 * 
	 * @param shoppingItems the items to calculate the weight of
	 * @return totalWeight
	 */
	protected BigDecimal calculateTotalWeight(final Collection< ? extends ShoppingItem> shoppingItems) {
		BigDecimal totWeight = BigDecimal.ZERO;

		for (ShoppingItem shoppingItem : shoppingItems) {
			if (shoppingItem.getProductSku().isShippable()) {
				BigDecimal currItemWeight = shoppingItem.getProductSku().getWeight();
				if (currItemWeight != null && currItemWeight.compareTo(BigDecimal.ZERO) > 0) {
					totWeight = totWeight.add(currItemWeight.multiply(BigDecimal.valueOf(shoppingItem.getQuantity())));
				}
			}
		}

		return totWeight;
	}

}
