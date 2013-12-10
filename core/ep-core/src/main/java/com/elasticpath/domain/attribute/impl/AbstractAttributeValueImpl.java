/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.attribute.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.openjpa.persistence.FetchAttribute;
import org.apache.openjpa.persistence.FetchGroup;
import org.apache.openjpa.persistence.jdbc.ForeignKey;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.constants.GlobalConstants;
import com.elasticpath.commons.constants.ImportConstants;
import com.elasticpath.commons.exception.EpBindException;
import com.elasticpath.commons.util.Utility;
import com.elasticpath.commons.util.impl.ConverterUtils;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeType;
import com.elasticpath.domain.attribute.AttributeValueWithType;
import com.elasticpath.domain.impl.AbstractLegacyPersistenceImpl;
import com.elasticpath.persistence.support.FetchGroupConstants;

/**
 * The default implementation of <code>AttributeValue</code>.
 */
@MappedSuperclass
@FetchGroup(name = FetchGroupConstants.ATTRIBUTE_VALUES, attributes = { @FetchAttribute(name = "localizedAttributeKey"),
		@FetchAttribute(name = "shortTextValue"), @FetchAttribute(name = "integerValue"), @FetchAttribute(name = "longTextValue"),
		@FetchAttribute(name = "decimalValue"), @FetchAttribute(name = "booleanValue"), @FetchAttribute(name = "dateValue"),
		@FetchAttribute(name = "attribute"), @FetchAttribute(name = "attributeTypeId") })
public abstract class AbstractAttributeValueImpl extends AbstractLegacyPersistenceImpl implements AttributeValueWithType {

	private static final long serialVersionUID = 5000000001L;

	private String localizedAttributeKey;

	private String shortTextValue;

	private Integer integerValue;

	private String longTextValue;

	private BigDecimal decimalValue;

	private Boolean booleanValue;

	private Date dateValue;

	private Attribute attribute;

	private int attributeTypeId;

	@Override
	@Basic
	@Column(name = "SHORT_TEXT_VALUE")
	public String getShortTextValue() {
		return shortTextValue;
	}

	@Override
	public void setShortTextValue(final String shortTextValue) {
		this.shortTextValue = shortTextValue;
	}

	@Override
	@Lob
	@Column(name = "LONG_TEXT_VALUE", length = GlobalConstants.LONG_TEXT_MAX_LENGTH)
	public String getLongTextValue() {
		return longTextValue;
	}

	@Override
	public void setLongTextValue(final String longTextValue) {
		this.longTextValue = longTextValue;
	}

	@Override
	@Basic
	@Column(name = "INTEGER_VALUE")
	public Integer getIntegerValue() {
		return integerValue;
	}

	@Override
	public void setIntegerValue(final Integer integerValue) {
		this.integerValue = integerValue;
	}

	@Override
	@Basic
	@Column(name = "DECIMAL_VALUE")
	public BigDecimal getDecimalValue() {
		return decimalValue;
	}

	@Override
	public void setDecimalValue(final BigDecimal decimalValue) {
		this.decimalValue = decimalValue;
	}

	@Override
	@Basic
	@Column(name = "BOOLEAN_VALUE")
	public Boolean getBooleanValue() {
		return booleanValue;
	}

	@Override
	public void setBooleanValue(final Boolean booleanValue) {
		this.booleanValue = booleanValue;
	}

	@Override
	@Basic
	@Column(name = "DATE_VALUE")
	public Date getDateValue() {
		return dateValue;
	}

	@Override
	public void setDateValue(final Date dateValue) {
		this.dateValue = dateValue;
	}

	@Override
	@ManyToOne(targetEntity = AttributeImpl.class, cascade = { CascadeType.REFRESH, CascadeType.MERGE })
	@JoinColumn(name = "ATTRIBUTE_UID")
	@ForeignKey
	public Attribute getAttribute() {
		return attribute;
	}

	@Override
	public void setAttribute(final Attribute attribute) {
		this.attribute = attribute;
	}

