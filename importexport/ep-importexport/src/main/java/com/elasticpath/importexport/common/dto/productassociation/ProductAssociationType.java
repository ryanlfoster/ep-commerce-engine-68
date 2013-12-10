package com.elasticpath.importexport.common.dto.productassociation;

import com.elasticpath.commons.exception.EpIntBindException;
import com.elasticpath.domain.catalog.ProductAssociation;

/**
 * The ProductAssociationType.
 */
public enum ProductAssociationType {

	/** Cross-sell association. */
	CrossSell {
		@Override
		public int typeId() {		
			return ProductAssociation.CROSS_SELL;
		}		
	},

	/** Up-sell association. */
	UpSell {
		@Override
		public int typeId() {		
			return ProductAssociation.UP_SELL;
		}
	},

	/** Warranty item. */
	Warranty {
		@Override
		public int typeId() {		
			return ProductAssociation.WARRANTY;
		}
	},

	/** Accessory product association. */
	Accessory {
		@Override
		public int typeId() {		
			return ProductAssociation.ACCESSORY;
		}
	}, 

	/** Replacement product association. */
	Replacement {
		@Override
		public int typeId() {		
			return ProductAssociation.REPLACEMENT;
		}	
	},

	/** Computed product recommendation association. */
	Recommendation {
		@Override
		public int typeId() {		
			return ProductAssociation.RECOMMENDATION;
		}
	};
	
	/**
	 * Gets ProductAssocaiation type ID.
	 *
	 * @return int
	 */
	public abstract int typeId();

	/**
	 * Converts typeId to ProductAssociationType.
	 *
	 * @param typeId the tyepId
	 * @return ProductAssociationType
	 */
	public static ProductAssociationType valueOf(final int typeId) {
		for (ProductAssociationType productAssociationType : values()) {
			if (productAssociationType.typeId() == typeId) {
				return productAssociationType;
			}
		}
		throw new EpIntBindException("Invalid association type index: " + typeId);
	}	
}
