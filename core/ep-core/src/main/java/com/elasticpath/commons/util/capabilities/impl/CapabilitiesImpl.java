package com.elasticpath.commons.util.capabilities.impl;

import java.util.HashSet;
import java.util.Set;

import com.elasticpath.commons.util.capabilities.Capabilities;
import com.elasticpath.commons.util.extenum.ExtensibleEnum;

/**
 * Capabilities implementation.
 */
public class CapabilitiesImpl implements Capabilities {
	
	/** The supported ExtensibleEnums. */
	private final Set<ExtensibleEnum> supported;
	
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;
	
	/**
	 * Constructor.
	 * 
	 * @param supported The supported ExtensibleEnums. Can be null or empty.
	 */
	public CapabilitiesImpl(final ExtensibleEnum... supported) {
		this.supported = new HashSet<ExtensibleEnum>();
		if (supported != null) {
			for (ExtensibleEnum capability : supported) {
				this.supported.add(capability);
			}
		}
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		CapabilitiesImpl other = (CapabilitiesImpl) obj;
		if (!supported.equals(other.supported)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + supported.hashCode();
		return result;
	}

	@Override
	public boolean supports(final ExtensibleEnum... capabilities) {
		if (capabilities == null || capabilities.length == 0) {
			return true;
		}
		for (ExtensibleEnum capability : capabilities) {
			if (!supported.contains(capability)) {
				return false;
			}
		}
		return true;
	}

}
