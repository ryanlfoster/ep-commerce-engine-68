package com.elasticpath.domain.auth;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * A memento for a {@link org.springframework.security.oauth2.provider.ClientAuthenticationToken}.
 */
@Embeddable
public class ClientAuthenticationMemento {
    private String clientId;
    
    private String clientSecret;
    
    /**
     * Sets the client id.
     *
     * @param clientId the new client id
     * 
     * @see org.springframework.security.oauth2.provider.ClientAuthenticationToken#setClientId(String)
     * 
     */
    public void setClientId(final String clientId) {
        this.clientId = clientId;
    }
    
    /**
     * Gets the client id.
     *
     * @return the client id
     * 
     * @see org.springframework.security.oauth2.provider.ClientAuthenticationToken#getClientId()
     */
    @Basic
    @Column(name = "CLIENT_ID", nullable = false)
    public String getClientId() {
        return clientId;
    }
    
    /**
     * Sets the client secret.
     *
     * @param clientSecret the new client secret
     * 
     * @see org.springframework.security.oauth2.provider.ClientAuthenticationToken#setClientSecret(String)
     */
    public void setClientSecret(final String clientSecret) {
        this.clientSecret = clientSecret;
    }
    
    /**
     * Gets the client secret.
     *
     * @return the client secret
     * 
     * @see org.springframework.security.oauth2.provider.ClientAuthenticationToken#getClientSecret()
     */
    @Basic
    @Column(name = "CLIENT_SECRET", nullable = true)
    public String getClientSecret() {
        return clientSecret;
    }
}
