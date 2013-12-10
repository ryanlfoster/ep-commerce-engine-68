package com.elasticpath.importexport.exporter.exporters.impl;
/**
 * Associations between {@link com.elasticpath.domain.store.Store}s are not captured as separate classes; they are simply registered 
 * as a list of associated UIDs. This class is used to register a Store Association dependency in the importexport tool so 
 * that Store Associations may be exported with Stores.
 */
public interface StoreAssociation {
}