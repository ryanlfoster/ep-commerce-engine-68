package com.elasticpath.service.command.impl;

import java.util.Map;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.command.CommandResult;
import com.elasticpath.service.command.UpdateStoreCommand;
import com.elasticpath.service.command.UpdateStoreCommandResult;
import com.elasticpath.service.impl.AbstractEpServiceImpl;
import com.elasticpath.settings.SettingsService;
import com.elasticpath.service.store.StoreService;
import com.elasticpath.settings.domain.SettingValue;

/**
 * Updates store using core services.
 */
public class UpdateStoreCommandImpl extends AbstractEpServiceImpl implements UpdateStoreCommand {

	private static final long serialVersionUID = 1L;

	private Store store;

	private Map<String, String> settingValueMap;

	private final UpdateStoreCommandResult updateStoreCommandResult;

	private transient StoreService storeService;

	private transient SettingsService settingsService;

	/**
	 * Initializes command result.
	 */
	public UpdateStoreCommandImpl() {
		updateStoreCommandResult = getBean(ContextIdNames.UPDATE_STORE_COMMAND_RESULT);
	}

	/**
	 * Updates the store and setting values.
	 * 
	 * @return command result
	 */
	public CommandResult execute() {
		updateStoreCommandResult.setStore(getStoreService().saveOrUpdate(store));
		final String storeCode = store.getCode();
		for (final String key : settingValueMap.keySet()) {
			final SettingValue settingValue = getSettingsService().getSettingValue(key, storeCode);
			settingValue.setValue(settingValueMap.get(key));
			getSettingsService().updateSettingValue(settingValue);
		}
		return updateStoreCommandResult;
	}

	@Override
	public void setStore(final Store store) {
		this.store = store;
	}

	@Override
	public void setSettingValues(final Map<String, String> settingValueMap) {
		this.settingValueMap = settingValueMap;
	}

	/**
	 * Gets store service.
	 * 
	 * @return store service
	 */
	StoreService getStoreService() {
		if (storeService == null) {
			storeService = getBean(ContextIdNames.STORE_SERVICE);
		}
		return storeService;
	}

	/**
	 * Gets settings service.
	 * 
	 * @return settings service
	 */
	SettingsService getSettingsService() {
		if (settingsService == null) {
			settingsService = getBean(ContextIdNames.SETTINGS_SERVICE);
		}
		return settingsService;
	}
}
