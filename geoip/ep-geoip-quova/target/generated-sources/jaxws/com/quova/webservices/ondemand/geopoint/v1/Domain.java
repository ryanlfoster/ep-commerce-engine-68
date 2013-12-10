
package com.quova.webservices.ondemand.geopoint.v1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Domain complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Domain">
 *   &lt;complexContent>
 *     &lt;extension base="{https://webservices.quova.com/OnDemand/GeoPoint/v1/}IPBase">
 *       &lt;sequence>
 *         &lt;element name="TopLevel" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SecondLevel" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Domain", propOrder = {
    "topLevel",
    "secondLevel"
})
public class Domain
    extends IPBase
{

    @XmlElement(name = "TopLevel")
    protected String topLevel;
    @XmlElement(name = "SecondLevel")
    protected String secondLevel;

    /**
     * Gets the value of the topLevel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTopLevel() {
        return topLevel;
    }

    /**
     * Sets the value of the topLevel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTopLevel(String value) {
        this.topLevel = value;
    }

    /**
     * Gets the value of the secondLevel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSecondLevel() {
        return secondLevel;
    }

    /**
     * Sets the value of the secondLevel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSecondLevel(String value) {
        this.secondLevel = value;
    }

}
