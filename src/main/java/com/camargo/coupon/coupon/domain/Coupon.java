package com.camargo.coupon.coupon.domain;

import com.camargo.coupon.coupon.domain.exception.CouponAlreadyDeletedException;
import com.camargo.coupon.coupon.domain.exception.CouponValidationException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Coupon {

    private static final BigDecimal MIN_DISCOUNT_VALUE = new BigDecimal("0.5");
    private static final int CODE_LENGTH = 6;

    private final UUID id;
    private final String code;
    private final String description;
    private final BigDecimal discountValue;
    private final Instant expirationDate;
    private final CouponStatus status;
    private final boolean published;
    private final boolean redeemed;
    private Instant deletedAt;

    public Coupon(
            UUID id,
            String code,
            String description,
            BigDecimal discountValue,
            Instant expirationDate,
            CouponStatus status,
            boolean published,
            boolean redeemed,
            Instant deletedAt
    ) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.discountValue = discountValue;
        this.expirationDate = expirationDate;
        this.status = status;
        this.published = published;
        this.redeemed = redeemed;
        this.deletedAt = deletedAt;
    }

    public static Coupon create(
            String rawCode,
            String description,
            BigDecimal discountValue,
            Instant expirationDate,
            Boolean published
    ) {
        String sanitizedCode = sanitizeAndValidateCode(rawCode);
        validateDescription(description);
        validateDiscountValue(discountValue);
        validateExpirationDate(expirationDate);

        return new Coupon(
                UUID.randomUUID(),
                sanitizedCode,
                description.trim(),
                discountValue,
                expirationDate,
                CouponStatus.ACTIVE,
                Boolean.TRUE.equals(published),
                false,
                null
        );
    }

    private static String sanitizeAndValidateCode(String rawCode) {
        if (rawCode == null || rawCode.isBlank()) {
            throw new CouponValidationException("Coupon code is mandatory");
        }
        String sanitized = rawCode.replaceAll("[^a-zA-Z0-9]", "");
        if (sanitized.length() != CODE_LENGTH) {
            throw new CouponValidationException(
                    String.format("Coupon code must have exactly %d alphanumeric characters after removing special characters. Provided: '%s' -> '%s' (length: %d)",
                            CODE_LENGTH, rawCode, sanitized, sanitized.length())
            );
        }
        return sanitized;
    }

    private static void validateDescription(String description) {
        if (description == null || description.isBlank()) {
            throw new CouponValidationException("Coupon description is mandatory");
        }
    }

    private static void validateDiscountValue(BigDecimal discountValue) {
        if (discountValue == null) {
            throw new CouponValidationException("Coupon discount value is mandatory");
        }
        if (discountValue.compareTo(MIN_DISCOUNT_VALUE) < 0) {
            throw new CouponValidationException(
                    String.format("Coupon discount value must be at least %s, but received: %s",
                            MIN_DISCOUNT_VALUE.toPlainString(), discountValue.toPlainString())
            );
        }
    }

    private static void validateExpirationDate(Instant expirationDate) {
        if (expirationDate == null) {
            throw new CouponValidationException("Coupon expiration date is mandatory");
        }
        if (expirationDate.isBefore(Instant.now())) {
            throw new CouponValidationException("Coupon expiration date cannot be in the past");
        }
    }

    public void delete() {
        if (this.deletedAt!=null) {
            throw new CouponAlreadyDeletedException("Coupon is already deleted");
        }
        this.deletedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getDiscountValue() {
        return discountValue;
    }

    public Instant getExpirationDate() {
        return expirationDate;
    }

    public CouponStatus getStatus() {
        return status;
    }

    public boolean isPublished() {
        return published;
    }

    public boolean isRedeemed() {
        return redeemed;
    }

    public boolean isDeleted() {
        return this.deletedAt!=null;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coupon coupon = (Coupon) o;
        return Objects.equals(id, coupon.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
