package com.elasticpath.tags.domain.impl;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.apache.openjpa.persistence.DataCache;
import org.apache.openjpa.persistence.jdbc.Unique;

import com.elasticpath.persistence.api.AbstractEntityImpl;
import com.elasticpath.tags.domain.TagDefinition;
import com.elasticpath.tags.domain.TagDictionary;

/**
 * {@link TagDictionary} implementation.
 */
@Entity
@Table(name = TagDictionaryImpl.TABLE_NAME)
@DataCache(enabled = false)
public class TagDictionaryImpl extends AbstractEntityImpl implements TagDictionary {
    /**
     * Database Table.
     */
    public static final String TABLE_NAME = "TTAGDICTIONARY";

    /**
     * Serial version id.
     */
    private static final long serialVersionUID = 5000000001L;

    private long uidPk;
    private String name;
    private String purpose;
    private Set<TagDefinition> tagDefinitions;

	private String guid;

    /**
     * Constructs and initialize the Tag Dictionary.
     */
    public TagDictionaryImpl() {
        tagDefinitions = new HashSet<TagDefinition>();
    }

    /**
     * @return the name
     */
    @Basic
    @Column(name = "NAME")
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param name a name to be set
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * @return the purpose description
     */
    @Basic
    @Column(name = "PURPOSE")
    public String getPurpose() {
        return purpose;
    }

    /**
     * Sets the purpose.
     *
     * @param purpose a purpose description to be set
     */
    public void setPurpose(final String purpose) {
        this.purpose = purpose;
    }

    /**
     * @return a set of {@link TagDefinition}
     */
    @ManyToMany(targetEntity = TagDefinitionImpl.class, fetch = FetchType.EAGER)
    @JoinTable(
      name = "TTAGDICTIONARYTAGDEFINITION",
      joinColumns = { @JoinColumn(name = "TAGDICTIONARY_GUID", referencedColumnName = "GUID") },
      inverseJoinColumns = { @JoinColumn(name = "TAGDEFINITION_GUID", referencedColumnName = "GUID") }
    )
    public Set<TagDefinition> getTagDefinitions() {
        return this.tagDefinitions;
    }

    /**
     * Sets a list of {@link TagDefinition}.
     *
     * @param tagDefinitions a set of {@link TagDefinition}
     */
    protected void setTagDefinitions(final Set<TagDefinition> tagDefinitions) {
        this.tagDefinitions = tagDefinitions;
    }

    /**
     * Adds a {@link TagDefinition}.
     *
     * @param tagDefinition a tag definition to be added
     */
    public void addTagDefinition(final TagDefinition tagDefinition) {
        this.tagDefinitions.add(tagDefinition);
    }

    /**
     * Removes the {@link TagDefinition}.
     *
     * @param tagDefinition a tag definition to be removed
     */
    public void removeTagDefinition(final TagDefinition tagDefinition) {
        this.tagDefinitions.remove(tagDefinition);
    }

    /**
     * @return the guid.
     */
    @Override
    @Basic
    @Column(name = "GUID")
    @Unique(name = "TAGDICTIONARY_UNIQUE")
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
    @Id
    @Column(name = "UIDPK")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
    @TableGenerator(name = TABLE_NAME, table = "JPA_GENERATED_KEYS", pkColumnName = "ID", valueColumnName = "LAST_VALUE", pkColumnValue = TABLE_NAME)
    public long getUidPk() {
        return this.uidPk;
    }

    @Override
    public void setUidPk(final long uidPk) {
        this.uidPk = uidPk;
    }

    /**
     * Generate the hash code.
     *
     * @return the hash code.
     */
    @Override
    public int hashCode() {
        if (getGuid() == null) {
            return 0;
        }

        return getGuid().hashCode();
    }

    /**
     * Determines whether the given object is equal to this 'TagDictionary'.
     *
     * @param obj to be compared for equality
     * @return true if the given object's GUID is equal to this one's GUID
     */
    @Override
    public boolean equals(final Object obj) {
        return obj instanceof TagDictionary && this.getGuid().equals(((TagDictionary) obj).getGuid());

    }

    /**
     * @return the string representation of the {@link com.elasticpath.tags.domain.TagDictionary}
     */
    @Override
    public String toString() {
        return "[TagDictionary: "
                + "GUID=" + this.getGuid()
                + "Name=" + this.getName()
                + "Purpose=" + this.getPurpose()
                + "]";
    }

}
