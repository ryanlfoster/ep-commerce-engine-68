package com.elasticpath.tags.domain.impl;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.apache.openjpa.persistence.DataCache;
import org.apache.openjpa.persistence.FetchAttribute;
import org.apache.openjpa.persistence.FetchGroup;
import org.apache.openjpa.persistence.jdbc.Index;
import org.apache.openjpa.persistence.jdbc.Unique;

import com.elasticpath.persistence.api.AbstractEntityImpl;
import com.elasticpath.persistence.support.FetchGroupConstants;
import com.elasticpath.tags.domain.ConditionalExpression;


/**
 * {@link com.elasticpath.tags.domain.ConditionalExpression} implementation class.
 */
@Entity
@Table(name = ConditionalExpressionImpl.TABLE_NAME)
@DataCache(enabled = false)
@FetchGroup(name = FetchGroupConstants.PROMOTION_INDEX, attributes = { 
		@FetchAttribute(name = "conditionString"),
		@FetchAttribute(name = "tagDictionaryGuid")
		})
public class ConditionalExpressionImpl extends AbstractEntityImpl implements ConditionalExpression {
    /**
     * Database Table.
     */
    public static final String TABLE_NAME = "TTAGCONDITION";

    /**
     * Serial version id.
     */
    private static final long serialVersionUID = 5000000002L;

    private long uidPk;
    private String name;
    private String description;
    private String conditionString;
    private String tagDictionaryGuid;
    private boolean named;

	private String guid;
    
    /**
     * Is condition created explicitly.
     * @return true if condition was created explicitly. 
     */
    @Basic
    @Column(name = "NAMED")
    public boolean isNamed() {
		return named;
	}

    /**
     * Set explicit flag to condition. I.e. is condition created as named (true) or from ad-hoc (false). 
     * @param named explicit flag.
     */
	public void setNamed(final boolean named) {
		this.named = named;
	}

	/**
     * @return the name of the 'Condition'
     */
    @Basic
    @Column(name = "NAME")
    public String getName() {
        return name;
    }

    /**
     * Sets a name for the 'Condition'.
     *
     * @param name a name to be set
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * @return the description of the 'Condition'
     */
    @Basic
    @Column(name = "DESCRIPTION")
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the 'Condition'.
     *
     * @param description a description to be set
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * @return the condition string.
     */
    @Basic
    @Column(name = "CONDITION_STRING")
    public String getConditionString() {
        return conditionString;
    }

    /**
     * Sets the condition string of the 'Condition'.
     *
     * @param conditionString a condition to be set
     */
    public void setConditionString(final String conditionString) {
        this.conditionString = conditionString;
    }

    /**
     * @return the condition scope.
     */
    @Basic
    @Column(name = "TAGDICTIONARY_GUID")
    @Index(name = "I_TAGDICTIONARY_FK")
    public String getTagDictionaryGuid() {
        return tagDictionaryGuid;
    }

    /**
     * Sets the condition string of the 'Condition'.
     *
     * @param tagDictionaryGuid a tag dictionary to be set
     */
    public void setTagDictionaryGuid(final String tagDictionaryGuid) {
        this.tagDictionaryGuid = tagDictionaryGuid;
    }

    /**
     * @return the guid.
     */
    @Override
    @Basic
    @Column(name = "GUID")
    @Unique(name = "TAGCONDITION_UNIQUE")
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
     * Determines whether the given object is equal to this 'Condition'.
     *
     * @param obj to be compared for equality
     * @return true if the given object's GUID is equal to this one's GUID
     */
    @Override
    public boolean equals(final Object obj) {
        return obj instanceof ConditionalExpression && this.getGuid().equals(((ConditionalExpression) obj).getGuid());

    }

    /**
     * @return the string representation of the {@link com.elasticpath.tags.domain.ConditionalExpression}
     */
    @Override
    public String toString() {
        return "[Condition: "
                + "GUID=" + this.getGuid()
                + ", Name=" + this.getName()
                + ", Description=" + this.getDescription()
                + ", ConditionString=" + this.getConditionString()
                + ", TagDictionary=" + this.getTagDictionaryGuid()
                + ", Named=" + this.isNamed()
                + "]";
    }
}