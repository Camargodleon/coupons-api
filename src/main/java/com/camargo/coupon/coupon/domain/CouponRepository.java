package com.camargo.coupon.coupon.domain;

import java.util.Optional;
import java.util.UUID;

public interface CouponRepository {

    Optional<Coupon> findById(Long id);

    Coupon save(Coupon coupon);
}
