package com.elasticpath.ql.custom.setting;

import java.util.ArrayList;
import java.util.List;

import com.elasticpath.ql.parser.EpQLField;
import com.elasticpath.ql.parser.EpQLFieldType;
import com.elasticpath.ql.parser.EpQLTerm;
import com.elasticpath.ql.parser.EpQuery;
import com.elasticpath.ql.parser.gen.ParseException;
import com.elasticpath.ql.parser.valueresolver.impl.SQLValueResolver;


/**
 * <code>SQLSettingValueResolver</code> extends <code>SQLValueResolver</code> to provide custom values resolution for MetadataKey field.
 */
public class SQLSettingValueResolver extends SQLValueResolver {

	private static final String SETTING_PATH_DELIM = "/";
	private static final String ANY_ENDING = "%";
	private static final String ANY_VALUE = "*";

	@Override
	public List<String> resolve(final EpQLTerm epQLTerm, final EpQLFieldType fieldType, final EpQuery epQuery) throws ParseException {
		if (epQLTerm.getEpQLField() == EpQLField.METADATAKEY) {
			return resolveMetadataKeyValue(epQLTerm, epQuery);			
		} else if (epQLTerm.getEpQLField() == EpQLField.NAMESPACE) {
			return resolveNamespaceValue(epQLTerm, epQuery);
		}	
		return super.resolve(epQLTerm, fieldType, epQuery);		
	}
	
	/**
	 * Resolves metadata to either one or two values. There will single value if metadata value has been specified by * (wildcard),
	 * there will be two values otherwise, whereas first value will be matadata key value and second will be metadata value.
	 * 
	 * @param epQLTerm EpQLTerm
	 * @param epQuery EpQuery
	 * @return list of resolved values
	 * @throws ParseException in case of errors
	 */
	List<String> resolveMetadataKeyValue(final EpQLTerm epQLTerm, final EpQuery epQuery) throws ParseException {
		List<String> values = new ArrayList<String>(2);		
		addMetadataKeyParameter(epQLTerm, epQuery, values);
		addMetadataValueParameter(epQLTerm, epQuery, values);
		return values;
	}
	
	private void addMetadataValueParameter(final EpQLTerm epQLTerm, final EpQuery epQuery, final List<String> values) throws ParseException {
		final String value = extractValue(epQLTerm);
		if (!value.equals(ANY_VALUE)) {
			epQuery.addParam(value);
			values.add(VALUE);
		}
	}
	
	private void addMetadataKeyParameter(final EpQLTerm epQLTerm, final EpQuery epQuery, final List<String> values) {
		epQuery.addParam(epQLTerm.getParameter2());
		values.add(VALUE);
	}
	
	/**
	 * Resolves EP QL's namespace value to either one or two values. <br>
	 * 'A' will yield 'A' and two fake values just to indicate that it differs from 'A/'<br>
	 * 'A/' will yield A/%'
	 * 'A%' will yield 'A%' and 'A%/%'
	 * 
	 * @param epQLTerm EpQLTerm
	 * @param epQuery EpQuery 
	 * @return list of resolved values
	 * @throws ParseException in case of errors
	 */
	List<String> resolveNamespaceValue(final EpQLTerm epQLTerm, final EpQuery epQuery) throws ParseException {
		List<String> values = new ArrayList<String>(2);
		String  namespace = extractValue(epQLTerm);
		if (namespace.endsWith(SETTING_PATH_DELIM)) {
			epQuery.addParam(namespace + ANY_ENDING);			
			values.add(VALUE);	
		} else if (namespace.endsWith(ANY_ENDING)) {
			epQuery.addParam(namespace);
			epQuery.addParam(namespace + SETTING_PATH_DELIM + ANY_ENDING);
			values.add(VALUE);
			values.add(VALUE);
		} else {
			epQuery.addParam(namespace);
			values.add(VALUE);
			values.add(VALUE);
			values.add(VALUE);
		}
		
		return values;
	}

}