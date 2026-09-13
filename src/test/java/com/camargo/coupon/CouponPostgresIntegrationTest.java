package com.camargo.coupon;

import com.camargo.coupon.coupon.domain.CouponStatus;
import com.camargo.coupon.coupon.infrastructure.persistence.CouponEntity;
import com.camargo.coupon.coupon.infrastructure.persistence.JpaCouponRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@DisplayName("PostgreSQL & Testcontainers Coupon Integration Tests")
class CouponPostgresIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Autowired
    private JpaCouponRepository jpaCouponRepository;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        jpaCouponRepository.deleteAll();
    }

    @Test
    @DisplayName("Complete lifecycle: Create with sanitized code, get by ID, soft delete, verify Postgres database state")
    void shouldExecuteFullCouponLifecycleAgainstPostgres() throws Exception {
        Instant futureExpiration = Instant.now().plus(Duration.ofDays(15));

        String createJson = String.format("""
                {
                    "code": "PRO-123",
                    "description": "Integration Test Coupon",
                    "discountValue": 10.5,
                    "expirationDate": "%s",
                    "published": true
                }
                """, futureExpiration);

        MvcResult createResult = mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.code").value("PRO123"))
                .andExpect(jsonPath("$.description").value("Integration Test Coupon"))
                .andExpect(jsonPath("$.discountValue").value(10.5))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.published").value(true))
                .andExpect(jsonPath("$.redeemed").value(false))
                .andReturn();

        JsonNode responseNode = objectMapper.readTree(createResult.getResponse().getContentAsString());
        UUID couponId = UUID.fromString(responseNode.get("id").asText());

        Optional<CouponEntity> savedEntityOpt = jpaCouponRepository.findById(couponId);
        assertThat(savedEntityOpt).isPresent();
        CouponEntity savedEntity = savedEntityOpt.get();
        assertThat(savedEntity.getCode()).isEqualTo("PRO123");
        assertThat(savedEntity.getStatus()).isEqualTo(CouponStatus.ACTIVE);
        assertThat(savedEntity.isPublished()).isTrue();
        assertThat(savedEntity.isRedeemed()).isFalse();
        assertThat(savedEntity.getDeletedAt()).isNull();

        mockMvc.perform(get("/coupon/{id}", couponId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(couponId.toString()))
                .andExpect(jsonPath("$.code").value("PRO123"));

        mockMvc.perform(delete("/coupon/{id}", couponId))
                .andExpect(status().isNoContent());

        Optional<CouponEntity> softDeletedEntityOpt = jpaCouponRepository.findById(couponId);
        assertThat(softDeletedEntityOpt).isPresent();
        CouponEntity softDeletedEntity = softDeletedEntityOpt.get();
        assertThat(softDeletedEntity.getDeletedAt()).isNotNull();
        assertThat(softDeletedEntity.getCode()).isEqualTo("PRO123");
        assertThat(softDeletedEntity.getDescription()).isEqualTo("Integration Test Coupon");
        assertThat(softDeletedEntity.getDiscountValue()).isEqualByComparingTo(new BigDecimal("10.5"));

        mockMvc.perform(get("/coupon/{id}", couponId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));

        mockMvc.perform(delete("/coupon/{id}", couponId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Coupon is already deleted"));
    }

    @Test
    @DisplayName("Should return 400 Bad Request and not persist when business rules are violated")
    void shouldRejectInvalidCouponAndNotPersist() throws Exception {
        Instant pastExpiration = Instant.now().minus(Duration.ofDays(1));
        String invalidJson = String.format("""
                {
                    "code": "PRO123",
                    "description": "Expired coupon",
                    "discountValue": 5.0,
                    "expirationDate": "%s",
                    "published": false
                }
                """, pastExpiration);

        mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Coupon expiration date cannot be in the past"));

        assertThat(jpaCouponRepository.count()).isZero();
    }
}
