package com.elasticpath.inventory;


/**
 * The public interface for {@link InventoryCommand} objects. Note that there is no execute() 
 * method because this capability is private to the inventory subsystem. 
 */
public interface InventoryCommand {

	/**
	 * @return the result of the execution of this command
	 */
	InventoryExecutionResult getExecutionResult();
	
}
