package com.app.diamondhotelbackend.util;

public class Constant {

    /**
     * ----- IMAGE -----
     **/
    public static final int MAX_IMAGE_SIZE = 4 * 1024;


    /**
     * ----- GRANTED AUTHORITY -----
     **/
    public static final String USER = "USER";

    public static final String ADMIN = "ADMIN";


    /**
     * ----- JWT -----
     **/
    public static final String LOCAL = "LOCAL";

    public static final String JWT_CLAIM_AUTHORITIES = "authorities";


    /**
     * ----- OAUTH2 -----
     **/
    public static final String OAUTH2 = "OAUTH2";

    public static final String OAUTH2_ATTR_NAME = "name";

    public static final String OAUTH2_ATTR_GIVEN_NAME = "given_name";

    public static final String OAUTH2_ATTR_FAMILY_NAME = "family_name";

    public static final String OAUTH2_ATTR_PICTURE = "picture";

    public static final String OAUTH2_ATTR_EMAIL = "email";

    public static final String OAUTH2_ATTR_CONFIRMED = "confirmed";

    public static final String OAUTH2_ATTR_ACCESS_TOKEN = "access-token";

    public static final String OAUTH2_ATTR_REFRESH_TOKEN = "refresh-token";

    public static final String OAUTH2_ATTR_ERROR = "error";

    public static final String OAUTH2_CALLBACK_URI = "/sign-in/oauth2/callback";


    /**
     * ----- PAYMENT -----
     **/
    public static final String APPROVED = "APPROVED";

    public static final String WAITING_FOR_PAYMENT = "WAITING_FOR_PAYMENT";

    public static final String CANCELLED = "CANCELLED";


    /**
     * ----- EXCEPTION -----
     **/
    public static final String INVALID_AUTH_PROVIDER_EXCEPTION = "Invalid auth provider exception";

    public static final String PASSWORD_EXISTS_EXCEPTION = "Password exists exception";

    public static final String USER_PROFILE_NOT_FOUND_EXCEPTION = "User profile not found";

    public static final String USER_PROFILE_EXISTS_EXCEPTION = "User profile exists";

    public static final String NUMBER_OF_AVAILABLE_ROOMS_HAS_CHANGED_EXCEPTION = "Number of available rooms has changed";

    public static final String INCORRECT_CHECK_IN_OR_CHECK_OUT_FORMAT_EXCEPTION = "Incorrect check in or check out format";

    public static final String INVALID_TOKEN_SIGNATURE_EXCEPTION = "Invalid JWT signature";

    public static final String INVALID_TOKEN_EXCEPTION = "Invalid JWT token";

    public static final String TOKEN_IS_EXPIRED_EXCEPTION = "JWT token is expired";

    public static final String TOKEN_IS_UNSUPPORTED_EXCEPTION = "JWT token is unsupported";

    public static final String TOKEN_CLAIMS_STRING_IS_EMPTY_EXCEPTION = "JWT claims string is empty";

    public static final String CONFIRMATION_TOKEN_NOT_FOUND = "Email token not found";

    public static final String CONFIRMATION_TOKEN_ALREADY_CONFIRMED = "Confirmation token already confirmed";

    public static final String CONFIRMATION_TOKEN_ALREADY_EXPIRED = "Confirmation token already expired";


    /**
     * ----- OPINION -----
     **/
    public static final String BAD = "BAD";

    public static final String GOOD = "GOOD";

    public static final String EXCELLENT = "EXCELLENT";


    /**
     * ----- EMAIL -----
     **/
    public static final String EMAIL_SENDER = "diamond.hotel.contact@gmail.com";

    public static final String EMAIL_ATTR_CONFIRMATION_TOKEN = "confirmation-token";

    public static final String EMAIL_CONFIRM_ACCOUNT_SUBJECT = "Diamond hotel - confirm your email";

    public static final String EMAIL_CONFIRM_ACCOUNT_CALLBACK_URI = "/account/confirmation";

    public static final String EMAIL_CONFIRM_ACCOUNT_LINK_DESCRIPTION = "Activate account now";

    public static final String EMAIL_CONFIRM_ACCOUNT_CONTENT_DESCRIPTION = "Thank you for registering. Please click on the below link to activate your account:";

    public static final String EMAIL_CONFIRM_ACCOUNT_CONTENT_TITLE = "Confirm your email";

    public static final String EMAIL_CHANGE_PASSWORD_CALLBACK_URI = "/change/password";

    public static final String EMAIL_CHANGE_PASSWORD_CONTENT_TITLE = "Change your password";

    public static final String EMAIL_CHANGE_PASSWORD_CONTENT_DESCRIPTION = "Do you want to change your password?. If you have not used the password reminder function, just ignore this message.";

    public static final String EMAIL_CHANGE_PASSWORD_LINK_DESCRIPTION = "Change password now";

    public static final String EMAIL_CHANGE_PASSWORD_SUBJECT = "Diamond hotel - change your password";
}
