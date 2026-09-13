package com.camargo.coupon.coupon.domain;

import com.camargo.coupon.coupon.domain.exception.CouponAlreadyDeletedException;
import com.camargo.coupon.coupon.domain.exception.CouponValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Coupon Domain Unit Tests")
class CouponTest {

    private static final String VALID_CODE = "PROMO1";
    private static final String VALID_DESCRIPTION = "Standard promotional coupon";
    private static final BigDecimal VALID_DISCOUNT = new BigDecimal("10.0");
    private static final Instant FUTURE_EXPIRATION = Instant.now().plus(Duration.ofDays(30));

    @Nested
    @DisplayName("Creation Business Rules")
    class CreationRules {

        @Test
        @DisplayName("Should create coupon successfully with valid inputs")
        void shouldCreateCouponSuccessfullyWithValidInputs() {
            Coupon coupon = Coupon.create(
                    VALID_CODE,
                    VALID_DESCRIPTION,
                    VALID_DISCOUNT,
                    FUTURE_EXPIRATION,
                    false
            );

            assertThat(coupon).isNotNull();
            assertThat(coupon.getId()).isNotNull();
            assertThat(coupon.getCode()).isEqualTo(VALID_CODE);
            assertThat(coupon.getDescription()).isEqualTo(VALID_DESCRIPTION);
            assertThat(coupon.getDiscountValue()).isEqualByComparingTo(VALID_DISCOUNT);
            assertThat(coupon.getExpirationDate()).isEqualTo(FUTURE_EXPIRATION);
            assertThat(coupon.getStatus()).isEqualTo(CouponStatus.ACTIVE);
            assertThat(coupon.isPublished()).isFalse();
            assertThat(coupon.isRedeemed()).isFalse();
            assertThat(coupon.isDeleted()).isFalse();
            assertThat(coupon.getDeletedAt()).isNull();
        }

        @Test
        @DisplayName("Should allow creating a coupon as already published")
        void shouldCreateCouponAsPublished() {
            Coupon coupon = Coupon.create(
                    VALID_CODE,
                    VALID_DESCRIPTION,
                    VALID_DISCOUNT,
                    FUTURE_EXPIRATION,
                    true
            );

            assertThat(coupon.isPublished()).isTrue();
        }

        @Test
        @DisplayName("Should default published to false when published is null")
        void shouldDefaultPublishedToFalseWhenNull() {
            Coupon coupon = Coupon.create(
                    VALID_CODE,
                    VALID_DESCRIPTION,
                    VALID_DISCOUNT,
                    FUTURE_EXPIRATION,
                    null
            );

            assertThat(coupon.isPublished()).isFalse();
        }

        @Test
        @DisplayName("Should trim description whitespace")
        void shouldTrimDescription() {
            Coupon coupon = Coupon.create(
                    VALID_CODE,
                    "   Whitespace description   ",
                    VALID_DISCOUNT,
                    FUTURE_EXPIRATION,
                    false
            );

            assertThat(coupon.getDescription()).isEqualTo("Whitespace description");
        }

        @Nested
        @DisplayName("Code Sanitization and Validation Rules")
        class CodeRules {

            @ParameterizedTest
            @ValueSource(strings = {"ABC-123", "ABC 123", "!@#ABC123", "A.B.C.1.2.3", "A_B_C_1_2_3", "###A1B2C3!!!"})
            @DisplayName("Should strip special characters from code and preserve exactly 6 alphanumeric characters")
            void shouldStripSpecialCharactersAndKeepSixAlphanumericCharacters(String rawCode) {
                Coupon coupon = Coupon.create(
                        rawCode,
                        VALID_DESCRIPTION,
                        VALID_DISCOUNT,
                        FUTURE_EXPIRATION,
                        false
                );

                assertThat(coupon.getCode()).hasSize(6);
                assertThat(coupon.getCode()).matches("^[a-zA-Z0-9]{6}$");
            }

            @ParameterizedTest
            @NullAndEmptySource
            @ValueSource(strings = {" ", "   ", "\t", "\n"})
            @DisplayName("Should fail when code is null, empty or blank")
            void shouldFailWhenCodeIsNullOrEmpty(String invalidCode) {
                assertThatThrownBy(() -> Coupon.create(
                        invalidCode,
                        VALID_DESCRIPTION,
                        VALID_DISCOUNT,
                        FUTURE_EXPIRATION,
                        false
                ))
                        .isInstanceOf(CouponValidationException.class)
                        .hasMessage("Coupon code is mandatory");
            }

            @ParameterizedTest
            @ValueSource(strings = {
                    "A", "AB", "ABC", "ABCD", "ABC12", // less than 6 alphanumeric characters
                    "ABC1234", "TOOLONG123",           // more than 6 alphanumeric characters
                    "AB-12", "A-B-C-1",                // special chars resulting in < 6 alphanumeric characters
                    "ABC-1234", "!@#ABCDEF1"           // special chars resulting in > 6 alphanumeric characters
            })
            @DisplayName("Should fail when code length after sanitization is not exactly 6 characters")
            void shouldFailWhenSanitizedCodeLengthIsNotSix(String invalidCode) {
                assertThatThrownBy(() -> Coupon.create(
                        invalidCode,
                        VALID_DESCRIPTION,
                        VALID_DISCOUNT,
                        FUTURE_EXPIRATION,
                        false
                ))
                        .isInstanceOf(CouponValidationException.class)
                        .hasMessageContaining("Coupon code must have exactly 6 alphanumeric characters");
            }

