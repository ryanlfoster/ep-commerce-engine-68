package com.elasticpath.web.ajax.dwrconverter;



import com.elasticpath.commons.exception.EpLicensingCorruptedException;
import com.elasticpath.commons.exception.EpLicensingExpiredException;
import com.elasticpath.commons.exception.EpLicensingInvalidException;
import com.elasticpath.commons.exception.EpLicensingMissingException;
import com.elasticpath.commons.util.AssetRepository;


/**
 * <code>EpCurrencyConverter</code> is a customized converter to convert java.util.Currency object to/from java.lang.String.
 */
public class EpCurrencyConverter {

	private AssetRepository assetRepository;
	
	

	/**
	 * Reads the license and check it against specified configuration.
	 *
	 * @param aForceLicenseRead forces LicenseReader to re-read license
	 */
	@SuppressWarnings("PMD.UncommentedEmptyMethod")
	public void checkLicense(final boolean aForceLicenseRead) {
		
	}

	/**
	 * Reads the license and check it against specified configuration.
	 *
	 * @throws EpLicensingCorruptedException if no license is corrupted
	 * @throws EpLicensingExpiredException if the given license is expired
	 * @throws EpLicensingMissingException if no license is found
	 * @throws EpLicensingInvalidException if the given license is invalid
	 */
	@SuppressWarnings("PMD.UncommentedEmptyMethod")
	public synchronized void checkLicense() throws EpLicensingInvalidException, EpLicensingMissingException, EpLicensingCorruptedException, //NOPMD
			EpLicensingExpiredException { 
		
	}

	/**
	 * @return the assetRepository
	 */
	public AssetRepository getAssetRepository() {
		return assetRepository;
	}

	/**
	 * @param assetRepository the assetRepository to set
	 */
	public void setAssetRepository(final AssetRepository assetRepository) {
		this.assetRepository = assetRepository;
	}
}