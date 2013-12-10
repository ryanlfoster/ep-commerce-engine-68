package com.elasticpath.ql.custom.setting;

import com.elasticpath.ql.parser.EpQLTerm;
import com.elasticpath.ql.parser.EpQuery;
import com.elasticpath.ql.parser.EpQLFieldDescriptor;
import com.elasticpath.ql.parser.NativeResolvedTerm;
import com.elasticpath.ql.parser.fieldresolver.EpQLFieldResolver;
import com.elasticpath.ql.parser.gen.ParseException;

/**
 * Metadata key field descriptor. This resolver treats parameter 2 as metadata key.
 */
public class MetadataFieldResolver implements EpQLFieldResolver {

	@Override
	public NativeResolvedTerm resolve(final EpQuery epQuery, final EpQLTerm epQLTerm, final EpQLFieldDescriptor solrTemplateDescriptor)
			throws ParseException {
		if (epQLTerm.getParameter2() == null) {
			throw new ParseException("Metadata key has to be specified in curly brackets.");
		}

		if (epQLTerm.getParameter1() != null) {
			throw new ParseException("Metadata key may not have a parameter in square brackets.");
		}

		NativeResolvedTerm resolvedSolrField = new NativeResolvedTerm(solrTemplateDescriptor);
		/** sets major field. */
		resolvedSolrField.setResolvedField(solrTemplateDescriptor.getMultiFieldTemplate()[0]);
		resolvedSolrField.setResolvedMultiField(solrTemplateDescriptor.getMultiFieldTemplate());
		return resolvedSolrField;
	}
}
