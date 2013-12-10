package com.elasticpath.sfweb.controller;

import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.sfweb.formbean.ShoppingItemFormBean;

/**
 * Maps a ShoppingItemFormBean to a ShoppingItemDto. 
 */
public interface ShoppingItemDtoMapper {

	/**
	 * Parses ShoppingItemFormBean into ShoppingItemDto. Expects the form bean to be the high level form bean
	 * which represents a bundle and not any of its constituents.
	 * The DTO returned from this method, like the FormBean passed in, will not be a complete representation
	 * of the Product from which the DTO would be created by the 
	 * {@link com.elasticpath.sellingchannel.director.ShoppingItemAssembler}
	 * 
	 * @param bean The bean to map from.
	 * @return ShoppingItemDto The resulting data transfer object.
	 */
	ShoppingItemDto mapFrom(final ShoppingItemFormBean bean);

}