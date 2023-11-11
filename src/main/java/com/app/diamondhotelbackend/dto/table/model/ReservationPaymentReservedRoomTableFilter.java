package com.app.diamondhotelbackend.dto.table.model;

import com.app.diamondhotelbackend.util.DateUtil;
import com.app.diamondhotelbackend.util.UrlUtil;
import lombok.Getter;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.sql.Date;

@Getter
public class ReservationPaymentReservedRoomTableFilter {

    private final Date minDate;

    private final Date maxDate;

    private final String userProfileEmail;

    private final String flightNumber;

    private final String paymentStatus;

    private final BigDecimal minPaymentCost;

    private final BigDecimal maxPaymentCost;

    private final String paymentCharge;

    private final String roomTypeName;

    public ReservationPaymentReservedRoomTableFilter(JSONObject filters) {
        minDate = DateUtil.parseDate(UrlUtil.getValueFromJSONObject("min_date", filters)).orElse(null);
        maxDate = DateUtil.parseDate(UrlUtil.getValueFromJSONObject("max_date", filters)).orElse(null);
        userProfileEmail = UrlUtil.getValueFromJSONObject("user_profile_email", filters);
        flightNumber = UrlUtil.getValueFromJSONObject("flight_number", filters);
        paymentStatus = UrlUtil.getValueFromJSONObject("payment_status", filters);
        minPaymentCost = toBigDecimalMapper(UrlUtil.getValueFromJSONObject("min_payment_cost", filters));
        maxPaymentCost = toBigDecimalMapper(UrlUtil.getValueFromJSONObject("max_payment_cost", filters));
        paymentCharge = UrlUtil.getValueFromJSONObject("payment_charge", filters);
        roomTypeName = UrlUtil.getValueFromJSONObject("room_type_name", filters);
    }

    private BigDecimal toBigDecimalMapper(String value) {
        try {
            if (value == null || value.isEmpty()) {
                return null;
            }

            return new BigDecimal(value);

        } catch (NumberFormatException e) {
            return null;
        }
    }
}
