
package com.quova.webservices.ondemand.geopoint.v1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for IpInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="IpInfo">
 *   &lt;complexContent>
 *     &lt;extension base="{https://webservices.quova.com/OnDemand/GeoPoint/v1/}IPBase">
 *       &lt;sequence>
 *         &lt;element name="Response" type="{https://webservices.quova.com/OnDemand/GeoPoint/v1/}Response" minOccurs="0"/>
 *         &lt;element name="IPAddress" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="IPType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="AnonymizerStatus" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Network" type="{https://webservices.quova.com/OnDemand/GeoPoint/v1/}Network" minOccurs="0"/>
 *         &lt;element name="Location" type="{https://webservices.quova.com/OnDemand/GeoPoint/v1/}Location" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IpInfo", propOrder = {
    "response",
    "ipAddress",
    "ipType",
    "anonymizerStatus",
    "network",
    "location"
})
public class IpInfo
    extends IPBase
{

    @XmlElement(name = "Response")
    protected Response response;
    @XmlElement(name = "IPAddress")
    protected String ipAddress;
    @XmlElement(name = "IPType")
    protected String ipType;
    @XmlElement(name = "AnonymizerStatus")
    protected String anonymizerStatus;
    @XmlElement(name = "Network")
    protected Network network;
    @XmlElement(name = "Location")
    protected Location location;

    /**
     * Gets the value of the response property.
     * 
     * @return
     *     possible object is
     *     {@link Response }
     *     
     */
    public Response getResponse() {
        return response;
    }

    /**
     * Sets the value of the response property.
     * 
     * @param value
     *     allowed object is
     *     {@link Response }
     *     
     */
    public void setResponse(Response value) {
        this.response = value;
    }

    /**
     * Gets the value of the ipAddress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIPAddress() {
        return ipAddress;
    }

    /**
     * Sets the value of the ipAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIPAddress(String value) {
        this.ipAddress = value;
    }

    /**
     * Gets the value of the ipType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIPType() {
        return ipType;
    }

    /**
     * Sets the value of the ipType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIPType(String value) {
        this.ipType = value;
    }

    /**
     * Gets the value of the anonymizerStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAnonymizerStatus() {
        return anonymizerStatus;
    }

    /**
     * Sets the value of the anonymizerStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAnonymizerStatus(String value) {
        this.anonymizerStatus = value;
    }

    /**
     * Gets the value of the network property.
     * 
     * @return
     *     possible object is
     *     {@link Network }
     *     
     */
    public Network getNetwork() {
        return network;
    }

    /**
     * Sets the value of the network property.
     * 
     * @param value
     *     allowed object is
     *     {@link Network }
     *     
     */
    public void setNetwork(Network value) {
        this.network = value;
    }

    /**
     * Gets the value of the location property.
     * 
     * @return
     *     possible object is
     *     {@link Location }
     *     
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Sets the value of the location property.
     * 
     * @param value
     *     allowed object is
     *     {@link Location }
     *     
     */
    public void setLocation(Location value) {
        this.location = value;
    }

}
