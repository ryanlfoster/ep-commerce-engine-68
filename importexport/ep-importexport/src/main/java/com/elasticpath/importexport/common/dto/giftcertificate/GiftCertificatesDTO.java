package com.elasticpath.importexport.common.dto.giftcertificate;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.elasticpath.common.dto.giftcertificate.GiftCertificateDTO;

/**
 * Wrapper JAXB entity for schema generation to collect a group of StoreDTOs.
 */
@XmlRootElement(name = "gift_certificates")
@XmlType(name = "gift_certificatesDTO", propOrder = { })
@XmlAccessorType(XmlAccessType.NONE)
public class GiftCertificatesDTO {

	@XmlElement(name = "gift_certificate")
	private final List<GiftCertificateDTO> giftCertificates = new ArrayList<GiftCertificateDTO>();

	public List<GiftCertificateDTO> getGiftCertificates() {
		return giftCertificates;
	}

}
