package com.camargo.coupon.coupon.api.dto;

import com.camargo.coupon.coupon.domain.Coupon;
import com.camargo.coupon.coupon.domain.CouponStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record CouponResponse(
        @Schema(description = "Coupon unique identifier", example = "cef9d1e3-aae5-4ab6-a297-358c6032b1e7")
        UUID id,

        @Schema(description = "Alphanumeric coupon code of exactly 6 characters", example = "ABC123")
        String code,

        @Schema(description = "Description of the coupon", example = "Discount voucher for loyal customers")
        String description,

        @Schema(description = "Discount value", example = "0.8")
        BigDecimal discountValue,

        @Schema(description = "Expiration date in ISO-8601 UTC format", example = "2026-11-04T17:36:46.577Z")
        Instant expirationDate,

        @Schema(description = "Coupon status", example = "ACTIVE")
        CouponStatus status,

        @Schema(description = "Whether the coupon is published", example = "false")
        boolean published,

        @Schema(description = "Whether the coupon has been redeemed", example = "false")
        boolean redeemed
) {

    public static CouponResponse fromDomain(Coupon coupon) {
        return new CouponResponse(
                coupon.getId(),
                coupon.getCode(),
                coupon.getDescription(),
                coupon.getDiscountValue(),
                coupon.getExpirationDate(),
                coupon.getStatus(),
                coupon.isPublished(),
                coupon.isRedeemed()
        );
    }
}
