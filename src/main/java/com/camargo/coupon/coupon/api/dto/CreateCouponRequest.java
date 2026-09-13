package com.camargo.coupon.coupon.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;

public record CreateCouponRequest(
        @Schema(description = "Alphanumeric coupon code with 6 characters (special characters will be removed)", example = "ABC-123")
        String code,

        @Schema(description = "Description of the coupon", example = "Discount voucher for loyal customers")
        String description,

        @Schema(description = "Discount value (minimum 0.5)", example = "0.8")
        BigDecimal discountValue,

        @Schema(description = "Expiration date in ISO-8601 UTC format, must not be in the past", example = "2026-11-04T17:14:45.180Z")
        Instant expirationDate,

        @Schema(description = "Whether the coupon is created as already published", example = "false")
        Boolean published
) {
}