	@Override
	@Transient
	public String getStringValue() {
		switch (getAttributeType().getTypeId()) {
		case AttributeType.SHORT_TEXT_TYPE_ID:
			if (getAttribute().isMultiValueEnabled()) {
				return StringUtils.join(getShortTextMultiValues(), ImportConstants.SHORT_TEXT_MULTI_VALUE_SEPERATOR);
			}
			return getShortTextValue();
		case AttributeType.LONG_TEXT_TYPE_ID:
			return getLongTextValue();
		case AttributeType.INTEGER_TYPE_ID:
			return ObjectUtils.toString(getIntegerValue(), null);
		case AttributeType.DECIMAL_TYPE_ID:
			return ObjectUtils.toString(getDecimalValue(), GlobalConstants.NULL_VALUE);
		case AttributeType.BOOLEAN_TYPE_ID:
			return ObjectUtils.toString(getBooleanValue(), null);
		case AttributeType.IMAGE_TYPE_ID:
			return getShortTextValue();
		case AttributeType.FILE_TYPE_ID:
			return getShortTextValue();
		case AttributeType.DATE_TYPE_ID:
			return ConverterUtils.date2String(getDateValue(), getUtilityBean().getDefaultLocalizedDateFormat());
		case AttributeType.DATETIME_TYPE_ID:
			return ConverterUtils.date2String(getDateValue(), getUtilityBean().getDefaultLocalizedDateFormat());
		default:
			throw new UnsupportedOperationException();
		}
	}

	@Override
	public Object getValue() {
		switch (getAttributeType().getTypeId()) {
		case AttributeType.SHORT_TEXT_TYPE_ID:
			return getStringValue();
		case AttributeType.LONG_TEXT_TYPE_ID:
			return getLongTextValue();
		case AttributeType.INTEGER_TYPE_ID:
			return getIntegerValue();
		case AttributeType.DECIMAL_TYPE_ID:
			return getDecimalValue();
		case AttributeType.BOOLEAN_TYPE_ID:
			return getBooleanValue();
		case AttributeType.IMAGE_TYPE_ID:
			return getShortTextValue();
		case AttributeType.FILE_TYPE_ID:
			return getShortTextValue();
		case AttributeType.DATE_TYPE_ID:
			return getDateValue();
		case AttributeType.DATETIME_TYPE_ID:
			return getDateValue();
		default:
			throw new UnsupportedOperationException();
		}
	}

	@Override
	public void setValue(final Object value) {
		switch (getAttributeType().getTypeId()) {
		case AttributeType.SHORT_TEXT_TYPE_ID:
			setStringValue((String) value);
			break;
		case AttributeType.LONG_TEXT_TYPE_ID:
			setLongTextValue((String) value);
			break;
		case AttributeType.INTEGER_TYPE_ID:
			setIntegerValue((Integer) value);
			break;
		case AttributeType.DECIMAL_TYPE_ID:
			setDecimalValue((BigDecimal) value);
			break;
		case AttributeType.BOOLEAN_TYPE_ID:
			setBooleanValue((Boolean) value);
			break;
		case AttributeType.IMAGE_TYPE_ID:
			setStringValue((String) value);
			break;
		case AttributeType.FILE_TYPE_ID:
			setStringValue((String) value);
			break;
		case AttributeType.DATE_TYPE_ID:
			setDateValue((Date) value);
			break;
		case AttributeType.DATETIME_TYPE_ID:
			setDateValue((Date) value);
			break;
		default:
			throw new UnsupportedOperationException();
		}
	}

	@SuppressWarnings("fallthrough")
	@Override
	public void setStringValue(final String stringValue) throws EpBindException {
		switch (getAttributeType().getTypeId()) {
		case AttributeType.SHORT_TEXT_TYPE_ID:
			if (getAttribute().isMultiValueEnabled()) {
				setShortTextMultiValues(stringValue);
			} else {
				setShortTextValue(stringValue);
			}
			break;
		case AttributeType.LONG_TEXT_TYPE_ID:
			setLongTextValue(stringValue);
			break;
		case AttributeType.INTEGER_TYPE_ID:
			setIntegerValue(ConverterUtils.string2Int(stringValue));
			break;
		case AttributeType.DECIMAL_TYPE_ID:
			setDecimalValue(ConverterUtils.string2BigDecimal(stringValue));
			break;
		case AttributeType.BOOLEAN_TYPE_ID:
			setBooleanValue(ConverterUtils.string2Boolean(stringValue));
			break;
		case AttributeType.IMAGE_TYPE_ID:
			setShortTextValue(stringValue);
			break;
		case AttributeType.FILE_TYPE_ID:
			setShortTextValue(stringValue);
			break;
		case AttributeType.DATE_TYPE_ID:
		case AttributeType.DATETIME_TYPE_ID:
			setDateValue(ConverterUtils.string2Date(stringValue, getUtilityBean().getDefaultLocalizedDateFormat()));
			break;
		default:
			throw new UnsupportedOperationException();
		}
	}

