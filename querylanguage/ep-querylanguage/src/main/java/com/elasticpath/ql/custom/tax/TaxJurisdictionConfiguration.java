package com.elasticpath.ql.custom.tax;

import com.elasticpath.ql.parser.AbstractEpQLCustomConfiguration;
import com.elasticpath.ql.parser.EpQLField;
import com.elasticpath.ql.parser.EpQLFieldType;
import com.elasticpath.ql.parser.fieldresolver.impl.NonLocalizedFieldResolver;
import com.elasticpath.ql.parser.querybuilder.SubQueryBuilder;

/**
 * Holds mapping between EqQL fields and field descriptors for TaxJurisdiction.
 */
public class TaxJurisdictionConfiguration extends AbstractEpQLCustomConfiguration {
	
	private NonLocalizedFieldResolver nonLocalizedFieldResolver;
	private SubQueryBuilder subQueryBuilder;
	
	@Override
	public void initialize() {
		setQueryPrefix("SELECT tj.guid FROM TaxJurisdictionImpl tj ");
		configureField(EpQLField.TAX_JURISDICTION_CODE, "tj.guid", nonLocalizedFieldResolver, EpQLFieldType.STRING, subQueryBuilder);
		configureField(EpQLField.TAX_JURISDICTION_REGION, "tj.regionCode", nonLocalizedFieldResolver, EpQLFieldType.STRING, subQueryBuilder);
		setQueryPostfix(" ORDER BY tj.guid ASC ");
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
