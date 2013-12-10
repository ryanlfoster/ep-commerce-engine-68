package com.elasticpath.ql.custom.promotion;

import java.util.HashSet;
import java.util.Set;

import com.elasticpath.ql.parser.AbstractEpQLCustomConfiguration;
import com.elasticpath.ql.parser.EpQLField;
import com.elasticpath.ql.parser.EpQLFieldDescriptor;
import com.elasticpath.ql.parser.EpQLFieldType;
import com.elasticpath.ql.parser.fieldresolver.impl.NonLocalizedFieldResolver;
import com.elasticpath.ql.parser.querybuilder.SubQueryBuilder;
import com.elasticpath.ql.parser.valueresolver.impl.EnumValueResolver;
import com.elasticpath.service.search.solr.SolrIndexConstants;

/**
 * Holds mapping between EqQL fields and Solr field descriptors for Promotion.
 */
public class PromotionConfiguration extends AbstractEpQLCustomConfiguration {

	private static final String STATE_DISABLED = "DISABLED";

	private static final String STATE_EXPIRED = "EXPIRED";

	private static final String STATE_ACTIVE = "ACTIVE";

	private NonLocalizedFieldResolver nonLocalizedFieldResolver;

	private SubQueryBuilder subQueryBuilder;

	@Override
	public void initialize() {
		/** Non-localized fields. */
		configureField(EpQLField.PROMOTION_NAME, SolrIndexConstants.PROMOTION_NAME, nonLocalizedFieldResolver, EpQLFieldType.STRING, subQueryBuilder);
		configureField(EpQLField.PROMOTION_TYPE, SolrIndexConstants.PROMOTION_RULESET_NAME, nonLocalizedFieldResolver, EpQLFieldType.STRING,
				subQueryBuilder);
		configureField(EpQLField.CATALOG_CODE, SolrIndexConstants.CATALOG_CODE, nonLocalizedFieldResolver, EpQLFieldType.STRING, subQueryBuilder);
		configureField(EpQLField.STORE_CODE, SolrIndexConstants.STORE_CODE, nonLocalizedFieldResolver, EpQLFieldType.STRING, subQueryBuilder);
		
		configurePromotionStateField();		
	}

	private void configurePromotionStateField() {

		final EnumValueResolver ruleStateEnumResolver = new EnumValueResolver();		
		ruleStateEnumResolver.setEnumValues(getPromotionStateEnumAvailableValues());

		final EpQLFieldDescriptor descriptor = new EpQLFieldDescriptor();
		descriptor.setFieldTemplate(SolrIndexConstants.PROMOTION_STATE);
		descriptor.setEpQLFieldResolver(nonLocalizedFieldResolver);
		descriptor.setEpQLValueResolver(ruleStateEnumResolver);
		descriptor.setSubQueryBuilder(subQueryBuilder);
		descriptor.setType(EpQLFieldType.ENUM);
		getAvailableEpQLObjectFields().put(EpQLField.STATE, descriptor);
	}

	private Set<String> getPromotionStateEnumAvailableValues() {
		final Set<String> enumValues = new HashSet<String>();
		enumValues.add(STATE_ACTIVE);
		enumValues.add(STATE_EXPIRED);
		enumValues.add(STATE_DISABLED);
		return enumValues;
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
