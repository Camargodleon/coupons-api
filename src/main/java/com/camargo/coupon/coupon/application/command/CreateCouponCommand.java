package com.camargo.coupon.coupon.application.command;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateCouponCommand(
        String code,
        BigDecimal discount,
        LocalDateTime validUntil
) {
}
