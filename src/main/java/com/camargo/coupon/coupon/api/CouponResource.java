package com.camargo.coupon.coupon.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Coupon")
public interface CouponResource {


    @Operation(summary = "Get coupon", description = "Get coupon by id", parameters = {@Parameter(name = "id", description = "Coupon id")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Coupon found"),
            @ApiResponse(responseCode = "404", description = "Coupon not found")
    })
    void getCoupon();

    @Operation(summary = "Create coupon", description = "Create a new coupon")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Coupon created"),
            @ApiResponse(responseCode = "400", description = "Invalid coupon data")
    })
    void createCoupon();


    @Operation(summary = "Delete coupon", description = "Delete an existing coupon", parameters = {@Parameter(name = "id", description = "Coupon id")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Coupon deleted"),
            @ApiResponse(responseCode = "404", description = "Coupon not found")
    })
    void deleteCoupon();
}