	/**
	 * Sets multi-valued short text attributes.<br>
	 * This implementation splits the given String on {@link ImportConstants#SHORT_TEXT_MULTI_VALUE_SEPERATOR}.
	 *
	 * @param delimitedShortTextValues the delimited string of short text values
	 */
	protected void setShortTextMultiValues(final String delimitedShortTextValues) {
		if (delimitedShortTextValues == null) {
			setShortTextMultiValues(Collections.<String>emptyList());
			return;
		}
		String[] values = StringUtils.split(delimitedShortTextValues, ImportConstants.SHORT_TEXT_MULTI_VALUE_SEPERATOR);
		setShortTextMultiValues(Arrays.asList(values));
	}

	@Override
	@Transient
	public AttributeType getAttributeType() {
		try {
			return AttributeType.valueOf(getAttributeTypeId());
		} catch (IllegalArgumentException iae) {
			return null;
		}
	}

	@Override
	public void setAttributeType(final AttributeType attributeType) {
		if (attributeType == null) {
			setAttributeTypeId(0);
		} else {
			setAttributeTypeId(attributeType.getTypeId());
		}
	}

	@Override
	public void setLocalizedAttributeKey(final String localizedAttributeKey) {
		this.localizedAttributeKey = localizedAttributeKey;
	}

	@Override
	@Basic
	@Column(name = "LOCALIZED_ATTRIBUTE_KEY")
	public String getLocalizedAttributeKey() {
		return localizedAttributeKey;
	}

	@Override
	@Transient
	public List<String> getShortTextMultiValues() {
		if ((getAttributeType() == AttributeType.SHORT_TEXT) && (getAttribute().isMultiValueEnabled())) {
			return parseShortTextMultiValues(getLongTextValue());
		}
		return Collections.emptyList();
	}

	/**
	 * Parse the multi values for short text type from a delimited string.<br>
	 * This implementation assumes the given string is delimited with {@value ImportConstants#SHORT_TEXT_MULTI_VALUE_SEPERATOR}.
	 *
	 * @param shortTextValue the string value which contains the multi-value for short text.
	 * @return the list of shortText value
	 */
	public static List<String> parseShortTextMultiValues(final String shortTextValue) {
		if (StringUtils.isEmpty(shortTextValue)) {
			return Collections.emptyList();
		}
		List<String> shortTextMultiValues = new ArrayList<String>();
		StringTokenizer stValues = new StringTokenizer(shortTextValue, ImportConstants.SHORT_TEXT_MULTI_VALUE_SEPERATOR);
		if (stValues.hasMoreTokens()) {
			while (stValues.hasMoreTokens()) {
				String singleValue = stValues.nextToken().trim();
				if (singleValue.length() > 0) {
					shortTextMultiValues.add(singleValue);
				}
			}
		}
		return shortTextMultiValues;
	}

	@Override
	public void setShortTextMultiValues(final List<String> shortTextMultiValues) {
		if ((getAttributeType() == AttributeType.SHORT_TEXT) && (getAttribute().isMultiValueEnabled())) {
			setLongTextValue(buildShortTextMultiValues(shortTextMultiValues));
		}
	}

