package com.camargo.coupon.coupon.api;

import org.springframework.web.bind.annotation.*;


@RequestMapping("/coupon")
@RestController
public class CouponController implements CouponResource {


    @Override
    @GetMapping("/{id}")
    public void getCoupon() {

    }

    @Override
    @PostMapping
    public void createCoupon() {

    }

    @Override
    @DeleteMapping("/{id}")
    public void deleteCoupon() {

    }
}
