package com.elasticpath.service.query;

import java.util.Date;

import com.elasticpath.commons.util.extenum.AbstractExtensibleEnum;
import com.elasticpath.persistence.api.Entity;

/**
 * The Class ResultType.
 */
public class ResultType extends AbstractExtensibleEnum<ResultType> {

	private static final long serialVersionUID = 1L;

	/** The Constant ENTITY_RESULT_ORDINAL. */
	public static final int ENTITY_RESULT_ORDINAL = 1;
	
	/** An Entity result. */
	public static final ResultType ENTITY = new ResultType(ENTITY_RESULT_ORDINAL, "entity", Entity.class);
	
	/** The Constant UID_RESULT_ORDINAL. */
	public static final int UID_RESULT_ORDINAL = 2;
	
	/** A uidPk result. */
	public static final ResultType UID = new ResultType(UID_RESULT_ORDINAL, "uid", Long.class);
	
	/** The Constant GUID_RESULT_ORDINAL. */
	public static final int GUID_RESULT_ORDINAL = 3;
	
	/** A Guid result. */
	public static final ResultType GUID = new ResultType(GUID_RESULT_ORDINAL, "guid", String.class);
	
	/** The Constant CONDITIONAL_RESULT_ORDINAL. */
	public static final int CONDITIONAL_RESULT_ORDINAL = 4;
	
	/** A boolean result. */
	public static final ResultType CONDITIONAL = new ResultType(CONDITIONAL_RESULT_ORDINAL, "conditional", Boolean.class);
	
	/** The Constant DATE_RESULT_ORDINAL. */
	public static final int DATE_RESULT_ORDINAL = 5;
	
	/** A Date result. */
	public static final ResultType DATE = new ResultType(DATE_RESULT_ORDINAL, "date", Date.class);
	
	private final Class<?> resultClass;
	
	/**
	 * Instantiates a new result type.
	 *
	 * @param ordinal the ordinal
	 * @param name the name
	 * @param resultClass the result class
	 */
	protected ResultType(final int ordinal, final String name, final Class< ? > resultClass) {
		super(ordinal, name, ResultType.class);
		this.resultClass = resultClass;
	}
	
	@Override
	protected Class<ResultType> getEnumType() {
		return ResultType.class;
	}
	
	/**
	 * Gets the result class.
	 *
	 * @return the result class
	 */
	public Class<?> getResultClass() {
		return resultClass;
	}

}
