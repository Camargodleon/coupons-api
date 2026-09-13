package com.camargo.coupon.coupon.infrastructure.persistence;

import com.camargo.coupon.coupon.domain.Coupon;
import org.springframework.stereotype.Component;

@Component
public class CouponPersistenceMapper {

    public Coupon toDomain(CouponEntity entity) {
        throw new UnsupportedOperationException(
                "Implement CouponEntity -> Coupon mapping"
        );
    }

    public CouponEntity toEntity(Coupon coupon) {
        throw new UnsupportedOperationException(
                "Implement Coupon -> CouponEntity mapping"
        );
    }
}
