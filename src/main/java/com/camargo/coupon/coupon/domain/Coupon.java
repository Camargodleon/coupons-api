package com.camargo.coupon.coupon.domain;

import java.sql.Timestamp;

public class Coupon {

    private String code;

    private String description;

    private Timestamp expirationDate;

    private CouponStatus status;

    private boolean redemmed;

    private boolean published;

}
