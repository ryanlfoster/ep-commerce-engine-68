package com.elasticpath.domain.misc;
/**
 *	Payer Authentication Validation Response Value. 
 */
public interface PayerAuthValidationValue {
	/**
	 * Gets the PaRES value.
	 * @return String PaRES value.
	 */
	String getPaRES();
	
	/**
	 * Sets PaRES value.
	 * @param pARES value from response.
	 */
	void setPaRES(final String pARES);
	
	/**
	 * Whether the account is validated or not.
	 * @return boolean the validation flag.
	 */
	boolean isValidated();
	
	/**
	 * Sets the validated boolean value.
	 * @param validated boolean value.
	 */
	void setValidated(final boolean validated);
	
	/**
	 * Gets XID.
	 * @return String XID value.
	 */
	String getXID();
	
	/**
	 * Sets XID value.
	 * @param xID the XID value
	 */
	void setXID(final String xID);
	
	/**
	 * Gets CAVV.
	 * @return String CAVV value
	 */
	String getCAVV();
	
	/**
	 * Sets CAVV value.
	 * @param cAVV the CAVV value.
	 */
	void setCAVV(final String cAVV);
	
	/**
	 * Gets AAV.
	 * @return String AAV value
	 */
	String getAAV();
	
	/**
	 * Sets AAV value.
	 * @param aAV the aAV value.
	 */
	void setAAV(final String aAV);
	
	/**
	 * Gets commerceIndicator.
	 * @return String commerceIndicator value
	 */
	String getCommerceIndicator();
	
	/**
	 * Sets CommerceIndicator value.
	 * @param commerceIndicator the commerceIndicator value.
	 */
	void setCommerceIndicator(final String commerceIndicator);
	
	/**
	 * Gets ECI.
	 * @return String ECI value
	 */
	String getECI();
	
	/**
	 * Sets ECI value.
	 * @param eCI the ECI value.
	 */
	void setECI(final String eCI);
	
	/**
	 * Gets ucafCollectionIndicator.
	 * @return String ucafCollectionIndicator value
	 */
	String getUcafCollectionIndicator();
	
	/**
	 * Sets UcafCollectionIndicator value.
	 * @param ucafCollectionIndicator the UcafCollectionIndicator value
	 */
	void setUcafCollectionIndicator(final String ucafCollectionIndicator);
}
