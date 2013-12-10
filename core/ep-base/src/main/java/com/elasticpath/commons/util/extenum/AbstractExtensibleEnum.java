package com.elasticpath.commons.util.extenum;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Base implementation to extend when creating extensible enums.
 *
 * @param <T> the enum type interface
 */
public abstract class AbstractExtensibleEnum<T extends ExtensibleEnum> implements ExtensibleEnum, Serializable {

	private static final Map<Class<?>, Map<Integer, ExtensibleEnum>> ENUMS_BY_ORDINAL_MAP_BY_TYPE_MAP =
			new ConcurrentHashMap<Class<?>, Map<Integer, ExtensibleEnum>>();

	private static final Map<Class<?>, Map<String, ExtensibleEnum>> ENUMS_BY_NAME_MAP_BY_TYPE_MAP =
			new ConcurrentHashMap<Class<?>, Map<String, ExtensibleEnum>>();

	private static final long serialVersionUID = 235676835545L;

	private final int ordinal;

	private final String name;

	/**
	 * Create an enum value for a enum type. Name is case-insensitive and will be stored in upper-case.
	 *
	 * @param ordinal the ordinal value
	 * @param name the name value (this will be converted to upper-case).
	 * @param klass the enum type interface
	 */
	protected AbstractExtensibleEnum(final int ordinal, final String name, final Class<T> klass) {
		this.name = name.toUpperCase(Locale.getDefault());
		this.ordinal = ordinal;

		final Map<Integer, ExtensibleEnum> enumsByOrdinalMap = getOrCreateEnumsByOrdinalMap(klass);
		if (enumsByOrdinalMap.containsKey(ordinal)) {
			throw new IllegalArgumentException(klass + " enum with ordinal " + ordinal + " already exists.");
		}
		enumsByOrdinalMap.put(ordinal, this);

		final Map<String, ExtensibleEnum> enumsByNameMap = getOrCreateEnumsByNameMap(klass);
		if (enumsByNameMap.containsKey(name)) {
			throw new IllegalArgumentException(klass + " enum with name '" + name + "' already exists (case insensitive comparison).");
		}
		enumsByNameMap.put(this.name, this);
	}

	private static Map<Integer, ExtensibleEnum> getOrCreateEnumsByOrdinalMap(final Class<?> klass) {
		Map<Integer, ExtensibleEnum> enumsByOrdinalMap = ENUMS_BY_ORDINAL_MAP_BY_TYPE_MAP.get(klass);
		if (enumsByOrdinalMap == null) {
			enumsByOrdinalMap = new ConcurrentHashMap<Integer, ExtensibleEnum>();
			ENUMS_BY_ORDINAL_MAP_BY_TYPE_MAP.put(klass, enumsByOrdinalMap);
		}
		return enumsByOrdinalMap;
	}

	private static Map<String, ExtensibleEnum> getOrCreateEnumsByNameMap(final Class<?> klass) {
		Map<String, ExtensibleEnum> enumsByNameMap = ENUMS_BY_NAME_MAP_BY_TYPE_MAP.get(klass);
		if (enumsByNameMap == null) {
			enumsByNameMap = new ConcurrentHashMap<String, ExtensibleEnum>();
			ENUMS_BY_NAME_MAP_BY_TYPE_MAP.put(klass, enumsByNameMap);
		}
		return enumsByNameMap;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getOrdinal() {
		return ordinal;
	}

	/**
	 * Get the enum type interface.
	 *
	 * @return the enum type
	 */
	protected abstract Class<T> getEnumType();

	/**
	 * Returns the canonical instance of the enum value.
	 *
	 * @return the canonical instance
	 * @throws ObjectStreamException never thrown
	 */
	protected Object readResolve() throws ObjectStreamException {
		return valueOf(ordinal, getEnumType());
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
		@SuppressWarnings("unchecked")
		final T value = (T) getOrCreateEnumsByOrdinalMap(klass).get(ordinal);
		if (value == null) {
			throw new IllegalArgumentException("No such enum value: " + klass + "[" + ordinal + "]");
		}
		return value;
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
		@SuppressWarnings("unchecked")
		final T value = (T) getOrCreateEnumsByNameMap(klass).get(name.toUpperCase(Locale.getDefault()));
		if (value == null) {
			throw new IllegalArgumentException("No such enum value: " + klass + "." + name);
		}
		return value;
	}

	/**
	 * Find all enum values for a particular enum type.
	 *
	 * @param <T> the enum type
	 * @param klass the enum type interface
	 * @return the enum values
	 */
	@SuppressWarnings("unchecked")
	protected static <T extends ExtensibleEnum> Collection<T> values(final Class<T> klass) {
		return (Collection<T>) getOrCreateEnumsByOrdinalMap(klass).values();
	}

	/**
	 * Final so that subclasses cannot change enum equality behaviour. Enum equality is instance equality.
	 *
	 * @param obj instance to check equality against
	 * @return true if objects are the same instance.
	 */
	@Override
	public final boolean equals(final Object obj) {
		return obj == this;
	}

	/**
	 * Final so that subclasses cannot change enum hash code.
	 *
	 * @return the hash code
	 */
	@Override
	public final int hashCode() {
		return ordinal;
	}

	@Override
	public String toString() {
		return name;
	}

}
