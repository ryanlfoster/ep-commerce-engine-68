package com.elasticpath.epcoretool.logic.dto;

/**
 * DTO used for Set/Unset Settings.
 */
public class EpSetting {
	private final String name;
	private final String context;
	private final String value;

	/**
	 * Instantiates a new ep setting.
	 *
	 * @param name the name
	 * @param context the context
	 * @param value the value
	 */
	public EpSetting(final String name, final String context, final String value) {
		this.name = name;
		this.context = context;
		this.value = value;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the context.
	 *
	 * @return the context
	 */
	public String getContext() {
		return context;
	}

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
}
