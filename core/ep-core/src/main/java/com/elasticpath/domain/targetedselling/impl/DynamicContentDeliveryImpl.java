package com.elasticpath.domain.targetedselling.impl;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import org.apache.commons.collections.CollectionUtils;
import org.apache.openjpa.persistence.DataCache;
import org.apache.openjpa.persistence.Dependent;
import org.apache.openjpa.persistence.jdbc.ForeignKey;
import org.apache.openjpa.persistence.jdbc.Unique;

import com.elasticpath.domain.contentspace.ContentSpace;
import com.elasticpath.domain.contentspace.DynamicContent;
import com.elasticpath.domain.contentspace.impl.ContentSpaceImpl;
import com.elasticpath.domain.contentspace.impl.DynamicContentImpl;
import com.elasticpath.domain.sellingcontext.SellingContext;
import com.elasticpath.domain.sellingcontext.impl.SellingContextImpl;
import com.elasticpath.domain.targetedselling.DynamicContentDelivery;
import com.elasticpath.persistence.api.AbstractEntityImpl;

/**
 * <p>Defines all of the {@link ContentSpace}s within which a particular piece of 
 * {@link DynamicContent} could appear.
 * Also, defines the SellingContext's guid in which the DynamicContent may appear.
 * In the case where more than one {@code DynamicContentDelivery}'s selling context applies
 * to a given {@link ContentSpace}, the priority of the {@code DynamicContentDelivery} 
 * can be evaluated to select a single winner.</p> 
 * 
 * <p>This implementation specifies JPA persistence annotations.</p>
 */
@Entity
@Table(name = DynamicContentDeliveryImpl.TABLE_NAME)
@DataCache(enabled = false)
public class DynamicContentDeliveryImpl extends AbstractEntityImpl implements DynamicContentDelivery {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 20090112L;

	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TCSDYNAMICCONTENTDELIVERY";

	/**
	 * The name of the table that joins many-to-many for <code>ContentSpace</code>.
	 */
	private static final String JOIN_TABLE_NAME_CONTENTSPACES = "TCSDYNAMICCONTENTSPACE";

	private static final String CSDC_CONTENT_UID = "CSDC_CONTENT_UID";

	private static final String SELLING_CONTEXT_GUID = "SELLING_CONTEXT_GUID";

	private static final String DC_DELIVERY_UID = "DC_DELIVERY_UID";

	private static final String DC_CONTENTSPACE_UID = "DC_CONTENTSPACE_UID";

	private static final String GUID = "GUID";

	private long uidPk;

	private String name;

	private String description;

	private DynamicContent dynamicContent;

	private int priority;

	private Set<ContentSpace> contentspaces = new HashSet<ContentSpace>();

	private SellingContext sellingContext;

	private String guid;

	@Override
	@OneToOne(targetEntity = SellingContextImpl.class)
	@JoinColumn(name = SELLING_CONTEXT_GUID, referencedColumnName = "GUID")
	@ForeignKey
	@Dependent
	public SellingContext getSellingContext() {
		return sellingContext;
	}

	@Override
	public void setSellingContext(final SellingContext sellingContext) {
		this.sellingContext = sellingContext;
	}

	/**
	 * Gets the unique identifier for this domain model object.
	 * 
	 * @return the unique identifier.
	 */
	@Id
	@Column(name = "UIDPK")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
	@TableGenerator(name = TABLE_NAME, table = "JPA_GENERATED_KEYS", pkColumnName = "ID", valueColumnName = "LAST_VALUE", pkColumnValue = TABLE_NAME)
	public long getUidPk() {
		return this.uidPk;
	}

	/**
	 * Get the priority of delivery.
	 * 
	 * @return priority
	 */
	@Basic
	@Column(name = "PRIORITY")
	public int getPriority() {
		return priority;
	}

