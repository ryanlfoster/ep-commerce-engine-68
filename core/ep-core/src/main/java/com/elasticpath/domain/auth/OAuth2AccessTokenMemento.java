package com.elasticpath.domain.auth;

import java.util.Date;
import java.util.Set;

import com.elasticpath.persistence.api.Entity;

/**
 * OAuthToken contains token string and serialized OAuth2AccessToken and OAuth2Authentication.
 */
public interface OAuth2AccessTokenMemento extends Entity {

    /**
     * @return token id value.
     */
    String getTokenId();

    /**
     * @param tokenId to set.
     */
    void setTokenId(String tokenId);

    /**
     * @param expiryDate the expiration date
     */
    void setExpiryDate(Date expiryDate);
    
    /**
     * @return the expiration date.
     */
    Date getExpiryDate();
    
    /**
     * The token type, as introduced in draft 11 of the OAuth 2 spec. The spec doesn't define (yet) that the valid token types are, but says it's
     * required so the default will just be "undefined".
     *
     * @return The token type, as introduced in draft 11 of the OAuth 2 spec.
     */
    String getTokenType();

    /**
     * The token type, as introduced in draft 11 of the OAuth 2 spec.
     *
     * @param tokenType The token type, as introduced in draft 11 of the OAuth 2 spec.
     */
    void setTokenType(String tokenType);

    /**
     * The scope of the token.
     *
     * @return The scope of the token.
     */
    Set<String> getScope();

    /**
     * The scope of the token.
     *
     * @param scope The scope of the token.
     */
    void setScope(Set<String> scope);
    
    /**
     * Gets the authentication memento.
     *
     * @return the authentication memento
     */
    OAuth2AuthenticationMemento getAuthenticationMemento();
    
    /**
     * Sets the authentication memento.
     *
     * @param authenticationMemento the new authentication memento
     */
    void setAuthenticationMemento(OAuth2AuthenticationMemento authenticationMemento);
    
}
