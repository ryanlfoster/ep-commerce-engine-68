
package com.quova.webservices.ondemand.geopoint.v1;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.quova.webservices.ondemand.geopoint.v1 package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _IpInfo_QNAME = new QName("https://webservices.quova.com/OnDemand/GeoPoint/v1/", "IpInfo");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.quova.webservices.ondemand.geopoint.v1
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link City }
     * 
     */
    public City createCity() {
        return new City();
    }

    /**
     * Create an instance of {@link DMA }
     * 
     */
    public DMA createDMA() {
        return new DMA();
    }

    /**
     * Create an instance of {@link MSA }
     * 
     */
    public MSA createMSA() {
        return new MSA();
    }

    /**
     * Create an instance of {@link Continent }
     * 
     */
    public Continent createContinent() {
        return new Continent();
    }

    /**
     * Create an instance of {@link Region }
     * 
     */
    public Region createRegion() {
        return new Region();
    }

    /**
     * Create an instance of {@link Coordinates }
     * 
     */
    public Coordinates createCoordinates() {
        return new Coordinates();
    }

    /**
     * Create an instance of {@link GetIpInfoResponse }
     * 
     */
    public GetIpInfoResponse createGetIpInfoResponse() {
        return new GetIpInfoResponse();
    }

    /**
     * Create an instance of {@link IpInfo }
     * 
     */
    public IpInfo createIpInfo() {
        return new IpInfo();
    }

    /**
     * Create an instance of {@link Country }
     * 
     */
    public Country createCountry() {
        return new Country();
    }

    /**
     * Create an instance of {@link State }
     * 
     */
    public State createState() {
        return new State();
    }

    /**
     * Create an instance of {@link Response }
     * 
     */
    public Response createResponse() {
        return new Response();
    }

    /**
     * Create an instance of {@link GetIpInfo }
     * 
     */
    public GetIpInfo createGetIpInfo() {
        return new GetIpInfo();
    }

    /**
     * Create an instance of {@link Domain }
     * 
     */
    public Domain createDomain() {
        return new Domain();
    }

    /**
     * Create an instance of {@link Location }
     * 
     */
    public Location createLocation() {
        return new Location();
    }

    /**
     * Create an instance of {@link Network }
     * 
     */
    public Network createNetwork() {
        return new Network();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link IpInfo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "https://webservices.quova.com/OnDemand/GeoPoint/v1/", name = "IpInfo")
    public JAXBElement<IpInfo> createIpInfo(IpInfo value) {
        return new JAXBElement<IpInfo>(_IpInfo_QNAME, IpInfo.class, null, value);
    }

}
