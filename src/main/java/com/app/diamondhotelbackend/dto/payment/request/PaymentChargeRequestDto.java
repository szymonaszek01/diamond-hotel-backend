package com.app.diamondhotelbackend.dto.payment.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentChargeRequestDto {

    private long id;

    private String token;

    private int amount;
}
