package com.elasticpath.importexport.example;

/**
 * Extension of {@link ExamplePersistence} for extensibility testing.
 */
public class ExamplePersistenceExt extends ExamplePersistence {
	private static final long serialVersionUID = 1L;
	private String code;

	public String getCode() {
		return code;
	}

	public void setCode(final String code) {
		this.code = code;
	}
}