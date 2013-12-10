package com.elasticpath.domain.auth;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;

/**
 * A memento of an {@link org.springframework.security.oauth2.provider.OAuth2Authentication<C, U>}.
 */
@Embeddable
public class OAuth2AuthenticationMemento {
    private UserAuthenticationMemento userAuthenticationMemento;
    
    private ClientAuthenticationMemento clientAuthenticationMemento;
    
    /**
     * Gets the user authentication memento.
     *
     * @return the user authentication memento
     */
    @Embedded
    public UserAuthenticationMemento getUserAuthenticationMemento() {
        return userAuthenticationMemento;
    }
    
    /**
     * Sets the user authentication memento.
     *
     * @param userAuthenticationMemento the new user authentication memento
     */
    public void setUserAuthenticationMemento(final UserAuthenticationMemento userAuthenticationMemento) {
        this.userAuthenticationMemento = userAuthenticationMemento;
    }
    
    /**
     * Gets the client authentication memento.
     *
     * @return the client authentication memento
     */
    @Embedded
    public ClientAuthenticationMemento getClientAuthenticationMemento() {
        return clientAuthenticationMemento;
    }
    
    /**
     * Sets the client authentication memento.
     *
     * @param clientAuthenticationMemento the new client authentication memento
     */
    public void setClientAuthenticationMemento(final ClientAuthenticationMemento clientAuthenticationMemento) {
        this.clientAuthenticationMemento = clientAuthenticationMemento;
    }
}
