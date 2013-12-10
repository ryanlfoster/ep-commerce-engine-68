
package com.quova.webservices.ondemand.geopoint.v1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Network complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Network">
 *   &lt;complexContent>
 *     &lt;extension base="{https://webservices.quova.com/OnDemand/GeoPoint/v1/}IPBase">
 *       &lt;sequence>
 *         &lt;element name="Domain" type="{https://webservices.quova.com/OnDemand/GeoPoint/v1/}Domain" minOccurs="0"/>
 *         &lt;element name="AOL" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Carrier" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ASN" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Connection" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="LineSpeed" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="IPRoutingType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Network", propOrder = {
    "domain",
    "aol",
    "carrier",
    "asn",
    "connection",
    "lineSpeed",
    "ipRoutingType"
})
public class Network
    extends IPBase
{

    @XmlElement(name = "Domain")
    protected Domain domain;
    @XmlElement(name = "AOL")
    protected String aol;
    @XmlElement(name = "Carrier")
    protected String carrier;
    @XmlElement(name = "ASN")
    protected String asn;
    @XmlElement(name = "Connection")
    protected String connection;
    @XmlElement(name = "LineSpeed")
    protected String lineSpeed;
    @XmlElement(name = "IPRoutingType")
    protected String ipRoutingType;

    /**
     * Gets the value of the domain property.
     * 
     * @return
     *     possible object is
     *     {@link Domain }
     *     
     */
    public Domain getDomain() {
        return domain;
    }

    /**
     * Sets the value of the domain property.
     * 
     * @param value
     *     allowed object is
     *     {@link Domain }
     *     
     */
    public void setDomain(Domain value) {
        this.domain = value;
    }

    /**
     * Gets the value of the aol property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAOL() {
        return aol;
    }

    /**
     * Sets the value of the aol property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAOL(String value) {
        this.aol = value;
    }

    /**
     * Gets the value of the carrier property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCarrier() {
        return carrier;
    }

    /**
     * Sets the value of the carrier property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCarrier(String value) {
        this.carrier = value;
    }

    /**
     * Gets the value of the asn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getASN() {
        return asn;
    }

    /**
     * Sets the value of the asn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setASN(String value) {
        this.asn = value;
    }

    /**
     * Gets the value of the connection property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getConnection() {
        return connection;
    }

    /**
     * Sets the value of the connection property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setConnection(String value) {
        this.connection = value;
    }

    /**
     * Gets the value of the lineSpeed property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLineSpeed() {
        return lineSpeed;
    }

    /**
     * Sets the value of the lineSpeed property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLineSpeed(String value) {
        this.lineSpeed = value;
    }

    /**
     * Gets the value of the ipRoutingType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIPRoutingType() {
        return ipRoutingType;
    }

    /**
     * Sets the value of the ipRoutingType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIPRoutingType(String value) {
        this.ipRoutingType = value;
    }

}
