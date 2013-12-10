package com.elasticpath.service.search.query.impl;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import org.springframework.orm.jpa.JpaSystemException;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.search.query.SearchTerms;
import com.elasticpath.domain.search.query.SearchTermsMemento;
import com.elasticpath.domain.search.query.SearchTermsMemento.SearchTermsId;
import com.elasticpath.persistence.dao.SearchTermsMementoDao;
import com.elasticpath.service.impl.AbstractEpServiceImpl;
import com.elasticpath.service.search.query.SearchTermsActivityStrategy;
import com.elasticpath.service.search.query.SearchTermsService;

/**
 * Operates on {@link SearchTerms}.
 */
public class SearchTermsServiceImpl extends AbstractEpServiceImpl implements SearchTermsService {
	private static final String CANNOT_DESERIALIZE_JSON = "cannot deserialize JSON into SearchTerms class: ";

	private static final String CANNOT_SERIALIZE_THE_OBJECT_INTO_JSON = "Cannot serialize the object into JSON";

	private static final Logger LOG = Logger.getLogger(SearchTermsServiceImpl.class);

	private SearchTermsMementoDao searchTermsMementoDao;
	private ObjectMapper objectMapper;
	private BeanFactory prototypeBeanFactory;
	private SearchTermsActivityStrategy searchTermsActivityStrategy;

	@Override
	public SearchTermsId saveIfNotExists(final SearchTerms searchTerms) {
		SearchTermsMemento memento = createMemento(searchTerms);
		SearchTermsMemento existingMemento = getSearchTermsMementoDao().find(memento.getId());
		try {
			if (existingMemento == null) {
				getSearchTermsMementoDao().saveSearchTermsMemento(memento);
				existingMemento = memento;
			}
			searchTermsActivityStrategy.logSearchTerm(existingMemento);
		} catch (JpaSystemException e) {
			LOG.debug("oops. Another thread probably saved a memento with the same ID. No big deal!", e);
		}
		return memento.getId();
	}

	/**
	 * Creates a memento from the search terms object.
	 *
	 * @param searchTerms the search terms
	 * @return the search terms memento
	 */
	protected SearchTermsMemento createMemento(final SearchTerms searchTerms) {
		String representation = serializeSearchTerms(searchTerms);
		SearchTermsId searchTermsId = generateIdentifier(representation);
		SearchTermsMemento memento = getPrototypeBeanFactory().getBean(ContextIdNames.SEARCH_TERMS_MEMENTO);
		memento.setSearchTermsRepresentation(representation);
		memento.setId(searchTermsId);
		return memento;
	}

	@Override
	public SearchTerms load(final SearchTermsId searchTermsId) {
		SearchTermsMemento memento = getSearchTermsMementoDao().find(searchTermsId);
		if (memento == null) {
			return null;
		}
		searchTermsActivityStrategy.logSearchTerm(memento);
		return deserializeSearchTerms(memento.getSearchTermsRepresentation());
	}

	/**
	 * Generates an identifier for a SearchTerms.
	 *
	 * @param searchTerms the string representation of the search terms
	 * @return the ID
	 */
	protected SearchTermsId generateIdentifier(final String searchTerms) {
		String shaHex = DigestUtils.sha1Hex(searchTerms);
		return new SearchTermsId(shaHex);
	}


	/**
	 * Creates a search terms object from the String representation. The current implementation uses a Jackson
	 * {@link ObjectMapper} to deserialize a JSON representation into the search terms object.
	 *
	 * @param representation the String representation of the search terms
	 * @return the search terms
	 */
	protected SearchTerms deserializeSearchTerms(final String representation) {
		Class<SearchTerms> searchTermsClass = getPrototypeBeanFactory().getBeanImplClass(ContextIdNames.SEARCH_TERMS);
		try {
			return getObjectMapper().readValue(representation, searchTermsClass);
		} catch (JsonParseException e) {
			throw new EpSystemException(CANNOT_DESERIALIZE_JSON + searchTermsClass.getName(), e);
		} catch (JsonMappingException e) {
			throw new EpSystemException(CANNOT_DESERIALIZE_JSON + searchTermsClass.getName(), e);
		} catch (IOException e) {
			throw new EpSystemException(CANNOT_DESERIALIZE_JSON + searchTermsClass.getName(), e);
		}
	}

	/**
	 * Serializes the search terms object into a String. The current implementation uses a Jackson
	 * {@link ObjectMapper} to serialize the object into JSON.
	 *
	 * @param searchTerms the search terms
	 * @return the string
	 */
	protected String serializeSearchTerms(final SearchTerms searchTerms) {
		try {
			return getObjectMapper().writeValueAsString(searchTerms);
		} catch (JsonGenerationException e) {
			throw new EpSystemException(CANNOT_SERIALIZE_THE_OBJECT_INTO_JSON, e);
		} catch (JsonMappingException e) {
			throw new EpSystemException(CANNOT_SERIALIZE_THE_OBJECT_INTO_JSON, e);
		} catch (IOException e) {
			throw new EpSystemException(CANNOT_SERIALIZE_THE_OBJECT_INTO_JSON, e);
		}
	}

	public void setSearchTermsMementoDao(final SearchTermsMementoDao searchTermsMementoDao) {
		this.searchTermsMementoDao = searchTermsMementoDao;
	}

	protected SearchTermsMementoDao getSearchTermsMementoDao() {
		return searchTermsMementoDao;
	}

	public void setObjectMapper(final ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	public ObjectMapper getObjectMapper() {
		return objectMapper;
	}

	public void setPrototypeBeanFactory(final BeanFactory prototypeBeanFactory) {
		this.prototypeBeanFactory = prototypeBeanFactory;
	}

	public BeanFactory getPrototypeBeanFactory() {
		return prototypeBeanFactory;
	}

	public void setSearchTermsActivityStrategy(final SearchTermsActivityStrategy searchTermsActivityStrategy) {
		this.searchTermsActivityStrategy = searchTermsActivityStrategy;
	}
}
