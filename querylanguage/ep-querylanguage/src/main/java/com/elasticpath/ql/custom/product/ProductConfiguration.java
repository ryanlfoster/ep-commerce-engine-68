package com.elasticpath.ql.custom.product;

import com.elasticpath.ql.parser.AbstractEpQLCustomConfiguration;
import com.elasticpath.ql.parser.EpQLField;
import com.elasticpath.ql.parser.EpQLFieldType;
import com.elasticpath.ql.parser.fieldresolver.impl.LocalizedFieldResolver;
import com.elasticpath.ql.parser.fieldresolver.impl.NonLocalizedFieldResolver;
import com.elasticpath.ql.parser.querybuilder.SubQueryBuilder;
import com.elasticpath.service.search.solr.SolrIndexConstants;

/**
 * Holds mapping between EqQL fields and Solr field descriptors for Product.
 */
public class ProductConfiguration extends AbstractEpQLCustomConfiguration {

	private LocalizedFieldResolver localizedFieldResolver;

	private AttributeFieldResolver attributeFieldResolver;

	private PriceFieldResolver priceFieldResolver;

	private NonLocalizedFieldResolver nonLocalizedFieldResolver;

	private SubQueryBuilder subQueryBuilder;

	private ProductStateQueryBuilder productStateQueryBuilder;

	@Override
	public void initialize() {
		/** Non-localized fields. */
		configureField(EpQLField.CATEGORY_CODE, SolrIndexConstants.CATEGORY_CODE, nonLocalizedFieldResolver, EpQLFieldType.STRING, subQueryBuilder);
		configureField(EpQLField.CATALOG_CODE, SolrIndexConstants.CATALOG_CODE, nonLocalizedFieldResolver, EpQLFieldType.STRING, subQueryBuilder);
		configureField(EpQLField.STORE_CODE, SolrIndexConstants.STORE_CODE, nonLocalizedFieldResolver, EpQLFieldType.STRING, subQueryBuilder);

		configureField(EpQLField.PRODUCT_START_DATE, SolrIndexConstants.START_DATE, nonLocalizedFieldResolver, EpQLFieldType.DATE, subQueryBuilder);
		configureField(EpQLField.PRODUCT_END_DATE, SolrIndexConstants.END_DATE, nonLocalizedFieldResolver, EpQLFieldType.DATE, subQueryBuilder);
		configureField(EpQLField.PRODUCT_LAST_MODIFIED_DATE, SolrIndexConstants.LAST_MODIFIED_DATE, nonLocalizedFieldResolver, EpQLFieldType.DATE,
				subQueryBuilder);

		// Note that there is no appropriate Solr index for this EpQL field. The resolution will be provided by ProductStateQueryBuilder.
		configureField(EpQLField.PRODUCT_ACTIVE, "", nonLocalizedFieldResolver, EpQLFieldType.BOOLEAN, productStateQueryBuilder);

		configureField(EpQLField.PRODUCT_PRICE, SolrIndexConstants.PRICE, priceFieldResolver, EpQLFieldType.FLOAT, subQueryBuilder);

		configureField(EpQLField.BRAND_CODE, SolrIndexConstants.BRAND_CODE, nonLocalizedFieldResolver, EpQLFieldType.STRING, subQueryBuilder);
		configureField(EpQLField.PRODUCT_CODE, SolrIndexConstants.PRODUCT_CODE, nonLocalizedFieldResolver, EpQLFieldType.STRING, subQueryBuilder);
		configureField(EpQLField.SKU_CODE, SolrIndexConstants.PRODUCT_SKU_CODE, nonLocalizedFieldResolver, EpQLFieldType.STRING, subQueryBuilder);

		/** Localized fields. */
		configureField(EpQLField.CATEGORY_NAME, SolrIndexConstants.CATEGORY_NAME_EXACT, localizedFieldResolver, EpQLFieldType.STRING,
				subQueryBuilder);
		configureField(EpQLField.BRAND_NAME, SolrIndexConstants.BRAND_NAME_EXACT, localizedFieldResolver, EpQLFieldType.STRING, subQueryBuilder);
		configureField(EpQLField.PRODUCT_NAME, SolrIndexConstants.PRODUCT_NAME_EXACT, localizedFieldResolver, EpQLFieldType.STRING, subQueryBuilder);

		/** Attributes. */
		configureField(EpQLField.ATTRIBUTE, EpQLField.ATTRIBUTE.getFieldName(), attributeFieldResolver, EpQLFieldType.STRING, subQueryBuilder);
		configureField(EpQLField.SKU_ATTRIBUTE, EpQLField.SKU_ATTRIBUTE.getFieldName(), attributeFieldResolver, EpQLFieldType.STRING,
				subQueryBuilder);
	}

	/**
	 * Sets localized field resolver requiring parameter1 (locale, currency, etc.) and restricting parameter2.
	 * 
	 * @param localizedFieldResolver the localizedFieldResolver to set
	 */
	public void setLocalizedFieldResolver(final LocalizedFieldResolver localizedFieldResolver) {
		this.localizedFieldResolver = localizedFieldResolver;
	}

	/**
	 * Sets resolver for fields with required parameter2 (attribure name or StoreCode for example) and restricted parameter1.
	 * 
	 * @param attributeFieldResolver the attributeFieldResolver to set
	 */
	public void setAttributeFieldResolver(final AttributeFieldResolver attributeFieldResolver) {
		this.attributeFieldResolver = attributeFieldResolver;
	}

	/**
	 * Sets price field resolver specific for price. Resolver requires parameter2 as obligatory and parameter1 depending on situation.
	 * 
	 * @param priceFieldResolver the priceFieldResolver to set
	 */
	public void setPriceFieldResolver(final PriceFieldResolver priceFieldResolver) {
		this.priceFieldResolver = priceFieldResolver;
	}

	/**
	 * Sets non localized field resolver used for fields without parameters.
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

	/**
	 * Sets Lucene boolean query builder to resolve product state field.
	 * 
	 * @param productStateQueryBuilder Lucene boolean query builder
	 */
	public void setProductStateQueryBuilder(final ProductStateQueryBuilder productStateQueryBuilder) {
		this.productStateQueryBuilder = productStateQueryBuilder;
	}
}
