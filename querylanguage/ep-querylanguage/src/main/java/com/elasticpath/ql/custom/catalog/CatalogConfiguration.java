package com.elasticpath.ql.custom.catalog;

import com.elasticpath.ql.parser.AbstractEpQLCustomConfiguration;
import com.elasticpath.ql.parser.EpQLField;
import com.elasticpath.ql.parser.EpQLFieldType;
import com.elasticpath.ql.parser.fieldresolver.impl.NonLocalizedFieldResolver;
import com.elasticpath.ql.parser.querybuilder.SubQueryBuilder;

/**
 * Holds mapping between EpQL fields and field descriptors for Catalog.
 */
public class CatalogConfiguration extends AbstractEpQLCustomConfiguration {
	
	private NonLocalizedFieldResolver nonLocalizedFieldResolver;

	private SubQueryBuilder subQueryBuilder;

	@Override
	public void initialize() {
		//FIXME: move template field out to some field holder
		setQueryPrefix("select c.uidPk from CatalogImpl c ");
		configureField(EpQLField.CATALOG_CODE, "c.code", nonLocalizedFieldResolver, EpQLFieldType.STRING, subQueryBuilder);
		setQueryPostfix("ORDER BY c.uidPk ASC");
	}
	
	/**
	 * Sets non localized field resolver restricting both parameter1 and parameter2 for fields of Code kind.
	 *  
	 * @param nonLocalizedFieldResolver non localized field resolver
	 */
	public void setNonLocalizedFieldResolver(final NonLocalizedFieldResolver nonLocalizedFieldResolver) {
		this.nonLocalizedFieldResolver = nonLocalizedFieldResolver;
	}

	/**
	 * Sets conventional query builder.
	 * 
	 * @param subQueryBuilder sub query builder
	 */
	public void setSubQueryBuilder(final SubQueryBuilder subQueryBuilder) {
		this.subQueryBuilder = subQueryBuilder;
	}
}
