package com.camargo.coupon.coupon.infrastructure.persistence;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaCouponRepository extends JpaRepository<CouponEntity, Long> {
}
