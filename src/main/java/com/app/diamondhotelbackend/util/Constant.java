package com.app.diamondhotelbackend.util;

public class Constant {

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

    public static final String OAUTH2_ATTR_ACCESS_TOKEN = "access-token";

    public static final String OAUTH2_ATTR_REFRESH_TOKEN = "refresh-token";

    public static final String OAUTH2_ATTR_ERROR = "error";


    /**
     * ----- PAYMENT -----
     **/
    public static final String APPROVED = "APPROVED";

    public static final String WAITING_FOR_PAYMENT = "WAITING_FOR_PAYMENT";

    public static final String CANCELLED = "CANCELLED";


    /**
     * ----- EXCEPTION -----
     **/
    public static final String USER_PROFILE_NOT_FOUND_EXCEPTION = "User profile not found";

    public static final String USER_PROFILE_EXISTS_EXCEPTION = "User profile exists";

    public static final String NUMBER_OF_AVAILABLE_ROOMS_HAS_CHANGED_EXCEPTION = "Number of available rooms has changed";

    public static final String INCORRECT_CHECK_IN_OR_CHECK_OUT_FORMAT_EXCEPTION = "Incorrect check in or check out format";

    public static final String INVALID_TOKEN_SIGNATURE_EXCEPTION = "Invalid JWT signature";

    public static final String INVALID_TOKEN_EXCEPTION = "Invalid JWT token";

    public static final String TOKEN_IS_EXPIRED_EXCEPTION = "JWT token is expired";

    public static final String TOKEN_IS_UNSUPPORTED_EXCEPTION = "JWT token is unsupported";

    public static final String TOKEN_CLAIMS_STRING_IS_EMPTY_EXCEPTION = "JWT claims string is empty";


    /**
     * ----- OPINION -----
     **/
    public static final String BAD = "BAD";

    public static final String GOOD = "GOOD";

    public static final String EXCELLENT = "EXCELLENT";
}
