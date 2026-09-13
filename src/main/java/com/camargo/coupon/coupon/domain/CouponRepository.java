package com.camargo.coupon.coupon.domain;

import java.util.Optional;
import java.util.UUID;

public interface CouponRepository {

    Optional<Coupon> findById(UUID id);

    Coupon save(Coupon coupon);
}
