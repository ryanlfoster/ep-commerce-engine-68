package com.elasticpath.importexport.exporter.exporters.impl;

/**
 * The Catalog exporter has a dependent exporter which does it work based on "Attribute.class". However, CustomerProfiles have attributes too, which
 * are not part of the catalog, so when a Customer exporter wants to hint at its needs to the CustomerProfileAttributeExporter, it uses this class to
 * differentiate itself from the work the catalog exporter will do.
 */
public interface CustomerProfileAttribute {

}

