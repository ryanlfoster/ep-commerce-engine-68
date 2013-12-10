/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.commons.util;

/**
 * A password generator can generate random passwords.
 */
public interface PasswordGenerator {

  /**
   * Return a random password with the default length.
   * @return a random password with default length.
   */
  String getPassword();

  /**
   * Sets the minimum password length.
   * @param minimumPasswordLength the minimum password length
   */
  void setMinimumPasswordLength(final Integer minimumPasswordLength);
  
}