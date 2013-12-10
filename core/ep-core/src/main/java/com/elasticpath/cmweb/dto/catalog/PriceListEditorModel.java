package com.elasticpath.cmweb.dto.catalog;

import java.util.Collection;
import java.util.List;

import com.elasticpath.common.dto.ChangeSetObjects;
import com.elasticpath.common.dto.pricing.BaseAmountDTO;
import com.elasticpath.common.dto.pricing.PriceListDescriptorDTO;

/**
 * Model holding data for a PriceListEditor.
 */
public interface PriceListEditorModel {
	
	/**
	 * @return list of BaseAmountDTOs in the model.
	 */
	Collection <BaseAmountDTO> getBaseAmounts();
	
	/**
	 * @return list of BaseAmountDTOs in the model. Removed objects are temporary kept in the list.
	 */
	Collection<BaseAmountDTO> getBaseAmountWithRemoved();
	/**
	 * Filters base amounts by given object type.
	 * 
	 * @param businessObjectType business object type
	 * @return list of base amounts with given object type
	 */
	Collection<BaseAmountDTO> filterBaseAmounts(final String businessObjectType);

	/**
     * @return true if this model has associated base amounts
     */
    boolean hasBaseAmounts();

    /**
     * @return the number of base amount DTO objects, associated with this model.
     */
    int getNumberOfBaseAmounts();
	
	/**
	 * Adds the base amount DTO to the model.
	 * 
	 * @param baseAmountDTO the base amount DTO
	 */
	void addBaseAmount(BaseAmountDTO baseAmountDTO);

	/**
	 * Updates an existing base amount in the model.
	 * 
	 * @param oldBaseAmountDTO the old base amount to replace
	 * @param baseAmountDTO the base amount DTO
	 */
	void updateBaseAmount(BaseAmountDTO oldBaseAmountDTO, BaseAmountDTO baseAmountDTO);

	/**
	 * Removes a base amount DTO from the model.
	 * 
	 * @param baseAmountDTO the base amount DTO
	 */
	void removeBaseAmount(BaseAmountDTO baseAmountDTO);

    /**
     * Removes all base amount DTOs from this model.
     */
    void removeAllBaseAmounts();

    /**
     * Gets base amount by qty.
     * 
     * @param qty the qty
     * @return base amount with given qty
     */
	BaseAmountDTO getBaseAmountDTO(final int qty);

	/**
	 * @return the PriceListDesciptorDTO for this model.
	 */
	PriceListDescriptorDTO getPriceListDescriptor();

	/**
	 * Set the price list descriptor DTO for this model.
	 * @param dto the price list descriptor DTO
	 */
	void setPriceListDescriptor(PriceListDescriptorDTO dto);
	
	/**
	 * @return the BaseAmountChangeSet for this model.
	 */
	ChangeSetObjects<BaseAmountDTO> getChangeSet();

	/**
	 * Logical equals comparison of two BaseAmountDTOs.
	 * This method captures the business key for BaseAmounts where the combination of
	 * ObjectGuid, ObjectType, and Quantity is unique.
	 * 
	 * @param dto1 first item to compare
	 * @param dto2 second item to compare
	 * @return true if the two items are logically equal
	 */
	boolean logicalEquals(final BaseAmountDTO dto1, final BaseAmountDTO dto2);
	
	/**
	 * Checks whether a base amount was added in this session.
	 * 
	 * @param baseAmountDto the base amount DTO
	 * @return true if added in this session
	 */
	boolean isNewlyAdded(final BaseAmountDTO baseAmountDto);
	
	/**
	 * Checks if base amount has been deleted.
	 *
	 * @param baseAmountDto the base amount DTO 
	 * @return true - if deleted, false otherwise
	 */
	boolean isDeleted(final BaseAmountDTO baseAmountDto);
	
	/**
	 * Checks if base amount has been deleted.
	 *
	 * @param baseAmountDto the base amount DTO
	 * @return true if edited, false otherwise
	 */
	boolean isEdited(final BaseAmountDTO baseAmountDto);
	
	/**
	 * Set change set.
	 * @param changeSet to set.
	 */
	void setChangeSet(ChangeSetObjects<BaseAmountDTO> changeSet);

	/**
	 * Returns list of BaseAmountDTOs in the model as it was selected from database ignoring changesets. 
	 *
	 * @return list of BaseAmountDTOs in the model as it was selected from database
	 */
	List<BaseAmountDTO> getRawBaseAmounts();

}
