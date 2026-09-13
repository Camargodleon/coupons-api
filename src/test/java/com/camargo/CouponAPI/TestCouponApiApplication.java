package com.camargo.CouponAPI;

import com.camargo.coupon.CouponApiApplication;
import org.springframework.boot.SpringApplication;

public class TestCouponApiApplication {

	public static void main(String[] args) {
		SpringApplication.from(CouponApiApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