	@Override
	@ManyToOne(targetEntity = DynamicContentImpl.class, cascade = { CascadeType.REFRESH, CascadeType.MERGE }, fetch = FetchType.EAGER)
	@JoinColumn(name = CSDC_CONTENT_UID, nullable = false)
	@ForeignKey(name = "TCSDCA_UNIQUE")
	public DynamicContent getDynamicContent() {
		return dynamicContent;
	}

	/**
	 * Sets the unique identifier for this domain model object.
	 * 
	 * @param uidPk the new unique identifier.
	 */
	public void setUidPk(final long uidPk) {
		this.uidPk = uidPk;
	}

	@Override
	public void setDynamicContent(final DynamicContent dynamicContent) {
		this.dynamicContent = dynamicContent;
	}

	/**
	 * Set the priority of delivery.
	 * 
	 * @param priority of delivery.
	 */
	public void setPriority(final int priority) {
		this.priority = priority;
	}

	@Override
	@ManyToMany(targetEntity = ContentSpaceImpl.class, cascade = {
			CascadeType.MERGE, CascadeType.REFRESH }, fetch = FetchType.EAGER)
	@JoinTable(
			name = JOIN_TABLE_NAME_CONTENTSPACES, 
			joinColumns        = { @JoinColumn(name = DC_DELIVERY_UID) }, 
			inverseJoinColumns = { @JoinColumn(name = DC_CONTENTSPACE_UID) })
	public Set<ContentSpace> getContentspaces() {
		return contentspaces;
	}

	@Override
	public void setContentspaces(final Set<ContentSpace> contentspaces) {
		this.contentspaces = contentspaces;
	}

	@Override
	@Basic
	@Column(name = "NAME")
	public String getName() {
		return this.name;
	}

	@Override
	public void setName(final String name) {
		this.name = name;
	}

	@Override
	@Basic
	@Column(name = "DESCRIPTION")
	public String getDescription() {
		return this.description;
	}

	@Override
	public void setDescription(final String description) {
		this.description = description;
	}

	/**
	 * @return the guid.
	 */
	@Override
	@Basic
	@Column(name = GUID)
	@Unique(name = "TCSDCA_UNIQUE")
	public String getGuid() {
		return guid;
	}

	/**
	 * Set the guid.
	 * 
	 * @param guid the guid to set.
	 */
	@Override
	public void setGuid(final String guid) {
		this.guid = guid;
	}

	@Override
	public String toString() {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append(this.getClass().getName());
		stringBuffer.append("\n[\nuidpk:");
		stringBuffer.append(getUidPk());
		stringBuffer.append("\nname:");
		stringBuffer.append(getName());
		stringBuffer.append("\ndescription:");
		stringBuffer.append(getDescription());
		stringBuffer.append("\n==========\ndynamicContent:");
		stringBuffer.append(getDynamicContent());
		stringBuffer.append("\nDelivery target(content space)");
		if (CollectionUtils.isNotEmpty(getContentspaces())) {
			for (ContentSpace contentspace : getContentspaces()) {
				stringBuffer.append("\n\t cs name:");
				stringBuffer.append(contentspace.getTargetId());
			}
		}
		stringBuffer.append("\n] guid = [");
		stringBuffer.append(getGuid());
		stringBuffer.append("\n] Selling context = [");
		stringBuffer.append(getSellingContext());
		stringBuffer.append(']');

		return stringBuffer.toString();
	}

	/**
	 * @return hash code
	 */
    @Override
    @SuppressWarnings("PMD.UselessOverridingMethod")
	public int hashCode() {
		return super.hashCode();
	}

	/**
	 * Compares to objects.
	 * 
	 * @param obj an object to compare
	 * @return true if objects are equal
	 */
	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof DynamicContentDeliveryImpl)) {
			return false;
		}
		return super.equals(obj);
	}

	@Override
	@Transient
	public String getSellingContextGuid() {
		if (getSellingContext() == null) {
			return null;
		}
		return getSellingContext().getGuid();
	}
}