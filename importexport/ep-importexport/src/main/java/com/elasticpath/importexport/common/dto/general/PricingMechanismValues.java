/*
 * Copyright (c) Elastic Path Software Inc., 2010
 */
package com.elasticpath.importexport.common.dto.general;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for pricingMechanismValues.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="pricingMechanismValues">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="calculated"/>
 *     &lt;enumeration value="assigned"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "pricingMechanismValues")
@XmlEnum
public enum PricingMechanismValues {

	/** Calculated pricing mechanism (price of the bundle is calculated by summing the prices of the constituents). */
    @XmlEnumValue("calculated")
    CALCULATED("calculated"),

    /** Assigned pricing mechanism. (price of the bundle is given) */
    @XmlEnumValue("assigned")
    ASSIGNED("assigned");
    private final String value;

    /**
     * Constructor of the enum.
     * @param stringValue - the string associated with the constant.
     */
    PricingMechanismValues(final String stringValue) {
        value = stringValue;
    }

    /** Get the string associated with the constant.
     *
     * @return the string associated with the constant.
     */
    public String value() {
        return value;
    }

    /** Create a constant based on a string value.
     *
     * @param stringValue - the string value
     * @return the constant associated with the string value.
     */
    public static PricingMechanismValues fromValue(final String stringValue) {
        for (PricingMechanismValues existingConstants : PricingMechanismValues.values()) {
            if (existingConstants.value.equals(stringValue)) {
                return existingConstants;
            }
        }
        throw new IllegalArgumentException(stringValue);
    }

}
