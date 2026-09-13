package com.camargo.coupon.coupon.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity
@Table(name = "coupon")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CouponEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    private String description;

    private Timestamp expirationDate;

    private String status;

    private boolean redemmed;

    private boolean published;


}