            @ParameterizedTest
            @ValueSource(strings = {"!@#$%", "---", "***___", "   -   "})
            @DisplayName("Should fail when code contains only special characters or symbols")
            void shouldFailWhenCodeContainsOnlySpecialCharacters(String specialOnlyCode) {
                assertThatThrownBy(() -> Coupon.create(
                        specialOnlyCode,
                        VALID_DESCRIPTION,
                        VALID_DISCOUNT,
                        FUTURE_EXPIRATION,
                        false
                ))
                        .isInstanceOf(CouponValidationException.class)
                        .hasMessageContaining("Coupon code must have exactly 6 alphanumeric characters");
            }
        }

        @Nested
        @DisplayName("Description Validation Rules")
        class DescriptionRules {

            @ParameterizedTest
            @NullAndEmptySource
            @ValueSource(strings = {" ", "   ", "\t", "\n"})
            @DisplayName("Should fail when description is null, empty or blank")
            void shouldFailWhenDescriptionIsNullOrEmpty(String invalidDescription) {
                assertThatThrownBy(() -> Coupon.create(
                        VALID_CODE,
                        invalidDescription,
                        VALID_DISCOUNT,
                        FUTURE_EXPIRATION,
                        false
                ))
                        .isInstanceOf(CouponValidationException.class)
                        .hasMessage("Coupon description is mandatory");
            }
        }

        @Nested
        @DisplayName("Discount Value Validation Rules")
        class DiscountValueRules {

            @Test
            @DisplayName("Should fail when discount value is null")
            void shouldFailWhenDiscountValueIsNull() {
                assertThatThrownBy(() -> Coupon.create(
                        VALID_CODE,
                        VALID_DESCRIPTION,
                        null,
                        FUTURE_EXPIRATION,
                        false
                ))
                        .isInstanceOf(CouponValidationException.class)
                        .hasMessage("Coupon discount value is mandatory");
            }

            @ParameterizedTest
            @ValueSource(strings = {"0.49", "0.4999", "0.0", "0", "-0.01", "-0.5", "-10.0"})
            @DisplayName("Should fail when discount value is strictly less than 0.5")
            void shouldFailWhenDiscountValueIsLessThanMinimum(String invalidValue) {
                BigDecimal discount = new BigDecimal(invalidValue);
                assertThatThrownBy(() -> Coupon.create(
                        VALID_CODE,
                        VALID_DESCRIPTION,
                        discount,
                        FUTURE_EXPIRATION,
                        false
                ))
                        .isInstanceOf(CouponValidationException.class)
                        .hasMessageContaining("Coupon discount value must be at least 0.5");
            }

            @ParameterizedTest
            @ValueSource(strings = {"0.5", "0.50", "0.5000", "0.51", "1.0", "99.99", "1000000.00"})
            @DisplayName("Should succeed when discount value is greater than or equal to minimum 0.5")
            void shouldSucceedWhenDiscountValueIsAtLeastMinimum(String validValue) {
                BigDecimal discount = new BigDecimal(validValue);
                Coupon coupon = Coupon.create(
                        VALID_CODE,
                        VALID_DESCRIPTION,
                        discount,
                        FUTURE_EXPIRATION,
                        false
                );

                assertThat(coupon.getDiscountValue()).isEqualByComparingTo(discount);
            }
        }

        @Nested
        @DisplayName("Expiration Date Validation Rules")
        class ExpirationDateRules {

            @Test
            @DisplayName("Should fail when expiration date is null")
            void shouldFailWhenExpirationDateIsNull() {
                assertThatThrownBy(() -> Coupon.create(
                        VALID_CODE,
                        VALID_DESCRIPTION,
                        VALID_DISCOUNT,
                        null,
                        false
                ))
                        .isInstanceOf(CouponValidationException.class)
                        .hasMessage("Coupon expiration date is mandatory");
            }

            @Test
            @DisplayName("Should fail when expiration date is in the past")
            void shouldFailWhenExpirationDateIsInThePast() {
                Instant pastDate = Instant.now().minus(Duration.ofMinutes(5));
                assertThatThrownBy(() -> Coupon.create(
                        VALID_CODE,
                        VALID_DESCRIPTION,
                        VALID_DISCOUNT,
                        pastDate,
                        false
                ))
                        .isInstanceOf(CouponValidationException.class)
                        .hasMessage("Coupon expiration date cannot be in the past");
            }

            @Test
            @DisplayName("Should succeed when expiration date is in the future")
            void shouldSucceedWhenExpirationDateIsInTheFuture() {
                Instant futureDate = Instant.now().plus(Duration.ofDays(1));
                Coupon coupon = Coupon.create(
                        VALID_CODE,
                        VALID_DESCRIPTION,
                        VALID_DISCOUNT,
                        futureDate,
                        false
                );

                assertThat(coupon.getExpirationDate()).isEqualTo(futureDate);
            }
        }
    }

