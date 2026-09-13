package com.camargo.coupon.shared.error;

import com.camargo.coupon.coupon.domain.exception.CouponAlreadyDeletedException;
import com.camargo.coupon.coupon.domain.exception.CouponNotFoundException;
import com.camargo.coupon.coupon.domain.exception.CouponValidationException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GlobalExceptionHandler Unit Tests")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        when(request.getRequestURI()).thenReturn("/coupon");
    }

    @Test
    @DisplayName("Should translate CouponNotFoundException to 404 NOT_FOUND")
    void shouldHandleCouponNotFoundException() {
        CouponNotFoundException ex = new CouponNotFoundException("Coupon not found with id: 123");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleNotFound(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(404);
        assertThat(response.getBody().error()).isEqualTo("Not Found");
        assertThat(response.getBody().message()).isEqualTo("Coupon not found with id: 123");
        assertThat(response.getBody().path()).isEqualTo("/coupon");
        assertThat(response.getBody().timestamp()).isNotNull();
    }

    @Test
    @DisplayName("Should translate CouponAlreadyDeletedException to 400 BAD_REQUEST")
    void shouldHandleCouponAlreadyDeletedException() {
        CouponAlreadyDeletedException ex = new CouponAlreadyDeletedException("Coupon is already deleted");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleAlreadyDeleted(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(400);
        assertThat(response.getBody().error()).isEqualTo("Bad Request");
        assertThat(response.getBody().message()).isEqualTo("Coupon is already deleted");
        assertThat(response.getBody().path()).isEqualTo("/coupon");
        assertThat(response.getBody().timestamp()).isNotNull();
    }

    @Test
    @DisplayName("Should translate CouponValidationException to 400 BAD_REQUEST")
    void shouldHandleCouponValidationException() {
        CouponValidationException ex = new CouponValidationException("Coupon code is mandatory");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidation(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(400);
        assertThat(response.getBody().error()).isEqualTo("Bad Request");
        assertThat(response.getBody().message()).isEqualTo("Coupon code is mandatory");
        assertThat(response.getBody().path()).isEqualTo("/coupon");
        assertThat(response.getBody().timestamp()).isNotNull();
    }
}
