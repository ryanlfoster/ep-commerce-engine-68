package com.elasticpath.commons.util.extenum.impl;

import java.util.Collection;

import com.elasticpath.commons.util.extenum.ExtensibleEnum;

/**
 * Base implementation to extend when creating extensible enums.
 *
 * @param <T> the enum type interface
 * @deprecated use {@link com.elasticpath.commons.util.extenum.AbstractExtensibleEnum}
 */
@Deprecated
public abstract class AbstractExtensibleEnum<T extends ExtensibleEnum> extends com.elasticpath.commons.util.extenum.AbstractExtensibleEnum<T> {
	private static final long serialVersionUID = -579614550008689580L;

	/**
	 * Create an enum value for a enum type. Name is case-insensitive and will be stored in upper-case.
	 *
	 * @param ordinal the ordinal value
	 * @param name the name value (this will be converted to upper-case).
	 * @param klass the enum type interface
	 */
	protected AbstractExtensibleEnum(final int ordinal, final String name, final Class<T> klass) {
		super(ordinal, name, klass);
	}

	/**
	 * Find the enum value with the specified ordinal value, for a particular enum type.
	 *
	 * @param <T> the enum type
	 * @param ordinal the ordinal value
	 * @param klass the enum type interface
	 * @return the enum value
	 */
	protected static <T extends ExtensibleEnum> T valueOf(final int ordinal, final Class<T> klass) {
		return com.elasticpath.commons.util.extenum.AbstractExtensibleEnum.valueOf(ordinal, klass);
	}

	/**
	 * Find the enum value with the specified name, for a particular enum type.
	 *
	 * @param <T> the enum type
	 * @param name the name
	 * @param klass the enum type interface
	 * @return the enum value
	 */
	protected static <T extends ExtensibleEnum> T valueOf(final String name, final Class<T> klass) {
		return com.elasticpath.commons.util.extenum.AbstractExtensibleEnum.valueOf(name, klass);
	}

	/**
	 * Find all enum values for a particular enum type.
	 *
	 * @param <T> the enum type
	 * @param klass the enum type interface
	 * @return the enum values
	 */
	protected static <T extends ExtensibleEnum> Collection<T> values(final Class<T> klass) {
		return com.elasticpath.commons.util.extenum.AbstractExtensibleEnum.values(klass);
	}
}
