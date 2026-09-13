package com.camargo.coupon.coupon.infrastructure.persistence;

import com.camargo.coupon.coupon.domain.Coupon;
import org.springframework.stereotype.Component;

@Component
public class CouponPersistenceMapper {

    public Coupon toDomain(CouponEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Coupon(
                entity.getId(),
                entity.getCode(),
                entity.getDescription(),
                entity.getDiscountValue(),
                entity.getExpirationDate(),
                entity.getStatus(),
                entity.isPublished(),
                entity.isRedeemed(),
                entity.getDeletedAt()
        );
    }

    public CouponEntity toEntity(Coupon coupon) {
        if (coupon == null) {
            return null;
        }
        return CouponEntity.builder()
                .id(coupon.getId())
                .code(coupon.getCode())
                .description(coupon.getDescription())
                .discountValue(coupon.getDiscountValue())
                .expirationDate(coupon.getExpirationDate())
                .status(coupon.getStatus())
                .published(coupon.isPublished())
                .redeemed(coupon.isRedeemed())
                .deletedAt(coupon.getDeletedAt())
                .build();
    }
}
