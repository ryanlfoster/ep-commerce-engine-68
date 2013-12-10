package com.elasticpath.ql.custom.pricelist;

import com.elasticpath.ql.parser.AbstractEpQLCustomConfiguration;
import com.elasticpath.ql.parser.EpQLField;
import com.elasticpath.ql.parser.EpQLFieldType;
import com.elasticpath.ql.parser.FetchType;
import com.elasticpath.ql.parser.fieldresolver.impl.NonLocalizedFieldResolver;
import com.elasticpath.ql.parser.querybuilder.SubQueryBuilder;

/**
 * Holds mapping between EpQL fields and field descriptors for Price list assignment.
 */
public class PriceListAssignmentConfiguration extends AbstractEpQLCustomConfiguration {

	private NonLocalizedFieldResolver nonLocalizedFieldResolver;
	private SubQueryBuilder subQueryBuilder;
	
	@Override
	public void initialize() {
		setQueryPrefix("select pla.guid FROM PriceListAssignmentImpl pla ");
		configureField(EpQLField.CATALOG_CODE, "pla.catalog.code", nonLocalizedFieldResolver, EpQLFieldType.STRING, subQueryBuilder);
		setQueryPostfix("");
		setFetchType(FetchType.GUID);
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
