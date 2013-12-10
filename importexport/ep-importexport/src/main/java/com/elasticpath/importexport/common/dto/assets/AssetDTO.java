package com.elasticpath.importexport.common.dto.assets;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.elasticpath.common.dto.Dto;

/**
 * DTO object containing the list of asset file names produced during the export.
 * Only assets from this list will be imported.
 */
@XmlRootElement(name = AssetDTO.ROOT_ELEMENT)
@XmlAccessorType(XmlAccessType.NONE)
public class AssetDTO implements Dto {

	private static final long serialVersionUID = 1L;

	/**
	 * The name of root element in XML representation.
	 */
	public static final String ROOT_ELEMENT = "asset";

	@XmlAttribute(name = "filename", required = true)
	private String asset;

	/**
	 * Constructs empty object.
	 */
	public AssetDTO() {
		// empty constructor for jaxb
	}

	/**
	 * Constructs asset dto with given arguments.
	 *
	 * @param filename the file name
	 */
	public AssetDTO(final String filename) {
		asset = filename;
	}

	/**
	 * Gets asset file name.
	 *
	 * @return asset file name
	 */
	public String getAsset() {
		return asset;
	}

	/**
	 * Sets asset file name.
	 *
	 * @param asset file name
	 */
	public void setAsset(final String asset) {
		this.asset = asset;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
			.append("asset", getAsset())
			.toString();
	}
}
