package com.camargo.coupon.coupon.application.command;

import java.util.UUID;

public record DeleteCouponCommand(
        UUID id
) {
}
