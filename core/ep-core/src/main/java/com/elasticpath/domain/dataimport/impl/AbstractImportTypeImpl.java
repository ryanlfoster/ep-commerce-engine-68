/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.dataimport.impl;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import com.elasticpath.domain.dataimport.ImportType;
import com.elasticpath.domain.impl.AbstractEpDomainImpl;

/**
 * <code>AbstractImportTypeImpl</code> represents an import type. It also contains enums of various import types.
 */
public abstract class AbstractImportTypeImpl extends AbstractEpDomainImpl implements Serializable, ImportType {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	private static final String INSERT_UPDATE_KEY = "ImportType_InsertAndUpdate";

	private static final String UPDATE_KEY = "ImportType_Update";

	private static final String INSERT_KEY = "ImportType_Insert";

	private static final String DELETE_KEY = "ImportType_Delete";


	/**
	 * Import type id of the insert and update import.
	 */
	protected static final int INSERT_UPDATE_ID = 1;

	/**
	 * Import type id of the update import.
	 */
	protected static final int UPDATE_ID = 2;

	/**
	 * Import type id of the insert import.
	 */
	protected static final int INSERT_ID = 3;

	/**
	 * Import type id of the insert import.
	 */
	protected static final int DELETE_ID = 4;

	private final int typeId;

	private final String nameMessageKey;

	/**
	 * The default constructor to create a import type.
	 *
	 * @param nameMessageKey the import type name key
	 * @param typeId the import type id
	 */
	protected AbstractImportTypeImpl(final String nameMessageKey, final int typeId) {
		this.nameMessageKey = nameMessageKey;
		this.typeId = typeId;
	}

	/**
	 * The insert and update import type.
	 */
	public static final ImportType INSERT_UPDATE_TYPE = new AbstractImportTypeImpl(INSERT_UPDATE_KEY, INSERT_UPDATE_ID) {
		/** Serial version id. */
		private static final long serialVersionUID = 5000000001L;
	};

	/**
	 * The update import type.
	 */
	public static final ImportType UPDATE_TYPE = new AbstractImportTypeImpl(UPDATE_KEY, UPDATE_ID) {
		/** Serial version id. */
		private static final long serialVersionUID = 5000000001L;
	};

	/**
	 * The insert import type.
	 */
	public static final ImportType INSERT_TYPE = new AbstractImportTypeImpl(INSERT_KEY, INSERT_ID) {
		/** Serial version id. */
		private static final long serialVersionUID = 5000000001L;
	};

	/**
	 * The delete import type.
	 */
	public static final ImportType DELETE_TYPE = new AbstractImportTypeImpl(DELETE_KEY, DELETE_ID) {
		/** Serial version id. */
		private static final long serialVersionUID = 5000000001L;
	};

	/**
	 * Return the import type Id.
	 *
	 * @return the import type Id
	 */
	public int getTypeId() {
		return typeId;
	}

	/**
	 * Return <code>true</code> if the given object is an import type and has the same id.
	 *
	 * @param object the object
	 * @return <code>true</code> if the given object is an import type and has the same id
	 */
	@Override
	public boolean equals(final Object object) {
		if (object instanceof AbstractImportTypeImpl) {
			final AbstractImportTypeImpl importType = (AbstractImportTypeImpl) object;
			return typeId == importType.getTypeId();
		}

		return false;
	}

	/**
	 * Return the hash code.
	 *
	 * @return the hash code
	 */
	@Override
	public int hashCode() {
		return typeId;
	}

	private static final ImportType[] IMPORT_TYPES = { null, INSERT_UPDATE_TYPE, UPDATE_TYPE, INSERT_TYPE, DELETE_TYPE };

	private static final List<ImportType> VALID_IMPORT_TYPES = Arrays.asList(IMPORT_TYPES).subList(1, IMPORT_TYPES.length);

	private Object readResolve() throws ObjectStreamException {
		return IMPORT_TYPES[typeId];
	}

	/**
	 * Get the import type with the given import type id.
	 *
	 * @param importTypeId the import type id
	 * @return the import type instance
	 */
	public static ImportType getInstance(final int importTypeId) {
		return IMPORT_TYPES[importTypeId];
	}

	/**
	 * Get the import type with the name message key.
	 * @param importTypeKey the name message key
	 * @return the import type instance
	 */
	public static ImportType getInstance(final String importTypeKey) {
		for (ImportType importType : IMPORT_TYPES) {
			if (importType != null && importType.getNameMessageKey().equals(importTypeKey)) {
				return importType;
			}
		}
		return null;
	}

	/**
	 * Return a list of all import types.
	 *
	 * @return a list of all import types
	 */
	public static List<ImportType> getAllImportTypes() {
		return VALID_IMPORT_TYPES;
	}

	/**
	 * Returns the attribute type name message key.
	 *
	 * @return the attribute type name message key.
	 */
	public String getNameMessageKey() {
		return nameMessageKey;
	}

	@Override
	public String toString() {
		return nameMessageKey;
	}
}