package com.elasticpath.sellingchannel.director;


import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.domain.shoppingcart.ShoppingItem;

/**
 * Maps data between a {@code ShoppingItemDto} and a {@code ShoppingItem} domain object.
 */
public interface ShoppingItemAssembler {

	/**
	 * Creates a {@code ShoppingItem} from a {@code ShoppingItemDto} for both normal and bundled products.
	 *
	 * @param shoppingItemDto The dto to use.
	 * @return The ShoppingItem.
	 */
	ShoppingItem createShoppingItem(ShoppingItemDto shoppingItemDto);

    /**
	 * Creates a {@code ShoppingItemDto} from a {@code ShoppingItem} for both normal and bundled products.
	 *
	 * @param shoppingItem The item to use.
	 *
	 * @return The ShoppingItemDto.
	 */
	ShoppingItemDto assembleShoppingItemDtoFrom(ShoppingItem shoppingItem);

	/**
	 *	Determines if the user selection are valid, using the product from the database.
	 * @param shoppingItemDto - item received from the user
	 */
	void validateShoppingItemDto(final ShoppingItemDto shoppingItemDto);
}
