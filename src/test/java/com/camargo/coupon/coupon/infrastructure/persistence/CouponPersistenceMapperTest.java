package com.camargo.coupon.coupon.infrastructure.persistence;

import com.camargo.coupon.coupon.domain.Coupon;
import com.camargo.coupon.coupon.domain.CouponStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CouponPersistenceMapper Unit Tests")
class CouponPersistenceMapperTest {

    private CouponPersistenceMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new CouponPersistenceMapper();
    }

    @Nested
    @DisplayName("toEntity")
    class ToEntity {

        @Test
        @DisplayName("Should map active domain coupon to entity preserving all fields")
        void shouldMapActiveDomainCouponToEntity() {
            Coupon coupon = Coupon.create(
                    "ABC123",
                    "Test description",
                    new BigDecimal("12.50"),
                    Instant.now().plus(Duration.ofDays(5)),
                    true
            );

            CouponEntity entity = mapper.toEntity(coupon);

            assertThat(entity).isNotNull();
            assertThat(entity.getId()).isEqualTo(coupon.getId());
            assertThat(entity.getCode()).isEqualTo("ABC123");
            assertThat(entity.getDescription()).isEqualTo("Test description");
            assertThat(entity.getDiscountValue()).isEqualByComparingTo(new BigDecimal("12.50"));
            assertThat(entity.getExpirationDate()).isEqualTo(coupon.getExpirationDate());
            assertThat(entity.getStatus()).isEqualTo(CouponStatus.ACTIVE);
            assertThat(entity.isPublished()).isTrue();
            assertThat(entity.isRedeemed()).isFalse();
            assertThat(entity.getDeletedAt()).isNull();
        }

        @Test
        @DisplayName("Should map soft-deleted domain coupon to entity preserving deletedAt")
        void shouldMapDeletedDomainCouponToEntity() {
            Coupon coupon = Coupon.create(
                    "ABC123",
                    "Test description",
                    new BigDecimal("12.50"),
                    Instant.now().plus(Duration.ofDays(5)),
                    false
            );
            coupon.delete();

            CouponEntity entity = mapper.toEntity(coupon);

            assertThat(entity).isNotNull();
            assertThat(entity.getDeletedAt()).isNotNull();
            assertThat(entity.getDeletedAt()).isEqualTo(coupon.getDeletedAt());
        }

        @Test
        @DisplayName("Should return null when domain coupon is null")
        void shouldReturnNullWhenCouponIsNull() {
            assertThat(mapper.toEntity(null)).isNull();
        }
    }

    @Nested
    @DisplayName("toDomain")
    class ToDomain {

        @Test
        @DisplayName("Should map entity to domain coupon preserving all fields")
        void shouldMapEntityToDomain() {
            UUID id = UUID.randomUUID();
            Instant expiration = Instant.now().plus(Duration.ofDays(10));
            CouponEntity entity = CouponEntity.builder()
                    .id(id)
                    .code("SAVE10")
                    .description("Save 10% coupon")
                    .discountValue(new BigDecimal("10.00"))
                    .expirationDate(expiration)
                    .status(CouponStatus.ACTIVE)
                    .published(true)
                    .redeemed(false)
                    .deletedAt(null)
                    .build();

            Coupon domain = mapper.toDomain(entity);

            assertThat(domain).isNotNull();
            assertThat(domain.getId()).isEqualTo(id);
            assertThat(domain.getCode()).isEqualTo("SAVE10");
            assertThat(domain.getDescription()).isEqualTo("Save 10% coupon");
            assertThat(domain.getDiscountValue()).isEqualByComparingTo(new BigDecimal("10.00"));
            assertThat(domain.getExpirationDate()).isEqualTo(expiration);
            assertThat(domain.getStatus()).isEqualTo(CouponStatus.ACTIVE);
            assertThat(domain.isPublished()).isTrue();
            assertThat(domain.isRedeemed()).isFalse();
            assertThat(domain.isDeleted()).isFalse();
            assertThat(domain.getDeletedAt()).isNull();
        }

        @Test
        @DisplayName("Should map entity with deletedAt to soft-deleted domain coupon")
        void shouldMapEntityWithDeletedAtToDeletedDomain() {
            UUID id = UUID.randomUUID();
            Instant expiration = Instant.now().plus(Duration.ofDays(10));
            Instant deletedAt = Instant.now().minus(Duration.ofHours(1));

            CouponEntity entity = CouponEntity.builder()
                    .id(id)
                    .code("SAVE10")
                    .description("Deleted coupon")
                    .discountValue(new BigDecimal("10.00"))
                    .expirationDate(expiration)
                    .status(CouponStatus.ACTIVE)
                    .published(false)
                    .redeemed(false)
                    .deletedAt(deletedAt)
                    .build();

            Coupon domain = mapper.toDomain(entity);

            assertThat(domain).isNotNull();
            assertThat(domain.isDeleted()).isTrue();
            assertThat(domain.getDeletedAt()).isEqualTo(deletedAt);
        }

        @Test
        @DisplayName("Should return null when entity is null")
        void shouldReturnNullWhenEntityIsNull() {
            assertThat(mapper.toDomain(null)).isNull();
        }
    }
}
