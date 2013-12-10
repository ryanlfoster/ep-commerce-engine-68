package com.elasticpath.ql.custom.category;

import com.elasticpath.ql.parser.AbstractEpQLCustomConfiguration;
import com.elasticpath.ql.parser.EpQLField;
import com.elasticpath.ql.parser.EpQLFieldType;
import com.elasticpath.ql.parser.fieldresolver.impl.LocalizedFieldResolver;
import com.elasticpath.ql.parser.fieldresolver.impl.NonLocalizedFieldResolver;
import com.elasticpath.ql.parser.querybuilder.SubQueryBuilder;
import com.elasticpath.service.search.solr.SolrIndexConstants;

/**
 * Holds mapping between EpQL fields and Solr field descriptors for Category.
 */
public class CategoryConfiguration extends AbstractEpQLCustomConfiguration {

	private LocalizedFieldResolver localizedFieldResolver;
	private NonLocalizedFieldResolver nonLocalizedFieldResolver;
	
	private SubQueryBuilder subQueryBuilder;

	@Override
	public void initialize() {
		/** Non-localized fields. */
		configureField(EpQLField.CATEGORY_CODE, SolrIndexConstants.CATEGORY_CODE, nonLocalizedFieldResolver, EpQLFieldType.STRING, subQueryBuilder);
		configureField(EpQLField.CATALOG_CODE, SolrIndexConstants.CATALOG_CODE, nonLocalizedFieldResolver, EpQLFieldType.STRING, subQueryBuilder);

		/** Localized fields. */
		configureField(EpQLField.CATEGORY_NAME, SolrIndexConstants.CATEGORY_NAME, localizedFieldResolver, EpQLFieldType.STRING, subQueryBuilder);
	}

	/**
	 * Sets localized field resolver requiring parameter1 (locale, currency, etc.) and restricting parameter2.
	 * 
	 * @param localizedFieldResolver localized field resolver
	 */
	public void setLocalizedFieldResolver(final LocalizedFieldResolver localizedFieldResolver) {
		this.localizedFieldResolver = localizedFieldResolver;
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
