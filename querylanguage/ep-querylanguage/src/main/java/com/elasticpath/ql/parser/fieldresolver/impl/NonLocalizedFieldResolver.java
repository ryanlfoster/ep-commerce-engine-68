package com.elasticpath.ql.parser.fieldresolver.impl;

import com.elasticpath.ql.parser.EpQLTerm;
import com.elasticpath.ql.parser.EpQuery;
import com.elasticpath.ql.parser.EpQLFieldDescriptor;
import com.elasticpath.ql.parser.NativeResolvedTerm;
import com.elasticpath.ql.parser.fieldresolver.EpQLFieldResolver;
import com.elasticpath.ql.parser.gen.ParseException;

/**
 * Verifies and resolves pure fields. Restricts both parameter1 and parameter2.
 */
public class NonLocalizedFieldResolver implements EpQLFieldResolver {

	@Override
	public NativeResolvedTerm resolve(final EpQuery epQuery, final EpQLTerm epQLTerm,
			final EpQLFieldDescriptor solrTemplateDescriptor) throws ParseException {
		if (epQLTerm.getParameter1() != null || epQLTerm.getParameter2() != null) {
			throw new ParseException("No parameters should be provided for field "
					+ epQLTerm.getEpQLField().getFieldName());
		}
		NativeResolvedTerm resolvedSolrField = new NativeResolvedTerm(solrTemplateDescriptor);
		resolvedSolrField.setResolvedField(solrTemplateDescriptor.getFieldTemplate());
		return resolvedSolrField;
	}
}
