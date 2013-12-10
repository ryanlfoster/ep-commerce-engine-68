package com.elasticpath.test.integration.auth;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.elasticpath.domain.auth.ClientAuthenticationMemento;
import com.elasticpath.domain.auth.OAuth2AccessTokenMemento;
import com.elasticpath.domain.auth.OAuth2AuthenticationMemento;
import com.elasticpath.domain.auth.UserAuthenticationMemento;
import com.elasticpath.domain.auth.impl.OAuth2AccessTokenMementoImpl;
import com.elasticpath.service.auth.OAuth2AccessTokenService;
import com.elasticpath.test.integration.BasicSpringContextTest;
import com.elasticpath.test.integration.DirtiesDatabase;

/**
 * Integration tests for {@link OAuthTokenService}.
 */
public class ExpiredOAuthTokenRemovalJobIntegrationTest extends BasicSpringContextTest {

	@Autowired
	@Qualifier("oAuth2AccessTokenService")
    private OAuth2AccessTokenService oauthAccessTokenService;

    /**
     * Tests the happy path of deleting an expired token.
     */
    @DirtiesDatabase
    @Test
    public void testDeletingTokenHappyPath() {
        final String tokenId = "ATokenID";
        final Date expiryDate = new Date();

        OAuth2AccessTokenMemento oauthToken = createOAuthToken(tokenId, expiryDate);

        oauthAccessTokenService.saveOrUpdate(oauthToken);

        assertNotNull(oauthAccessTokenService.load(tokenId));
        final Date futureDate = getDate(expiryDate, Calendar.HOUR, 5);

        oauthAccessTokenService.removeTokensByDate(futureDate);

        assertNull(oauthAccessTokenService.load(tokenId));
    }

    /**
     * Tests deleting tokens around the expiry boundary. <br>
     * An older token will be removed, a younger token will remain and a token that has the same time as the boundary will still remain.
     */
    @DirtiesDatabase
    @Test
    public void testTokenDeletionDateBoundaries() {
        final Date expiryDate = new Date();

        String tokenIdPast = "tokenID_expiry_past";
        OAuth2AccessTokenMemento oauthTokenPast = createOAuthToken(tokenIdPast, getDate(expiryDate, Calendar.HOUR, -5));
        String tokenIdNow = "tokenID_exipry_now";
        OAuth2AccessTokenMemento oauthTokenNow = createOAuthToken(tokenIdNow, expiryDate);
        String tokenIdFuture = "tokenID_expiry_future";
        OAuth2AccessTokenMemento oauthTokenFuture = createOAuthToken(tokenIdFuture, getDate(expiryDate, Calendar.HOUR, 5));

        oauthAccessTokenService.saveOrUpdate(oauthTokenPast);
        oauthAccessTokenService.saveOrUpdate(oauthTokenNow);
        oauthAccessTokenService.saveOrUpdate(oauthTokenFuture);

        assertNotNull(oauthAccessTokenService.load(tokenIdPast));
        assertNotNull(oauthAccessTokenService.load(tokenIdNow));
        assertNotNull(oauthAccessTokenService.load(tokenIdFuture));

        oauthAccessTokenService.removeTokensByDate(expiryDate);

        assertNull("Should not have past token after cleanup.", oauthAccessTokenService.load(tokenIdPast));
        assertNotNull("Should have current token after clean up.", oauthAccessTokenService.load(tokenIdNow));
        assertNotNull("Should have future token after cleanup.", oauthAccessTokenService.load(tokenIdFuture));
    }

    /**
     * Tests that passing a null date into the {@link OAuthTokenService#removeTokensByDate(Date)} will not cause any weird behaviour. <br>
     * Expired tokens will still exist in database.
     */
    @DirtiesDatabase
    @Test
    public void testNullDate() {
        final Date expiryDate = new Date();

        String tokenIdPast = "tokenID_expiry_past";
        OAuth2AccessTokenMemento oauthToken = createOAuthToken(tokenIdPast, getDate(expiryDate, Calendar.HOUR, -5));

        oauthAccessTokenService.saveOrUpdate(oauthToken);
        assertNotNull(oauthAccessTokenService.load(tokenIdPast));

        oauthAccessTokenService.removeTokensByDate(null);

        assertNotNull("Past token should still exist.", oauthAccessTokenService.load(tokenIdPast));
    }

    private OAuth2AccessTokenMemento createOAuthToken(final String tokenId, final Date expiryDate) {
        OAuth2AccessTokenMemento oauthToken = new OAuth2AccessTokenMementoImpl();
        oauthToken.setTokenId(tokenId);
        oauthToken.setExpiryDate(expiryDate);
        oauthToken.setTokenType("undefined");
        OAuth2AuthenticationMemento authenticationMemento = createAuthentication();
        oauthToken.setAuthenticationMemento(authenticationMemento);
        return oauthToken;
    }

    private OAuth2AuthenticationMemento createAuthentication() {
        OAuth2AuthenticationMemento authenticationMemento = new OAuth2AuthenticationMemento();
        ClientAuthenticationMemento clientAuthenticationMemento = new ClientAuthenticationMemento();
        clientAuthenticationMemento.setClientId("clientID");
        clientAuthenticationMemento.setClientSecret("*****");
        authenticationMemento.setClientAuthenticationMemento(clientAuthenticationMemento);
        UserAuthenticationMemento userAuthenticationMemento = new UserAuthenticationMemento();
        userAuthenticationMemento.setCustomerGuid("00000000-0000000");
        userAuthenticationMemento.setRole("role");
        userAuthenticationMemento.setCredentials("haha nice try");
        userAuthenticationMemento.setStoreCode("storecode");
        authenticationMemento.setUserAuthenticationMemento(userAuthenticationMemento);
        return authenticationMemento;
    }

    private Date getDate(final Date expiryDate, final int calendarPosition, final int offset) {
        final Calendar instance = Calendar.getInstance();
        instance.setTime(expiryDate);
        instance.add(calendarPosition, offset);
        return instance.getTime();
    }
}