	/**
	 * Build up the multi values for short text. <br>
	 * Using SHORT_TEXT_MULTI_VALUE_SEPERATOR to contact each single value and save it in the longtextvalue.
	 *
	 * @param shortTextMultiValues the list of multi-value for short text.
	 * @return the storage format of the multi-value for short text.
	 */
	public static String buildShortTextMultiValues(final List<String> shortTextMultiValues) {
		if (shortTextMultiValues == null) {
			return null;
		}

		StringBuilder builder = new StringBuilder();
		for (String string : shortTextMultiValues) {
			if (string != null) {
				String singleValue = string.trim();
				if (singleValue.length() > 0) {
					builder.append(singleValue);
					builder.append(ImportConstants.SHORT_TEXT_MULTI_VALUE_SEPERATOR);
				}
			}
		}
		if (builder.length() == 0) {
			return null;
		}

		builder.deleteCharAt(builder.length() - 1);
		return builder.toString();
	}

	/**
	 * Get the attribute type Id.
	 *
	 * @return the Id of the attribute type
	 */
	@Basic
	@Column(name = "ATTRIBUTE_TYPE")
	protected int getAttributeTypeId() {
		return attributeTypeId;
	}

	/**
	 * Set the attribute type id.
	 *
	 * @param attributeTypeId the id of the attribute type
	 */
	protected void setAttributeTypeId(final int attributeTypeId) {
		this.attributeTypeId = attributeTypeId;
	}

	@Override
	@SuppressWarnings({"PMD.MissingBreakInSwitch", "fallthrough"})
	public int compareTo(final AttributeValueWithType other) {
		if (!getAttributeType().equals(other.getAttributeType())) {
			throw new EpDomainException("Must have the same attribute type!");
		}
		switch (getAttributeType().getTypeId()) {
		case AttributeType.LONG_TEXT_TYPE_ID:
		case AttributeType.SHORT_TEXT_TYPE_ID:
			return getStringValue().compareTo(other.getStringValue());
		case AttributeType.DECIMAL_TYPE_ID:
			return getDecimalValue().compareTo(other.getDecimalValue());
		case AttributeType.INTEGER_TYPE_ID:
			return getIntegerValue().compareTo(other.getIntegerValue());
		case AttributeType.DATE_TYPE_ID:
		case AttributeType.DATETIME_TYPE_ID:
			return getDateValue().compareTo(other.getDateValue());
		default:
			throw new EpDomainException("Not implemented");
		}
	}

	@Override
	public boolean isDefined() {
		if (getValue() == null) {
			return false;
		}
		if ("".equals(getStringValue().trim())) {
			return false;
		}
		return true;
	}

	/**
	 * Implements equals semantics.<br>
	 * This class more than likely would be extended to add functionality that would not effect the equals method in comparisons, and as such would
	 * act as an entity type. In this case, content is not crucial in the equals comparison. Using instanceof within the equals method enables
	 * comparison in the extended classes where the equals method can be reused without violating symmetry conditions. If getClass() was used in the
	 * comparison this could potentially cause equality failure when we do not expect it. If when extending additional fields are included in the
	 * equals method, then the equals needs to be overridden to maintain symmetry.
	 *
	 * @param obj the other object to compare
	 * @return true if equal
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof AbstractAttributeValueImpl)) {
			return false;
		}
		AbstractAttributeValueImpl other = (AbstractAttributeValueImpl) obj;
		EqualsBuilder builder = new EqualsBuilder();
		return builder.append(integerValue, other.integerValue)
				.append(decimalValue, other.decimalValue)
				.append(booleanValue, other.booleanValue)
				.append(dateValue, other.dateValue)
				.append(attribute, other.attribute)
				.append(shortTextValue, other.shortTextValue)
				.append(longTextValue, other.longTextValue)
				.append(localizedAttributeKey, other.localizedAttributeKey)
				.append(attributeTypeId, other.attributeTypeId)
				.isEquals();
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();
		return builder.append(integerValue)
				.append(decimalValue)
				.append(booleanValue)
				.append(dateValue)
				.append(attribute)
				.append(shortTextValue)
				.append(longTextValue)
				.append(localizedAttributeKey)
				.append(attributeTypeId)
				.toHashCode();
	}

	@Override
	public String toString() {
		if (getAttributeType() != null) {
			return getStringValue();
		}
		return null;
	}

	@Transient
	private Utility getUtilityBean() {
		return getBean(ContextIdNames.UTILITY);
	}
}
