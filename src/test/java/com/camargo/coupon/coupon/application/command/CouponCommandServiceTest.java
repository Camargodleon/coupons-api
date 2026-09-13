package com.camargo.coupon.coupon.application.command;

import com.camargo.coupon.coupon.domain.Coupon;
import com.camargo.coupon.coupon.domain.CouponRepository;
import com.camargo.coupon.coupon.domain.exception.CouponAlreadyDeletedException;
import com.camargo.coupon.coupon.domain.exception.CouponNotFoundException;
import com.camargo.coupon.coupon.domain.exception.CouponValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CouponCommandService Unit Tests")
class CouponCommandServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @InjectMocks
    private CouponCommandService couponCommandService;

    private static final String VALID_CODE = "PROMO1";
    private static final String VALID_DESCRIPTION = "Summer discount";
    private static final BigDecimal VALID_DISCOUNT = new BigDecimal("15.5");
    private static final Instant FUTURE_EXPIRATION = Instant.now().plus(Duration.ofDays(10));

    @Nested
    @DisplayName("Create Coupon")
    class CreateCoupon {

        @Test
        @DisplayName("Should create and save coupon successfully when command data is valid")
        void shouldCreateAndSaveCouponSuccessfully() {
            CreateCouponCommand command = new CreateCouponCommand(
                    "PRO-123",
                    VALID_DESCRIPTION,
                    VALID_DISCOUNT,
                    FUTURE_EXPIRATION,
                    true
            );

            when(couponRepository.save(any(Coupon.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Coupon created = couponCommandService.create(command);

            assertThat(created).isNotNull();
            assertThat(created.getCode()).isEqualTo("PRO123");
            assertThat(created.getDescription()).isEqualTo(VALID_DESCRIPTION);
            assertThat(created.getDiscountValue()).isEqualByComparingTo(VALID_DISCOUNT);
            assertThat(created.isPublished()).isTrue();
            assertThat(created.isDeleted()).isFalse();

            ArgumentCaptor<Coupon> couponCaptor = ArgumentCaptor.forClass(Coupon.class);
            verify(couponRepository, times(1)).save(couponCaptor.capture());
            assertThat(couponCaptor.getValue().getCode()).isEqualTo("PRO123");
        }

        @Test
        @DisplayName("Should fail and not save to repository when coupon validation rules fail")
        void shouldNotSaveWhenValidationFails() {
            CreateCouponCommand invalidCommand = new CreateCouponCommand(
                    "INVALID_LONG_CODE",
                    VALID_DESCRIPTION,
                    VALID_DISCOUNT,
                    FUTURE_EXPIRATION,
                    false
            );

            assertThatThrownBy(() -> couponCommandService.create(invalidCommand))
                    .isInstanceOf(CouponValidationException.class);

            verify(couponRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should fail when discount is below minimum and not save to repository")
        void shouldNotSaveWhenDiscountBelowMinimum() {
            CreateCouponCommand invalidCommand = new CreateCouponCommand(
                    VALID_CODE,
                    VALID_DESCRIPTION,
                    new BigDecimal("0.2"),
                    FUTURE_EXPIRATION,
                    false
            );

            assertThatThrownBy(() -> couponCommandService.create(invalidCommand))
                    .isInstanceOf(CouponValidationException.class);

            verify(couponRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Delete Coupon")
    class DeleteCoupon {

        @Test
        @DisplayName("Should soft delete existing coupon and save changes")
        void shouldSoftDeleteAndSave() {
            UUID couponId = UUID.randomUUID();
            Coupon coupon = Coupon.create(
                    VALID_CODE,
                    VALID_DESCRIPTION,
                    VALID_DISCOUNT,
                    FUTURE_EXPIRATION,
                    false
            );

            when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));
            when(couponRepository.save(any(Coupon.class))).thenAnswer(invocation -> invocation.getArgument(0));

            DeleteCouponCommand command = new DeleteCouponCommand(couponId);
            couponCommandService.delete(command);

            assertThat(coupon.isDeleted()).isTrue();
            assertThat(coupon.getDeletedAt()).isNotNull();

            ArgumentCaptor<Coupon> captor = ArgumentCaptor.forClass(Coupon.class);
            verify(couponRepository, times(1)).save(captor.capture());
            assertThat(captor.getValue().isDeleted()).isTrue();
            assertThat(captor.getValue().getDeletedAt()).isNotNull();
        }

        @Test
        @DisplayName("Should throw CouponNotFoundException when coupon does not exist")
        void shouldThrowNotFoundWhenCouponDoesNotExist() {
            UUID nonExistentId = UUID.randomUUID();
            when(couponRepository.findById(nonExistentId)).thenReturn(Optional.empty());

            DeleteCouponCommand command = new DeleteCouponCommand(nonExistentId);

            assertThatThrownBy(() -> couponCommandService.delete(command))
                    .isInstanceOf(CouponNotFoundException.class)
                    .hasMessageContaining("Coupon not found with id: " + nonExistentId);

            verify(couponRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw CouponAlreadyDeletedException when trying to delete already deleted coupon")
        void shouldThrowAlreadyDeletedWhenCouponAlreadyDeleted() {
            UUID couponId = UUID.randomUUID();
            Coupon coupon = Coupon.create(
                    VALID_CODE,
                    VALID_DESCRIPTION,
                    VALID_DISCOUNT,
                    FUTURE_EXPIRATION,
                    false
            );
            coupon.delete();

            when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));

            DeleteCouponCommand command = new DeleteCouponCommand(couponId);

            assertThatThrownBy(() -> couponCommandService.delete(command))
                    .isInstanceOf(CouponAlreadyDeletedException.class)
                    .hasMessage("Coupon is already deleted");

            verify(couponRepository, never()).save(any());
        }
    }
}
