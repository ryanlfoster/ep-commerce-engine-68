package com.elasticpath.sfweb.controller.impl;

import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.sfweb.controller.ShoppingItemDtoMapper;
import com.elasticpath.sfweb.formbean.ShoppingItemFormBean;

/**
 * Maps a ShoppingItemFormBean to a ShoppingItemDto.
 */
public class ShoppingItemDtoMapperImpl implements ShoppingItemDtoMapper {

	@Override
	public ShoppingItemDto mapFrom(final ShoppingItemFormBean bean) {
		// Note that at present the first level object does not pass the code and quantity in the bean.
		ShoppingItemDto root = new ShoppingItemDto(bean.getSkuCode(), bean.getQuantity());
		root.setSelected(bean.isSelected());
		doConstituentsMapping(root, bean);
		doItemFieldsMapping(root, bean);
		return root;
	}
	
	/**
	 * Add the special fields from form to item fields.
	 * 
	 * @param parentDto {@link ShoppingItemDto}.
	 * @param parentBean {@link ShoppingItemFormBean}.
	 */
	protected void doItemFieldsMapping(final ShoppingItemDto parentDto, final ShoppingItemFormBean parentBean) {
		parentDto.setItemFields(parentBean.getGiftCertificateFields().getAsItemFields());
	}

	/**
	 * Recursively create shopping item dto from shopping item form bean.
	 * 
	 * @param parentDto {@link ShoppingItemDto}.
	 * @param parentBean {@link ShoppingItemFormBean}.
	 */
	protected void doConstituentsMapping(final ShoppingItemDto parentDto, final ShoppingItemFormBean parentBean) {
		for (ShoppingItemFormBean childBean : parentBean.getConstituents()) {
			ShoppingItemDto child = new ShoppingItemDto(childBean.getSkuCode(), parentBean.getQuantity());
			parentDto.addConstituent(child);
			child.setSelected(childBean.isSelected());
			doConstituentsMapping(child, childBean);
		}
	}
}
