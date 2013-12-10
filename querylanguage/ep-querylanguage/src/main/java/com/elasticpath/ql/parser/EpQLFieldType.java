package com.elasticpath.ql.parser;

/**
 * Represents a type of Ep QL field. The type specification is required since it will affect how the field will be resolved to a native field.
 */
public enum EpQLFieldType {
	
	/** Date data type. */
	DATE,
	/** String data type. */
	STRING,
	/** Float data type. */
	FLOAT,
	/** Boolean data type. */
	BOOLEAN,
	/** Enum data type. */
	ENUM;
	// TODO: add other data types if required to be handled in unique way.
}
