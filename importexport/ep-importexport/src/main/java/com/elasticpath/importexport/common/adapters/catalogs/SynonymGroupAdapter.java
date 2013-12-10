package com.elasticpath.importexport.common.adapters.catalogs;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.LocaleUtils;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.search.Synonym;
import com.elasticpath.domain.search.SynonymGroup;
import com.elasticpath.importexport.common.adapters.AbstractDomainAdapterImpl;
import com.elasticpath.importexport.common.dto.catalogs.SynonymGroupDTO;
import com.elasticpath.importexport.common.exception.runtime.PopulationRollbackException;

/**
 * The implementation of <code>DomainAdapter</code> interface.<br>
 * It is responsible for data transformation between <code>SynonymGroup</code> and
 * <code>SynonymGroupDTO</code> objects.
 */
public class SynonymGroupAdapter extends AbstractDomainAdapterImpl<SynonymGroup, SynonymGroupDTO> {
	
	@Override
	public void populateDTO(final SynonymGroup source, final SynonymGroupDTO target) {
		target.setLocale(source.getLocale().toString());
		target.setConceptTerm(source.getConceptTerm());

		final List<String> synonyms = new ArrayList<String>();
		for (Synonym synonym : source.getSynonyms()) {
			synonyms.add(synonym.getSynonym());
		}
		target.setSynonyms(synonyms);		
	}

	@Override
	public void populateDomain(final SynonymGroupDTO source, final SynonymGroup target) {
		Locale locale = null;
		try {
			locale = LocaleUtils.toLocale(source.getLocale());
			if (!LocaleUtils.isAvailableLocale(locale)) {
				throw new IllegalArgumentException();
			}
		} catch (IllegalArgumentException exception) {
			throw new PopulationRollbackException("IE-10000", exception, source.getLocale());
		}
		target.setLocale(locale);
		target.setConceptTerm(source.getConceptTerm());

		final List<Synonym> synonyms = new ArrayList<Synonym>();
		for (String synonymName : source.getSynonyms()) {			
			Synonym synonym = getBean(ContextIdNames.SYNONYM);
			synonym.setSynonym(synonymName);
			synonyms.add(synonym);
		}
		target.setSynonyms(synonyms);
	}
	
	@Override
	public SynonymGroup createDomainObject() {
		return getBean(ContextIdNames.SYNONYM_GROUP);
	}

	@Override
	public SynonymGroupDTO createDtoObject() {
		return new SynonymGroupDTO();
	}
}
