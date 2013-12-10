package com.elasticpath.importexport.common.dto.catalogs;

/**
 * Catalog type enum.
 */
public enum CatalogType {

	/**
	 * Master Catalog type.
	 */
	master,

	/**
	 * Virtual Catalog type.
	 */
	virtual;
	
	private CatalogType() {
		// private constructor
	}

	/**
	 * Gets Catalog type by boolean value.
	 * 
	 * @param isMaster indicate if it is master catalog
	 * @return appropriate CatalogType
	 */
	public static CatalogType getCatalogType(final boolean isMaster) {
		if (isMaster) {
			return master;
		}
		return virtual;
	}

	/**
	 * Gets true if type is master and false otherwise.
	 * 
	 * @param type the catalog type
	 * @return true if type is master false otherwise
	 */
	public static boolean isMaster(final CatalogType type) {
		return type.equals(master);
	}

}