    @Nested
    @DisplayName("Soft Delete Business Rules")
    class SoftDeleteRules {

        @Test
        @DisplayName("Should successfully soft delete an active coupon")
        void shouldSoftDeleteActiveCoupon() {
            Coupon coupon = Coupon.create(
                    VALID_CODE,
                    VALID_DESCRIPTION,
                    VALID_DISCOUNT,
                    FUTURE_EXPIRATION,
                    false
            );

            assertThat(coupon.isDeleted()).isFalse();
            assertThat(coupon.getDeletedAt()).isNull();

            coupon.delete();

            assertThat(coupon.isDeleted()).isTrue();
            assertThat(coupon.getDeletedAt()).isNotNull();
            assertThat(coupon.getDeletedAt()).isBeforeOrEqualTo(Instant.now());
        }

        @Test
        @DisplayName("Should preserve all original coupon data after soft delete")
        void shouldPreserveAllDataAfterSoftDelete() {
            Coupon coupon = Coupon.create(
                    VALID_CODE,
                    VALID_DESCRIPTION,
                    VALID_DISCOUNT,
                    FUTURE_EXPIRATION,
                    true
            );
            UUID originalId = coupon.getId();
            String originalCode = coupon.getCode();
            String originalDescription = coupon.getDescription();
            BigDecimal originalDiscount = coupon.getDiscountValue();
            Instant originalExpiration = coupon.getExpirationDate();
            CouponStatus originalStatus = coupon.getStatus();
            boolean originalPublished = coupon.isPublished();
            boolean originalRedeemed = coupon.isRedeemed();

            coupon.delete();

            assertThat(coupon.getId()).isEqualTo(originalId);
            assertThat(coupon.getCode()).isEqualTo(originalCode);
            assertThat(coupon.getDescription()).isEqualTo(originalDescription);
            assertThat(coupon.getDiscountValue()).isEqualByComparingTo(originalDiscount);
            assertThat(coupon.getExpirationDate()).isEqualTo(originalExpiration);
            assertThat(coupon.getStatus()).isEqualTo(originalStatus);
            assertThat(coupon.isPublished()).isEqualTo(originalPublished);
            assertThat(coupon.isRedeemed()).isEqualTo(originalRedeemed);
            assertThat(coupon.isDeleted()).isTrue();
        }

        @Test
        @DisplayName("Should throw CouponAlreadyDeletedException when deleting an already deleted coupon")
        void shouldThrowExceptionWhenDeletingAlreadyDeletedCoupon() {
            Coupon coupon = Coupon.create(
                    VALID_CODE,
                    VALID_DESCRIPTION,
                    VALID_DISCOUNT,
                    FUTURE_EXPIRATION,
                    false
            );

            coupon.delete();
            assertThat(coupon.isDeleted()).isTrue();

            assertThatThrownBy(coupon::delete)
                    .isInstanceOf(CouponAlreadyDeletedException.class)
                    .hasMessage("Coupon is already deleted");
        }
    }

    @Nested
    @DisplayName("Equality and Identity")
    class EqualityAndIdentity {

        @Test
        @DisplayName("Coupons with the same ID should be equal and have the same hashCode")
        void shouldBeEqualWhenSameId() {
            UUID id = UUID.randomUUID();
            Coupon coupon1 = new Coupon(id, "CODE01", "Desc 1", new BigDecimal("10.0"), FUTURE_EXPIRATION, CouponStatus.ACTIVE, false, false, null);
            Coupon coupon2 = new Coupon(id, "CODE02", "Desc 2", new BigDecimal("20.0"), FUTURE_EXPIRATION, CouponStatus.INACTIVE, true, true, null);

            assertThat(coupon1).isEqualTo(coupon2);
            assertThat(coupon1.hashCode()).isEqualTo(coupon2.hashCode());
        }

        @Test
        @DisplayName("Coupons with different IDs should not be equal")
        void shouldNotBeEqualWhenDifferentId() {
            Coupon coupon1 = new Coupon(UUID.randomUUID(), "CODE01", "Desc 1", new BigDecimal("10.0"), FUTURE_EXPIRATION, CouponStatus.ACTIVE, false, false, null);
            Coupon coupon2 = new Coupon(UUID.randomUUID(), "CODE01", "Desc 1", new BigDecimal("10.0"), FUTURE_EXPIRATION, CouponStatus.ACTIVE, false, false, null);

            assertThat(coupon1).isNotEqualTo(coupon2);
        }

        @Test
        @DisplayName("Coupon should not be equal to null or different class")
        void shouldNotBeEqualToNullOrDifferentType() {
            Coupon coupon = Coupon.create(VALID_CODE, VALID_DESCRIPTION, VALID_DISCOUNT, FUTURE_EXPIRATION, false);

            assertThat(coupon).isNotEqualTo(null);
            assertThat(coupon).isNotEqualTo("some string");
        }
    }
}
