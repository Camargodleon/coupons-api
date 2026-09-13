package com.camargo.coupon.coupon.application.query;

import com.camargo.coupon.coupon.domain.Coupon;
import com.camargo.coupon.coupon.domain.CouponRepository;
import com.camargo.coupon.coupon.domain.exception.CouponNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CouponQueryService {

    private final CouponRepository couponRepository;

    public CouponQueryService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    public Coupon getById(GetCouponQuery query) {
        return couponRepository.findById(query.id())
                .filter(coupon -> !coupon.isDeleted())
                .orElseThrow(() -> new CouponNotFoundException("Coupon not found with id: " + query.id()));
    }
}
