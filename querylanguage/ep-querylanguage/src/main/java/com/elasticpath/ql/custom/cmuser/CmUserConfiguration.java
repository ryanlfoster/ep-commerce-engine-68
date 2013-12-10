package com.elasticpath.ql.custom.cmuser;

import com.elasticpath.ql.parser.AbstractEpQLCustomConfiguration;
import com.elasticpath.ql.parser.EpQLField;
import com.elasticpath.ql.parser.EpQLFieldType;
import com.elasticpath.ql.parser.FetchType;
import com.elasticpath.ql.parser.fieldresolver.impl.NonLocalizedFieldResolver;
import com.elasticpath.ql.parser.querybuilder.SubQueryBuilder;

/**
 * Holds mapping between EPQL fields and field descriptors for CmUser.
 */
public class CmUserConfiguration extends AbstractEpQLCustomConfiguration {

	private NonLocalizedFieldResolver nonLocalizedFieldResolver;

	private SubQueryBuilder subQueryBuilder;
	
	@Override
	public void initialize() {
		//cm.userRoles is a Collection
		//use DISTINCT so that 'FIND CmUser' (with no where clause) will work correctly
		setQueryPrefix("SELECT DISTINCT cm.guid FROM CmUserImpl cm LEFT JOIN cm.userRoles roles ");

		configureField(EpQLField.CMUSER_ROLE, "roles.name", nonLocalizedFieldResolver, 
				EpQLFieldType.STRING, subQueryBuilder);

		setQueryPostfix(" ORDER BY cm.guid ASC");
		setFetchType(FetchType.GUID);
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
	 * @param nonLocalizedFieldResolver Non-localized field resolver.
	 */
	public void setNonLocalizedFieldResolver(final NonLocalizedFieldResolver nonLocalizedFieldResolver) {
		this.nonLocalizedFieldResolver = nonLocalizedFieldResolver;
	}

}