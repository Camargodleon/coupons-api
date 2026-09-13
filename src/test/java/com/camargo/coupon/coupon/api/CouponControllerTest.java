package com.camargo.coupon.coupon.api;

import com.camargo.coupon.coupon.api.dto.CouponResponse;
import com.camargo.coupon.coupon.api.dto.CreateCouponRequest;
import com.camargo.coupon.coupon.application.command.CouponCommandService;
import com.camargo.coupon.coupon.application.command.CreateCouponCommand;
import com.camargo.coupon.coupon.application.command.DeleteCouponCommand;
import com.camargo.coupon.coupon.application.query.CouponQueryService;
import com.camargo.coupon.coupon.application.query.GetCouponQuery;
import com.camargo.coupon.coupon.domain.Coupon;
import com.camargo.coupon.coupon.domain.CouponStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CouponController Unit Tests")
class CouponControllerTest {

    @Mock
    private CouponCommandService commandService;

    @Mock
    private CouponQueryService queryService;

    @InjectMocks
    private CouponController controller;

    @Test
    @DisplayName("POST /coupon should create coupon and return 201 Created with CouponResponse")
    void shouldCreateCoupon() {
        Instant expiration = Instant.now().plus(Duration.ofDays(5));
        CreateCouponRequest request = new CreateCouponRequest(
                "ABC-123",
                "Promotional coupon",
                new BigDecimal("10.0"),
                expiration,
                true
        );

        Coupon createdDomain = Coupon.create(
                "ABC-123",
                "Promotional coupon",
                new BigDecimal("10.0"),
                expiration,
                true
        );

        when(commandService.create(any(CreateCouponCommand.class))).thenReturn(createdDomain);

        ResponseEntity<CouponResponse> response = controller.createCoupon(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("ABC123");
        assertThat(response.getBody().description()).isEqualTo("Promotional coupon");
        assertThat(response.getBody().discountValue()).isEqualByComparingTo(new BigDecimal("10.0"));
        assertThat(response.getBody().status()).isEqualTo(CouponStatus.ACTIVE);
        assertThat(response.getBody().published()).isTrue();
        assertThat(response.getBody().redeemed()).isFalse();

        ArgumentCaptor<CreateCouponCommand> captor = ArgumentCaptor.forClass(CreateCouponCommand.class);
        verify(commandService, times(1)).create(captor.capture());
        assertThat(captor.getValue().code()).isEqualTo("ABC-123");
    }

    @Test
    @DisplayName("GET /coupon/{id} should return 200 OK with CouponResponse")
    void shouldGetCouponById() {
        UUID id = UUID.randomUUID();
        Instant expiration = Instant.now().plus(Duration.ofDays(5));
        Coupon coupon = Coupon.create(
                "ABC123",
                "Existing coupon",
                new BigDecimal("25.0"),
                expiration,
                false
        );

        when(queryService.getById(any(GetCouponQuery.class))).thenReturn(coupon);

        ResponseEntity<CouponResponse> response = controller.getCoupon(id);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("ABC123");
        assertThat(response.getBody().description()).isEqualTo("Existing coupon");

        ArgumentCaptor<GetCouponQuery> captor = ArgumentCaptor.forClass(GetCouponQuery.class);
        verify(queryService, times(1)).getById(captor.capture());
        assertThat(captor.getValue().id()).isEqualTo(id);
    }

    @Test
    @DisplayName("DELETE /coupon/{id} should call delete and return 204 No Content")
    void shouldDeleteCouponById() {
        UUID id = UUID.randomUUID();

        doNothing().when(commandService).delete(any(DeleteCouponCommand.class));

        ResponseEntity<Void> response = controller.deleteCoupon(id);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isNull();

        ArgumentCaptor<DeleteCouponCommand> captor = ArgumentCaptor.forClass(DeleteCouponCommand.class);
        verify(commandService, times(1)).delete(captor.capture());
        assertThat(captor.getValue().id()).isEqualTo(id);
    }
}
