
package com.quova.webservices.ondemand.geopoint.v1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for IPBase complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="IPBase">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IPBase")
@XmlSeeAlso({
    IpInfo.class,
    Domain.class,
    Location.class,
    MSA.class,
    Country.class,
    City.class,
    Coordinates.class,
    Region.class,
    State.class,
    Network.class,
    DMA.class,
    Continent.class
})
public abstract class IPBase {


}
