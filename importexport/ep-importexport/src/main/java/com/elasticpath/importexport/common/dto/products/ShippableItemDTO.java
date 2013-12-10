package com.elasticpath.importexport.common.dto.products;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import com.elasticpath.common.dto.Dto;

/**
 * The implementation of the <code>Dto</code> interface that contains data of shippable properties.
 * <p>
 * This implementation designed for JAXB to working with xml representation of data
 */
@XmlAccessorType(XmlAccessType.NONE)
public class ShippableItemDTO implements Dto {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@XmlElement(name = "weight")
	private UnitDTO weight;

	@XmlElement(name = "width")
	private UnitDTO width;

	@XmlElement(name = "length")
	private UnitDTO length;

	@XmlElement(name = "height")
	private UnitDTO height;

	@XmlAttribute(name = "enabled", required = true)
	private boolean enabled;

	/**
	 * Gets the weight.
	 * 
	 * @return the weight
	 */
	public UnitDTO getWeight() {
		return weight;
	}

	/**
	 * Sets the weight.
	 * 
	 * @param weight the weight to set
	 */
	public void setWeight(final UnitDTO weight) {
		this.weight = weight;
	}

	/**
	 * Gets the width.
	 * 
	 * @return the width
	 */
	public UnitDTO getWidth() {
		return width;
	}

	/**
	 * Sets the width.
	 * 
	 * @param width the width to set
	 */
	public void setWidth(final UnitDTO width) {
		this.width = width;
	}

	/**
	 * Gets the length.
	 * 
	 * @return the length
	 */
	public UnitDTO getLength() {
		return length;
	}

	/**
	 * Sets the length.
	 * 
	 * @param length the length to set
	 */
	public void setLength(final UnitDTO length) {
		this.length = length;
	}

	/**
	 * Gets the height.
	 * 
	 * @return the height
	 */
	public UnitDTO getHeight() {
		return height;
	}

	/**
	 * Sets the height.
	 * 
	 * @param height the height to set
	 */
	public void setHeight(final UnitDTO height) {
		this.height = height;
	}

	/**
	 * Gets the enabled.
	 * 
	 * @return the enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * Sets the enabled.
	 * 
	 * @param enabled the enabled to set
	 */
	public void setEnabled(final boolean enabled) {
		this.enabled = enabled;
	}

}
