package com.camargo.coupon.coupon.api;

import com.camargo.coupon.coupon.api.dto.CouponResponse;
import com.camargo.coupon.coupon.api.dto.CreateCouponRequest;
import com.camargo.coupon.shared.error.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@Tag(name = "Coupon", description = "Endpoints for managing discount coupons")
public interface CouponResource {

    @Operation(summary = "Get coupon", description = "Get coupon by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Coupon found",
                    content = @Content(schema = @Schema(implementation = CouponResponse.class))),
            @ApiResponse(responseCode = "404", description = "Coupon not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<CouponResponse> getCoupon(
            @Parameter(name = "id", description = "Coupon id", required = true) UUID id
    );

    @Operation(summary = "Create coupon", description = "Create a new coupon")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Coupon created",
                    content = @Content(schema = @Schema(implementation = CouponResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid coupon data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<CouponResponse> createCoupon(CreateCouponRequest request);

    @Operation(summary = "Delete coupon", description = "Delete an existing coupon (soft delete)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Coupon deleted"),
            @ApiResponse(responseCode = "400", description = "Coupon already deleted",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Coupon not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<Void> deleteCoupon(
            @Parameter(name = "id", description = "Coupon id", required = true) UUID id
    );
}
