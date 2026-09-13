package com.camargo.coupon.coupon.api;

import com.camargo.coupon.coupon.api.dto.CouponResponse;
import com.camargo.coupon.coupon.api.dto.CreateCouponRequest;
import com.camargo.coupon.coupon.application.command.CouponCommandService;
import com.camargo.coupon.coupon.application.command.CreateCouponCommand;
import com.camargo.coupon.coupon.application.command.DeleteCouponCommand;
import com.camargo.coupon.coupon.application.query.CouponQueryService;
import com.camargo.coupon.coupon.application.query.GetCouponQuery;
import com.camargo.coupon.coupon.domain.Coupon;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("/coupon")
@RestController
public class CouponController implements CouponResource {

    private final CouponCommandService commandService;
    private final CouponQueryService queryService;

    public CouponController(CouponCommandService commandService, CouponQueryService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<CouponResponse> getCoupon(@PathVariable("id") UUID id) {
        Coupon coupon = queryService.getById(new GetCouponQuery(id));
        return ResponseEntity.ok(CouponResponse.fromDomain(coupon));
    }

    @Override
    @PostMapping
    public ResponseEntity<CouponResponse> createCoupon(@RequestBody CreateCouponRequest request) {
        CreateCouponCommand command = new CreateCouponCommand(
                request.code(),
                request.description(),
                request.discountValue(),
                request.expirationDate(),
                request.published()
        );
        Coupon createdCoupon = commandService.create(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(CouponResponse.fromDomain(createdCoupon));
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCoupon(@PathVariable("id") UUID id) {
        commandService.delete(new DeleteCouponCommand(id));
        return ResponseEntity.noContent().build();
    }
}
