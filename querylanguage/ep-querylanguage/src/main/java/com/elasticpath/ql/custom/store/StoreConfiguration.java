package com.elasticpath.ql.custom.store;

import com.elasticpath.ql.parser.AbstractEpQLCustomConfiguration;
import com.elasticpath.ql.parser.EpQLField;
import com.elasticpath.ql.parser.EpQLFieldType;
import com.elasticpath.ql.parser.fieldresolver.impl.NonLocalizedFieldResolver;
import com.elasticpath.ql.parser.querybuilder.SubQueryBuilder;

/**
 * Holds mapping between EPQL fields and Store domain object fields.
 */
public class StoreConfiguration extends AbstractEpQLCustomConfiguration {

	private NonLocalizedFieldResolver nonLocalizedFieldResolver;
	private SubQueryBuilder subQueryBuilder;
	
	@Override
	public void initialize() {
		setQueryPrefix("SELECT s.code FROM StoreImpl s ");
		configureField(EpQLField.STORE_CODE, "s.code", nonLocalizedFieldResolver, EpQLFieldType.STRING, subQueryBuilder);
		setQueryPostfix(" ORDER BY s.code ASC");
	}

	/**
	 * @param nonLocalizedFieldResolver Non-localized field resolver.
	 */
	public void setNonLocalizedFieldResolver(final NonLocalizedFieldResolver nonLocalizedFieldResolver) {
		this.nonLocalizedFieldResolver = nonLocalizedFieldResolver;
	}

	/**
	 * @param subQueryBuilder Subquery builder.
	 */
	public void setSubQueryBuilder(final SubQueryBuilder subQueryBuilder) {
		this.subQueryBuilder = subQueryBuilder;
	}
}
