
package com.quova.webservices.ondemand.geopoint.v1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Location complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Location">
 *   &lt;complexContent>
 *     &lt;extension base="{https://webservices.quova.com/OnDemand/GeoPoint/v1/}IPBase">
 *       &lt;sequence>
 *         &lt;element name="Continent" type="{https://webservices.quova.com/OnDemand/GeoPoint/v1/}Continent" minOccurs="0"/>
 *         &lt;element name="Region" type="{https://webservices.quova.com/OnDemand/GeoPoint/v1/}Region" minOccurs="0"/>
 *         &lt;element name="Country" type="{https://webservices.quova.com/OnDemand/GeoPoint/v1/}Country" minOccurs="0"/>
 *         &lt;element name="State" type="{https://webservices.quova.com/OnDemand/GeoPoint/v1/}State" minOccurs="0"/>
 *         &lt;element name="DMA" type="{https://webservices.quova.com/OnDemand/GeoPoint/v1/}DMA" minOccurs="0"/>
 *         &lt;element name="MSA" type="{https://webservices.quova.com/OnDemand/GeoPoint/v1/}MSA" minOccurs="0"/>
 *         &lt;element name="City" type="{https://webservices.quova.com/OnDemand/GeoPoint/v1/}City" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Location", propOrder = {
    "continent",
    "region",
    "country",
    "state",
    "dma",
    "msa",
    "city"
})
public class Location
    extends IPBase
{

    @XmlElement(name = "Continent")
    protected Continent continent;
    @XmlElement(name = "Region")
    protected Region region;
    @XmlElement(name = "Country")
    protected Country country;
    @XmlElement(name = "State")
    protected State state;
    @XmlElement(name = "DMA")
    protected DMA dma;
    @XmlElement(name = "MSA")
    protected MSA msa;
    @XmlElement(name = "City")
    protected City city;

    /**
     * Gets the value of the continent property.
     * 
     * @return
     *     possible object is
     *     {@link Continent }
     *     
     */
    public Continent getContinent() {
        return continent;
    }

    /**
     * Sets the value of the continent property.
     * 
     * @param value
     *     allowed object is
     *     {@link Continent }
     *     
     */
    public void setContinent(Continent value) {
        this.continent = value;
    }

    /**
     * Gets the value of the region property.
     * 
     * @return
     *     possible object is
     *     {@link Region }
     *     
     */
    public Region getRegion() {
        return region;
    }

    /**
     * Sets the value of the region property.
     * 
     * @param value
     *     allowed object is
     *     {@link Region }
     *     
     */
    public void setRegion(Region value) {
        this.region = value;
    }

    /**
     * Gets the value of the country property.
     * 
     * @return
     *     possible object is
     *     {@link Country }
     *     
     */
    public Country getCountry() {
        return country;
    }

    /**
     * Sets the value of the country property.
     * 
     * @param value
     *     allowed object is
     *     {@link Country }
     *     
     */
    public void setCountry(Country value) {
        this.country = value;
    }

    /**
     * Gets the value of the state property.
     * 
     * @return
     *     possible object is
     *     {@link State }
     *     
     */
    public State getState() {
        return state;
    }

    /**
     * Sets the value of the state property.
     * 
     * @param value
     *     allowed object is
     *     {@link State }
     *     
     */
    public void setState(State value) {
        this.state = value;
    }

    /**
     * Gets the value of the dma property.
     * 
     * @return
     *     possible object is
     *     {@link DMA }
     *     
     */
    public DMA getDMA() {
        return dma;
    }

    /**
     * Sets the value of the dma property.
     * 
     * @param value
     *     allowed object is
     *     {@link DMA }
     *     
     */
    public void setDMA(DMA value) {
        this.dma = value;
    }

    /**
     * Gets the value of the msa property.
     * 
     * @return
     *     possible object is
     *     {@link MSA }
     *     
     */
    public MSA getMSA() {
        return msa;
    }

    /**
     * Sets the value of the msa property.
     * 
     * @param value
     *     allowed object is
     *     {@link MSA }
     *     
     */
    public void setMSA(MSA value) {
        this.msa = value;
    }

    /**
     * Gets the value of the city property.
     * 
     * @return
     *     possible object is
     *     {@link City }
     *     
     */
    public City getCity() {
        return city;
    }

    /**
     * Sets the value of the city property.
     * 
     * @param value
     *     allowed object is
     *     {@link City }
     *     
     */
    public void setCity(City value) {
        this.city = value;
    }

}
