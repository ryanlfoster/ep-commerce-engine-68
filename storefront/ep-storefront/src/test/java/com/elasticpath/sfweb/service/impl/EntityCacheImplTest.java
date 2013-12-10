package com.elasticpath.sfweb.service.impl;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;


import java.util.Arrays;
import java.util.List;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.persistence.api.PersistenceEngine;

public class EntityCacheImplTest {
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private static final String UIDPK = "uidPk";
	private static final String GUID = "guid";
	private static final String ENTITY_NAME = "FOO";

	private Ehcache ehcache;
	private List<String> keyProperties;
	private EntityCacheImpl<Foo> entityCache;
	private final PersistenceEngine persistenceEngine = context.mock(PersistenceEngine.class);
	private final Foo foo1 = new Foo(1L, "one", "foo1"),
		foo2 = new Foo(2L, "two", "foo2");

	@Before
	public void setUp() {
		ehcache = context.mock(Ehcache.class);
		keyProperties = Arrays.asList(new String[] {UIDPK, GUID});

		entityCache = new EntityCacheImpl<Foo>();
		entityCache.setCache(ehcache);
		entityCache.setEntityName(ENTITY_NAME);
		entityCache.setKeyProperties(keyProperties);
		entityCache.setPersistenceEngine(persistenceEngine);
	}

	@Test
	public void ensurePutDetachesObjectAndAddsOneEntryPerKeyIntoCache() {
		final Foo detachedFoo = new Foo(foo1.getUidPk(), foo1.getGuid() + "-detached", foo1.getPayload());

		// Expectations
		context.checking(new Expectations() { {
			Element uidPkElement = new Element("FOO-uidPk-1", detachedFoo);
			Element guidElement = new Element("FOO-guid-one-detached", detachedFoo);

			oneOf(persistenceEngine).detach(foo1); will(returnValue(detachedFoo));
			oneOf(ehcache).put(uidPkElement);
			oneOf(ehcache).put(guidElement);
		} });

		// When
		Foo cached = entityCache.put(foo1);
		assertSame("Should return detached object", detachedFoo, cached);
	}

	@Test
	public void ensurePutIsTolerantOfNullKeyValues() {
		// Given
		foo1.setGuid(null);

		// Expectations
		context.checking(new Expectations() { {
			Foo detachedFoo = new Foo(foo1.getUidPk(), foo1.getGuid(), foo1.getPayload());
			Element uidPkElement = new Element("FOO-uidPk-1", detachedFoo);

			oneOf(persistenceEngine).detach(foo1); will(returnValue(detachedFoo));
			oneOf(ehcache).put(uidPkElement);
		} });

		// When
		entityCache.put(foo1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void ensurePutPukesOnNullInput() {
		entityCache.put(null);
	}

	@Test
	public void ensureGetRetrievesValuesByKey() {
		// Expectations
		context.checking(new Expectations() { {
			final Element foo2Element = new Element("FOO-uidPk-2", foo2);

			oneOf(ehcache).get("FOO-uidPk-2"); will(returnValue(foo2Element));
		} });

		// When
		Foo cached = entityCache.get(UIDPK, 2);

		// Then
		assertSame("Cache should retrieve entity", foo2, cached);
	}

	@Test
	public void ensureGetReturnsNullIfValueNotFound() {
		// Expectations
		context.checking(new Expectations() { {
			oneOf(ehcache).get("FOO-uidPk-2"); will(returnValue(null));
		} });

		// When
		Foo cached = entityCache.get(UIDPK, 2);

		// Then
		assertNull("Cache should retrieve entity", cached);
	}

	@Test
	public void ensureInvalidateAllInvalidatesCache() {
		// Expectations
		context.checking(new Expectations() { {
			oneOf(ehcache).removeAll();
		} });

		// When
		entityCache.invalidate();
	}

	@Test
	public void ensureInvalidateByKeyInvalidatesAllCacheKeys() {
		// Expectations
		context.checking(new Expectations() { {
			oneOf(ehcache).removeAll();
		} });

		// When
		entityCache.invalidate(1L);
	}

	public static class Foo implements Persistable {
		private static final long serialVersionUID = 1L;

		long uidPk;
		String guid;
		String payload;

		public Foo(final long uidPk, final String guid, final String payload) {
			this.uidPk = uidPk;
			this.guid = guid;
			this.payload = payload;
		}

		@Override
		public long getUidPk() {
			return uidPk;
		}

		@Override
		public void setUidPk(final long uidPk) {
			this.uidPk = uidPk;
		}

		@Override
		public boolean isPersisted() {
			return getUidPk() != 0;
		}

		public String getGuid() {
			return guid;
		}

		public void setGuid(final String guid) {
			this.guid = guid;
		}

		public String getPayload() {
			return payload;
		}

		public void setPayload(final String payload) {
			this.payload = payload;
		}

		@Override
		public boolean equals(final Object obj) {
			if (!(obj instanceof Foo)) {
				return false;
			}

			Foo rhs = (Foo) obj;
			return new EqualsBuilder()
					.append(uidPk, rhs.uidPk)
					.append(guid, rhs.guid)
					.append(payload, rhs.payload)
					.isEquals();
		}

		@Override
		public int hashCode() {
			return new HashCodeBuilder()
					.append(uidPk)
					.append(guid)
					.append(payload)
					.toHashCode();
		}

		@Override
		public String toString() {
			return new ToStringBuilder(this)
					.append(UIDPK, uidPk)
					.append(GUID, guid)
					.toString();
		}
	}
}
