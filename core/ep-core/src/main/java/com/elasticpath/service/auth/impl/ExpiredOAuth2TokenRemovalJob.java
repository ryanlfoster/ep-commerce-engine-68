package com.elasticpath.service.auth.impl;

import java.util.Date;

import org.apache.log4j.Logger;

import com.elasticpath.service.auth.OAuth2AccessTokenService;
import com.elasticpath.service.misc.TimeService;

/**
 * Job to be run by quartz which will remove expired OAuth2 Tokens from persistence engine.
 */
public class ExpiredOAuth2TokenRemovalJob {

    private static final Logger LOG = Logger.getLogger(ExpiredOAuth2TokenRemovalJob.class);

    private TimeService timeService;

    private OAuth2AccessTokenService oAuth2AccessTokenService;

    /**
     * Cleans up the expired OAuth2 Tokens.
     */
    public void cleanUpExpiredOAuth2Tokens() {
        LOG.info("Cleaning up OAuth2 tokens");

        final Date currentTime = getTimeService().getCurrentTime();
        oAuth2AccessTokenService.removeTokensByDate(currentTime);
    }

    /**
     * @return the timeService
     */
    protected TimeService getTimeService() {
        return timeService;
    }

    /**
     * @param timeService the timeService to set
     */
    public void setTimeService(final TimeService timeService) {
        this.timeService = timeService;
    }

    /**
     * @param oauthTokenService the tokenService to set
     */
    public void setOAuth2AccessTokenService(final OAuth2AccessTokenService oauthTokenService) {
        this.oAuth2AccessTokenService = oauthTokenService;
    }

}
