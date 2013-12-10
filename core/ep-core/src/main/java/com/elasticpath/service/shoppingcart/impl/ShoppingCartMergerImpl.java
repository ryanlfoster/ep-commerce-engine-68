package com.elasticpath.service.shoppingcart.impl;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.domain.catalog.GiftCertificate;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.ShoppingItemPredicateUtils;
import com.elasticpath.sellingchannel.director.CartDirector;
import com.elasticpath.sellingchannel.director.ShoppingItemAssembler;
import com.elasticpath.service.shoppingcart.ShoppingCartMerger;

/**
 * Utility class for merging two shopping lists into a single list.
 */
public class ShoppingCartMergerImpl implements ShoppingCartMerger {

	private ShoppingItemAssembler shoppingItemAssembler;
	private CartDirector cartDirector;

    @Override
	public final ShoppingCart merge(final ShoppingCart recipient, final ShoppingCart donor) {

        final List<ShoppingItem> recipientItems = recipient.getCartItems();
        final List<ShoppingItem> donorItems = donor.getCartItems();

        final boolean recipientHadItems = (recipient.getNumItems() > 0);
        for (Iterator<ShoppingItem> donorItemIterator = donorItems.iterator(); donorItemIterator.hasNext();) {
            ShoppingItem donorItem = donorItemIterator.next();

            Predicate matchingShoppingItemPredicate = ShoppingItemPredicateUtils.matchingShoppingItemPredicate(donorItem);
            ShoppingItem matchingRecipient = (ShoppingItem) CollectionUtils.find(recipientItems, matchingShoppingItemPredicate);

            if (matchingRecipient == null) {
            	if (recipientHadItems) {
            		recipient.setMergedNotification(true);
            	}
            	addDonorItemToCart(recipient, donorItem);
                donorItemIterator.remove();
            } else {
            	if (donorItem.getQuantity() != matchingRecipient.getQuantity()) {
                	ShoppingItemDto dto = shoppingItemAssembler.assembleShoppingItemDtoFrom(donorItem);
            		cartDirector.updateCartItem(recipient, matchingRecipient.getUidPk(), dto);
            		recipient.setMergedNotification(true);
            	}
            }
        }
        
        mergeTransientData(recipient, donor);
        cartDirector.removeAnyNonPurchasableItems(recipient.getCartItems(), recipient);
        return recipient;
    }

    /**
     * The donor item will be deleted by JPA, so we make a clone from it, and add it to the recipient cart.
     *
     * @param shoppingCart the cart to update
     * @param donorItem the item to add to the cart
     */
	private void addDonorItemToCart(final ShoppingCart shoppingCart, final ShoppingItem donorItem) {
		ShoppingItemDto dto = shoppingItemAssembler.assembleShoppingItemDtoFrom(donorItem);
		ShoppingItem addedItem = cartDirector.addItemToCart(shoppingCart, dto);
		
		if (addedItem == null) {
			shoppingCart.getNotPurchasableCartItemSkus().add(dto.getSkuCode());
		}
	}

	/**
	 * Merge transient data such as promo codes and gift certificates.
	 *
	 * @param recipient the recipient
	 * @param donor the donor
	 */
	protected void mergeTransientData(final ShoppingCart recipient, final ShoppingCart donor) {
		for (String promoCode : donor.getPromotionCodes()) {
        	recipient.applyPromotionCode(promoCode);
        }
        
        for (GiftCertificate giftCertificate : donor.getAppliedGiftCertificates()) {
        	recipient.applyGiftCertificate(giftCertificate);
        }

		recipient.setCmUserUID(donor.getCmUserUID());
	}

    /**
	 * @param shoppingItemAssembler The assembler to set.
	 */
	public void setShoppingItemAssembler(final ShoppingItemAssembler shoppingItemAssembler) {
		this.shoppingItemAssembler = shoppingItemAssembler;
	}

	/**
	 * @param cartDirector the cartDirector to set
	 */
	public void setCartDirector(final CartDirector cartDirector) {
		this.cartDirector = cartDirector;
	}

}
