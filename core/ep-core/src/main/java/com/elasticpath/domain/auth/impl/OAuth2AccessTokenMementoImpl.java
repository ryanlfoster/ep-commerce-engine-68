/*
 * Copyright (c) Elastic Path Software Inc., 2011.
 */
package com.elasticpath.domain.auth.impl;

import java.util.Collections;
import java.util.Date;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.openjpa.persistence.DataCache;

import com.elasticpath.domain.auth.OAuth2AccessTokenMemento;
import com.elasticpath.domain.auth.OAuth2AuthenticationMemento;
import com.elasticpath.persistence.api.AbstractEntityImpl;

/**
 * Implementation of OAuthToken.
 */
@Entity
@Table(name = OAuth2AccessTokenMementoImpl.TABLE_NAME)
@DataCache(enabled = true, timeout = OAuth2AccessTokenMementoImpl.TIMEOUT)
public class OAuth2AccessTokenMementoImpl extends AbstractEntityImpl implements OAuth2AccessTokenMemento {

    /**
     * Serial version id.
     */
    private static final long serialVersionUID = 1L;

    /** TIMEOUT. */
    public static final int TIMEOUT = 24 * 60 * 60 * 1000;

    /** Allocation size for JPA_GENERATED_KEYS id. */
    private static final int ALLOCATION_SIZE = 1000;

    /** The name of the table & generator to use for persistence. */
    public static final String TABLE_NAME = "TOAUTHACCESSTOKEN";

    private String tokenId;

    private long uidPk;

    private Date expiryDate;
    
    private OAuth2AuthenticationMemento authenticationMemento;

    private String tokenType;

    @Override
    @Basic
    @Column(name = "TOKEN_ID", nullable = false)
    public String getTokenId() {
        return tokenId;
    }

    @Override
    public void setTokenId(final String tokenId) {
        this.tokenId = tokenId;
    }

    @Override
    @Id
    @Column(name = "UIDPK")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
    @TableGenerator(
            name = TABLE_NAME,
            table = "JPA_GENERATED_KEYS",
            pkColumnName = "ID",
            valueColumnName = "LAST_VALUE",
            pkColumnValue = TABLE_NAME,
            allocationSize = ALLOCATION_SIZE)
    public long getUidPk() {
        return uidPk;
    }

    @Override
    public void setUidPk(final long uidPk) {
        this.uidPk = uidPk;
    }

    @Override
    public void setExpiryDate(final Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    @Override
    @Basic
    @Column(name = "EXPIRY_DATE")
    public Date getExpiryDate() {
        return expiryDate;
    }

    /**
     * Overrides the guid with the token Id.
     * @return the tokenId as the Guid.
     */
    @Override
    @Transient
    public String getGuid() {
        return getTokenId();
    }
    
    @Override
    @Transient
    public void setGuid(final String guid) {
        setTokenId(guid);
    }
    
    @Override
    public void setAuthenticationMemento(final OAuth2AuthenticationMemento authenticationMemento) {
        this.authenticationMemento = authenticationMemento;
    }
    
    @Override
    @Embedded
    public OAuth2AuthenticationMemento getAuthenticationMemento() {
        return authenticationMemento;
    }

    @Override
    @Basic
    @Column(name = "TOKEN_TYPE")
    public String getTokenType() {
        return tokenType;
    }

    @Override
    public void setTokenType(final String tokenType) {
        this.tokenType = tokenType;
    }

    @Override
    @Transient
    public Set<String> getScope() {
        return Collections.emptySet();
    }

    @Override
    public void setScope(final Set<String> scope) {
        throw new UnsupportedOperationException("scope is not supported.");
    }

    @Override
    @Transient
    public int hashCode() {
        return ObjectUtils.hashCode(getGuid());
    }

    @Override
    @Transient
    public boolean equals(final Object obj) {
        if (obj instanceof OAuth2AccessTokenMementoImpl) {
            final OAuth2AccessTokenMementoImpl other = (OAuth2AccessTokenMementoImpl) obj;
            return ObjectUtils.equals(getGuid(), other.getGuid());
        }
        return false;
    }
}
