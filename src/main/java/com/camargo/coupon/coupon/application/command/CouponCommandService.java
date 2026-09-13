package com.camargo.coupon.coupon.application.command;

import com.camargo.coupon.coupon.domain.Coupon;
import com.camargo.coupon.coupon.domain.CouponRepository;
import com.camargo.coupon.coupon.domain.exception.CouponNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CouponCommandService {

    private final CouponRepository couponRepository;

    public CouponCommandService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    @Transactional
    public Coupon create(CreateCouponCommand command) {
        Coupon coupon = Coupon.create(
                command.code(),
                command.description(),
                command.discountValue(),
                command.expirationDate(),
                command.published()
        );
        return couponRepository.save(coupon);
    }

    @Transactional
    public void delete(DeleteCouponCommand command) {
        Coupon coupon = couponRepository.findById(command.id())
                .orElseThrow(() -> new CouponNotFoundException("Coupon not found with id: " + command.id()));

        coupon.delete();
        couponRepository.save(coupon);
    }
}
