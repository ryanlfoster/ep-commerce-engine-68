package com.elasticpath.ql.custom.setting;

import com.elasticpath.ql.parser.AbstractEpQLCustomConfiguration;
import com.elasticpath.ql.parser.EpQLField;
import com.elasticpath.ql.parser.EpQLFieldDescriptor;
import com.elasticpath.ql.parser.EpQLFieldType;
import com.elasticpath.ql.parser.FetchType;
import com.elasticpath.ql.parser.fieldresolver.EpQLFieldResolver;
import com.elasticpath.ql.parser.fieldresolver.impl.NonLocalizedFieldResolver;
import com.elasticpath.ql.parser.querybuilder.SubQueryBuilder;

/**
 * EPQL Configuration for System Configuration Settings.
 */
public class SettingConfiguration extends AbstractEpQLCustomConfiguration {

	private NonLocalizedFieldResolver nonLocalizedFieldResolver;

	private SubQueryBuilder subQueryBuilder;

	private MetadataFieldResolver metadataFieldResolver;

	@Override
	public void initialize() {
		setFetchType(FetchType.GUID);
		setQueryPrefix("select distinct d.path, v.context from "
				+ "TSETTINGDEFINITION d left join TSETTINGVALUE v on d.uidpk=v.setting_definition_uid "
				+ "left join TSETTINGMETADATA m on d.uidpk=m.setting_definition_uid ");
		configureField(EpQLField.NAMESPACE, "d.path", nonLocalizedFieldResolver, EpQLFieldType.STRING, subQueryBuilder);
		configureField(EpQLField.CONTEXT, "v.context", nonLocalizedFieldResolver, EpQLFieldType.STRING, subQueryBuilder);
		configureMetadataKeyField(EpQLField.METADATAKEY, metadataFieldResolver, EpQLFieldType.STRING, subQueryBuilder, "m.metadata_key", "m.value");
		setQueryPostfix("");
	}

	private void configureMetadataKeyField(final EpQLField epQLFieldName, final EpQLFieldResolver fieldResolver, // NOPMD
			final EpQLFieldType fieldType, final SubQueryBuilder subQueryBuilder, final String... multiFieldTemplate) {
		final EpQLFieldDescriptor descriptor = new EpQLFieldDescriptor();
		descriptor.setMultiFieldTemplate(multiFieldTemplate);
		descriptor.setEpQLFieldResolver(fieldResolver);
		descriptor.setEpQLValueResolver(getEpQLValueResolver());
		descriptor.setSubQueryBuilder(subQueryBuilder);
		descriptor.setType(fieldType);
		getAvailableEpQLObjectFields().put(epQLFieldName, descriptor);
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

	/**
	 * @param metadataFieldResolver the metadataFieldResolver to set
	 */
	public void setMetadataFieldResolver(final MetadataFieldResolver metadataFieldResolver) {
		this.metadataFieldResolver = metadataFieldResolver;
	}
}
