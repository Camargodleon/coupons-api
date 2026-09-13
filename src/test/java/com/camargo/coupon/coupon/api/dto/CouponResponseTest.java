package com.camargo.coupon.coupon.api.dto;

import com.camargo.coupon.coupon.domain.Coupon;
import com.camargo.coupon.coupon.domain.CouponStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CouponResponse Unit Tests")
class CouponResponseTest {

    @Test
    @DisplayName("Should correctly map all domain properties to CouponResponse")
    void shouldMapDomainToResponse() {
        Instant expiration = Instant.now().plus(Duration.ofDays(10));
        Coupon coupon = Coupon.create(
                "ABC123",
                "Discount coupon",
                new BigDecimal("15.75"),
                expiration,
                true
        );

        CouponResponse response = CouponResponse.fromDomain(coupon);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(coupon.getId());
        assertThat(response.code()).isEqualTo("ABC123");
        assertThat(response.description()).isEqualTo("Discount coupon");
        assertThat(response.discountValue()).isEqualByComparingTo(new BigDecimal("15.75"));
        assertThat(response.expirationDate()).isEqualTo(expiration);
        assertThat(response.status()).isEqualTo(CouponStatus.ACTIVE);
        assertThat(response.published()).isTrue();
        assertThat(response.redeemed()).isFalse();
    }
}
