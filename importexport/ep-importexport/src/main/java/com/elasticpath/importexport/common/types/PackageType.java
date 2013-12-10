package com.elasticpath.importexport.common.types;

/**
 * Package Type is a common property both for export and import.
 * It defines how to pack/unpack files (streams processed during export/import job execution).
 */
public enum PackageType {

	/**
	 * Packager of this type packs entries into ZIP archive.
	 * Unpackager of this type is able to unpack ZIP archive produces by packager. 
	 */
	ZIP,
	
	/**
	 * Packager of this type passes entries (streams) as it is without any packaging.
	 * So each file represents separate package and isn't processed by unpackager but just passed out. 
	 */
	NONE
}
