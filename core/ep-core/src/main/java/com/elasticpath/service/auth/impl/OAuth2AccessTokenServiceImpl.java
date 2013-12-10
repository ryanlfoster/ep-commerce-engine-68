package com.elasticpath.service.auth.impl;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.auth.OAuth2AccessTokenMemento;
import com.elasticpath.domain.auth.impl.OAuth2AccessTokenMementoImpl;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.persistence.support.FetchGroupConstants;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.auth.OAuth2AccessTokenService;
import com.elasticpath.service.impl.AbstractEpPersistenceServiceImpl;
import com.elasticpath.service.misc.FetchPlanHelper;

/**
 * Implementation of OAuthTokenService.
 */
public class OAuth2AccessTokenServiceImpl extends AbstractEpPersistenceServiceImpl implements OAuth2AccessTokenService {
    private static final Logger LOG = Logger.getLogger(OAuth2AccessTokenServiceImpl.class);
    private FetchPlanHelper fetchPlanHelper;

    private BeanFactory beanFactory;

    @Override
    public void removeTokensByDate(final Date removalDate) {
        
        if (removalDate == null) {
            LOG.error("null removalDate");
        }
        final FetchGroupLoadTuner accessTokenFetchGroupLoadTuner = beanFactory.getBean(ContextIdNames.FETCH_GROUP_LOAD_TUNER);
        accessTokenFetchGroupLoadTuner.addFetchGroup(FetchGroupConstants.OAUTH_BASIC);
        fetchPlanHelper.configureLoadTuner(accessTokenFetchGroupLoadTuner);
        final Object[] parameters = new Object[] {
                removalDate
        };
        getPersistenceEngine().executeNamedQuery("REMOVE_OAUTHACCESSTOKEN_BY_DATE", parameters);
        fetchPlanHelper.clearFetchPlan();
    }

    /**
     * @param fetchPlanHelper the fetchPlanHelper to set
     */
    public void setFetchPlanHelper(final FetchPlanHelper fetchPlanHelper) {
        this.fetchPlanHelper = fetchPlanHelper;
    }

    /**
     * @param beanFactory the beanFactory to set
     */
    public void setBeanFactory(final BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }
    @Override
    public OAuth2AccessTokenMemento saveOrUpdate(final OAuth2AccessTokenMemento oauthToken) {
        return getPersistenceEngine().saveOrMerge(oauthToken);
    }

    @Override
    public OAuth2AccessTokenMemento load(final String tokenId) {
        final Object[] parameters = new Object[] {
                tokenId
        };
        final List<OAuth2AccessTokenMemento> results = getPersistenceEngine().retrieveByNamedQuery("FIND_OAUTHACCESSTOKEN_BY_TOKENID", parameters);

        if (results.size() != 1) {
            return null;
        }

        return results.get(0);
    }

    @Override
    public void remove(final String tokenId) {
        final Object[] parameters = new Object[] {
                tokenId
        };
        getPersistenceEngine().executeNamedQuery("REMOVE_OAUTHACCESSTOKEN_BY_TOKENID", parameters);
    }

    @Override
    public OAuth2AccessTokenMemento getObject(final long uid) throws EpServiceException {
        return getPersistenceEngine().load(OAuth2AccessTokenMementoImpl.class, uid);
    }

}
