package com.elasticpath.service.auth;

import java.util.Date;

import com.elasticpath.domain.auth.OAuth2AccessTokenMemento;

/**
 * <code>OAuthTokenService</code> provides services for managing a oauth2 token persistence.
 */
public interface OAuth2AccessTokenService {

    /**
     * @param oauthToken to save.
     * @return OAuthAccessToken
     */
    OAuth2AccessTokenMemento saveOrUpdate(OAuth2AccessTokenMemento oauthToken);

    /**
     * Load an oauth2 token.
     * @param tokenId - the ID of the oauth2 token.
     * @return OAuthAccessToken
     */
    OAuth2AccessTokenMemento load(String tokenId);

    /**
     * Remove an oauth2 token.
     * @param tokenId - the ID of the oauth2 token.
     */
    void remove(String tokenId);


    /**
     * Removes tokens which have expiration dates older than removalDate.
     *
     * @param removalDate the date.
     */
    void removeTokensByDate(Date removalDate);
}
