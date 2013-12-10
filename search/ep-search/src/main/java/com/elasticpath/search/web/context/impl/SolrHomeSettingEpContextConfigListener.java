package com.elasticpath.search.web.context.impl;

import javax.servlet.ServletContext;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.ElasticPath;
import org.apache.log4j.Logger;
import org.springframework.web.context.WebApplicationContext;

import com.elasticpath.service.search.solr.SolrIndexConstants;
import com.elasticpath.web.context.impl.EpContextConfigListener;

/**
 * <p>
 * Extension of {@link EpContextConfigListener} that sets a default value for the Solr Home property if the system property "solr.solr.home" has not
 * been set.
 * </p>
 * <p>
 * By default, the Solr Home directory will be set to the search server webapp's WEB-INF/solrHome directory.
 * </p>
 */
public class SolrHomeSettingEpContextConfigListener extends EpContextConfigListener {

	private static final Logger LOG = Logger.getLogger(SolrHomeSettingEpContextConfigListener.class);

	@Override
	protected void doElasticPathConfig(final WebApplicationContext webApplicationContext, final ServletContext servletContext) {
		super.doElasticPathConfig(webApplicationContext, servletContext);

		if (System.getProperty(SolrIndexConstants.SOLR_HOME_PROPERTY) == null) {
            ElasticPath elasticPath = getBean(ContextIdNames.ELASTICPATH);
            System.setProperty(SolrIndexConstants.SOLR_HOME_PROPERTY, elasticPath.getWebInfPath() + SolrIndexConstants.SOLR_HOME_DIR);
		}

		LOG.info("Solr home: " + System.getProperty(SolrIndexConstants.SOLR_HOME_PROPERTY));
	}
}