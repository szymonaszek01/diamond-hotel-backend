package com.app.diamondhotelbackend.util;

public class ConstantUtil {

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

    public static final String OAUTH2_ATTR_ID = "id";

    public static final String OAUTH2_ATTR_ACCESS_TOKEN = "access-token";

    public static final String OAUTH2_ATTR_REFRESH_TOKEN = "refresh-token";

    public static final String OAUTH2_ATTR_ERROR = "error";

    public static final String OAUTH2_CALLBACK_URI = "/sign-in/oauth2/callback";


    /**
     * ----- PAYMENT -----
     **/
    public static final String REFUND = "refund";

    public static final String SUCCEEDED = "succeeded";

    public static final String APPROVED = "approved";

    public static final String WAITING_FOR_PAYMENT = "waiting-for-payment";

    public static final String CANCELLED = "cancelled";


    /**
     * ----- EXCEPTION -----
     **/
    public static final String INVALID_AUTH_PROVIDER_EXCEPTION = "Invalid auth provider";

    public static final String PASSWORD_EXISTS_EXCEPTION = "Password exists";

    public static final String EMAIL_EXISTS_EXCEPTION = "Email exists";

    public static final String USER_PROFILE_NOT_FOUND_EXCEPTION = "User profile not found";

    public static final String EMAIL_NOT_FOUND_FROM_OAUTH_2_PROVIDER_EXCEPTION = "Email not found from OAuth2 provider";

    public static final String USER_PROFILE_EXISTS_EXCEPTION = "User profile exists";

    public static final String NOT_ENOUGH_AVAILABLE_ROOMS = "Not enough available rooms";

    public static final String INVALID_TOKEN_SIGNATURE_EXCEPTION = "Invalid JWT signature";

    public static final String INVALID_TOKEN_EXCEPTION = "Invalid JWT token";

    public static final String TOKEN_IS_EXPIRED_EXCEPTION = "JWT token is expired";

    public static final String TOKEN_IS_UNSUPPORTED_EXCEPTION = "JWT token is unsupported";

    public static final String TOKEN_CLAIMS_STRING_IS_EMPTY_EXCEPTION = "JWT claims string is empty";

    public static final String CONFIRMATION_TOKEN_NOT_FOUND_EXCEPTION = "Email token not found";

    public static final String CONFIRMATION_TOKEN_ALREADY_CONFIRMED_EXCEPTION = "Confirmation token already confirmed";

    public static final String CONFIRMATION_TOKEN_ALREADY_EXPIRED_EXCEPTION = "Confirmation token already expired";

    public static final String INVALID_PARAMETERS_EXCEPTION = "Invalid parameters";

    public static final String ROOM_TYPE_NOT_FOUND_EXCEPTION = "Room type not found";

    public static final String AVAILABLE_ROOM_NOT_FOUND_EXCEPTION = "Available room not found";

    public static final String PAYMENT_NOT_FOUND_EXCEPTION = "Payment not found";

    public static final String FLIGHT_NOT_FOUND_EXCEPTION = "Flight not found";

    public static final String RESERVATION_NOT_FOUND_EXCEPTION = "Reservation not found exception";

    public static final String PAYMENT_EXPIRED_EXCEPTION = "Payment expired";

    public static final String CHARGE_NOT_FOUND_EXCEPTION = "Charge not found";


    /**
     * ----- OPINION -----
     **/
    public static final String BAD = "BAD";

    public static final String GOOD = "GOOD";

    public static final String EXCELLENT = "EXCELLENT";


    /**
     * ----- OPEN WEATHER -----
     **/
    public static final String OPEN_WEATHER_BASE_URI = "https://api.openweathermap.org";

    public static final String OPEN_WEATHER_VALUE_LAT = "4.195762812224563";

    public static final String OPEN_WEATHER_ATTR_LAT = "lat";

    public static final String OPEN_WEATHER_ATTR_LON = "lon";

    public static final String OPEN_WEATHER_ATTR_EXCLUDE = "exclude";

    public static final String OPEN_WEATHER_ATTR_APPID = "appid";

    public static final String OPEN_WEATHER_ATTR_UNITS = "units";

    public static final String OPEN_WEATHER_VALUE_UNITS = "metric";

    public static final String OPEN_WEATHER_VALUE_EXCLUDE = "minutely,hourly,alerts";

    public static final String OPEN_WEATHER_VALUE_LON = "73.52610223044698";


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

    public static final String EMAIL_CHANGE_PASSWORD_CALLBACK_URI = "/forgot/password";

    public static final String EMAIL_CHANGE_PASSWORD_CONTENT_TITLE = "Change your password";

    public static final String EMAIL_CHANGE_PASSWORD_CONTENT_DESCRIPTION = "Do you want to change your password?. If you have not used the password reminder function, just ignore this message.";

    public static final String EMAIL_CHANGE_PASSWORD_LINK_DESCRIPTION = "Change password now";

    public static final String EMAIL_CHANGE_PASSWORD_SUBJECT = "Diamond hotel - change your password";

    public static String buildEmail(String userName, String emailTitle, String emailDescription, String linkDescription, String link) {
        return "<div style=\"font-family:Poppins, sans-serif;font-size:16px;margin:0;color:#0b0c0c\">\n" +
                "\n" +
                "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>\n" +
                "\n" +
                "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">\n" +
                "        \n" +
                "        <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n" +
                "          <tbody><tr>\n" +
                "            <td width=\"70\" valign=\"middle\">\n" +
                "                <table align=\"left\" role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-top:20px;padding-bottom:20px\">\n" +
                "                      <span style=\"font-family:Poppins, sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">" + emailTitle + "</span>\n" +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "              </a>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n" +
                "      <td>\n" +
                "        \n" +
                "                <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td bgcolor=\"#d0bf79\" width=\"100%\" height=\"10\"></td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "\n" +
                "\n" +
                "\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody>" +
                "    <tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "      <td style=\"font-family:Poppins, sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n" +
                "        \n" +
                "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi " + userName + ",</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">" + emailDescription + "</p><blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #d0bf79;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\"><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> <a href=\"" + link + "\">" + linkDescription + "</a> </p></blockquote>\n Link will expire in 15 minutes. <p>See you soon</p>" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "  <table bgcolor=\"#0b0c0c\" role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n" +
                "          <tbody><tr>\n" +
                "            <td width=\"100\" valign=\"middle\">\n" +
                "                <table align=\"right\" role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                   <tr>\n" +
                "                    <td style=\"padding-top:20px;padding-bottom:20px;padding-right:20px\">\n" + "<img width=\"200\" height=\"auto\" src=\"cid:image\">\n" + "</td>" +
                "                   </tr>\n" +
                "                </tbody></table>\n" +
                "              </a>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </tbody></table>\n" +
                "<div class=\"yj6qo\"></div><div class=\"adL\">\n" +
                "\n" +
                "</div></div>";
    }
}
