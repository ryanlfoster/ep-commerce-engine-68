package com.elasticpath.commons.exception;

import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.domain.catalog.ProductBundle;

/**
 * Can be thrown if user selection doesn't match with the bundle selection rule.
 */
public class InvalidBundleSelectionException extends RuntimeException {
	private static final long serialVersionUID = 4887982045027036418L;
	private final ProductBundle bundle;
	private final ShoppingItemDto dto;
	
	/**
	 * Constructs the exception with given arguments.
	 * 
	 * @param bundle the bundle that is being validated against
	 * @param dto the dtop that represents the user selection
	 */
	public InvalidBundleSelectionException(final ProductBundle bundle, final ShoppingItemDto dto) {
		super("Bundle selection is not valid!");
		this.bundle = bundle;
		this.dto = dto;
	}

	/**
	 * @return the bundle in validation
	 */
	public ProductBundle getBundle() {
		return bundle;
	}

	/**
	 * @return the dto in validation
	 */
	public ShoppingItemDto getDto() {
		return dto;
	}
}
