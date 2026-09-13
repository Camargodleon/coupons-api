package com.camargo.coupon.coupon.application.query;

import com.camargo.coupon.coupon.domain.Coupon;
import com.camargo.coupon.coupon.domain.CouponRepository;
import com.camargo.coupon.coupon.domain.exception.CouponNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CouponQueryService Unit Tests")
class CouponQueryServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @InjectMocks
    private CouponQueryService couponQueryService;

    @Test
    @DisplayName("Should return coupon when coupon exists and is not deleted")
    void shouldReturnCouponWhenActive() {
        UUID couponId = UUID.randomUUID();
        Coupon coupon = Coupon.create(
                "ACTIVE",
                "Active coupon",
                new BigDecimal("5.0"),
                Instant.now().plus(Duration.ofDays(7)),
                true
        );

        when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));

        Coupon result = couponQueryService.getById(new GetCouponQuery(couponId));

        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo("ACTIVE");
        assertThat(result.isDeleted()).isFalse();
        verify(couponRepository, times(1)).findById(couponId);
    }

    @Test
    @DisplayName("Should throw CouponNotFoundException when coupon does not exist in repository")
    void shouldThrowNotFoundWhenDoesNotExist() {
        UUID nonExistentId = UUID.randomUUID();
        when(couponRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> couponQueryService.getById(new GetCouponQuery(nonExistentId)))
                .isInstanceOf(CouponNotFoundException.class)
                .hasMessage("Coupon not found with id: " + nonExistentId);

        verify(couponRepository, times(1)).findById(nonExistentId);
    }

    @Test
    @DisplayName("Should throw CouponNotFoundException when coupon is soft-deleted")
    void shouldThrowNotFoundWhenCouponIsSoftDeleted() {
        UUID couponId = UUID.randomUUID();
        Coupon coupon = Coupon.create(
                "DELE01",
                "Soft deleted coupon",
                new BigDecimal("10.0"),
                Instant.now().plus(Duration.ofDays(7)),
                false
        );
        coupon.delete();

        when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));

        assertThatThrownBy(() -> couponQueryService.getById(new GetCouponQuery(couponId)))
                .isInstanceOf(CouponNotFoundException.class)
                .hasMessage("Coupon not found with id: " + couponId);

        verify(couponRepository, times(1)).findById(couponId);
    }
}
