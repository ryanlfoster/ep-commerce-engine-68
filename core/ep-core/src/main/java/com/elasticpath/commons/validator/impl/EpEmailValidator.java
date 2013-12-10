package com.elasticpath.commons.validator.impl;

import org.apache.commons.validator.EmailValidator;

/**
 * <code>EpEmailValidator</code> currently uses {@link EmailValidator}.
 * 
 * @deprecated whereever possible use directly the {@link EmailValidator} from the commons package
 */
@Deprecated
public class EpEmailValidator {
	
//	private static final int PRIMARY_DOMAIN_MINLENGTH = 3;
//	private static final int PRIMARY_DOMAIN_MAXLENGTH = 7;
	
	/**
     * Singleton instance of this class.
     */
    private static final EpEmailValidator EP_EMAIL_VALIDATOR = new EpEmailValidator();

    /**
     * Returns the Singleton instance of this validator.
     * @return singleton instance of this validator.
     */
    public static EpEmailValidator getInstance() {
        return EP_EMAIL_VALIDATOR;
    }

    /**
     * Protected constructor for subclasses to use.
     */
    protected EpEmailValidator() {
        super();
    }

	/**
     * <p>Checks if a field has a valid e-mail address.</p>
     *
     * @param email The value that validation is being performed on.  A <code>null</code>
     * value is considered invalid.
     * @return true if the email address is valid.
     */
    public boolean isValid(final String email) {
		return EmailValidator.getInstance().isValid(email);
	}
    
//    private boolean validateLocalPart(final String localPart) { 
//		/*
//		 * Valid charater for local-part (user name part) of email: 
//		 * Uppercase and lowercase letters
//		 * The digits 0 through 9
//		 * The characters, ! # $ % & ' * + - / = ? ^ _ ` { | } ~
//		 * The character "." provided that it is not the first or last character in the local-part.
//		 */
//		final String validUserChars = "a-zA-Z0-9\\-!#\\$%&\\'\\*\\+\\/=\\?\\^_`\\{\\|\\}~";
//		final String quotedUser = "(\"[^\"]*\")";
//		final String validUser = "(^([" + validUserChars + "]+)(\\.[" + validUserChars + "]+)*$|" + quotedUser + ")";
//		final Pattern userPat = Pattern.compile(validUser);
//			
//		final Matcher userMat = userPat.matcher(localPart);
//		if (!userMat.matches()) {
//			return false;
//		}
//		return true;
//	}
//	
//	private static boolean validateDomainPart(final String domainPart) { 
//		final Pattern ipDomainPat = Pattern.compile("^(\\d+)[\\.](\\d+)[\\.](\\d+)[\\.](\\d+)$");
//		final String validDomainChar = "[a-zA-Z\\d\\-]+";
//		final Pattern domainPat = Pattern.compile("^(" + validDomainChar + ")(\\." + validDomainChar + ")+$");
//		
//		final Matcher ipMat = ipDomainPat.matcher(domainPart);
//		// we make sure that the email domain name is an IP address
//		if (ipMat.matches()) {
//			final Pattern fullIpDomainPat = Pattern.compile(
//					"^(\\b(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)"
//					+ "\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)"
//					+ "\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)"
//					+ "\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\b)$");
//			
//			return fullIpDomainPat.matcher(domainPart).matches();
//		}
//
//		Matcher domainMat = domainPat.matcher(domainPart);
//		if (!domainMat.matches()) {
//			return false;
//		}
//		final int len = domainMat.groupCount();
//		if (len < 2) {
//			return false;
//		}
//		String domString = domainMat.group(2);
//		while (domainMat.matches()) {
//			domString = domainMat.group(2);
//			domainMat = domainPat.matcher(domString);
//		}
//
//		if (domString.length() < PRIMARY_DOMAIN_MINLENGTH || domString.length() > PRIMARY_DOMAIN_MAXLENGTH) {
//			return false;
//		}
//		return true;
//	}
}
