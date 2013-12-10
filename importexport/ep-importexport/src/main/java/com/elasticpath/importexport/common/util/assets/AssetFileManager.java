package com.elasticpath.importexport.common.util.assets;


/**
 * Interface that provides methods for working with assets.
 */
public final class AssetFileManager {

	/** Relative path to subfolder containing digitalgood assets in VFS. */
	public static final String PROPERTY_DIGITALGOODS_ASSET_SUBFOLDER = "asset.subfolder.digitalgoods";

	/** Relative path to subfolder containing images assets in VFS. */
	public static final String PROPERTY_IMAGE_ASSET_SUBFOLDER = "asset.subfolder.image";

	/** Path to root folder containing assets relative to VFS root path. */
	public static final String PROPERTY_ASSET_VFS_ROOTPATH = "asset.vfs.rootpath";

	/** Password for VFS. */
	public static final String PROPERTY_ASSET_VFS_PASSWORD = "asset.vfs.password";

	/** Login for VFS. */
	public static final String PROPERTY_ASSET_VFS_USERNAME = "asset.vfs.username";

	/** Port for VFS server. */
	public static final String PROPERTY_ASSET_VFS_PORT = "asset.vfs.port";

	/** VFS host IP address. */
	public static final String PROPERTY_ASSET_VFS_HOST = "asset.vfs.host";

	/** Specified VFS protocol. */
	public static final String PROPERTY_ASSET_VFS_PROTOCOL = "asset.vfs.protocol";

}